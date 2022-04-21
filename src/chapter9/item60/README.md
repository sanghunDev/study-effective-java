# ITEM 60 정확한 답이 필요하다면 float와 double은 피하라

--------------------------------------------

float와 double 타입은 과학과 공학 계산용으로 설계되었다
* 이진 부동소수점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 근사치로 계산하도록 세심하게 설계 되었다
* 정확한 결과가 필요한 경우에는 사용하면 안되며 float와 double 타입은 특히 금융 관련 계산과는 맞지 않는다
  * 0.1 혹은 10의 음의 거듭 제곱수를 표현할 수 없기 때문이다

```` java
주머니에 1.03달러가 있었는데 그중 42센트를 쓰면 남은 돈은 얼마인가?

어설프게 작성한 코드
System.out.println(1.03 - 0.42);
안타깝게도 결과는 0.60......1

주머니에 1달러가 있었는데 10센트짜리 사탕 9개를 샀으면 얼마가 남았을까
System.out.println(1.00 - 9 * 0.10);
이 코드는 0.099.....8 을 출력한다

결과값을 출력하기 전에 반올림을 하면 해결 될거라 생각되지만 반올림을 해도 틀린 답이 나올 수 있다
````

```` java
금융계산에 부동소수 타입을 사용하여 오류가 나는 코드

public static void main(String[] args) {
  double funds = 1.00;
  int itemsBought = 0;
  for (double price = 0.10; funds >= price; price += 0.10) {
    funds -= price;
    itemBought++;
  }
  
  System.out.println(itemsBought + "개 구입");
  System.out.println("잔돈(달러):" + funds);
}

결과값이 잘못되어 사탕 3개를 구입한 후 잔돈은 0.399999...9달러가 남았다고 나온다

이 문제를 제대로 해결하려면 금융계산에는 BigDecimal, int 혹은 long을 사용해야 한다

아래 코드는 위 코드에서 double 타입을 BigDecimal로 교체만 했다
BigDecimal의 생성자 중 문자열을 받는 생성자를 사용해서 계산시 부정확한 값이 사용되는걸 막았다

public static void main(String[] args) {
  final BigDecimal TEN_CENTS = new BigDecimal(".10");
  
  int itemsBought = 0;
  BigDecimal funds = new BigDecimal("1.00");
  for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0; price = price.add(TEN_CENTS)) {
    funds = funds.subtract(price);
    itemBought++;
  }
  
  System.out.println(itemsBought + "개 구입");
  System.out.println("잔돈(달러):" + funds);
}

위 코드를 실행하면 사탕 4개를 구입한 후 잔돈은 0달러가 남는다
제대로된 결과지만 BigDecimal에는 두가지 단점이 있다
- 기본 타입보다 쓰기가 훨씬 불편하며 느려서 단발성 계산이라면 느리다는 문제는 무시할 수 있지만 쓰기 불편하다는 점이 아쉽다

BigDecimal의 대한으로 int 혹은 long 타입을 쓸 수도 있다
- 하지만 그런 경우 다룰 수 있는 값의 크기가 제한되며 소수점을 직접 관리해야 한다

````

#### 정확한 답이 필요한 계산에는 float나 double을 피하라
#### 소수점 추적은 시스템에 맡기고 코딩 시의 불편함이나 성능 저하를 신경 쓰지 않겠다면 BigDecimal을 사용하라
#### BigDecimal이 제공하는 여덟 가지 반올림 모드를 이용하여 반올림을 완벽히 제어 할 수 있다
#### 법으로 정해진 반올림을 수행해야 하는 비지니스 계산에서 아주 편리한 기능이다
#### 반면 성능이 중요하고 소수점을 직접 추적할 수 있고 숫자가 너무 크지 않다면 int나 long을 사용하라
#### 숫자를 아홉 자리 십진수로 표현할 수 있다면 int를 사용하고, 열여덟 자리 십진수로 표현할 수 있다면 long을 사용하라
#### 열여덟 자리를 넘어가면 BigDecimal을 사용해야 한다