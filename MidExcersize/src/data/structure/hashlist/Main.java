package data.structure.hashlist;

import java.util.Scanner;

/**
 * <PRE>
10
aaa 1
bbb 2
ccc 3
ddd 4
eee 5
fff 6
aaa 7
bbb 8
bbb 9
bbb 10
 * </PRE>
 */
public class Main {
	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);

		Hashtable htable = new Hashtable(2);

		int N = scan.nextInt();
		for (int n = 0; n < N; n++) {
			char key[] = scan.next().toCharArray();
			int id = scan.nextInt();
			htable.put(key, id);
		}
		
		htable.printTable();
		
		scan.close();
	}
}
