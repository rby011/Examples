package example.logistics;

public class UserSolution {
	static int NID = 1, EID = 1;
	static int MAX_NODE = 0;

	static Node nidx_table[] = null;
	static Edge eidx_table[] = null;
	static Edge emap_table[][] = null;

	static Graph graph = null;

	static final int HORIZONTAL = 1, VERTICAL = 2;

	public static void main(String args[]) {
		init(5);
		addTerminal(2, 402);
		graph.print();
		System.out.println();

		addTerminal(104, 101);
		graph.print();
		System.out.println();

		addTerminal(200, 203);
		graph.print();
		System.out.println();

	}

	public static void init(int n) {
		MAX_NODE = (n + 1) * (n + 1);
		nidx_table = new Node[MAX_NODE];
		eidx_table = new Edge[MAX_NODE];
		emap_table = new Edge[n][n];
		graph = new Graph(MAX_NODE);
	}

	public static void addTerminal(int terminal1, int terminal2) {
		int y1 = terminal1 / 100, x1 = terminal1 % 100;
		int y2 = terminal2 / 100, x2 = terminal2 % 100;

		int ut = 0, vt = 0;
		int type = 0;

		if (y1 == y2) {
			if (x2 > x1) {
				vt = terminal2;
				ut = terminal1;
			} else if (x1 > x2) {
				vt = terminal1;
				ut = terminal2;
			}
			type = HORIZONTAL;
		} else if (x1 == x2) {
			if (y2 > y1) {
				vt = terminal2;
				ut = terminal1;
			} else if (y1 > y2) {
				vt = terminal1;
				ut = terminal2;
			}
			type = VERTICAL;
		}

		Node u = new Node(ut, NID++);
		Node v = new Node(vt, NID++);

		nidx_table[u.node_id] = u;
		nidx_table[v.node_id] = v;

		Edge uedge = null, vedge = null;// uedge 순방향, vedge 역방향
		try {
			uedge = new Edge(EID++, u.node_id, v.node_id, nidx_table);
			vedge = new Edge(EID++, v.node_id, u.node_id, nidx_table);

			eidx_table[uedge.edge_id] = uedge;
			eidx_table[vedge.edge_id] = vedge;

			graph.addEdgeIndex(uedge.u_id, uedge.edge_id);
			graph.addEdgeIndex(vedge.u_id, vedge.edge_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (type == VERTICAL) {
			int x = u.x;
			for (int y = u.y; y <= v.y; y++) {
				if (emap_table[y][x] == null) {
					emap_table[y][x] = uedge;
				} else {
					int vert_u = u.node_id;
					int vert_v = v.node_id;
					int hori_u = emap_table[y][x].u_id;
					int hori_v = emap_table[y][x].v_id;
					try {
						Edge edge1 = new Edge(EID++, hori_v, vert_u, nidx_table);
						eidx_table[edge1.edge_id] = edge1;
						graph.addEdgeIndex(hori_v, edge1.edge_id);
						Edge edge2 = new Edge(EID++, vert_u, hori_u, nidx_table);
						eidx_table[edge2.edge_id] = edge2;
						graph.addEdgeIndex(vert_u, edge2.edge_id);
						Edge edge3 = new Edge(EID++, hori_u, vert_v, nidx_table);
						eidx_table[edge3.edge_id] = edge3;
						graph.addEdgeIndex(hori_u, edge3.edge_id);
						Edge edge4 = new Edge(EID++, vert_v, hori_v, nidx_table);
						eidx_table[edge4.edge_id] = edge4;
						graph.addEdgeIndex(vert_v, edge4.edge_id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (type == HORIZONTAL) {
			int y = u.y;
			for (int x = u.x; x <= v.x; x++) {
				if (emap_table[y][x] == null) {
					emap_table[y][x] = uedge;
				} else {
					int vert_u = emap_table[y][x].u_id;
					int vert_v = emap_table[y][x].v_id;
					int hori_u = u.node_id;
					int hori_v = v.node_id;
					try {
						Edge edge1 = new Edge(EID++, hori_v, vert_u, nidx_table);
						eidx_table[edge1.edge_id] = edge1;
						graph.addEdgeIndex(hori_v, edge1.edge_id);
						Edge edge2 = new Edge(EID++, vert_u, hori_u, nidx_table);
						eidx_table[edge2.edge_id] = edge2;
						graph.addEdgeIndex(vert_u, edge2.edge_id);
						Edge edge3 = new Edge(EID++, hori_u, vert_v, nidx_table);
						eidx_table[edge3.edge_id] = edge3;
						graph.addEdgeIndex(hori_u, edge3.edge_id);
						Edge edge4 = new Edge(EID++, vert_v, hori_v, nidx_table);
						eidx_table[edge4.edge_id] = edge4;
						graph.addEdgeIndex(vert_v, edge4.edge_id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public static int getDistance(int from, int to) {
		Node src = getNode(from), dst = getNode(to);

		src.key = 0;
		MinHeap minqueue = new MinHeap(nidx_table);

		while (!minqueue.isempty()) {
			Node unode = minqueue.dequeue();
			EdgeIndex eidx = graph.adjlist[unode.node_id].head;
			while (eidx != null) {
				Edge edge = eidx_table[eidx.edge_id];
				Node vnode = minqueue.findNode(edge.v_id);
				if (vnode != null && edge.distance + unode.key < vnode.key) {
					vnode.key = edge.distance + unode.key;
					minqueue.update(vnode.node_id, vnode.key);
				}
				eidx = eidx.next;
			}
		}

		return dst.key;
	}

	private static Node getNode(int terminal) {
		int y = terminal / 100;
		int x = terminal % 100;

		int uid = emap_table[y][x].u_id;
		int vid = emap_table[y][x].v_id;

		Node u = nidx_table[uid];
		Node v = nidx_table[vid];

		if (u.coordinate == terminal)
			return u;
		else if (v.coordinate == terminal)
			return v;

		return null;
	}

}

class Graph {
	EdgeIdxList adjlist[] = null;

	Graph(int nodecnt) {
		adjlist = new EdgeIdxList[nodecnt + 1];
		for (int i = 0; i < adjlist.length; i++)
			adjlist[i] = new EdgeIdxList();
	}

	public void addEdgeIndex(int u, int edgeIndex) {
		EdgeIndex eindex = new EdgeIndex(edgeIndex);
		adjlist[u].addEdgeToHead(eindex);
	}

	public void print() {
		Node nidx_table[] = UserSolution.nidx_table;
		Edge eidx_table[] = UserSolution.eidx_table;

		for (int i = 1; i < adjlist.length; i++) {
			if (adjlist[i].head != null) {
				EdgeIndex eidx = adjlist[i].head;
				int u_id = eidx_table[eidx.edge_id].u_id;
				Node u = nidx_table[u_id];
				System.out.print("[" + u.y + "," + u.x + "] --> ");
				while (eidx != null) {
					int v_id = eidx_table[eidx.edge_id].v_id;
					Node v = nidx_table[v_id];
					System.out.print("[" + v.y + "," + v.x + "/" + eidx_table[eidx.edge_id].distance + "] ");
					eidx = eidx.next;
				}
				System.out.println();
			}
		}

		System.out.println();
	}
}

class EdgeIdxList {
	EdgeIndex head;

	public void addEdgeToHead(EdgeIndex edgeidx) {
		if (head == null) {
			this.head = edgeidx;
			return;
		}

		edgeidx.next = head;
		this.head = edgeidx;
	}
}

class EdgeIndex {
	int edge_id;
	EdgeIndex next;

	EdgeIndex(int eidx) {
		this.edge_id = eidx;
	}
}

class Edge {

	int edge_id;
	int u_id, v_id;
	int distance;

	Edge(int eid, int uid, int vid, Node nidx_table[]) throws Exception {
		this.edge_id = eid;
		this.u_id = uid;
		this.v_id = vid;

		Node u = nidx_table[uid];
		Node v = nidx_table[vid];

		if (u == null || v == null)
			throw new Exception();

		if (u.y == v.y) {
			this.distance = v.x - u.x;
		} else if (u.x == v.x) {
			this.distance = v.y - u.y;
		} else {
			int distx = v.x - u.x;
			int disty = v.y - u.y;
			if (distx < 0)
				distx = distx * -1;
			if (disty < 0)
				disty = disty * -1;
			this.distance = distx + disty;
		}

		if (this.distance < 0)
			this.distance = this.distance * -1;
	}
}

class Node {
	int node_id;
	int y, x;
	int coordinate;

	int key;

	public Node(int terminal_id, int node_id) {
		this.coordinate = terminal_id;
		this.y = terminal_id / 100;
		this.x = terminal_id % 100;
		this.node_id = node_id;
	}
}

class MinHeap {

	int R = 1;
	int size = 0;

	Node tree[] = null;
	int nidx2tidx[] = null;

	public MinHeap(Node nodes[]) {
		this.tree = new Node[nodes.length];
		nidx2tidx = new int[tree.length];
		for (int tidx = 1; tidx < tree.length && tree[tidx] != null; tidx++) {
			tree[tidx] = nodes[tidx];
			tree[tidx].key = Integer.MAX_VALUE;
			nidx2tidx[tree[tidx].node_id] = tidx;
			size++;
		}

		buildheap();
	}

	private void buildheap() {
		for (int tidx = this.size / 2; tidx >= R; tidx--)
			minheapify(tidx);
	}

	private void minheapify(int tidx) {
		int lcidx = tidx * 2, rcidx = lcidx + 1;

		boolean haslchild = lcidx >= size;
		boolean hasrchild = rcidx >= size;

		if (!haslchild && !hasrchild)
			return;

		int minidx = lcidx;
		if (hasrchild && tree[rcidx].key < tree[lcidx].key)
			minidx = rcidx;

		if (tree[tidx].key <= tree[minidx].key)
			return;

		swap(tidx, minidx);

		minheapify(minidx);
	}

	private void liftup(int tidx) {
		while (tidx > 1) {
			int pidx = tidx / 2;
			if (tree[pidx].key <= tree[tidx].key)
				break;
			swap(tidx, pidx);
			tidx = pidx;
		}
	}

	public Node findNode(int node_id) {
		if (!contains(node_id))
			return null;

		int tidx = nidx2tidx[node_id];
		return tree[tidx];
	}

	public Node dequeue() {
		Node ret = tree[R];

		swap(R, size);

		tree[size] = null;
		nidx2tidx[ret.node_id] = 0;
		size--;

		minheapify(R);

		return ret;
	}

	public void update(int node_id, int key) {
		int tidx = nidx2tidx[node_id];
		tree[tidx].key = key;

		liftup(tidx);
		minheapify(tidx);
	}

	public boolean contains(int node_id) {
		return nidx2tidx[node_id] != 0;
	}

	public boolean isempty() {
		return size == 0;
	}

	private void swap(int tidx1, int tidx2) {
		int nidx1 = tree[tidx1].node_id;
		int nidx2 = tree[tidx2].node_id;

		Node tnode = tree[tidx1];
		tree[tidx1] = tree[tidx2];
		tree[tidx2] = tnode;

		int tmp = nidx2tidx[nidx1];
		nidx2tidx[nidx1] = nidx2tidx[nidx2];
		nidx2tidx[nidx2] = tmp;

	}
}
