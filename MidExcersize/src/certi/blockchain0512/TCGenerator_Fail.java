package certi.blockchain0512;

import java.util.HashSet;
import java.util.Iterator;

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

	private static int pseudo_rand(int max) {
		seed = (int) (((long) seed * 1103515245 + 12345) & 0xFFFF);
		return seed % max;
	}

	private static int[] generateIBlock() {
		int chunk[] = null;
		int random = pseudo_rand((int) Math.pow(2, 15));
		int transN = pseudo_rand(MAX_TRANS_N);
		if (transN == 0)
			transN = 1;

		int nchunk = 1 + 1 + transN;
		int cidx = 1;
		chunk = new int[nchunk];
		chunk[cidx++] = (random << 16 | transN);

		HashSet<Integer> set = new HashSet<Integer>();
		for (int n = 0; n < transN; n++, cidx++) {
			int transID = pseudo_rand(transN + transN / 2);
			while (set.contains(transID)) {
				transID = pseudo_rand(transN + transN / 2);
			}
			set.add(transID);
			int transAmt = pseudo_rand((int) Math.pow(2, 23));
			chunk[cidx] = (transID << 24 | transAmt);
		}

		return chunk;
	}

	private static int generateHierarchy(Block blocks[]) {
		if (blocks.length == 1)
			return 0;

		HashSet<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < blocks.length; i++)
			set.add(i);

		int blkN = blocks.length, madeN = 0;
		boolean made[] = new boolean[blkN];
		int ridx = pseudo_rand(blkN - 1);
		blocks[ridx].setParentHash(0);
		made[ridx] = true;
		madeN++;

		set.remove(ridx);

		int pidx = ridx;
		while (madeN < blkN) {
			int phash = blocks[pidx].hash;
			// SET NUMBER OF CHILDS
			int childN = pseudo_rand(MAX_CHILD > blkN - madeN ? blkN - madeN : MAX_CHILD);
			if (childN == 0)
				childN = 1;

			// ADD CHILDS TO SELCTED PARENT
			for (int i = 0; i < childN && madeN < blkN; i++) {
				int iter = 0, cidx = 0;
				while (iter < 1000) {
					cidx = pseudo_rand(blkN - 1);
					if (!made[cidx] && cidx != pidx)
						break;
					iter++;
				}

				if (iter == 1000) {
					Iterator<Integer> iteration = set.iterator();
					while (iteration.hasNext() && i < childN) {
						cidx = iteration.next();
						blocks[cidx].setParentHash(phash);
						made[cidx] = true;
						madeN++;
						blocks[pidx].addChild(blocks[cidx]);
						i++;
						set.remove(cidx);
					}
				} else {
					blocks[cidx].setParentHash(phash);
					made[cidx] = true;
					madeN++;
					blocks[pidx].addChild(blocks[cidx]);
					i++;
					set.remove(cidx);
				}
			}

			// SELECT A NODE AS PARENT FROM ALREADY MADE NODES
			if (madeN < blkN) {
				int tpidx = pseudo_rand(blkN - 1);
				while (!made[tpidx])
					tpidx = pseudo_rand(blkN - 1);
				pidx = tpidx;
			}
		}

		return ridx;
	}

	public static BlockImage[] generateBlockImages() {
		int totalblkN = 0, minblkN = Integer.MAX_VALUE;
		int blkimgN = pseudo_rand(MAX_BLOCK_IMG_N);
		if (blkimgN == 0)
			blkimgN = 1;
		int blkNs[] = new int[blkimgN];
		BlockImage bimages[] = new BlockImage[blkimgN];

		for (int i = 0; i < blkimgN; i++) {
			int n = pseudo_rand(MAX_BLOCK_N);
			if (n == 0)
				n = 1;
			blkNs[i] = n;
			totalblkN = totalblkN + n;
			if (n < minblkN)
				minblkN = n;
		}

		int iblocks[][] = new int[totalblkN][];
		for (int i = 0; i < totalblkN; i++) {
			iblocks[i] = generateIBlock();
		}

		int dupN = 0, dpn = 0;
		boolean preadded[] = new boolean[totalblkN];
		HashSet<Integer> preset = null;

		for (int i = 0; i < blkimgN; i++) {
			Block blocks[] = new Block[blkNs[i]];
			boolean curadded[] = new boolean[totalblkN];

			HashSet<Integer> curset = new HashSet<Integer>();

			for (int j = 0; j < blkNs[i]; j++) {
				int bidx = 0, iterN = 0;

				// TRY RANDOM
				while (iterN < 1000) {
					bidx = pseudo_rand(totalblkN);
					iterN++;
					if (dpn < dupN) {
						if (!curadded[bidx] && preadded[bidx]) {
							dpn++;
							break;
						}
					} else {
						if (!curadded[bidx])
							break;
					}
				}

				// JUST SEQUENTIAL, NOT RANDOM
				if (iterN == 1000) {
					if (dpn < dupN) {
						for (int t = 0; t < totalblkN && j < blkNs[i] && dpn < dupN; t++) {
							if (!curset.contains(t)) {
								if (preset.contains(t)) {
									bidx = t;
									curset.add(bidx);
									curadded[bidx] = true;
									blocks[j] = new Block(j, iblocks[bidx]);
									j++;
									dpn++;
								}
							}
						}
					} else {
						for (int t = 0; t < totalblkN && j < blkNs[i]; t++) {
							if (!curset.contains(t)) {
								bidx = t;
								curset.add(bidx);
								curadded[bidx] = true;
								blocks[j] = new Block(j, iblocks[bidx]);
								j++;
							}
						}
					}
				} else {
					curset.add(bidx);
					curadded[bidx] = true;
					blocks[j] = new Block(j, iblocks[bidx]);
				}
			}

			if (i != 0) {
				dpn = 0;
				for (int c = 0; c < curadded.length; c++) {
					if (curadded[c])
						preadded[c] = true;
				}
				preset = curset;
				dupN = pseudo_rand(minblkN);
				if (dupN == 0) {
					dupN = minblkN / 2;
				}
			}

			int ridx = generateHierarchy(blocks);
			bimages[i] = new BlockImage(blocks, ridx);
		}

		return bimages;
	}

	public static void main(String args[]) {
		
		BlockImage bimages[] = generateBlockImages();

		if (!validateBlockUniqueness(bimages))
			System.exit(-1);

		for (int i = 0; i < bimages.length; i++) {
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
						if (ablock.equalToByHash(bblock)) {
							if (!ablock.equalToByContent(bblock))
								return false;
							else
								System.out.println(">>>>> EQUALS : " + i + "," + b + "=" + ii + "," + bb);
						}
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

		System.out.println(node.id + " (parenthash : " + node.parenthash + ", hash : " + node.hash + ")");
		System.out.println("childs : ");
		for (int i = 0; i < node.childN; i++)
			System.out.println("- " + node.childs[i].id + "(parenthash : " + node.childs[i].parenthash + ", hash : "
					+ node.childs[i].hash + ")");
		System.out.println();

		for (int i = 0; i < node.childN; i++)
			traverse(node.childs[i]);
	}

}

class Block {
	final int MAX_CHILD = TCGenerator.MAX_CHILD;

	int id = -1, hash = -1, parenthash = -1;
	int iblock[];

	Block next;

	int childN = 0;
	Block childs[] = new Block[MAX_CHILD];

	public Block(int id, int block[]) {
		this.id = id;
		this.iblock = block;
		this.parenthash = iblock[0];
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
		this.parenthash = hash;
		this.hash = calcHash();
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
