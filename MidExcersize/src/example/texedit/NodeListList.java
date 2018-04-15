package example.texedit;

public class NodeListList {
	NodeList headlist;
	NodeList taillist;

	int lists;

	public void insertNodeListToHeadList(NodeList list) {
		if (headlist == null && taillist == null) {
			headlist = taillist = list;
			return;
		}

		list.nextlist = headlist;
		headlist.prevlist = list;
		list.prevlist = null;
		headlist = list;

		return;
	}

	public void insertNodeListToTailList(NodeList list) {
		if (headlist == null && taillist == null) {
			headlist = taillist = list;
			return;
		}

		list.prevlist = taillist;
		taillist.nextlist = list;
		list.nextlist = null;
		taillist = list;
	}

	public void insertNodeListAfter(NodeList atlist, NodeList list) {
		if (headlist == taillist) {
			if (atlist == taillist) {
				insertNodeListToTailList(list);
				return;
			}
		}

		if (headlist.nextlist == taillist) {
			if (atlist == taillist) {
				insertNodeListToTailList(list);
				return;
			}
		}

		if (atlist == taillist) {
			insertNodeListToTailList(list);
			return;
		}

		if (atlist.nextlist != null) {
			atlist.nextlist.prevlist = list;
			list.nextlist = atlist.nextlist;
		}
		atlist.nextlist = list;
		list.prevlist = atlist;

	}

	public void addNodeListToHeadList(NodeList list) {
		if (headlist == null && taillist == null) {
			headlist = taillist = list;
			return;
		}

		if (headlist == taillist) {
			list.nextlist = headlist;
			headlist.prevlist = list;
			headlist = list;
			headlist.prevlist = null;
			taillist = headlist.nextlist;
			taillist.nextlist = null;
			return;
		}
		
		list.nextlist = headlist;
		headlist.prevlist = list;
		headlist = list;
		headlist.prevlist = null;

	}

	public void addNodeListToTailList(NodeList list) {
		if (headlist == null && taillist == null) {
			headlist = taillist = list;
			return;
		}

		if (headlist == taillist) {
			taillist = list;
			headlist.nextlist = taillist;
			taillist.prevlist = headlist;
			headlist.prevlist = null;
			taillist.nextlist = null;
			return;
		}

		taillist.nextlist = list;
		list.prevlist = taillist;
		taillist = list;
		taillist.nextlist = null;
	}

	public void removeRange(NodeList flist, NodeList tlist) {
		if (flist == null || tlist == null) {
			System.out.println("removing range is null");
			return;
		}

		// THIS LIST ONLY HAS ONE NODE
		// OR DELETING NODE IS ONE
		if (flist == tlist) {
			removeList(flist);
			return;
		}

		// THIS LIST ONLY HAS TWO NODE
		// OR REMOVE ALL NODES IN THIS LIST WHERE THIS LIST HAS MORE THAN THREE NODE
		if (flist == headlist && tlist == taillist) {
			headlist = null;
			taillist = null;
			return;
		}

		// THIS LIST HAS MORE THAN THREE NODE
		if (flist == headlist) {
			headlist = tlist.nextlist;
			headlist.prevlist = null;
			return;
		}

		if (tlist == taillist) {
			taillist = flist.prevlist;
			taillist.nextlist = null;
			return;
		}

		flist.prevlist.nextlist = tlist.nextlist;
		tlist.nextlist.prevlist = flist.prevlist;
		flist.prevlist = null;
		tlist.nextlist = null;

	}

	// THIS LIST MUST CONTAIN THE 'dnode'
	public void removeList(NodeList list) {
		if (list == null) {
			System.out.println("deleting list is null");
			return;
		}

		// IF THIS LIST HAS NO NODE
		if (headlist == null && taillist == null) {
			System.out.println("this list is null. ");
			return;
		}

		// IF THIS LIST HAS 1 NODE
		if (headlist == taillist) {
			headlist = null;
			taillist = null;
			list.nextlist = null;
			list.prevlist = null;
			return;
		}

		// IF THIS LIST HAS 2 NODES
		// OR DELEETING NODE IS HEAD OR TAIL
		if (headlist.nextlist == taillist || list == headlist || list == taillist) {
			if (headlist == list) {
				headlist = list.nextlist;
				headlist.prevlist = null;
				return;
			}

			if (taillist == list) {
				taillist = list.prevlist;
				taillist.nextlist = null;
				return;
			}
		}

		// IF THIS LIST HAS MORE THAN 3 NODES
		// AND DELETING NODE IS NOT HEAD OR TAIL
		list.prevlist.nextlist = list.nextlist;
		list.nextlist.prevlist = list.prevlist;
		list.nextlist = null;
		list.prevlist = null;
	}

	public void printList() {
		NodeList list = this.headlist;
		while (list != null) {
			list.printList();
			list = list.nextlist;
		}
	}

	public static void main(String args[]) {
		NodeListList nllist = new NodeListList();

		Node a = new Node('a');
		Node b = new Node('b');
		Node c = new Node('c');
		Node d = new Node('d');
		NodeList list1 = new NodeList();
		list1.addNodeToTail(a);
		list1.addNodeToTail(b);
		list1.addNodeToTail(c);
		list1.addNodeToTail(d);

		Node e = new Node('e');
		Node f = new Node('f');
		Node g = new Node('g');
		Node h = new Node('h');
		NodeList list2 = new NodeList();
		list2.addNodeToTail(e);
		list2.addNodeToTail(f);
		list2.addNodeToTail(g);
		list2.addNodeToTail(h);

		Node i = new Node('i');
		Node j = new Node('j');
		Node k = new Node('k');
		Node l = new Node('l');
		NodeList list3 = new NodeList();
		list3.addNodeToTail(i);
		list3.addNodeToTail(j);
		list3.addNodeToTail(k);
		list3.addNodeToTail(l);

		Node m = new Node('m');
		Node n = new Node('n');
		Node o = new Node('o');
		Node p = new Node('p');
		NodeList list4 = new NodeList();
		list4.addNodeToTail(m);
		list4.addNodeToTail(n);
		list4.addNodeToTail(o);
		list4.addNodeToTail(p);

		nllist.addNodeListToTailList(list1);
		nllist.addNodeListToTailList(list2);
		nllist.addNodeListToTailList(list3);
		nllist.addNodeListToTailList(list4);

		System.out.println("# BUILD : ");
		nllist.printList();

		System.out.println("# RECONSTRUCTION RANGE : ");
		// list1.attachNodeFromList(a, b, list3, k, l);
		nllist.removeRange(list2, list3);
		nllist.printList();

	}

}
