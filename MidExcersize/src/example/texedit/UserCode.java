package example.texedit;

public class UserCode {
	static final int INITIAL = 100;
	static final int SHIFTED = 200;
	static final int RANGED = 300;
	static int state = 0, prestate;

	static NodeListList nllist = null;

	// CURRENT CURSOR
	static int cursorraw = 0, cursorcol = 0;
	static NodeList cursorlist = null;
	static Node cursornode = null;

	// FOR RANGE : ARROW
	static int scursorraw = 0, scursorcol = 0, ecursorraw = 0, ecursorcol = 0;
	static NodeList scursorlist = null, ecursorlist = null;
	static Node scursornode = null, ecursornode = null;

	// PRINTING TEXT
	static char txt[] = null;

	// CLIPBOARD
	static NodeListList cliplist = null;

	public static void init() {
		// NEW DATA STRUCTURE
		nllist = new NodeListList();

		// STATE INIT
		state = INITIAL;

		// CURSOR INIT
		cursorraw = cursorcol = 0;
		cursorlist = new NodeList();
		nllist.addNodeListToTailList(cursorlist);
		cursornode = null;

		// RANGE INIT
		setrange(INITIAL, INITIAL);

		// TXT INIT
		txt = new char[ASCII.LENMAX];

		// CLIPBOARD INIT
		cliplist = null;
	}

	public static void setrange(int state, int prestate) {
		if (state == INITIAL) {
			scursorraw = scursorcol = ecursorraw = ecursorcol = 0;
			scursorlist = ecursorlist = null;
			scursornode = ecursornode = null;
		} else if (state == SHIFTED && prestate != SHIFTED) {// AFTER SHIFT-IN
			scursorraw = cursorraw;
			scursorcol = cursorcol;
			scursorlist = cursorlist;
			scursornode = cursornode;
		} else if (state == RANGED) {// AFTER SHIFT-OUT
			ecursorraw = cursorraw;
			ecursorcol = cursorcol;
			ecursorlist = cursorlist;
			ecursornode = cursornode;
		}
	}

	public static void process(char input) {

		// STATE TRANSITION FLOW
		if (state == INITIAL) {
			setrange(state, prestate);
			if (input >= ASCII.PRINT_CHAR_START && input <= ASCII.PRINT_CHAR_END) {
				processPRINTABLE(input);
			} else if (input >= ASCII.ARROW_UP && input <= ASCII.ARROW_LEFT) {
				processARROW(input);
			} else if (input == ASCII.BS) {
				processBACKSPACE();
			} else if (input == ASCII.LF) {
				processLINEFEED();
			} else if (input == ASCII.SHFTIN) {
				state = SHIFTED;
			} else if (input == ASCII.CRTL_V) {
				pasteRANGE(cliplist);
			}
			prestate = INITIAL;
		} else if (state == SHIFTED) {
			if (input >= ASCII.ARROW_UP && input <= ASCII.ARROW_LEFT) {
				setrange(state, prestate);
				processARROW(input);
			} else if (input == ASCII.SHIFTOUT) {
				state = RANGED;
			}
			prestate = SHIFTED;
		} else if (state == RANGED) {
			setrange(state, prestate);
			
			if (input >= ASCII.PRINT_CHAR_START && input <= ASCII.PRINT_CHAR_END) {
				cutRANGE();
				processPRINTABLE(input);
			} else if (input >= ASCII.ARROW_UP && input <= ASCII.ARROW_LEFT) {
				processARROW(input);
			} else if (input == ASCII.BS) {
				cutRANGE();
			} else if (input == ASCII.LF) {
				cutRANGE();
				processLINEFEED();
			} else if (input == ASCII.CRTL_X) {
				cliplist = clipRANGE();
				cutRANGE();
			} else if (input == ASCII.CRTL_C) {
				cliplist = clipRANGE();
			} else if (input == ASCII.CRTL_V) {
				cutRANGE();
				pasteRANGE(cliplist);
			}

			if (input != ASCII.CRTL_C) {
				state = INITIAL;
				prestate = RANGED;
			}
		}
	}

	public static void pasteRANGE(NodeListList cliplist) {
		if (cliplist == null || cliplist.headlist == null || cliplist.headlist == null)
			return;

		if (cursornode == null) {
			NodeList nlist = cliplist.headlist;

			int lcnt = 0;
			if (cursorlist.prevlist != null) {
				while (nlist != null && nlist.nextlist != null) {
					NodeList tlist = nlist.nextlist;
					nllist.insertNodeListAfter(cursorlist.prevlist, nlist);
					nlist = tlist;
					lcnt++;
				}
			} else {
				while (nlist != null && nlist.nextlist != null) {
					NodeList tlist = nlist.nextlist;
					nllist.addNodeListToHeadList(nlist);
					nlist = tlist;
					lcnt++;
				}
			}

			// LAST LINE OF CLIP BOARD
			if (nlist.tail != null)
				nlist.tail.next = cursorlist.head;
			if (cursorlist.head != null)
				cursorlist.head.prev = nlist.tail;
			cursorlist.head = nlist.head;
			cursorlist.tail = cursorlist.tail;

			// CURSOR SETTING
			cursorlist = cliplist.taillist;
			cursornode = cliplist.taillist.tail;

			cursorraw = cursorraw + lcnt;
			cursorcol = cliplist.taillist.printables;

		} else {

			NodeList cplist = cliplist.headlist;
			NodeList cslist = cursorlist;
			Node csnnode = cursornode.next;

			// FIRST LINE OF CLIPBOARD
			cursornode.next = cplist.head;
			cplist.head.prev = cursornode;
			cursorlist.tail = cplist.tail;

			cursorlist.nodes = cursorcol + cliplist.headlist.nodes;
			cursorlist.printables = cursorcol + cliplist.headlist.printables;

			// MIDDLE LINES OF CLIPBOARD
			cplist = cplist.nextlist;
			if (cslist.nextlist != null)
				cslist = cslist.nextlist;
			while (cplist != null && cplist != cliplist.taillist) {
				nllist.insertNodeListAfter(cslist, cplist);
				cslist = cslist.nextlist;
				cplist = cplist.nextlist;
			}

			// LAST LINE OF CLIPBOARD
			if (cplist != null) {
				cplist.tail.next = csnnode;
				if (csnnode != null)
					csnnode.prev = cplist.tail;

				cursorlist = cplist;
				cursornode = cplist.tail;

				cursorlist.tail = cslist.tail;
				cursorlist.head = cplist.head;

				cursorlist.nodes = cplist.nodes + cslist.nodes;
				cursorlist.printables = cursorlist.printables;
				if (cursorlist.tail.input == ASCII.LF)
					cursorlist.printables--;
				
				nllist.addNodeListToTailList(cursorlist);
			}
		}
	}

	public static NodeListList clipRANGE() {
		NodeListList cnllist = new NodeListList();

		NodeList from_list = null, to_list = null;
		Node from_node = null, to_node = null;

		if (scursorraw < ecursorraw) {
			from_list = scursorlist;
			to_list = ecursorlist;
			from_node = scursornode;
			to_node = ecursornode;
		} else if (scursorraw > ecursorraw) {
			from_list = ecursorlist;
			to_list = scursorlist;
			from_node = ecursornode;
			to_node = scursornode;
		} else if (scursorraw == ecursorraw) {
			from_list = to_list = scursorlist;
			if (scursorcol < ecursorcol) {
				from_node = scursornode;
				to_node = ecursornode;
			} else if (scursorcol > ecursorcol) {
				from_node = ecursornode;
				to_node = scursornode;
			}
		}

		if (from_node == null && from_list.head != null)
			from_node = from_list.head;
		else
			from_node = from_node.next;

		if (scursorraw != ecursorraw) {
			// FIRST LINE COPY
			NodeList nlist = new NodeList();
			while (from_node != null) {
				Node nnode = new Node(from_node.input);
				nlist.addNodeToTail(nnode);
				from_node = from_node.next;
			}
			cnllist.addNodeListToTailList(nlist);

			// MIDDLE LINE COPY
			from_list = from_list.nextlist;
			while (from_list != to_list) {
				nlist = new NodeList();
				Node node = from_list.head;
				while (node != null) {
					Node nnode = new Node(node.input);
					nlist.addNodeToTail(nnode);
					node = node.next;
				}
				cnllist.addNodeListToTailList(nlist);
				from_list = from_list.nextlist;
			}

			// LAST LINE COPY
			nlist = new NodeList();
			if (to_node != null) {
				nlist = new NodeList();
				Node node = to_list.head;

				while (node != to_node) {
					Node nnode = new Node(node.input);
					nlist.addNodeToTail(nnode);
					node = node.next;
				}
				Node nnode = new Node(node.input);
				nlist.addNodeToTail(nnode);

			}
			cnllist.addNodeListToTailList(nlist);
		} else {
			NodeList nllist = new NodeList();

			Node node = from_node;

			while (node != to_node) {
				Node nnode = new Node(node.input);
				nllist.addNodeToTail(nnode);
				node = node.next;
			}
			Node nnode = new Node(node.input);
			nllist.addNodeToTail(nnode);

			cnllist.addNodeListToTailList(nllist);
		}
		return cnllist;
	}

	public static void cutRANGE() {
		Node fnode = null, tnode = null;
		int fcol = 0, tcol = 0;
		if (scursorraw == ecursorraw) {
			if (scursorcol < ecursorcol) {
				fnode = scursornode;
				tnode = ecursornode;
				fcol = scursorcol;
				tcol = ecursorcol;
			} else if (scursorcol > ecursorcol) {
				fnode = ecursornode;
				tnode = scursornode;
				fcol = ecursorcol;
				tcol = scursorcol;
			}

			if (fnode == null && cursorlist.head != null)
				fnode = cursorlist.head;
			else
				fnode = fnode.next;

			cursornode = fnode.prev;
			cursorlist.removeRange(fnode, tnode, fcol - 1, tcol);

		} else {
			if (scursorraw < ecursorraw) {
				// CURSOR UPDATE
				cursorlist = scursorlist;
				cursornode = scursornode;
				cursorraw = scursorraw;
				cursorcol = scursorcol;

				// LINKING BETWEEN OUTSIDES OF RANGE
				if (scursornode == null) {
					if (ecursornode != null)
						scursorlist.head = ecursornode.next;
					else
						scursorlist.head = ecursorlist.head;
				} else {
					if (ecursornode != null)
						scursornode.next = ecursornode.next;
					else
						scursornode.next = ecursorlist.head;
				}
				if (ecursornode != null) {
					if (ecursornode.next != null)
						ecursornode.next.prev = scursornode;
				} else {
					ecursorlist.head.next.prev = scursornode;
				}
				scursorlist.tail = ecursorlist.tail;

				scursorlist.nodes = scursorcol + (ecursorlist.nodes - scursorcol);
				scursorlist.printables = scursorlist.nodes;
				if (scursorlist.tail.input == ASCII.LF)
					scursorlist.printables--;

				// ERASE RANGE BY RAW LIST UNIT
				NodeList dlist = scursorlist.nextlist;
				while (dlist != ecursorlist && dlist != null) {
					nllist.removeList(dlist);
					dlist = dlist.nextlist;
				}

				nllist.removeList(ecursorlist);

			} else {
				// CURSOR UPDATE
				cursorlist = ecursorlist;
				cursornode = ecursornode;
				cursorraw = ecursorraw;
				cursorcol = ecursorcol;

				// LINKING BETWEEN OUTSIDES OF RANGE
				if (ecursornode == null) {
					if (scursornode != null)
						ecursorlist.head = scursornode.next;
					else
						ecursorlist.head = ecursorlist.head;
				} else {
					if (scursornode != null)
						ecursornode.next = scursornode.next;
					else
						ecursornode.next = scursorlist.head;
				}
				if (scursornode != null) {
					if (scursornode.next != null)
						scursornode.next.prev = ecursornode;
				} else {
					scursorlist.head.next.prev = ecursornode;
				}
				ecursorlist.tail = scursorlist.tail;

				ecursorlist.nodes = ecursorcol + (scursorlist.nodes - scursorcol);
				ecursorlist.printables = ecursorlist.nodes;
				if (ecursorlist.tail.input == ASCII.LF)
					ecursorlist.printables--;

				// ERASE RANGE BY RAW LIST UNIT
				nllist.removeList(scursorlist);
				NodeList dlist = ecursorlist.nextlist;
				while (dlist != scursorlist && dlist != null) {
					nllist.removeList(dlist);
					dlist = dlist.nextlist;
				}

			}
		}
	}

	public static void processARROW(char input) {
		if (ASCII.ARROW_DOWN == input) {
			movedown();
		} else if (ASCII.ARROW_UP == input) {
			moveup();
		} else if (ASCII.ARROW_LEFT == input) {
			moveleft();
		} else if (ASCII.ARROW_RIGHT == input) {
			moveright();
		}
	}

	private static void movedown() {
		if (cursorlist.nextlist != null) {
			if (cursornode == null) {
				if (cursorlist.nextlist != null) {
					cursorlist = cursorlist.nextlist;
					cursorraw = cursorraw + 1;
				}
			} else {
				if (cursorlist.nextlist.printables < cursorcol)
					cursorcol = cursorlist.nextlist.printables;
				cursorraw = cursorraw + 1;
				cursorlist = cursorlist.nextlist;
			}
		}
	}

	private static void moveup() {
		if (cursorlist.prevlist != null) {
			if (cursornode == null) {
				if (cursorlist.prevlist != null) {
					cursorlist = cursorlist.prevlist;
					cursorraw = cursorraw - 1;
				}
			} else {
				if (cursorlist.prevlist.printables < cursorcol)
					cursorcol = cursorlist.prevlist.printables;
				cursorraw = cursorraw - 1;
				cursorlist = cursorlist.prevlist;

				Node cnode = cursorlist.head;
				for (int c = 0; c < cursorcol - 1; c++)
					cnode = cnode.next;
				cursornode = cnode;
			}
		}
	}

	private static void moveleft() {
		if (cursornode == null && cursorlist.prevlist != null) {
			if (cursorlist.prevlist.tail != null) {
				cursornode = cursorlist.prevlist.tail.prev;// 'tail' CANNOT BE NULL
				cursorlist = cursorlist.prevlist;

				cursorcol = cursorlist.printables;
				cursorraw = cursorraw - 1;
			}
		} else if (cursornode != null) {
			cursornode = cursornode.prev;
			cursorcol = cursorcol - 1;
		}
	}

	private static void moveright() {
		if (cursornode == null && cursorlist.head != null) {
			if (cursorlist.head.next == null) {
				if (cursorlist.nextlist != null) {
					cursorlist = cursorlist.nextlist;
					cursornode = null;

					cursorcol = 0;
					cursorraw = cursorraw + 1;
				}
			} else {
				cursornode = cursorlist.head;
				cursorcol = cursorcol + 1;
			}
		} else if (cursornode != null && cursornode.next != null && cursornode.next.next == null) {
			if (cursorlist.nextlist != null) {
				cursorlist = cursorlist.nextlist;
				cursornode = null;

				cursorcol = 0;
				cursorraw = cursorraw + 1;
			}
		} else if (cursornode != null && cursornode.next != null & cursornode.next.next != null) {
			cursornode = cursornode.next;
			cursorcol = cursorcol + 1;
		}
	}

	public static void processPRINTABLE(char input) {
		cursorcol = cursorcol + 1;
		Node node = new Node(input);
		if (cursornode == null) {
			cursorlist.addNodeToTail(node);
		} else {
			cursorlist.insertNodeAfter(cursornode, node);
		}
		cursornode = node;

	}

	public static void processLINEFEED() {

		Node nnode = null;
		if (cursornode != null) {

			nnode = cursornode.next;
			if (nnode != null)
				nnode.prev = null;

			if (cursornode.next != null)
				cursornode.next.prev = null;
			cursornode.next = null;
			cursorlist.tail = cursornode;

			cursorlist.nodes = cursorcol;
			cursorlist.printables = cursorcol;
			cursorlist.addNodeToTail(new Node('\n'));

			NodeList nnlist = new NodeList(nnode);
			nllist.insertNodeListAfter(cursorlist, nnlist);

			cursorlist = nnlist;
			cursornode = null;

		} else {
			NodeList nnlist = new NodeList(new Node('\n'));
			if (nllist.headlist == cursorlist) {
				nllist.insertNodeListToHeadList(nnlist);
			} else {
				nllist.insertNodeListAfter(cursorlist.prevlist, nnlist);
			}
			cursorlist = cursorlist.nextlist;
		}
		cursorcol = 0;
		cursorraw = cursorraw + 1;
	}

	public static void processBACKSPACE() {
		if (cursornode == null && cursorlist.prevlist == null) {
			return;
		}

		if (cursornode == null && cursorlist.prevlist != null) {
			NodeList ncursorlist = cursorlist.prevlist;
			ncursorlist.removeNode(ncursorlist.tail);

			nllist.removeList(cursorlist);

			cursorlist = ncursorlist;

			cursorraw = cursorraw - 1;
			cursorcol = cursorlist.printables;
			return;
		}

		if (cursornode != null) {
			Node ncursornode = cursornode.prev;
			cursorlist.removeNode(cursornode);
			cursornode = ncursornode;

			cursorcol = cursorcol - 1;
		}
	}

	public static char[] save() {
		int idx = 0;
		NodeList list = nllist.headlist;
		while (list != null) {
			Node node = list.head;
			while (node != null) {
				txt[idx++] = node.input;
				node = node.next;
			}
			list = list.nextlist;
		}

		txt[idx++] = 0;
		return txt;
	}
}

class ASCII {
	// PRINTING CHARACTER RANGE
	static final int PRINT_CHAR_START = 32;
	static final int PRINT_CHAR_END = 126;

	// CONTROL CHARACTERS
	static final int LENMAX = 1000000 + 1;// LAST + 1 for NULL CHRACTER
	static final int NULL = 0;
	static final int SPACE = 32;
	static final int DEL = 127;
	static final int LF = 10;// LINE FEED
	static final int BS = 8;// BACKSPACE
	static final int SHIFTOUT = 14;
	static final int SHFTIN = 15;
	static final int ARROW_UP = 17;
	static final int ARROW_DOWN = 18;
	static final int ARROW_RIGHT = 19;
	static final int ARROW_LEFT = 20;
	static final int CRTL_C = 3;
	static final int CRTL_F = 6;
	static final int CRTL_V = 22;
	static final int CRTL_X = 24;
	static final int CRTL_Z = 26;
	static final int ENQ = 5;
	static final int ESC = 27;
}