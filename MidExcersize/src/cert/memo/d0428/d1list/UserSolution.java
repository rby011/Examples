package cert.memo.d0428.d1list;

import java.util.Scanner;

/*<PRE>
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

</PRE> */
public class UserSolution {

	public static void main(String args[]) {
		UserSolution.init(10);

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

			UserSolution.create(mid, y, x, height, width, str);
		}

		// UserSolution.move(3, 2, 5);
		// UserSolution.move(10, 6, 6);
		// UserSolution.move(9, 6, 5);
		// UserSolution.move(5, 5, 5);
		// UserSolution.select(3);
		// UserSolution.move(3, 3, 3);

		char res[][] = new char[5][5];
		print(UserSolution.mlist);
		UserSolution.getscreencontext(2, 3, res);
		print(res);
		// UserSolution.getscreencontext(1, 1, null);

		// UserSolution.select(9);
		// char nstr[] = { '#', '#', '\0' };
		// UserSolution.change(3, 2, 5, nstr);
		// UserSolution.getscreencontext(2, 2, null);

		scan.close();
	}

	public static void print(char res[][]) {
		for (int y = 0; y < res.length; y++) {
			for (int x = 0; x < res.length; x++) {
				System.out.print(res[y][x] + "\t");
			}
			System.out.println();
		}
	}

	public static void print(MemoList mlist) {
		Memo memo = mlist.head;
		while (memo != null) {
			System.out.println(memo.mid + " " + memo.str[0]);
			memo = memo.next;
		}
	}

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
				res[ry - y][rx - x] = 0;
				Memo memo = mlist.head;
				while (memo != null) {
					if (memo.contains(ry, rx)) {
						int ich = memo.getchar(ry, rx);
						if (ich != -1) {
							res[ry - y][rx - x] = (char) ich;
							break;
						}
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
		int xx = x - this.x;

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