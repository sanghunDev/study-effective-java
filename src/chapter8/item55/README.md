# ITEM 55 옵셔널 반환은 신중히 하라

--------------------------------------------

자바8 이전의 메서드가 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 두가지 선택지
1. 예외를 던지는 방법
   1. 정말로 예외적인 상황에서만 사용해야 하며 예외를 생성할 때 스택 추적 전체를 캡쳐하므로 비용도 만만치 않다
2. 반환 타입이 객체 참조라면 null을 반환하는 것
   1. null을 반환할 수 있는 메서드를 호출 할 때는 null이 반환될 일이 절대 없다고 확신하지 않는 한 별도로 null처리 코드를 작성해야 한다
   2. null처리를 무시하고 반환된 null값을 어딘가에 저장해두면 근본적인 원인과 상관없이 언젠가 NPE가 발생 가능

자바8 부터는 하나의 선택지가 더 생겼다
* 옵셔널을 반환하는 메서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며 null을 반환하는 메서드보다 오류 가능성이 적다
```` java
Optional<T> 는 null이 아닌 T 타입 참조를 하나 담거나 아무것도 담지 않을 수 있다

아무것도 담지 않은 옵셔널은 '비었다'고 한다
어떠한 값을 담고 있는 옵셔널은 '비지 않았다'고 한다

옵셔널은 원소를 최대 1개 가질 수 있는 불변 컬렉션이다
Optional<T>가 Collection<T>를 구현하지는 않았지만 원칙적으로 그렇다는 말

보통은 T를 반환해야 하지만 특정 조건에서 아무것도 반환하지 않아야 하는 경우 T 대신 Optional<T>를 반환하자
```` 


옵셔널 미사용 코드
````
java
컬렉션에서 최댓값을 구한다(컬렉션이 비었으면 예외를 던진다)

public static <E extends Comparable<E>> max(Collection<E> c) {
  if(c.isEmpty())
    throw new IllegalArgumentException("빈 컬렉션");
    
  E result = null;
  for(E e : c)
    if(result == null || e.compareTo(result) > 0)
      result = Objects.requireNonNull(e);
      
 return result;
}

빈 컬렉션이 넘어오면 IllegalArgumentException을 던진다
````

옵셔널 사용 코드
````
java
컬렉션에서 최댓값을 구해 Optional<E>로 반환한다

public static <E extends Comparable<E>> Optional max(Collection<E> c) {
  if(c.isEmpty())
    return Optional.empty();
        
  E result = null;
  for(E e : c)
    if(result == null || e.compareTo(result) > 0)
      result = Objects.requireNonNull(e);
      
 return Optional.of(result);
}
````
옵셔널로 반환 하는건 어렵지 않으며 적절한 정적 팩터리를 사용해 옵셔널을 생성해 주기만 하면 된다

위 코드에서는 두가지 팩터리가 사용되었다
* 빈 옵셔널은 Optional.empty()로 만들고 값이 든 옵셔널은 Optional.of(value)로 생성했다
* Optional.of(value)에 null을 넣으면 NPE를 던지니 조심하자
* null값도 허용하는 옵셔널을 만들려면 Optional.ofNullable(value)를 사용하면 된다

#### 옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말자 null을 반환하는건 옵셔널을 도입한 취지를 완전히 무시하는것이다


스트림의 종단 연산 중 상당수는 옵셔널을 반환한다

위에서 작성했던 max 메서드를 스트림버전으로 바꿔보자
```` java
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
  return c.stream().max(Comparator.naturalOrder());
}

비교자를 명시적으로 전달해야 하지만 Stream의 max 연산이 우리에게 필요한 옵셔널을 생성해준다
````

언제 옵셔널을 사용해야할까?
* 옵셔널은 검사 예외와 취지가 비슷하다
  * 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다
* 비검사 예외를 던지거나 null을 반환한다면 API 사용자가 그 사실을 인지하지 못해 문제가 생길 수 있다
* 위와 다르게 검사 예외를 던지면 클라이언트는 반드시 대처하는 코드를 넣어야 한다
* 검사 예외와 비슷하게 옵셔널을 반환하면 클라이언트는 값을 받지 못했을때 취할 행동을 선택해야 한다
```` java
기본값을 설정

String lastWordInLexicon = max(words).orElse("단어없음");
````
* 상황에 맞는 예외를 던질 수도 있다
  * 실제 예외가아니라 예외 팩터리를 건네서 실제로 예외가 발생하지 않는 한 예외 생성 비용은 들지 않는다

```` java
원하는 예외를 던질 수 있다

Toy myToy = max(toys).orElseThrow(TemperTantrumException::new);
````
* 옵셔널에 항상 값이 채워져 있다고 확신하면 바로 값을 꺼내서 사용해도 된다
  * 하지만 잘못 판단한거면 NoSuchElementException이 발생 한다

```` java
항상 값이 채워져 있다고 가정한다

Element lastNobleGas = max(Elements.NOBLE_GASES).get();
````

모든 경우에 반환값으로 옵셔널을 사용하는건 좋지 않다
* 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안된다
  * 빈 Optional<List<T>>를 반환하기 보다는 빈 List<T>를 반환하는게 좋다
  * 빈 컨테이너를 그대로 반환하면 클라이언트에 옵셔널 처리 코드를 넣지 않아도 된다
* 메서드 반환타입을 T 대신 Optional<T>로 선언하는 기본적인 경우
  * 결과가 없을 수 있으며 클라이언트가 이 상황을 특별하게 처리해야 하는 경우
    * 하지만 Optional<T>를 반환하면 대가가 따른다
    * Optional도 새로 할당하고 초기화 해야하는 객체이며 값을 꺼내려면 메서드 호출이 필요하니 한 단계를 더 거치는 셈이다
    * 따라서 성능이 중요한 경우에는 Optional이 맞지 않을 수도 있다

박싱된 기본 타입을 담는 옵셔널은 기본 타입 자체보다 무거울 수 밖에 없다
* 값을 두번이나 감싸기 때문이다
* 그래서 int, long, double 전용 옵셔널이 준비되어있다
  * OptionalInt, OptionalLong, OptionalDouble 이며 이 옵셔널들도 Optional<T>가 제공하는 메서드를 거의 다 제공한다
* 위의 옵셔널들을 사용하고 박싱된 기본 타입을 담은 옵셔널을 반환하는 일은 없도록 하자

옵셔널을 인스턴스 필드에 저장해두는게 필요할 때가 있을까?
* 이런 경우 대부분은 필수 필드를 갖는 클래스와 이를 확장해 선택적 필드를 추가한 하위 클래스를 따로 만들어야 함을 암시는 나쁜냄새다
* 하지만 예외적인 경우로 인스턴스 필드 중 상당수는 필수필드가 아니며 그 필드들이 기본타입이라 값이 없음을 나타낼 방법이 없는 경우 선택적 필드의 게터 메서드들이 옵셔널을 반환하게 해주면 좋을것이다
  * 이런 경우 필드 자체를 옵셔널로 선언하는것도 좋은 방법

#### 옵셔널을 컬렉션의 키, 값, 원소나 배열의 원소로 사용하는게 적절한 상황은 거의 없다
#### 값을 반환하지 못할 가능성이 있고 호출할 때마다 반환값이 없을 가능성을 염두에 둬야 하는 메서드라면 옵셔널을 반환해야 할 상황일 수 있다
#### 하지만 옵셔널 반환에는 성능 저하가 뒤따르니 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수 있다
#### 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드물다