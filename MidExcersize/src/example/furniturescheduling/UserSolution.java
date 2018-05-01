package example.furniturescheduling;

/**
 * <PRE>
4
1
0 30 3 4
1 5 2 2
2 50 5 7
-1
3
0 30 3 4
1 5 2 2
2 50 5 7
-1
3
0 30 3 4
1 5 2 2
2 50 5 7
3 100 1 1
-1
5
0 10 2 2
0 10 2 2
0 10 2 2
1 100 2 2
1 100 2 2
1 200 2 2 
-1

[재작업 문제]
1
4
0 100 2 3
0 10  2 3
0 10  2 3
1 100 2 2
1 100 2 2
1 200 2 2 
1 200 2 2
-1
 * </PRE>
 */
public class UserSolution {

	// static boolean startMaking(int masterID, int furnitureID) {
	// static boolean completeMaking(int masterID) {
	static Workers workers = null;
	static MaxQueue queue = null;
	static final int MAX_FURNITURE = 10000;

	public static void init(int masterCount) {
		workers = new Workers(masterCount);
		queue = new MaxQueue(MAX_FURNITURE, MAX_FURNITURE);
	}

	public static void tick(int currenttick) {
		harvest();

		// 1. 대기 작업이 없고, 모든 작업자가 업무중이라면
		// 2. 대기 작업이 없고, 놀고 있는 작업자가 있다면
		if (queue.isempty()) {
			work();
			return;
		}

		// 3. 대기 작업이 있고, 놀고 있는 작업자가 있다면
		if (!queue.isempty() && workers.playingworker() != null) {
			Master master = null;
			while ((master = workers.playingworker()) != null) {
				Furniture furniture = null;
				// max 이나 작업을 기한내에 못끝낼 것이라면 버림
				while (!queue.isempty() && !(furniture = queue.max(currenttick)).compeletable(currenttick))
					queue.dequeue(currenttick);

				if (queue.isempty())
					break;

				furniture = queue.dequeue(currenttick);
				master.assign(furniture);
				Solution.startMaking(master.midx, furniture.fid);
			}

		}

		// 4. 대기 작업이 있고, 놀고 있는 작업자가 없다면
		if (!queue.isempty() && workers.playingworker() == null) {
			while (true) {
				Master min_master = workers.minworker(currenttick);

				Furniture max_furniture = null;
				// max 가 있으나 작업을 기한내에 못끝낼 것이라면 버림
				while (!queue.isempty() && !(max_furniture = queue.max(currenttick)).compeletable(currenttick)) {
					// System.out.println(max_furniture.limit_tick);
					// System.out.println(currenttick);
					// System.out.println(max_furniture.make_time);
					queue.dequeue(currenttick);
				}

				if (!queue.isempty() && min_master.isworking && min_master.furniture != null) {
					if (min_master.furniture.priority(currenttick) < max_furniture.priority(currenttick)) {
						Furniture stopping_furniture = min_master.remove();
						Furniture making_furniture = queue.dequeue(currenttick);
						queue.enqueue(stopping_furniture);

						min_master.assign(making_furniture);
						Solution.startMaking(min_master.midx, making_furniture.fid);
					} else {
						break;
					}
				} else {
					if (!queue.isempty())
						System.out.println("ERRROR4");
					// error!
					break;
				}
			}
		}

		// 작업을 교체했던 그렇지 못했던
		work();
		return;

	}

	private static void harvest() {
		for (int i = 1; i < workers.masters.length; i++) {
			if (workers.masters[i].isworking && workers.masters[i].furniture != null) {
				if (workers.masters[i].furniture.done) {
					workers.masters[i].remove();
					Solution.completeMaking(workers.masters[i].midx);
				}
			}
		}
	}

	private static void work() {
		for (int i = 1; i < workers.masters.length; i++) {
			if (workers.masters[i].isworking && workers.masters[i].furniture != null) {
				workers.masters[i].furniture.make();
			} else if (workers.masters[i].isworking && workers.masters[i].furniture == null) {
				// error!
				System.out.println("ERRROR1");
			} else if (!workers.masters[i].isworking && workers.masters[i].furniture != null) {
				// error!
				System.out.println("ERRROR2");
			} else if (!workers.masters[i].isworking && workers.masters[i].furniture == null) {
			}
		}
	}

	public static void request(int furnitureNumber, int requestTime, int price, int makingTime, int waitingTime) {
		Furniture furniture = new Furniture(furnitureNumber, requestTime, price, makingTime, waitingTime);
		queue.enqueue(furniture);
	}

}

class Workers {
	Master masters[] = null;

	Workers(int worker_cnt) {
		this.masters = new Master[worker_cnt + 1];
		for (int i = 1; i < masters.length; i++)
			masters[i] = new Master(i);
	}

	public Master playingworker() {
		for (int i = 1; i < masters.length; i++) {
			if (!masters[i].isworking && masters[i].furniture == null)
				return masters[i];
		}
		return null;
	}

	public Master minworker(int currenttick) {
		double min = Double.MAX_VALUE;
		Master minmaster = null;

		for (int i = 1; i < masters.length; i++) {
			if (masters[i].isworking && masters[i].furniture != null) {
				double candidate = masters[i].furniture.priority(currenttick);
				if (candidate < min) {
					min = candidate;
					minmaster = masters[i];
				}
			}
		}
		return minmaster;
	}
}

class Master {
	int midx;
	boolean isworking;
	Furniture furniture;

	Master(int midx) {
		this.midx = midx;
		this.isworking = false;
		furniture = null;
	}

	public boolean assign(Furniture furniture) {
		if (isworking)
			return false;
		if (furniture == null)
			return false;

		this.furniture = furniture;
		this.isworking = true;

		return true;
	}

	public Furniture remove() {
		if (!isworking)
			return null;
		if (furniture == null)
			return null;

		Furniture ret = this.furniture;
		this.isworking = false;
		this.furniture = null;
		return ret;
	}
}

class MaxQueue {
	final int R = 1;
	int size;
	Furniture tree[] = null;
	int fidx2tidx[] = null;

	public MaxQueue(int max_capacity, int max_fidx) {
		tree = new Furniture[max_capacity + 1];
		fidx2tidx = new int[max_fidx + 1];
		size = 0;
	}

	public void enqueue(Furniture furniture) {
		int tidx = size + 1;

		tree[tidx] = furniture;
		fidx2tidx[furniture.fid] = tidx;
		size = size + 1;

		// liftup(tidx);
	}

	public Furniture dequeue(int currenttick) {
		buildheap(currenttick);

		Furniture max = tree[R];

		fidx2tidx[max.fid] = tree[size].fid;
		tree[R] = tree[size];
		size = size - 1;

		tree[size + 1] = null;
		maxheapify(R, currenttick);

		return max;
	}

	public Furniture max(int currenttick) {
		buildheap(currenttick);
		return tree[R];
	}

	public boolean isempty() {
		return this.size == 0;
	}

	private void buildheap(int currenttick) {
		int tidx = this.size / 2;
		for (int i = tidx; i >= 1; i--)
			maxheapify(i, currenttick);
	}

	private void maxheapify(int tidx, int currenttick) {
		int lc_tidx = tidx * 2;
		int rc_tidx = lc_tidx + 1;

		boolean haslchild = lc_tidx <= size;
		boolean hasrchild = rc_tidx <= size;

		if (!haslchild && !hasrchild)
			return;

		int max_child_tidx = lc_tidx;
		if (hasrchild && tree[rc_tidx].priority(currenttick) > tree[lc_tidx].priority(currenttick))
			max_child_tidx = rc_tidx;

		if (tree[tidx].priority(currenttick) >= tree[max_child_tidx].priority(currenttick))
			return;

		swap(tidx, max_child_tidx);

		maxheapify(max_child_tidx, currenttick);
	}

	// private void liftup(int tidx, int currenttick) {
	// while (tidx / 2 != 0) {
	// if (tree[tidx / 2].priority(currenttick) >= tree[tidx].priority(currenttick))
	// break;
	// swap(tidx, tidx / 2);
	// tidx = tidx / 2;
	// }
	// }

	private void swap(int tidx1, int tidx2) {
		int fidx1 = fidx2tidx[tidx1];
		int fidx2 = fidx2tidx[tidx2];

		Furniture ftmp = tree[tidx1];
		tree[tidx1] = tree[tidx2];
		tree[tidx2] = ftmp;

		int tidx_tmp = fidx2tidx[fidx1];
		fidx2tidx[fidx1] = fidx2tidx[fidx2];
		fidx2tidx[fidx2] = tidx_tmp;
	}

}

class Furniture {
	int fid;// FROM 1
	int price;
	int req_tick, limit_tick;
	int wait_time, make_time;
	boolean done;

	// int furnitureNumber, int requestTime, int price, int makingTime, int
	// waitingTime
	public Furniture(int fid, int req_tick, int price, int make_time, int wait_time) {
		this.fid = fid;
		this.price = price;
		this.req_tick = req_tick;
		this.limit_tick = req_tick + wait_time;
		this.wait_time = wait_time;
		this.make_time = make_time;
	}

	public boolean compeletable(int currenttick) {
		if (limit_tick  >= currenttick + make_time)
			return true;
		return false;
	}

	public double priority(int currenttick) {
		return (double) price / (double) (make_time + limit_tick - currenttick);
	}

	public void make() {
		if (this.make_time == 0)
			return;

		this.make_time--;

		if (this.make_time == 0)
			this.done = true;
	}
}