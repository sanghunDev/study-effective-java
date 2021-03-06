# ITEM 21 인터페이스는 구현하는 쪽을 생각해 설계하라

--------------------------------------------

#### 자바 8이전에는 기존 구현체를 깨뜨리지 않고 인터페이스에 새로운 메서드를 추가하는 방법이 없었다

#### 자바 8부터 디폴트 메서드를 통해 기존 인터페이스에 새로운 메서드를 추가할 수 있게 되었다

#### 디폴트 메서드를 사용하면 인터페이스를 구현한 모든 구현체에서 디폴트 메서드를 재정의 하지 않아도 디폴트 메서드를 사용 가능하다

#### 디폴트 메서드가 생겨난 덕분에 하위 호환성이 좋아졌지만 자바 8 이전에 작성된 코드들은 인터페이스에 새로운 메서드가 추가되는 일은 없을거라고 가정하고 짠 것이니 항상 주의해야 한다
* 디폴트 메서드가 하위 호환성이 좋지만 모든 경우에 매끄럽게 연결되지 않을수 있다
* 생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기는 어렵다

#### 기존 인터페이스에 디폴트 메서드로 새 메서드를 추가하는 일은 꼭 필요한 경우가 아니면 피해야 한다
* 디폴트 메서드는 컴파일에 성공하더라도 기존 구현체에 런타임 오류를 일으킬 수 있다
* 추가하려는 디폴트 메서드가 기존 메서드와 충돌을 일으키지 않는지 생각해야한다
* 하지만 새로운 인터페이스를 만드는 경우에는 표준적인 메서드를 제공하는데 아주 적합하다

#### 디폴트 메서드는 인터페이스로부터 메서드를 제거하거나 기존 메서드의 시그니처를 수정하는 용도가 아님을 명심하자
* 이런식으로 인터페이스를 변경하면 반드시 기존 클라이언트가 망가지게 된다

#### 디폴트 메서드가 생겼어도 인터페이스 설계시에는 세심한 주의가 필요하다
* 디폴트 메서드로 기존 인터페이스에 새로운 메서드를 추가하면 분명히 위험 부담도 있다
* 새로운 인터페이스라면 반드시 3가지 이상의 다른 방식 구현하여 테스트를 해보자
* 각 인터페이스의 인스턴스를 다양한 작업에 활용하는 클라이언트 코드도 작성해보자
* 인터페이스를 릴리즈한 후에도 결함을 수정하는게 가능할수도 있지만 절대 그 가능성에 기대면 안된다