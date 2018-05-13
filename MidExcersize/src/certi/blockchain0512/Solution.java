package certi.blockchain0512;

import java.util.Scanner;

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

	public static void main(String args[]) {
		scan = new Scanner(System.in);
		run();
		scan.close();
	}

	public static void run() {
		int TC = scan.nextInt();
		int S = scan.nextInt();
		int Q = scan.nextInt();

		for (int tc = 1; tc <= TC; tc++) {
			for (int s = 0; s < S; s++)
				BLOCKIMG[s] = generateBlockImage();

			UserSolution.syncBlock(S, BLOCKIMG);

			for (int q = 0; q < Q; q++) {

				int hash = 0, id = 0;

				UserSolution.calcAmount(hash, id);
			}

		}
	}

	// block 당 transaction 최대 14 개
	public static int calchash(char buf[], int pos, int len) {
		int hash = 0;
		for (int i = pos, r = 0; i < pos + len; i = i + 4, r++) {
			int radix = (int) Math.pow(16, r);
			int value = 0;
			for (int j = 0; j < 4; j++)
				value = value | (((int) buf[i + j]) << (8 * (3 - j)));
			hash = hash + value * radix;
		}
		return hash & 0xffffffff;
	}

	public static char[] generateBlockImage() {
		String head = scan.next() + " ";
		String line = head + scan.nextLine().trim();
		int len = line.length();
		int N = (len + 1) / 3;
		char blockimage[] = new char[MAX_BLOCK_LEN];
		for (int n = 0, i = 0; n < N && i < len; n++) {
			char fourbit_1st = line.charAt(i++);
			char fourbit_2nd = line.charAt(i++);
			i++;
			char onebyte = (char) (todecimal(fourbit_1st) << 4 | todecimal(fourbit_2nd));
			blockimage[n] = onebyte;
			System.out.print((int) onebyte + " ");
		}
		System.out.println();
		return blockimage;
	}

	public static int todecimal(char ch) {
		if (ch >= 'a' && ch <= 'f') {
			return (int) (ch - 'a' + 10);
		} else {
			return (int) (ch - '0');
		}
	}
	
}
