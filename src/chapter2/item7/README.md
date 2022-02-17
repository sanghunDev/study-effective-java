# ITEM 7 다 쓴 객체 참조를 해제하라 

--------------------------------------------
#### GC에 너무 의존하지 말자

### 메모리 누수가 일어나는 상황
아래의 Stack을 구현한 간단한 예제로 메모리 누수가 발생하는 상황을 살펴보자
```` java

public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY= 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }
  
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
````
위의 코드는 특별한 문제가 없어 보인다

하지만 이런 코드를 오랫동안 실행하면 GC 활동과 메모리 사용량이 늘어나 성능 저하와 오류를 일으킬 가능성이 있다

### 문제점은 무엇인가?
```` java
public Object pop() {
    if (size == 0) {
        throw new EmptyStackException();
    }
    return elements[--size];
}
````
위에서 스택을 구현한 코드에서는 스택이 커졌다 줄어졌다 할때

스택에서 꺼내진 객체들은 해당 객체들이 다시 사용되지 않아도 GC가 회수하지 않는다

* 스택이 객체들의 다 쓴 참조(obsolete reference)를 가지고 있기 때문
  * 다 쓴 참조란 앞으로 다시 쓰지 않을 참조를 말한다
  * 위 코드에서는 elements 배열의 활성영역 밖의 참조들이다
    * 활성영역은 인덱스가 size보다 작은 원소로 구성된다
* 위와 같이 객체 참조 하나를 살려두면 GC는 그 객체 뿐만이 아니라 그 객체가 참조하는 모든 객체를 회수하지 못한다

### 해결방안
* 해당 참조를 다 사용한 경우 null 처리를 해준다
  * null 처리로 인해 참조가 해제된다

```` java
public Object pop() {
    if (size == 0) {
        throw new EmptyStackException();
    }
    Object result = elements[--size];
    elements[--size] = null;
    return result;
}
````
* elements[--size] 를 null 로 처리하여 참조를 해제해준다
  * 위와 같은 처리로 만약 null 처리한 참조를 사용하려고 한다면 NPE가 발생하게 된다
    * 오류 조기 발견 가능
    

* 하지만 모든 객체를 사용후 null 처리할 필요는 없다
  * 오히려 코드가 지저분해진다
  * null 처리하는 경우는 예외적인 상황이어야 한다
  
* 가장 좋은 해결방안은 참조를 담은 변수를 유효 범위 밖으로 밀어내는것
  * 변수의 범위를 최소화 시켜 정의 했다면 자연스럽게 일어나는일 
  
### 왜 Stack에서는 null 처리를 해준걸까?
* 위의 예제에서 null 처리하는 이유는 스택이 자기 메모리를 직접 관리하기 때문이다
* 이 스택은 (객체 자체가 아닌 객체 참조를 담는) elements 배열로 저장소 풀을 만들어 원소를 관리한다
* 배열의 활성 영역에 속한 원소들이 사용되고 비활성 영역은 쓰이지 않는다
  * 가지비 컬렉터는 이 사실을 알 길이 없다
  * 가비지 컬렉터가 보기엔 비활성 영역에서 참조하는 객체도 똑같이 유효한 객체다
    * 비활성 영역의 객체가 쓸모 없다는건 개발자만 알 수 있다
    * GC가 모르기 때문에 개발자가 null 처리를 해서 알려준다

### 정리
* 자기 메모리를 스스로 관리하는 클래스라면 항상 메모리 누수에 주의해야한다


* 캐시도 메모리 누수를 일으키는 주범 중 하나다
* 객체 참조를 캐시에 넣고 이 사실을 까먹는 경우가 많다
  * 외부에서 키를 참조하는 동안 엔트리가 살아있는 캐시가 필요한거라면 WeakHashMap을 사용해 캐시를 만들어서 해결한다


* 리스너 또는 콜백이라 부르는 녀석들도 메모리 누수의 주범 중 하나다
  * 콜백을 등록만 하고 명확하게 해지하지 않는다면 따로 조치가 없는 이상 계속 쌓이게된다
    * 약한 참조로 저장하면 해결된다
      * 예) WeakHashMap 의 키로 저장

#### 메모리 누수는 발견 하기가 어려워 오랫동안 시스템에 존재하는 경우가 많다
#### 코드 리뷰나 힙 프로파일러 같은 디버깅 도구를 사용해서 발견하기도 하지만 미리 예방법을 익혀서 코드를 작성하는게 중요하다