package example.zombyhunter;

public class User {
	// hunter coordinate
	static int hunt_y, hunt_x;

	static final int X = 0;
	static final int Y = 0;
	static final double NaN = Double.MAX_VALUE;

	// index table : /w coordinate, /w index
	static Zomby zbcoordtable[][] = null;
	static Zomby zbidxtable[] = null;

	// priority queue
	static MxShotQueue mxpshotque = null;

	// target coordinate [..][0] = x(col), [..][1] =y(raw)
	static int tgcoord[][] = null;

	public static boolean getTarget(int map_size, int map[][], int shot[][]) {

		int zbidx_seed = 0, tgidx = 0;

		zbcoordtable = new Zomby[map_size][map_size];
		zbidxtable = new Zomby[map_size * map_size];
		tgcoord = new int[map_size * 2 + (map_size - 2) * 2][2];
		mxpshotque = new MxShotQueue(map_size * 2 + (map_size - 2) * 2);

		/**
		 * EXTRACT HUNTER'S & ZOMBY'S COORDINATE
		 */
		for (int y = 0; y < map_size; y++) {
			for (int x = 0; x < map_size; x++) {
				if (map[y][x] == 1) {// hunter
					hunt_y = y;
					hunt_x = x;
				} else if (map[y][x] >= 10) {// zomby
					Zomby zb = new Zomby(zbidx_seed, y, x, map[y][x]);
					zbidxtable[zbidx_seed] = zb;
					zbcoordtable[y][x] = zb;
				}
				// all shootable coordinate
				if (y == 0 || y == map_size - 1) {
					tgcoord[tgidx][X] = x;
					tgcoord[tgidx][Y] = y;
					tgidx++;
				}
				if (y > 0 && y < map_size - 1 && (x == 0 || x == map_size - 1)) {
					tgcoord[tgidx][X] = x;
					tgcoord[tgidx][Y] = y;
				}
			}
		}

		/**
		 * CONSTRUCT VALID-SHOT (WITH POSSILBY KILLING ZOMBY) LIST
		 */
		for (int t = 0; t < tgidx; t++) {
			int tgy = tgcoord[t][Y], tgx = tgcoord[t][X];
			int hty = hunt_y, htx = hunt_x;

			int startx = -1, endx = -1, boundx = -1;

			if (tgx < htx) {
				startx = 0;
				endx = htx - 1;
			} else if (tgx > htx) {
				startx = htx + 1;
				endx = tgx;
			} else {
				boundx = htx;
			}

			if (boundx != -1) {
				continue;
			}

			// extract 1st order equation
			double gradient = getgradient(hty, htx, tgy, (double) tgx);
			double bias = getbias(hty, htx, tgy, (double) tgx);

			// shot that is possibly added to the shot table
			boolean needtoaddshot = false;
			Shot pshot = new Shot(tgy, tgx);

			if (gradient == NaN) {
				int starty = 0, endy = 0;
				if (tgy > hty) {
					starty = hty + 1;
					endy = map.length - 1;
				} else if (tgy < hty) {
					starty = 1;
					endy = tgy - 1;
				} else {
					// error in target coordinate extraction
				}

				for (int y = starty; y <= endy; y++) {
					if (zbcoordtable[y][tgx] != null) {
						ZbIndex zbidx = new ZbIndex(zbcoordtable[y][tgx].zid);
						pshot.zidxlist.addZombyToHead(zbidx);
						needtoaddshot = true;
					}
				}
			} else {
				for (int x = startx; x <= endx; x++) {

					double dy = gety(gradient, bias, (double) x);

					// iy1, iy2 coordinate to check zomby existance
					int iy1 = (int) dy;
					int iy2 = -1;
					if ((double) iy1 - dy != 0)
						iy2 = iy1 + 1;

					if (zbcoordtable[iy1][x] != null) {
						ZbIndex zbidx = new ZbIndex(zbcoordtable[iy1][x].zid);
						pshot.zidxlist.addZombyToHead(zbidx);
						needtoaddshot = true;
					} else {
						// error!! when making zbindex(coord) table
					}
					if (zbcoordtable[iy2][x] != null) {
						ZbIndex zbidx = new ZbIndex(zbcoordtable[iy2][x].zid);
						pshot.zidxlist.addZombyToHead(zbidx);
						needtoaddshot = true;
					} else {
						// error!! when making zbindex(coord) table
					}
				}
			}

			// add shot when this shot can decrease zomby's hp
			if (needtoaddshot)
				mxpshotque.enqueue(pshot);

		}

		return false;
	}

	public static int getScore(int size, int map[][], int shot[][]) {
		return 0;
	}

	public static double gety(double gradient, double bias, double x) {
		return gradient * x - bias;
	}

	public static double getgradient(double y1, double x1, double y2, double x2) {
		if (x2 - x1 == 0)
			return NaN;
		return (y2 - y1) / (x2 - x1);
	}

	public static double getbias(double y1, double x1, double y2, double x2) {
		if (x2 - x1 == 0)
			return NaN;
		return (x1 * y2 - x2 * y1) / (x2 - x1);
	}

}

class MxShotQueue {
	Shot max;
	Shot shottable[] = null;
	int size = 0;

	MxShotQueue(int capacity) {
		this.max = null;
		this.shottable = new Shot[capacity];
	}

	// just keep this.max
	public void enqueue(Shot shot) {
		shottable[size++] = shot;

		if (max == null) {
			max = shot;
			return;
		}

		if (shot.zcnt > max.zcnt)
			this.max = shot;

	}

	// change this.max when dequeing the max
	public Shot dequeue() {

		return this.max;
	}

}

class Shot {
	int tg_x, tg_y;
	int zcnt;

	ZbIndexList zidxlist;

	public Shot(int y, int x) {
		this.tg_y = y;
		this.tg_x = x;
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

	ZbIndex(int zbidx) {
		this.zbidx = zbidx;
	}
}

class Zomby {
	int zid;
	int zb_x, zb_y, hp;

	public Zomby(int zid, int y, int x, int hp) {
		this.zid = zid;
		this.zb_x = x;
		this.zb_y = y;
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