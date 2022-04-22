# ITEM 73 추상화 수준에 맞는 예외를 던져라

--------------------------------------------
#### 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야 한다
* 그렇지 않으면 수행하려는 일과 관련 없어 보이는 예외가 발생해 내부 구현 방식을 드러내고 윗 레벨 API를 오염시키게 된다
  * 다음 릴리스에서 구현 방식을 바꾸면 다른 예외가 나타나 기존 클라이언트를 깨지게 할 수도 있다

```` java
예외 번역

try {
  ... // 저수준 추상화를 이용한다
} catch (LowerLevelException e) {
  // 추상화 수준에 맞게 번역한다
  throw new HigherLevelException(...);
}

다음은 AbstractSequentialList에서 수행하는 예외 번역의 예다
AbstractSequentialList는 List 인터페이스의 골격 구현이다

아래 예제에서 수행한 예외 번역은 List<E> 인터페이스의 get 메서드 명세에 명시된 필수 사항이다

/**
* 이 리스트 안의 지정한 위치의 원소를 반환한다
* @throws IndexOutOfBoundsException index가 범위 밖이면 ({@code index < 0 || index >= size()}) 이면 발생
*/

public E get(in index) {
  ListIterator<E> i = listIterator(index);
  try{
    return i.next();
  } catch (NoSuchElementException e) {
    throw new IndexOutOfBoundsException("인덱스 : " + index);
  }
}

예외 번역시 저수준 예외가 디버깅에 도움이 되면 예외 연쇄(excep-tion chaining)를 사용하는게 좋다
예외연쇄 : 문제의 근본 원인(cause)인 저수준 예외를 고수준 예외에 실어 보내는 방식, 별도의 접근자 메서드(Throwable의 getCause 메서드)를 통해 필요하면 언제든지 저수준 예외를 꺼내서 볼 수 있다

예외연쇄

try {
  ... // 저수준 추상화를 이용한다
} catch (LowerLevelException cause) {
  // 저수준 예외를 고수준 예외에 실어 보낸다
  throw new HigherLevelException(...);
}

고수준 예외의 생성자는 예외 연쇄용으로 설계된 상위 클래스의 생성자에 이 원인을 건네주어 최종적으로 Throwable(Throwable) 생성자까지 건네게 한다

예외 연쇄용 생성자
class HigherLevelException extends Exception {
  HigherLevelException(Throwable cause) {
    super(cause);
  }
}

대부분 표준 예외는 예외 연쇄용 생성자를 갖추고 있다
그렇지 않은 예외라도 Throwable의 initCause 메서드를 이용해 원인을 직접 못 박을수 있다
예외 연쇄는 문제의 원인을 getCause 메서드로 프로그램에서 접근할 수 있게 해주며 원인과 고수준 예외의 스택 추적 정보를 잘 통합해준다

````
#### 무턱대고 예외를 전파하는 것 보다 예외 번역이 우수한 방법이지만 그렇다고 남용해서는 안된다
* 가능하면 저수준 메서드가 반드시 성공하도록 하여 아래 계층에서는 예외가 발생하지 않도록 하는것이 최선
  * 상위 계층 메서드의 매개변수 값을 아래 계층 메서드로 건네기 전에 미리 검사하는 방법으로 목적 달성이 가능한 경우가 있다
* 차선책은 아래 계층에서 피할수 없다면 상위 계층에서 그 예외를 조용하게 처리하여 문제를 API 까지 전파시키지 않는 것이다
  * 이런 경우 발생한 예외는 적절한 로깅 기능을 활용하여 기록하면 좋다
  * 이렇게 하면 클라이언트 코드와 사용자에게 문제를 전파하지 않으면서 개발자가 로그 분석이 가능하게 된다

#### 아래 계층의 예외를 예방하거나 스스로 처리할 수 없고 그 예외를 상위 계층에 그대로 노출하기 곤란하다면 예외 번역을 사용하자
#### 이때 예외 연쇄를 이용하면 상위 계층에는 맥락에 어울리는 고수준 예외를 던지면서 근본 원인도 함께 알려주어 오류를 분석하기에 좋다