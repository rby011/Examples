package example.zombyhunter;

public class User {
	static int zshotcnt[] = null;// zomby index 별 조준된 shot 카운트
	static int zhp[] = null;// zomby index 별 hp

	static int zidx;
	static int hraw, hcol;
	static HuddleList hudlist = null;
	static ZombyList zombylist = null;
	static Zomby zombytable[][] = null;
	static MaxHeap mheap = null;

	// GIVEN : map[MAP_MAX][MAP_MAX]
	// GIVEN : shot[SHOT_MAX ][2] / [..][0] : col / [..][1] : raw
	public static boolean getTarget(int map_size, int map[][], int shot[][]) {
		zidx = 0;
		zshotcnt = new int[map_size * map_size];
		zhp = new int[map_size * map_size];

		hudlist = new HuddleList();
		zombylist = new ZombyList();

		zombytable = new Zomby[map_size][map_size];

		mheap = new MaxHeap();

		/*
		 * Huddle, Zomby
		 */
		for (int r = 1; r < map_size - 1; r++) {
			for (int c = 1; c < map_size - 1; c++) {
				if (map[r][c] == 1) {// hunter
					hraw = r;
					hcol = c;
				} else if (map[r][c] == 2) {// huddle
					hudlist.addHuddleToHead(new Huddle(r, c));
				} 
			}
		}

		/*
		 * Shot - UP, DOWN, LEFT, RIGHT
		 */

		// UP : sraw is 0
		for (int i = 0; i < map_size; i++) {
			double gradient = getGradient(0, (double) i);
			double bias = getBias(0, (double) i);

			Shot shoot = new Shot(0, i);

			if (i == hcol) {
				for (int sr = 1; sr < hraw; sr++) {
					if (map[sr][hcol] >= 10) {
						addZomby(shoot, map, sr, hcol);
					}
				}
				continue;
			}
			for (int sc = 1; sc < hraw - 1; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZomby(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZomby(shoot, map, ceiling_raw, sc);

			}
			mheap.enque(shoot);

		}

		// DOWN
		for (int i = 0; i < map_size; i++) {

			double gradient = getGradient(map_size - 1, (double) i);
			double bias = getBias(map_size - 1, (double) i);

			Shot shoot = new Shot(map_size, i);

			if (i == hcol) {
				for (int sr = hraw + 1; sr < map_size; sr++) {
					if (map[sr][hcol] >= 10) {
						addZomby(shoot, map, sr, hcol);
					}
				}
				continue;
			}

			for (int sc = hraw + 1; sc < map_size; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZomby(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZomby(shoot, map, ceiling_raw, sc);

			}
			mheap.enque(shoot);

		}

		// LEFT
		for (int i = 0; i < map_size; i++) {

			double gradient = getGradient((double) i, 0);
			double bias = getBias((double) i, 0);

			Shot shoot = new Shot(i, 0);

			if (i == hraw) {
				for (int sc = 1; sc < hcol; sc++) {
					if (map[hraw][sc] >= 10) {
						addZomby(shoot, map, hraw, sc);
					}
				}
				continue;
			}

			for (int sc = 1; sc < hcol; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZomby(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZomby(shoot, map, ceiling_raw, sc);

			}
			mheap.enque(shoot);

		}

		// RIGHT
		for (int i = 0; i < map_size; i++) {

			double gradient = getGradient((double) i, map_size - 1);
			double bias = getBias((double) i, map_size - 1);

			Shot shoot = new Shot(i, map_size - 1);

			if (i == hraw) {
				for (int sc = hcol + 1; sc < map_size - 1; sc++) {
					if (map[hraw][sc] >= 10) {
						addZomby(shoot, map, hraw, sc);
					}
				}
				continue;
			}

			for (int sc = hcol + 1; sc < map_size - 1; sc++) {

				double raw = gradient * (double) sc - bias;
				int floor_raw = (int) raw;
				int ceiling_raw = floor_raw + 1;

				if (map[floor_raw][sc] >= 10)
					addZomby(shoot, map, floor_raw, sc);
				if (map[ceiling_raw][sc] >= 10)
					addZomby(shoot, map, ceiling_raw, sc);

			}
			mheap.enque(shoot);

		}

		return false;
	}

	public static void addZomby(Shot shoot, int map[][], int raw, int col) {
		Zomby zomby = null;

		if (zombytable[raw][col] == null) {
			zomby = new Zomby(zidx, raw, col, map[raw][col]);
			zombytable[raw][col] = zomby;
			zidx = zidx + 1;
		} else {
			zomby = zombytable[raw][col];
		}

		shoot.zlist.addZombyToHead(zomby);
		zombylist.addZombyToHead(zomby);
	}

	public static double getGradient(double sraw, double scol) {
		if (hcol == scol)
			return Integer.MAX_VALUE;
		return (scol - hcol) / (sraw - hraw);
	}

	public static double getBias(double sraw, double scol) {
		if (hcol == scol)
			return Integer.MAX_VALUE;
		return (hcol * sraw - scol * hraw) / (sraw - scol);
	}

	public static int getScore(int size, int map[][], int shot[][]) {
		return 0;
	}
}

class MaxHeap {
	public void buildheap() {

	}

	public void enque(Shot shot) {

	}

	public Shot dequeue() {
		return null;
	}
}

class Shot {
	int sraw, scol;
	int reqhp;
	int zcnt;
	
	ZombyList zlist;

	public Shot(int sraw, int scol) {
		this.sraw = sraw;
		this.scol = scol;
		this.zlist = new ZombyList();
	}

	public void addZomby(Zomby zomby) {
		zcnt++;
		zlist.addZombyToHead(zomby);
	}
}

class ZombyList {
	Zomby head;

	public void addZombyToHead(Zomby zomby) {
		if (head == null) {
			this.head = zomby;
			return;
		}
		zomby.next = head;
		head = zomby;
	}
}

class Zomby {
	int zid;
	int zraw, zcol, hp;
	Zomby next;

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