# ITEM 37 oridinal 인덱싱 대신 EnumMap을 사용하라

--------------------------------------------
#### 

배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인덱스를 얻는 코드가 있다

```` java
식물을 간단하게 나타낸 클래스

class Plant {
  enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }
  
  final String name;
  final LifeCycle lifeCycle;
  
  Plant(String name, LifeCycle lifeCycle) {
    this.name = name;
    this.lifeCycle = lifeCycle;
  }
  
  @Override public String toString() {
    return name;
  }
}
````

정원에 심은 식물들을 배열 하나로 관리하고 이들을 생애주기별로 묶어보자
* 생애주기별(한애살이, 여러해살이, 두해살이)별로 총 3개의 집합 생성
* 정원은 돌면서 각 식물을 해당하는 집합에 넣는다 

이때 어떤 개발자는 집합들을 배열 하나에 넣고 생애주기의 ordinal 값을 그 배열의 인덱스로 사용하려 할 것이다

```` java
ordinal()을 배열 인덱스로 사용 (안좋은 코드)

Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];

    for (int i = 0; i < plantsByLifeCycle.length; i++) {
        plantsByLifeCycle[i] = new HashSet<>();
    }
    
    for (Plant p : garden){
        plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
    }
    
    //결과 출력
    for (int i = 0; i < plantsByLifeCycle.length; i++) {
        System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
    } 
````
위 코드는 동작은 하지만 문제가 많다
* 배열은 제네릭과 호환되지 않아 비검사 형변환을 수행해야하며 깔끔하게 컴파일되지 않을것이다
* 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다
* 제일 큰 문제는 정확한 정숫값을 사용한다는 것을 개발자가 직접 보증해야 한다는 것이다
  * 정수는 열거 타입과 다르게 타입 안전하지 않다
  * 잘못된 값을 사용하더라도 잘못된 동작을 그대로 수행하거나 운이 좋은 경우 ArrayIndexOutOfBoundsException이 출력 될 것

위 코드에서 배열은 열거타입 상수를 값으로 매핑하는 일을 하므로 Map으로 대체가 된다
* 열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체인 EnumMap을 사용해보자

```` java
EnumMap을 사용해 데이터와 열거 타입을 맵핑

Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
    plantsByLifeCycle.put(lc, new HashSet<>());
}

for (Plant p : garden) {
    plantsByLifeCycle.get(p.lifeCycle).add(p);
}

System.out.println(plantsByLifeCycle);
````
더 짧고 명료하고 안전하고 성능도 비슷하다
* 안전하지 않은 형변환을 사용하지 않고 맵의 키인 열거 타입이 그 자체로 출력용 문자열을 제공하니 출력 결과에 레이블을 달 필요도 없다
* 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천 봉쇄된다
* EnumMap의 성능이 ordinal을 쓴 배열에 비교되는 이유는 내부에서 배열을 사용하기 때문이다
  * 내부 구현 방식을 안으로 숨겨서 Map의 타입 안전성과 배열의 성능을 모두 얻었다
* 위 코드에서 EumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰이며 런타임 제네릭 타입 정보를 제공한다

스트림을 사용하면 코드를 더 줄일 수 있다

```` java
앞의 예제를 모방한 가장 단순한 스트림 코드 - EnumMap 미사용

EnumMap이 아닌 고유한 맵 구현체를 사용해서 EnumMap을 써서 얻은 공간과 성능 이점이 사라지는 문제가 있다
매개변수 3개짜리 Collectors.groupingBy 메서드는 mapFactory 매개변수에 원하는 맵 구현체를 명시해 호출 할 수 있다

System.out.println(Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle)));

EnumMap을 이용해 데이터와 열거 타입을 맵핑

System.out.println(Arrays.stream(garden).collect(grouping?By(p -> p.lifeCycle, () -> new EnumMap<>(LifeCycle.class), toSet())));

이런 단순한 예제에서는 최적화가 필요 없지만 맵을 많이 사용한다면 최적화가 필요할 것이다

````