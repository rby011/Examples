package example.gameparty;

public class UserSolution {

	public static void init(int partycnt) {

	}

	public static void recoveryMember(byte[] members) {

	}

	public static void suspendMember(byte[] members) {

	}

	public static void closeParty(int pidx) {

	}

	public static void addParty(int pidx, int memberN, byte[][] members) {

	}

	public static int search(byte[][] members, int mode) {
		return 0;
	}

}

class MemberTable {
	int capacity;
	MemberList mtable[] = null;
}

class MemberList {
	Member head;

	public void addMemberToHead(Member member) {

	}
}

class Member {
	char member[];
	boolean issuspend;
	PartyIndexList pidlist = null;
	Member next;

}

class PartyIndexList {
	PartyIndex head;

	public void addPartyIndexToHead(PartyIndex pidx) {

	}
}

class PartyIndex {
	int pidx;
	PartyIndex next;

	PartyIndex(int pidx) {
		this.pidx = pidx;
	}
}
