# ITEM 11 equals를 재정의하려거든 hashCode도 재정의하라 

--------------------------------------------
### equals를 재정의한 클래스 모두에서 hashCode도 재정의해야 한다
* hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 HashMap HashSet 같은 컬렉션의 원소로 사용할 때 문제가 발생 가능
* 재정의한 hashCode는 Object의 API문서에 기술된 일반 규약을 따라야 한다
* 서로 다른 인스턴스라면 되도록 해시코드도 서로 다르게 구현해야 한다

#### Object 명세에서 발췌한 규약
* equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야 한다
  * 애플리케이션을 다시 실행한다면 이 값이 달라져도 상관없다
* equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다
* equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다
  * 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다

#### 성능 향상을 위해 해시코드 계산시 핵심 필드를 생략 하지말자
* 속도는 빨라지지만 해시 품질이 나빠져 해시테이블의 성능이 저하되는 문제가 발생 가능
#### hashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말자
* 클라이언트가 값에 의존하지 않도록 해준다
* 추후 계산방식 변경에 용이하다
* 나쁜예로 String, Intger 등.. 자세하게 표현되고 있다 (추후 해시 관련 재배포가 어렵다)