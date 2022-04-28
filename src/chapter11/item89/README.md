# ITEM 89 인스턴스 수를 통제해야 한다면 readResolve 보다는 열거 타입을 사용하라

--------------------------------------------
```` java
바깥에서 생성자를 호출하지 못하게 막는 방식으로 싱글턴을 보장한 코드

public class Elvis {
  public static final Elvis INSTANCE = new Elvis();
  private Elvis() { .. }
  
  public void leaveTheBuilding() { .. }
}

위 코드는 클래스 선언에 implements Serializable을 추가하면 더이상 싱글턴이 아니게 된다

기본 직렬화를 쓰지 않더라도, 명시적인 readObject를 제공하더라도 소용 없다

어떤 readObject를 사용하든 이클르새가 초기화될 때 만들어진 인스턴스와는 별개인 인스턴스를 반환하게 된다

readResolve 기능을 이용하면 readObject가 만들어낸 인스턴스를 다른 것으로 대체 가능하다

역직렬화한 객체의 클래스가 readResolve 메서드를 적절히 정의해뒀다면 역직렬화 후 새로 생성된 객체를 인수로 이 메서드가 호출되며
이 메서드가 반환한 객체 참조가 새로 생성된 객체를 대신해 반환된다
대부분의 경우 이때 새로 생성된 객체의 참조는 유지하지 않으므로 바로 가비지 컬렉션 대상이 된다

위 Elvis 클래스가 Serializable을 구현한다면 아래의 readReslove 메서드를 추가해 싱글턴을 유지 가능하다

//인스턴스 통제를 위한 readResolve - 개선 가능
private Object readResolve() {
  //진짜 Elvis를 반환하고 가짜 Elvis는 GC 에 맡긴다
  return INSTANCE; 
}

이 메서드는 역직렬화한 객체는 무시하며 클래스 초기화시 만들어진 Elvis 인스턴스를 반환하며 Elvis 인스턴스의 직렬화 형태는 아무런 실 데이터를 가질 이유가 없으니 모든 인스턴스 필드를 transient로 선언해야 한다
````
#### readResolve를 인스턴스 통제 목적으로 사용한다면 객체 참조 타입 인스턴스 필드는 모두 transient로 선언해야 한다
* 이렇게 안하면 MutablePeriod 공격과 비슷한 방식으로 readResolve 메서드가 수행되기 전에 역직렬화된 객체의 참조를 공격할 여지가 남는다

인스턴스 통제를 위해 readResolve를 사용하는 방식이 완전히 쓸모없는 것은 아니다
* 직렬화 가능 인스턴스 통제 클래스를 작성해야 할때 컴파일 타임에는 어떤 인스턴스들이 있는지 알 수 없는 상황이라면 열거 타입으로 표현하는 것이 불가능하기 때문

#### readResolve 메서드의 접근성은 매우 중요하다
* final 클래스에서라면 readResolve 메서드는 private 이어야 한다
* final이 아닌 클래스에서는 다음의 몇 가지를 주의해서 고려해야 한다
  * private으로 선언하면 하위 클래스에서 사용할 수 없다
  * package-private으로 선언하면 같은 패키지에 속한 하위 클래스에서만 사용할 수 있다
  * protected나 public으로 선언하면 이를 재정의하지 않은 모든 하위 클래스에서 사용할 수 있다
  * protected나 public이면서 하위 클래스에서 재정의하지 않았다면 하위 클래스의 인스턴스를 역직렬화하면 상위 클래스의 인스턴스를 생성하여 ClassCastException을 일으킬 수 있다

#### 불변식을 지키기 위해 인스턴스를 통제해야 한다면 간으한 한 열거 타입을 사용하자
#### 여의치 않은 상황에서 직렬화와 인스턴스 통제가 모두 필요하다면 readResolve 메서드를 작성해 넣어야 한다
* 그 클래스에서 모든 참조 타입 인스턴스 필드를 transient로 선언해야 한다
