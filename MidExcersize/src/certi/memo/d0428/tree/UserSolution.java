package certi.memo.d0428.tree;

import java.util.Scanner;

/**
 * <PRE>
12
0 1 1 4 4 0000000000000000
1 5 3 5 3 111111111111111
2 0 6 8 4 22222222222222222222222222222222
3 2 2 2 2 3333
4 6 4 3 2 444444
5 1 6 4 4 5555555555555555
6 5 7 3 3 666666666
7 2 7 2 2 7777
8 1 2 3 3 888888888
9 2 3 3 3 999999999
10 1 3 2 2 AAAA
11 4 5 1 1 B
 * </PRE>
 */
public class UserSolution {

	public static void main(String args[]) {
		UserSolution solution = new UserSolution();
		solution.init(10);

		Scanner scan = new Scanner(System.in);

		int N = scan.nextInt();
		for (int n = 0; n < N; n++) {

			int mid = scan.nextInt();
			int y = scan.nextInt();
			int x = scan.nextInt();
			int height = scan.nextInt();
			int width = scan.nextInt();
			char str[] = new char[100];
			char tstr[] = scan.next().toCharArray();
			for (int i = 0; i < tstr.length; i++)
				str[i] = tstr[i];

			solution.create(mid, y, x, height, width, str);
		}

		solution.move(3, 2, 5);
		solution.move(10, 6, 6);
		solution.move(9, 6, 5);
		
		scan.close();

		// solution.create(0, y, x, height, width, str);
	}

	static final int ROOT_REGION_ID = 0;

	static int z_order = 0;

	Region r_table[] = null;
	RegionIndexTreeList rtree_list = null;

	public void init(int screensize) {
		r_table = new Region[screensize * screensize];
		r_table[ROOT_REGION_ID] = new Region(0, 0, screensize, screensize);

		rtree_list = new RegionIndexTreeList();
	}

	public void create(int mid, int y, int x, int height, int width, char str[]) {
		mid = mid + 1;// FROM 1, NOT 0
		//
		// CREATE & ADD MEMO
		//
		Memo memo = new Memo(y, x, height, width, str);
		r_table[mid] = memo;

		//
		// ADD REGION
		//
		boolean added = false;
		// 1. AT THE FIRST TIME
		if (rtree_list.head == null) {
			RegionIndexTree r_tree = new RegionIndexTree(this.r_table);
			added = r_tree.addRegionIndex(mid, memo);
			if (added) {
				rtree_list.addMemoTreeToHead(r_tree);
			} else {
				// error!!
			}
			return;
		}
		// 2. OTHER CASES
		RegionIndexTree r_tree = rtree_list.head;
		while (r_tree != null) {
			added = r_tree.addRegionIndex(mid, memo);
			if (!added) {
				r_tree = r_tree.next;
			} else {
				break;
			}
		}
		// 3. ADD ADDITIONAL Z-LAYER
		if (!added) {
			RegionIndexTree nr_tree = new RegionIndexTree(this.r_table);
			added = nr_tree.addRegionIndex(mid, memo);
			if (added) {
				rtree_list.addMemoTreeToHead(nr_tree);
			} else {
				// error!!
			}
		}
	}

	public void select(int mid) {
		mid = mid + 1;

		Region region = r_table[mid];
		if (region != null) {
			region.z = RegionIndexTreeList.DEFAULT_Z_ORDER - 1;
		} else {
			// error!
		}
	}

	public void move(int mid, int y, int x) {
		mid = mid + 1;

		// 1. 기존 RegionIndex 삭제, property 만 변경해도 되는 경우가 있지 않을까?
		boolean removed = false;
		RegionIndexTree rtree = rtree_list.head;
		while (rtree != null) {
			if (rtree.removeRegionIndex(mid)) {
				removed = true;
				break;
			}
			rtree = rtree.next;
		}
		// 2. 기존 Region 수정
		boolean modified = false;
		if (removed) {
			if (r_table[mid] != null) {
				r_table[mid].y = y;
				r_table[mid].x = x;
				modified = true;
			} else {
				// error!
			}
		} else {
			// error!!! or not existing mid
		}

		// 3. 신규 RegionIndex 추가, 제일 위로 보여함.
		if (modified) {
			rtree.addRegionIndex(mid, r_table[mid]);
			r_table[mid].z = RegionIndexTreeList.DEFAULT_Z_ORDER - 1;
		} else {
			// error!
		}
	}

	public void change(int mid, int height, int width, char str[]) {
		mid = mid + 1;

		// 1. 기존 RegionIndex 삭제, property 만 변경해도 되는 경우가 있지 않을까?
		boolean removed = false;
		RegionIndexTree rtree = rtree_list.head;
		while (rtree != null) {
			if (rtree.removeRegionIndex(mid)) {
				removed = true;
				break;
			}
			rtree = rtree.next;
		}

		// 2. 기존 Region 수정
		boolean modified = false;
		if (removed) {
			if (r_table[mid] != null) {
				r_table[mid].height = height;
				r_table[mid].width = width;
				char dst[] = new char[str.length];
				int i = 0;
				for (; str[i] != '\0'; i++)
					dst[i] = str[i];
				((Memo) r_table[mid]).str = dst;
				modified = true;
			} else {
				// error!
			}
		} else {
			// error!!! or not existing mid
		}

		// 3. 신규 RegionIndex 추가, 제일 위로 보여함.
		if (modified) {
			rtree.addRegionIndex(mid, r_table[mid]);
			r_table[mid].z = RegionIndexTreeList.DEFAULT_Z_ORDER - 1;
		} else {
			// error!
		}
	}

	public void getScreenContext(int y, int x, char res[][]) {

	}
}

class RegionIndexTreeList {
	static int DEFAULT_Z_ORDER = 0;

	RegionIndexTree head;

	public void addMemoTreeToHead(RegionIndexTree rtree) {
		if (head == null) {
			this.head = rtree;
			return;
		}
		rtree.next = head;
		head = rtree;
	}
}

class RegionIndexTree {

	int default_zorder = -1;

	RegionIndex root_ridx = null;// for tree
	RegionIndexTree next = null;// for tree list
	Region r_table[] = null;

	public RegionIndexTree(Region r_table[]) {
		this.default_zorder = RegionIndexTreeList.DEFAULT_Z_ORDER++;

		this.r_table = r_table;
		this.root_ridx = new RegionIndex(UserSolution.ROOT_REGION_ID);
	}

	public boolean addRegionIndex(int rid, Region region) {
		RegionIndex pridx = findParentRegionIndex(this.root_ridx, region);
		if (pridx != null) {

			region.z = this.default_zorder;

			pridx.child_list.addRegionIndexToHead(rid);
			return true;
		}
		return false;
	}

	RegionIndex pridx = null;
	public boolean removeRegionIndex(int rid) {
		RegionIndex tpridx = pridx;
		RegionIndex ridx = findRegionIndex(rid, this.root_ridx, pridx);

		if (ridx == null)
			return false;

		if (pridx != null && pridx != tpridx) {
			pridx.child_list.removeRegionIndex(rid);
			RegionIndex child = ridx.child_list.head;
			while (child != null) {
				pridx.child_list.addRegionIndexToHead(child.rid);
				child = child.next;
			}
			return true;
		}

		return false;
	}

	// in : rid , current
	// out : region idnex , parent
	private RegionIndex findRegionIndex(int rid, RegionIndex current, RegionIndex parent) {
		RegionIndex ridx_node = current;

		while (ridx_node != null) {
			if (ridx_node.rid == rid) {
				this.pridx = parent;
				return ridx_node;
			} else {
				if (ridx_node.haschild()) {
					RegionIndex cridx_node = ridx_node.child_list.head;
					while (cridx_node != null) {
						RegionIndex ret = findRegionIndex(rid, cridx_node, ridx_node);
						if (ret != null) {
							return ret;
						}
						cridx_node = cridx_node.next;
					}
				}
			}

			ridx_node = ridx_node.next;
		}
		return null;

	}

	private RegionIndex findParentRegionIndex(RegionIndex contaner_ridx, Region new_region) {
		if (r_table[contaner_ridx.rid].containable(new_region)) {
			if (contaner_ridx.haschild()) {
				RegionIndexList ridx_child_list = contaner_ridx.child_list;
				RegionIndex ridx_child = ridx_child_list.head;

				boolean all_not_containable = true;
				boolean all_disjointable = true;
				while (ridx_child != null) {
					Region child_region = r_table[ridx_child.rid];
					if (child_region.containable(new_region)) {
						RegionIndex tcontainer_ridx = findParentRegionIndex(ridx_child, new_region);
						if (tcontainer_ridx != null)
							return tcontainer_ridx;
						all_not_containable = false;
					} else {
						if (!child_region.disjointable(new_region))
							all_disjointable = false;
					}
					ridx_child = ridx_child.next;
				}

				if (all_not_containable && all_disjointable)
					return contaner_ridx;
			} else {
				return contaner_ridx;
			}
		}
		return null;
	}
}

class RegionIndexList {
	RegionIndex head;

	public boolean addRegionIndexToHead(int rid) {
		RegionIndex ridx = new RegionIndex(rid);
		if (head == null) {
			this.head = ridx;
			return true;
		}

		ridx.next = head;
		head = ridx;
		return true;
	}

	public boolean removeRegionIndex(int rid) {
		if (head == null)
			return false;

		if (head.rid == rid) {
			head = head.next;
			return true;
		}

		boolean found = false;
		RegionIndex ridx_node = head;
		RegionIndex pridx_node = null;
		while (ridx_node != null) {
			if (ridx_node.rid == rid) {
				found = true;
				break;
			}
			pridx_node = ridx_node;
			ridx_node = ridx_node.next;
		}

		if (!found)
			return false;

		pridx_node.next = ridx_node.next;
		ridx_node.next = null;
		return true;
	}

}

class RegionIndex {
	int rid;
	RegionIndex next;// for list
	RegionIndexList child_list;// for tree

	public RegionIndex(int rid) {
		this.rid = rid;
		this.child_list = new RegionIndexList();
	}

	public RegionIndex() {
		// TODO Auto-generated constructor stub
	}

	public boolean haschild() {
		if (child_list != null && child_list.head != null)
			return true;
		return false;
	}
}

class Region {
	int z = -1;// layer order
	int y = -1, x = -1;
	int height = -1, width = -1;

	public Region(int y, int x, int height, int width) {
		this.y = y;
		this.x = x;
		this.height = height;
		this.width = width;
	}

	public boolean containable(int y, int x) {
		if (y >= this.y && y <= this.y + this.height - 1) {
			if (x >= this.x && x <= this.x + this.width - 1) {
				return true;
			}
		}
		return false;
	}

	public boolean containable(int y, int x, int height, int width) {
		if (y >= this.y && y + height <= this.y + this.height)
			if (x >= this.x && x + width <= this.x + this.width)
				return true;
		return false;
	}

	public boolean containable(Region region) {
		return containable(region.y, region.x, region.height, region.width);
	}

	public boolean disjointable(int y, int x, int height, int width) {
		if (y >= this.y + this.height || y + height <= this.y)
			return true;
		if (x >= this.x + this.width || x + width <= this.x)
			return true;
		return false;
	}

	public boolean disjointable(Region region) {
		return disjointable(region.y, region.x, region.height, region.width);
	}

}

class Memo extends Region {
	char str[] = null;

	public Memo(int y, int x, int height, int width, char str[]) {
		super(y, x, height, width);
		this.str = new char[str.length];
		for (int i = 0; str[i] != '\0'; i++) {
			this.str[i] = str[i];
		}
	}

}