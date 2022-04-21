# ITEM 58 전통적인 for 문보다는 for-each문을 사용하라

--------------------------------------------

향상된 for문(enhanced for statement)
* 정식 명칭은 향상된 for문 이지만 for-each문 이라고 부른다
* 반복자와 인덱스 변수를 사용하지 않으니 코드가 깔끔하고 오류가 날 일도 없다
* 하나의 관용구로 컬렉션과 배열을 모두 처리 가능하여 어떤 컨테이너를 다루는지 신경쓰지 않아도 된다

```` java
for (Element e : elements) {
  ... // e로 무언가를 한다
}

: 은 "안의(in)"라고 읽으면 되며 위 코드는 "elements 안의 각 원소 e에 대해"라고 읽는다
````
* 반복 대상이 컬렉션이든 배열이든 for-each문을 사용해도 속도는 그대로다
* for-each문이 만들어내는 코드는 사람이 손으로 최적화한 것과 사실상 같다
* 컬렉션을 중첩 순회하는 경우 for-each문의 이점이 더욱 커진다

```` java
반복문을 중첩하는 경우 흔하게 발생하는 실수


enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }
...
static Collection<Suit> suits = Arrays.asList(Suit.values());
static Collection<Rank> ranks = Arrays.asList(Rank.values());

List<Card> deck = new ArrayList<>();
for(Iterator<Suit> i = suits.iterator(); i.hasNext(); )
  for(Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
    deck.add(new Card(i.next(), j.next()));
  
위 코드에서 문제는 바깥 컬렉션(suits)의 반복자에서 next 메서드가 너무 많이 불리는 것이다
마지막 줄의 i.next()를 보면 숫자 하나당 한 번씩만 불려야 하는데 안쪽 반복문에서 호출되는 바람에 카드 하나당 한번씩 불리고 있다
그래서 숫자가 바닥나면 NoSuchElementException을 던진다
정말 운이 나빠서 바깥 컬렉션의 크기가 안쪽 컬렉션 크기의 배수라면 원하는 일을 수행하지도 않고 예외도 없이 종료된다

같은 버그 다른 증상
enum Face { ONE, TWO, THREE, FOUR, FIVE, SIX }
...
Collection<Face> faces = EnumSet.allOf(Face.class);

for(Iterator<Face> i = faces.iterator(); i.hasNext();)
  for(Iterator<Face> j = faces.iterator(); j.hasNext();)
    System.out.println(i.next() + " " + j.next());

이 프로그램은 예외가 발생하지는 않지만 가능한 조합을 여섯 쌍만 출력하고 끝나버린다


위 두 예제에서 발생한 문제를 해결하려면 아래처럼 바깥 반복문에 바깥 원소를 저장하는 변수를 하나 추가해야 한다

for(Iterator<Suit> i = suits.iterator(); i.hasNext(); )
  Suit suit = i.next();
  for(Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
    deck.add(new Card(suit, j.next()));
  
for-each 문을 사용하면 문제가 간단하게 해결되며 코드도 간결해진다
for(Suit suit : suits)
  for(Rank rank : ranks)
    deck.add(new Card(suit, rank);
````
위와 같이 for-each 문은 다양한 이점이 있지만 사용할 수 없는 상황이 세가지 존재한다
* 파괴적인 필터링(destructive filtering)
  * 컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 반복자의 remove 메서드를 호출해야 한다
  * 자바 8부터는 Collection의 removeIf 메서드를 사용해 컬렉션을 명시적으로 순회하는 일을 피할 수 있다
* 변형(transforming)
  * 리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다
* 병렬반복(parallel iteration)
  * 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다

위 세가지 상황 중 하나에 속할 때는 일반적인 for문을 사용하자 하지만 이번 item에서 나온 문제는 항상 경계하자

for-each문은 컬렉션과 배열, Iterable 인터페이스를 구현한 객체라면 무엇이든 순회 가능하다

```` java
Iterable 인터페이스는 메서드가 단 하나 뿐이다
public interface Iterable<E> {
  Iterable<E> iterator(); //이 객체의 원소들을 순회하는 반복자를 반환한다
}

Iterable을 처음부터 직접 구현하는건 어렵지만 원소들의 묶음을 표현하는 타입을 작성해야 한다면 Iterable을 구현하는 걸 고민해보자
해당 타입에서 Collection 인터페이스는 구현하지 않기로 했더라도 Iterable을 구현해 놓으면 그 타입을 사용하는 개발자가 for-each문을 사용할 때마다 고마워 할 것이다
````

#### 전통적인 for문과 비교했을 때 for-each문은 명료하고 유연하고 버그를 방지한다
#### 성능 저하도 없으니 가능한 모든 곳에서 for문이 아닌 for-each문을 사용하자