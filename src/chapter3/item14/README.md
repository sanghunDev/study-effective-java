# Item 14. Comparable을 구현할지 고려하라

### Comparable 인터페이스의 compareTo는 두가지만 제외하면 equals와 같다

-   compareTo는 단순 동치성 비교에 더해 순서까지 비교 가능하며 제네릭하다
-   Comparable을 구현한 객체들의 배열은 손쉽게 정렬이 가능하다
    -   Comparable을 구현 했다는것은 해당 클래스의 인스턴스에게 자연적인 순서가 존재 한다는 것
-   검색, 극단값 계산, 자동 정렬되는 컬렉션 관리도 쉽게 가능
-   알파벳 숫자 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable을 구현하자

#### compareTo 메서드의 일반 규약은 equals의 규약과 비슷하다

-   이 객체와 주어진 객체의 순서를 비교한다
-   이 객체가 주어진 객체보다 작으면 음의 정수를, 같으면 0을 크면 양의 정수를 반환한다
-   이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다
-   compareTo는 타입이 다른 객체를 신경쓰지 않아도 된다
    -   equals 메서드는 모든 객체에 대해 전역 동치관계를 부여한다

#### compareTo 규약

-   Comparable을 구현한 클래스는 모든 x,y에 대해 sgn(x.compareTo(y)) == -sgn(y.compareTo(x))여야 한다 (따라서 x.compareTo(y)는 y.compareTo(x)가 예외를 던질때에 한해 예외를 던져야 한다)
    -   두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다
        -   첫번째 객체가 두번째 객체보다 작으면 두번째가 첫번째 보다 커야한다

-   Comparable을 구현한 클래스는 추이성을 보장해야 한다. 즉, (x.compareTo(y) > 0 && y.compareTo(z) > 0 이면 x.compareTo(z) > 0이다
    -   첫번째가 두번째보다 크고 두번째가 세번째보다 크면 첫번째는 세번째보다 커야한다

-   Comparable을 구현한 클래스는 모든 z에 대해 x.compareTo(y) == 0이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z))다
    -   크기가 같은 객체들끼리는 어떤 객체와 비교해도 항상 같아야 한다

-   이번 권고가 필수는 아니지만 꼭 지키는게 좋다
    -   (x.compareTo(y) == 0) == (x.equals(y))여야 한다.
    -   Comparable을 구현하고 이 권고를 지키지 않는 모든 클래스는 그 사실을 명시해야 한다
        -   주의: 이 클래스의 순서는 equals 메서드와 일관되지 않다

#### 정리

-   순서를 고려해야 하는 값 클래스라면 꼭 Comparable 인터페이스를 구현하자
-   compareTo 메서드에서 필드의 값을 비교할 때 < 와 > 연산자는 쓰지 말자
    -   예전 방식으로 거추장스럽고 오류를 유발한다
-   박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자\*\*