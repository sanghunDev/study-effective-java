# ITEM 10 equals는 일반 규약을 지켜 재정의하라 

--------------------------------------------
### equals 메서드를 재정의 하지않는 경우
#### 다음과 같은 경우에 equals 메서드를 재정의 하지 않는것이 최선이다
* 각 인스터스가 본질적으로 고유하다
  * 값이 아니라 동작하는 개체를 표현하는 클래스
    * Thread 가 좋은 예시이다
    

* 인스턴스의 논리적 동치성을 검사할 필요가 없다
  * 설계자의 의도에 따라 기본 equals 만으로 해결이 가능
  * 어떠한 값을 검사 할때 꼭 같은 정규표현식을 쓸 필요는 없을수도 있다(논리적 동치성 검사가 필요 없을 수 있다는 것)

  
* 상위 클래스에서 재정의한 equals 메서드가 하위 클래스에도 딱 들어 맞는다
  * Set, List, Map 등의 구현체들은 상위 클래스에서 구현한 equals 메서드를 상속 받아 사용한다
    * AbstractSet AbstractList AbstractMap 등..
  * 클래스가 private 이거나 package-private 이고 equals 메서드를 호출할 일이 없다
    * 위험을 최대한 방지하기 위해 equals 호출을 막고 싶다면 아래처럼 구현
    ```` java
    @Overrid public boolean equals(Object o) {
        throw new AssertionError(); //호출시 에러 반환
    }  
    ````

### equals 메서드를 재정의 하는경우는 언제일까?
#### 상위 클래스에서 equals 메서드를 논리적 동치성이 비교 가능하도록 재정의 하지 않아 논리적 동치성을 비교 할 수 없는 경우
* 주로 값에 대한 클래스들이 해당한다
  * Integer String 등 값을 표현하는 클래스는 객체가 같은 객체인지를 판단하는 것이 아니라 값을 비교하고 싶을것
  * equals 가 논리적 동치성을 제공하도록 재정의 한다면 값 비교도 가능하며 Map 의 키와 Set 의 원소로도 사용이 가능하다
  * 단 값이 같은 인스턴스가 둘 이상 만들어지지 않는 통제 클래스는 equals 메서드를 재정의 할 필요가 없다
    * 어짜피 같은 인스턴스가 둘 이상 만들어지지 않으니 논리적 동치성과 객체 식별성의 의미가 같아진다
      * 인스턴스가 같으면 값도 같다고 볼 수 있으니 Object 의 equals 메서드로 충분하다

### equals 메서드를 재정의 하는 경우 반드시 일반 규약을 따르자
#### Object 명세에 적힌 규약

* 반사성(reflexivity) : null 이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true다
* 대칭성(symmetry) : null 이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true다
* 추이성(transitivity) : null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고 y.equals(z)도 true면 x.equals(z)도 true다
* 일관성(consistency) : null이 아닌 모든 참조 값 x,y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다
* null-아님 : null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다

#### equals 메서드가 쓸모 있으려면 모든 원소가 같은 동치류에 속한 어떤 원소와도 서로 교환이 가능해야 한다
* Object 명세에서 말하는 동치관계는 집합을 서로 같은 원소들로 이뤄진 부분집합으로 나누는 연산
  * 이 부분집합이 동치류(동치클래스 : equivalence class)

### 동치관계 만족을 위한 다섯가지 요건

#### 반사성
* 객체는 자기 자신과 같아야 한다
* 일부로 어기지 않는한 만족시키지 않는게 더 힘듬
  * 해당 요건을 어긴 클래스의 인스턴스를 컬레션에 넣고 contains 메서드를 호출해보자 (없다고 나올것)

#### 대칭성
* 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다

대칭성을 위배하는 코드
```` java
public final class CaseInsensitivieString {
    private final String s;

    public CaseInsensitivieString(String s) {
            this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitivieString)
            return s.equalsIgnoreCase(((CaseInsensitivieString) o).s);
        if (o instanceof String)
            return s.equalsIgnoreCase((String) o);
        return false;
    }
}
````
위 예제를 보면 equals 메서드 재정의 시 비교가 단방향으로 되어있다

따라서 아래처럼 호출 시 testVal.equals(cis) 와 cis.equals(testVal)의 결과값이 다르다
#### String의 equals 메서드는 CaseInsensitivieString의 존재를 모른다
#### 이는 명확히 대칭성을 위배하게 된다
```` java
public static void main(String[] agrs) {
    //대칭성 위배
    CaseInsensitivieString cis = new CaseInsensitivieString("hi");
    String testVal = "hi";

    System.out.println(cis.equals(testVal));    //true
    System.out.println(testVal.equals(cis));    //false

}
````
#### 이런 문제는 아래와 같이 해결 가능하다

```` java
public final class CaseInsensitivieStringGood {
    private final String s;

    public CaseInsensitivieStringGood(String s) {
            this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitivieStringGood && ((CaseInsensitivieStringGood) o).s.equalsIgnoreCase(s);
    }

}
````

#### 이번 주말에 보충 예정 !!!