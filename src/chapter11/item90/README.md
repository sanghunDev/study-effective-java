# ITEM 90 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라

--------------------------------------------
Serializable을 구현하기로 결정한 순간 언어의 정상 메커니즘인 생성자 이외의 방법으로 인스턴스 생성이 가능해진다
* 버그와 보안 문제가 일어날 가능성이 커진다

직렬화 프록시 패턴을 사용하면 버그와 보안 문제가 줄어든다
* 적절한 프록시 패턴은 별로 복잡하지 않다
  * 바깥 클래스의 논리적 상태를 정밀하게 표현하는 중첩 클래스를 설계해 private static으로 선언한다
    * 이 중첩 클래스가 바로 바깥 클래스의 직렬화 프록시다
  * 중첩 클래스의 생성자는 단 하나여야 하며 바깥 클래스를 매개변수로 받아야 한다
  * 이 생성자는 단순히 인수로 넘어온 인스턴스의 데이터를 복사한다
  * 일관성 검사나 방어적 복사도 필요 없다
  * 설계상 직렬화 프록시의 기본 직렬화 형태는 바깥 클래스의 직렬화 형태로 쓰기에 이상적이다
  * 바깥 클래스와 직렬화 프록시 모두 Serializable을 구현한다고 선언해야 한다

```` java
Period 클래스용 직렬화 프록시
Period 클래스는 아주 간단해서 직렬화 프록시도 바깥 클래스와 완전히 같은 필드로 구성되었다

public final class Period {
   private final Date start;
   private final Date end;
 
    public Period(Date start, Date end) {
      this.start = new Date(start.getTime());
      this.end = new Date(end.getTime());
      
      if(this.start.compareto(this.end) > 0)
        throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
  
    }

    public Date start() { return new Date(start.getTime()); }   
    public Date end() { return new Date(end.getTime()); }
    public String toString() { return  start + " - " + end; }
       
    private static class SerializationProxy implements Serializable {
      private final Date start;
      private final Date end;
      
      SerializationProxy(Period p) {
        this.start = p.start;
        this.end = p.end;
      }
      
      private static final long = serialVersionUID = 12312312314123;
      
      /**
      * 바깥 클래스와 논리적으로 동일한 인스턴스를 반환하는 readResolve 메서드
      * 역직렬화시 직렬화 시스템이 직렬화 프록시를 다시 바깥 클래스의 인스턴스로 변환하게 해준다
      * readResolve 메서드는 공개된 API만을 사용해 바깥 클래스의 인스턴스를 생성하는데 이 패턴이 아름다운 이유가 바로 이거다
      * 이 패턴은 직렬화의 언어도단적 특성을 상당 부분 제거한다
      * 일반 인스턴스를 만들 때와 똑같은 생성자 정적 팩터리 혹은 다른 메서드를 사용해 역직렬화된 인스턴스를 생성하는 것이다
      * 따라서 역직렬화된 인스턴스가 해당 클래스의 불변식을 만족하는지 검사할 또 다른 수단을 강구하지 않아도 된다
      * 그 클래스의 정적 팩터리나 생성자가 불변식을 확인해주고 인스턴스 메서드들이 불변식을 잘 지켜준다면 따로 더 해줘야 할 게 없다
      */
      // Period.SerializationProxy용 readReslove 메서드
      private Object readReslove() {
        return new Period(start, end);  //public 생성자 사용
      }
    }

  /**
  * 직렬화 프록시 패턴용 (범용적인 메서드라 직렬화 프록시를 사용하는 모든 클래스에 그대로 복사해서 사용가능)
  * 자바의 직렬화 시스템이 바깥 클래스의 인스턴스 대신 SerializationProxy의 인슽너스를 반환하게 하는 역할
  * 직렬화가 이뤄지기 전에 바깥 클래스의 인스턴스를 직렬화 프록시로 변환해준다
  */
  private Object writeReplace() {
    return new SerializationProxy(this);
  }
 
 writeReplace 덕분에 직렬화 시스템은 결코 바깥 클래스의 직렬화된 인스턴스를 생성해낼 수 없다
 하지만 공격자는 불변식을 훼손하고자 이런 시도를 해 볼 수 있는데 readObject 메서드를 바깥 클래스에 추가하면 그런 공격도 막을 수 있다
 
 //직렬화 프록시 패턴용 readObject 메서드
 private void readObject(ObjectInputStream stream) throws InvalidObjectException {
  throw new InvalidObjectException("프록시가 필요합니다");
 }
 
}
````

방어적 복사 처럼 직렬화 프록시 패턴은 가짜 바이트 스트림 공격과 내부 필드 탈취 공격을 프록시 수준에서 차단해준다

앞의 두 접근법과 다르게 직렬화 프록시는 Period의 필드를 final로 선언해도 되기 때문에 Period 클래스를 진정한 불변으로 만들 수 있다

또 어떤 필드가 기만적인 직렬화 공격의 목표가 될지 고민할 필요도 없고 역직렬화 때 유효성 검사를 수행하지 않아도 된다

직렬화 프록시 패턴이 readObject에서의 방어적 복사보다 강력한 경우가 하나 더 있다
* 직렬화 프록시 패턴은 역직렬화한 인스턴스와 원래의 인스턴스의 클래스가 달라도 정상 작동한다

직렬화 프록시 패턴에는 두가지 한계가 있다
1. 클라이언트가 멋대로 확장할 수 있는 클래스에는 적용이 불가능하다
2. 객체 그래프에 순환이 있는 클래슷에도 적용할 수 없다
* 직렬화 프록시만 가졌을 뿐 실제객체는 아직 만들어진 것이 아니기 때문에 직렬화 프록시의 readResolve안에서 호출하려고 하면 ClassCastException이 발생할 것이다

#### 직렬화 프록시는 강력하며 안전하지만 속도는 조금 느려질 수도 있다
#### 제3자가 확장할 수 없는 클래스라면 가능한 한 직렬화 프록시 패턴을 사용하자
#### 이 패턴이 아마도 중요한 불변식을 안정적으로 직렬화해주는 가장 쉬운 방법일 것이다
