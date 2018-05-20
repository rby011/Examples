package example.parking;

import java.util.HashSet;

public class Test {
	static HashSet<Double> hset = new HashSet<Double>();

	public static void main(String args[]) {

		// MAKING POSSIBLE CHARACTER SET
		char pcode[] = new char[36];
		int j = 0;
		for (int i = 'a'; i <= 'z'; i++, j++)
			pcode[j] = (char) i;
		for (int i = '0'; i <= '9'; i++, j++)
			pcode[j] = (char) i;

		// GENERATE CODE AND CHECK HASH COLLISION
		char code[] = new char[10];
		traverse(pcode, code, 0);
	}

	private static void traverse(char pcode[], char code[], int idx) {
		if (idx == code.length) {
			double hash = hashcode(code);

			if (hset.contains(hash)) {
				System.out.println(new String(code) + ":" + hash);
			}
			hset.add(hash);

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
			hash = hash * 5381 + code[i];
		}
		return hash;
	}

}
