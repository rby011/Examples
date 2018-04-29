package certi.memo.d0428.list;

public class UserSolution {
	MemoIndexList midx_table[][] = null;
	Memo m_table[] = null;

	public void init(int screensize) {
		midx_table = new MemoIndexList[screensize][screensize];
		m_table = new Memo[screensize * screensize];/** ? **/
	}

	// str : ends with '\0'
	// mid : starts from 0
	public void create(int mid, int y, int x, int height, int width, char str[]) {
		Memo memo = new Memo(y, x, height, width, str);
		m_table[mid] = memo;
		for (int ry = y; y < y + height; ry++) {
			for (int rx = x; x < x + width; rx++) {
				MemoIndex midx = new MemoIndex(mid);
				if (midx_table[ry][rx] == null)
					midx_table[ry][rx] = new MemoIndexList();
				midx_table[ry][rx].addMemoIndexToHead(midx);
			}
		}
	}

	public void select(int mid) {
		if (m_table[mid] != null) {
			int x = m_table[mid].x;
			int y = m_table[mid].y;
			int height = m_table[mid].height;
			int width = m_table[mid].width;
			for (int ry = y; ry < y + height; ry++) {
				for (int rx = x; rx < x + width; rx++) {
					if (midx_table[ry][rx] != null) {
						midx_table[ry][rx].moveToFirst(mid);
					} else {
						// must be error!
					}
				}
			}
		} else {
			// error??,
		}
	}

	public void move(int mid, int y, int x) {
		Memo memo = m_table[mid];
		if (memo != null && memo.y != y && memo.x != x) {
			// 혹시 화면에 보여지는 메모만 선택 가능???
			int oy = memo.y, ox = memo.x;
			int height = memo.height, width = memo.width;
			for (int ry = oy; ry < oy + height; ry++) {
				for (int rx = ox; rx < ox + width; rx++) {
					midx_table[ry][rx].removeMemoIndex(mid);
				}
			}

			memo.y = y;
			memo.x = x;

			for (int ry = y; ry < y + height; ry++) {
				for (int rx = x; rx < x + width; rx++) {
					midx_table[ry][rx].addMemoIndexToHead(new MemoIndex(mid));
				}
			}
		} else {
			// error??
		}
	}

	public void change(int mid, int nheight, int nwidth) {
		Memo memo = m_table[mid];
		if (memo != null && memo.height != nheight && memo.width != nwidth) {
			int hdiff = memo.height - nheight;
			int wdiff = memo.width - nwidth;

			if (hdiff < 0 && wdiff < 0) {
				int sx = memo.x + memo.width + wdiff;
				int ex = memo.x + memo.width;

				for (int y = memo.y; y < memo.y + memo.height; y++) {
					for (int x = sx; x < ex; x++) {
						midx_table[y][x].removeMemoIndex(mid);
					}
				}

				int sy = memo.y + memo.height + hdiff;
				int ey = memo.y + memo.height;

				for (int y = sy; y < ey; y++) {
					for (int x = memo.x; x < sx; x++) {
						midx_table[y][x].removeMemoIndex(mid);
					}
				}
			} else if (hdiff > 0 && wdiff > 0) {
				int sx = memo.x + memo.width;
				int ex = memo.x + memo.width + wdiff;

				for (int y = memo.y; y < memo.y + memo.height; y++) {
					for (int x = sx; x < ex; x++) {
						midx_table[y][x].addMemoIndexToHead(new MemoIndex(mid));
					}
				}

				int sy = memo.y + memo.height;
				int ey = memo.y + memo.height + hdiff;

				for (int y = sy; y < ey; y++) {
					for (int x = memo.x; x < memo.x + memo.width; x++) {
						midx_table[y][x].addMemoIndexToHead(new MemoIndex(mid));
					}
				}
			} else if (hdiff > 0 && wdiff < 0) {
				int sx = memo.x + memo.width + wdiff;
				int ex = memo.x + memo.width;

				for (int y = memo.y; y < memo.y + memo.height; y++) {
					for (int x = sx; x < ex; x++) {
						midx_table[y][x].removeMemoIndex(mid);
					}
				}

				int sy = memo.y + memo.height;
				int ey = memo.y + memo.height + hdiff;

				for (int y = sy; y < ey; y++) {
					for (int x = memo.x; x < memo.x + memo.width + wdiff; x++) {
						midx_table[y][x].addMemoIndexToHead(new MemoIndex(mid));
					}
				}
			} else if (hdiff < 0 && wdiff > 0) {
				int sx = memo.x + memo.width;
				int ex = memo.x + memo.width + wdiff;

				for (int y = memo.y; y < memo.y + memo.height + hdiff; y++) {
					for (int x = sx; x < ex; x++) {
						midx_table[y][x].addMemoIndexToHead(new MemoIndex(mid));
					}
				}

				int sy = memo.y + memo.height + hdiff;
				int ey = memo.y + memo.height;

				for (int y = sy; y < ey; y++) {
					for (int x = memo.x; x < memo.x + memo.width; x++) {
						midx_table[y][x].removeMemoIndex(mid);
					}
				}
			}

		} else {
			// error??
		}
	}

	// res : 5x5
	public void getScreenContext(int y, int x, char res[][]) {
		for (int ry = y; ry < y + 5; ry++) {
			for (int rx = x; rx < x + 5; rx++) {
				MemoIndex firstidx = midx_table[ry][rx].head;
				if (firstidx == null) {
					res[ry][rx] = '\0';
				} else {
					Memo firstmemo = m_table[firstidx.mid];
					if (firstmemo != null) {
						int ich = firstmemo.getchar(ry, rx);
						if (ich != -1) {
							res[ry][rx] = (char) ich;
						} else {
							// error!!
						}
					} else {
						// error!!
					}
				}
			}
		}
	}
}

class MemoIndexList {
	MemoIndex head;

	public void addMemoIndexToHead(MemoIndex midx) {
		if (head == null) {
			this.head = midx;
			return;
		}

		midx.next = head;
		head = midx;
	}

	public boolean moveToFirst(int mid) {
		boolean found = false;
		if (head == null)
			return found;
		if (this.head.mid == mid) {
			return found;
		}

		MemoIndex idx_node = head;
		MemoIndex pidx_node = null;
		while (idx_node != null) {
			if (idx_node.mid == mid) {
				found = true;
				break;
			}
			pidx_node = idx_node;
			idx_node = idx_node.next;
		}

		if (found) {
			pidx_node.next = idx_node.next;
			idx_node.next = head;
			head = idx_node;
		}

		return found;
	}

	public boolean removeMemoIndex(int mid) {
		boolean found = false;
		if (head == null)
			return found;

		if (head.mid == mid) {
			head = head.next;
			return found;
		}

		MemoIndex idx_node = head;
		MemoIndex pidx_node = null;
		while (idx_node != null) {
			if (idx_node.mid == mid) {
				found = true;
				break;
			}
			pidx_node = idx_node;
			idx_node = idx_node.next;
		}
		if (found)
			pidx_node.next = idx_node.next;

		return found;
	}

}

class MemoIndex {
	int mid;

	MemoIndex next;

	public MemoIndex(int mid) {
		this.mid = mid;
	}
}

class Memo {
	int y, x;
	int height, width;
	char str[];

	public Memo(int y, int x, int height, int width, char str[]) {
		this.y = y;
		this.x = x;
		this.height = height;
		this.width = width;
		this.str = new char[str.length];
		copy(this.str, str);
	}

	private void copy(char dst[], char src[]) {
		int i = 0;
		for (i = 0; i < dst.length && src[i] != '\0'; i++) {
			dst[i] = src[i];
		}
		dst[i] = '\0';
	}

	public int getchar(int y, int x) {
		if (y >= this.y + this.height || x >= this.x + this.width)
			return -1;

		// 상대좌표 구하기
		int my = y - this.y;
		int mx = x - this.x;

		// 상대좌표로 인덱스 구하기
		int midx = this.width * my + mx;

		return this.str[midx];
	}
}
