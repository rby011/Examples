package cert.memo.d0428.d1list;

public class UserSolution {

	static MemoList mlist = null;
	static Memo mtable[] = null;
	static int N = 0;

	public static void init(int screensize) {
		N = screensize;
		mlist = new MemoList();
		mtable = new Memo[N * N];
	}

	public static void create(int mid, int y, int x, int height, int width, char str[]) {
		Memo memo = new Memo(mid, y, x, height, width, str);
		mtable[mid] = memo;
		mlist.addMemoToHead(memo);
	}

	public static void select(int mid) {
		Memo memo = mtable[mid];
		if (memo == null) {
			;// error!
			return;
		}
		mlist.moveToFirst(memo);
	}

	public static void select(int y, int x) {
		Memo memo = mlist.head;
		while (memo != null) {
			if (memo.contains(y, x)) {
				mlist.moveToFirst(memo);
				return;
			}
			memo = memo.next;
		}
	}

	public static void move(int mid, int y, int x) {
		Memo memo = mtable[mid];
		if (memo == null)
			return;// error!
		memo.y = y;
		memo.x = x;
		mlist.moveToFirst(memo);
	}

	public static void change(int mid, int height, int width, char str[]) {
		Memo memo = mtable[mid];
		if (memo == null)
			return;
		memo.height = height;
		memo.width = width;
		memo.copy(memo.str, str);
		mlist.moveToFirst(memo);
	}

	public static void getscreencontext(int y, int x, char res[][]) {
		for (int ry = y; ry < y + 5; ry++) {
			for (int rx = x; rx < x + 5; rx++) {
				res[ry][rx] = 0;
				Memo memo = mlist.head;
				while (memo != null) {
					if (memo.contains(ry, rx)) {
						int ich = memo.getchar(ry, rx);
						if (ich != -1)
							res[ry][rx] = (char) ich;
					}
					memo = memo.next;
				}
			}
		}
	}
}

class MemoList {
	Memo head;

	public void addMemoToHead(Memo memo) {
		if (head == null) {
			head = memo;
			return;
		}

		head.prev = memo;
		memo.next = head;
		memo.prev = null;
		head = memo;
	}

	public void moveToFirst(Memo memo) {
		if (memo == head) {
			return;
		}

		if (memo.next == null) {
			memo.prev.next = null;
			memo.next = head;
			head.prev = memo;
			head = memo;
			head.prev = null;
			return;
		}

		memo.prev.next = memo.next;
		memo.next.prev = memo.prev;
		memo.next = head;
		head.prev = memo;
		head = memo;
		head.prev = null;
	}
}

class Memo {
	int mid;
	int y, x;
	int height, width;
	char str[];

	Memo prev;
	Memo next;

	public Memo(int mid, int y, int x, int height, int width, char str[]) {
		this.mid = mid;
		this.y = y;
		this.x = x;
		this.height = height;
		this.width = width;

		this.str = new char[str.length];
		this.copy(this.str, str);
	}

	public int getchar(int y, int x) {
		if (!contains(y, x))
			return -1;

		int yy = y - this.y;
		int xx = x - this.y;

		int index = yy * this.width + xx;

		return this.str[index];
	}

	public boolean contains(int y, int x) {
		if (y >= this.y && y < this.y + this.height && x >= this.x && x < this.x + this.width)
			return true;
		return false;
	}

	public void copy(char dst[], char src[]) {
		int i = 0;
		while (src[i] != 0) {
			dst[i] = src[i];
			i += 1;
		}
		dst[i] = src[i];
	}
}