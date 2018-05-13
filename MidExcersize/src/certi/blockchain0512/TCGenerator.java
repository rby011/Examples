package certi.blockchain0512;

import java.util.HashSet;

public class TCGenerator {
	/**
	 * 조정 불가 인자
	 */
	// 한 블록이 갖을 수 있는 최대 거래 수
	static final int MAX_TRANS_N = 14;
	/**
	 * 조정 가능한 인자
	 */
	// 한 블록이 갖을 수 있는 최대 자식 블록 수
	static final int MAX_CHILD = 5;
	// 한 블록 이미지가 가질 수 있는 최대 블록 수
	static final int MAX_BLOCK_N = 10;
	// 최대 서버 수 , 최대 블록 이미지 수
	static final int MAX_BLOCK_IMG_N = 5;

	static int seed = 12345;

	static int pseudo_rand(int max) {
		seed = (int) (((long) seed * 1103515245 + 12345) & 0xFFFF);
		return seed % max;
	}

	public static int generateBlkImageN() {
		int n = pseudo_rand(MAX_BLOCK_IMG_N);
		if (n == 0)
			return 2;
		return n;
	}

	public static int generateRandom() {
		return pseudo_rand((int) Math.pow(2, 15));
	}

	public static int generateTransN() {
		return pseudo_rand(MAX_TRANS_N);
	}

	public static int generateTransID(int transN) {
		return pseudo_rand(transN + transN / 2);
	}

	public static int generateTransAmt() {
		return pseudo_rand((int) Math.pow(2, 23));
	}

	public static int generateBlockN() {
		int n = pseudo_rand(MAX_BLOCK_N);
		return n == 0 ? 1 : n;
	}

	public static int calcHash(int block[]) {
		int hash = 1;
		for (int i = 0; i < block.length; i++) {
			hash = hash * 32 + block[i];
		}
		return hash;
	}

	public static int[] generateOneBlockWithoutHash() {

		int chunk[] = null;
		int random = generateRandom();
		int transN = generateTransN();

		int nchunk = 1 + 1 + transN;
		int cidx = 1;
		chunk = new int[nchunk];
		chunk[cidx++] = (random << 16 | transN);

		HashSet<Integer> set = new HashSet<Integer>();
		for (int n = 0; n < transN; n++, cidx++) {
			int transID = generateTransID(transN);
			while (set.contains(transID)) {
				transID = generateTransID(transN);
			}
			set.add(transID);
			int transAmt = generateTransN();
			chunk[cidx] = (transID << 24 | transAmt);
		}

		return chunk;
	}

	public static String convertToString(int integers[]) {
		StringBuffer sbuf = new StringBuffer();

		for (int i = 0; i < integers.length; i++) {
			int integer = integers[i];
			for (int r = 0; r < 4; r++) {
				int chunk = (integer >> (24 - r * 8)) & 0xff;
				String chunk_str = Integer.toHexString(chunk);

				if (chunk_str.length() > 2) {
					System.out.println("ERROR!!!");
					System.exit(-1);
				}

				sbuf.append(chunk_str);
				sbuf.append(" ");
			}
		}

		return sbuf.toString().trim();
	}

	public static BlockImage generateOneBlockImage() {
		BlockImage bimage = null;

		// GENRERATE BLOCK COUNT
		int bidx = 0, blockN = generateBlockN();
		Block bidx_table[] = new Block[blockN];
		int added = 0;
		boolean addedToParent[] = new boolean[blockN];

		// GENERATE BLOCK
		for (int i = 0; i < blockN; i++) {
			int iblock[] = generateOneBlockWithoutHash();
			Block block = new Block(bidx, iblock);
			bidx_table[bidx] = block;
			bidx++;
		}

		// ROOT BLOCK
		int rbidx = pseudo_rand(blockN);
		bidx_table[rbidx].setParentHash(0);
		addedToParent[rbidx] = true;
		added = 1;

		bimage = new BlockImage(bidx_table, rbidx);

		int pidx = rbidx;
		while (added < blockN) {
			int pHash = calcHash(bidx_table[pidx].iblock);

			// SLECT CHILDS FROM NOT-YET ADDED SET ADD THEM
			int childN = pseudo_rand(blockN - added);
			if (childN == 0)
				childN = 1;

			HashSet<Integer> set = new HashSet<Integer>();
			for (int c = 0; c < childN; c++) {
				int cbidx = 0, iterN = 0;
				while (true) {
					cbidx = pseudo_rand(blockN);
					iterN++;
					if (!set.contains(cbidx) && !addedToParent[cbidx] && iterN < 1000 && cbidx != pidx)
						break;
				}

				if (iterN == 1000)
					break;

				// ADD CHILD INTO PARENT
				if (bidx_table[pidx].addChild(bidx_table[cbidx])) {
					// SET PARENT HASH TO CHILD
					bidx_table[cbidx].setParentHash(pHash);
					added++;
					addedToParent[cbidx] = true;
				}
			}

			// SELECT PARENT FROM ALREADY ADDED SET
			if (added < blockN) {
				int tpidx = pseudo_rand(blockN);
				while (!addedToParent[tpidx]) {
					tpidx = pseudo_rand(blockN);
				}
				pidx = tpidx;
			}
		}

		return bimage;
	}

	public static void main(String args[]) {
		int blkimgN = generateBlkImageN();
		BlockImage bimages[] = new BlockImage[blkimgN];

		for (int i = 0; i < blkimgN; i++) {
			BlockImage bimage = generateOneBlockImage();
			bimages[i] = bimage;
		}

		if (!validateBlockUniqueness(bimages))
			System.exit(-1);

		for (int i = 0; i < blkimgN; i++) {
			BlockImage bimage = bimages[i];
			System.out.println("[BLOCK IMAGE - " + i + "]");
			System.out.println("# TREE STRUCTURE : ");
			bimage.printTree();
			System.out.println("# BLOCKS");
			bimage.printBlocks();
			System.out.println("# HEXA STRING : ");
			System.out.println(bimage.toHexString());
			System.out.println();
			System.out.println();
		}

	}

	public static boolean validateBlockUniqueness(BlockImage images[]) {
		for (int i = 0; i < images.length; i++) {
			Block blocks[] = images[i].blocks;
			for (int b = 0; b < blocks.length; b++) {
				Block ablock = blocks[b];
				for (int ii = 0; ii < images.length; ii++) {
					for (int bb = 0; bb < images[ii].blocks.length && !(i == ii && b == bb); bb++) {
						Block bblock = images[ii].blocks[bb];
						if (ablock.equalToByContent(bblock))
							if (!ablock.equalToByHash(bblock))
								return false;
					}
				}
			}
		}
		return true;
	}

}

class BlockImage {

	int len = 0;
	int rootID = 0;
	Block blocks[] = null;

	BlockImage(Block blocks[], int rootID) {
		this.blocks = blocks;
		this.rootID = rootID;
		for (int i = 0; i < blocks.length; i++) {
			len = len + blocks[i].iblock.length * 4;
		}
	}

	public Block getRoot() {
		return blocks[this.rootID];
	}

	public String toHexString() {
		StringBuffer sbuf = new StringBuffer();

		for (int r = 0; r < 4; r++) {
			int chunk = len >> (24 - 8 * r) & 0xff;
			sbuf.append(Integer.toHexString(chunk));
			sbuf.append(" ");
		}

		for (int i = 0; i < blocks.length; i++) {
			sbuf.append(blocks[i].toHexString());
			sbuf.append(" ");
		}

		return sbuf.toString().trim();
	}

	public void printBlocks() {
		for (int i = 0; i < blocks.length; i++) {
			System.out.println(blocks[i].toString());
		}
	}

	public void printTree() {
		traverse(getRoot());
	}

	private void traverse(Block node) {
		if (node.childN == 0)
			return;

		System.out.println(node.id + " (parenthash : " + node.getHash() + ")");
		System.out.println("childs : ");
		for (int i = 0; i < node.childN; i++)
			System.out.println("- " + node.childs[i].id + "(parenthash : " + node.childs[i].getHash() + ")");
		System.out.println();

		for (int i = 0; i < node.childN; i++)
			traverse(node.childs[i]);
	}

}

class Block {
	final int MAX_CHILD = TCGenerator.MAX_CHILD;

	int id;
	int hash;
	int iblock[];

	Block next;

	int childN = 0;
	Block childs[] = new Block[MAX_CHILD];

	public Block(int id, int block[]) {
		this.id = id;
		this.iblock = block;
		this.hash = this.calcHash();
	}

	public boolean addChild(Block child) {
		if (childN >= MAX_CHILD)
			return false;

		if (equalToByContent(child))
			return false;

		childs[childN++] = child;
		return true;
	}

	private int calcHash() {
		int hash = 1;
		for (int i = 0; i < iblock.length; i++) {
			hash = hash * 32 + iblock[i];
		}
		return hash;
	}

	public void setParentHash(int hash) {
		this.iblock[0] = hash;
	}

	public boolean equalToByContent(Block oblock) {
		int block2[] = oblock.iblock;
		for (int i = 1; i < block2.length; i++) {
			if (this.iblock[i] != block2[i])
				return false;
		}
		return true;
	}

	public boolean equalToByHash(Block oblock) {
		return this.hash == oblock.hash;
	}

	public int getTransN() {
		int chunk = iblock[1];
		return chunk & 0x0000ffff;
	}

	public int getRandom() {
		int chunk = iblock[1];
		return (chunk >> 16) & 0x0000ffff;
	}

	public int getTransID(int nst) {
		if (iblock.length < nst + 2)
			return -1;

		int chunk = iblock[2 + nst];
		return (chunk >> 24) & 0x000000ff;
	}

	public int getTransAmt(int transidx) {
		if (iblock.length < transidx + 2)
			return -1;

		int chunk = iblock[2 + transidx];
		return (chunk) & 0x00ffffff;

	}

	public int getHash() {
		int chunk = iblock[0];
		return chunk;
	}

	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		System.out.println("<Block ID : " + this.id + ">");
		System.out.println("* Parent Hash : " + this.getHash());
		System.out.println("* Hash : " + this.hash);
		System.out.println("* Random : " + this.getRandom());
		System.out.println("* TransN : " + this.getTransN());
		for (int i = 0; i < this.getTransN(); i++) {
			System.out.println("** TransID (Amount) :" + this.getTransID(i) + "(" + this.getTransAmt(i) + ")");
		}
		System.out.println("* HEXA STRING : " + this.toHexString());
		return sbuf.toString();
	}

	public String toHexString() {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < iblock.length; i++) {
			int integer = iblock[i];
			for (int r = 0; r < 4; r++) {
				int hex = (integer >> (24 - 8 * r)) & 0x000000ff;
				sbuf.append(Integer.toHexString(hex));
				sbuf.append(" ");
			}
		}
		return sbuf.toString().trim();
	}
}
