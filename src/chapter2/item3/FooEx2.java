package chapter2.item3;

public class FooEx2 {
    private static final FooEx2 INSTANSE = new FooEx2();

    private FooEx2() {}

    public static FooEx2 getInstance() {
        return INSTANSE;
    }

    public void getPrint() {
        System.out.println("hello");
    }
}
