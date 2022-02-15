# ITEM 2 생성자에 매개변수가 많다면 빌더를 고려하라

--------------------------------------------

정적 팩터리 패턴과 생성자는 매개변수가 많을 때 가독성이 떨어지게 된다

이럴때 점층적 생성자 패턴, 자바 빈즈 패턴, 빌더 패턴을 사용해보자

## 점층적 생성자 패턴

--------------

```` java

public class Foo {
    private String userName;    //필수값
    private int age;            //필수값
    private String address;     //필수값
    private String tel;         //선택값

    public Foo(String userName) {
        this.userName = userName;
    }

    public Foo(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Foo(String userName, int age, String address) {
        this.userName = userName;
        this.age = age;
        this.address = address;
    }

    public Foo(String userName, int age, String address, String tel) {
        this.userName = userName;
        this.age = age;
        this.address = address;
        this.tel = tel;
    }
}

````
#### 장점
* 매개변수의 개수에 따라 생성자가 점점 증가하는 방식이다
* 한번의 호출로 객체가 생성된다

#### 단점
* 매개변수가 증가함에 따라 각 값의 순서나 개수등을 신경 써야한다
* 한눈에 의미를 파악하기가 어렵고 호출부의 가독성이 떨어지고 작성하기가 어려워진다
* 필수값이 아닌 값이라도 객체 생성을 위해 빈 값을 넣어줘야 한다

```` java
public static void main(String[] args) {
    Foo foo = new Foo("hong");
    Foo foo2 = new Foo("hong",10);
    Foo foo3 = new Foo("hong",10,"대전");
    Foo foo4 = new Foo("hong",10,"대전","");
}
````

## 자바 빈즈 패턴

--------------
```` java
public class FooJavaBeans {
    private String userName;
    private int age;
    private String address;
    private String tel;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
````
#### 장점
* 기본 생성자 하나만 만들고 set 메서드를 통하여 값을 셋팅해준다
* 점층적 생성자 패턴보다 가독성이 좋다
* 점층적 생성자 패턴보다 확장하기가 편하다

#### 단점
* 한번의 호출로 객체 생성이 끝나지 않기 때문에 일관성이 무너진 상태가 된다
* setter가 존재 하기에 불변으로 만들수 없어 쓰레드 안정성을 위해 추가적인 작업이 필요하다
* 점층적 생성자 패턴과 다르게 매개변수의 유효성 체크가 즉각적으로 이루어지지 않는다

```` java
    public static void main(String[] args) {
        FooJavaBeans fooJavaBeans = new FooJavaBeans();
        
        fooJavaBeans.setUserName("hong");
        fooJavaBeans.setAge(10);
        fooJavaBeans.setAddress("대전");
        fooJavaBeans.setTel("");

    }
````

## 빌더 패턴

--------------

```` java
package chapter2.item2;

public class FooBuilder {
    private final String userName;
    private final int age;
    private final String address;
    private final String tel;

    public static class Builder {
        private final String userName;    //필수값
        private final int age;            //필수값
        private final String address;     //필수값
        private String tel;         //선택값

        public Builder(String userName, int age, String address) {
            this.userName = userName;
            this.age = age;
            this.address = address;
        }

        public Builder tel(String tel) {
            this.tel = tel;
            return this;
        }

        public FooBuilder build() {
            return new FooBuilder(this);
        }
    }

    private FooBuilder(Builder builder) {
        userName = builder.userName;
        age = builder.age;
        address = builder.address;
        tel = builder.tel;
    }

}
````

#### 장점

* 점층적 생성자 패턴과 자바 빈 패턴의 장점을 합쳤다(불변성 + 가독성)
* 호출부 코드를 메서드 체이닝으로 작성하여 가독성이 좋아졌다
* 점층적 생성자 패턴처럼 객체의 일관성도 보장되며 객체 생성시 필수값 지정도 가능하다
* 빌더 하나로 여러 객체를 순회하며 만들 수 있다
* 매개변수에 따라 다른 객체를 생성 할수도 있으므로 유연성이 높다

#### 단점

* 클래스 내부에 빌더를 추가로 만들어 주는 작업이 필요하기 때문에 필수 매개변수가 많다면 생각이 좀 필요하다
* 어쨋거나 빌더를 추가로 만드는 것익이 때문에 성능에 민감하다면 생각을 좀 해봐야 한다

```` java
  public static void main(String[] args) {
        FooBuilder fooBuilder = new FooBuilder.Builder("hong",10,"대전")
                .tel("01012345678")
                .build();
    }
````
#### 불변(immutable) 과 불변식(invariant)

* 불변 
  * 변경을 할수 없다는 뜻, 대표적으로 String이 있다
* 불변식
  * 프로그램이 실행중, 정해진 일정 기간동안 만족해야 하는 조건이다
  * 변경 허용이 가능하지만 특정 조건내에서만 가능하다

---------------------------------------------------------------

#### 생성자의 매개변수가 늘어나는 경우가 빈번하니 추후 확장성을 위해 처음부터 빌더로 생성 하는걸 고려 해보자 