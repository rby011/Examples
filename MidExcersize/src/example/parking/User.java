package example.parking;

public class User {

	public static void main(String args[]) {

	}

	// number[11], code[11]
	// 1. 이미 주차되어 있는 차량에 대해서 다시 이 함수를 호출하지 않는다
	// 2. 이전에 출차했던 차량은 다시 주차하지 않는다
	static void parking(char number[], char code[], int contract_term) {

	}

	// number[11]
	// 1. 계약 기간이 만료되지 않은 차를 출차
	// 2. tick 을 통해 출차될 차량 수 에 포함하지 않음
	// 3. 주차장에 있지 않은 차량이 요청되기도 함
	public static void unparking(char number[]) {

	}

	// number[11], code[11] renewal with existing contract_term
	// 1. 주차되어 있는 차량의 계약 기간 갱신
	// 2. 주차장에 있지 않은 차량에 대해 계약 기간 갱신 요청, 이는 무시
	// 3. 코드가 다를 경우 계약 갱신 할 수 없음
	public static void renew(char number[], char code[]) {

	}

	// 출차해야 하는 차량의 대수를 구해야 함
	// 1. 이 함수가 불려질 때 마다 한 term 시간이 흐름
	// 2. tick 변경 시점에 출차해야 하는 차량 대수 반환, 해당 차량 출차
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