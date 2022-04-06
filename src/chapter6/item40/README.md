# ITEM 40 @Override 애너테이션을 일관되게 사용하라

--------------------------------------------

자바가 기본으로 제공하는 애너테이션 중 보통 개발자에게 가장 중요한 것은 @Override일 것이다

@Override는 메서드 선언부에만 달 수 있고 이 애노테이션이 달려있다는 것은 상위 타입의 메서드를 재정의 했다는 것이다

이 애너테이션을 일관되게 사용한다면 여러 가지 악명 높은 버그를 예방해준다

```` java
영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
- 소문자 2개로 구성된 바이그램 26개를 10번 반복해 집합에 넣고 크기를 출력한다
- Set은 중복을 허용하지 않으니 26을 기대하지만 260이 출력된다

 public class Bigram {
  private final char first;
  private final char second;
  
  public Bigram(char first, char second) {
    this.first = first;
    this.second = second;
  }
  
  public boolean equals(Bigram b) {
    return b.first == first && b.second == second;
  }
  
  public int hashCode() {
    return 31 * first + second;
  }
  
  public static void main(String[] args) {
    Set<Bigram> s = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      for (char ch = 'a'; ch <= 'z'; ch++) {
        s.add(new Bigram(ch,ch));
      }
    }
    
    System.out.println(s.size());
  }
  
 }
 
 위 코드에서는 equals 메서드를 재정의 하려고 하였으나 재정의가 아닌 다중 정의가 되어버렸다(매개변수 타입을 Object로 사용하지 않음)
 
 Object의 equals는 == 연산자와 똑같이 객체 식별성만을 확인하여 같은 소문자를 소유한 바이그램 10개 각각이 서로 다른 객체로 인식된것
 
 위와 같은 오류는 컴파일러가 확인이 가능한데 컴파일러가 확인하려면 Object.equals를 재정의 한다고 알려줘야 한다
 
 @Override
 public boolean equals(Bigram b) {
  return b.first == first && b.second == second;
 }
 
 위 처럼 작성한다면 컴파일 오류로 인해 문제점을 확인하여 아래처럼 바꿀 수 있다
 
 @Override
 public boolean equals(Object o) {
  if (!(o instanceof Bigram)) {
    return false;
  }
  
  Bigram b = (Bigram)o;
  return b.first == first && b.second == second;
 }
 
````

위와 같은 오류를 만나지 않으려면 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자
* 하나의 예외로 구체 클래스에서 상위 클래스의 추상 메서드를 재정의하는 경우는 안달아도 된다
* 구체 클래스인데 구현하지 않은 추상 메서드가 있다면 컴파일러가 바로 알려준다

@Override는 클래스뿐 아니라 인터페이스의 메서드를 재정의하는 경우에도 사용 가능하다

디폴트 메서드를 지원하면서 인터페이스 메서드를 구현한 메서드에도 @Override를 다는 습관을 들이면 시그니처가 올바른지 확신이 든다

구현하려는 인터페이스에 디폴트 메서드가 없다는걸 안다면 이를 구현한 메서드에서는 @Override를 생략해 코드를 깔끔하게 유지하는것도 좋다

하지만 추상 클래스나 인터페이스에서는 상위 클래스나 상위 인터페이스의 메서드를 재정의하는 모든 메서드에 @Override를 다는게 좋다

상위 클래스가 구체 클래스든 추상 클래스든 마찬가지다

