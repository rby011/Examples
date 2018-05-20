package example.parking;

public class User {

	public static void main(String args[]) {

	}

	// number[11], code[11]
	// 1. �̹� �����Ǿ� �ִ� ������ ���ؼ� �ٽ� �� �Լ��� ȣ������ �ʴ´�
	// 2. ������ �����ߴ� ������ �ٽ� �������� �ʴ´�
	static void parking(char number[], char code[], int contract_term) {

	}

	// number[11]
	// 1. ��� �Ⱓ�� ������� ���� ���� ����
	// 2. tick �� ���� ������ ���� �� �� �������� ����
	// 3. �����忡 ���� ���� ������ ��û�Ǳ⵵ ��
	public static void unparking(char number[]) {

	}

	// number[11], code[11] renewal with existing contract_term
	// 1. �����Ǿ� �ִ� ������ ��� �Ⱓ ����
	// 2. �����忡 ���� ���� ������ ���� ��� �Ⱓ ���� ��û, �̴� ����
	// 3. �ڵ尡 �ٸ� ��� ��� ���� �� �� ����
	public static void renew(char number[], char code[]) {

	}

	// �����ؾ� �ϴ� ������ ����� ���ؾ� ��
	// 1. �� �Լ��� �ҷ��� �� ���� �� term �ð��� �帧
	// 2. tick ���� ������ �����ؾ� �ϴ� ���� ��� ��ȯ, �ش� ���� ����
	public static int tick() {
		return 0;
	}

}

class HashCarList {
	Car head_hash;

	public void addCarToHead(Car car) {
		if (head_hash == null) {
			this.head_hash = car;
			return;
		}

		car.next_hash = head_hash;
		head_hash = car;
	}

}

class TickCarList {
	Car head_tck;

	public void addCarToHead(Car car) {
		if (head_tck == null) {
			this.head_tck = car;
			return;
		}

		car.next_tick = head_tck;
		head_tck = car;
	}

}

class Car {
	Car next_hash;
	Car next_tick;

	char number[];
	char code[];
	int term;
	int out_tick;

	double hash;

	private double hashcode(char code[]) {
		double hash = 1;
		for (int i = 0; i < code.length; i++) {
			hash = hash * 5381 + code[i];
		}
		return hash;
	}
}