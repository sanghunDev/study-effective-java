# ITEM 72 표준 예외를 사용하라

--------------------------------------------
표준 예외를 재사용하면 얻는게 많다
* 최고의 장점은 내가 만든 API가 다른 사람이 익히고 사용하기가 쉬워진다
  * 많은 개발자들에게 이미 익숙해져 있는 규약을 따르기 때문
* 내가 만든 API를 사용한 프로그램도 낯선 예외를 사용하지 않게 되어 읽기 쉽게 된다
* 예외 클래스 수가 적을수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게 걸린다

가장 흔하게 재사용 많이 되는 예외

* IllegalArgumentException
  * 허용하지 않는 값이 인수로 건네졌을때 (null 은 따로 NPE 처리)
* IllegalStateException
  * 객체가 메서드를 수행하기에 적절하지 않은 상태인 경우
* NullPointerException
  * null을 허용하지 않는 메서드에 null을 건낸경우
* IndexOutOfBoundsException
  * 인덱스가 범위를 넘었을때
* ConcurrentModificationException
  * 허용하지 않는 동시 수정이 발견됐을 때
* UnsupportedOperationException
  * 호출한 메서드를 지원하지 않을 때

#### Exception, RuntimeException, Throwable, Error는 직접 재사용하지 말자
* 이 클래스는 추상 클래스라고 생각하자
* 이 예외들은 다른 예외들의 상위 클래스라 안정적인 테스트가 불가능하다
  * 여러 성격의 예외들을 포괄하는 클래스라서 그렇다

#### 인수의 값이 무엇이었든 어짜피 실패했을거라면 IllegalStateException을 던지고 아니면 IllegalArgumentException을 던지자