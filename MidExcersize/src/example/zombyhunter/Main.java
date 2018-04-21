package example.zombyhunter;

import java.util.Scanner;

public class Main {
	static final int SHOT_MAX = 10;
	static final int MAP_MAX = 100;

	static int shot[][] = new int[SHOT_MAX][2];
	static int map[][] = new int[MAP_MAX][MAP_MAX];

	public static void init() {
		int i = 0, j = 0;
		// target init
		for (i = 0; i < SHOT_MAX; i++) {
			shot[i][0] = -1;// x
			shot[i][1] = 1;// y
		}
		// map init
		for (i = 0; i < MAP_MAX; i++)
			for (j = 0; j < MAP_MAX; j++)
				map[i][j] = 0;
	}

	public static int runGame(int size) {
		User.getTarget(size, map, shot);
		return User.getScore(size, map, shot);
	}

	public static void main(String args[]) {

		double a = 1.653;
		System.out.println((int)a);
		
		Scanner scan = new Scanner(System.in);
		int TC, N, score = 0, total_score = 0;

		TC = scan.nextInt();
		for (int t = 1; t <= TC; t++) {
			init();

			N = scan.nextInt();
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					map[i][j] = scan.nextInt();
				}
			}

			score = runGame(N);

			System.out.println("- score : " + score);

			total_score += score;
		}

		System.out.println("# total score : " + total_score);

		if (total_score > 0)
			System.out.println("success!");
		else
			System.out.println("fail!");

		scan.close();

	}
}
