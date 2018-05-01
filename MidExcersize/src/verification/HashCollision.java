package verification;

import java.util.HashSet;
import java.util.Hashtable;

public class HashCollision {
	static HashSet<Double> hset = new HashSet<Double>();
	static Hashtable<Double, char[]> htable = new Hashtable<Double, char[]>();

	public static void main(String args[]) {
		String code1 = "9999999999";
		int index = (int)generateidnex(code1.toCharArray());
		System.out.println(index);
		
	}

	public static int pow(int base, int radix) {
		int ret = 1;
		for (int i = 0; i < radix; i++) {
			ret = ret * base;
		}
		return ret;
	}

	static long dp[][] = new long[37][10];
	public static long pow_dp(int base, int radix) {
		if (radix == 0)
			return 1;
		if (radix == 1)
			return base;

		if (dp[base][radix] != 0)
			return dp[base][radix];

		if (radix % 2 == 0) {
			if (dp[base][radix / 2] == 0)
				dp[base][radix / 2] = pow_dp(base, radix / 2);
			return dp[base][radix / 2] * dp[base][radix / 2];
		} else {
			if (dp[base][radix / 2] == 0)
				dp[base][radix / 2] = pow_dp(base, radix / 2);
			return dp[base][radix / 2] * dp[base][radix / 2] * base;
		}
	}

	public static long generateidnex(char code[]) {
		long index = 0;
		for (int i = 0; i < code.length; i++) {
			long digit = 0;
			if (code[i] >= 'a' && code[i] <= 'z') {
				digit = code[i] - 'a';
			} else if (code[i] >= 'A' && code[i] <= 'Z') {
				digit = code[i] - 'A';
			} else if (code[i] >= '0' && code[i] <= '9') {
				digit = code[i] - '0' + 26;
			}
			index = index + digit * pow_dp(36, i);
		}
		return index;
	}

	static int code_cnt = 0;
	static int col_cnt = 0;

	private static void traverse(char pcode[], char code[], int idx) {
		if (idx == code.length) {
			// double hash = hashcode(code);

			code_cnt++;
			System.out.println(new String(code));
			// if (hset.contains(hash)) {
			// char ncode[] = htable.get(hash);
			// System.out.println("# " + new String(code) + ":" + hash);
			// System.out.println("# " + new String(ncode) + ":" + hash);
			// System.out.println();
			// col_cnt++;
			// }
			//
			// hset.add(hash);
			// htable.put(hash, code);

			return;
		}

		for (int i = 0; i < pcode.length; i++) {
			code[idx] = pcode[i];
			traverse(pcode, code, idx + 1);
		}
	}

	private static double hashcode(char code[]) {
		double hash = 1;
		for (int i = 0; i < code.length; i++) {
			hash = hash * 33d + (double) code[i];
			// hash = hash * 33d + (double) (code[i] * code[i] + code[i] + 41);
		}
		return hash;
	}

}
