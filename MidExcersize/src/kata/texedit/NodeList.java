package kata.texedit;
   
public class NodeList {
	NodeList nextlist;
	NodeList prevlist;
  
	Node head;
	Node tail;

	int nodes = 0;
	int printables = 0;

	public NodeList() {
	}

	public NodeList(Node nhead) {
		Node node = nhead;
		while (node != null) {
			addNodeToTail(node);
			node = node.next;
		}
	}

	public void insertNodeAfter(Node atnode, Node node) {
		if (head == null && tail == null) {
			return;
		}
		
		if (head == tail && atnode == head) {
			addNodeToTail(node);
			return;
		}

		if (head.next == tail) {
			if (atnode == head) {
				node.next = head;
				head.prev = node;
				head = node;
				nodes++;
				if (node.input != ASCII.LF)
					printables++;
			} else if (atnode == tail) {
				addNodeToTail(node);
			}
			return;
		}

		if (atnode == head) {
			atnode.next.prev = node;
			node.next = atnode.next;
			atnode.next = node;
			node.prev = atnode;

			nodes++;
			if (node.input != ASCII.LF)
				printables++;
		} else if (atnode == tail) {
			addNodeToTail(node);
		} else {
			atnode.next.prev = node;
			node.next = atnode.next;
			atnode.next = node;
			node.prev = atnode;
		}
	}

	public void addNodeToHead(Node node) {
		nodes++;
		if (node.input != ASCII.LF)
			printables++;

		if (head == null && tail == null) {
			head = node;
			tail = node;
			return;
		}

		if (head == tail) {
			node.next = head;
			head.prev = node;
			head = node;
			tail = head.next;
			tail.next = null;
			return;
		}

		node.next = head;
		head.prev = node;
		head = node;
	}

	public void addNodeToTail(Node node) {
		nodes++;
		if (node.input != ASCII.LF)
			printables++;

		if (head == null && tail == null) {
			head = node;
			tail = node;
			return;
		}

		if (head == tail) {
			tail = node;
			head.next = tail;
			tail.prev = head;
			tail.next = null;
			return;
		}

		tail.next = node;
		node.prev = tail;
		tail = node;
		tail.next = null;
	}

	public void removeAllNodes() {
		head = null;
		tail = null;

		nodes = printables = 0;

		return;
	}

	// THIS LIST MUST CONTAIN THE 'from node' ADN 'to node'
	public void removeRange(Node fnode, Node tnode, int fcursorcol, int tcursorcol) {
		if (fnode == null || tnode == null) {
			System.out.println("removing range is null");
			return;
		}

		// THIS LIST ONLY HAS ONE NODE
		// OR DELETING NODE IS ONE
		if (fnode == tnode) {
			removeNode(fnode);
			return;
		}

		int cnt = tcursorcol - fcursorcol;
		printables = printables - cnt;
		nodes = nodes - cnt;
		if (tnode.input == ASCII.LF)
			printables++;

		// THIS LIST ONLY HAS TWO NODE
		// OR REMOVE ALL NODES IN THIS LIST WHERE THIS LIST HAS MORE THAN THREE NODE
		if (fnode == head && tnode == tail) {
			head = null;
			tail = null;
			return;
		}

		// THIS LIST HAS MORE THAN THREE NODE
		if (fnode == head) {
			head = tnode.next;
			head.prev = null;
			return;
		}

		if (tnode == tail) {
			tail = fnode.prev;
			tail.next = null;
			return;
		}

		fnode.prev.next = tnode.next;
		tnode.next.prev = fnode.prev;
		fnode.prev = null;
		tnode.next = null;
	}

	// THIS LIST MUST CONTAIN THE 'dnode'
	public void removeNode(Node node) {
		if (node == null) {
			System.out.println("deleting node is null");
			return;
		}

		// IF THIS LIST HAS NO NODE
		if (head == null && tail == null) {
			System.out.println("this list is null. ");
			return;
		}

		nodes--;
		if (node.input != ASCII.LF)
			printables--;

		// IF THIS LIST HAS 1 NODE
		if (head == tail) {
			head = null;
			tail = null;
			node.next = null;
			node.prev = null;
			return;
		}

		// IF THIS LIST HAS 2 NODES
		// OR DELEETING NODE IS HEAD OR TAIL
		if (head.next == tail || node == head || node == tail) {
			if (head == node) {
				head = node.next;
				head.prev = null;
				return;
			}

			if (tail == node) {
				tail = node.prev;
				tail.next = null;
				return;
			}
		}

		// IF THIS LIST HAS MORE THAN 3 NODES
		// AND DELETING NODE IS NOT HEAD OR TAIL
		node.prev.next = node.next;
		node.next.prev = node.prev;
		node.next = null;
		node.prev = null;
	}

	public Node detachNodeAfter(Node node) {
		Node dnode = node.next;

		return dnode;
	}

	// this <- this.[fnode1 ... tnode1] + other.[fnode2 ... tnode2]
	// this.head <-- this.fnode1, this.tail <-- other.tnode2
	// remove all nodes in the list2
	public NodeList attachNodeFromList(Node fnode1, Node tnode1, NodeList list2, Node fnode2, Node tnode2,
			int fcursorcol1, int tcursorcol1, int fcursorcol2, int tcursorcol2) {

		// NODE COUNT
		int cnt1 = tcursorcol1 - fcursorcol1;
		int cnt2 = tcursorcol2 - fcursorcol2;
		printables = cnt1 + cnt2;
		nodes = cnt1 + cnt2;
		if (tnode2.input == ASCII.LF)
			printables--;

		// NODE LINKING
		tnode1.next = fnode2;
		fnode2.prev = tnode1;

		head = fnode1;
		tail = tnode2;

		head.prev = null;
		tail.next = null;

		list2.removeAllNodes();

		return this;
	}

	public void printList() {
		Node node = head;
		while (node != null) {
			System.out.print(node.input);
			node = node.next;
		}
		System.out.println();
	}
}

class Node {
	char input;
	int iinput;

	Node next;
	Node prev;

	public Node(char input) {
		this.input = input;
		this.iinput = (int) input;
	}
}