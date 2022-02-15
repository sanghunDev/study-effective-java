# ITEM 4 인스턴스화를 막으려거든 private 생성자를 사용하라

--------------------------------------------
## 정적 메서드와 정저 필드만이 있는 클래스가 있다
* 인스턴스를 생성하지 못하게 해야되는 클래스가 필요한 경우가 있다
* ex) 배열 관련 메서드를 모아놓은 클래스

  * java.util.Arrays
```` java
public class Arrays {

    private static final int MIN_ARRAY_SORT_GRAN = 1 << 13;

    // Suppresses default constructor, ensuring non-instantiability.
    private Arrays() {}
    
    static final class NaturalOrder implements Comparator<Object> {
        @SuppressWarnings("unchecked")
        public int compare(Object first, Object second) {
            return ((Comparable<Object>)first).compareTo(second);
        }
        static final NaturalOrder INSTANCE = new NaturalOrder();
    }

    /**
     * Checks that {@code fromIndex} and {@code toIndex} are in
     * the range and throws an exception if they aren't.
     */
    private static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }
```` 

 
  * java.lang.Math
```` java
public final class Math {

    /**
     * Don't let anyone instantiate this class.
     */
    private Math() {}

    /**
     * The {@code double} value that is closer than any other to
     * <i>e</i>, the base of the natural logarithms.
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The {@code double} value that is closer than any other to
     * <i>pi</i>, the ratio of the circumference of a circle to its
     * diameter.
     */
    public static final double PI = 3.14159265358979323846;

    public static double sin(double a) {
        return StrictMath.sin(a); // default impl. delegates to StrictMath
    }
````
* 특정 인터페이스를 구현하는 객체를 생성해주는 정적 메서드를 모아놓은 인터페이스
  * java.util.Collections
* 이러한 클래스들은 항상 동일한 입력값을 넣으면 동일한 결과값이 나오게 되며 어디서든 있는 그대로 재사용이 가능하다
* 즉 전역으로 사용할 수 있는 필드들이 모인 유틸성 클래스이다
---------------------------------------------
#### 왜 private 생성자를 사용할까?

```` java
public class Foo {
    public static boolean useYn = true;
    public static int startNo = 1;
}
````
위와 같은 코드를 그냥 사용 하는것은 문제가 없다 

하지만 그냥 사용하게 된다면 인스턴스 생성이 가능하기에 아래와 같은 이유로 private 생성자를 사용한다 

* 목적 자체가 인스턴스로 만들어서 사용하려는 클래스가 아니다
  * 쓸데 없는 혼란을 야기한다
* 기본 생성자인 public 생성자는 전체 접근이 가능하여 private를 선언해서 인스턴스화를 막는것이다
* 모든 생성자는 상위 클래스의 생성자를 호출하게 되는데 private로 선언하여 하위 클래스가 상위 클래스로 접근이 막히게 되어 상속을 방지하는 효과도 있다

#### 인스턴화를 막으려면 어떻게 해야할까?

```` java
public class FooEx1 {
    public static boolean useYn = true;
    public static int startNo = 1;

    private FooEx1() {
        throw new IllegalStateException("인스턴스화 불가능!");
    }
}
````

* 클래스 내부에 명시된 생성자가 없다면 기본 생성자를 만들기 때문에 private 생성자를 사용하면 된다
  * 생성자가 존재하지만 호출이 불가능하니 개발자가 헷갈릴수 있기에 주석을 잘 달아주자 
  * 추상 클래스로 생성하는 것은 하위 클래스를 만들어 인스턴스화가 가능하기 때문에 대안이 되지 않는다
