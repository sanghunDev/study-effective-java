# ITEM 23 태그 달린 클래스보다는 클래스 계층구조를 활용하라

--------------------------------------------

#### 태그 달린 클래스
* 두 가지 이상의 의미를 표현할 수 있으며 현재 표현하는 의미를 태그 값으로 알려주는 클래스
* 태그 달린 클래스의 단점
  * 열거 타입의 선언, 태그 필드, switch 문 등 쓸데없는 코드가 많다
  * 여러 구현이 한 클래스에 들어있어 가독성이 안좋다
  * 다른 의미를 위한 코드가 같이 있으니 메모리 사용량이 많다
  * 필드들을 final로 선언하려면 해당 의미에 쓰이지 않는 필드까지 생성자에서 초기화 해야한다
  * 태그 달린 클래스는 장황하며 오류가 나기 쉬우며 비효율적이다

#### 클래스 계층구조
* 타입 하나로 다양한 의미의 객체를 표현 가능한 좋은 수단이다
* 태그 달린 클래스는 클래스 계층구조를 어설프게 흉내낸 아류일 뿐이다
* 태그 달린 클래스를 클래스 계층구조로 바꾸는 법
  * 계층구조의 루트가 될 추상 클래스를 정의한다
  * 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언한다
  * 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가한다
  * 모든 하위 클래스에서 공통으로 사용하는 데이터 필드들도 전부 루트 클래스로 올린다
  * 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다
  * 각 하위 클래스에는 각자의 의미에 해당하는 데이터 필드들을 넣는다
  * 루트 클래스가 정의한 추상 메서드를 각자의 의미에 맞게 구현한다
* 클래스 계층구조는 태그 달린 클래스의 단점을 모두 없애며 간결하고 명확하다

#### 태그 달린 클래스를 써야 하는 상황은 거의 없다
#### 새로운 클래스를 만들때 태그를 사용해야 한다면 클래스 계층구조를 고려하자
#### 기존에 만든 클래스중 태그를 사용한게 있다면 클래스 계층구조로 리팩토링 해보자