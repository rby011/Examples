package certi.blockchain.kata;

public class UserSolution {

	static Hashtable ltable[] = null;
	static Node roots[] = null;
	static int ledgerCnt = 0;

	static Hashtable gltable = null;
	static Node groot = null;

	static int TC = 0;

	public static void restoreLedger(int L, char[][] ledgerData) {
		TC++;
		ltable = new Hashtable[L];
		roots = new Node[L];
		ledgerCnt = L;

		for (int l = 0; l < L; l++) {
			ltable[l] = new Hashtable(17071);
			Node tNodes[] = new Node[17071];
			int size = ledgerData[l][0] * 16777216 + ledgerData[l][1] * 65536 + ledgerData[l][2] * 256
					+ ledgerData[l][3];
			int index = 4, nCnt = 0;
			while (index < size + 4) {
				int pos = index;
				int pHash = ledgerData[l][index++] * 16777216 + ledgerData[l][index++] * 65536
						+ ledgerData[l][index++] * 256 + ledgerData[l][index++];
				int random = ledgerData[l][index++] * 256 + ledgerData[l][index++];
				int itemN = ledgerData[l][index++];

				// ITEM IN A SMALL DEPT
				int items[] = new int[20];
				for (int i = 0; i < itemN; i++) {
					int itemID = ledgerData[l][index++];
					int amount = ledgerData[l][index++] * 256 + ledgerData[l][index++];
					items[itemID] += amount;
				}

				int len = index - pos;
				int hash = Solution.calcHash(ledgerData[l], pos, len);

				// FOR EACH SMALL DEPT
				Node node = new Node(hash, pHash, items);
				ltable[l].put(hash, node);
				tNodes[nCnt++] = node;
			}

			// BUILD HIERACHICAL-RELATIONSHIP FOR EACH LEDGER
			roots[l] = new Node(0, 0, null);
			ltable[l].put(0, roots[l]);
			for (int n = 0; n < nCnt; n++) {
				int pHash = tNodes[n].phash;
				Node pNode = ltable[l].get(pHash);
				if (pNode != null) {
					pNode.addChilds(tNodes[n]);
				} else {
					// ERROR!
					// System.out.println("ERROR");
				}
			}
		}

		// BUILD GLOBAL TREE
		gltable = new Hashtable(17071);
		root = new Node(0, 0, null);
		gltable.put(0, root);
		traverseForBuildTree(root);

	}

	static Node root = null;

	public static void traverseForBuildTree(Node node) {
		if (node == null)
			return;

		int valid = 0;
		Node anode = null;
		for (int l = 0; l < ledgerCnt; l++) {
			anode = ltable[l].get(node.hash);
			if (anode != null)
				valid++;
		}
		if (valid <= ledgerCnt / 2)
			return;
		
		Node pnode = gltable.get(node.phash);
		if (pnode != null) {
			Node nnode = new Node(node.hash, node.phash, node.items);
			pnode.addChilds(nnode);
			gltable.put(nnode.hash, nnode);
		}
		
		for (int l = 0; l < ledgerCnt; l++) {
			anode = ltable[l].get(node.hash);
			if (null == gltable.get(node.hash)) {
				if (anode == null)
					continue;
				gltable.put(anode.hash, new Node(anode.hash, anode.phash, anode.items));
			}
			if (anode != null)
				for (int c = 0; c < anode.childN; c++)
					traverseForBuildTree(anode.childs[c]);
		}
	}

	public static int calcAmount(int hash, int itemid) {
		// # TIME OUT
		// Hashtable atable = new Hashtable(17071);
		// traverse_slow(atable, hash, itemid);
		// return atable.amount;
		return traverse(hash, itemid);
	}

	public static int traverse(int hash, int itemid) {
		int amount = 0;
		Node node = gltable.get(hash);
		if (node != null) {
			amount = amount + node.items[itemid];
			for (int c = 0; c < node.childN; c++)
				amount = amount + traverse(node.childs[c].hash, itemid);

		}
		return amount;
	}

	public static void traverse_slow(Hashtable atable, int hash, int itemid) {
		int valid = 0;
		int amount = 0;
		Node node = null;
		for (int l = 0; l < ledgerCnt; l++) {
			node = ltable[l].get(hash);
			if (node != null)
				valid++;
		}
		if (valid <= ledgerCnt / 2)
			return;

		for (int l = 0; l < ledgerCnt; l++) {
			node = ltable[l].get(hash);
			if (null == atable.get(hash)) {
				if (node == null)
					continue;
				amount = amount + node.items[itemid];
				atable.put(new Node(hash), amount);
			}
			if (node != null)
				for (int c = 0; c < node.childN; c++)
					traverse_slow(atable, node.childs[c].hash, itemid);
		}
	}

	public static void printTree(Node node) {
		Queue queue = new Queue(170171);
		queue.enqueue(node);
		while (!queue.isempty()) {
			Node vnode = queue.dequeue();
			System.out.print(vnode.hash + "[" + vnode.phash + "] ");
			for (int i = 0; i < vnode.items.length; i++)
				if (vnode.items[i] != 0)
					System.out.print(i + "," + vnode.items[i] + ",");
			System.out.println();
			for (int c = 0; c < vnode.childN; c++) {
				queue.enqueue(vnode.childs[c]);
			}
		}
	}

	static class Queue {
		int size;
		int front, tail;
		Node queue[] = null;

		Queue(int capacity) {
			this.size = 0;
			this.front = this.tail = 0;
			this.queue = new Node[capacity];
		}

		public boolean enqueue(Node node) {
			if (this.isfull())
				return false;

			queue[tail++] = node;
			size++;

			return true;
		}

		public Node dequeue() {
			if (isempty())
				return null;

			return queue[front++];
		}

		public boolean isempty() {
			return front == tail;
		}

		public boolean isfull() {
			return tail == queue.length;
		}

	}
}

class Hashtable {
	int capacity = 0, size = 0;
	NodeList table[] = null;

	int amount = 0;

	Hashtable(int capacity) {
		this.table = new NodeList[capacity];
		this.capacity = capacity;
	}

	public void put(int hash, Node node) {
		int idx = toindex(hash);
		if (table[idx] == null)
			table[idx] = new NodeList();
		table[idx].addNodeToHead(node);
		size++;
	}

	public void put(Node node, int amount) {
		int idx = toindex(node.hash);
		if (table[idx] == null)
			table[idx] = new NodeList();
		table[idx].addNodeToHead(node);
		this.amount = this.amount + amount;
	}

	public Node get(int hash) {
		int idx = toindex(hash);

		if (table[idx] == null || table[idx].head == null)
			return null;

		Node node = table[idx].head;
		while (node != null) {
			if (node.hash == hash)
				return node;
			node = node.next;
		}

		return null;
	}

	private int toindex(int hash) {
		return (hash & 0x7fffffff) % this.capacity;
	}
}

class NodeList {
	Node head;

	public void addNodeToHead(Node node) {
		if (head == null) {
			this.head = node;
			return;
		}

		node.next = head;
		head = node;
	}
}

class Node {
	int hash;
	int phash;
	int items[] = null;
	Node next;

	int childN = 0;
	Node childs[] = new Node[20];

	Node(int hash) {
		this.hash = hash;
	}

	Node(int hash, int phash, int items[]) {
		this.hash = hash;
		this.phash = phash;
		this.items = items;
	}

	public void addChilds(Node child) {
		for (int i = 0; i < childN; i++) {
			if (childs[i] != null) {
				if (childs[i].hash == child.hash)
					return;
			}
		}
		childs[childN++] = child;
	}
}

class Item {
	int id;
	int amount;

	Item(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}
}