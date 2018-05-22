* Party
. Game ID 1 ~ 4 명
. 정지와 무관하게 파티 삭제 가능 (복구되지 않음)


* Game ID
. ID 정지 시 ID 포함 모든 Party 운영 중지
  (중지 Party 는 검색 시 숫자 카운팅에서 제외)
. ID 정지 복구 시 해당 Party 운영 가능
  (복구 시 소속 ID 모두가 복구 되었다면 재 운영)


* 검색 기능
1) 한 Game ID 대상 소속 파티 개수 검색
2) 두 Game ID 대상 두 명 중 한 명이라도 참여하고 있는 모든 파티의 수
3) 두 Game ID 대상 두 명이 모두 참여하고 있는 파티의 수


* 구현 요구 함수

1) void init(int n) : 초기화
 
   a. n = 파티 최대 수(4~50,000)


2) void addParty(int index, int m, char members[4][11]) : 파티 추가

   a. 최대 n 회수만큼 호출된다.(호출 회수는 n보다 적을 수 있다)
   b. 파티 index , 0 시작 순차적 배정, closeParty() 와 공유
   c. m = 파티 멤버 수(1 ~ 4) ,  member=id (길이 4~10, 소문자 알파벳+숫자 조합)
      파티 내 id 중복 없음

3) void closeParty(int index) : index 가 지정하는 파티 제거

   a. addParty() 통해 주어진 index, 즉 존재하는 index 만 주어짐

4) void suspendMember(char member[11]) : id 중지

   a. 아직 파티에 포함되어 있지 않더라도 계정을 중지 가능
      (id 중지 이전 구성 파티도 해당 id 를 포함하는 파티가 생성되면 중지 상태, addParty 에서 체크해야함)

5) void recoveryMember(char member[11]) : id 복구

   a. 정지되지 않은 id를 대상으로 호출되지는 않음
   b. id 포함된 파티 내 모든 id 가 중지 상태 아니면 파티 운영 재개

6) int search(char members[2][11], int mode) : id 포함, 정지상태 아닌 파티 수 확인

   a. id는 모두 suspend 되어 있지 않으며 두 개 주어진 경우 반드시 다른 id
   b. mode - 0 :  정지상태가 아닌 파티들의 수를 계산
   c. mode - 1 : 주어진 두 member가 모두 속해 있고 정지상태가 아닌 파티들의 수 (AND)
   d. mode - 2 : 두 member들 중 한 명이라도 속해있는 파티들의 수를 계산 (OR) 