package chapter2.item3;

public class Foo {
    public static final Foo INSTANCE = new Foo();

    private Foo() {}

    public void getPrint() {
        System.out.println("hello");
    }
}
