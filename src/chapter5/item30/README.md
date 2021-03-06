# ITEM 30 이왕이면 제네릭 메서드로 만들어라

--------------------------------------------

### 제네릭 메서드

* 제네릭 메서드 작성법은 제네릭 클래스 작성법과 비슷하다

```` java
로 타입을 사용한 메서드

public static Set union(Set s1, Set s2) {
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}
````

위의 메서드를 안전하게 만들려면 타입을 안전하게 만들어야 한다

* 메서드 선언에서의 입력2개, 리턴1개 이 원소 타입을 타입 매개변수로 명시한다
* 메서드 내부에서 명시한 타입 매개변수만 사용한다
  * 타입 매개변수 목록은 메서드의 접근제어자와 리턴 타입 사이에 온다

```` java
타입 매개변수 목록 : <E>
리턴 타입 : Set<E>

public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
````

* 제네릭 타입처럼 제네릭 메서드가 더 안전하다
  * 클라이언트에서 매개변수와 리턴타입을 명시적으로 형변환 해야 하는것 보다 안전함
* 형변환 없이 사용할 수 있는게 좋다
* 대부분의 경우 형변환 없이 사용하려면 제네릭 메서드로 만들어야 한다
* 기존 메서드 중 형변환이 필요한 메서는 제네릭 타입을 고려해보자
* 제네릭 메서드로 변경 한다면 클라이언트 코드의 변경 없이 유연하게 확장 가능하게 될 것이다