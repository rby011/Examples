package certi.blockchain0512.gen;

import java.util.HashSet;

public class BlockChainGenerator {
	static final int MAX_BLOCK_IMAGE_N = 5;
	public static final int MAX_BLOCK_N = 20000;

	static int HACK_START = 0;
	static final float HACK_RATIO = 0.3f;
	static final int HACK_PROBABILITY = 3; // 25%

	static int MISSING_START = 0;
	static final float MISSING_START_RATIO = 0.7f;
	static final int MISSING_PROBABILITY = 2; // 33%

	public static BlockImage[] generateBlockChain() {
		int nimage = Random.pseudo_rand(MAX_BLOCK_IMAGE_N);
		if (nimage < 3)
			nimage = 3;
		if (nimage % 2 == 0)
			nimage = nimage + 1;

		int nblock = Random.pseudo_rand(MAX_BLOCK_N);
		if (nblock < MAX_BLOCK_N / 2)
			nblock = MAX_BLOCK_N / 2;
		nblock = nblock < nimage ? nimage : nblock;

		MISSING_START = (int) (nimage * nblock * MISSING_START_RATIO);
		HACK_START = (int) (nimage * nblock * HACK_RATIO);

		BlockImage images[] = new BlockImage[nimage];
		for (int i = 0; i < images.length; i++)
			images[i] = new BlockImage();

		// ROOT FOR EACH IMAGE
		Block root = Block.generateValidBlock();
		for (int i = 0; i < images.length; i++) {
			Block toadd = Block.copyBlock(root);
			images[i].setRoot(toadd);
		}

		// SET FOR ODD PIDX
		HashSet<Integer> pset = new HashSet<Integer>();

		int mcnt = 0, hcnt = 0;
		for (int i = 1; i < nblock; i++) {
			Block block = Block.generateValidBlock();
			Block fblock = null;

			int pbidx = 0, iter = 0;
			while (iter < 1000) {
				// PARENT BLOCK ID
				iter++;
				pbidx = Random.pseudo_rand(i);
				if (!pset.contains(pbidx))
					break;
			}

			int miss = 1;
			for (int j = 0; j < nimage; j++, mcnt++, hcnt++) {
				if (mcnt > MISSING_START)
					miss = Random.pseudo_rand(192939192) % MISSING_PROBABILITY;
				if (miss != 0) {
					if (hcnt > HACK_START) {
						int hacked = Random.pseudo_rand(192939192) % HACK_PROBABILITY;
						if (hacked == 0)
							fblock = Block.generateInvalidBlock(block.id);
						else
							fblock = block;
					} else {
						fblock = block;
					}
					if (!images[j].addToParent(Block.copyBlock(fblock), pbidx)) {
						// pbidx 가 없는 경우가 정말로 존재함. .어느 경우지?? missing 된 경우
						System.out.println(fblock.id + " FAIL TO ADD INTO - " + pbidx + " @ IMAGE -" + j);
						pset.add(pbidx);
					}
				}
			}
		}

		return images;
	}

	public static boolean validateBlockChain(BlockImage images[]) {
		Hashtable htable = new Hashtable(22691);
		for (int i = 0; i < images.length; i++) {
			checkBlocks(images[i].root, htable);
			// if (!checkBlocks(images[i].root, htable))
			// return false;
		}
		return true;
	}

	private static boolean checkBlocks(Block node, Hashtable htable) {
		if (!htable.put(node))
			return false;

		for (int i = 0; i < node.childN; i++) {
			if (!checkBlocks(node.childs[i], htable)) {
				Block cblock = htable.get(node.childs[i].hash);
				System.out.println("!! BLOCK COLLISION : " + node.childs[i].id + "<->" + cblock.id);
			}
		}

		return true;
	}

	public static char[][] generateBlockImageString() throws Exception {
		// System.setOut(new PrintStream(new File("./data/output.txt")));

		// 1. 블록 체인 구성, 블록 구성, 블록간 계층 구조 구성, 해쉬 미계산 상태
		BlockImage images[] = generateBlockChain();

		// 2. 블록 이미지별 해쉬 계산
		for (int i = 0; i < images.length; i++)
			images[i].dohashing();

		// 3. 블록의 해쉬 값이 충돌 없는지 체크
		if (validateBlockChain(images)) {
			char imgChArr[][] = new char[images.length][];
			for (int i = 0; i < images.length; i++) {
				System.out.println("# IMAGE - " + i);
				images[i].printBlockTree();
				System.out.println(images[i].toString());
				System.out.println(images[i].toHexaString());
				imgChArr[i] = images[i].toHexaString().toCharArray();
			}
			return imgChArr;
		} else {
			System.out.println("TOTALLY BLOCK COLLISION!");
			return null;
		}
	}

	public static void main(String args[]) throws Exception {
		generateBlockImageString();
	}
}

class BlockImage {
	Block root;
	int size;

	public void setRoot(Block root) {
		this.root = root;
		size++;
	}

	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		stringTraverse(this.root, sbuf);
		return sbuf.toString();
	}

	public void stringTraverse(Block node, StringBuffer sbuf) {
		sbuf.append(node.toString() + "\n");
		for (int i = 0; i < node.childN; i++) {
			stringTraverse(node.childs[i], sbuf);
		}
	}

	public String toHexaString() throws Exception {
		StringBuffer sbuf1 = new StringBuffer();
		StringBuffer sbuf2 = new StringBuffer();

		hexaTraverse(this.root, sbuf1);
		int len = sbuf1.length() - 1;
		len = (len + 1) / 3;

		for (int r = 0; r < 4; r++) {
			int hexa = (len >> 24 - r * 8) & 0xff;
			String xstr = Integer.toHexString(hexa);
			if (xstr.length() == 1)
				xstr = "0" + xstr;
			sbuf2.append(xstr);
			sbuf2.append(" ");
		}

		sbuf2.append(sbuf1);

		return sbuf2.toString().trim();
	}

	public void hexaTraverse(Block node, StringBuffer sbuf) {
		sbuf.append(node.toHexaString());
		sbuf.append(" ");

		for (int i = 0; i < node.childN; i++) {
			hexaTraverse(node.childs[i], sbuf);
		}
	}

	public boolean addToParent(Block child, int pbidx) {

		Block pnode = findBlock(this.root, pbidx);

		if (pnode == null)
			return false;

		try {
			pnode.addToChild(child);
			size++;
			return true;
		} catch (Exception ex) {
			// ex.printStackTrace();
			// 선택된 parent 블록의 차일드가 꽉 차 있는 경우
			return false;
		}
	}

	public void dohashing() {
		dohashing(root, 0);
	}

	private void dohashing(Block node, int phash) {
		node.parentHash = phash;
		node.hash = node.hashcode();

		for (int i = 0; i < node.childN; i++)
			dohashing(node.childs[i], node.hash);
	}

	private Block findBlock(Block block, int id) {
		if (block.id == id)
			return block;

		for (int i = 0; i < block.childs.length; i++)
			if (block.childs[i] != null) {
				Block ret = findBlock(block.childs[i], id);
				if (ret != null)
					return ret;
			}

		return null;
	}

	public void printBlockTree() throws Exception {
		Queue queue = new Queue(this.size + 10);

		queue.enqueue(this.root);
		System.out.println(this.root.id + "<" + this.root.hash + ">" + "[R<" + this.root.parentHash + ">]");

		while (!queue.isempty()) {
			Block block = queue.dequeue();
			boolean haschild = false;
			for (int i = 0; i < block.childN; i++) {
				queue.enqueue(block.childs[i]);
				if (!block.childs[i].hacked)
					System.out.print(block.childs[i].id + "<" + block.childs[i].hash + ">" + "[" + block.id + "<"
							+ block.hash + ">] ");
				else {
					if (!block.hacked)
						System.out.print("*" + block.childs[i].id + "<" + block.childs[i].hash + ">" + "[" + block.id
								+ "<" + block.hash + ">] ");
					else
						System.out.print("*" + block.childs[i].id + "<" + block.childs[i].hash + ">" + "[*" + block.id
								+ "<" + block.hash + ">] ");

				}
				haschild = true;
			}
			if (haschild)
				System.out.println();
		}
	}

	class Queue {
		Block queue[] = null;
		int front = 0, tail = 0;

		Queue(int capacity) {
			queue = new Block[capacity];
		}

		public Block dequeue() throws Exception {
			if (isempty())
				throw new Exception();
			return queue[front++];
		}

		public void enqueue(Block block) throws Exception {
			if (isfull())
				throw new Exception();
			queue[tail++] = block;
		}

		public boolean isempty() {
			return front == tail;
		}

		public boolean isfull() {
			return tail == queue.length;
		}
	}
}

class Block {
	static int BLOCK_ID = 0;

	static final int MAX_CHILD_N = 20;

	static final int MAX_RANDOM = 0xffff;
	static final int MAX_TRASACTION_N = 0xf/* fff */;
	static final int MAX_TRANSACTION_ID = 0xf;
	static final int MAX_TRASACTION_AMOUNT = 0xff;

	int hash;
	int parentHash;

	int id;

	int random;// 2 BYTE
	int tranN;// 2 BYTE
	Transaction trans[];
	int childN = 0;
	Block childs[] = null;

	boolean hacked = false;

	private Block() {

	}

	public void addToChild(Block child) throws Exception {
		if (childN >= childs.length)
			throw new Exception();
		childs[childN++] = child;
	}

	public int hashcode() {
		int n = 2 + tranN;
		int integers[] = new int[n];
		integers[0] = parentHash;
		integers[1] = random << 16 | tranN;
		for (int i = 2; i < integers.length; i++)
			integers[i] = trans[i - 2].tranID << 24 | trans[i - 2].tranAmt;

		double a = 13 / 400;

		int hashcode = 1;
		int prime = 0;
		for (int i = 0; i < integers.length; i++) {
			prime = ((i + 1) * (i + 1) + (i + 1) + 41);
			hashcode = (hashcode << 5) + integers[i] * prime;
			// hashcode = (hashcode << 5) + integers[i];
		}

		return hashcode;
	}

	public String toString() {
		StringBuffer sbuf = new StringBuffer();

		sbuf.append("[B-ID : " + this.id + "]\n");
		sbuf.append(" -PHASH : " + this.parentHash + "\n");
		sbuf.append(" -RAND  : " + this.random + "\n");
		sbuf.append(" -NTRAN : " + this.tranN + "\n");
		for (int i = 0; i < this.tranN; i++)
			sbuf.append("  .TID : " + this.trans[i].tranID + "/ - TAM : " + this.trans[i].tranAmt + "\n");
		sbuf.append(" *HASH : " + this.hash + "\n");
		return sbuf.toString().trim();
	}

	public String toHexaString() {
		StringBuffer sbuf = new StringBuffer();

		int integers[] = new int[2];
		integers[0] = parentHash;
		integers[1] = random << 16 | tranN;
		for (int i = 0; i < integers.length; i++) {
			int integer = integers[i];
			for (int r = 0; r < 4; r++) {
				int hexa = (integer >> 24 - r * 8) & 0xff;
				String xstr = Integer.toHexString(hexa);
				if (xstr.length() == 1)
					xstr = "0" + xstr;
				sbuf.append(xstr);
				sbuf.append(" ");
			}
		}

		for (int i = 0; i < tranN; i++) {
			sbuf.append(trans[i].toHexaString());
			sbuf.append(" ");
		}

		return sbuf.toString().trim();
	}

	public boolean equalByHash(Block inblock) {
		return this.hash == inblock.hash;
	}

	public boolean equalByContent(Block inblock) {
		if (this.random != inblock.random || this.tranN != inblock.tranN)
			return false;
		for (int i = 0; i < this.tranN; i++) {
			if (this.trans[i].tranID != inblock.trans[i].tranID)
				return false;
			if (this.trans[i].tranAmt != inblock.trans[i].tranAmt)
				return false;
		}
		return true;
	}

	static Block copyBlock(Block src) {
		Block block = new Block();
		block.id = src.id;

		block.hash = src.hash;
		block.parentHash = src.parentHash;

		block.random = src.random;
		block.tranN = src.tranN;
		block.trans = src.trans;
		block.childs = new Block[MAX_CHILD_N];

		block.hacked = src.hacked;

		return block;
	}

	static Block generateInvalidBlock(int id) {
		Block block = new Block();

		Transaction trans[] = new Transaction[1];
		Transaction tran = new Transaction(1, 100);
		trans[0] = tran;

		block.id = id;
		block.random = 100;
		block.tranN = 1;
		block.trans = trans;
		block.childs = new Block[MAX_CHILD_N];

		block.hacked = true;

		return block;

	}

	static Block generateValidBlock() {
		int random = Random.pseudo_rand(MAX_RANDOM);
		int tranN = Random.pseudo_rand(MAX_TRASACTION_N);

		Transaction trans[] = new Transaction[tranN];
		for (int i = 0; i < tranN; i++) {
			int tranID = Random.pseudo_rand(MAX_TRANSACTION_ID);
			int tranAmt = Random.pseudo_rand(MAX_TRASACTION_AMOUNT);
			trans[i] = new Transaction(tranID, tranAmt);
		}

		Block block = new Block();
		block.id = BLOCK_ID++;
		block.random = random;
		block.tranN = tranN;
		block.trans = trans;
		block.childs = new Block[MAX_CHILD_N];

		block.hacked = false;

		return block;
	}
}

class Transaction {
	int tranID;// 1 BYTE
	int tranAmt;// 3 BYTE

	Transaction(int id, int amount) {
		this.tranID = id;
		this.tranAmt = amount;
	}

	public int toInteger() {
		int integer = tranID << 24 | tranAmt;
		return integer;
	}

	public String toHexaString() {
		StringBuffer sbuf = new StringBuffer();
		int integer = toInteger();
		for (int r = 0; r < 4; r++) {
			int hexa = integer >> (24 - r * 8) & 0xff;
			String xstr = Integer.toHexString(hexa);
			if (xstr.length() == 1)
				xstr = "0" + xstr;
			sbuf.append(xstr);
			sbuf.append(" ");

		}
		return sbuf.toString().trim();
	}
}

class Random {
	static int seed = 1234567;

	public static int pseudo_rand(int max) {
		seed = (int) (((long) seed * 1103515245 + 12345) & 0xFFFF);
		return seed % max;
	}
}

class Hashtable {
	Block table[] = null;
	int capacity = 0;
	int size;

	public Hashtable(int capacity) {
		this.table = new Block[capacity];
		this.capacity = capacity;
	}

	public Block get(int hash) {
		int idx = toIndex(hash);
		return table[idx];
	}

	// in order to check block collision
	public boolean put(Block block) {
		int idx = toIndex(block);

		if (table[idx] == null) {
			table[idx] = block;
			size++;
			return true;
		}

		boolean isequalbyhash = table[idx].equalByHash(block);
		boolean isequalbycontent = table[idx].equalByContent(block);

		if (isequalbyhash && isequalbycontent)
			return true;

		// block collision
		if (isequalbyhash && !isequalbycontent)
			return false;

		// hashtable collision
		if (!isequalbyhash && !isequalbycontent)
			;

		// impossible case
		if (!isequalbyhash && isequalbycontent)
			;

		return true;
	}

	public boolean contains(Block block) {
		int idx = toIndex(block);

		if (table[idx] == null)
			return false;

		if (table[idx].equalByHash(block) && table[idx].equalByContent(block))
			return true;

		return false;
	}

	public int toIndex(Block block) {
		return (block.hash & 0x7fffffff) % this.capacity;
	}

	public int toIndex(int hashcode) {
		return (hashcode & 0x7fffffff) % this.capacity;
	}
}
