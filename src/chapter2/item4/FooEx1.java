package chapter2.item4;

public class FooEx1 {
    public static boolean useYn = true;
    public static int startNo = 1;

    private FooEx1() {
        throw new IllegalStateException("인스턴스화 불가능!");
    }
}
