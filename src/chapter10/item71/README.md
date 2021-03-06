# ITEM 71 필요 없는 검사 예외 사용은 피하라

--------------------------------------------

검사 예외를 싫어하는 개발자가 많지만 제대로 활용하면 API와 프로그램의 질을 높일 수 있다
* 결과를 코드로 반환하거나 비검사 예외를 던지는 것과 다르게 검사 예외는 발생한 문제를 개발자가 처리해 안정성을 높여준다
* 하지만 검사 예외를 과하게 쓰면 오히려 쓰기 불편한 API가 된다
* 어떤 메서드가 검사 예외를 던질 수 있다면 이 메서드를 호출하는 코드에서는 catch 블록을 이용해 그 예외를 붙잡아 처리하거나 더 바깥으로 던져 문제를 전파해야된다
  * 어느쪽이든 API 사용자에게 부담을 주며 검사 예외를 던지는 메서드는 스트림 안에서 직접 사용할 수 없기 때문에 자바8부터는 더욱 부담이 커졌다
* API를 제대로 사용해도 발생할 수 있는 예외거나 개발자가 의미있는 조치를 취할 수 있다면 이 정도는 부담 없을 것이다
  * 하지만 둘 중 어디에도 해당이 안되면 비검사 예외를 사용하자

검사 예외와 비검사 예외 중 어느 것을 선택해야 할지는 개발자가 그 예외를 어떻게 다룰지 생각해보면 된다
```` java
이게 최선인가??

}catch (TheCheckedException e) {
  throw new AssertionError(); //일어날 수 없다
}

이 방식은 어떤가??

}catch (TheCheckedException e) {
  e.printStackTrace(); //우리가 졌다
  System.exit(1);
}

더 나은 방법이 없으면 비검사 예외를 선택해야 한다

````

검사 예외가 개발자에게 부담이 되는 경우는 단 하나의 검사 예외만 던질 때가 특히 크다
* 이미 다른 검사 예외도 던지는 상황에서 또 다른 검사 예외를 추가하는 경우라면 catch문 하나 추가하면 끝이다
* 검사 예외가 단 하나 뿐이면 그 예외 때문에 API 사용자는 try 블록을 추가하고 스트림에서 직접 사용하지 못하게 된다
* 따라서 이런 경우에는 검사 예외를 안 던지고 해결이 가능한지 생각 해보자

검사 예외를 회피하는 가장 쉬운 방법은 적절한 결과 타입을 담은 옵셔널을 반환하는 것이다
* 검사 예외를 던지는 대신 단순히 빈 옵셔널을 반환하면 된다
* 이 경우 문제점은 예외가 발생한 이유를 알려주는 부가 정보를 담을 수 없다
  * 예외를 쓰면 예외 타입등이 제공하는 메서드를 통해 부가 정보를 제공할 수 있다
* 또 다른 방법은 검사 예외를 던지는 메서드를 2개로 쪼개 비검사 예외로 바꿀수 있다

#### 꼭 필요한 곳에만 사용한다면 검사 예외는 프로그램의 안정성을 높여주지만 남용하면 쓰기 고통스러운 API를 낳는다
#### API 호출자가 예외 상황에서 복구할 방법이 없다면 비검사 예외를 던지자
#### 복구가 가능하고 호출자가 그 처리를 해주길 바란다면 우선 옵셔널을 반환해도 될지 고민하자
#### 옵셔널만으로 상황을 처리하기에 충분한 정보를 제공할 수 없을 때 검사 예외를 던지자