# ITEM 52 다중정의는 신중히 사용하라

--------------------------------------------

```` java
컬렉션을 집합, 리스트, 그 외로 구분하는 프로그램

public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }
    public static String classify(List<?> list) {
        return "리스트";
    }
    public static String classify(Collection<?> c) {
        return "그 외";
    }
    
    public static void main(String[] srgs) {
        Collection<?>[] collections = {
            new HashSet<String>().
            new ArrayList<BigInteger>(),
            new HashMap<String, String>().values()
        };
        
        for (Collection<?> c : collections)
            System.out.println(classify(c));
    }
}

"집합", "리스트", "그 외"를 차례로 출력할 것 같지만 실제로는 "그 외"만 세 번 연달아 출력한다
- 다중정의(오버로딩)된 세 classify 중 어느 메서드를 호출할지가 컴파일타임에 정해지기 때문이다

컴파일타임에는 for문 안의 c는 항상 Collection<?> 타입이다
런타임에는 타입이 매번 달라지지만 호출할 메서드를 선택하는 데는 영향을 주지 못 한다
따라서 컴파일타임의 매개변수 타입을 기준으로 항상 세 번째 메서드인 classify(Collection<?>)만 호출하는 것
````
위와 같이 직관과 어긋나는 이유는 재정의한 메서드는 동적으로 선택되고 다중정의한 메서드는 정적으로 선택되기 때문이다

메서드를 재정의했다면 해당 객체의 런타임 타입이 어떤 메서드를 호출할지의 기준이 된다
* 메서드 재정의란 상위 클래스가 정의한 것과 똑같은 시그니처의 메서드를 하위 클래스에서 다시 정의한 것을 말한다

메서드를 재정의한 다음 하위 클래스의 인스턴스에서 그 메서드를 호출하면 재정의한 메서드가 실행된다

컴파일 타임에 그 인스턴스의 타입이 무엇이었냐는 상관없다

아래의 코드는 이 상황을 구체적으로 보여준다
```` java
재정의된 메서드 호출 메커니즘

class Wine {
    String name() { return "포도주"; }
}
class SparklingWine extends Wine {
    @Override String name() { return "발포성 포도주"; }
}
class Champagne extends SparklingWine {
    @Override String name() { return "샴페인"; }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
            new Wine(), new SparklingWine(), new Champagne());
            
        for (Wine wine : wineList)
            System.out.println(wine.name());
    }
}

Wine 클래스에 정의된 name 메서드는 하위 클래스인 SparklingWine과 Champagne에서 재정의된다

예상한 것처럼 이 프로그램은 "포도주", "발포성 포도주", "샴페인"을 차례로 출력한다

for 문에서의 컴파일타임 타입이 모두 Wine인 것에 무관하게 항상 '가장 하위에서 정의한' 재정의 메서드가 실행되는 것이다

````
다중 정의된 메서드 사이에서는 객체의 런타임 타입은 전혀 중요치 않다
* 선택은 컴파일타임에, 오직 매개변수의 컴파일타임 타입에 의해 이뤄진다

컬렉션을 집합, 리스트, 그 외로 구분하는 프로그램의 원래 의도는 매개변수의 런타임 타입에 기초해 적절한 다중정의 메서드로 자동 분배하는 것이었다
* Wine의 예에서의 name 메서드와 같은 동작을 하길 원했으나 다중정의는 이렇게 동작하지 않는다
* 이 문제는 정적 메서드를 사용해도 좋다면 CollectionClassifier의 모든 classify 메서드를 하나로 합친 후 instanceof로 명시적으로 검사하면 말끔히 해결된다

```` java

public static String classify(Collection<?> c) {
    return c instanceof Set ? "집합" : 
           c instanceof List ? "리스트" : "그 외";
}
````

개발자에게는 재정의가 정상적인 동작 방식이고 다중정의가 예외적인 동작 방식으로 보일 것이다
* 재정의한 메서드는 개발자가 기대한대로 동작하지만 Collection 예에서처럼 다중정의한 메서드는 이러한 기대를 가볍게 무시한다

헷갈릴 수 있는 코드는 작성하지 않는게 좋으며 특히나 공개 API라면 더욱 신경 써야 한다
* API 사용자가 매개변수를 넘기면서 어떤 다중 정의 메서드가 호출될지를 모른다면 프로그램이 오동작하기 쉽다
* 런타임시 동작이 이상할 수 있고 API 사용자는 문제를 찾느라 오랜 시간을 보내야 될 것이다
* 따라서 다중정의가 혼돈을 일으키는 상황을 피해야 한다

정확히 어떻게 사용했을 때 다중정의가 혼란을 주느냐에 대해서는 논란의 여지가 있지만 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자
* 가변인수를 사용하는 메서드라면 다중정의를 아예 하지 말아야 한다(item 53은 예외)
* 이 규칙을 잘 지키면 어떤 다중정의 메서드가 호출될지 헷갈릴 일은 없을 것이다
* 다중정의 하는 대신 메서드 이름을 다르게 지어주는 길도 항상 열려있으니 고려하자

생성자는 이름을 다르게 지을수 없으니 두 번째 생성자부터는 무조건 다중정의가 된다
* 하지만 정적 팩터리로 대안을 활용할 수 있는 경우가 많다
* 생성자는 재정의할 수 없으니 다중정의와 재정의가 혼용될 걱정은 넣어둬도 된다

여러 생성자가 같은 수의 매개변수를 받아야 하는 경우를 완전히 피해갈 수 없으니 그런 경우의 대책을 알아보자
* 매개변수의 수가 같은 다중정의 메서드가 많더라도 어떤게 주어진 매개변수 집합을 처리할지 정확하게 알면 헷갈릴 일이 없을것이다
  * 매개변수 중 하나 이상이 근본적으로 다르다면 헷갈릴 일이 없다는것
  * 근본적으로 다르다는건 두 타입의(null이 아닌) 값을 서로 어느 쪽으로든 형변환 할 수 없다는 것
  * 이 조건이 충족되면 어느 다중정의 메서드를 호출할지가 매개변수들의 런타임 타입만으로 결정된다
    * 컴파일타임타입에는 영향을 받지 않게 되고 혼란을 주는 주된 원인이 사라진다

```` java
자바 4까지는 모든 기본 타입이 모든 참조 타입과 근본적으로 달랐다
자바 5부터 오토박싱이 도입되면서 평화롭던 시대가 막을 내렸다

public class SetList {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        List<Integer> list = new ArrayList<>();
        
        for (int i=-3; i<3; i++) {
            set.add(i);
            list.add(i);
        }
        
        for(int i=0; i<3; i++) {
            set.remove(i);
            list.remove(i);
        }
        System.out.println(set + " " + list);
    }
}

이 프로그램은 -3 ~ 2 까지의 정수를 정렬된 집합과 리스트에 각각 추가하고 양쪽에 똑같이 remove 메서드를 세번 호출했다

그러면 이 프로그램은 음이 아닌 값 (0, 1, 2)를 제거한 후 [-3, -2, -1] [-3, -2, -1]을 출력할거라 생각한다

실제로는 집합에서 음이 아닌 값을 제거하고 리스트에서는 홀수를 제거한 후 [-3, -2, -1] [-2, 0, 2]를 출력한다

이 결과에 여러분은 혼란에 빠졌을 것이다

왜 이런 결과가 나왔을까? 

set.remove(i)의 시그니처는 remove(Object)다
다중정의된 다른 메서드가 없으니 기대한 대로 동작하여 집합에서 0 이상의 수들을 제거한다
한편 list.remove(i)는 다중정의된 remove(int index)를 선택한다
그런데 이 remove는 지정한 위치의 원소를 제거하는 기능을 수행한다
리스트의 처음 원소가 [-3, -2, -1, 0, 1, 2]이고 차례로 0번째, 1번째, 2번째 원소를 제거하면 [-2, 0, 2]가 남는것이다

이 문제는 list.remove의 인수를 Integer로 형변환하여 올바른 다중정의 메서드를 선택하게 하면 해결된다
혹은 Integer.valueOf를 이용해 i를 Integer로 변환한 후 list.remove에 전달해도 된다
어느 방식을 쓰든 원래 기대한 [-3, -2, -1] [-3, -2, -1]을 출력한다

for (int i=0; i<3; i++) {
    set.remove(i);
    list.remove((Integer) i);   // 또는 remove(Integer.valueOf(i))
}

이 예제가 혼란스러운 이유는 List<E> 인터페이스가 remove(Object)와 remove(int)를 다중정의 했기 때문이다
제네릭이 도입되기 전인 자바 4까지의 List에서는 Object와 int가 근본적으로 달라서 문제가 없었다
제네릭과 오토박싱이 등장하면서 두 메서드의 매개변수 타입이 더는 근본적으로 다르지 않게 되었다
정리하자면 자바 언어에 제네릭과 오토박싱을 더한 결과 List 인터페이스가 취약해졌다

다행히 같은 피해를 입은 API는 거의 없지만 다중정의시 주의를 기울여야 할 근거로는 충분하다
````

```` java
자바 8에서 도입한 람다와 메서드 참조 역시 다중정의 시의 혼란을 키웠다

//Thread의 생성자 호출
new Thread(System.out::println).start();

//ExecutorService의 submit 메서드 호출
ExecutorService exec = Executors.newCachedThreadPool();
exec.submit(System.out::println);

1번과 2번이 비슷하게 생겼지만 2번은 컴파일 오류가 난다
넘겨진 인수는 모두 System.out::println으로 똑같으며 양쪽 모두 Runnable을 받는 형제 메서드를 다중정의하고 있다

왜 2번은 실패할까?

원인은 바로 submit 다중정의 메서드 중에는 Callable<T>를 받는 메서드도 있다는데 있다
하지만 모든 println이 void를 반환하니까 반환값이 있는 Callable과 헷갈릴리 없다고 생각할지도 모르겠다
합리적인 추론이지만 다중정의 해소(resolution; 적절한 다중정의 메서드를 찾는 알고리즘)는 이렇게 동작하지 않는다
놀라운 사실 하나는 만약 println이 다중정의 없이 단 하나만 존재했다면 이 submit 메서드 호출이 제대로 컴파일 됐을거다
지금은 참조된 메서드(println)와 호출한 메서드(submit) 양쪽 다 다중정의되어 다중정의 해소 알고리즘이 우리의 기대처럼 동작하지 않는 상황이다
````
핵심은 다중정의된 메서드(또는 생성자)들이 함수형 인터페이스를 인수로 받을 때 비록 서로 다른 함수형 인터페이스라도 인수 위치가 같으면 혼란이 생긴다는 것이다
* 메서드를 다중정의할 때 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안된다
* 서로 다른 함수형 인터페이스라도 근본적으로 다르지 않다는 뜻이다
* 컴파일 할때 명령줄 스위치로 -Xlint:overloads 를 지정하면 이런 종류의 다중정의를 경고해줄 것이다

Object 외의 클래스 타입과 배열 타입은 근본적으로 다르다
* Serializable 과 Cloneable외의 인터페이스 타입과 배열 타입도 근본적으로 다르다
* String과 throwsable처럼 상위/하위 관계가 아닌 두 클래스는 관련없다(unrelated)고 한다
* 어떤 객체도 관련 없는 두 클래스의 공통 인스턴스가 될 수 없으므로 관련 없는 클래스들끼리도 근본적으로 다르다

#### 프로그래밍 언어가 다중정의를 허용한다고 해서 다중정의를 꼭 활용하라는 뜻은 아니다
#### 일반적으로 매개변수 수가 같을 때는 다중정의를 피하는게 좋다
#### 상황에 따라 특히 생성자라면 이 조언을 따르기가 불가능할 수 있다
#### 그럴 때는 헷갈릴 만한 매개변수는 형변환하여 정확한 다중정의 메서드가 선택되도록 해야 한다
#### 이것이 불가능하면 예컨대 기존 클래스를 수정해 새로운 인터페이스를 구현애햐 할 때는 같은 객체를 입력받는 다중정의 메서드들이 모두 동일하게 동작하도록 만들어야 한다
#### 그렇지 못하면 프로그래머들은 다중정의된 메서드나 생성자를 효과적으로 사용하지 못할 것이고 의도대로 동작하지 않는 이유를 이해하지도 못할 것이다


