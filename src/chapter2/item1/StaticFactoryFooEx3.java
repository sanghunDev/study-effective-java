package chapter2.item1;

public abstract class StaticFactoryFooEx3 {
    abstract void getPrint();

    public static StaticFactoryFooEx3 getNewInstance() {
        return new Ex3Child();
    }
}

class Ex3Child extends StaticFactoryFooEx3 {
    @Override
    public void getPrint() {
        System.out.println("나는 자식");
    }
}
