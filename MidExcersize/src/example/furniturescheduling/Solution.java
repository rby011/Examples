package example.furniturescheduling;

import java.util.Scanner;
/*
<PRE>
1
1
0 30 3 4
1 5 2 2
2 50 5 7
-1
</PRE>
 */
public class Solution {
	static class Furniture {
		int requestTime;
		int price;
		int makingTime;
		int waitingTime;
		int masterID;
		boolean done;
	}

	static final int MAXFURNITURE = 10000;
	static int furnitureNumber; // the number of furnitures
	static Furniture furniture[] = new Furniture[MAXFURNITURE + 1];
	static {
		for (int i = 0; i < MAXFURNITURE + 1; i++)
			furniture[i] = new Furniture();
	}

	static class Master {
		int furnitureID;
		int startMakingTime;
	}

	static final int MAXMASTER = 32;
	static int masterCount; // the number of masters
	static Master master[] = new Master[MAXMASTER + 1];
	static {
		for (int i = 0; i < MAXMASTER + 1; i++) {
			master[i] = new Master();
		}
	}
	static int maxwaitingTime;
	static int currentTime;
	static int earned;
	static int total;
	static boolean success;
	static int testcase;
	static int TC;

	static boolean startMaking(int masterID, int furnitureID) {
		if (masterID <= 0 || masterID > masterCount || furnitureID <= 0 || furnitureID > furnitureNumber
				|| furniture[furnitureID].requestTime > currentTime || furniture[furnitureID].masterID != -1)
			return success = false;

		if (master[masterID].furnitureID != -1) {
			furniture[master[masterID].furnitureID].makingTime -= currentTime - master[masterID].startMakingTime;
			furniture[master[masterID].furnitureID].masterID = -1;
		}

		master[masterID].furnitureID = furnitureID;
		master[masterID].startMakingTime = currentTime;
		furniture[furnitureID].masterID = masterID;

		return true;
	}

	static boolean completeMaking(int masterID) {
		int furnitureID;
		if (masterID <= 0 || masterID > masterCount || (furnitureID = master[masterID].furnitureID) == -1
				|| furniture[furnitureID].done == true
				|| furniture[furnitureID].makingTime > currentTime - master[masterID].startMakingTime
				|| currentTime > furniture[furnitureID].requestTime + furniture[furnitureID].waitingTime)
			return success = false;

		earned += furniture[furnitureID].price;
		furniture[furnitureID].done = true;
		master[masterID].furnitureID = -1;

		return true;
	}

	static void run() {

		masterCount = scan.nextInt();
		for (int idx = 1; idx <= masterCount; ++idx)
			master[idx].furnitureID = -1;

		UserSolution.init(masterCount);

		total = 0;
		maxwaitingTime = 0;
		furnitureNumber = 1;
		currentTime = 0;
		while (true) {
			furniture[furnitureNumber].requestTime = scan.nextInt();

			if (furniture[furnitureNumber].requestTime < 0)
				break;
			
			while (furniture[furnitureNumber].requestTime > currentTime) {
				UserSolution.tick(currentTime);
				currentTime++;
			}

			furniture[furnitureNumber].price = scan.nextInt();
			furniture[furnitureNumber].makingTime = scan.nextInt();
			furniture[furnitureNumber].waitingTime = scan.nextInt();

			furniture[furnitureNumber].masterID = -1;
			furniture[furnitureNumber].done = false;
			total += furniture[furnitureNumber].price;
			if (furniture[furnitureNumber].requestTime + furniture[furnitureNumber].waitingTime > maxwaitingTime) {
				maxwaitingTime = furniture[furnitureNumber].requestTime + furniture[furnitureNumber].waitingTime;
			}
			UserSolution.request(furnitureNumber, furniture[furnitureNumber].requestTime,
					furniture[furnitureNumber].price, furniture[furnitureNumber].makingTime,
					furniture[furnitureNumber].waitingTime);
			++furnitureNumber;
		}

		while (currentTime <= maxwaitingTime) {
			UserSolution.tick(currentTime);
			currentTime++;
			if (!success)
				return;
		}
	}

	static final int PRODUCTION_RATE = 70;

	static void main() {

		int totalscore = 0;

		TC = scan.nextInt();

		for (testcase = 1; testcase <= TC; ++testcase) {
			earned = 0;
			success = true;
			run();
			if (success && earned * 100 / total >= PRODUCTION_RATE)
				totalscore += earned;
			else
				totalscore -= 10000000;
			System.out.println("#" + testcase + " " + earned + " / " +total);
		}

		if (totalscore < 0)
			totalscore = 0;
		
		System.out.println("#total score : " + totalscore);

		if (totalscore > 0)
			System.out.println("SUCCESS");
		else
			System.out.println("FAIL");
	}

	static Scanner scan = null;

	public static void main(String args[]) {
		scan = new Scanner(System.in);
		main();
		scan.close();
	}
}