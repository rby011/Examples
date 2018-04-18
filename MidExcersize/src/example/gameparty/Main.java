package example.gameparty;

import java.util.Scanner;

public class Main {
	static boolean FULL_CHECK = false;

	static final int MAX_PARTIES = 50000;
	static final int MAX_MEMBERS = 10000;
	static final int MAX_COMMANDS = 100000;

	static final int CLOSE_RATES = 5;
	static final int SUPSPEND_RATES = 10;
	static final int RECOVERY_RATES = 15;
	static final int ADD_RATES = 60;

	static int N, M;
	static char members[][] = new char[MAX_MEMBERS][11];

	static long seed = 12345;

	static long pseudo_rand(int max) {
		seed = ((seed * 1103515245) + 12345) & 0xFFFF;
		return seed % max;
	}

	static int checkId[] = new int[131071];

	static Scanner scan = null;

	static long getIndex(char str[]) {
		long num = 5381;
		int c = 0, idx = 0;
		while ((c = str[idx++]) != 0) {
			num = (((num << 5) + num) + c) % 131071;
		}
		return num % 131071;
	}

	static void makeMembers() {
		for (int i = 0; i < MAX_MEMBERS; ++i) {
			int len = (int) pseudo_rand(6) + 4;
			for (int j = 0; j < len; j++) {
				members[i][j] = (char) (pseudo_rand(26) + 'a');
			}

			members[i][len] = 0;
			while (checkId[(int) getIndex(members[i])] != 0) {
				members[i][(int) pseudo_rand(len)] = (char) (pseudo_rand(10) + '0');
			}

			checkId[(int) getIndex(members[i])] = 1;
		}
	}

	static int partyCnt = 0;
	static boolean partyStatus[] = new boolean[MAX_PARTIES];
	static int memberStatus[] = new int[MAX_MEMBERS];
	static int suspendStatus[] = new int[MAX_MEMBERS];

	static final int OFF = 0, ON = 1;

	static void addParty() throws Exception {
		int memberN = (int) (pseudo_rand(4) + 1);
		int maxN = N < MAX_MEMBERS ? N : MAX_MEMBERS;

		char temp[][] = new char[4][11];
		for (int i = 0; i < memberN; ++i) {
			boolean sameChk = true;
			int randIdx = (int) pseudo_rand(maxN);

			while (sameChk) {
				sameChk = false;
				for (int j = 0; j < i; j++) {
					if (strcmp(members[randIdx], temp[j])) {
						sameChk = true;
						++randIdx;
						if (randIdx >= maxN)
							randIdx = 0;
						break;
					}
				}
			}

			strcpy(temp[i], members[randIdx]);
			memberStatus[randIdx] = ON;
		}

		User.addParty(partyCnt, memberN, temp);
		partyStatus[partyCnt++] = true;
	}

	static int search() throws Exception {
		int mode = (int) pseudo_rand(3);
		char temp[][] = new char[2][11];

		int maxN = N < MAX_MEMBERS ? N : MAX_MEMBERS;
		int randIdx = (int) pseudo_rand(maxN);

		while (suspendStatus[randIdx] == ON) {
			++randIdx;
			if (randIdx >= maxN)
				randIdx = 0;
		}

		strcpy(temp[0], members[randIdx]);

		if (mode >= 1) {
			randIdx = (int) pseudo_rand(maxN);

			while (suspendStatus[randIdx] == ON || !strcmp(members[randIdx], temp[0])) {
				++randIdx;
				if (randIdx >= maxN)
					randIdx = 0;
			}

			strcpy(temp[1], members[randIdx]);
		}

		return User.search(temp, mode);
	}

	static int run() throws Exception {
		int accepted = 0;
		int searchCnt = 0;
		int suspendCnt = 0;
		int closeCnt = 0;
		partyCnt = 0;
		int maxN = N < MAX_MEMBERS ? N : MAX_MEMBERS;

		int startN = (int) pseudo_rand(N);
		if (startN < 4)
			startN = 4;
		for (int i = 0; i < startN; i++)
			addParty();

		for (int i = 0; i < M - startN; i++) {
			int command = (int) pseudo_rand(100);
			char temp[] = new char[11];
			int randIdx = 0;
			if (command < CLOSE_RATES && closeCnt < partyCnt / 5) {
				randIdx = (int) pseudo_rand(partyCnt);
				while (partyStatus[randIdx]) {
					++randIdx;
					if (randIdx >= partyCnt)
						randIdx = 0;
				}
				User.closeParty(randIdx);
				partyStatus[randIdx] = false;
				++closeCnt;
			} else if (command < SUPSPEND_RATES && suspendCnt < partyCnt / 3) {
				randIdx = (int) pseudo_rand(maxN);
				while (suspendStatus[randIdx] == ON) {
					++randIdx;
					if (randIdx >= maxN)
						randIdx = 0;
				}
				strcpy(temp, members[randIdx]);
				User.suspendMember(temp);
				suspendStatus[randIdx] = ON;
				++suspendCnt;
			} else if (command < RECOVERY_RATES && suspendCnt > 0) {
				randIdx = (int) pseudo_rand(maxN);
				while (suspendStatus[randIdx] == OFF) {
					++randIdx;
					if (randIdx >= maxN)
						randIdx = 0;
				}
				strcpy(temp, members[randIdx]);
				User.recoveryMember(temp);
				suspendStatus[randIdx] = OFF;
				--suspendCnt;
			} else if (command < ADD_RATES && partyCnt < N) {
				addParty();
			} else {
				int answer = search();
				if (FULL_CHECK || searchCnt < 10) {
					int correct = 0;
					correct = scan.nextInt();
					if (correct == answer)
						++accepted;
					accepted = accepted + answer;
					++searchCnt;
				}
			}
		}
		return accepted;
	}

	public static void main(String args[]) throws Exception {
		int T = 0;
		int totalScore = 0;
		scan = new Scanner(System.in);

		T = scan.nextInt();

		makeMembers();

		for (int tc = 1; tc <= T; ++tc) {
			for (int i = 0; i < MAX_MEMBERS; ++i) {
				memberStatus[i] = OFF;
				suspendStatus[i] = OFF;
			}

			seed = scan.nextLong();
			N = scan.nextInt();
			M = scan.nextInt();

			User.init(N);

			int answer = run();
			int correct = scan.nextInt();
			if (answer == correct) {
				System.out.println("# " + tc + " 100");
				totalScore = totalScore + 100;
			} else {
				System.out.println("# " + tc + " 0");
			}

			System.out.println("Total Socre = " + totalScore / T);
		}
	}

	static void strcpy(char dest[], char src[]) throws Exception {
		if (dest.length != src.length)
			throw new Exception("String Copy Length");

		for (int i = 0; i < dest.length; i++) {
			dest[i] = src[i];
		}
	}

	static boolean strcmp(char str1[], char str2[]) throws Exception {
		if (str1.length != str2.length)
			throw new Exception("String Compare Length");

		int idx = 0;
		while (str1[idx] == str2[idx]) {
			if (str1[idx] == 0 || str2[idx] == 0)
				return true;
			idx = idx + 1;
		}
		return false;
	}

}
