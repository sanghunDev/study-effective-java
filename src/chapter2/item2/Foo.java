package chapter2.item2;

public class Foo {
    private String userName;    //필수값
    private int age;            //필수값
    private String address;     //필수값
    private String tel;         //선택값

    public Foo(String userName) {
        this.userName = userName;
    }

    public Foo(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Foo(String userName, int age, String address) {
        this.userName = userName;
        this.age = age;
        this.address = address;
    }

    public Foo(String userName, int age, String address, String tel) {
        this.userName = userName;
        this.age = age;
        this.address = address;
        this.tel = tel;
    }
}
