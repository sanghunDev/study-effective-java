# ITEM 48 스트림 병렬화는 주의해서 적용하라

--------------------------------------------

주류 언어 중, 동시성 프로그래밍 측면에서 자바는 항상 앞서갔다
* 처음 릴리즈된 1996년부터 스레드, 동기화, wait/notify를 지원했다
* 자바 5부터는 동시성 컬렉션인 java.util.concurrent 라이브러리와 실행자(Executor) 프레임워크를 지원했다
* 자바 7부터는 고성능 병렬 분해(parallel decom-position)프레임워크인 포크-조인(fork-join) 패키지를 추가했다
* 자바 8부터는 parallel 메서드만 한 번 호출하면 파이프라인을 병렬 실행할 수 있는 스트림을 지원했다

자바로 동시성 프로그램을 작성하는게 계속 쉬워지고 있지만 이를 올바르고 빠르게 작성하는건 여전히 어렵다

동시성 프로그래밍을 할 때는 안전성(safety)과 응답 가능(liveness) 상태를 유지하려고 해야 하는데 병렬 스트림 파이프라인 프로그래밍에서도 다를 바 없다

```` java
스트림을 사용해 처음 20개의 메르센 소수를 생성하는 프로그램
public static void main(String[] args) {
  primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
          .filter(mersenne -> mersenne.isProbablePrime(50))
          .limit(20)
          .forEach(System.out::println);
}

static Stream<BigInteger> primes() {
  return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}
````
위 프로그램의 속도를 높이기 위해 parallel()을 호출하면 어떻게 될까?
* 오히려 성능 향상이 일어나는게 아니라 아무것도 출력하지 못하고 cpu는 90%나 잡아먹는 상태가 무한히 계속된다(liveness failure)
  * 종국에는 완료될수도 있지만 저자의 말에 따르면 1시간 반이지나 강제종료 시점까지 아무 결과도 출력되지 않았다고 함
  
왜 아무것도 출력하지 못했을까?
* 프로그램이 느려진 원인은 스트림 라이브러리가 이 파이프라인을 병렬화하는 방법을 찾아내지 못했기 때문이다
* 환경이 좋아도 데이터 소스가 Stream.iterate거나 중간 연산으로 limit을 사용하면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다
* 위에서 작성한 코드는 두가지 문제 모두 가지고 있으며 스트림 파이프라인을 마구잡이로 병렬화하면 어떠한 결과가 발생하는지 알려준다

#### 스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위일때 병렬화의 효과가 가장 좋다
* 위 자료구조들은 모두 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어 일을 다수의 스레드에 분배하기에 좋다
* 나누는 작업은 Spliterator가 담당하고 Spliterator 객체는 Stream이나 Iterable의 spliterator 메서드로 얻어올 수 있다
* 또한 중요한 공통점으로 원소들을 순차적으로 실행할 때의 참조 지역성(locality of reference)이 뛰어나다
  * 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻
  * 만약 참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있을 수 있는데 그러면 참조 지역성이 나빠진다
    * 참조 지역성이 낮으면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 대부분 시간을 멍하게 보내게 된다
    * 따라서 참조 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 아주 중요한 요소로 작용한다
  * 참조 지역성이 가장 뛰어난 자료구조는 기본 타입의 배열이다
    * 기본 타입 배열에서는 (참조가 아닌) 데이터 자체가 메모리에 연속해서 저장되기 때문

스트림 파이프라인의 종단 연산 방식도 병렬 수행 효율에 영향을 준다

* 종단 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하면서 순차적인 엿나이라면 파이프라인 병렬 수행의 효과는 제한될수밖에 없다
* 종단 연산 중 병렬화에 가장 적합한 것은 축소(reducation)다
* 축소는 파이프라인에서 만들어진 모든 원소를 하나로 합치는 작업으로 Stream의 reduce 메서드 중 하나 혹인 min, max, count, sum 같이 완성된 형태로 제공되는 메서드 중 하나를 선택해 수행한다
* anyMatch, allMatch, noneMatch처럼 조건에 맞으면 바로 반환되는 메서드도 병렬화에 적합하다 반면 가변 축소(mutable reduction)를 수행하는 Stream의 collect 메서드는 병렬화에 적합하지 않다
* 컬렉션들을 합치는 부담이 크기 때문이다
* 직접 구현한 Steam, Iterable, Collection이 병렬화 이점을 제대로 누리게 하고 싶다면 spliterator 메서드를 반드시 재정의하고 결과 스트림의 병렬화 성능을 강도 높게 테스트하라 고효율 spliterator를 작성하기란 상당한 난이도의 일이다

스트림을 잘못 병렬화하면 (응답 불가를 포함해) 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다
* 결과가 잘못되거나 오동작하는 것은 안전 실패(safety failure)라 한다
* 안전 실패는 병렬화한 파이프라인이 사용하는 mappers, filters, 또는 개발자가 제공한 다른 함수 객체가 명세대로 동작하지 않을 때 벌어질 수 있다
* Stream 명세는 이때 사용되는 함수 객체에 관한 엄중한 규약을 정의해놨다
  * 한 예로 Stream의 reduce 연산에 건네지는 accumulator(누적기)와 combiner(결합기) 함수는 반드시 결합법칙을 만족하고(associative), 간섭받지 않고(non-interfering), 상태를 갖지 않아야(stateless) 한다
  * 이런 요구사항을 지키지 못하는 상태라도 파이프라인을 순차적으로 수행한다면 올바른 결과를 얻을수도 있다
  * 하지만 병렬로 수행하면 참혹한 실패로 이어지기 십상이다
* 데이터 소스 스트림이 효율적으로 나눠지고, 병렬화하거나 빨리 끝나는 종단 연산 사용, 함수 객체들도 간섭하지 않아도 파이프라인이 수행하는 진짜 작업이 병렬화에 드는 추가 비용을 상쇄하지 못한다면 성능 향상은 미미할 수 있다
  * 스트림 안의 원소 수와 원소당 수행된느 코드 줄 수를 곱해서 실제로 성능향상이 될지 간단하게 추정 가능하다
    * 이 값이 최소 수십만은 되어야 성능 향상을 느낄 수 있다

스트림 병렬화는 오직 성능 최적화 수단이다
* 다른 최적화와 마찬가지로 변경 전후로 반드시 성능을 테스트하여 병렬화를 사용할 가치가 있는지 확인해야 한다
* 이상적으로는 운영 시스템과 흡사한 환경에서 테스트 하는 것이 좋다
* 보통은 병렬 스트림 파이프라인도 공통의 포크-조인풀에서 수행되니(같은 스레드풀 사용) 잘못된 파이프라인 하나가 시스템의 다른 부분의 성능에 악영향을 줄 수 있다는걸 생각하자
* 스트림 파이프라인을 병렬화할 일은 생각보다 적지만 조건이 잘 갖춰지면 parallel 메서드 호출 하나로 거의 프로세서 코어 수에 비례하는 성능 향상을 만끽할 수 있다


#### 계산도 올바로 수행하고 성능도 빨라질 거라는 확신 없이는 스트림 파이프라인 병렬화는 시도조차 하지 말자
#### 스트림을 잘못 병렬화하면 프로그램을 오동작하게 하거나 성능을 급격히 떨어뜨린다
#### 병렬화하는 편이 낫다고 믿더라도, 수정 후의코드가 여전히 정확한지 확인하고 운영 환경과 유사한 조건에서 수행해보며 선응지표를 유심히 관찰하라
#### 계산도 정확하고 성능도 좋아졌음이 확실해졌을 때 오직 그럴 때만 병렬화 버전 코드를 운영 코드에 반영하라