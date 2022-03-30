# ITEM 36 비트 필드 대신 EnumSet을 사용하라

--------------------------------------------
#### 열거한 값이 단독이 아닌 집합으로 사용될 경우

예전에는 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용했다
```` java
비트별 필드 열거 상수 - 구닥다리 방법

public class Text {
  public static final int STYLE_BOLD              = 1 << 0;
  public static final int STYLE_ITALIC            = 1 << 1;
  public static final int STYLE_UNDERLINE         = 1 << 2;
  public static final int STYLE_STRIKETHROUGH     = 1 << 3;
  
  // 매개변수 styles 는 0개 이상의 STYLE_ 상수를 비트별로 OR한 값
  public void applyStyles(int styles) { ... }
}
````

```` java
비트별 OR을 사용해 여러 상수를 하나의 집합으로 모으는 방법

이렇게 만들어진 집합은 비트필드 라고 부른다

text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
````
비트 필드를 사용하면 비트별 연산으 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행 할 수 있다

하지만 비트 필드는 정수 열거 상수의 단점이 그대로 가지고 있으며 그 외에도 아래와 같은 단점이 있다
* 비트 필드 값이 그대로 출력되면 단순한 정수 영ㄹ거 상수를 출력할 때보다 해석하기가 어렵다
* 비트 필드 하나에 녹아 있ㄴ슨 모든 원소를 순회하기 까다롭다
* 최대 몇 비트가 필요한지를 API 작성 시 미리 예측하여 적절한 타입을 선택해야 한다(int, long)
  * API를 수정하지 않고는 비트 수(32 , 64)를 더 늘릴 수 없기 때문
* 정수 상수보다 열거 타입을 선호하는 개발자도 상수 집합을 주고 받아야 하는 경우에 비트 필드를 쓰기도 한다
  * 하지만 더 좋은 대안으로 EnumSet이 있으니 사용해보자


```` java
EnumSet 비트 필드를 대체하는 현대적 기법

짧고 깔끔하며 안전하다

public class Text {
  public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
  
  //어떤 Set을 넘겨도 상관 없지만 EnumSet이 가장 좋다
  public void applyStyles(Set<Style> styles) { ... }
}

````
* EnumSet 클래스는 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다
* Set 인터페이스를 완벽히 구현하고 타입 안전하며 다른 어떤 Set 구현체와도 함께 사용 가능하다
* 하지만 EnumSet의 내부는 비트 벡터로 구현되어 있어서 원소가 총 64개 이하면 (대부분의 경우) EnumSet 전체를 long 변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여준다

```` java
applyStyles 메서드에 EnumSet 인스턴스를 건네는 클라이언트 코드

text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
````

* EnumSet은 집합 생성 등 다양한 기능의 정적 팩터리를 제공하는데 위 코드에서는 그 중 of 메서드를 사용했다
* applyStyles 메서드가 EnumSet<Style>이 아닌 Set<Style>을 받은 이유를 생각해보자
  * 모든 클라이언트가 EnumSet을 건네는 상황이라고 해도 이왕이면 인터페이스로 받는게 좋은 습관이다
  * 이렇게 작성해야 만약 다른 Set 구현체를 넘겨도 처리가 된다

#### 열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도 비트 필드를 사용할 이유는 없다
#### EnumSet 클래스가 비트 필드 수준의 명료함과 성능을 제공하고 열거 타입의 장점까지 선사한다
#### EnumSet의 유일한 단점이라면 불변 EnumSet을 만들수 없다는것(java 9 기준)
#### 추후 배포시 변경 될 가능성도 있으니 그때까지는 Collections.unmodifiableSet으로 EnumSet을 감싸 사용할 수 있다