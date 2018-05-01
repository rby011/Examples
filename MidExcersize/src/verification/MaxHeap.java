package verification;

public class MaxHeap {

	public static void main(String args[]) {
		MaxHeap heap = new MaxHeap(10, 100);
		HeapNode node = null;
		for (int i = 1; i <= 10; i++) {
			node = new HeapNode(i + 5, i * 10);
			heap.enqueue(node);
		}
		heap.printHeap();

		heap.dequeue();
		heap.printHeap();

		heap.update(7, 1000);
		heap.printHeap();
		
		heap.update(7, -1);
		heap.printHeap();
		
		heap.update(12, -150);
		heap.printHeap();

	}

	final int R = 1;
	int size;

	HeapNode tree[] = null;
	int nidx2tidx[] = null;

	public MaxHeap(int max_tree_capacity, int max_node_id) {
		this.tree = new HeapNode[max_tree_capacity + 1];
		this.nidx2tidx = new int[max_node_id + 1];
		this.size = 0;
	}

	public void enqueue(HeapNode node) {
		int tidx = size + 1;
		int nidx = node.nid;

		tree[tidx] = node;
		nidx2tidx[nidx] = tidx;
		size = size + 1;

		liftup(tidx);
	}

	public HeapNode dequeue() {
		HeapNode max = tree[R];

		swap(R, size);

		nidx2tidx[max.nid] = 0;
		tree[size] = null;
		size = size - 1;

		maxheapify(R);

		return max;
	}

	public boolean contains(int nidx) {
		if (nidx > this.nidx2tidx.length - 1)
			return false;
		return nidx2tidx[nidx] != 0;
	}

	public boolean update(int nidx, int value) {
		if (!contains(nidx))
			return false;

		int tidx = nidx2tidx[nidx];
		tree[tidx].value = value;

		liftup(tidx);
		maxheapify(tidx);

		return true;
	}

	private void maxheapify(int tidx) {
		int lc_tidx = tidx * 2, rc_tidx = lc_tidx + 1;

		boolean haslchild = lc_tidx <= size;
		boolean hasrchild = rc_tidx <= size;

		if (!haslchild && !hasrchild)
			return;

		int max_child_tidx = lc_tidx;
		if (hasrchild && tree[rc_tidx].value > tree[lc_tidx].value)
			max_child_tidx = rc_tidx;

		if (tree[tidx].value >= tree[max_child_tidx].value)
			return;

		swap(tidx, max_child_tidx);

		maxheapify(max_child_tidx);
	}

	private void liftup(int tidx) {
		int ptidx = tidx;
		while (ptidx / 2 != 0) {
			if (tree[ptidx / 2].value >= tree[ptidx].value)
				break;
			swap(ptidx, ptidx / 2);
			ptidx = ptidx / 2;
		}
	}

	private void swap(int tidx1, int tidx2) {
		int nidx1 = tree[tidx1].nid;
		int nidx2 = tree[tidx2].nid;

		HeapNode ntemp = tree[tidx1];
		tree[tidx1] = tree[tidx2];
		tree[tidx2] = ntemp;

		int itemp = nidx2tidx[nidx1];
		nidx2tidx[nidx1] = nidx2tidx[nidx2];
		nidx2tidx[nidx2] = itemp;
	}

	public boolean isempty() {
		return this.size == 0;
	}

	public void printHeap() {
		int depth = 0;
		while (true) {
			if (!traverse(depth++))
				break;
			System.out.println();
		}
	}

	private boolean traverse(int depth) {
		int start_tidx = (int) Math.pow(2, depth);
		int end_tidx = start_tidx * 2 - 1;

		if (start_tidx > size)
			return false;

		for (int i = start_tidx; i <= size && i <= end_tidx; i++)
			System.out.print(tree[i].nid + "(" + tree[i].value + ")");

		return true;
	}
}

class HeapNode {
	int nid;
	int value;

	public HeapNode(int nid, int value) {
		this.nid = nid;
		this.value = value;
	}
}
