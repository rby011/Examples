package example.fashion;

import java.util.Scanner;

public class FashionSuggestion {
	static final int MAX_N = 5000;
	static final int MAX_M = 5;
	static final int MAX_R = 500;
	static final int MAX_CODE = 10;

	// FOR SIMLIARITY
	static int weight[] = new int[3];

	// FOR CATALOG DB
	static int N, K;
	static int itemN[] = new int[MAX_M];
	static char code[][][] = new char[MAX_M][MAX_R][MAX_CODE];
	static int catalog[][] = new int[MAX_N * 2][MAX_M];

	// FOR RANDOM INPUT GENERATION
	static int seed = 1;

	// FOR TC
	static int tc;

	// FOR INPUT
	static Scanner scan = null;

	public static void main(String args[]) {

		scan = new Scanner(System.in);

		int T = scan.nextInt();
		int score = 0;

		for (tc = 1; tc <= T; tc++) {
			int r = run();
			score = score + r;

			System.out.println("# " + tc + " " + r);
		}
		System.out.println("# total score : " + score / T);

		scan.close();
	}

	private static int run() {
		int score = 100;

		N = scan.nextInt();
		K = scan.nextInt();
		seed = scan.nextInt();
		weight[0] = scan.nextInt();
		weight[1] = scan.nextInt();
		weight[2] = scan.nextInt();

		itemN[0] = scan.nextInt();
		itemN[1] = scan.nextInt();
		itemN[2] = scan.nextInt();
		itemN[3] = scan.nextInt();
		itemN[4] = scan.nextInt();

		makeFashion();

		// INVOKE USER FUNCTION
		UserCode.init();

		for (int i = 0; i < N; i++) {
			Fashion fashion = new Fashion();
			strcpy(fashion.hat, code[0][catalog[i][0]]);
			strcpy(fashion.top, code[1][catalog[i][1]]);
			strcpy(fashion.pants, code[2][catalog[i][2]]);
			strcpy(fashion.shoes, code[3][catalog[i][3]]);
			strcpy(fashion.accessory, code[4][catalog[i][4]]);

			// INVOKE USER FUNCTION
			UserCode.addCatalog(fashion);
		}

		// SCORING
		for (int i = N; i < (N + K); ++i) {
			int answer = scan.nextInt();

			Fashion fashion = new Fashion();
			strcpy(fashion.hat, code[0][catalog[i][0]]);
			strcpy(fashion.top, code[1][catalog[i][1]]);
			strcpy(fashion.pants, code[2][catalog[i][2]]);
			strcpy(fashion.shoes, code[3][catalog[i][3]]);
			strcpy(fashion.accessory, code[4][catalog[i][4]]);

			// INVOKE USER FUNCTION
			int result = UserCode.newFashion(fashion);
			if (result != answer)
				score = 0;
		}

		return score;
	}

	private static void strcpy(char dest[], char src[]) {
		if (src.length > dest.length)
			return;

		for (int i = 0; i < src.length; i++)
			dest[i] = src[i];
	}

	private static void makeFashion() {
		for (int i = 0; i < MAX_M; i++) {
			for (int k = 0; k < itemN[i]; ++k) {
				int length = 0;
				for (int m = 0; m < 3; ++m) {
					code[i][k][length++] = (char) ('A' + pseudoRand() % 26);
					int num = pseudoRand() % 100;
					if (num >= 10) {
						code[i][k][length++] = (char) ('0' + num / 10);
						num = num % 10;
						code[i][k][length++] = (char) ('0' + num / 10);
					} else if (num > 0) {
						code[i][k][length++] = (char) ('0' + num);
					}
				}
				code[i][k][length] = 0;
			}
		}

		for (int i = 0; i < (N + K); ++i) {
			for (int k = 0; k < MAX_M; ++k) {
				catalog[i][k] = pseudoRand() % itemN[k];
			}
		}
	}

	private static int pseudoRand() {
		seed = seed * 1103515245 + 12345;
		return ((seed / 65536) % 32768) & 0x7fffffff;
	}

	private static int calcSimliarity(char code1[], char code2[]) {
		if (new String(code1).equals(new String(code2))) {
			return 100;
		}
		int sum = 0;
		int index1 = 0, index2 = 0;
		for (int i = 0; i < 3; i++) {
			char c1 = code1[index1++];
			char c2 = code2[index2++];
			int n1 = 0;
			while (code1[index1] != 0 && code1[index1] <= '9') {
				n1 = n1 * 10;
				n1 = n1 + code1[index1++] - '0';
			}
			int n2 = 0;
			while (code2[index2] != 0 && code2[index2] <= '9') {
				n2 = n2 * 10;
				n2 = n2 + code2[index2++] - '0';
			}
			sum = sum + ABS((25 - ABS(c1 - c2)) * weight[i] - ABS(n1 - n2)) / 5;
		}

		if (sum > 99)
			sum = 99;
		if (sum < 0)
			sum = 0;

		return sum;
	}

	private static int ABS(int num) {
		return num < 0 ? num * -1 : num;
	}
}

class Fashion {
	static final int MAX_CODE = 10;

	char hat[] = new char[MAX_CODE];
	char top[] = new char[MAX_CODE];
	char pants[] = new char[MAX_CODE];
	char shoes[] = new char[MAX_CODE];
	char accessory[] = new char[MAX_CODE];

}