package data.structure.hashlist;

/**
 * <PRE>
 * 1. 문제 해결 포인트
 * 동일 key 에 대한 put 연산이 update 가 아닌 add 성격을 갖는 경우, 
 * collision 발생으로 인한 동일 index
 * 위치로의 추가와 해당 key 에 상응하는 아이템의 추가를 구분하고자 함
 * 
 * 2. 문제 해결 방안 
 * list 수평/수직으로 구성하고 
 * 수직 방향은 동일 key 를 갖는 아이템을 추가하는 것으로 하고
 * 수평 방향은 collision 발생으로 인한 아이템을 추가하는 것으로 구별함
 * </PRE>
 */
public class Hashtable {

	// capacity = n^2 + n + 41
	int capacity = 0;

	ItemList table[] = null;

	public Hashtable(int capacity) {
		table = new ItemList[capacity];
		for (int i = 0; i < table.length; i++)
			table[i] = new ItemList();
		this.capacity = capacity;
	}

	public void put(char key[], int id) {
		int index = toindex(key);
		
		// NEW FOR THIS INDEX : MOST FREQUENT CASE
		if (table[index].head == null) {
			table[index].addItemToHead(new Item(id, key));
			return;
		}

		// ADD ITEM WITH THE SAME KEY TO CORESSPONDING LIST
		ItemList col_list = table[index];
		ItemList p_col_list = null;
		while (col_list != null) {
			if (col_list.head != null && col_list.head.equalto(key)) {
				col_list.addItemToHead(new Item(id, key));
				return;
			}
			p_col_list = col_list;
			col_list = col_list.lnext;
		}

		// ADD ITEMLIST AT COLLISION
		if (p_col_list != null) {
			// create a new item list that contain the new item
			ItemList n_item_list = new ItemList();
			n_item_list.addItemToHead(new Item(id, key));

			// ATTACH A NEW KIND OF LIST ITEM AT THE TAIL
			p_col_list.lnext = n_item_list;
		}
	}

	// RETURN ITEMS THAT HAS A SAME KEY
	public ItemList get(char key[]) {
		int index = toindex(key);

		ItemList list = table[index];
		while (list != null) {
			if (list.head.equalto(key))
				return list;
			list = list.lnext;
		}

		return null;
	}

	public int toindex(char key[]) {
		return (hashcode(key) & 0x7fffffff) % this.capacity;
	}

	public int hashcode(char key[]) {
		int hashcode = 1;
		for (int i = 0; i < key.length; i++) {
			hashcode = hashcode * 31 + key[i];
		}
		return hashcode;
	}

	public void printTable() {
		for (int c = 0; c < this.capacity; c++) {
			ItemList list = table[c];
			System.out.println("# INDEX - " + c);
			int itemid = 0;

			while (list != null) {
				Item item = list.head;

				System.out.println("## ITEM LIST - " + (itemid++));

				while (item != null) {
					char name[] = item.name;
					int id = item.id;

					System.out.print(" - " + id + " ");
					System.out.print(new String(name) + " ");
					System.out.println();

					item = item.next;
				}
				list = list.lnext;
			}

		}
	}
}

class ItemList {
	// FOR ADD A DUPLICATED KEY ITEM
	Item head;

	// at the collision case
	ItemList lnext;

	public void addItemToHead(Item item) {
		if (head == null) {
			this.head = item;
			return;
		}

		item.next = head;
		head = item;
	}
}

class Item {
	int id;
	char name[];

	Item next;

	public Item(int id, char name[]) {
		this.id = id;
		this.name = name;
	}

	public boolean equalto(char iname[]) {
		if (name.length != iname.length)
			return false;

		for (int i = 0; i < name.length; i++) {
			if (this.name[i] != iname[i])
				return false;
		}
		return true;
	}
}
