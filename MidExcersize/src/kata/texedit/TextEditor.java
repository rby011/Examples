package kata.texedit;

import java.util.Scanner;

public class TextEditor {
	static Scanner scan = null;

	public static void main(String args[]) {
		scan = new Scanner(System.in);

		int T = scan.nextInt(), score = 0;
		for (int t = 1; t <= T; t++) {
			UserCode.init();

			int input = ASCII.SPACE;

			while (true) {
				input = scan.nextInt();
				if (input == ASCII.NULL)
					break;
				UserCode.process((char) input);
			}

			char text[] = UserCode.save();

			System.out.println("\n#" + t + " result : ");
			printEdit(text);
		}

		scan.close();
	}

	public static void printEdit(char text[]) {
		for (int i = 0; (int) text[i] != ASCII.NULL; i++) {
			System.out.print(text[i]);
		}
		System.out.println();
	}
}


