package example.logistics;

public class User {
//
//	static int N = 0;
//
//	static int map[][] = null;
//
//	static int NID = 0;
//	static Node nodes[][] = null;
//	static Node nidx_table[] = null;
//
//	static int EID = 0;
//	static Edge eidx_table[] = null;
//
//	static final int EMPTY = 0;
//	static final int NOTTHING = 0, VERTICAL = 1, HORIZONTAL = 2;
//	static final int TERMINAL = 1, INTERSECT = 2, HIGHWAY = 3;
//
//	public static void init(int n) {
//		map = new int[N][N];
//		nodes = new Node[N][N];
//		nidx_table = new Node[N * N];
//	}
//
//	public static void addTerminal(int terminal1, int terminal2) {
//		int y1 = terminal1 / 100, x1 = terminal1 % 100;
//		int y2 = terminal1 / 100, x2 = terminal2 % 100;
//
//		Node u_node = null, v_node = null;
//		Edge edge = null;
//
//		if (y1 == y2) {// horizontal
//			int sx = x1 < x2 ? x1 : x2;
//			int ex = x1 < x2 ? x2 : x1;
//
//			if (x1 == sx) {
//				u_node = new Node(createNodeID(), x1, y1);
//				v_node = new Node(createNodeID(), x2, y2);
//				nodes[y1][x1] = u_node;
//				nodes[y2][x2] = v_node;
//			} else {
//				u_node = new Node(createNodeID(), x2, y2);
//				v_node = new Node(createNodeID(), x1, y1);
//			}
//			nidx_table[u_node.nid] = u_node;
//			nidx_table[v_node.nid] = v_node;
//
//			edge = new Edge(createEdgeID(), u_node.nid, v_node.nid, ex - sx);
//			eidx_table[edge.eid] = edge;
//
//			/**
//			 * 1. TERMINAL 추가하기
//			 */
//			if (map[y1][sx] == EMPTY) {
//				map[y1][sx] = definetype(NOTTHING, TERMINAL);
//			} else if (getconsttype(map[y1][sx]) == TERMINAL) {
//				addTurnEdges(true, true, false, false, sx, y1, TERMINAL, edge);
//			}
//			if (map[y1][ex] == EMPTY) {
//				map[y1][ex] = definetype(NOTTHING, TERMINAL);
//			} else if (getconsttype(map[y1][ex]) == TERMINAL) {
//				addTurnEdges(true, true, false, false, ex, y1, TERMINAL, edge);
//			}
//
//			/**
//			 * 2. HIGH WAY 추가하기
//			 */
//			for (int x = sx + 1; x < ex; x++) {
//				if (map[y1][x] == EMPTY) {
//					map[y1][x] = definetype(HORIZONTAL, HIGHWAY);
//				} else if (getdirype(map[y1][x]) == VERTICAL && getconsttype(map[y1][x]) == HIGHWAY) {
//					map[y1][x] = definetype(NOTTHING, INTERSECT);
//					addTurnEdges(true, true, false, false, x, y1, HIGHWAY, edge);
//				} else if (getconsttype(map[y1][x]) == TERMINAL) {
//					addTurnEdges(true, true, false, false, x, y1, HIGHWAY, edge);
//				}
//			}
//
//			// TODO : add edge to graph
//
//		} else if (x1 == x2) {// vertical
//			int sy = y1 < y2 ? y1 : y2;
//			int ey = y1 > y2 ? y2 : y1;
//
//			/**
//			 * 1. TERMINAL 추가하기
//			 */
//			if (map[sy][x1] == EMPTY) {
//				map[sy][x1] = definetype(NOTTHING, TERMINAL);
//			} else if (getconsttype(map[sy][x1]) == TERMINAL) {
//				addTurnEdges(false, false, true, true, x1, sy, TERMINAL, edge);
//			}
//			if (map[ey][x1] == EMPTY) {
//				map[ey][x1] = definetype(NOTTHING, TERMINAL);
//			} else if (getconsttype(map[ey][x1]) == TERMINAL) {
//				addTurnEdges(false, false, true, true, x1, y2, TERMINAL, edge);
//			}
//
//			/**
//			 * 2. HIGH WAY 추가하기
//			 */
//			for (int y = sy + 1; y < ey; y++) {
//				if (map[y][x1] == EMPTY) {
//					map[y][x1] = definetype(HORIZONTAL, HIGHWAY);
//				} else if (getdirype(map[y][x1]) == HORIZONTAL && getconsttype(map[y][x1]) == HIGHWAY) {
//					map[y][x1] = definetype(NOTTHING, INTERSECT);
//					addTurnEdges(false, false, true, true, x1, y, HIGHWAY, edge);
//				} else if (getconsttype(map[y][x1]) == TERMINAL) {
//					addTurnEdges(true, true, true, true, x1, y, HIGHWAY, edge);
//				}
//			}
//
//			// TODO : add edge to graph
//		}
//
//	}
//
//	// x,y = terminal 좌표
//	private static void addTurnEdges(boolean up, boolean down, boolean left, boolean right, int x, int y,
//			int construction, Edge edge) {
//		if (up) {
//			for (int cy = y - 1; cy >= 0; cy--) {
//				if (getconsttype(map[cy][x]) != HIGHWAY) {
//					break;
//				} else if (getconsttype(map[cy][x]) == TERMINAL) {
//					if (construction == TERMINAL) {
//						if (nidx_table[edge.un_id].x == x && nidx_table[edge.un_id].y == y) {
//							// v from up
//							int weight = y - cy + edge.weight;
//							Edge nedge = new Edge(createEdgeID(), edge.vn_id, nodes[cy][x].nid, weight);
//							eidx_table[nedge.eid] = nedge;
//							// TODO : add new edge to graph
//						} else if (nidx_table[edge.vn_id].x == x && nidx_table[edge.vn_id].y == y) {
//							// v from up
//							int weight = y - cy + edge.weight;
//							Edge nedge = new Edge(createEdgeID(), nodes[cy][x].nid, edge.un_id, weight);
//							eidx_table[nedge.eid] = nedge;
//							// TODO : add new edge to graph
//
//						}
//					} else if (construction == HIGHWAY) {
//						int weight1 = y - cy + nidx_table[edge.vn_id].x - x;
//						int weight2 = y - cy + x - nidx_table[edge.un_id].x;
//						Edge nedge1 = new Edge(createEdgeID(), edge.vn_id, nodes[cy][x].nid, weight1);
//						Edge nedge2 = new Edge(createEdgeID(), nodes[cy][x].nid, edge.un_id, weight2);
//						eidx_table[nedge1.eid] = nedge1;
//						eidx_table[nedge2.eid] = nedge2;
//						// TODO : add the new two edges to graph
//
//					}
//					break;
//				}
//			}
//		}
//		if (down) {
//			for (int cy = y + 1; y < N; cy++) {
//				if (getconsttype(map[cy][x]) != HIGHWAY) {
//					break;
//				} else if (getconsttype(map[cy][x]) == TERMINAL) {
//					if (construction == TERMINAL) {
//						if (nidx_table[edge.un_id].x == x && nidx_table[edge.un_id].y == y) {
//							// v from up
//							int weight = cy - y + edge.weight;
//							Edge nedge = new Edge(createEdgeID(), nodes[cy][x].nid, edge.vn_id, weight);
//							eidx_table[nedge.eid] = nedge;
//							// TODO : add new edge to graph
//
//						} else if (nidx_table[edge.vn_id].x == x && nidx_table[edge.vn_id].y == y) {
//							// v from up
//							int weight = cy - y + edge.weight;
//							Edge nedge = new Edge(createEdgeID(), edge.un_id, nodes[cy][x].nid, weight);
//							eidx_table[nedge.eid] = nedge;
//							// TODO : add new edge to graph
//
//						}
//					} else if (construction == HIGHWAY) {
//						int weight1 = cy - y + x - nidx_table[edge.vn_id].x;
//						int weight2 = cy - y + nidx_table[edge.un_id].x - x;
//						Edge nedge1 = new Edge(createEdgeID(), edge.un_id, nodes[cy][x].nid, weight1);
//						Edge nedge2 = new Edge(createEdgeID(), nodes[cy][x].nid, edge.vn_id, weight2);
//						eidx_table[nedge1.eid] = nedge1;
//						eidx_table[nedge2.eid] = nedge2;
//						// TODO : add the new two edges to graph
//
//					}
//					break;
//				}
//			}
//		}
//		if (left) {
//			for (int cx = x - 1; cx >= 0; cx--) {
//				if (getconsttype(map[y][cx]) != HIGHWAY) {
//					break;
//				} else if (getconsttype(map[y][cx]) == TERMINAL) {
//					// TODO : add turn right edges
//					break;
//				}
//			}
//		}
//		if (right) {
//			for (int cx = x + 1; cx < N; cx++) {
//				if (getconsttype(map[y][cx]) != HIGHWAY) {
//					break;
//				} else if (getconsttype(map[y][cx]) == TERMINAL) {
//					// TODO : add turn right edges
//					break;
//				}
//			}
//		}
//	}
//
//	private static int definetype(int direction, int construction) {
//		return direction * 10 + construction;
//	}
//
//	private static int getdirype(int type) {
//		return type / 10;
//	}
//
//	private static int getconsttype(int type) {
//		return type % 10;
//	}
//
//	private static int createNodeID() {
//		return NID++;
//	}
//
//	private static int createEdgeID() {
//		return EID++;
//	}

}

// class Graph {
// EdgeList adjlist[] = null;
//
// Graph(int node_cnt) {
// this.adjlist = new EdgeList[node_cnt];
// }
//
// public void addEdge(Edge edge) {
// if (adjlist[edge.un_id] == null) {
// adjlist[edge.un_id] = new EdgeList();
// }
// adjlist[edge.un_id].addEdgeToHead(edge);
//
// }
// }
//
// class EdgeList {
// Edge head;
//
// public void addEdgeToHead(Edge edge) {
// if (head == null) {
// this.head = edge;
// return;
// }
// edge.next = head;
// head = edge;
// }
// }
//
// class Edge {
// int eid;
// int weight;
// int un_id, vn_id;
//
// Edge next;
//
// public Edge(int eid, int un_id, int vn_id, int weight) {
// this.eid = eid;
// this.un_id = un_id;
// this.vn_id = vn_id;
// this.weight = weight;
// }
//
// }
//
// class Node {
// int nid;
// int x, y;
//
// Node(int nid, int x, int y) {
// this.nid = nid;
// this.x = x;
// this.y = y;
// }
// }
