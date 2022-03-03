# ITEM 17 변경 가능성을 최소화하라

--------------------------------------------
#### 불변클래스를 만드는 규칙
불변클래스란 인스턴스 내부 값을 수정할 수 없는 클래스다

불변 클래스에 간직된 정보는 객체가 파괴되는 순간까지 변경되지 않는다

불변 클래스는 가변 클래스보다 설계하고 구현하고 사용하기 쉬우며, 오류가 생길 여지도 적고 안전하다

* 객체의 상태를 변경하는 메서드를 제공하지 않는다
* 클래스를 확장할 수 없도록 한다
  * 하위 클래스에서 객체의 상태를 변하게 만드는 경우를 막아준다
    * 상속을 막는 대표적인 방법은 클래스르 final로 선언하는것
* 모든 필드를 final로 선언한다
  * 시스템이 강제하는 수단을 이용해 설계자의 의도를 명확히 드러내는 방법
* 모든 필드를 private로 선언한다
  * 필드가 참조하는 가변객체를 클라이언트에서 직접 접근해 수정하지 못하도록 한다
  * public final로도 불변 객체가 되지만 다음 배포시 내부 표현 변경이 어려워 비추
* 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다
  * 클래스에 가변 객체를 참조하는 필드가 하나라도 있다면 클라이언트에서 그 객체의 참조를 얻을수 없도록 해야함
  * 절대 클라이언트가 제공한 객체 참조를 가르키게 하면 안됨
  * 접근자 메서드가 그 필드를 그대로 반환해도 안됨
  * 생성자, 접근자, readObject 메서드 모두에서 방어적 복사를 수행하라

#### 불변 객체의 특징
* 불변 객체는 단순하다
  * 불변 객체는 생성된 시점의 상태를 파괴되는 순간까지 간직한다
* 불변 객체는 근본적으로 스레드 안전하여 따로 동기화할 필요 없다
  * 여러 스레드가 동시에 사용해도 절대 훼손되지 않는다
* 불변 객체는 안심하고 공유할 수 있다
  * 다른 스레드가 영향을 주지 않기 때문
  * 최대한 재활용하기를 권장함(상수로 활용 등)
* 불변 객체는 자유롭게 공유할 수 있음은 물론, 불변 객체끼리는 내부 데이터를 공유할 수 있다
* 객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 이점이 많다
  * 값이 바뀌지 않는 구성요소들로 이루어진 객체라면 그 구조가 아무리 복잡하더라도 불변식을 유지하기 훨씬 수월하다
  * 불변 객체는 맵의 키와 Set의 원소로 쓰기에 안성맞춤이다
    * 맵이나 집합은 안에 담긴 값이 바뀌면 불변식이 허물어 지는데 그걸 방지해준다
* 불변 객체는 그 자체로 실패 원자성을 제공한다
  * 상태가 절대 변하지 않으니 잠깐이라도 불일치 상태에 빠질 가능성이 없다
    * 실패 원자성이란 메서드에서 예외가 발생한 후에도 그 객체는 여전히(메서드 호출 전과 같은) 유효한 상태여야 한다
* 불변 클래스의 단점
  * 값이 다르면 반드시 독립된 객체로 만들어야 한다
    * 값이 여러가지면 생산 비용이 많이든다
