# ITEM 5 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

--------------------------------------------
## 자원을 코드에 직접 명시하지 말자
* java로 개발을 하다 보면 많은 클래스가 여러개의 자원에 의존하게 된다
* 이런 경우 의존성을 코드에 명시하게 되면 클라이언트와 의존성이 강하게 결합된다
  * 유연하지 않은 코드가 되며 테스트도 어려워진다
  

#### 정적 유틸리티를 잘못 사용한 예제이다
```` java
public class SpellChecker {
  private static final Lexicon dictionary = ...;
  
  private SpellChecker() {} 
  
  public static boolean isValid(String word) { ... }
  public static List<String> suggestions(String typo) { ... }
}

````

#### 싱글턴을 잘못 사용한 클래스이다
```` java
public class SpellChecker {
  private final Lexicon dictionary = ...;
  
  private SpellChecker(...){ }
  public static SpellChecker INSTANCE = new SpellChecker(...);
  
  public boolean isValid(String word) { ... }
  public List<String> suggestions(String typo) { ... }
}
````
#### 두가지 방식 모두 dictionary를 하나만 사용한다고 가정한다면 훌륭하지 않다
#### 실제 업무에서는 다양한 종류에 사전이 존재 할 것이며 그에 따라 유연하게 변경 되어야 한다
#### 이처럼 사용하는 자원에 따라 동작이 달라져야 한다면 위의 두 방식은 적합하지 않다
* 위의 방식에서 dictionary 필드의 final 키워드를 제외하고 다른 사전으로 교체하는 메서드를 추가할수도 있다
  * 보기에 이상하고 오류를 내기가 쉬우며 멀티 쓰레드 환경에서는 사용이 불가능하다

## 어떻게 해야하나?
#### 사용하는 자원에 따라 동적이 달라져야 하는 클래스는 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식을 사용해야한다 
* 의존성 주입의 한 형태이다

```` java
public class SpellChecker {
  private final Lexicon dictionary;
  
  public SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }
  
  public boolean isValid(String word) { ... }
  public List<String> suggestions (String typo) { ... }
}
````
* 위와 같은 의존객체 주입 방식은 아주 단순하다
* 이러한 방식은 자원이 몇개든 의존관계가 어떤식으로 변하든 상관없이 동작한다
* 불변을 보장하며 여러 클라이언트가 의존 객체들을 안심하고 공유가 가능하다
* 의존객체 주입은 생성자, 정적팩터리, 빌더 모두 응용 가능하다

## 쓸만한 변형
#### 위와 같은 패턴의 쓸만한 변형으로는 팩터리 메서드 패턴이 있다
#### 팩터리 : 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체
#### java8에서 소개한 Supplier<T> 인터페이스가 팩터리를 표현한 완벽한 예제다

## 마무리
* 의존객체 주입은 유연성과 테스트를 용이하게 해준다
* 하지만 의존성이 많은 코드에선 오히려 복잡도를 증가시킬 확률도 있다
* 이러한 복잡도를 개선 시키려면 프레임워크를 사용해라(우리가 주력으로 쓰는 Spring 같은거)

#### 클래스가 하나 이상의 자원에 의존하고 그 자원이 클래스의 동작에 영향을 준다면 싱글턴과 정적 유틸리는 사용하지 말자
#### 위와 같이 의존 객체 주입 기법을 사용하여 클래스를 유연하고 재사용성이 높게 만들어 보자