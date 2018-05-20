package example.fashion;

public class UserCode {

	static final int HAT_IDX = 0, TOP_IDX = 1, PNT_IDX = 2, SHO_IDX = 3, ACC_IDX = 4;
	static final int CAPACITY = 10001;

	static Hashtable itemtable[] = null;
	static boolean checked[] = null;

	public static void init() {
		itemtable = new Hashtable[5];
		for (int i = 0; i < 5; i++)
			itemtable[i] = new Hashtable(CAPACITY);

		checked = new boolean[10000];
	}

	public static void addCatalog(Fashion fashion) {
		
		UFashion ufashion = new UFashion(fashion);
		
		// itemtable[HAT_IDX].put(fashion.hat, fashion);
		// itemtable[TOP_IDX].put(fashion.top, fashion);
		// itemtable[PNT_IDX].put(fashion.pants, fashion);
		// itemtable[SHO_IDX].put(fashion.shoes, fashion);
		// itemtable[ACC_IDX].put(fashion.accessory, fashion);
	}

	public static int newFashion(Fashion fashion) {
		return -1;
	}

	private static int toindex(char code[]) {

		return -1;
	}
}

class Hashtable {

	int capacity;
	UFashionList table[] = null;

	public Hashtable(int capacity) {
		this.table = new UFashionList[capacity];
		this.capacity = capacity;

		for (int i = 0; i < this.capacity; i++) {
			table[i] = new UFashionList();
		}
	}

	// KEY : fashion code composition
	public void put(Fashion fashion) {
		int idx = toindex(hashcode(fashion));
		table[idx].addFashionToTail(new UFashion(fashion));
	}

	// KEY : a fashion item code
	public void put(char item[], UFashion ufashion) {
		int idx = toindex(hashcode(item));
		table[idx].addFashionToTail(ufashion);
	}

	// THE LIST MAY CONTAINS COLLAPED NODE
	public UFashionList get(char item[]) {
		int idx = toindex(hashcode(item));
		return table[idx];
	}

	public Fashion get(Fashion fashion) {
		int idx = toindex(hashcode(fashion));
		UFashion ufnode = table[idx].head;
		while (ufnode != null) {
			if (ufnode.compare(ufnode.fashion)) {
				return ufnode.fashion;
			}
			ufnode = ufnode.next;
		}
		return null;
	}

	public int toindex(int hashcode) {
		return (hashcode & 0x7fffffff) % this.capacity;
	}

	public int hashcode(char item[]) {
		int hash = 1;
		for (int i = 0; i < item.length && item[i] != 0; i++) {
			hash = hash * 131 + item[i];
		}
		return hash;
	}

	public int hashcode(Fashion fashion) {
		int hash = 0;

		hash = hashcode(fashion.hat);
		hash = hash + hashcode(fashion.top);
		hash = hash + hashcode(fashion.pants);
		hash = hash + hashcode(fashion.accessory);
		hash = hash + hashcode(fashion.shoes);

		return hash;
	}
}

class UFashionList {
	UFashion head;

	public void addFashionToTail(UFashion ufashion) {
		if (head == null) {
			this.head = ufashion;
			return;
		}

		ufashion.next = head;
		head = ufashion;
	}
}

class UFashion {
	static int ID_SEED = 0;

	int fashion_id = 0;
	Fashion fashion;

	UFashion next;

	// FOR BIT OPERATION
	int uhat = 0;
	int utop = 0;
	int upants = 0;
	int uaccessesory = 0;
	int ushoes = 0;

	UFashion(Fashion fashion) {
		this.fashion = fashion;
		this.fashion_id = ID_SEED++;
	}

	// NEED TO OPTIMZIE WITH BIT OPERATION
	public boolean compare(Fashion ofashion) {
		if (compare(fashion.accessory, ofashion.accessory)) {
			if (compare(fashion.hat, ofashion.hat)) {
				if (compare(fashion.pants, ofashion.pants)) {
					if (compare(fashion.shoes, ofashion.shoes)) {
						if (compare(fashion.top, ofashion.top)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// NEED TO OPTIMZIE WITH BIT OPERATION
	private boolean compare(char code1[], char code2[]) {
		for (int i = 0; i < code1.length; i++) {
			if (code1[i] != code2[i])
				return false;
		}
		return true;
	}

}