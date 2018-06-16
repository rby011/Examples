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
		// System.out.println("* Ledger Count : " + L);
		ltable = new Hashtable[L];
		roots = new Node[L];
		ledgerCnt = L;

		for (int l = 0; l < L; l++) {
			ltable[l] = new Hashtable(17071);
			Node tNodes[] = new Node[17071];

			int size = ledgerData[l][0] * 16777216 + ledgerData[l][1] * 65536 + ledgerData[l][2] * 256
					+ ledgerData[l][3];
			// System.out.println(" - size : " + size);

			int index = 4, nCnt = 0;
			while (index < size + 4) {
				int pos = index;
				int pHash = ledgerData[l][index++] * 16777216 + ledgerData[l][index++] * 65536
						+ ledgerData[l][index++] * 256 + ledgerData[l][index++];
				int random = ledgerData[l][index++] * 256 + ledgerData[l][index++];
				int itemN = ledgerData[l][index++];

				// ITEM IN A SMALL DEPT
				Item items[] = new Item[itemN];
				// System.out.println(" - phash randome itemN " + pHash + "\t" + random + "\t" +
				// itemN);
				for (int i = 0; i < itemN; i++) {
					int itemID = ledgerData[l][index++];
					int amount = ledgerData[l][index++] * 256 + ledgerData[l][index++];

					items[i] = new Item(itemID, amount);

					// System.out.println(" --- itemID amount " + itemID + "\t" + amount);
				}

				int len = index - pos;
				int hash = Solution.calcHash(ledgerData[l], pos, len);

				// FOR EACH SMALL DEPT
				Node node = new Node(hash, pHash, items);
				ltable[l].put(hash, node);
				tNodes[nCnt++] = node;
				// if (TC == 4)
				// System.out.println(hash + " , " + pHash);
			}

			// if (TC == 4)
			// for (int n = 0; n < nCnt; n++) {
			// System.out.println(tNodes[n].hash + " , " + tNodes[n].phash);
			// }

			// BUILD HIERACHICAL-RELATIONSHIP FOR EACH LEDGER
			for (int n = 0; n < nCnt; n++) {
				int pHash = tNodes[n].phash;
				if (pHash == 0) {
					roots[l] = tNodes[n];
					continue;
				}
				Node pNode = ltable[l].get(pHash);
				if (pNode != null) {
					pNode.addChilds(tNodes[n]);
				} else {
					// ERROR!
					// System.out.println("ERROR");
				}
			}
			
			int k=10;
			k++;
		}

		gltable = new Hashtable(17071);
	}

	public static int calcAmount(int hash, int itemid) {
		Hashtable atable = new Hashtable(17071);
		for (int l = 0; l < ledgerCnt; l++) {
			Node node = ltable[l].get(hash);
			if (node != null)
				traverse(atable, node, itemid);
		}
		return atable.amount;
	}

	public static void traverse(Hashtable atable, Node node, int itemid) {
		if (node == null)
			return;

		int valid = 0;
		for (int l = 0; l < ledgerCnt; l++) {
			if (ltable[l].get(node.hash) != null) {
				valid++;
			}
		}
		if (valid <= ledgerCnt / 2)
			return;

		if (null == atable.get(node.hash)) {
			int amount = 0;
			for (int i = 0; i < node.items.length; i++) {
				if (node.items[i].id == itemid) {
					amount = node.items[i].amount;
					break;
				}
			}
			atable.put(new Node(node.hash), amount);
		}

		for (int c = 0; c < node.childN; c++) {
			traverse(atable, node.childs[c], itemid);
		}
	}

	public static void printTree(Node node) {
		Queue queue = new Queue(170171);
		queue.enqueue(node);
		while (!queue.isempty()) {
			Node vnode = queue.dequeue();
			System.out.println(vnode.hash + "[" + vnode.phash + "]");
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
	Item items[] = null;
	Node next;

	int childN = 0;
	Node childs[] = new Node[20];

	Node(int hash) {
		this.hash = hash;
	}

	Node(int hash, int phash, Item items[]) {
		this.hash = hash;
		this.phash = phash;
		this.items = items;
	}

	public void addChilds(Node child) {
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