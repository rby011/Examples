package example.zombyhunter;

public class User {
	static int hraw, hcol;// hunter coordinate

	static int zbidx;
	static int zbcount;
	// in order to check duplication when creating Zomby instance
	static Zomby zbcoordtable[][] = null;
	static Zomby zbidxtable[] = null;
	static int zbhptable[] = null;

	static MaxHeap mheap = null;

	// GIVEN : map[MAP_MAX][MAP_MAX]
	// GIVEN : shot[SHOT_MAX ][2] / [..][0] : col / [..][1] : raw
	public static boolean getTarget(int map_size, int map[][], int shot[][]) {
		zbidx = 0;
		zbcount = 0;

		/*
		 * Huddle, Zomby
		 */
		zbcoordtable = new Zomby[map_size][map_size];
		zbidxtable = new Zomby[map_size * map_size];
		zbhptable = new int[map_size * map_size];

		for (int r = 1; r < map_size - 1; r++) {
			for (int c = 1; c < map_size - 1; c++) {
				if (map[r][c] == 1) {// hunter
					hraw = r;
					hcol = c;
				} else if (map[r][c] >= 10) {
					Zomby zb = new Zomby(zbidx, r, c, map[r][c]);
					zbidxtable[zbidx] = zb;
					zbcoordtable[r][c] = zb;
					zbhptable[zbidx] = map[r][c];
					
					zbcount = zbcount + 1;
					zbidx = zbidx + 1;
				}
			}
		}

		mheap = new MaxHeap(map_size * map_size + 1, zbcount, zbidxtable,zbhptable);
		/*
		 * Shot - UP, DOWN, LEFT, RIGHT
		 */
		// UP : sraw is 0
		for (int i = 0; i < map_size; i++) {
			double gradient = getGradient(0, (double) i, hraw, hcol);
			double bias = getBias(0, (double) i, hraw, hcol);

			Shot shoot = new Shot(0, i);

			if (gradient == Double.MAX_VALUE) {
				for (int sr = 1; sr < hraw; sr++) {
					if (map[sr][hcol] >= 10) {
						addZombyIdx(shoot, map, sr, hcol);
					}
				}
				mheap.enqueue(shoot);
				continue;
			}

			int min = 0, max = 0;
			if (i > hcol) {
				min = hcol + 1;
				max = i - 1;
			} else {
				min = i + 1;
				max = hcol - 1;
			}

			for (int sc = min; sc <= max; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw;
				if (raw - (double) floor_raw > 0.0d)
					ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZombyIdx(shoot, map, floor_raw, sc);
				if (floor_raw != ceiling_raw && map[ceiling_raw][sc] >= 10)
					addZombyIdx(shoot, map, ceiling_raw, sc);

			}

			mheap.enqueue(shoot);
		}

		// DOWN
		for (int i = 0; i < map_size; i++) {

			double gradient = getGradient(map_size - 1, (double) i, hraw, hcol);
			double bias = getBias(map_size - 1, (double) i, hraw, hcol);

			Shot shoot = new Shot(map_size, i);

			if (gradient == Double.MAX_VALUE) {
				for (int sr = hraw + 1; sr < map_size; sr++) {
					if (map[sr][hcol] >= 10) {
						addZombyIdx(shoot, map, sr, hcol);
					}
				}
				mheap.enqueue(shoot);
				continue;
			}

			int min = 0, max = 0;
			if (i > hcol) {
				min = hcol + 1;
				max = i - 1;
			} else {
				min = i + 1;
				max = hcol - 1;
			}

			for (int sc = min; sc <= max; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZombyIdx(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZombyIdx(shoot, map, ceiling_raw, sc);

			}
			mheap.enqueue(shoot);

		}

		// LEFT
		for (int i = 1; i < map_size - 1; i++) {

			double gradient = getGradient((double) i, 0, hraw, hcol);
			double bias = getBias((double) i, 0, hraw, hcol);

			Shot shoot = new Shot(i, 0);

			if (gradient == 0.0d) {
				for (int sc = 1; sc < hcol; sc++) {
					if (map[hraw][sc] >= 10) {
						addZombyIdx(shoot, map, hraw, sc);
					}
				}
				mheap.enqueue(shoot);
				continue;
			}

			for (int sc = 1; sc <= hcol - 1; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZombyIdx(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZombyIdx(shoot, map, ceiling_raw, sc);

			}
			mheap.enqueue(shoot);

		}

		// RIGHT
		for (int i = 1; i < map_size - 1; i++) {

			double gradient = getGradient((double) i, map_size - 1, hraw, hcol);
			double bias = getBias((double) i, map_size - 1, hraw, hcol);

			Shot shoot = new Shot(i, map_size - 1);

			if (gradient == 0.0d) {
				for (int sc = hcol + 1; sc < map_size - 1; sc++) {
					if (map[hraw][sc] >= 10) {
						addZombyIdx(shoot, map, hraw, sc);
					}
				}
				mheap.enqueue(shoot);
				continue;
			}

			for (int sc = hcol + 1; sc <= map_size - 1; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZombyIdx(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZombyIdx(shoot, map, ceiling_raw, sc);

			}
			mheap.enqueue(shoot);

		}

		return false;
	}

	public static int getScore(int size, int map[][], int shot[][]) {
		int shootcnt = 0;

		while (!mheap.isempty() && shootcnt < shot.length) {
			mheap.printHeap();
			Shot shoot = mheap.dequeue();
			int raw = shoot.sraw;// y, 1
			int col = shoot.scol;// x, 0
			shot[shootcnt][0] = col;
			shot[shootcnt][1] = raw;
			shootcnt = shootcnt + 1;
			System.out.println("(x,y) = " + col + "," + raw);
			mheap.printHeap();
		}

		int zbcnt = mheap.zcount;

		return (zbcnt * (-1000)) + ((shot.length - shootcnt) * 100 + 100);
	}

	public static void addZombyIdx(Shot shoot, int map[][], int raw, int col) {
		Zomby zomby = zbcoordtable[raw][col];
		ZbIndex zbidx = new ZbIndex();
		zbidx.zbidx = zomby.zid;
		shoot.addZomby(zbidx);
	}

	public static double getGradient(double y1, double x1, double y2, double x2) {
		if (x1 == x2)
			return Double.MAX_VALUE;
		return (y2 - y1) / (x2 - x1);
	}

	public static double getBias(double y1, double x1, double y2, double x2) {
		if (x1 == x2)
			return Double.MAX_VALUE;
		return ((x1 * y2 - x2 * y1) / (x2 - x1));
	}

}

class MaxHeap {

	Shot tree[] = null;
	int size;
	int zcount = 0;

	Zomby zbidxtable[] = null;
	int zbhptable[] = null;
	
	public MaxHeap(int capacity, int zcount, Zomby zbidxtable[], int zbhptable[]) {
		this.tree = new Shot[capacity + 1];
		this.size = 0;
		this.zcount = zcount;
		this.zbidxtable = zbidxtable;
		this.zbhptable = zbhptable;
	}

	public void enqueue(Shot shot) {
		int nindex = ++size;
		tree[nindex] = shot;

		while (nindex > 1) {
			int pindex = nindex / 2;
			if (tree[pindex].zcnt < tree[nindex].zcnt) {
				Shot tmp = tree[pindex];
				tree[pindex] = tree[nindex];
				tree[nindex] = tmp;

				nindex = pindex;
			} else {
				break;
			}
		}
	}

	public Shot dequeue() {
		if (size == 0)
			return null;

		Shot max = tree[1];

		ZbIndex zbidx = max.zidxlist.head;
		while (zbidx != null) {
			Zomby zomby = this.zbidxtable[zbidx.zbidx];
			if (zomby.hp >= 10) {
				zomby.hp = zomby.hp - 10;
				zbhptable[zbidx.zbidx] = zomby.hp;
				if (zomby.hp == 0) {
					max.zcnt = max.zcnt - 1;
					zcount = zcount - 1;
				}
			}
			zbidx = zbidx.next;
		}

		// HEAP 을 사용하는 의미가 없음. 이 루프에서 MAX 를 얻을 수 있음!!! 어떻게 풀어야 하나??
		for (int i = 2; i <= this.size; i++) {
			zbidx = tree[i].zidxlist.head;
			while (zbidx != null) {
				if (zbhptable[zbidx.zbidx] == 0) {
					tree[i].zcnt--;
				}
				zbidx = zbidx.next;
			}
		}

		if (max.zcnt == 0) {
			tree[1] = tree[size];
			size = size - 1;
		}

		maxheapify(1);

		return max;
	}

	public boolean isempty() {
		return this.size == 0;
	}

	public void maxheapify(int index) {
		int lcindex = index * 2;
		int rcindex = lcindex + 1;

		boolean haslchild = lcindex <= size;
		boolean hasrchild = rcindex <= size;

		if (!haslchild) {
			return;
		}

		int maxindex = lcindex;
		if (hasrchild && tree[lcindex].zcnt > tree[rcindex].zcnt) {
			maxindex = lcindex;
		}

		if (tree[index].zcnt >= tree[maxindex].zcnt)
			return;

		Shot tmp = tree[index];
		tree[index] = tree[maxindex];
		tree[maxindex] = tmp;

		maxheapify(maxindex);
	}

	public void printHeap() {
		int index = 1;
		while (true) {
			Shot shot = tree[index];
			if (shot == null)
				break;
			System.out.println("" + index + " : " + shot.zcnt);
			index++;
		}
	}
}

class Shot {
	int sraw, scol;
	int reqhp;
	int zcnt;

	ZbIndexList zidxlist;

	public Shot(int sraw, int scol) {
		this.sraw = sraw;
		this.scol = scol;
		this.zidxlist = new ZbIndexList();
	}

	public void addZomby(ZbIndex zbidx) {
		zcnt++;
		zidxlist.addZombyToHead(zbidx);
	}
}

class ZbIndexList {
	ZbIndex head;

	public void addZombyToHead(ZbIndex zbidx) {
		if (head == null) {
			this.head = zbidx;
			return;
		}
		zbidx.next = head;
		head = zbidx;
	}
}

class ZbIndex {
	int zbidx;
	ZbIndex next;
}

class Zomby {
	int zid;
	int zraw, zcol, hp;

	public Zomby(int zid, int raw, int col, int hp) {
		this.zid = zid;
		this.zraw = raw;
		this.zcol = col;
		this.hp = hp;
	}
}

class HuddleList {
	Huddle head;

	public void addHuddleToHead(Huddle huddle) {
		if (head == null) {
			this.head = huddle;
			return;
		}
		huddle.next = head;
		head = huddle;
	}
}

class Huddle {
	int hraw, hcol;

	Huddle(int raw, int col) {
		this.hraw = raw;
		this.hcol = col;
	}

	Huddle next;
}