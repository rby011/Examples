package certi.blockchain0512;

import certi.blockchain0512.gen.BlockChainGenerator;

public class UserSolution {
	static final int MAX_BLOCK_N = BlockChainGenerator.MAX_BLOCK_N;
	static final int MAX_CAPACITY = 10141;// PRIME NUMBER AROUND MAX_BLOCK_N

	static BlockTable blockTable[] = null;
	static Block bidxTable[][] = null;
	static int bcnt[] = null;
	static int nserver = 0;

	static BlockTable globalTable = null;

	public static void syncBlock(int S, char[][] blockimage) {
		nserver = S;

		blockTable = new BlockTable[S];
		bidxTable = new Block[S][];
		bcnt = new int[S];

		for (int s = 0; s < S; s++) {
			blockTable[s] = new BlockTable(MAX_CAPACITY);
			bidxTable[s] = new Block[MAX_CAPACITY];
			bcnt[s] = 0;

			char cimage[] = blockimage[s];
			parseBlockImage(cimage, s);

			buildBlockTree(s);
		}

	}

	public static int calcAmount(int hash, int id) {
		int found = 0, limit = (nserver / 2) + 1;
		Block roots[] = new Block[nserver];

		// 1. CHECK ROOT
		for (int s = 0; s < nserver; s++) {
			Block root = blockTable[s].get(hash);
			if (root != null) {
				roots[s] = root;
				found++;
			}
		}
		if (found < limit)
			return 0;

		globalTable = new BlockTable(MAX_CAPACITY);

		total = 0;
		for (int s = 0; s < nserver; s++) {
			if (roots[s] != null)
				trasTraverse(roots[s], id, limit);
		}

		return total;
	}

	static int total = 0;

	public static void trasTraverse(Block node, int tid, int limit) {
		globalTable.put(node);
		if (node.nexist >= limit) {
			Block block = globalTable.get(node.hash);
			total = total + block.tidx2amt[tid];
		}

		for (int i = 0; i < node.nchild; i++) 
			trasTraverse(node.childs[i], tid, limit);
	}

	public static int getTranAmount(int hash, int tid) {
		int amount = 0;

		int found = 0;
		for (int s = 0; s < nserver; s++) {
			Block block = blockTable[s].get(hash);
			if (block != null) {
				amount = block.tidx2amt[tid];
				found++;
			}
		}

		if (found < (nserver / 2 + 1))
			return 0;

		return amount;

	}

	public static void buildBlockTree(int cidx) {
		for (int i = 0; i < bcnt[cidx]; i++) {
			Block block = bidxTable[cidx][i];
			int phash = block.phash;
			Block pblock = blockTable[cidx].get(phash);
			if (pblock == null) {
				if (block.phash != 0) {
					System.out.print("ERROR !!! <NOT FOUND BLOCK> WHNE BUILDING TREE : ");
					System.out.println("phash : " + block.phash + ", block :" + block.hash);
				}
			} else {
				pblock.addChild(block);
			}
		}
	}

	public static void parseBlockImage(char image[], int cidx) {
		int pos = 0, idx = 0;

		// A. IMAGE LENGTH (4 byte)
		int ilen = 0;
		for (int b = 0; b < 4; pos = pos + 1, idx = idx + 3, b = b + 1) {
			int hex = (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));
			ilen = ilen | hex << 8 * (3 - b);
		}

		// B. A LOOP FOR EACH BLOCK
		int len = 0;
		while (len < ilen) {
			int bpos = pos, blen = 0;

			// 1. PARENT HASH
			int phash = 0;
			for (int b = 0; b < 4; pos = pos + 1, idx = idx + 3, b++) {
				int hex = (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));
				phash = phash | hex << 8 * (3 - b);
			}

			// 2. SKIP RANDOM
			pos = pos + 2;
			idx = idx + 6;

			// 3. # of TRANSACTION
			int ntran = 0;
			for (int b = 0; b < 2; pos = pos + 1, idx = idx + 3, b++) {
				int hex = (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));
				ntran = ntran | hex << 8 * (1 - b);
			}

			// 4. TRANSACTIONS : ID (1byte) , AMT(3byte)
			int transactions[] = new int[ntran];
			for (int t = 0; t < ntran; t++) {
				int tranID = 0, tranAmt = 0;
				tranID = (todecimal(image[idx]) << 4 | todecimal(image[idx + 1]));
				idx = idx + 3;
				pos = pos + 1;

				for (int b = 0; b < 3; pos = pos + 1, idx = idx + 3, b++) {
					int hex = todecimal(image[idx]) << 4 | todecimal(image[idx + 1]);
					tranAmt = tranAmt | hex << 8 * (2 - b);
				}

				transactions[t] = tranID << 24 | tranAmt;
			}
			blen = pos - bpos + 1;
			len = len + blen;

			// 5. HASH
			int hash = Solution.calchash(image, bpos, blen);

			// 6. MAKE A BLOCK INSTANCE AND ADD THIS TO HASHTABLE
			Block block = new Block(bcnt[cidx], hash, phash, transactions);
			bidxTable[cidx][bcnt[cidx]++] = block;
			blockTable[cidx].put(block);

			System.out.println(cidx + " :  " + block.hash);
		}
	}

	public static int todecimal(char ch) {
		if (ch >= 'a' && ch <= 'f') {
			return (int) (ch - 'a' + 10);
		} else {
			return (int) (ch - '0');
		}
	}

	public static void main(String args[]) {
		char images[][] = new char[2][];
		images[0] = "00 00 00 78 00 00 00 00 f9 52 00 0e 0a 00 00 80 06 00 00 8e 04 00 00 6d 0b 00 00 bf 0b 00 00 69 09 00 00 e6 09 00 00 b1 03 00 00 9f 0d 00 00 42 0a 00 00 46 08 00 00 d5 0c 00 00 f5 05 00 00 e8 02 00 00 8b 44 86 da f3 12 14 00 04 0e 00 00 a3 07 00 00 3f 04 00 00 98 00 00 00 3a 44 86 da f3 ee 88 00 06 03 00 00 b5 09 00 00 0b 0b 00 00 c6 01 00 00 1c 0e 00 00 56 05 00 00 40"
				.toCharArray();
		images[1] = "00 00 00 58 00 00 00 00 f9 52 00 0e 0a 00 00 80 06 00 00 8e 04 00 00 6d 0b 00 00 bf 0b 00 00 69 09 00 00 e6 09 00 00 b1 03 00 00 9f 0d 00 00 42 0a 00 00 46 08 00 00 d5 0c 00 00 f5 05 00 00 e8 02 00 00 8b 44 86 da f3 12 14 00 04 0e 00 00 a3 07 00 00 3f 04 00 00 98 00 00 00 3a"
				.toCharArray();

		syncBlock(2, images);
	}

}

class BlockTable {
	BlockList htable[] = null;
	int capacity;

	public BlockTable(int capacity) {
		this.htable = new BlockList[capacity];
		this.capacity = capacity;
	}

	public void put(Block block) {
		int idx = toindex(block.hash);

		if (htable[idx] == null)
			htable[idx] = new BlockList();

		htable[idx].addBlockToHead(block);
	}

	public void putWithExistCheck(Block block) {
		int idx = toindex(block.hash);

		if (htable[idx] == null)
			htable[idx] = new BlockList();

		Block rblock = htable[idx].head;
		while (rblock != null) {
			if (rblock.hash == block.hash) {
				rblock.nexist++;
				return;
			}
			rblock = rblock.next;
		}

		block.nexist++;
		htable[idx].addBlockToHead(block);
	}

	public Block get(int hash) {
		int idx = toindex(hash);

		if (htable[idx] == null)
			return null;

		Block rblock = htable[idx].head;
		while (rblock != null) {
			if (rblock.hash == hash)
				return rblock;
			rblock = rblock.next;
		}

		return null;
	}

	public int toindex(int hashcode) {
		return (hashcode & 0x7fffffff) % this.capacity;
	}

}

class BlockList {
	Block head;

	public void addBlockToHead(Block block) {
		if (head == null) {
			head = block;
			return;
		}

		block.next = head;
		head = block;
	}
}

class Block {
	static final int MAX_N_TRAN = 0xf;
	static final int MAX_TRAN_ID = 0xf;

	static final int MAX_N_CHILD = 0x14;

	int id;

	int hash, phash;

	int transactions[] = null;
	int tidx2amt[] = null;

	int nchild = 0;
	Block childs[] = null;

	Block next;// for hashtable

	int nexist = 0;

	public Block(int id, int hash, int phash, int trans[]) {
		this.id = id;

		this.hash = hash;
		this.phash = phash;

		this.transactions = trans;
		tidx2amt = new int[MAX_TRAN_ID];
		for (int t = 0; t < this.transactions.length; t++) {
			int tid = (trans[t] >> 24) & 0xf;
			int tamt = trans[t] & 0xfff;
			tidx2amt[tid] = tamt;
		}

		childs = new Block[MAX_N_CHILD];
	}

	public boolean addChild(Block block) {
		if (nchild >= MAX_N_CHILD) {
			System.err.println("parent = " + this.hash + ", child = " + block.hash);
			return false;
		}
		childs[nchild++] = block;
		return true;
	}
}
