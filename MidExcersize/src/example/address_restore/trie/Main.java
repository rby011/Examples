package example.address_restore.trie;

import java.util.Scanner;

public class Main {
	static final int ALLOWED_CODE_COUNT = 32;
	static final int MAX_CODE_SIZE = 20000;
	static final int MAX_QUERY = 20000;
	static final int MIN_QUERY = 100;
	static final int CODE_LENGTH = 6;
	static final int CUTLINE = 95;

	static int totalinput, correctCount, N = 50;

	static char allowCode[] = new char[ALLOWED_CODE_COUNT];
	static char code[][] = new char[MAX_CODE_SIZE][CODE_LENGTH + 1];

	static Scanner scan = null;

	static boolean check(char ch1[], char ch2[]) {
		int idx = 0;
		while (ch1[idx] == ch2[idx]) {
			idx = idx + 1;
			if (ch1[idx] == 0 && ch2[idx] == 0)
				return true;
		}
		return false;
	}

	static void run() {
		char input[] = new char[CODE_LENGTH + 1];
		char prediction[] = new char[CODE_LENGTH + 1];

		char tempAllowCode[] = new char[ALLOWED_CODE_COUNT + 1];
		char tempCode[][] = new char[MAX_CODE_SIZE][CODE_LENGTH + 1];

		boolean used[] = new boolean[MAX_CODE_SIZE];

		// INVOKE USERCODE : POSSIBLE LETTERS
		tempAllowCode = scan.next().toCharArray();
		User.getAllowCode(tempAllowCode);

		N = scan.nextInt();

		for (int i = 0; i < N; ++i) {
			tempCode[i] = scan.next().toCharArray();

			code[i] = new char[CODE_LENGTH + 1];
			for (int j = 0; j < CODE_LENGTH; j++) {
				code[i][j] = tempCode[i][j];
			}
			code[i][CODE_LENGTH] = 0;
		}

		// INVOKE USERCODE : POSSIBLE ADDRESSES
		User.getCode(N, tempCode);

		while (true) {
			int selected = scan.nextInt();
			if (selected < 0)
				break;

			input = scan.next().toCharArray();

			// INVOKE USER CODE FOR EACH ADDRESS
			User.restore(input, prediction);

			if (check(code[selected], prediction))
				++correctCount;

			++totalinput;
		}
	}

	public static void main(String args[]) {
		scan = new Scanner(System.in);

		int T = scan.nextInt();
		int totalscore = 0;
		for (int tc = 1; tc <= T; tc++) {
			totalinput = 0;
			correctCount = 0;
			run();
			int score = correctCount * 100 / totalinput > CUTLINE ? 100 : 0;
			totalscore = totalscore + score;
		}
		totalscore = totalscore / T;
		System.out.println("#total score : " + totalscore);
		if (totalscore == 100)
			System.out.println("- Success");
		else
			System.out.println(" - Fail");
		scan.close();
	}
}
