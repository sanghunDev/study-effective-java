# ITEM 9 try-finally보다는 try-with-resources를 사용하라

--------------------------------------------
#### 자원 닫기의 안전망으로 finalizer를 활용하는 경우가 많지만 item8에서 봤듯이 믿을만 하지 못하다

### 전통적으로 자원을 제대로 닫을때 사용하던 방법은 try-finally이다
* 예외가 발생하거나 메서드에서 반환되는 경우도 마찬가지
```` java
static String firstLineOfFile(String path) throws IOException {
  BufferedReader br = new BufferedReader(new FileReader(path));
  try {
    return br.readLine();
  } finally {
    br.close();
  }
}
````
* 자원이 많아지면 지저분해진다
```` java
static void copy(String src, String dst) throws IOException {
  InputStream in = new FileInputStream(src);
  try {
    OutputStream out = new FileOutputStream(dst);
    try{
      byte[] buf = new byte[BUFFER_SIZE];
      int n;
      while ((n = in.read(buf)) >= 0)
        out.write(buf, 0, n);
    } finally {
      out.close();
    }
  } finally {
    in.close();
  }
}
````
* 초보자 뿐만 아니라 많은 개발자들에게서 굉장히 많이 보이는 케이스이다
* 위와 같이 try-finally문을 잘 사용 했어도 약간의 문제는 존재한다
  * 예외 자체는 try와 finally 블록 모두 발생 가능하다
  * 기기의 물리적 예외가 발생한다면 firstLineOfFile() 안의 readLine() 메서드가 예외를 던질테고 그럼 br.close() 메서드는 실패할것이다
  * 이런 경우 두번째 예외가 첫번째 예외를 삼켜 버리고 스택 추적 내역에 첫번째 예외에 관한 정보가 없게된다
    * 따라서 처음 발생 원인을 찾는 디버깅이 어려워진다

### try-with-resources 를 사용하자
* 위와 같은 문제들은 java 7의 try-with-resources를 통하여 모두 해결 되었다
  * 이 구조를 사용하기 위해선 AutoCloseable 인터페이스를 구현해야한다
    * void를 반환하는 close 메서드 하나만 있다
* 위의 예제들을 try-with-resources를 통해 개선해보자

#### try-with-resources 를 사용하여 firstLineOfFile를 리팩토링 한 코드이다
```` java
static String firstLineOfFile(String path) throws IOException {
  try (BufferedReader br = new BufferedReader(
    new FileReader(path))) {
    return br.readLine();
  }
}
````
* close를 숨김으로 인해 readLine과 close 양쪽에서 예외가 발생해도 readLine의 예외가 기록되게 된다
  * 실제 프로그래머가 보고싶어하는 예외를 보존시키고 다른걸 숨길수 있게 되었다
  * 숨겨진 예외 조차도 스택 추적 내역에서 suppressed라는 꼬리표를 달고 출력된다
    * Throwable에 추가된 getSuppressed 메서드를 이용하면 프로그램 코드에서 가져올 수도 있다
```` java
static String firstLineOfFile(String path) throws IOException {
  try (BufferedReader br = new BufferedReader(
    new FileReader(path))) {
    return br.readLine();
  } catch (IOException e) {
    return "기본 예외"
  }
}
````
* 보통의 try-finally처럼 catch도 사용이 가능하다
* catch 덕분에 try를 중첩하지 않고도 다수의 예외 처리가 가능하다

#### try-with-resources 를 사용하여 copy를 리팩토링 한 코드이다
```` java
static void copy(String src, String dst) throws IOException {
  try (InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst)) {
    byte[] buf = new byte[BUFFER_SIZE];
    int n;
    while ((n = in.read(buf)) >= 0)
        out.write(buf, 0, n);
  }
}
````
* 다중 자원에 대한 예외 처리에서도 try-with-resources 를 적용하자 코드가 훨씬 간결해졌다

### 정리
* 꼭 회수가 필요한 자원은 try-with-resources 를 사용하자 
* 가독성도 좋아지고 간결해지는데 쓰지 않을 이유가 없다