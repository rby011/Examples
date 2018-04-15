package example.address_restore;

public class User {
	static final int ALLOWED_CODE_COUNT = 32;
	static final int CODE_LENGTH = 6;
	static final int BIT = 8;
	static final int ASCII_LENGTH = 256;

	static int N = 0;
	static byte[] l2i_table = new byte[ASCII_LENGTH];
	static char[] i2l_table = new char[ALLOWED_CODE_COUNT];

	static Trie code_table = new Trie();

	static void getAllowCode(char code[]) {
		l2i_table = new byte[ASCII_LENGTH];
		i2l_table = new char[ALLOWED_CODE_COUNT];

		for (int i = 0; i < ALLOWED_CODE_COUNT; i++) {
			l2i_table[code[i]] = (byte) (i + 1);
			i2l_table[i] = code[i];
		}
	}

	static void getCode(int n, char code[][]) {
		N = n;
		for (n = 0; n < N; n++)
			code_table.put(code[n], l2i_table);
	}

	static void restore(char errcode[], char prediction[]) {
		Node pnode = new Node();
		code_table.traverse(errcode, prediction, l2i_table, i2l_table, 0, code_table.root, pnode);
	}
}

class Trie {
	Node root = new Node();

	public void put(char[] code, byte[] ltable) {
		Node node = this.root;
		Node pnode = null;
		for (int i = 0; i < User.CODE_LENGTH; i++) {
			int index = ltable[code[i]];
			if (node.childs[index] == null)
				node.childs[index] = new Node();
			pnode = node;
			node = node.childs[index];
		}
		pnode.isterminal = true;
	}

	public Node get(char[] code, byte[] ltable) {
		Node node = this.root;
		Node pnode = null;
		for (int i = 0; i < User.CODE_LENGTH; i++) {
			int index = ltable[code[i]];
			if (node.childs[index] != null) {
				pnode = node;
				node = node.childs[index];
			} else {
				return null;
			}
		}
		return pnode;
	}

	public boolean traverse(char errcode[], char predcode[], byte l2i_table[], char i2l_table[], int depth, Node node,
			Node pnode) {
		if (node == null && pnode != null && pnode.isterminal) {
			// FOUND RESTORATION
			return true;
		}

		char ech = errcode[depth];
		Node childs[] = node.childs;
		// ALLOWED_CODE_COUNT == childs.length
		for (int idx = 0; idx < User.ALLOWED_CODE_COUNT; idx++) {
			if (childs[idx] != null) {
				char pch = i2l_table[idx];
				if (ech == pch) {
					predcode[depth] = errcode[depth];
					boolean ret = traverse(errcode, predcode, l2i_table, i2l_table, depth + 1, childs[idx], node);
					if (ret)
						return true;
					else
						continue;
				} else {
					// if a '1 bit negation of ech' is equal to 'och'RECURSION with childs[i]
					// IF RECURSION RESULT IS TRUE, BREAK
					char nech = 0;
					for (int bit = 0; bit < User.BIT; bit++) {
						if (((1 << bit) & ech) == 1) {
							nech = (char) ((~(1 << bit) & 0x7ffffff) & ech);
						} else {
							int mask = 1 << bit;
							nech = (char) (mask ^ ech);// exclusive-or
						}
						if (nech == pch) {
							predcode[depth] = nech;
							boolean ret = traverse(errcode, predcode, l2i_table, i2l_table, depth + 1, childs[idx],
									node);
							if (ret)
								return true;
							else
								continue;
						}
					}
				}
			}
		}

		// NOT FOUND
		return false;
	}
}

class Node {
	boolean isterminal = false;
	Node childs[] = new Node[User.ALLOWED_CODE_COUNT + 1];
}