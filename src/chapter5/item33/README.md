# ITEM 33 타입 안전 이종 컨테이너를 고려하라

--------------------------------------------

#### 타입 안전 이종 컨테이너 패턴(type safe heterogeneous container pattern)
컨테이너 대신 키를 매개변수화 하고 컨테이너에 값을 넣거나 뺄때 매개변수화한 키를 함께 제공하여 제네릭 타입 시스템이 값의 타입이 키와 같음을 보장해주는것

``` java
//Favorites api
public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance);
    public <T> T getFavorite(Class<T> type);
}

//Favorites의 구현
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();
    
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }
    
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type);
    }
}

public static void main(String[] args) {
    Favorites f = new Favorites();
    
    f.putFavorite(String.class, "Java");
    f.putFavorite(Integer.class, 0xcafebabe);
    f.putFavorite(Class.class, Favorites.class);
    
    String favoriteString = f.getFavorite(String.class);
    int favoriteInteger = f.getFavorite(Integer.class);
    Class<?> favoriteClass = f.getFavorite(Class.class);
    
    System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getName());
}
```
* Favorites 인스턴스는 타입 안전하다
* 모든 키의 타입이 제각각이라 일반적인 맵과 다르게 여러 가지 타입 원소를 담을 수 있다
* 따라서 Favorites는 타입 안전 이종 컨테이너라고 부를만하다
* Favorites가 사용하는 private 맵 변수인 favorites의 타입을 보면 비한정적 와일드카드 타입이다
  * 얼핏 생각하기엔 아무것도 넣을수 없을거 같지만 와일드카드 타입이 중첩이라 키가 와일드 카드이다
  * 따라서 모든 키가 서로 다른 매개변수화 타입이 가능하다
    * 다양한 타입 지원이 가능하다
* favorites 맵의 값 타입은 단순히 Object이다
  * 이 맵은 키와 값 사이의 타입 관계를 보증하지 않는다는 뜻 
  * getFavorite()에서 반환시 Object를 반환하지만 우리는 이걸 T로 바꿔서 반환 해야된다
    * getFavorite() 구현은 Class의 cast 메서드를 사용해 이 객체 참조를 Class 객체가 바라보는 타입으로 동적 형변환 한다
    * cast 메서드는 형변환 연산자의 동적 버전이며 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지 검사한다
    * 여기서 인스턴스가 일치한다면 그대로 반환하고 아니면 ClassCastException 을 던지기 때문에 favorites 맵 안의 값은 해당 키의 타입과 항상 일치하게 된다
    * 그럼 왜 cast 메서드를 사용하는가 의문이 드는데 cast 메서드의 시그니처가 Class 클래스가 제네릭이라는 이점을 완벽히 활용하기 때문이다
``` java
cast의 반환 타입은 Class 객체의 타입 매개변수와 같다
이 부분이 getFavorite 메서드에서 필요한 부분이며 T로 비검사 형변환을 하지 않아도 타입 안정성을 보장해주게 된다

public class Class<T> {
    T cast(Object obj);
}
```
* 하지만 지금 상태의 Favorites 클래스에는 문제가 있다
  * 클라이언트가 Class 객체를 로타입으로 넘기면 Favorites 인스턴스의 타입 안정성이 깨진다
    * 하지만 이런 코드는 비검사 경고가 출력된다
    * 타입 불변식을 어기지 않게 하려면 putFavorite 메서드에서 인수로 주어진 instance의 타입이 type으로 명시한 타입으로 같은지 동적 형변환으로 확인하면 된다
  * 실체화 불가 타입에는 사용할 수 없다
    * String이나 STring[]은 저장할 수 있지만 List<String>은 List<String>.class가 불가능 하기 때문에 사용이 안된다
    * 이 문제에 대한 완벽한 해결책은 없지만 슈퍼 타입 토큰을 사용하려는 시도가 있었다
* Favorites가 사용하는 타입 토큰은 비한정적이라 어떤 Class 객체든 받아들이는데 타입을 좀 제한하고 싶다면 한정적 타입 토큰을 사용하자
  * 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰
  
#### 리터럴 타입 토큰 : 컴파일타임 타입 정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고 받는 class 리터럴
#### 컬렉션 API로 대표되는 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수의 수가 고정적이다
#### 컨테이너 자체가 아닌 키를 타입 매개변수로 바꿔서 위와 같은 제약이 없는 타입 안전 이종 컨테이너를 만들 수 있다
#### 타입 안전 이종 컨테이너는 Class를 키로 사용하고 이렇게 사용되는 Class 객체를 타입 토큰이라고 부른다
#### 직접 구현안 키 타입도 사용 가능하다(예를 들자면 데이터베이스의 행을 표현한 DatabaseRow 타입에는 제네릭 타입인 Column<T>를 키로 사용 가능하다)