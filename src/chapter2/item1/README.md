# ITEM 1 생성자 대신 정적 팩터리 메서드를 고려하라.

--------------------------------------------

## 생성자를 이용하여 객체 생성

--------------

```` java
public class Foo {
    private String name;

    public Foo(String name) {
        this.name = name;
    }
}
````
위와 같이 생성자를 통해 객체를 생성 하는 경우

아래와 같이 호출부에서 이 객체의 역할을 알기가 힘들다

```` java
    public static void main(String[] args) {
        Foo foo1 = new Foo("hong");
    }
````

생성자의 개수가 더 많아진다면 ?

```` java
public class Foo {
    private String name;
    private int age;

    public Foo(String name) {
        this.name = name;
    }

    public Foo(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
````

지금은 두개라 별로 불편하지는 않지만.. 

객체를 생성하려는 클래스의 내부 구조를 잘 알지 못 한다면 호출부에서는 객체의 역할을 더욱 알기 힘들어진다

```` java
    public static void main(String[] args) {
        Foo fooNm = new Foo("hong");
       
        Foo fooInfo = new Foo("hong",20);
    }
````

## 정적 팩토리 메서드를 이용한 객체 생성

--------------

```` java

public class StaticFactoryFooEx1 {
    private String name;
    private int age;

    private StaticFactoryFooEx1(String name) {
        this.name = name;
    }

    private StaticFactoryFooEx1(String name, int age) {
        this.name = name;
        this.age = age;
    }

    static public StaticFactoryFooEx1 getNewInstanceByNM(String name) {
        return new StaticFactoryFooEx1(name);
    }

    static public StaticFactoryFooEx1 getNewInstanceByNMAndAge(String name, int age) {
        return new StaticFactoryFooEx1(name, age);
    }
}

````
### 정적 팩터리 메서드를 사용하여 객체를 생성한다면 위와 같이 이름을 지어줄 수 있다

이름이 있다면 객체 생성시 해당 객체의 의미를 파악하기가 더 쉬워진다
```` java
    public static void main(String[] args) {
        StaticFactoryFooEx1 staticFactoryFooEx1Sample1 = StaticFactoryFooEx1.getNewInstanceByNM("hong");
        StaticFactoryFooEx1 staticFactoryFooEx1Sample2 = StaticFactoryFooEx1.getNewInstanceByNMAndAge("hone", 20);    }
````

### 정적 팩터리 메서드를 사용하면 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다

```` java
public class StaticFactoryFooEx2 {
    public static final StaticFactoryFooEx2 STATIC_FACTORY_FOO_EX_2 = new StaticFactoryFooEx2();

    private StaticFactoryFooEx2() {

    }
}
````

두번 호출 하였지만 두 객체의 인스턴스는 같은걸 확인 가능하다
```` java
   public static void main(String[] args) {
        StaticFactoryFooEx2 staticFactoryFooEx2Sample1 = StaticFactoryFooEx2.STATIC_FACTORY_FOO_EX_2;
        StaticFactoryFooEx2 staticFactoryFooEx2Sample2 = StaticFactoryFooEx2.STATIC_FACTORY_FOO_EX_2;

        System.out.println(staticFactoryFooEx2Sample1 == staticFactoryFooEx2Sample2);
    
        //출력값 true
    }
````
### 정적 팩터리 메서드를 사용하면 반환 타입의 하위 타입 객체를 반환이 가능하다

정적 팩터리 메서드를 사용하여 부모 클래스에 getPrint()라는 추상 메서드를 선언한다

StaticFactoryFooEx3 를 상속 받은 클래스 자식 클래스는 getPrint()를 구현한다

정적 팩터리 메서드로 자식 타입을 반환하게 만든 후 호출부에서 호출하여 테스트 해보자

```` java
public abstract class StaticFactoryFooEx3 {
    abstract void getPrint();

    public static StaticFactoryFooEx3 getNewInstance() {
        return new Ex3Child();
    }
}

class Ex3Child extends StaticFactoryFooEx3 {
    public void getPrint() {
        System.out.println("나는 자식");
    }
}
````

아래의 코드처럼 테스트 결과 자식 클래스의 함수가 호출 되는것이 확인 가능하다

이처럼 정적 팩터리 메서드로 하위 타입의 객체를 반환하게 되면 클라이언트는 실제 구현부를 신경 쓸 필요가 없어지며 코드의 유연성이 확보된다

(java.util.Collections의 팩토리 메서드의 사례가 대표적이다)

```` java
public static void main(String[] args) {
        StaticFactoryFooEx3 staticFactoryFooEx3 = StaticFactoryFooEx3.getNewInstance();
        //나는 자식
        staticFactoryFooEx3.getPrint();
    }
````
### 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환 할 수가 있다.

위와 비슷한 사례로 같은 이름의 메서드지만 넘어오는 매개변수의 값에 따라 실제 반환하는 하위 클래스가 달라집니다.
```` java
public abstract class StaticFactoryFooEx4 {
    abstract void getPrint();

    public static StaticFactoryFooEx4 getNewInstance(boolean useYn) {
        StaticFactoryFooEx4 child = null;

        if(useYn) {
            child = new Ex4FirstChild();
        } else {
            child = new Ex4SecondChild();
        }

        return child;
    }
}

class Ex4FirstChild extends StaticFactoryFooEx4 {
    public void getPrint() {
        System.out.println("나는 첫번째 자식");
    }
}

class Ex4SecondChild extends StaticFactoryFooEx4 {
    public void getPrint() {
        System.out.println("나는 두번째 자식");
    }
}
````
이처럼 인자에 따라 출력되는 결과가 달라지는것이 확인 가능합니다.

이러한 특성을 활용하면 역시 유연한 코드 작성이 가능해집니다.
```` java
    public static void main(String[] args) {
        StaticFactoryFooEx4 staticFactoryFooEx4First = StaticFactoryFooEx4.getNewInstance(true);
        //나는 첫번째 자식
        staticFactoryFooEx4First.getPrint();

        StaticFactoryFooEx4 staticFactoryFooEx4Second = StaticFactoryFooEx4.getNewInstance(false);
        //나는 두번째 자식
        staticFactoryFooEx4Second.getPrint();
    }
````

### 정적 팩터리 메서드를 작성하는 시점에 반환할 객체의 클래스가 존재하지 않아도 된다

메서드 안에서 객체를 반환할 당시에 클래스가 존재하지 않아도 특정 위치를 지정해주면 해당 객체의 반환이 가능하다
리플렉션을 사용하여 다른 위치에 있는 클래스를 호출하도록 만들었다

```` java
public abstract class StaticFactoryFooEx5 {
    protected abstract void getPrint();

    public static StaticFactoryFooEx5 getNewInstance() {
        StaticFactoryFooEx5 child = null;

        try {
            Class<?> childCls = Class.forName("chapter2.demo.Ex5Child");
            child = (StaticFactoryFooEx5) childCls.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("클래스가 없어!!");
        } catch (InstantiationException e) {
            System.out.println("인스턴스화 실패!!");
        } catch (IllegalAccessException e) {
            System.out.println("접근 불가능!!");
        }

        return child;
    }
}
````

아래의 문자열이 출력되는지 확인 해보자
```` java
import chapter2.item1.StaticFactoryFooEx5;

public class Ex5Child extends StaticFactoryFooEx5 {

    @Override
    protected void getPrint() {
        System.out.println("나는 다른 패키지에 있는 자식");
    }
}
````

정상적으로 잘 출력 되는것이 확인 가능하다

이것도 역시 유연한 코드를 작성하는데 도움을 준다

대표적인 예) Class.forName("oracle.jdbc.driver.OracleDriver");

```` java
public static void main(String[] args) {
        StaticFactoryFooEx5 staticFactoryFooEx5 = StaticFactoryFooEx5.getNewInstance();
        //나는 다른 패키지에 있는 자식
        staticFactoryFooEx5.getPrint();
    }
````

### 정적 팩터리 메서드의 단점

* 프로그래머가 찾기가 힘들다
* 생성자 처럼 api 문서에서 별도 항목으로 보여지지 않아 관련 내용을 찾아 보기가 힘들어서 더 자세하게 api 문서를 만들어 줘야 한다 (명명 규약을 잘 지켜서 만들자)
* 정적 팩터리 메서드만 제공한다면 하위 클래스를 만들 수 없다 (상속에 사용할 public , protected 생성자가 없음, 컴포지션 방식을 유도하므로 꼭 단점인가 싶다)

------------------------------------------------------------------------------------------------------------------------------
#### 정적 팩터리 메서드 네이밍 규칙

```` text
from
- 매개변수 하나로 하나의 인스턴스 만듦  
of
- 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드  
valueOf
- from 과 of의 자세한 버전
getInstance, instance 
- 매개변수를 받는다면 매개변수로 명시한 인스턴스를 반환하지만 같은 인스턴스임을 보장하지는 않는다
create, newInstance 
- 매번 새로운 인스턴스를 생성해 반환함을 보장한다
getType
- 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다 (Type은 팩터리 메서드가 반환할 객체의 타입)
newType
- getType 이랑 똑같음
Type
- getType, newType 의 간결한 버전
````
-----------------------------------------------------------------------------------------------------------------------
#### public 생성자와 정적 팩터리 각각 쓰임새가 있으니 상대적인 장단점을 이해하고 쓰는게 좋다
#### 하지만 정적 팩터리를 사용하는 경우가 단점 보다는 장점이 많기 때문에 public 생성자 사용보다 정적 팩터리 사용을 고려해보자
