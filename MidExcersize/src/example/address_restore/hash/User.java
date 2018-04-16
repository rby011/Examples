package example.address_restore.hash;

public class User {

	static final int ALLOWED_CODE_COUNT = 32;
	static final int MIN_CHAR = 33, MAX_CHAR = 126;
	static boolean err_map[][] = null;
	static boolean alw_code[] = null;

	static AddressTable addr_table = null;

	// 이번 TC 에서 허용된 문자 목록
	static void getAllowCode(char code[]) {
		// ALLOWED LETTER(CODE) INDEX TABLE
		alw_code = new boolean[MAX_CHAR + 1];
		for (int i = 0; i < ALLOWED_CODE_COUNT; i++)
			alw_code[code[i]] = true;

		// POSSIBLE EREOR CASE GRAPH
		initializeErrMap();
	}

	// raw 축은 error code 이고, column 축은 possible restored code
	static void initializeErrMap() {
		err_map = new boolean[MAX_CHAR + 1][MAX_CHAR + 1];
		for (int ecode = MIN_CHAR; ecode <= MAX_CHAR; ecode++) {
			for (int pcode = MIN_CHAR; alw_code[pcode] && pcode <= MAX_CHAR; pcode++) {
				int diff = ecode - pcode;
				if (diff < 0)
					diff = diff * -1;
				if (diff == 1 || diff == 2 || diff == 4 || diff == 8 || diff == 16 || diff == 32 || diff == 64
						|| diff == 128)
					err_map[ecode][pcode] = true;
			}
		}
	}

	// 이번 TC 에서 유효한 것으로 정의된 주소 목록
	static void getCode(int n, char code[][]) {
		addr_table = new AddressTable(20000 + 13);
		for (int i = 0; i < n; i++)
			addr_table.put(code[i]);
	}

	static void restore(char errcode[], char prediction[]) {
		try_restore(errcode, prediction, 0);
	}

	static boolean try_restore(char errcode[], char predcode[], int depth) {
		if (depth == 6) {
			if (addr_table.get(predcode) != null)
				return true;
			else
				return false;
		}

		char ec = errcode[depth];
		for (int i = MIN_CHAR; i <= MAX_CHAR; i++) {
			if (err_map[ec][i]) {
				predcode[depth] = (char) i;
				boolean ret = try_restore(errcode, predcode, depth + 1);
				if (ret)
					return true;
			}
		}

		return false;
	}

}

class AddressTable {
	int capacity;
	AddressList table[] = null;

	public AddressTable(int capacity) {
		this.table = new AddressList[capacity];
		for (int i = 0; i < capacity; i++)
			this.table[i] = new AddressList();
		this.capacity = capacity;
	}

	public void put(char code[]) {
		int index = toindex(code);
		table[index].addAddressToHead(new Address(code));
	}

	public Address get(char code[]) {
		int index = toindex(code);
		Address addr = table[index].head;
		while (addr != null) {
			if (addr.eqaulto(code))
				return addr;
			addr = addr.next;
		}
		return null;
	}

	private int toindex(char code[]) {
		return (hashcode(code) & 0x7fffffff) % this.capacity;
	}

	private int hashcode(char code[]) {
		int hash = 1;
		for (int i = 0; i < code.length; i++) {
			hash = hash * 131313 + code[i];
		}
		return hash;
	}
}

class AddressList {
	Address head;

	public void addAddressToHead(Address addr) {
		if (head == null) {
			this.head = addr;
			return;
		}

		addr.next = head;
		head = addr;
	}
}

class Address {
	char address[] = null;
	Address next;

	public Address(char address[]) {
		this.address = address;
	}

	public boolean eqaulto(char address[]) {
		for (int i = 0; i < this.address.length && this.address[i] != 0 && address[i] != 0; i++) {
			if (this.address[i] != address[i])
				return false;
		}
		return true;
	}
}
