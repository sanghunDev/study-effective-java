# ITEM 76 가능한 한 실패 원자적으로 만들라

--------------------------------------------
실패 원자적(failure-atomic)
* 호출된 메서드가 실패하더라도 해당 객체는 메서드 호출 전 상태를 유지해야 한다
* 메서드를 실패 원자적으로 만드는 방법
  * 가장 간단한 방법은 불변 객체로 설계하는 것
    * 불변 객체의 상태는 생성 시점에 고정되어 절대 변하지 않기 때문에 태생부터 실패 원자적이다
  * 가변 객체를 실패 원자적으로 만드는 방법은 작업 수행에 앞서 매개변수 유효성을 검사하는 것이다
    * 객체의 내부 상태를 변경하기 전에 잠재적 예외의 가능성 대부분을 걸러낼 수 있다
  * 객체의 임시 복사본에서 작업을 수행하고 작업이 성공적으로 완료되면 원래 객체와 교체하는 것
    * 데이터를 임시 자료구조에 저장해 작업하는게 더 빠를 때 적용하기 좋다
  * 작업 도중 발생하는 실패를 가로채는 복구 코드를 작성해 작업 전 상태로 되돌리는 방법
    * 주로 디스크 기반의 내구성을 보장해야 하는 자료구조에서 사용되는데 자주 쓰이지는 않는다

실패 원자성은 일반적으로 권장되긴 하지만 항상 달성할 수 있는건 아니다
* 예로 두 스레드가 동기화 없이 같은 객체를 동시에 수정한다면 그 객체의 일관성이 깨질 수 있다
* 또한 실패 원자적으로 만들수 있더라도 실패 원자성을 달성하기 위한 비용이나 복잡도가 아주 큰 경우도 있으니 꼭 그렇게 해야되는건 아니다
  * 그래도 문제가 무엇인지 알고 나면 실패 원자성을 공짜로 얻을 수 있는 경우가 더 많다

메서드 명세에 기술한 예외라면 혹시 예외가 발생하더라도 객체의 상태는 메서드 호출 전과 똑같이 유지된다는게 기본 규칙이다
* 이 규칙을 지키지 못한다면 실패 시의 객체 상태를 API 설명에 명시해야 한다
  * 이상적인 내용이며 실제 상당수의 API 문서에서 잘 지켜지지 않고 있다


