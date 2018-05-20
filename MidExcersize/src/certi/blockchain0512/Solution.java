package certi.blockchain0512;

import java.util.Scanner;

import certi.blockchain0512.gen.BlockChainGenerator;

/**
 * <pre>
# block image-1
1
1 1
00 00 00 0c 01 02 03 04 05 06 07 08 09 0a 0b 0c
 * </pre>
 **/
public class Solution {

	static final int MAX_BLOCK_LEN = 20000;
	static final int MAX_SERVER = 5;
	static char BLOCKIMG[][] = new char[MAX_SERVER][MAX_BLOCK_LEN];

	static Scanner scan = null;

	public static void run() {

		char cimages[][] = null;
		while (true) {
			try {
				cimages = BlockChainGenerator.generateBlockImageString();
			} catch (Exception e) {
			}
			if (cimages != null)
				break;
		}

		int nimage = 0;
		for (; nimage < cimages.length; nimage++) {
			BLOCKIMG[nimage] = cimages[nimage];
		}

		UserSolution.syncBlock(nimage, BLOCKIMG);
	}

	// IN ORDER TO DEBUG, DIVDE INTEGERS INTO EACH BLOCK PROPERTY
	public static int calchash(char image[], int pos, int len) {
		int idx = pos * 3;
		int integers[] = null;

		// 1. parent hash : length[4]
		int phash = 0;
		for (int p = 0; p < 4; p++, idx = idx + 3)
			phash = (phash << 8 * p) | (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));

		// 2. random : length[2]
		int rand_tran = 0, rand = 0, tran = 0;
		for (int r = 0; r < 2; r++, idx = idx + 3)
			rand = (rand << 8 * r) | (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));

		// 3. # of tran : length[2]
		for (int n = 0; n < 2; n++, idx = idx + 3)
			tran = (tran << 8 * n) | (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));
		rand_tran = rand << 16 | tran;

		// 4. transactions : (# of tran) x 4
		// 4.1 tran id : length[1]
		// 4.2 tran amt : length[3]
		int transactions[] = new int[tran];
		for (int t = 0; t < tran; t++) {
			int tranid = 0, tranamt = 0;
			tranid = todecimal(image[idx]) << 4 | todecimal(image[idx + 1]);
			idx = idx + 3;
			for (int a = 0; a < 3; a++, idx = idx + 3)
				tranamt = (tranamt << 8 * a) | (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));
			transactions[t] = tranid << 24 | tranamt;
		}

		/**
		 * HASHING
		 */
		integers = new int[2 + tran];
		integers[0] = phash;
		integers[1] = rand_tran;
		for (int i = 2; i < integers.length; i++)
			integers[i] = transactions[i - 2];

		int hashcode = 1;
		int prime = 0;
		for (int i = 0; i < integers.length; i++) {
			prime = ((i + 1) * (i + 1) + (i + 1) + 41);
			hashcode = (hashcode << 5) + integers[i] * prime;
		}

		return hashcode;
	}

	public static int todecimal(char ch) {
		if (ch >= 'a' && ch <= 'f') {
			return (int) (ch - 'a' + 10);
		} else {
			return (int) (ch - '0');
		}
	}

	public static void testCalcHash() {
		String sImage = "00 00 00 78 00 00 00 00 f9 52 00 0e cd 00 00 80 f6 00 00 8e c7 00 00 6d 0b 00 00 bf 38 00 00 69 54 00 00 e6 cc 00 00 b1 5d 00 00 9f ee 00 00 42 73 00 00 46 cb 00 00 d5 a2 00 00 f5 50 00 00 e8 b6 00 00 8b b8 86 da f3 12 14 00 04 3b 00 00 a3 bb 00 00 3f 7c 00 00 98 3c 00 00 3a b8 86 da f3 ee 88 00 06 03 00 00 b5 9f 00 00 0b 29 00 00 c6 a6 00 00 1c 2c 00 00 56 b9 00 00 40";
		char cImage[] = sImage.toCharArray();
		calchash(cImage, 4, 64);
	}

	public static void main(String args[]) {
		// run();
		testCalcHash();
	}

}
