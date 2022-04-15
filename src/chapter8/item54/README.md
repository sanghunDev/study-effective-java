# ITEM 54 null이 아닌 빈 컬렉션이나 배열을 반환하라

--------------------------------------------

```` java
나쁜예제 
- 컬렉션이 비었으면 null을 반환한다

/***
* @return 매장 안의 모든 치즈 목록을 반환한다
* 단, 재고가 하나도 없다면 null을 반환한다
*/
public List<Cheese> getCheese() {
  return cheeseInStock.isEmpty() ? null : new ArrayList<>(cheeseInStock);
}

사실 재고가 없다고 해서 특별히 취급할 이유는 없다
하지만 위 코드처럼 null을 반환하게 되면 아래의 코드 처럼 클라이언트가 null을 처리하는 코드를 추가해야된다

List<Cheese> cheeses = shop.getCheese();
if (cheeses != null && cheeses.contains(Cheese.STILTON))
  System.out.println("null 까지 처리해야 뜬다");

컬렉션이나 배열 같은 컨테이너가 비었을 때 null을 반환하는 메서드를 사용할 때면 항시 이와 같은 방어 코드를 넣어줘야 한다  
````
클라이언트에서 방어 코드를 빼먹으면 오류가 발생할 수 있다
* 객체가 0개일 가능성이 거의 없는 상황에서는 수년 뒤에야 오류가 발생하기도 한다
* null을 반환하려면 반환하는 쪽에서도 이 상황을 특별히 취급해줘야 해서 코드가 더 복잡해진다

빈 컨테이너를 할당하는데도 비용이 들기 때문에 null을 반환하는게 낫다는 주장도 있지만 이건 잘못된 주장이다
1. 성능 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한 이정도 성능 차이는 신경쓸 수준이 못 된다
2. 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다
```` java
아래는 빈 컬렉션을 반환하는 전형적인 코드다
(대부분 이런식으로 처리하면 된다)

public List<Cheese> getCheeses() {
  return new ArraryList<>(cheeseInStock);
}
````

작은 가능성이지만 사용 패턴에 따라 빈 컬렉션 할당이 눈에 띄는 성능 저하를 일으키는 경우도 있다
* 이럴땐 매번 똑같은 빈 불변 컬렉션을 반환하면 된다
  * 불변 객체는 자유롭게 공유해도 안전하다
    * 아래 코드의 Collections.emptyList가 좋은 예다, 집합이 필요하면 Collections.emptySet , 맵이 필요하면 Collections.emptyMap 을 사용하면 된다
    * 하지만 이 역시 최적화에 해당하기 때문에 꼭 필요한 경우에만 사용하자
    * 최적화가 필요하다고 판단되면 수정 전과 후의 성능을 측정하여 실제로 성능이 개선 되는지 꼭 확인하자
```` java
최적화
- 빈 컬렉션을 매번 새로 할당하지 않도록 했다

public List<Cheese> getCheeses() {
  return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArraryList<>(cheeseInStock);
}
````

배열을 사용하는 경우도 마찬가지다
* 절대 null을 반환하지 말고 길이가 0인 배열을 반환하라
* 보통은 단순한 정확한 길이의 배열을 반환하기만 하면 된다
  * 그 길이가 0일수도 있을 뿐
* 아래 코드의 toArray 메서드에 건넨 길이 0짜리 배열은 우리가 원하는 반환타입을 알려주는 역할을 한다(Cheese[])
```` java
public Cheese[] getCheeses() {
  return cheesesInStock.toArray(new Cheese[0]);
}
````

위 방식이 성능을 저하를 일으킬거 같으면 길이 0짜리 배열을 미리 선언해두고 매번 그 배열을 반환하면 된다
길이 0인 배열은 모두 불변이기 때문이다

```` java
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
  return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}

이 최적화 버전의 getCheeses는 항상 EMPTY_CHEESE_ARRAY를 인수로 넘겨 toArray를 호출한다
따라서 cheesesInStock이 비었을 때 언제나 EMPTY_CHEESE_ARRAY를 반환하게 된다
단순히 성능을 개선할 목적이라면 toArray에 넘기는 배열을 미리 할당하는건 추천하지 않는다
오히려 성능이 떨어진다는 연구 결과도 있다

나쁜예 - 배열을 미리 할당하면 성능이 나빠진다
return cheesesInStock.toArray(new Cheese[cheesesInStock.size()]);
````
#### null이 아닌 빈 배열이나 컬렉션을 반환하자 null을 반환하는 API는 사용하기 어렵고 오류 처리 코드도 늘어나며 성능이 좋은것도 아니다