package certi.gameparty;

public class UserSolution {
	static int tc = -1;
	static long times[][] = new long[30][4];
	static int counts[][] = new int[30][4];

	static final int MAX_N_PARTY = 50000, MAX_PARTY_CAPACITY = 50891;
	static final int MAX_N_MEMBER = 10000, MAX_MEMBER_CAPACITY = 10141;

	static final int PARTY_OPEN = 0, PARTY_SUSPENDED = 1, PARTY_CLOSED = 2;

	static final boolean MEMBER_NORMAL = false, MEMBER_SUSPENDED = true;
	static int N_PARTY = 0;

	static boolean midxStatus[] = null;
	static char midxTable[][] = null;
	static Hashset midxHTable = null;

	static int pidxStatus[] = null;
	static Hashtable pidxHTable = null;
	static Party pidxTable[] = null;

	public static void init(int pn) {
		tc++;

		N_PARTY = pn;
		midxStatus = new boolean[MAX_N_MEMBER];
		midxTable = new char[MAX_N_MEMBER][];
		midxHTable = new Hashset(MAX_MEMBER_CAPACITY, midxTable);

		pidxStatus = new int[MAX_N_PARTY];
		pidxHTable = new Hashtable(MAX_PARTY_CAPACITY);
		pidxTable = new Party[MAX_N_PARTY];

		// System.out.println("START ID : " + pidxHTable.MEMBER_ID);
	}

	public static void addParty(int index, int memberN, char[][] mids) {
		long stime = System.nanoTime();

		// for (int i = 0; i < mids.length; i++) {
		// for (int j = 0; j < mids[i].length; j++) {
		// if (!((mids[i][j] >= 'a' && mids[i][j] <= 'z') || (mids[i][j] >='0' &&
		// mids[i][j] <='9')))
		// mids[i][j] = ' ';
		// }
		// }
		//
		// System.out.print("ADD-PARTY\t" + index + "\t" + memberN + "\t");
		// for (int i = 0; i < mids.length; i++)
		// System.out.print(mids[i]);
		// System.out.println();

		Party party = new Party(index, memberN, mids);
		// ADD NEW PARTY TO PARTY TABLE
		pidxTable[index] = party;

		// FOR EACH MEMBER OF PARTY
		for (int m = 0; m < memberN; m++) {
			Index pidx = new Index(index);

			// int idx = pidxHTable.toindex(pidxHTable.hashcode(mids[m]));

			// ADD PARTY INDEX TO HASHTABLE AND GET THIS MEMBER INDEX
			pidxHTable.put(mids[m], pidx);

			// ADD MEMBER INTO MEMBER INDEX TABLE
			int midx = midxHTable.get(mids[m]);
			if (midx == -1) {
				midx = midxHTable.put(mids[m]);
				midxTable[midx] = mids[m];
			}

			// SETTING STATUS OF THIS PARTY DEPNDING ON THE MEMBER STATUS
			if (midxStatus[midx] == MEMBER_SUSPENDED)
				pidxStatus[index] = PARTY_SUSPENDED;
		}

		long time = System.nanoTime() - stime;

		times[tc][0] += time;
		counts[tc][0]++;
	}

	public static void closeParty(int pid) {
		// System.out.println("CLOSE-PARTY\t" + pid);

		pidxStatus[pid] = PARTY_CLOSED;
	}

	public static void suspendMember(char[] mid) {
		long start = System.nanoTime();

		// for (int i = 0; i < mid.length; i++)
		// if (!((mid[i] >= 'a' && mid[i] <= 'z')|| (mid[i] >= '0' && mid[i] <= '9')))
		// mid[i] = ' ';

		// System.out.println("SUSPEND-MEMBER\t" + new String(mid));

		// 아직 파티에 소속되지 않는 멤버도 suspend 시킬 수 있음, 신규 멤버로 등록 필요
		int imidx = midxHTable.get(mid);
		if (imidx == -1) {
			imidx = midxHTable.put(mid);
			midxTable[imidx] = mid;
			midxStatus[imidx] = MEMBER_SUSPENDED;
			return;
		}

		PartyIndexList pidxlist = pidxHTable.get(mid);

		if (pidxlist == null) {
			return;
		}

		// SETTING STATUS OF THE MEMBER
		midxStatus[imidx] = MEMBER_SUSPENDED;

		// SETTING STATUS OF ALL PARTY THAT THE MEMBER JOINS
		Index pidx = pidxlist.head;
		while (pidx != null) {
			if (pidxStatus[pidx.index] != PARTY_CLOSED)
				pidxStatus[pidx.index] = PARTY_SUSPENDED;
			pidx = pidx.next;
		}

		long time = System.nanoTime() - start;

		times[tc][1] += time;
		counts[tc][1]++;
	}

	public static void recoveryMember(char[] mid) {
		long start = System.nanoTime();

		// for (int i = 0; i < mid.length; i++)
		// if (!((mid[i] >= 'a' && mid[i] <= 'z')|| (mid[i] >= '0' && mid[i] <= '9')))
		// mid[i] = ' ';
		// System.out.println("RECOVERY-MEMBER\t" + new String(mid));

		int midx = midxHTable.get(mid);
		if (midx == -1) {
			System.out.println("ERROR!!!");
			return;
		}

		// SETTING STATUS OF THE MEMBER
		midxStatus[midx] = MEMBER_NORMAL;

		PartyIndexList pidxlist = pidxHTable.get(mid);
		if (pidxlist == null)
			return;

		// SETTING STATUS OF ALL PARTY THAT THE MEMBER JOINS
		// !!! NEED TO CHCHEK MEMBERS ARE SUSPENDED!!!! IF ONE OF THEM IS SUSPENDED,
		// CANNOT OPEN
		Index pidx = pidxlist.head;
		while (pidx != null) {
			Party party = pidxTable[pidx.index];

			boolean suspended = false;
			for (int m = 0; m < party.nMember; m++) {

				midx = midxHTable.get(party.mids[m])/* pidxHTable.get(party.mids[m]).kmidx */;

				if (midxStatus[midx] == MEMBER_SUSPENDED) {
					if (pidxStatus[pidx.index] != PARTY_CLOSED) {
						pidxStatus[pidx.index] = PARTY_SUSPENDED;
						suspended = true;
					}
					break;
				}
			}
			if (!suspended && pidxStatus[pidx.index] != PARTY_CLOSED)
				pidxStatus[pidx.index] = PARTY_OPEN;

			pidx = pidx.next;
		}

		long time = System.nanoTime() - start;

		times[tc][2] += time;
		counts[tc][2]++;
	}

	public static int search(char[][] mids, int mode) {
		long start = System.nanoTime();

		// for (int i = 0; i < mids.length; i++) {
		// for (int j = 0; j < mids[i].length; j++) {
		// if (!((mids[i][j] >= 'a' && mids[i][j] <= 'z') || (mids[i][j] >='0' &&
		// mids[i][j] <='9')))
		// mids[i][j] = ' ';
		// }
		// }
		// System.out.print("SEARCH\t" + mode + "\t");
		// if (mode == 0)
		// System.out.println(mids[0]);
		// else
		// System.out.println(new String(mids[0]) + "\t" + new String(mids[1]));

		int nparty = 0;

		if (mode == 0) {
			char mid[] = mids[0];

			PartyIndexList pidxlist = pidxHTable.get(mid);
			if (pidxlist == null)
				return 0;

			nparty = countPartyInList(pidxlist);

			return nparty;
		}

		// AND/OR SEARCH, BUT ONE OF TWO IS NULL
		PartyIndexList pidxlist0 = pidxHTable.get(mids[0]);
		PartyIndexList pidxlist1 = pidxHTable.get(mids[1]);

		PartyIndexList pidxlist = null;
		if (pidxlist0 == null)
			pidxlist = pidxlist1;
		if (pidxlist1 == null)
			pidxlist = pidxlist0;
		if (pidxlist != null) {
			if (mode == 2) {// OR
				nparty = countPartyInList(pidxlist);
				return nparty;
			} else {// AND
				return nparty;
			}
		}

		// AND SEARCH
		int common = countCommonPartyInLists(pidxlist0, pidxlist1);
		if (mode == 1)
			nparty = common;

		// OR SEARCH
		if (mode == 2) {
			int nparty0 = countPartyInList(pidxlist0);
			int nparty1 = countPartyInList(pidxlist1);

			nparty = nparty0 + nparty1 - common;
		}

		long time = System.nanoTime() - start;

		times[tc][3] += time;
		counts[tc][3]++;

		return nparty;
	}

	public static int countPartyInList(PartyIndexList pidxlist) {
		int nparty = 0;

		if (pidxlist == null)
			return 0;

		Index pidx = pidxlist.head;
		while (pidx != null) {
			int ipidx = pidx.index;
			if (pidxStatus[ipidx] == PARTY_SUSPENDED || pidxStatus[ipidx] == PARTY_CLOSED) {
				pidx = pidx.next;
				continue;
			}
			nparty++;
			pidx = pidx.next;
		}

		return nparty;

	}

	public static int countCommonPartyInLists(PartyIndexList pidxlist0, PartyIndexList pidxlist1) {
		if (pidxlist0 == null || pidxlist1 == null)
			return 0;

		Index pidx0 = pidxlist0.head;
		Index pidx1 = pidxlist1.head;

		if (pidx0 == null || pidx1 == null)
			return 0;

		int nparty = 0;
		Index pidx_base = pidxlist0.head;
		Index pidx_compare = pidxlist1.head;

		if (pidxlist0.nparty > pidxlist1.nparty) {
			pidx_base = pidxlist1.head;
			pidx_compare = pidxlist0.head;
		}

		Index pidx_start = pidx_compare;

		while (pidx_base != null) {
			int ipidx = pidx_base.index;
			boolean found = false;

			if (pidx_compare != null && ipidx < pidx_compare.index) {
				pidx_base = pidx_base.next;
				continue;
			}

			if (pidxStatus[ipidx] == PARTY_SUSPENDED || pidxStatus[ipidx] == PARTY_CLOSED) {
				pidx_base = pidx_base.next;
				continue;
			}

			while (pidx_compare != null) {
				if (pidx_compare.index == ipidx && pidxStatus[pidx_compare.index] == PARTY_OPEN) {
					found = true;
					pidx_compare = pidx_compare.next;
					break;
				}

				if (pidx_compare.index > ipidx)
					break;

				pidx_compare = pidx_compare.next;
			}
			if (found)
				nparty++;
			else
				pidx_compare = pidx_start;

			pidx_base = pidx_base.next;
		}

		return nparty;
	}

	// public static void main(String args[]) {
	// Hashtable.test();
	// }
}

class Hashset {
	static int putcall = 0;
	static int collision = 0;

	int MEMBER_ID = 0;

	MemberIndexList table[] = null;
	int capacity = 0;
	char midxTable[][] = null;

	public Hashset(int capacity, char midxTable[][]) {
		this.table = new MemberIndexList[capacity];
		this.capacity = capacity;
		this.midxTable = midxTable;
	}

	public int put(char mid[]) {
		int idx = toindex(hashcode(mid));

		if (table[idx] == null)
			table[idx] = new MemberIndexList();

		table[idx].addMemberIndexToHead(new Index(MEMBER_ID));

		return MEMBER_ID++;
	}

	public int put(int idx) {
		putcall++;

		if (table[idx] == null) {
			table[idx] = new MemberIndexList();
			table[idx].addMemberIndexToHead(new Index(MEMBER_ID));
			return MEMBER_ID++;
		}

		collision++;

		table[idx].addMemberIndexToHead(new Index(MEMBER_ID));

		return MEMBER_ID++;
	}

	public int get(char mid[]) {
		int idx = toindex(hashcode(mid));

		if (table[idx] == null)
			return -1;

		Index node = table[idx].head;

		while (node != null) {
			if (this.midxTable[node.index] != null && compare(this.midxTable[node.index], mid)) {
				return node.index;
			}
			node = node.next;
		}

		return -1;
	}

	public int get(char mid[], int idx) {
		if (table[idx] == null)
			return -1;

		Index node = table[idx].head;

		while (node != null) {
			if (this.midxTable[node.index] != null && compare(this.midxTable[node.index], mid)) {
				return node.index;
			}
			node = node.next;
		}

		return -1;
	}

	public int toindex(int hashcode) {
		return (hashcode & 0x7fffffff) % this.capacity;
	}

	public int hashcode(char str[]) {
		int hash = 1;
		for (int i = 0; i < str.length; i++) {
			if ((str[i] >= 'a' && str[i] <= 'z') || (str[i] >= '0' && str[i] <= '9'))
				hash = (hash << 5) + str[i] * ((i + 1) * (i + 1) + (i + 1) + 41);
		}
		return hash;
	}

	private boolean compare(char str1[], char str2[]) {
		for (int i = 0; i < str1.length; i++) {
			if (str1[i] != str2[i])
				return false;
		}
		return true;
	}
}

class Hashtable {
	static int putcall = 0;
	static int collision = 0;
	static long overhead = 0;

	int MEMBER_ID = 0;

	int capacity = 0;

	PartyIndexList table[] = null;

	public Hashtable(int capacity) {
		this.table = new PartyIndexList[capacity];
		this.capacity = capacity;
	}

	public void put(char mid[], Index pidx) {
		putcall++;

		int idx = toindex(hashcode(mid));

		if (table[idx] == null) {
			table[idx] = new PartyIndexList(mid, MEMBER_ID);
			table[idx].headlist = table[idx];
			table[idx].addPartyIndex(pidx);
			return /* MEMBER_ID++ */;
		}

		// ADD ANOTHER PARTY FOR THE MEMBER-ID
		if (compare(table[idx].kmid, mid)) {
			table[idx].addPartyIndex(pidx);
			return /* MEMBER_ID++ */;
		}

		collision++;

		// TRY TO FIND 'PartyList' THAT HAS 'mid' AS KEY
		PartyIndexList plist = table[idx].headlist;
		PartyIndexList flist = null;
		while (plist != null) {
			if (compare(plist.kmid, mid)) {
				plist.addPartyIndex(pidx);
				return /* plist.kmidx */;
			}
			flist = plist;
			plist = plist.nextlist;
		}

		// AT COLLISION, NOT FOUND 'PartyList', NEED TO ADD NEW LIST
		flist.nextlist = new PartyIndexList(mid, MEMBER_ID);
		flist.nextlist.addPartyIndex(pidx);

		return /* MEMBER_ID++ */;
	}

	public void put(int idx, char mid[], Index pidx) {
		putcall++;

		if (table[idx] == null) {
			table[idx] = new PartyIndexList(mid, MEMBER_ID);
			table[idx].headlist = table[idx];
			table[idx].addPartyIndex(pidx);
			return /* MEMBER_ID++ */;
		}

		// ADD ANOTHER PARTY FOR THE MEMBER-ID
		if (compare(table[idx].kmid, mid)) {
			table[idx].addPartyIndex(pidx);
			return /* MEMBER_ID++ */;
		}

		collision++;

		long stime = System.nanoTime();

		// TRY TO FIND 'PartyList' THAT HAS 'mid' AS KEY
		PartyIndexList plist = table[idx].headlist;
		PartyIndexList flist = null;
		while (plist != null) {
			if (compare(plist.kmid, mid)) {
				plist.addPartyIndex(pidx);
				return /* plist.kmidx */;
			}
			flist = plist;
			plist = plist.nextlist;
		}

		// AT COLLISION, NOT FOUND 'PartyList', NEED TO ADD NEW LIST
		flist.nextlist = new PartyIndexList(mid, MEMBER_ID);
		flist.nextlist.addPartyIndex(pidx);

		long etime = System.nanoTime() - stime;

		overhead = overhead + etime;

		return /* MEMBER_ID++ */;
	}

	public PartyIndexList get(char mid[]) {
		int idx = toindex(hashcode(mid));

		if (table[idx] == null)
			return null;

		PartyIndexList plist = table[idx].headlist;
		while (plist != null) {
			if (compare(mid, plist.kmid))
				return plist;
			plist = plist.nextlist;
		}

		return null;
	}

	public PartyIndexList get(int idx, char mid[]) {
		if (table[idx] == null)
			return null;

		PartyIndexList plist = table[idx].headlist;
		while (plist != null) {
			if (compare(mid, plist.kmid))
				return plist;
			plist = plist.nextlist;
		}

		return null;
	}

	public int toindex(int hashcode) {
		return (hashcode & 0x7fffffff) % this.capacity;
	}

	public int hashcode(char str[]) {
		int hash = 1;
		for (int i = 0; i < str.length; i++) {
			if ((str[i] >= 'a' && str[i] <= 'z') || (str[i] >= '0' && str[i] <= '9'))
				hash = (hash << 5) + str[i] * ((i + 1) * (i + 1) + (i + 1) + 41);

		}
		return hash;
	}

	private boolean compare(char str1[], char str2[]) {
		for (int i = 0; i < str1.length; i++) {
			if (str1[i] != str2[i])
				return false;
		}
		return true;
	}

	static class TestHahtable extends Hashtable {
		public TestHahtable(int capacity) {
			super(capacity);
		}

		public int hashcode(char str[]) {
			return str[0] - 'a' + str[1] - 'a';
		}
	}

	public static void test() {

		TestHahtable htable = new TestHahtable(100);

		char members[][][] = { { { 'a', 'b' } }, { { 'a', 'c' } }, { { 'b', 'a' } }, { { 'c', 'a' } } };

		Party ab = new Party(0, 1, members[0]);
		Party ac = new Party(1, 1, members[1]);
		Party ba = new Party(2, 1, members[2]);
		Party ca = new Party(3, 1, members[3]);

		// htable.put(members[0][0], ab);
		// htable.put(members[1][0], ac);
		// htable.put(members[2][0], ba);
		// htable.put(members[3][0], ca);

		char c_ab[] = htable.get(members[0][0]).kmid;
		char c_ac[] = htable.get(members[1][0]).kmid;
		char c_ba[] = htable.get(members[2][0]).kmid;
		char c_ca[] = htable.get(members[3][0]).kmid;

		System.out.println(c_ab);
		System.out.println(c_ac);
		System.out.println(c_ba);
		System.out.println(c_ca);
	}
}

class MemberIndexList {
	Index head;

	public void addMemberIndexToHead(Index mindex) {
		if (head == null) {
			this.head = mindex;
			return;
		}

		mindex.next = this.head;
		this.head = mindex;
	}
}

class PartyIndexList {
	// USE THIS FOR SAME MEMBER
	Index head;
	Index tail;
	int nparty = 0;

	// USE THIS AT COLLISION
	PartyIndexList headlist;
	PartyIndexList nextlist;

	char kmid[] = null;
	// int kmidx = 0;

	public PartyIndexList(char kmid[], int kmidx) {
		this.kmid = kmid;
		// this.kmidx = kmidx;
	}

	public void addPartyIndex(Index pidx) {
		nparty++;

		if (this.head == null && this.tail == null) {
			this.head = this.tail = pidx;
			return;
		}

		this.tail.next = pidx;
		this.tail = pidx;
	}

	public void removePartyIndex(int ipidx) {
		if (head == null & tail == null)
			return;

		nparty--;

		if (head.index == ipidx) {
			if (head == tail) {
				head = tail = null;
				return;
			}
			Index pidx = head.next;
			head = pidx;
			return;
		}

		Index pidx = head;
		Index fidx = null;
		while (pidx != null) {
			if (pidx.index == ipidx) {
				break;
			}
			fidx = pidx;
			pidx = pidx.next;
		}
		if (fidx != null) {
			if (pidx != null) {
				fidx.next = pidx.next;
			} else {
				// NOTHING TO DELETE
			}
		} else {
			// THE NODE TO DELET IS HEAD
		}

	}
}

class Index {
	int index;
	Index next;

	Index(int index) {
		this.index = index;
	}
}

class Party {
	int index;
	int nMember;
	char mids[][] = null;

	Party next;

	Party(int index, int nMember, char mids[][]) {
		this.index = index;
		this.nMember = nMember;
		this.mids = mids;
	}

}
