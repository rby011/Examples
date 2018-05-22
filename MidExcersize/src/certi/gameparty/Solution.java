package certi.gameparty;

import java.util.Scanner;

/**
 * # TC 16 1361 번째 search 결과 잘못됨 8 이 정답인데, 7을 정답이라고 함
 * 
 */
public class Solution {

	static boolean FULL_CHECK = false;

	static final int MAXPARTIES = 50000;
	static final int MAXMEMBERS = 10000;
	static final int MAXCOMMANDS = 100000;

	static final int CLOSERATE = 5;
	static final int SUSPENDRATE = 10;
	static final int RECOVERYRATE = 15;
	static final int ADDRATE = 60;

	/**
	 * extern void init(int n); extern void addParty(int index, int m, char
	 * members[4][11]); extern void closeParty(int index); extern void
	 * suspendMember(char member[11]); extern void recoveryMember(char member[11]);
	 * extern int search(char members[2][11], int mode);
	 **/

	static int N;
	static int M;
	static char members[][] = new char[MAXMEMBERS][11];

	/**
	 * static unsigned int seed = 12345; static unsigned int pseudo_rand(int max) {
	 * seed = ((unsigned long long)seed * 1103515245 + 12345) & 0xFFFF; return seed
	 * % max; }
	 **/
	static int seed = 12345;

	static int pseudo_rand(int max) {
		seed = (int) (((long) seed * 1103515245 + 12345) & 0xFFFF);
		return seed % max;
	}

	// static int checkId[] = new int[0xFFFF];
	// static unsigned int getIndex(const char *str) {
	// unsigned long num = 5381;
	// int c;
	// while (c = *str++) {
	// num = (((num << 5) + num) + c) % 0xFFFF;
	// }
	// return num % 0xFFFF;
	// }
	static int checkId[] = new int[0xFFFF];

	static int getIndex(char str[]) {
		long num = 5381;
		byte c = 0;
		int i = 0;
		while ((c = (byte) str[i]) != 0) {
			num = (((num << 5) + num) + c) % 0xFFFF;
			i += 1;
		}
		return (int) num % 0xffff;
	}

	static void makeMembers() {
		for (int i = 0; i < MAXMEMBERS; ++i) {
			int len = pseudo_rand(6) + 4;
			for (int j = 0; j < len; ++j) {
				members[i][j] = (char) (pseudo_rand(26) + 'a');
			}
			members[i][len] = 0;
			while (checkId[getIndex(members[i])] != 0) {
				members[i][pseudo_rand(len)] = (char) (pseudo_rand(10) + '0');
			}
			checkId[getIndex(members[i])] = 1;
		}
	}

	static int partyCnt;
	static boolean partyStatus[] = new boolean[MAXPARTIES];
	static int memberStatus[] = new int[MAXMEMBERS];
	static int suspendStatus[] = new int[MAXMEMBERS];

	// member status
	static final int OFF = 0, ON = 1;

	static void addParty() {
		int memberN = pseudo_rand(4) + 1;
		int maxN = N < MAXMEMBERS ? N : MAXMEMBERS;

		char temp[][] = new char[4][11];

		for (int i = 0; i < memberN; ++i) {
			boolean sameChk = true;
			int randIdx = pseudo_rand(maxN);
			while (sameChk) {
				sameChk = false;
				for (int j = 0; j < i; ++j) {
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
		UserSolution.addParty(partyCnt, memberN, temp);
		partyStatus[partyCnt++] = true;
	}

	static void strcpy(char dest[], char src[]) {
		for (int i = 0; i < dest.length; i++)
			dest[i] = src[i];
	}

	static boolean strcmp(char str1[], char str2[]) {
		for (int i = 0; i < str1.length; i++) {
			if (str1[i] != str2[i])
				return false;
		}
		return true;
	}

	static int search() {
		int mode = pseudo_rand(3);
		char temp[][] = new char[2][11];
		int maxN = N < MAXMEMBERS ? N : MAXMEMBERS;
		int randIdx = pseudo_rand(maxN);
		while (suspendStatus[randIdx] == ON) {
			++randIdx;
			if (randIdx >= maxN)
				randIdx = 0;
		}
		strcpy(temp[0], members[randIdx]);

		if (mode >= 1) {
			randIdx = pseudo_rand(maxN);
			while (suspendStatus[randIdx] == ON || /* ! */strcmp(members[randIdx], temp[0])) {
				++randIdx;
				if (randIdx >= maxN)
					randIdx = 0;
			}
			strcpy(temp[1], members[randIdx]);
		}

		return UserSolution.search(temp, mode);
	}

	static int run() {

		int accepted = 0;
		int searchCnt = 0;
		int suspendCnt = 0;
		int closeCnt = 0;
		partyCnt = 0;
		int maxN = N < MAXMEMBERS ? N : MAXMEMBERS;

		int startN = pseudo_rand(N);
		if (startN < 4)
			startN = 4;

		// INITIAL BUIDING PARTY DATA
		for (int i = 0; i < startN; ++i) {
			addParty();
		}

		// WITH INITIAL PARTY DATA, TEST USER OPERATINS
		for (int i = 0; i < M - startN; ++i) {

			int command = pseudo_rand(100);
			char temp[] = new char[11];
			int randIdx;
			if (command < CLOSERATE && closeCnt < partyCnt / 5) {
				randIdx = pseudo_rand(partyCnt);
				while (!partyStatus[randIdx]) {
					++randIdx;
					if (randIdx >= partyCnt)
						randIdx = 0;
				}

				UserSolution.closeParty(randIdx);

				partyStatus[randIdx] = false;
				++closeCnt;
			} else if (command < SUSPENDRATE && suspendCnt < partyCnt / 3) {
				randIdx = pseudo_rand(maxN);
				while (suspendStatus[randIdx] == ON) {
					++randIdx;
					if (randIdx >= maxN)
						randIdx = 0;
				}
				strcpy(temp, members[randIdx]);

				UserSolution.suspendMember(temp);

				suspendStatus[randIdx] = ON;
				++suspendCnt;
			} else if (command < RECOVERYRATE && suspendCnt > 0) {
				randIdx = pseudo_rand(maxN);
				while (suspendStatus[randIdx] == OFF) {
					++randIdx;
					if (randIdx >= maxN)
						randIdx = 0;
				}
				strcpy(temp, members[randIdx]);

				UserSolution.recoveryMember(temp);

				suspendStatus[randIdx] = OFF;
				--suspendCnt;
			} else if (command < ADDRATE && partyCnt < N) {
				addParty();
			} else {
				int answer = search();
				if (FULL_CHECK || searchCnt < 10) {
					int correct = scan.nextInt();
					if (correct == answer)
						++accepted;
					else
						accepted = accepted;
				} else
					accepted += answer;
				++searchCnt;
			}
		}
		return accepted;
	}

	static int main() {
		// try {
		// System.setIn(new FileInputStream("C:\\Users\\Changsun
		// Song\\Desktop\\알고리즘자격준비\\git\\MidExcersize\\src\\certi\\gameparty\\input.txt"));
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

		int T = scan.nextInt();
		;
		int totalScore = 0;

		makeMembers();

		for (int test_case = 1; test_case <= T; ++test_case) {
			// System.out.println("##### TC " + test_case + "#####");
			// System.out.println();

			for (int i = 0; i < MAXMEMBERS; ++i) {
				memberStatus[i] = OFF;
				suspendStatus[i] = OFF;
			}

			seed = scan.nextInt();
			N = scan.nextInt();
			M = scan.nextInt();

			UserSolution.init(N);

			int answer = run();
			int correct = scan.nextInt();
			if (answer == correct) {
				System.out.println("#" + test_case + " 100");
				totalScore += 100;
			} else
				System.out.println("#" + test_case + " 0");
		}

		System.out.println("#total score : " + (totalScore / T));
		
		float total = 0;
		for (int i = 0; i < UserSolution.tc; i++) {
			System.out.println("# " + (i + 1));
			for (int j = 0; j < 4; j++) {
				System.out.println(UserSolution.counts[i][j] + " , " + (UserSolution.times[i][j]/1000000000f));
				total = total + (UserSolution.times[i][j]/1000000000f);
			}
		}
		System.out.println("#total time : " + total);
		
		System.out.println("#party table collision : " + Hashtable.collision +"/"+ Hashtable.putcall);
		System.out.println("#member table collision : " + Hashset.collision +"/"+ Hashset.putcall);
		
		System.out.println("#time overhead : " + Hashtable.overhead/1000000000f);
		return 0;
	}

	static Scanner scan = null;

	public static void main(String args[]) {
		scan = new Scanner(System.in);
		main();
		scan.close();
	}
}