package chapter2.item1;

public class StaticFactoryFooEx1 {
    private String name;
    private int age;

    private StaticFactoryFooEx1(String name) {
        this.name = name;
    }

    private StaticFactoryFooEx1(String name, int age) {
        this.name = name;
        this.age = age;
    }

    static public StaticFactoryFooEx1 getNewInstanceByNM(String name) {
        return new StaticFactoryFooEx1(name);
    }

    static public StaticFactoryFooEx1 getNewInstanceByNMAndAge(String name, int age) {
        return new StaticFactoryFooEx1(name, age);
    }
}
