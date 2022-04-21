# ITEM 63 문자열 연결은 느리니 주의하라

--------------------------------------------

문자열 연결 연산자(+)는 여러 문자열을 하나로 합칠때 많이 사용한다
* 한 줄 짜리 출력값, 작고 크기가 고정된 객체의 문자열 표현을 만드는 경우는 괜찮다
* 하지만 본격적으로 사용하면 성능 저하를 감내하기가 어렵다
* 문자열 연결 연산자로 문자열 n개를 잇는 시간은 n²에 비례한다
* 문자열은 불변이라 두 문자열을 연결하면 양쪽의 내용을 모두 복사하니 성능 저하는 필연적이다

```` java
청구서의 품목(item)을 전부 하나의 문자열로 연결하는 코드

문자열 연결을 잘못 사용한 예
public String statement() {
  String result = "";
  for (int i = 0; i < numItems(); i++) {
    result += lineForItem(i); //문자열 연결
  }
  
  return result;
}

위 코드는 품목이 많은 경우 심각하게 느려질 수 있다

성능을 포기하고 싶지 않다면 String 대신 StringBuilder를 사용하자

StringBuilder를 사용하면 문자열 연결 성능이 크게 향상 된다
public String statement2() {
  StringBuilder b = new StringBuilder(numItems() * LINE_WIDTH); 
  for (int i = 0; i < numItems(); i++) {
    b.append(lineForItem(i));
  }
  return b.toString();
}

자바 6 이후 문자열 연결 성능이 많이 개선 됐지만 아직도 두 메서드의 성능 차이는 크다
````

#### 원칙은 간단하다 성능에 신경써야 한다면 많은 문자열을 연결할 때는 문자열 연결 연산자(+)를 피하자
#### 대신 StringBuilder의 append 메서드를 사용하자
#### 문자 배열을 사용하거나 문자열을 연결하지 않고 하나씩 처리하는 방법도 있다