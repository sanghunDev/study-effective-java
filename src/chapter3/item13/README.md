# Item 13. clone 재정의는 주의하여 진행하라

### 대략적인 요점정리
* clone()은 사용하기에 문제가 있다.
* clone() 메서드가 선언된 곳이 Cloneable이 아닌 Object이다.
* clone() 메서드의 접근 제한자가 protected이다.
* Cloneable을 구현하는 것만으로는 외부 객체에서 clone()을 호출할 수 없다.


### 추가 정리 필요
* clone() 메서드를 잘 동작할 수 있는 구현 방법
* clone() 을 사용하기 위한 상황
* clone() 말고 다른 방법이 있는지

### Cloneable 인터페이스의 용도
* Object의 protected 메서드인 clone의 동작 방식을 결정한다.
* Cloneable을 구현한 클래스의 인터페이스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환한다.
* Cloneable을 구현하지 않은 클래스에서  clone을 호출하는 경우 CloneNotSupportedException을 던진다.
clone 메서드의 허술한 일반 규약
* 복사의 정확한 뜻은 그 객체를 구현한 클래스에 따라 다를 수 있다.

#### 추후 보충 예정