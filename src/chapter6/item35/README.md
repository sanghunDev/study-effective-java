# ITEM 35 ordinal 메서드 대신 인스턴스 필드를 사용하라

--------------------------------------------

대부분의 열거 타입 상수는 자연스럽게 하나의 정숫값에 대응된다

열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ordinal 이라는 메서드를 제공한다
* 이렇게 제공을 하니까 열거 타입 상수와 연결된 정숫값이 필요하면 ordinal 메서드를 쓰고 싶어진다

```` java
합주단의 종류를 연주자가 1명인 솔로부터 10명인 디텍트 까지 정의한 열거타입

ordinal을 잘못 사용한 예제

public enum Ensemble {
  SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET
  
  public int numberOfMusicians() {
    return ordinal() + 1;
  }
}
````

* 위 코드는 동작은 하지만 좋지 않은 코드이다
  * 상수 선언 순서를 바꾸는 순간 오류가 발생하며 기존에 사용중인 정수와 값이 같은 상수는 추가 할 수도 없다
  * 중간에 값을 비울 수도 없다
* 위와 같은 문제점을 해결하는 방법은 열거 타입 상수에 연결된 값은 ordinal 메서드로 얻지 않으면 된다
  * 인스턴스 필드에 저장하여 사용하자 확장성이 올라가고 가독성도 좋다

```` java
  ordinal 메서드를 사용하지 않고 인스턴스 필드에 저장하는 방법
  
public enum Ensemble {
  SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), SEXTET(6), SEPTET(7), OCTET(8), NONET(9), DECTET(10)
  
  private final int numberOfMusicians;
  Ensemble(int size) {
    this.numberOfMusicians = size;
  }
  public int numberOfMusicians() {
    return numberOfMusicians;
  }
}
````

* Enum의 API 문서를 보면 ordinal 메서드는 대부분의 프로그래머는 사용할 일이 없으며 EnumSet과 EnumMap 같은 열거 타입 기반의 범용 자료구조에 사용할 목적으로 설계 되었다고 한다
  * 위와 같은 용도가 아니면 ordinal 메서드는 절대 사용하지 말자