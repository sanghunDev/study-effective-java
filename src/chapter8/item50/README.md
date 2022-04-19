# ITEM 50 적시에 방어적 복사본을 만들라

--------------------------------------------

자바는 안전한 언어이며 이게 자바를 쓰는 즐거움 중 하나다
* 네이티브 메서드를 사용하지 않아 C,C++ 같은 안전하지 않은 언어에서 보이는 버퍼 오버런, 배열 오버런, 와일드 포인터 같은 메모리 충돌 오류에서 안전하다
* 자바로 작성한 클래스는 시스템의 다른 부분에서 무슨 짓을 하든 그 불변식이 지켜진다
* 메모리 전체를 하나의 배열로 다루는 언어에서는 누릴 수 없는 강점이다

하지만 아무리 자바라도 다른 클래스로부터의 침범을 아무런 노력 없이 다 막을 수 없다
* 클라이언트가 여러분의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍해야 한다
* 실제로도 악의적인 의도를 가진 사람들이 시스템의 보안을 뚫으려는 시도가 늘고 있다
* 평범한 개발자도 순전히 실수로 내가 작성한 클래스를 오작동하게 만들 수 있다
* 후자의 경우가 더 많지만 어떤 경우든 적절치 않은 클라이언트로 부터 클래스를 보호하는데 충분한 시간을 투자하는게 좋다

어떤 객체든 그 객체의 허락 없이는 외부에서 내부를 수정하는 일은 불가능하다

하지만 주의를 기울이지 않으면 자기도 모르게 내부를 수정하도록 허락하는 경우가 흔하게 생긴다

```` java
public final class Period {
    private final Date start;
    private final Date end;
    
    /***
    @param start 시작시각
    @param end 종료시각 시작 시각보다 뒤어야 한다
    @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생
    @throws NullPointerException start나 end가 null이면 발생
    ***/
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(
            start + "가 " + end + "보다 늦다"
            );
        }
        this.start = start;
        this.end = end;
    }
    
    public Date start() {
        return start;
    }
    
    public Date end() {
        return end;
    }
    
    ..// 나머지 코드 생략
}

얼핏 보면 불변처럼 보이는 클래스이며 시작 시각이 종류 시각보다 늦을 수 없다는 불변식이 무리 없이 지켜질 것 같다
하지만 Date가 가변이라는 사실을 이용하면 어렵지 않게 그 불변식을 깨뜨릴 수 있다
````

```` java
Period 인스턴스의 내부를 공격해보자

Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(78);    //p의 내부를 수정
````

위와 같은 문제는 자바 8이후로는 쉽게 해결 가능하다
* Date 대신 불변인 Instant를 사용하면 된다(혹은 LocalDateTime , ZonedDateTime)
* Date는 낡은 API이니 코드를 새로 짜는 경우에는 사용하지 말자

하지만 앞으로 안쓴다고 이 문제에서 해방되는건 아니다
* Date 처럼 가변인 낡은 값 타입을 사용하던 시절이 길어서 여전히 많은 API와 내부 구현에 그 잔재가 남아있다
* 이번 아이템은 이러한 예전에 작성된 낡은 코드들을 대처하기 위한 것이다

외부 공격으로 부터 Period 인스턴스의 내부를 보호하려면 생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)해야 한다

그 후 Period 인스턴스 안에서는 원본이 아닌 복사본을 사용한다

```` java
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    
    if (this.start.compareTo(this.end) > 0)
        throw new IllegalArgumentException(this.start + "가 " + this.end + "보다 늦다"); 
}
````
새로 작성한 생성자를 사용하면 앞서의 공격은 더 이상 Period에 위협이 되지 않는다
* 매개변수의 유효성을 검사하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사한 점에 주목하자
* 순서가 부자연스러워 보이지만 반드시 이렇게 짜야한다
  * 멀티스레딩 환경에서 원본 객체의 유효성을 검사 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 원본 객체를 수정할 위험이 있기 때문이다
* 방어적 복사를 매개변수 유효성 검사전에 수행하면 이런 위험에서 해방될 수 있다
  * 컴퓨터 보안 커뮤니티에서는 이런걸 검사시점/사용시점(time-of-check/time-of-use) 공격 또는 TOCTOU 공격이라 부른다
* 또한 방어적 복사에 Date의 clone 메서드를 사용하지 않은 점도 주목하자
  * Date는 final이 아니기 때문에 clone이 Date가 정의한게 아닐 수 있다
  * 즉 clone이 악의를 가진 하위 클래스의 인스턴스를 반환할 수도 있다
  * 예로 이 하위 클래스는 start와 end 필드의 참조를 private 정적 리스트에 담아뒀다가 공격자에게 이 리스트에 접근하는 길을 열어줄 수도 있다
  * 결국 공격자에게 Period 인스턴스 자체를 송두리째 맡기는 꼴이며 이런 공격을 막기위해 매개변수가 제3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안된다

생성자를 수정하면 앞의 공격은 막을수 있지만 Period 인스턴스는 아직도 변경 가능하다
* 접근자 메서드가 내부의 가변 정보를 직접 드러내기 때문

```` java
Period 인스턴스를 향한 두 번째 공격

Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
p.end().setYear(78);    //p의 내부 변경
````

#### 두번째 공격을 막으려면 접근자가 가변 필드의 방어적 복사본을 반환하면 된다

```` java
수정한 접근자 : 필드의 방어적 복사본을 반환한다

public Date start() {
    return new Date(start.getTime());
}

public Date end() {
    return new Date(end.getTime());
}
````

새로운 접근자까지 적용하면 Period는 완벽한 불변이 된다
* 악의적이거나 부주의한 개발자라도 시작 시각이 종료 시각보다 나중일 수 없다는 불변식을 위배할 방법은 없다
  * 네이티브 메서드나 리플렉션 같이 언어 외적인 수단을 동원하지 않고서는 불가능
* Period 자신 말고는 가변 필드에 접근할 방법이 없으니 확실하다
  * 모든 필드가 객체 안에 완벽하게 캡슐화 되었다 
* 생성자와 다르게 접근자 메서드에서는 방어적 복사에 clone을 사용해도 된다
  * Period가 가지고 있는 Date 객체는 java.util.Date임이 확실하기 때문(신뢰할 수 없는 하위 클래스가 아니다)
    * 그래도 인스턴스를 복사하는데 일반적으로 생성자나 정적팩터리를 쓰는게 좋다(item 13 참고)

매개변수를 방어적으로 복사하는 목적이 불변 객체를 만들기 위해서만은 아니다
* 메서드든 생성자든 클라이언트가 제공한 객체의 참조를 내부의 자료구조에 보관해야 할 때면 항시 그 객체가 잠재적으로 변경될 수 있는지 생각해야 함
* 변경될 수 있는 객체라면 그 객체가 클래스에 넘겨진 뒤 임의로 변경 되어도 그 클래스가 문제없이 동작할지 따져보자
  * 확신할 수 없다면 복사본을 만들어 저장해야 한다
  * 예로 클라이언트가 건네준 객체를 내부의 Set 인스턴스에 저장하거나 Map 인스턴스의 키로 사용하면 나중에 그 객체가 변경되면 객체를 담고 있는 Set 혹은 Map의 불변식이 깨지게된다

내부 객체를 클라이언트에 건네주기 전에 방어적 복사본을 만드는 이유도 마찬가지다
* 클래스가 불변이든 가변이든, 가변인 내부 객체를 클라이언트에 반환할 때 반드시 심사숙고 해야한다
* 안심할 수 없다면 원본을 노출하지 말고 방어적 복사본을 반환해야 한다
* 길이가 1 이상인 배열은 무조건 가변임을 잊지 말자(item 15 참고)
  * 따라서 내부에서 사용하는 배열을 클라이언트에 반환 할 때는 항상 방어적 복사를 수행해야 한다
  * 혹은 배열의 불변 뷰를 반환하는 방법도 있다

방어적 복사는 성능 저하가 따르고 항상 쓸 수 있는 것도 아니다
* 같은 패키지에 속하는 등의 이유로 호출자가 컴포넌트 내부를 수정하지 않으리라 확신하면 방어적 복사를 생략할 수 있다
* 이런 상황이라도 호출자에서 해당 매개변수나 반환값을 수정하지 말아야 함을 명확히 문서화하는게 좋다
* 다른 패키지에서 사용한다고 해서 넘겨받은 가변 매개변수를 항상 방어적으로 복사해서 저장해야 하는건 아니다
* 때로는 메서드나 생성자의 매개변수로 넘기는 행위가 그 객체의 통제권을 명백히 이전함을 뜻한다
  * 통제권을 이전하는 메서드를 호출하는 클라이언트는 해당 객체를 더 이상 직접 수정하는 일이 없다고 약속해야 한다
  * 클라이언트가 건네주는 가변 객체의 통제권을 넘겨 받는다고 기대하는 메서드나 생성자에서도 그 사실을 확실히 문서에 기재해야 한다

#### 클래스가 클라이언트로부터 받는 혹은 클라이언트로 반환하는 구성요소가 가변이라면 그 요소는 반드시 방어적으로 복사해야 한다
#### 복사 비용이 너무 크거나 클라이언트가 그 요소를 잘못 수정할 일이 없음을 신뢰한다면 방어적 복사를 수행하는 대신 해당 구성요소를 수정했을 때의 책임이 클라이언트에 있음을 문서에 명시하도록 하자