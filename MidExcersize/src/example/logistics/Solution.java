package example.logistics;

import java.util.Scanner;

public class Solution {

	static int score;
	static int getCount;

	static final int CMD_ADD = 100, CMD_GET = 200, CMD_EXIT = 900;

	static void run() {
		int N = scan.nextInt();
		UserSolution.init(N);

		int terminal1, terminal2;
		while (true) {
			int cmd = scan.nextInt();
			if (cmd == CMD_EXIT)
				break;
			terminal1 = scan.nextInt();
			terminal2 = scan.nextInt();

			if (cmd == CMD_ADD) {
				UserSolution.addTerminal(terminal1, terminal2);
			} else if (cmd == CMD_GET) {
				int ret = UserSolution.getDistance(terminal1, terminal2);
				int correct = scan.nextInt();
				if (ret == correct)
					++score;
				++getCount;
			}
		}
	}

	static Scanner scan = null;

	static int main() {

		int T = scan.nextInt();

		int test_case;
		int totalScore = 0;
		for (test_case = 1; test_case <= T; test_case++) {
			score = 0;
			getCount = 0;
			run();
			score = score / getCount * 100;
			score = (score == 100) ? 100 : 0;
			totalScore += score;
			System.out.println("#" + test_case + " " + score);
		}
		System.out.println("#total score : " + (totalScore / T));
		return 0;
	}

	public static void main(String args[]) {
		scan = new Scanner(System.in);
		main();
		scan.close();

	}
}
