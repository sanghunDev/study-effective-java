# ITEM 16 public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

--------------------------------------------
#### public 클래스

인스턴스 필드를 모아놓는 퇴보한 클래스
```` java
class Point {
  public double x;
  public double y;
}
````

이런 클래스가 public 이라면 데이터 필드에 직접 접근이 가능하여 캡슐화의 이점이 없어진다

데이터 필드에 직접 접근이 가능해지면 아래와 같은 문제가 생긴다
* api를 수정하지 않고서는 내부 표현 변경이 불가능하다
* 불변식을 보장할 수 없다
* 외부에서 필드에 접근 할 때 부수 작업을 수행할 수 없다

철저한 객체지향 프로그래머는 아래와 같이 작성한다

```` java
class Point {
  private double x;
  private double y;
  
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public double getX() { return x; }
  public double getY() { return y; }
  
  public void setX(double x) { this.x = x; }
  public void setY(double y) { this.y = y; }

}
````
* 필드를 모두 private 으로 작성
* public 접근자를 추가 
* 패키지 바깥에서 접근 할 수 있는 클래스이기 때문에 접근자를 제공 
* 클래스 내부 표현 방식을 유연하게 수정 가능

#### package-private 클래스, private 중첩 클래스
* 데이터 필드를 노출해도 문제가 없다
* 클래스가 표현하려는 추상 개념만 올바르게 표현 해주면 된다
* 클래스 선언 측면, 클라이언트 코드 측면 모두 접근자 방식보다 훨씬 깔끔하다
* 클라이언트 코드가 이 클래스 내부 표현에 묶이지만 클라이언트도 이 클래스를 포함하는 패키지 안에서만 동작하는 코드라 상관없다
  * 패키지 바깥 코드 변경 없이 데이터 표현 방식을 수정 가능
* private 중첩 클래스라면 수정 범위가 더 좁아져 이 클래스를 포함하는 외부 클래스까지로 제한된다

#### 정리
* public 클래스는 절대 가변 필드를 직접 노출해서는 안 된다
* 불변 필드라면 노출해도 덜 위험하지만 완전히 안심할 수는 없다
* package-private 클래스나 private 중첩 클래스에서는 종종 (불변이든 가변이든) 필드를 노출하는 편이 나을때도 있다