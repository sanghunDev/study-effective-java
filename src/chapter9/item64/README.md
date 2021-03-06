# ITEM 64 객체는 인터페이스를 사용해 참조하라

--------------------------------------------

#### 적합한 인터페이스만 있다면 매개변수뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언하라
* 객체의 실제 클래스를 사용해야 할 상황은 오직 생성자로 생성할 때 뿐이다

```` java
Set 인터페이스를 구현한 LinkedHashSet 변수를 선언하는 올바른 모습

//인터페이스를 타입으로 선언한 좋은 예
Set<Son> sonSet = new LinkedHashSet<>();

//클래스를 타입으로 사용한 나쁜 예
LinkedHashSet<Son> sonSet = new LinkedHashSet<>();

````
#### 인터페이스를 타입으로 사용하는 습관을 길러두면 프로그램이 훨씬 유연해질것이다
* 구현 클래스를 교체하는 상황이 발생하면 그저 새 클래스의 생성자나 다른 정적 팩터리를 호출해주기만 하면된다
```` java
첫번째 예제에서 구현 클래스를 변경해도 아무 문제없이 간단하게 변경 가능하다

Set<Son> sonSet = new HashSet<>();
````
* 단 하나 주의할 점은 원래의 클래스가 인터페이스의 일반 규약 이외의 특별한 기능을 제공하며 주변 코드가 이 기능에 기대어 동작한다면 새로운 클래스도 반드시 같은 기능을 제공해야 한다
  * ex) 첫번째 예제에서 선언의 주변 코드가 LinkedHashSet이 따르는 순서 정책을 가정하고 동작하는 상황에서 HashSet으로 바꾸면 문제가 생길 수 있다
    * HashSet은 반복자의 순회 순서를 보장하지 않는다
* 구현 타입을 바꾸려는 이유는 원래 것보다 성능이 좋거나 새로운 기능을 제공하기 때문일 것이다

#### 선언 타입과 구현 타입을 동시에 바꿀 수 있으니 변수를 구현 타입으로 선언해도 괜찮을 거라 생각할 수 있다
* 잘 못 하면 컴파일 자체가 안될 수 있다
  * ex) 클라이언트에서 기존 타입에서만 제공하는 메서드를 사용했거나 기존 타입을 사용해야 하는 다른 메서드에 그 인스턴스를 넘겼다고 가정하자
  * 그러면 새로운 코드에서는 컴파일 되지 않을것이다
  * 변수를 인터페이스 타입으로 선언하면 이런 일이 발생하지 않는다

#### 적합한 인터페이스가 없다면 당연히 클래스로 참조해야 한다
* 대표적인 예로 String과 BigInteger 같은 값 클래스가 있다
  * 값 클래스를 여러 가지로 구현될 수 있다고 생각하고 설계하는 일은 거의 없다
  * 따라서 final인 경우가 많으며 상응하는 인터페이스가 있는 경우도 거의 없다
  * 이런 값 클래스는 매개변수, 변수, 필드, 반환 타입으로 사용해도 무방하다
* 클래스 기반으로 작성된 프레임워크가 제공하는 객체
  * 이런 경우라도 특정 구현 클래스보다는 보통은 추상 클래스인 기반 클래스를 사용해 참조하는게 좋다
    * OutputStream 등 java.io 패키지의 여러 클래스가 이런 부류다
* 인터페이스에는 없는 특별한 메서드를 제공하는 클래스
  * ex) PriorityQueue 클래스는 Queue 인터페이스에 없는 comparator 메서드를 제공한다
  * 클래스 타입을 직접 사용하는 경우 이런 추가 메서드를 최소화 하고 절대 남발해서는 안된다

#### 적합한 인터페이스가 없다면 클래스의 계층구조 중 필요한 기능을 만족하는 가장 덜 구체적인 상위의 클래스를 타입으로 사용하자