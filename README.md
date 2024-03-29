## Effective Java 3/E 공부
------------------------------

#### 회사에서 java로 개발을 하고 있지만 구현에만 치중되어 깊이가 없다는 느낌을 받았다
#### 현재 사용하는 기술들의 깊이를 더하기 위해 올해는 java와 spring의 깊이를 더해보기로 하였다

#### 이 책을 완독하는 시점에는 조금더 java를 이해하고 좋은 코드를 작성할 수 있었으면 한다
* 모르는 부분은 해결하고 넘어간다
* 단순히 책에 나와있는 예제 코드를 따라 치는것이 아니라 의미를 생각하며 작성한다
* 백기선님 인프런 강의를 통해 한번 더 복습 및 깊은 내용에 대해 실습한 링크 추가

### 2장 객체 생성과 파괴
  
  * [생성자 대신 정적 팩터리 메서드를 고려하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item1)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item01/README.md)
  * [생성자에 매개변수가 많다면 빌더를 고려하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item2)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item02/README.md)
  * [private 생성자나 열거타입으로 싱글턴임을 보증하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item3)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item03/README.md)
  * [인스턴스화를 막으려거든 private 생성자를 사용하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item4)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item04/README.md)
  * [자원을 직접 명시하지 말고 의존 객체 주입을 사용하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item5)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item05/README.md)
  * [불필요한 객체 생성을 피하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item6)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item06/README.md)
  * [다 쓴 객체 참조를 해제하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item7)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item07/README.md)
  * [finalizer와 cleaner사용을 피하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item8)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item08/README.md)
  * [try-finally보다는 try-with-resources를 사용하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter2/item9)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater01/item09/README.md)

### 3장 모든 객체의 공통 메서드

  * [equals는 일반 규약을 지켜 재정의하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter3/item10)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater02/item10/README.md)
  * [equals를 재정의하려거든 hashCode도 재정의하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter3/item11)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater02/item11/README.md)
  * [toString을 항상 재정의하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter3/item12)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater02/item12/README.md)
  * [clone 재정의는 주의해서 진행해라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter3/item13)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater02/item13/README.md)
  * [Comparable 을 구현할지 고민하라](https://github.com/sanghunDev/study-effective-java/tree/master/src/chapter3/item14)
    * [실습](https://github.com/sanghunDev/study-effective-java-part1/blob/master/src/main/java/com/example/studyeffectivejavapart1/chpater02/item14/README.md)
