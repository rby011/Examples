package certi.blockchain.kata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Solution {

	static final int MAXLEDGER = 5;
	static final int MAXDATASIZE = 400000;
	static final int MAXQUERY = 1500;
	static final int MAXORGBLOCK = 10000;

	static char ledgerData[][] = new char[MAXLEDGER][MAXDATASIZE];

	static int tc, L, Q;

	// JAVA DOES NOT SUPPORT 'unsigned' & 'long long'
	// static long pseudo_rand(int max) {
	// seed = ((unsigned long long)seed * 1103515245 + 12345) & 0x7FFFFFFF;
	// return seed % max;
	// }

	static long seed = 54321;

	static long pseudo_rand(int max) {
		seed = (seed * 1103515245 + 12345) & 0x7FFFFFFF;
		return Long.remainderUnsigned(seed, max);
	}

	static int calcHash(char buf[], int pos, int len) {
		int hash = 0;
		while (len-- > 0)
			hash = (((hash << 5) + hash) + buf[pos++]) & 0x7fffffff;
		return hash;
	}

	static final int BLOCK_SIZE = 55;
	static final int MAXCHILD = 20;
	static final int MAXDEPTH = 50;

	static ORG_BLOCK orgBlock[] = new ORG_BLOCK[MAXORGBLOCK];
	static {
		for (int i = 0; i < MAXORGBLOCK; i++)
			orgBlock[i] = new ORG_BLOCK();
	}
	static ORG_BLOCK hashTable[] = new ORG_BLOCK[MAXORGBLOCK];

	static boolean findHash(int hash) {
		ORG_BLOCK b = hashTable[hash % MAXORGBLOCK];
		while (b != null) {
			if (hash == b.hash)
				return true;
			b = b.next;
		}
		return false;
	}

	static void setNumber4(char buf[], int hash) {
		for (int i = 3; i >= 0; --i) {
			buf[i] = (char) (hash & 0xFF);
			hash >>= 8;
		}
	}

	static int run() {
		int corrected = 0;
		for (int i = 0; i < MAXORGBLOCK; ++i)
			hashTable[i] = null;

		L = scan.nextInt();
		Q = scan.nextInt();
		seed = scan.nextLong();

		int blockCnt = (int) (pseudo_rand(Q * 20) + Q / 2);
		if (blockCnt > MAXORGBLOCK)
			blockCnt = MAXORGBLOCK;
		int totalLen = 0;
		for (int i = 0; i < blockCnt; ++i) {
			if (totalLen >= MAXDATASIZE - (7 + 3 * 16)) {
				blockCnt = i;
				break;
			}
			orgBlock[i].valid = L - (L + 1) / 2;
			orgBlock[i].childCnt = 0;
			// JAVA DOES NOT SUPPORT NEGATIVE INDEX
			// int pre = -1;
			int pre = blockCnt - 1;
			if (i == 0) {
				setNumber4(orgBlock[i].data, 0);
				orgBlock[pre].depth = 1;
			} else {
				do {
					pre = (int) pseudo_rand(i);
				} while (orgBlock[pre].childCnt >= MAXCHILD || orgBlock[pre].depth >= MAXDEPTH);
				++orgBlock[pre].childCnt;
				orgBlock[i].depth = orgBlock[pre].depth + 1;
				setNumber4(orgBlock[i].data, orgBlock[pre].hash);
			}
			orgBlock[i].data[4] = (char) pseudo_rand(256);
			orgBlock[i].data[5] = (char) pseudo_rand(256);
			int itemCnt = (int) (pseudo_rand(16) + 1);
			orgBlock[i].data[6] = (char) itemCnt;
			for (int j = 0; j < itemCnt; ++j) {
				orgBlock[i].data[7 + j * 3] = (char) (pseudo_rand(16) + 1);
				int amount = (int) (pseudo_rand(0x7FFF) + 1);
				orgBlock[i].data[7 + j * 3 + 1] = (char) (amount / 0xFF);
				orgBlock[i].data[7 + j * 3 + 2] = (char) (amount & 0xFF);
			}
			orgBlock[i].dataLen = 7 + itemCnt * 3;
			totalLen += orgBlock[i].dataLen;
			orgBlock[i].data[orgBlock[i].dataLen] = 0;
			orgBlock[i].hash = calcHash(orgBlock[i].data, 0, orgBlock[i].dataLen);
			orgBlock[i].next = hashTable[orgBlock[i].hash % MAXORGBLOCK];
			hashTable[orgBlock[i].hash % MAXORGBLOCK] = orgBlock[i];
		}

		int totalBlockCnt = blockCnt;
		for (int ledger = 0; ledger < L; ++ledger) {
			boolean used[] = new boolean[MAXORGBLOCK];
			int p = 4;
			for (int i = 0; i < blockCnt; ++i) {
				int index = (int) pseudo_rand(blockCnt);
				while (used[index]) {
					++index;
					if (index >= blockCnt)
						index = 0;
				}
				if (totalBlockCnt < 15000 && L != 1 && pseudo_rand(5) == 0 && orgBlock[index].valid > 0) {
					char data[] = new char[BLOCK_SIZE + 1];
					int j = 0;
					for (j = 0; j < orgBlock[index].dataLen; ++j)
						data[j] = orgBlock[index].data[j];
					if (pseudo_rand(2) != 0) {
						do {
							j = (int) pseudo_rand(orgBlock[index].dataLen);
						} while (j == 6);
						if (data[j] > 8)
							--data[j];
						else
							++data[j];
					} else {
						int cnt = data[6];
						if (cnt < 8) {
							++data[6];
							data[7 + cnt * 3] = (char) (pseudo_rand(16) + 1);
							int amount = (int) (pseudo_rand(0x7FFF) + 1);
							data[7 + cnt * 3 + 1] = (char) (amount / 0xFF);
							data[7 + cnt * 3 + 2] = (char) (amount & 0xFF);
						} else {
							--data[6];
						}
					}
					int len = 7 + data[6] * 3;
					for (j = 0; j < len; ++j) {
						ledgerData[ledger][p] = data[j];
						++p;
					}
					--orgBlock[index].valid;
					++totalBlockCnt;
				} else {
					for (int j = 0; j < orgBlock[index].dataLen; ++j) {
						ledgerData[ledger][p] = orgBlock[index].data[j];
						++p;
					}
				}
				used[index] = true;
			}
			setNumber4(ledgerData[ledger], p - 4);
		}

		UserSolution.restoreLedger(L, ledgerData);

		for (int q = 0; q < Q; ++q) {
			int hash, itemid, answer;
			hash = scan.nextInt();
			itemid = scan.nextInt();
			int result = UserSolution.calcAmount(hash, itemid);
			answer = scan.nextInt();
			if (result == answer) {
				++corrected;
				// System.out.print("CORRECT ");
			} else {
				System.out.println("WRONG : " + hash + " , " + itemid + " / " + result + "<X> ," + answer + "<OK>");
			}
		}
		return (corrected == Q) ? 100 : 0;
	}

	static Scanner scan = null;

	public static void main(String args[]) throws IOException {
		int T, totalscore;
		System.setIn(new FileInputStream(new File("./data/blockchain_input.txt")));
		scan = new Scanner(System.in);

		T = scan.nextInt();

		totalscore = 0;
		for (tc = 1; tc <= T; ++tc) {
			int score = run();
			totalscore += score;
			System.out.println("#" + tc + " : " + score);
		}
		System.out.println("#total score : " + (totalscore / T));
		if (totalscore / T == 100)
			return;

		scan.close();
		return;
	}
}

class ORG_BLOCK {
	int hash;
	int valid;
	int childCnt;
	int depth;
	int dataLen;
	char data[] = new char[Solution.BLOCK_SIZE + 1];
	ORG_BLOCK next;
}
