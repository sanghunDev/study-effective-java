# ITEM 32 제네릭과 가변인수를 함께 쓸 때는 신중하라

--------------------------------------------

#### 가변인수
* 가변인수를 사용하면 메서드에 넘기는 인수의 개수를 클라이언트가 조절 가능
* 가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 하나 생긴다
  * 이 배열이 내부로 감춰 지는게 아니라 클라이언트에 노출되는 문제가 있다
    * 따라서 varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 경고가 발생한다
* 거의 대부분의 제네릭과 매개변수화 타입은 실체화 되지 않는다
  * 실체화 불가 타입은 컴파일 타임보다 런타임시에 타입에 대한 정보를 적게 담고 있다
  * 메서드를 선언할때 실체화 불가 티입으로 varargs 매개변수를 선언하면 경고가 출력된다
  
```` java
가변인수 메서드 호출시에도 varargs 매개변수가 실체화 불가 타입이면 아래 경고가 나타난다

warning : [unchecked] possible heap pollution from parameterized vararg type List<String>
````
* 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다
  * 타입이 다른 객체를 참조하면 컴파일러가 자동으로 생성한 형변환이 실패할 수도 있게된다
    * 따라서 제네릭의 강점인 타입 안정성이 보장되지 않는다

```` java
타입 안정성이 깨지는 상황

static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;               //타입이 다른 객체를 참조하여 힙 오염 발생
    String s = stringLists[0].get(0);   //자동 형변환에 실패하여 ClassCastException 발생(타입 안정성이 깨짐)
}
````
위와 같이 타입 안정성이 깨지는 상황이 발생하므로 제네릭 varargs 배열 매개변수에 값을 저장하는건 안전하지 않다

#### 만약 메서드가 타입 안정성을 보장할 수 있다면 메서드 작성시에 @SafeVarargs 어노테이션을 사용하자
* 위 어노테이션을 사용하면 타입에 대한 안정성이 보장된다고 컴파일러는 판단하고 경고를 발생시키지 않는다
* 확실하게 타입이 안전한게 아니라면 절대 사용하지 말자
* 메서드가 타입 안전한지 확인
  * 가변인수 메서드를 호출할때 varargs 매개변수를 저장하는 배열이 생기는것을 기억한다
  * 메서드가 배열에 아무것도 저장하지 않고 해당 배열의 참조가 밖으로 노출되지 않으면 타입이 안전한다
    * 신뢰 할 수 없는 코드가 배열에 접근이 불가능한지 확인하자
  * 본래의 목적 대로 순수하게 인수를 전달하는 일만 한다면 해당 메서드는 안전한것이다
* 하지만 varargs 매개변수 배열에 값을 저장하지 않아도 타입 안정성이 깨질 수 있다
  * 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일 타임에 결정된다
  * 해당 시점에 컴파일러에게 충분한 정보가 없어서 타입을 잘 못 반환이 가능하다
  * 자신의 varargs 매개변수 배열을 그대로 반환하면 힙 오염을 이 메서드를 호출한 곳의 콜스택까지 전이가 가능하다

```` java
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return toArray(a, b);
        case 1: return toArray(a, c);
        case 2: return toArray(b, c);
    }
    throw new AssertionError(); //여기까지 안온다
}
````
위 예제는 T타입 인수를 3개 받고 그 중에서 2개를 무작위로 골라서 담은 배열을 반환한다

제네릭 가변 인수를 받는 toArray() 메서드를 호출 하지만 않으면 위험한 건 없다

하지만 toArray()를 호출하면 아래와 같은 위험이 생긴다
* 컴파일러는 toArray()에 넘길 T 인스턴스를 2개 담을 varargs 매개변수 배열을 만든다
* 해당 배열의 타입은 어떤 타입의 객체가 넘어 오더라도 받을 수 있는 가장 구체적인 타입인 Object[]이다
* toArray() 메서드가 돌려준 배열이 그대로 pickTwo를 호출한 클라이언트에 전달된다
* pickTwo는 항상 Object[] 배열을 반환한다
```` java
public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
````
* 위와 같이 pickTwo를 호출 후 String[] 타입의 attributes 에 Object[] 타입의 반환값을 저장하므로 ClassCastException 이 발생한다
  * String[] 으로 형변환 하는 코드를 컴파일러가 자동으로 만드는데 Object[]는 String[]의 하위 타입이 아니기 때문이다
* 이 와 같은 예제에서 알 수 있는것은 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다는 것이다
  * 이런 경우 예외가 두가지가 있다
  * @SafeVarargs로 제대로 어노테이트된 또 다른 varargs 메서드에 넘기는 경우
  * 배열 내용의 일부 함수를 호출만 하는 일반 메서드에 넘기는 경우(varargs를 받지 않는 경우)

아래는 제네릭 varargs 매개변수를 안전하게 사용하는 예제이다

```` java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    
    for (List<? extends T> list : lists)
        result.addAll(list);
    
    return result;
}
````
* 위 예제에서 flatten() 는 임의 개수의 리스트를 인수로 받는다
* 받은 순서대로 list의 모든 원소를 새로운 result에 옮겨 담아서 반환한다
* 메서드에 @SafeVarargs 어노테이션이 달려 있으니 선언부 사용부 모두 경고가 출력되지 않는다
* @SafeVarargs를 사용해야 하는 경우는 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 사용하면 된다
  * 안전하지 않은 varargs 메서드에는 절대 달지 말라는 말이다
  * 안전한 제네릭 varargs 메서드를 만들려면 아래 두개의 규칙은 꼭 지키자
    * varargs 매개변수 배열에 아무것도 저장하지 않는다
    * 해당 배열이나 복제본을 신뢰할 수 없는 코드에 노출하지 않는다
  * 또한 재정의 할 수 없는 메서드에만 사용해야 안전하다, 재정의를 한 메서드는 안전한지 보장이 불가능 하기 때문이다
  * java8에서는 정적 메서드와 final 인스턴스에서만 사용 가능하다
  * java9 부터는 private 인스턴스 메서드에도 사용 가능하다

@SafeVarargs를 사용하지 않고 실제로는 배열이지만 varargs 매개변수를 List 매개변수로 바꾸는 방법도 있다

```` java
@SafeVarargs
static <T> List<T> flatten(List<List<? extends T>> lists) {
    List<T> result = new ArrayList<>();
    
    for (List<? extends T> list : lists)
        result.addAll(list);
    
    return result;
}
````
아래처럼 정적 팩터리 메서드 List.of를 사용하여 임의 개수의 인수를 넘길 수 있다
* List.of 메서드에는 @SafeVarargs 어노테이션이 달려 있다

```` java
audience = flatten(List.of(friends, romans, countrymen));
````

위와 같은 방식을 활용하면 컴파일러가 메서드의 타입 안정성을 검증 가능하다

또한 @SafeVarargs 어노테이션을 직접 달지 않아도 괜찮아서 실수로 해당 어노테이션을 사용할 일도 없다

해당 방식의 단점은 클라이언트 코드가 조금 지저분하며 약간 느릴 수 있다

위에서 작성했던 pickTwo 메서드도 아래 처럼 리팩토링이 가능하다


```` java
static <T> List<T> pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return List.of(a, b);
        case 1: return List.of(a, c);
        case 2: return List.of(b, c);
    }
    throw new AssertionError();
}
````

```` java
public static void main(String[] args) {
    List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
}
````
이렇게 변경 하면 배열 없이 제네릭만 사용하게 되었으며 안전하다

