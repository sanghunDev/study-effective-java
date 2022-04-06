# ITEM 38 확장 할 수 있는 열거타입이 있으면 인터페이스를 사용하라

--------------------------------------------

열거 타입은 거의 모든 상황에서 타입 안전 열거 패턴(typesafe enum pattern)보다 우수하다
* 한가지 예외적인 경우는 타입 안전 열거 패턴은 확장할 수 있지만 열거 타입은 그럴 수 없다
  * 타입 안전 열거 패턴은 열거한 값들을 그대로 가져온 다음 값을 더 추가하여 다른 목적으로 쓸 수 있지만 열거 타입은 그렇게 할 수 없다
    * 실수로 이렇게 설계한 것이 아니며 대부분의 상황에서 열거 타입을 확장하는건 좋지 않다
    * 확장한 타입의 원소는 기반 타입의 원소로 취급하지만 그 반대는 성립하지 않는다면 이상하다
    * 또한 기반 타입과 확장된 타입들의 원소 모두를 순회하는 방법도 마땅하지 않다
    * 확장성을 높이려면 고려 할 요소가 늘어나 설계와 구현이 더 복잡해진다
* 확장할 수 있는 열거 타입이 어울리는 쓰임새는 연산코드이다(operation code , opcode)
  * 연산코드의 각 원소는 특정 기계가 수행하는 연산을 뜻한다
  * 가끔 api가 제공하는 기본 연산외에 사용자가 확장 연산을 추가할 수 있도록 열어줘야 하는 경우도 있는데 이런경우 열거 타입으로 이 효과를 낼 수 있다 
  * 기본 아이디어는 열거 타입이 임의의 인터페이스를 구현할 수 있다는 사실을 이용하는것
    * 연산코드용 인터페이스를 정의하고 열거 타입이 이 인터페이스를 구현하게 하면 된다
    * 열거 타입이 이 인터페이스의 표준 구현체 역할을 하게된다

```` java
Operation 타입을 확장 할 수 있게 만든 코드

인터페이스를 이용해 확장 가능 열거타입을 흉내냄

public interface Operation {
    double apply(double x, double y);
}

public enum BasicOperation implements Operation {
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
    };
    
    private final String symbol;
    
    BasicOperation(String symbol) {
      this.symbol = symbol;
    }
    
    @Override public String toString() {
      return symbol;
    }
}
````
열거 타입인 BasicOperation은 확장할 수 없지만 인터페이스인 Operation은 확장할 수 있으며 이 인터페이스를 연산의 타입으로 사용하면 된다
* 이러면 Operation을 구현한 또 다른 열거 타입을 정의해 기본 타입인 BasicOperation을 대체 가능

여기에 연산 타입을 확장해서 지수 연산(EXP)와 나머지 연산(REMAINDER)을 추가해보자
* Operation 인터페이스를 구현한 열거 타입을 작성하기만 하면 된다
```` java
확장 가능 열거 타입

public enum ExtendedOperation implements Operation {
  EXP("^") {
    public double apply(double x, double y) {
      return Math.pow(x,y);
    }
  },
  REMAINDER("%") {
    public double apply(double x, double y) {
      return x % y;
    }
  };
  
  private final String symbol;
  
  ExtendedOperation(String symbol) {
    this.symbol = symbol;
  }
  
  @Override public String toString() {
    return symbol;
  }
}
````

새로 작성한 연산은 기존 연산을 쓰던 곳이면 어디든 쓸 수 있다
* BasicOperation 이 아닌 Operation 인터페이스를 사용하도록 작성되어 있으면 된다
* apply가 Operation 인터페이스에 선언되어 있으니 열거 타입에 따로 추상 메서드로 선언하지 않아도 된다
  * 이게 상수별 메서드 구현과 다른 점이다

개별 인스턴스 수준에서뿐 아니라 타입 수준에서도, 기본 열거 타입 대신 확장된 열거 타입을 넘겨 확장된 열거 타입의 원소 모두를 사용하게 할 수도 있다

```` java
item34의 테스트 프로그램을 가져와서 ExtendedOperation의 모든 원소를 테스트 하도록 수정한 모습

public static vodi main(String[] args) {
  double x = Double.parseDouble(args[0]);
  double y = Double.parseDouble(args[1]);
  test(ExtendedOperation.class, x, y);
}

private static <T extends Enum<T> & Operation> void test(Class<T> opEnumType, double x, double y) {
  for (Operation op : opEnumType.getEnumConstants()){
    System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x,y));
  }
}

main 메서드는 test 메서드에 ExtendedOperation의 class 리터럴을 넘겨 확장된 연산들이 무엇인지 알려준다

여기서 class 리터럴은 한정적 타입 토큰 역할을 한다

opEnumType 매개변수의 선언(<T extends Enum<T> & Operation>)은 복잡한데, Class 객체가 열거 타입인 동시에 Operation의 하위 타입이어야 한다는 뜻이다

열거 타입이어야 원소를 순회할 수 있고 Operation이어야 원소가 뜻하는 연산을 수행할 수 있기 때문이다


두번째 대안은 Class 객체 대신 한정적 와일드카드 타입인 Collection<? extends Operation>을 넘기는 방법이다

public static vodi main(String[] args) {
  double x = Double.parseDouble(args[0]);
  double y = Double.parseDouble(args[1]);
  test(Arrays.asList(ExtendedOperation.values()), x, y);
}

private static void test(Collection<? extends Operation> opSet, double x, double y) {
  for (Operation op : opSet){
    System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x,y));
  }
}

위 코드는 첫번째 코드보다 덜 복잡하며 test 메서드가 조금 더 유연하다(여러 구현 타입의 연산을 조합해 호출 할 수 있다)

하지만 특정 연산에서는 EnumSet과 EnumMap을 사용하지 못한다

````
인터페이스를 이용해 확장 가능한 열거 타입을 흉내 내는 방식에도 한가지 사소한 문제가 있다
* 열거 타입끼리 구현을 상속할 수 없다
  * 아무 상태에도 의존하지 않는 경우에는 디폴트 구현을 이용해 인터페이스에 추가하는 방법이 있다
  * 반면 Operation 예는 연산 기호를 저장하고 찾는 로직이 BasicOperation과 ExtendedOperation 모두에 들어가야한다
  * 위의 경우는 중복량이 적으니 문제가 되진 않지만 공유하는 기능이 많다면 그 부분을 별도의 도우미 클래스나 정적 도우미 메서드로 분리하는 방식으로 코드 중복을 없앨 수 있다
* 자바 라이브러리에서도 이번 아이템에서 소개한 패턴을 사용한다 (java.nio.file.LinkOption 열거 타입은 CopyOption과 OpenOption 인터페이스를 구현했다)

#### 열거 타입 자체는 확장 할 수 없지만 인터페이스와 그 인터페이스를 구현하는 기본 열거 타입을 함께 사용해 같은 효과를 낼 수 있다
#### 이러면 클라이언트는 이 인터페이스를 구현해 자신만의 열거 타입을 만들 수 있다(다른타입도 가능)
#### 기본 API가(기본 열거 타입을 직접 명시하지 않고) 인터페이스 기반으로 작성되었다면 기본 열거 타입의 인스턴스가 쓰이는 모든 곳을 새로 확장한 열거 타입의 인스턴스로 대체해 사용 가능하다

