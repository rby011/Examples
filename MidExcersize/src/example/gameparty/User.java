package example.gameparty;

public class User {
	// static final int MAX_PARTIES = 50000;
	// static final int MAX_MEMBERS = 10000;

	static Party parties[] = null;
	static PartyTable ptable = null;

	// n : maximum # of parties
	static void init(int n) {
		parties = new Party[n];
		ptable = new PartyTable(n + 121);// n 보다 큰 소수
	}

	// index : party index
	// m : # of members
	// members : member's game id, members[4][11]
	static void addParty(int index, int m, char members[][]) {
		Party temp = new Party(index, m, members);
		parties[index] = temp;
		for (int i = 0; i < m; i++) {
			ptable.put(members[i], temp);
		}
	}

	// index : party index
	static void closeParty(int index) {
		parties[index].closed = true;
	}

	// member : member's game id
	static void suspendMember(char member[]) {
		PartyList plist = ptable.get(member);
		Party party = plist.head_party;

		while (party != null) {
			party.suspended = true;
			party = party.next;
		}
	}

	// member : member's game id
	static void recoveryMember(char member[]) {
		PartyList plist = ptable.get(member);

		Party party = plist.head_party;
		while (party != null) {
			party.suspended = false;
			party = party.next;
		}

	}

	// members : at maximum, two member's game id
	// mode
	// - 0 : return # of parties that are not suspended and includes the given game
	// id
	// - 1 : return # of parties that are not suspended and includes the two given
	// game ids
	// - 2 : return # of parties that are not suspended and includes the first given
	// game id or the second game id
	static int search(char members[][], int mode) {
		if (mode == 0) {
			PartyList plist = ptable.get(members[0]);
			return plist.numparties;
		}

		if (mode == 1) {
			PartyList plist0 = ptable.get(members[0]);
			PartyList plist1 = ptable.get(members[1]);

			Party party = plist1.head_party;
			char member[] = members[0];

			if (plist0.numparties > plist1.numparties) {
				party = plist0.head_party;
				member = members[1];
			}
			
		}
		return 0;
	}
}

// KEY : member id
// VALUE : party index, Party
class PartyTable {
	int capacity;
	PartyList table[] = null;

	public PartyTable(int capacity) {
		this.capacity = capacity;
	}

	public void put(char mid[], Party party) {
		// member 가 다르면 partylist 를 추가해야 하고 member 가 같은 것이라면 해당 list 에 party 를 추가해야 함
		int index = toindex(mid);
		PartyList plist = table[index];
		PartyList olist = plist;

		while (plist != null) {
			if (compare(plist.member, mid)) {
				plist.addPartyToHead(party);
				return;
			}
			plist = plist.next_list;
		}

		olist.addPartyToNewPartyListTAtHead(party);
	}

	public PartyList get(char mid[]) {
		int index = toindex(mid);
		PartyList plist = table[index];
		while (plist != null) {
			if (compare(plist.member, mid)) {
				return plist;
			}
		}
		return null;
	}

	public void remove(char mid[]) {

	}

	public int toindex(char mid[]) {
		return 0;
	}

	public boolean compare(char str1[], char str2[]) {
		return false;
	}

}

class PartyList {
	PartyList head_list;
	PartyList next_list;

	Party head_party;
	int numparties;

	char member[] = null;

	boolean suspended = false;

	// member 가 다르면 partylist 를 추가해야 하고 member 가 같은 것이라면 해당 list 에 party 를 추가해야 함
	public void addPartyToNewPartyListTAtHead(Party party) {
		PartyList nlist = new PartyList();
		nlist.addPartyToHead(party);

		if (head_list == null) {
			head_list = nlist;
			return;
		}

		nlist.next_list = head_list;
		head_list = nlist;
	}

	public void addPartyToHead(Party party) {
		this.numparties++;
		if (head_party == null) {
			head_party = party;
			return;
		}
		party.next = this.head_party;
		this.head_party = party;
	}
}

class Party {

	int index = 0;
	int m = 0;
	char members[][] = null;
	boolean suspended = false;
	boolean closed = false;

	Party next;// for handling duplicated key (동일 member id 로 여러 party 에 가입되어 있을 수 있음)

	public Party(int index, int m, char members[][]) {
		this.index = index;
		this.m = m;
		this.members = members;
	}

}
