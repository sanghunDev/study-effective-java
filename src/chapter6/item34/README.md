# ITEM 34 int 상수 대신 열거 타입을 사용하라

--------------------------------------------

#### 열거타입
* 일정 개수의 상수 값을 정의하고 그 외의 값은 허용하지 않는 타입

#### 정수 열거 패턴
* 자바에서 열거 타입을 지원하기 전에 사용하던 패턴으로 정수 상수를 한 묶음 선언해서 사용하는 단점이 많은 패턴이다
```` java
정수 열거 패턴

public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
````
* 정수 열거 패턴은 타입 안전을 보장 할 수 없고 표현력도 좋지 않다
  * 오렌지를 건내야 할 메서드에 사과를 보내고 동등 연산자로 비교해도 컴파일러는 아무 경고 메시지를 출력하지 않는다
  * 정수 열거 패턴을 위한 별도의 이름공간을 제공하지 않기에 사과용 상수의 이름은 모두 APPLE_로 시작하고 오렌지용 상수는 ORANGE_로 접두어를 사용하여 구분한다
* 정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다
  * 단순히 상수를 나열한 것 뿐이라서 컴파일하면 클라이언트 코드에 그 값이 그대로 새겨진다
  * 따라서 상수의 값이 변경되면 클라이언트 코드도 다시 컴파일 해야 정상 작동 하는 문제가 있다
* 정수 상수는 문자열로 출력하기가 다소 까다롭다
  * 값을 출력하거나 디버거로 보면 의미가 아닌 단순히 숫자로 보여 큰 도움이 되지 않는다
* 같은 정수 열거 그룹에 속한 모든 상수를 한 바퀴 순회하는 방법도 마땅히 없으며 상수의 개수를 파악 할 수도 없다

#### 문자열 열거 패턴
* 정수 대신 문자열 상수를 사용하는 변형 패턴인 이 변형은 더 안좋다
  * 상수의 의미를 출력할 수 있다는 부분은 좋지만 경험이 부족한 개발자가 문자열 상수의 이름 대신 문자열 값을 그대로 하드코딩할 가능성이 높다
  * 하드코딩한 문자열에 오타가 있어도 컴파일러에서 확인 할 수 없어 런타임 버그가 발생할 확률이 높다
  * 문자열 비교에 따른 성능 저하도 추가적으로 따라온다

#### 열거타입
* 자바에서는 위와 같은 단점들을 보완하고 여러가지 장점이 있는 열거 타입을 제공하게 되었다
```` java
가장 단순한 형태의 열거타입

public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
````
* 그냥 보기엔 C, C++, C# 같은 다른 언어의 열거 타입과 비슷하다
* 하지만 형태만 비슷할뿐 자바의 열거 타입은 다른 언어틔 열거 타입보다 훨씬 강력하다
  * 자바의 열거 타입은 완전한 형태의 클래스이다(다른 언어는 단순한 정숫값)
* 자바 열거 타입을 뒷바침하는 아이디어는 단순하다
* 열거 타입 자체는 클래스이며 상수 하나당 자신의 인스턴스르 하나씩 만들어 public static final 필드로 공개한다
* 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final 이다
  * 따라서 클라이언트가 인스턴스를 직접 생성하거나 확장 할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재한다
  * 즉 열거 타입은 인스턴스 통제 된다
  * 싱글턴은 원소가 하나뿐인 열거 타입이라 할 수 있으며 열거타입은 싱글턴을 일반화한 형태라고 볼 수 있다
* 열거 타입은 컴파일 안정성을 제공한다
  * 위 예제에서 Apple 열거 타입을 매개변수로 받는 메서드를 선언하여 건네받는 참조는 null이 아니라면 Apple의 세 가지 값 중 하나인게 보장된다
  * 다른 타입의 값을 넘기려고 한다면 컴파일 오류가 날 것이다
    * 타입이 다른 열거 타입 변수에 할당하려 하거나 다른 열거 타입의 값끼리 == 연산자로 비교하려는 모습이기 때문
* 열거 타입에는 각자의 이름 공간이 있어서 이름이 같은 상수도 평화롭게 공존한다
  * 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일 하지 않아도 된다
  * 공개되는것이 오로지 필드의 이름 뿐이라서 정수 열거 패턴과 다르게 상수의 값이 클라이언트 코드에 고정되는 것이 아니다
* 열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다
* 열거 타입에는 임의의 메서드나 필드를 추가 할 수도 있으며 임의의 인터페이스를 구현하게 할 수도 있다
  * Object 메서드, Comparable, Serializable을 구현했으며 직렬화 행태도 어느정도는 변형해도 동작하도록 해놨다

#### 메서드나 필드를 추가한 열거타입
* 열거 타입에 메서드나 필드를 추가 해야하는 시점은 언제일까?
  * 각 상수와 연관된 데이터를 해당 상수 자체에 내재시키고 싶은 경우
  * 열거 타입은 실제로는 클래스이기 때문에 고차원의 추상 개념을 완벽히 표현할 수 있다
* 열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다

```` java
태양계의 여덟 행성을 열거 타입으로 나타냈다

각 행성에는 질량과 반지름이 있으며 이 두 속성을 이용하여 표면 중력을 계산 가능하다

어떤 객체의 질량이 주어지면 그 객체가 행성 표면에 있을 때의 무게도 계산할 수 있다

열거 타입 상수 오른쪽 괄호 안 숫자는 생성자에 넘겨지는 매개변수로 이 예제에서는 행성의 질량과 반지름을 뜻한다

public enum Planet {
//오른쪽 괄호 안 숫자는 생성자에 넘겨지는 매개변수 (질량, 반지름)
  MERCURY (3.302e+23, 2.439e6),
  VENUS   (4.869e+24, 6.052e6),
  EARTH   (5.975e+24, 6.378e6),
  MARS    (6.419e+23, 3.393e6),
  JUPITER (1.899e+27, 7.149e7),
  SATURN  (5.685e+26, 6.027e7),
  UNANUS  (8.683e+25, 2.556e7),
  NEPTUNE (1.024e+26, 2.477e7),
  
  private final double mass;          //질량(단위 : 킬로그램)
  private final double radius;        //반지름(단위 : 미터)
  private final double sufaceGravity; //표면중력(단위 : m / s^2)
  
  //중력상수(단위 m^3 / kg s^2)
  private static final double G = 6.67300E-11;
  
  // 생성자
  Planet(double mass, double radius) {
    this.mass = mass;
    this.radius = radius;
    sufaceGravity = G * mass / (radius * radius);
  }
  
  public double mass()              { return mass; }
  public double radius()            { return radius; }
  public double sufaceGravity()     { return sufaceGravity; }
  
  public double sufaceWeight(double mass) {
    return mass * sufaceGravity;  // F = ma
  }
}
````
* 열거 타입은 근본적으로 불변이라 모든 필도는 final 이어야 한다
* 필드를 public 으로 선언해도 되지만 private 으로 두고 public 접근자 메서드를 두는게 낫다
* Planet의 생성자에서 표면중력을 계산한 이유는 단순히 최적화를 위해서이다
* Planet 열거 타입은 단순하지만 놀랍도록 강력하다
```` java
어떤 객체의 지구에서의 무게를 입력 받아 여덟 행성에서의 무게를 출력하는 코드

public class WeightTable {
  public static void main(String[] args) {
    double earthWeight = Double.parseDouble(args[0]);
    double mass = earthWeight / Planet.EARTH.surfaceGravity();
    for (Planet p : Planet.values())
      System.out.printf("%s에서의 무게는 %f이다.%n",
                          p, p.surfaceWeight(mass));
  }
}
````
* 열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values를 제공한다
  * 값들은 선언된 순서대로 저장된다
  * 각 열거 타입 값의 toString 메서드는 상수 이름을 문자열로 반환하므로 printf와 println으로 출력하기에 좋다
  * 기본 toString 의 형식이 마음에 들지 않으면 당연히 재정의도 가능하다
* 열거 타입에서 상수를 하나 제거 하더라도 해당 상수를 참조하고 있지 않은 클라이언트는 아무런 영향이 없다
  * 해당 상수를 참조하고 있다면 재컴파일시 컴파일 오류가 적절한 발생할것이고 재컴파일을 하지 않는다면 런타임시 적절한 오류가 발생 할 것이다
  * 정수형 열거 패턴에서는 이런 부분을 기대 할 수 없다
* 열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 private나 package-private 메서드로 구현한다
  * 일반 클래스와 마찬가지로 해당 기능을 클라이언트에 노출할 필요가 없다면 private 아니라면 package-private를 사용하자
* 많이 사용되는 열거 타입은 톱레벨 클래스로 만들자
  * 특정 톱레벨 클래스에서만 사용된다면 해당 클래스의 멤버 클래스로 만든다

#### 상수마다 동작이 달라져야 하는 경우
```` java
값에 따라 분기하는 열거타입

public eum Operation {
  PLUS, MINUS, TIMES, DIVIDE;
  
  // 상수가 뜻하는 연산을 수행
  public double apply(double x, double y) {
    switch(this) {
      case PULS:    return x + y;
      case MINUS:    return x - y;
      case TIMES:    return x * y;
      case DIVIDE:    return x / y;
    }
    throw new AssertionError("알 수 없는 연산 : " + this);
  }
}
````
* 위와 같이 작성해도 상수마다 다른 동작을 수행하기는 하지만 코드가 예쁘지는 않다
* 마지막의 throw 는 실제로 도달하지 않지만 기술적으로는 도달할 수 있기 때문에 생략하면 컴파일도 안된다
* 새로운 상수를 추가하면 case문도 추가해야 하며 깜빡 한다면 컴파일은 되지만 런타임시 throw가 실행되어 오류가 출력 될 것이다
  * 유지보수가 쉽지 않고 깨지기 쉬운 코드이다
* 열거 타입은 이러한 단점을 보완하여 상수별로 다르게 동작하는 코드를 구현하는 더 나은 수단을 제공한다
  * 상수별 메서드 구현 이라는 열거 타입에 추상 메서드를 선언하고 각 상수별 클래스 몸체, 즉 각 상수에서 자신에 맞게 재정의하는 방법

```` java
상수별 메서드 구현

public enum Operation {
  PLUS    {public double apply(double x, double y){return x + y;}},
  MINUS   {public double apply(double x, double y){return x - y;}},
  TIMES   {public double apply(double x, double y){return x * y;}},
  DIVIDE  {public double apply(double x, double y){return x / y;}},
  
  public abstract double apply(double x, double y);
}
````
* apply라는 추상 메서드를 선언하여 상수별 구현을 작성하였다
* apply 메서드가 상수 선언 바로 옆에 있으니 새로운 상수룰 추가하는 경우 apply도 재정의 해야 한다는 것을 명확하게 알 수 있다
* 또한 apply가 추상 메서드이기 때문에 재정의 하지 않으면 컴파일 오류가 발생된다
* 상수별 메서드 구현을 상수별 데이터와 결합도 가능하다

```` java
상수별 클래스 몸체와 데이터를 사용한 열거타입

Operation 의 toString을 재정의하여 해당 연산을 뜻하는 기호를 반환하도록 하는 예제

public enum Operation {
  PLUS("+") {
    public double apply(double x, double y) { return x + y; }
  },
  MINUS("-") {
    public double apply(double x, double y) { return x - y; }
  },
  TIMES("*") {
    public double apply(double x, double y) { return x * y; }
  },
  DIVIDE("/") {
    public double apply(double x, double y) { return x / y; }
  },
  
  private final String symbol;
  
  Operation(String symbol) { this.symbol = symbol; }
  
  //toString을 재정의하여 계산식 출력을 편하게 확인 가능하다
  @Override public String toString() { return symbol; }
  
  public abstract double apply(double x, double y);
}

public static void main(String[] args) {
  double x = Double.parseDouble(args[0]);
  double y = Double.parseDouble(args[1]);
  
  for (Operation op : Operation.values())
    System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x,y));
}
````
* 열거 타입에는 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String) 메서드가 자동으로 생성된다
* 열거 타입의 toString 메서드를 재정의하려면 toString이 반환되는 문자열을 해당 열거 타입 상수로 변환해주는 fromString 메서드도 함께 제공되는걸 고려해보자

```` java
모든 열거 타입에서 사용 가능한 fromString 메서드(타입 이름을 적절히 바꿔야 하고 모든 상수의 문자열 표현이 고유해야 한다)

private static final Map<String, Operation> stringToEnum = 
          Stream.of(values()).collect(
            toMap(Object::toString, e -> e)
            );

//지정한 문자열에 해당하는 Operation을 존재한다면 반환한다
public static Optional<Operation> fromString(String symbol) {
  return Optional.ofNullable(stringToEnum.get(symbol));
}
````
* Operation 상수가 stringToEnum 맵에 추가되는 시점은 열거 타입 상수 생성 후 정적 필드가 초기화 될 때다
* 위 예제는 values 메서드가 반환하는 배열 대신 stream을 사용했다
  * 자바 8 이전에는 빈 해시맵을 만들어 values가 반환한 배열을 순회하며 문자열, 열거 타입 상수 쌍을 맵에 추가 했을 것이다
  * 열거 타입 상수는 생성자에서 자신의 인스턴스를 맵에 추가 할 수 없다
    * 위와 같이 작성하면 컴파일 오류가 나며 만약 이게 허용 되었다면 런타임에 NullPointerException이 발생 했을 것
    * 열거 타입의 정적 필드 중 열거 타입의 생성자에서 접근할 수 있는것은 상수 변수 뿐
    * 열거 타입 생성자가 실행되는 시점에는 정적 필드들이 아직 초기화되기 전이라 자기 자신을 추가하지 못하게 하는 제약이 필요하다
    * 이 제약의 특수한 예로는 열거 타입 생성자에서 같은 열거 타입 생성자에서 같은 열거타입의 다른 상수에도 접근 할 수 없다
* fromString이 Optional<Operation>을 반환하는 점도 주의하자
  * 주어진 문자열이 가리키는 연산이 존재하지 않을 수 있음을 클라이언트에게 알리고 대처하도록 한것이다
* 상수별 메서드 구현에는 열거타입 상수끼리 코드 공유하기 어렵다는 단점이 있다

```` java
급여 명세서에 쓸 요일을 표현하는 열거 타입

값에 따라 분기하여 코드를 공유하는 열거 타입 - 간결한 코드지만 관리 관점에서는 위험한 코드

enum PayrollDay {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
  
  private static final int MINS_PER_SHIFT = 8 * 60;
  
  int pay(int mintesWorked, int payRate) {
    int basePay = minutesWorked * payRate;
    
    int overtimePay;
    switch(this) {
      case SATURDAY: case SUNDAY: // 주말
        overtimePay = basePay / 2;
        break;
      default:  // 주중
        overtimePay = minutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2; 
    }
    
    return basePay + overtimePay;
  }

}

휴가와 같은 새로운 값을 열거 타입에 추가하려면 그 값을 처리하는 case문을 쌍으로 넣어줘야한다

상수별 메서드 구현으로 급여를 정확히 계산하는 방법은 두가지다

1. 잔업수당을 계산하는 코드를 모든 상수에 중복해서 넣는 방법

2. 계산코드를 평일용과 주말용으로 나눠 각각 도우미 메서드로 작성하고 각 상수가 자신에게 필요한 메서드를 호출하면 된다

두 방식 모두 코드가 장황해져 가독성이 떨어지며 오류 발생 확률도 높다

가장 깔끔한 방법은 새로운 상수를 추가할 경우 잔업수당 전략을 선택하도록 하는 것이다

잔업수당 계산을 private 중첩 열거 타입(다음 코드의 PayType)으로 옮기고 PayrollDay 열거 타입의 생성자에서 이 중 적당한 것을 선택한다
그러면 PayrollDay 열거 타입은 잔업수당 계산을 그 전략 열거 타입에 위임하여 switch 문이나 상수별 메서드 구현이 필요 없게 된다
이 패턴은 switch 문보다 복잡하지만 더 안전하고 유연하다

enum PayrollDay {
  MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY), THURSDAY(WEEKDAY), FRIDAY(WEEKDAY), SATURDAY(WEEKEND), SUNDAY(WEEKEND);
  
  private final PayType payType;
  
  PayrollDay(PayType payType) { this.payType = payType; }
  
  int pay(int minutesWorked, int payRate) {
    return payType.pay(minutesWorked, payRate);
  }
  
  //전략 열거 타입
  enum PayType {
    WEEKDAY {
      int overtimePay(int minsWorked, int payRate) {
        return minsWorked <= MINS_PER_SHIFT ? 0 : (minsworked - MINS_PER_SHIFT) * payRate / 2; 
      }
    },
    WEEKEND {
      int overtimePay(int minsWorked, int payRate) {
        return minsWorked * payRate / 2; 
      }
    };
    
    abstract int overtimePay(int mins, int payRate);
    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minsWorked, int payRate) {
      int basePay = minsWorked * payRate;
      return basePay + overtime(minsWorked, payRate);
    }
  }
}

위 두가지 예제를 보면 switch 문이 열거 타입의 상수별 동작을 구현하는데 적합하지 않다는 것을 알 수 있다

하지만 예외적으로 기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 switch문이 좋은 선택이 될 수 있다

아래 예제는 switch 문을 이요해 원래 열거 타입에 없는 기능을 수행하였다
public static Operation inverse(Opertaion op) {
  switch(op) {
    case PLUS : return Opertaion.MINUS;
    case MINUS : return Opertaion.PLUS;
    case TIMES : return Opertaion.DIVIDE;
    case DIVIDE : return Opertaion.TIMES;
    
    default: throw new AssertionError("알 수 없는 연산 : " + op);
  }
}

추가 하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입이라도 이 방식을 적용하는게 좋다

대부분의 열거 타입은 성능상 정수 상수와 큰 차이가 없으며 열거 타입을 메모리에 올리는 공간과 초기화 하는 시간이 들지만 체감될 정도는 아니다

필요한 원소를 컴파일 타임에 다 알수 있는 상수 집합이라면 항상 열거 타입을 사용하자

또한 열거 타입에 정의된 상수의 개수가 영원히 고정 불변일 필요는 없다

열거 타입은 나중에 상수가 추가 되더라도 바이너리 수준에서 호환 되도록 설계 되었다

````