package chapter2.item2;

public class Main {

    public static void main(String[] args) {
        Foo foo = new Foo("hong");
        Foo foo2 = new Foo("hong",10);
        Foo foo3 = new Foo("hong",10,"대전");
        Foo foo4 = new Foo("hong",10,"대전","");

        FooJavaBeans fooJavaBeans = new FooJavaBeans();
        fooJavaBeans.setUserName("hong");
        fooJavaBeans.setAge(10);
        fooJavaBeans.setAddress("대전");
        fooJavaBeans.setTel("");

        FooBuilder fooBuilder = new FooBuilder.Builder("hong",10,"대전")
                .tel("01012345678")
                .build();
    }
}
