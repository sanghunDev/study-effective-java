# ITEM 3 private 생성자나 열거타입으로 싱글턴임을 보증하라

--------------------------------------------
## 싱글턴

* 인스턴스를 오직 하나만 생성할 수 있는 클래스
* 함수와 같은 무상태 객체, 유일해야 하는 시스템 컴포넌트 등
* mock 구현이 안되기 때문에 클라이언트 테스트가 어려울수있다

---------------------------------------------
#### 장점
* 객체를 한번만 생성 후 재사용 하기 때문에 메모리 낭비를 방지할 수 있고 다른 객체와 공유도 가능하다
#### 단점
* 인터페이스로 만든 후 해당 인터페이스를 구현한 싱글턴이 아니면 mock 구현이 안되기 때문에 클라이언트 테스트가 어렵다
----------------------------------------------------------------------------
## 싱글턴 생성 방식

----------------------------------------------------------------------------
### public static final 필드

```` java
public class Foo {
    public static final Foo INSTANCE = new Foo();

    private Foo() {}

    public void getPrint() {
        System.out.println("hello");
    }
}
````
* private 생성자는 public static final로 선언한 필드를 초기화 할 때 딱 한번 실행된다
* public 이나 protected 생성자가 없으니 해당 클래스가 초기화 될 때 만들어진 인스턴스가 전체 시스템 내에서 하나 뿐임이 보장된다
* public 필드 방식을 사용한다면 해당 클래스가 싱글톤임이 api에 명백히 드러나게 된다
* 소스가 간결하다

```` java
public static void main(String[] args) {
    Foo foo = Foo.INSTANCE;
    //hello
    foo.getPrint();
}
````
-------------------------------------------------------------
### 정적 팩터리 메서드를 public static 멤버로 제공한다

```` java
public class FooEx2 {
    private static final FooEx2 INSTANSE = new FooEx2();

    private FooEx2() {}

    public static FooEx2 getInstance() {
        return INSTANSE;
    }

    public void getPrint() {
        System.out.println("hello");
    }
}
````
* public static 멤버로 제공된 정적 팩터리 메서드를 호출 시 항상 같은 객체의 참조를 반환
* 싱글턴이 아니게 변경하더라도 API를 바꾸지 않고 싱글턴이 아니게 수정이 가능하다
* 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다
* 정적 팩터리의 메서드를 참조 공급자로 사용 가능하다
* 위와 같은 장점들이 필요하지 않으면 public 필드 방식을 사용하는 것이 좋다

```` java
public static void main(String[] args) {
    FooEx2 foo = FooEx2.getInstance();
    //hello
    foo.getPrint();
}
````
---------------------------------------------------------------------------
#### 위 두가지 방식의 유의점

* 두가지 모두 권한이 있는 사용자가 리플렉션을 사용해서 private 생자를 호출하는 경우 문제가 발생한다
  * 생성자를 수정하여 두번째 객체가 생성된다면 예외를 발생시키도록 처리가 필요하다
* 싱글턴 직렬화 시 Serializable을 구현하는것 만으로는 역 직렬화 할때 마다 새로운 인스턴스 생성이 가능한 위험이 발생가능
  * 모든 인스턴스 필드를 transient 선언 후 readResolve 메서드를 제공 해야한다

-------------------------------------------------------------------------
### 원소가 하나인 열거 타입을 선언

```` java
public enum FooEx3 {
    INSTANCE;

    public void getPrint() {
        System.out.println("hello");
    }
}
````

* public 필드 방식과 비슷하지만 더 간결하고 추가 노력 없이 직렬화가 가능하며 리플렉션 공격도 막아준다
* 조금은 부자연스러워 보이지만 대부분의 경우 원소가 하나뿐인 열거형 타입으로 싱글턴을 만드는게 가장 좋다
* 단 만들려는 싱글턴이 Eum 외의 클래스를 상속해야 한다면 사용 불가능
  * 열거 타입이 다른 인스턴스를 구현 하는건 가능

```` java
    public static void main(String[] args) {
        //hello
        FooEx3.INSTANCE.getPrint();
    }
````