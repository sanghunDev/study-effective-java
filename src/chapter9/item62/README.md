# ITEM 62 다른 타입이 적절하다면 문자열 사용을 피하라

--------------------------------------------

String은 텍스를 표현하도록 설계되었고 설계 의도대로 아주 잘 동작하지만 워낙 흔하고 자바가 잘 지원해주어 원래의 의도와 다른용도로 쓰이는 경우가 많다

문자열을 쓰지 않아야 하는 경우
* 문자열은 다른 값 타입을 대신하기에 적합하지 않다
  * 많은 경우 파일, 네트워크, 키보드 입력으로부터 데이터를 받을 때 문자열을 사용하는 경우가 많지만 입력받을 데이터가 진짜 문자열인 경우에만 쓰는게 좋다
  * 받은 데이터가 수치형이면 int, float, BigInteger 등 적당한 수치형 타입으로 변환해야 한다
  * 예/아니오 같은 질문의 답이라면 적절한 열거 타입이나 boolean으로 변환해야 한다
  * 일반화해서 이야기하면 기본 타입이든 참조 타입이든 적절한 값 타입이 있으면 그걸 사용하고 없으면 하나 새로 만들어라
* 문자열은 열거 타입을 대신하기에 적합하지 않다
  * 상수를 열거할 때는 문자열보다는 열거 타입이 훨씬 낫다(item 34)
* 문자열은 혼합 타입을 대신하기에 적합하지 않다
  * 여러 요소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 대체로 좋지 않은 생각이다
```` java
혼합 타입을 문자열로 처리한 부적절한 예
String compoundKey = className + "#" + i.next();

만약 두 요소를 구분하는 #이 두 요소 중 하나에 쓰이고 있다면 혼란을 가중시킨다
각 요소를 개별로 접근하려면 문자열 파싱이 필요해 귀찮으며 느려지고 오류가 발생할 가능성도 커진다
equals, toString, compareTo 메서드를 제공할 수 없으며 String이 제공하는 기능에만 의존해야 하므로 전용 클래스를 새로 만드는게 낫다
이런 클래스는 보통 private 정적 멤버 클래스로 선언한다(item 24)
````
* 문자열은 권한을 표현하기에 적합하지 않다
  * 권한(capacity)을 문자열로 표현하는 경우가 종종 있는데 아래의 예제를 보자
```` java
스레드 지역변수 기능을 설계하는 경우 
- 각 스레드가 자신만의 변수를 갖게 해주는 기능이다
- 이 기능은 자바 2부터 지원했으며 그 전에는 개발자가 직접 구현했다
- 그때 이 기능을 설계했던 여러 개발자가 독립적으로 방버을 찾아다 결국엔 똑같은 설계를 하게 되었다
  - 클라이언트가 제공한 문자열 키로 스레드별 지역변수를 식별하는 것


문자열을 사용해 권한을 구분한 잘못된 예제
public class ThreadLocal {
  private ThreadLocal() { }//객체 생성 불가
  
  //현 스레드의 값을 키로 구분해서 저장한다
  public static void set(String key, Object value);
  
  //키가 가리키는 현 스레드의 값을 반환한다
  public static Object get(String key);
}

이 방식의 문제는 스레드 구분용 문자열 키가 전역 이름공간에서 공유된다는 점이다
이 방식이 의도대로 동작하려면 각 클라이언트가 고유한 키를 제공해야 한다
만약 두 클라이언트가 서로 소통하지 못해 같은 키를 쓰기로 결정한다면 의도치 않게 같은 변수를 공유하게 된다
결국 두 클라이언트에 모두 문제가 생기며 보안도 취약해지고 악의적으로 같은 키를 사용해 다른 클라이언트의 값을 가져올 수도 있다

이 문제를 개선하려면 문자열 대신 위조할 수 없는 키를 사용하면 된다
이 키를 권한(capacity)라고도 한다

Key 클래스로 권한을 구분했다
public class ThreadLocal {
  private ThreadLocal() { }
  
  public static class Key { //권한
    key() {}
  }
  
  //위조 불가능한 고유 키를 생성한다
  public static Key getKey() {
    return nwe Key();
  }
  
  public static void set(Key key, Object value);
  public static Object get(Key key);
  
}

위 코드는 문자열 기반의 API의 문제를 다 해결해주지만 아직도 개선의 여지는 있다
set, get 메서드는 정적 메서드일 필요가 없으니 Key 클래스의 인스턴스 메서드로 바꾸자

이러면 Key는 더이상 스레드 지역변수를 구분하기 위한 키가 아니라 그 자체가 스레드 지역변수가 된다
결과적으로 지금의 톱레벨 클래스인 ThreadLocal은 별로 하는게 없어지니 지우고 중첩 key의 이름을 ThreadLocal로 바꾸자

리팩터링하여 Key를 ThreadLocal로 변경
public final class ThreadLocal {
  public ThreadLocal();
  public void set(Object value);
  public Object get();
}

위 API는 get으로 얻은 Object를 실제 타입으로 형변환이 필요하다
처음에 만든 문자열기반과 Key를 사용한 API도 타입 안전하지 않으며 현재 코드도 그렇다
하지만 ThreadLocal을 매개변수화 타입으로 타입안전성이 보장된다

매개변수화하여 타입안전성 확보
public final class ThreadLocal<T> {
  public ThreadLocal();
  public void set(Object value);
  public T get();
}

위 형태는 java.lang.ThreadLocal과 비슷하며 문자열 기반 API의 문제를 해결했고 키 기반 API보다 빠르다
````

#### 더 적합한 데이터 타입이 있거나 새로 작성할 수 있다면 문자열을 쓰고 싶은 유혹을 뿌리쳐라
#### 문자열은 잘못 사용하면 번거롭고, 덜 유연하고, 느리고, 오류 가능성도 크다
#### 문자열을 잘못 사용하는 흔한 예로는 기본 타입, 열거 타입, 혼합 타입이 있다