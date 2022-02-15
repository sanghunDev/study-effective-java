package chapter2.item3;

public class Main {
    public static void main(String[] args) {
        Foo foo = Foo.INSTANCE;
        //hello
        foo.getPrint();

        FooEx2 fooEx2 = FooEx2.getInstance();
        //hello
        fooEx2.getPrint();

        //hello
        FooEx3.INSTANCE.getPrint();
    }
}
