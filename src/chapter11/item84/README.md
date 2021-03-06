# ITEM 84 프로그램의 동작을 스레드 스케줄러에 기대지 말라

--------------------------------------------
여러 스레드가 실행 중이면 운영체제의 스레드 스케줄러가 어떤 스레드를 얼마나 오래 실행할지 정한다

정상적인 운영체제라면 이 작업을 공정하게 수행하지만 구체적인 스케쥴링 정책은 운영체제마다 다를 수 있다

따라서 잘 작성된 프로그램이라면 이 정책에 좌지우지 돼서는 안 된다

#### 정확성이나 성능이 스레드 스케줄러에 따라 달라지는 프로그램이라면 다른 플랫폼에 이식하기 어렵다
* 빠르고 견고하며 이식성 좋은 프로그램을 작성하는 가장 좋은 방법은 실행 가능한 스레드의 평균적인 수를 프로세서 수보다 지나치게 많아지지 않도록 하는 것
  * 이렇게 해야 스레드 스케쥴러가 고민할 거리가 줄어든다
* 실행 준비가 된 스레드들은 맡은 작업을 완료할 때까지 계속 실행되도록 만들자
  * 이런 프로그램이라면 스레드 스케줄링 정책이 아주 상이한 시스템에서도 동작이 크게 달라지지 않는다
  * 여기서 실행 가능한 스레드의 수와 전체 스레드 수는 구분해야 한다
  * 전체 스레드 수는 훨씬 많을 수 있고 대기 중인 스레드는 실행 가능하지 않다
  
실행 가능한 스레드 수를 적게 유지하는 주요 기법은 각 스레드가 무언가 유용한 작업을 완료한 후에는 다음 일거리가 생길 때까지 대기하도록 하는 것이다

#### 스레드는 당장 처리해야 할 작업이 없다면 실행돼서는 안 된다
* 실행자 프레임워크를 생각해보면 스레드 풀 크기를 적절히 설정하고 작업은 짧게 유지하면 된다
  * 너무 짧으면 작업을 분배하는 부담이 오히려 성능을 떨어뜨릴 수도 있다

스레드는 절대 바쁜 대기(busy waiting) 상태가 되면 안 된다
* 공유 객체의 상태가 바뀔 때까지 쉬지 않고 검사해서는 안 된다는 뜻이다
* 바쁜 대기는 스레드 스케줄러의 변덕에 취약하며 프로세서에 큰 부담을 준다
  * 다른 유용한 작업이 실행될 기회를 빼앗는다

특정 스레드가 다른 스레드들과 비교해 CPU 시간을 충분히 얻지 못해서 간신히 돌아가는 프로그램을 보더라도 Thread.yield를 써서 문제를 고쳐 보려는 유횩을 떨쳐내자
* 어느정도 증상이 좋아질 수도 있지만 이식성은 그렇지 않을 것이다
* 처음 JVM에서는 성능을 높여준 yiel가 두 번째 JVM에서는 아무 효과 없고 세번째는 오히려 더 느려질 수 있다
* Thread.yield는 테스트할 수단도 없다
* 차라리 애플리케이션 구조를 바꿔 동시에 실행 가능한 스레드 수가 적어지도록 하는게 좋다

스레드 우선순위는 자바에서 이식성이 가장 나쁜 특성에 속한다
* 스레드 몇 개의 우선순위를 조율해서 애플리케이션의 반응 속도를 높이는 것도 타당할 수 있지만 정말 그래야 하는 경우는 드물며 이식성도 떨어진다
* 심각한 응답 불가 문제를 스레드 우선순위로 해결하려는 시도는 절대 합리적이지 않다

#### 프로그램의 동작을 스레드 스케줄러에 기대지 말자 견고성과 이식성을 모두 해친다
#### Thread.yield와 스레드 우선순위에 의존해서도 안 되며 이 기능들은 스레드 스케줄러에 제공하는 힌트일뿐이다
#### 스레드 우선순위는 이미 잘 동작하는 프로그램의 서비스 품질을 높이기 위해 드물게 쓰일 수 있지만 간신히 동작하는 프로그램을 고치는 용도로 사용해서는 안된다