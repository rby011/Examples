package data.structure.hashlist;

public class BestAndSimpleHashFunctions {

	/**
	 * <PRE>
	 *  HASH FUNCITONS : https://diveintodata.org/2015/05/25/hash-function-implementations/
	 *  - SIMPLE IMPLEMENTATIONS : http://www.cse.yorku.ca/~oz/hash.html
	 *  - BENCHMARK HASH FUNCTIONS : https://softwareengineering.stackexchange.com/questions/49550/which-hashing-algorithm-is-best-for-uniqueness-and-speed
	 * </PRE>
	 */

	public static void main(String args[]) {
		for (int i = 0; i < 10; i++) {
			System.out.println("" + hash_multiplication(i));
		}

	}

	/**
	 * HASH FUNCTION FOR STRING
	 */
	// 문자열 해슁 함수
	public static int hash_djb2(char[] sequence) {
		return (hashcode_djb2(sequence) & 0x7fffffff) % m;
	}

	// - 2^32 ~ 2^31 까지의 정수가 반환
	public static int hashcode_djb2(char[] sequence) {
		int hash = 1;

		for (int s = 0; s < sequence.length; s++) {
			hash = hash * 31 + sequence[s];
		}
		return hash;
	}

	/**
	 * HASH FUNCTION FOR NUMBER
	 */
	// 정수 해슁 함수
	static int m = 100;

	public static int hash_multiplication(int key) {
		double a = 13 / 400;
		return (int) (((double) key * a) % 1.0) * m;
	}

	public int hash_division(int key) {
		return key % m;
	}
}
