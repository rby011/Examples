package kata.texteditor.fail;

import java.util.Scanner;

public class TextEditor_TBD {
	static Scanner scan = null;

	public static void main(String args[]) {
		scan = new Scanner(System.in);

		int T = scan.nextInt(), score = 0;
		for (int t = 1; t <= T; t++) {
			UserCode.init();

			int input = ASCII.SPACE;

			while (true) {
				input = scan.nextInt();
				if (input == ASCII.NULL)
					break;
				UserCode.process((char) input);
			}

			char text[] = UserCode.save();

			System.out.println("\n#" + t + " result : ");
			printEdit(text);
		}

		scan.close();
	}

	public static void printEdit(char text[]) {
		for (int i = 0; (int) text[i] != ASCII.NULL; i++) {
			System.out.print(text[i]);
		}
		System.out.println();
	}
}

class NodeListList {
	int lists;// NUMBER OF LIST IN THIS LIST
	NodeList headlist, taillist;

	public void addNodeListToTailList(NodeList newnodelist) {
		lists++;
		if (headlist == null && taillist == null) {
			headlist = taillist = newnodelist;
			return;
		}

		if (headlist == taillist) {
			headlist.prevlist = null;
			headlist.nextlist = newnodelist;
			taillist = newnodelist;

			taillist.prevlist = headlist;
			taillist.nextlist = null;
			return;
		}

		taillist.nextlist = newnodelist;
		newnodelist.prevlist = taillist;
		taillist = newnodelist;
	}

	public void removeNodeList(NodeList nodelist) {
		if (this.headlist == null && this.taillist == null)
			return;

		lists--;

		if (this.headlist == this.taillist) {
			this.headlist = null;
			this.taillist = null;

			nodelist.nextlist = null;
			nodelist.prevlist = null;

			return;
		}

		if (this.headlist == nodelist) {
			this.headlist = nodelist.nextlist;
			if (this.headlist != null)
				this.headlist.prevlist = null;
			nodelist.nextlist = null;
			nodelist.prevlist = null;
			return;
		}

		if (this.taillist == nodelist) {
			this.taillist = nodelist.prevlist;
			if (this.taillist != null)
				this.taillist.nextlist = null;
			nodelist.nextlist = null;
			nodelist.prevlist = null;
			return;
		}

		if (nodelist.prevlist != null)
			nodelist.prevlist.nextlist = nodelist.nextlist;
		nodelist.nextlist.prevlist = nodelist.prevlist;
		nodelist.prevlist = null;
		nodelist.nextlist = null;
	}

	public NodeList getNodeListAtRaw(int rawCursor) {
		NodeList nlist = this.headlist;
		int raw = 0;
		while (raw < rawCursor)
			nlist = nlist.nextlist;
		return nlist;
	}

	public Node getNodeAtCursor(int raw, int col) {
		NodeList nlist = this.headlist;
		int r = 0, c = 0;
		while (nlist != null && r < raw) {
			r++;
			nlist = nlist.nextlist;
		}
		if (nlist == null)
			return null;

		Node node = nlist.head;
		while (node != null && c < col)
			node = node.next;
		return node;
	}

	public boolean replace(int sraw, int scol, int eraw, int ecol, char input) {

		return false;
	}

	public void convertToArray(char txtarr[]) {
		int idx = 0;
		NodeList nlist = this.headlist;
		while (nlist != null) {
			Node node = nlist.head;
			while (node != null) {
				if (node.input == ASCII.LF)
					txtarr[idx++] = '\n';
				else
					txtarr[idx++] = node.input;

				node = node.next;
			}
			nlist = nlist.nextlist;
		}
	}
}

class NodeList {
	// FOR NodeListList
	int nodes;// NUMBER OF NODES IN THIS LIST
	int chars;// NUBMER OF PRINTABLE CHARACTERS IN THIS LIST

	NodeList nextlist = null;
	NodeList prevlist = null;

	// FOR NodeList
	Node head, tail = null;

	public Node addNodeToTail(char input) {
		return addNodeToTail(new Node(input));
	}

	public void addNodeListToTail(NodeList nlist) {
		if (head == null & tail == null) {
			this.head = nlist.head;
			this.tail = nlist.tail;
			return;
		}

		this.tail.next = nlist.head;
		this.tail = nlist.tail;

		this.nodes = this.nodes + nlist.nodes;
		this.chars = this.chars + nlist.chars;
	}

	public Node addNodeToTail(Node newnode) {
		nodes++;

		if (newnode.input != ASCII.LF)
			chars++;

		if (head == null && tail == null) {
			head = tail = newnode;
			return newnode;
		}

		if (head == tail) {
			head.prev = null;
			head.next = newnode;
			tail = newnode;

			tail.prev = head;
			tail.next = null;
			return newnode;
		}

		tail.next = newnode;
		newnode.prev = tail;
		tail = newnode;
		return newnode;
	}

	public void removeNode(Node node) {
		if (node == null)
			return;

		if (this.head == null && this.tail == null)
			return;

		nodes--;
		if (node.input != ASCII.LF)
			chars--;

		if (this.head == this.tail) {
			this.head = null;
			this.tail = null;

			node.next = null;
			node.prev = null;

			return;
		}

		if (this.head == node) {
			this.head = node.next;
			if (this.head != null)
				this.head.prev = null;

			node.next = null;
			node.prev = null;

			return;
		}

		if (this.tail == node) {
			this.tail = node.prev;
			if (this.tail != null)
				this.tail.next = null;

			node.next = null;
			node.prev = null;

			return;
		}

		node.prev.next = node.next;
		node.next.prev = node.prev;
		node.next = null;
		node.prev = null;

	}

	public void removeNodeAtTail() {

		if (head == null && tail == null)
			return;

		nodes--;
		if (tail.input != ASCII.LF)
			chars--;

		if (tail == head) {
			tail = null;
			head = null;
			return;
		}

		tail.prev.next = null;
		tail.prev = null;
		tail = tail.prev;
		return;
	}

	public Node getNodeAtCursor(int colcursor) {
		if (colcursor == 0)
			return null;

		int col = 1;
		Node cnode = this.head;
		for (; col < colcursor; col++) {
			if (cnode == null || cnode.input == ASCII.LF)
				System.out.println("ERROR!!!");
			cnode = cnode.next;
		}
		return cnode;
	}

}

class Node {
	Node prev, next;
	char input;
	int iinput;

	Node(char input) {
		this.input = input;
		this.iinput = (int) input;
	}
}

class UserCode {

	// STATE
	static final int SHIFTED = 100;
	static final int UNSHIFTED = 200;
	static final int RANGED = 300;
	static int state = UNSHIFTED, prestate = -1;;

	static NodeListList nllist = null;
	static char txtarr[] = null;

	// CURSOR POSITION
	static NodeList cursorlist = null;
	static Node cursornode = null;
	static int cursorRaw = 0, cursorCol = 0;

	// RANGE SETTING
	static int sraw, scol, eraw, ecol;
	static NodeList scursorlist, ecursorlist;
	static Node scursornode, ecursornode;

	/**
	 * CALLED BY SYSTEM
	 */
	static void init() {
		state = UNSHIFTED;
		prestate = -1;

		cursorRaw = 0;
		cursorCol = 0;

		setrange(UNSHIFTED);

		nllist = new NodeListList();
		cursorlist = new NodeList();
		cursornode = null;// cursornode.next IS THE LOCATION TO ADD A NODE
		nllist.addNodeListToTailList(cursorlist);

		txtarr = new char[ASCII.LENMAX];
	}

	/**
	 * CALLED BY SYSTEM
	 */
	static char[] save() {
		nllist.convertToArray(txtarr);
		return txtarr;
	}

	/**
	 * <PRE>
	 * - CALLED BY SYSTEM 
	 * - STATE TRANSITION : UNSHIFTED --> SHIFTED --> RANGED
	 * </PRE>
	 */
	static void process(char input) {
		if (state == UNSHIFTED) {
			if (prestate != UNSHIFTED) {
				setrange(UNSHIFTED);
				prestate = UNSHIFTED;
			}
			if (isshiftin(input)) {
				prestate = state;
				state = SHIFTED;
			} else {
				if (isbackspace(input)) {
					processBACKSPACE();
				} else if (isarrow(input)) {
					processARROW(input);
				} else if (isprintable(input)) {
					processPRINTABLE(input);
				} else if (islinefeed(input)) {
					processENTER(input);
				} else if (isctrlv(input)) {
					// TBD
				}
			}
		} else if (state == SHIFTED) {
			if (prestate != SHIFTED) {
				setrange(SHIFTED);
				prestate = SHIFTED;
			}
			if (isshiftout(input)) {
				prestate = SHIFTED;
				state = RANGED;
			} else if (isarrow(input)) {// ACCEPTABLE INPUT
				processARROW(input);
			}
		} else if (state == RANGED) {
			if (prestate != RANGED) {
				setrange(RANGED);
				prestate = state;
			}
			if (isbackspace(input)) {
				eraseRANGE();
				prestate = RANGED;
				state = UNSHIFTED;
			} else if (isarrow(input)) {
				processARROW(input);
				prestate = RANGED;
				state = UNSHIFTED;
			} else if (isprintable(input)) {
				eraseRANGE();
				processPRINTABLE(input);
				prestate = RANGED;
				state = UNSHIFTED;
			} else if (islinefeed(input)) {

			} else if (isctrlv(input)) {
				// TBD
			} else if (isctrlx(input)) {
				prestate = RANGED;
				state = UNSHIFTED;
				// TBD
			} else if (isctrlc(input)) {
				// TBD
			}
		}
	}

	static void setrange(int state) {
		if (state == SHIFTED) {
			// INIT
			sraw = cursorRaw;
			scol = cursorCol;
			scursorlist = cursorlist;
			scursornode = cursornode;
		} else if (state == RANGED) {
			// SET
			eraw = cursorRaw;
			ecol = cursorCol;
			ecursorlist = cursorlist;
			ecursornode = cursornode;
		} else if (state == UNSHIFTED) {
			sraw = scol = eraw = ecol = -1;
			scursorlist = null;
			ecursorlist = null;
			scursornode = null;
			ecursornode = null;
		}
	}

	static void linkOUTRANGE() {
		if (sraw == eraw) {
			Node from = null, to = null;

			int delta = scol - ecol;
			if (delta < 0)
				delta = delta * -1;

			if (scol < ecol) {
				from = scursornode;
				to = ecursornode;
			} else if (scol > ecol) {
				from = ecursornode;
				to = scursornode;
			}

			if (from != null) {
				from.next = to.next;
				if (to.next != null)
					to.next.prev = from;
			} else {
				cursorlist.head = to.next;
				to.next.prev = null;
			}

			cursorlist.nodes = cursorlist.nodes - delta;
			if (to.input == ASCII.LF)
				cursorlist.chars = cursorlist.chars - (delta - 1);
			else
				cursorlist.chars = cursorlist.chars - delta;

		} else {
			Node fromnode = null, tonode = null;
			NodeList fromlist = null, tolist = null;

			if (sraw < eraw) {
				fromlist = scursorlist;
				tolist = ecursorlist;
				fromnode = scursornode;
				tonode = ecursornode;
			} else {
				fromlist = ecursorlist;
				tolist = scursorlist;
				fromnode = ecursornode;
				tonode = scursornode;
			}

		}

	}

	static void eraseRANGE() {
		// FROM scursornode@scursorlist TO ecursorndoe@ecursorlist
		if (sraw == eraw) {
			if (scol < ecol) {
				Node dnode = scursornode != null ? scursornode.next : cursorlist.head;
				while (dnode != ecursornode.next) {
					cursorlist.removeNode(dnode);
					cursorlist.chars--;
					cursorlist.nodes--;
					dnode = dnode.next;
				}
				cursornode = scursornode;
			} else if (scol > ecol) {
				if (ecursornode != null) {
					ecursornode.next = scursornode.next;
					scursornode.prev = ecursornode;
				} else {
					cursorlist.head = scursornode.next;
				}
				cursornode = ecursornode;
				cursorlist.chars = cursorlist.chars - (scol - ecol);
				cursorlist.nodes = cursorlist.nodes - (scol - ecol);
			}
		} else {
			if (sraw < eraw) {
				// REMOVE NODES
				if (scursornode != null)
					scursornode.next = ecursornode.next;
				if (ecursornode.next != null)
					ecursornode.next.prev = scursornode;

				// REMOVE NODELISTS AT THE MIDDLE
				if (eraw - sraw > 1) {
					NodeList dlist = ecursorlist.prevlist;
					while (dlist != null && dlist != scursorlist) {
						nllist.removeNodeList(dlist);
						dlist = dlist.prevlist;
					}
				}
				// REMOVE THE FIRST LIST WHEN THE CURSOR IS AT THE FIRST OF FIRST
				if (scursorlist.prevlist == null && scursornode == null) {
					nllist.removeNodeList(scursorlist);
				}
				// REMOVE THE LAST LIST WHNE THE CURST IS AT THE LAST OF LAST
				if (ecursorlist.nextlist == null && ecursornode.next == null) {
					nllist.removeNodeList(ecursorlist);
				}
			} else if (sraw > eraw) {
				// REMOVE NODES
				if (ecursornode != null)
					ecursornode.next = scursornode.next;
				if (scursornode.next != null)
					scursornode.next.prev = ecursornode;

				ecursorlist.nextlist = scursorlist.nextlist;
				if (scursorlist.nextlist != null)
					scursorlist.nextlist.prevlist = ecursorlist;
				
				/*
				if (sraw - eraw > 1) {
					NodeList dlist = scursorlist.prevlist;
					while (dlist != null && dlist != ecursorlist) {
						nllist.removeNodeList(dlist);
						dlist = dlist.prevlist;
					}
				}
				// REMOVE THE FIRST LIST WHEN THE CURSOR IS AT THE FIRST OF FIRST
				if (ecursorlist.prevlist == null && ecursornode == null) {
					nllist.removeNodeList(ecursorlist);
				}
				// REMOVE THE LAST LIST WHNE THE CURST IS AT THE LAST OF LAST
				if (scursorlist.nextlist == null && scursornode.next == null) {
					nllist.removeNodeList(scursorlist);
				}
				*/
			}
		}
	}

	static void processPRINTABLE(char input) {
		// ADD NODE WITH INPUT
		cursornode = cursorlist.addNodeToTail(input);

		// CURSOR UPDATE
		cursorCol = cursorCol + 1;
	}

	static void processENTER(char input) {
		// ADD NODE WITH LINE_FEED
		cursorlist.addNodeToTail(input);

		// INITIALIZE FOR THE NEX LINE
		NodeList emptylist = new NodeList();
		nllist.addNodeListToTailList(emptylist);
		cursorlist = emptylist;
		cursornode = null;

		// CURSOR UPDATE
		cursorRaw = cursorRaw + 1;
		cursorCol = 0;
	}

	static void processBACKSPACE() {
		if (cursornode == null && cursorlist == null)
			return;

		if (cursornode == null && cursorlist.prevlist != null) {
			// NODELISTLIST
			NodeList plist = cursorlist.prevlist;
			nllist.removeNodeList(cursorlist);

			// NODELIST
			int cnum = plist.chars;
			plist.removeNode(plist.tail);// REMOVE LINE_FEED
			cursornode = plist.tail;
			cursorlist = plist;

			// CURSOR UPDATE
			cursorRaw = cursorRaw - 1;
			cursorCol = cnum;
			return;
		}

		if (cursornode != null) {
			Node ncnode = cursornode.prev;
			cursorlist.removeNode(cursornode);
			cursorCol = cursorCol - 1;
			cursornode = ncnode;
			return;
		}

	}

	static void processARROW(char input) {
		if (input == ASCII.ARROW_RIGHT) {
			if (cursorlist != null) {
				if (cursornode != null) {
					if (cursornode.next != null && cursornode.next.input == ASCII.LF) {
						if (cursorlist.nextlist != null) {
							cursorlist = cursorlist.nextlist;
							cursornode = null;
							cursorCol = 0;
							cursorRaw = cursorRaw + 1;
						} else {
							// DO NOTHING
						}
					} else if (cursornode.next != null && cursornode.next.input != ASCII.LF) {
						cursornode = cursornode.next;
						cursorCol = cursorCol + 1;
					}
				} else {// IF cursornode IS null
					cursornode = cursorlist.head;
					cursorCol = cursorCol + 1;
				}
			} else {
				// ABNORMAL CASE
			}
			return;
		}

		if (input == ASCII.ARROW_LEFT) {
			if (cursorlist != null) {
				if (cursornode == null) {
					if (cursorlist.prevlist != null) {
						cursorlist = cursorlist.prevlist;
						cursornode = cursorlist.tail.prev;
						cursorCol = cursorlist.chars;
						cursorRaw = cursorRaw - 1;
					} else {
						// DO NOTHING
					}
				} else {
					cursornode = cursornode.prev;
					cursorCol = cursorCol - 1;
				}
			} else {
				// ABNORMAL CASE
			}

			return;
		}

		if (input == ASCII.ARROW_UP) {
			if (cursorlist != null) {
				if (cursorlist.prevlist == null) {
					// DO NOTHING
				} else {
					if (cursorCol >= cursorlist.prevlist.chars) {
						cursorlist = cursorlist.prevlist;
						cursornode = cursorlist.tail.prev;
						cursorRaw = cursorRaw - 1;
						cursorCol = cursorlist.chars;
					} else {
						cursorlist = cursorlist.prevlist;
						cursornode = cursorlist.getNodeAtCursor(cursorCol);
						cursorRaw = cursorRaw - 1;
					}
				}
			} else {
				// ABNORMAL CASE??
			}
			return;
		}

		if (input == ASCII.ARROW_DOWN) {
			if (cursorlist != null) {
				if (cursorlist.nextlist == null) {
					// DO NOTHING
				} else {
					if (cursorCol >= cursorlist.nextlist.chars) {
						cursorlist = cursorlist.nextlist;
						cursornode = cursorlist.tail.prev;
						cursorRaw = cursorRaw + 1;
						cursorCol = cursorlist.chars;
					} else {
						cursorlist = cursorlist.nextlist;
						cursornode = cursorlist.getNodeAtCursor(cursorCol);
						cursorRaw = cursorRaw + 1;
					}
				}
			} else {
				// ABNORMAL CASE??
			}
			return;
		}
	}

	/**
	 * INPUT TYPE IDENTIFICATION
	 */
	static boolean isprintable(char input) {
		return input >= ASCII.PRINT_CHAR_START && input <= ASCII.PRINT_CHAR_END;
	}

	static boolean isarrow(char input) {
		return input == ASCII.ARROW_DOWN || input == ASCII.ARROW_LEFT || input == ASCII.ARROW_RIGHT
				|| input == ASCII.ARROW_UP;
	}

	static boolean isbackspace(char input) {
		return input == ASCII.BS;
	}

	static boolean islinefeed(char input) {
		return input == ASCII.LF;
	}

	static boolean isshiftin(char input) {
		return input == ASCII.SHFTIN;
	}

	static boolean isshiftout(char input) {
		return input == ASCII.SHIFTOUT;
	}

	static boolean isctrlc(char input) {
		return input == ASCII.CRTL_C;
	}

	static boolean isctrlx(char input) {
		return input == ASCII.CRTL_X;
	}

	static boolean isctrlv(char input) {
		return input == ASCII.CRTL_V;
	}

	static boolean isctrlz(char input) {
		return input == ASCII.CRTL_Z;
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
