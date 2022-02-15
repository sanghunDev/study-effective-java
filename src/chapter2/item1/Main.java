package chapter2.item1;

public class Main {

    public static void main(String[] args) {
        Foo foo1 = new Foo("hong");

        Foo foo2 = new Foo("hong",20);

        StaticFactoryFooEx1 staticFactoryFooEx1Sample1 = StaticFactoryFooEx1.getNewInstanceByNM("hong");
        StaticFactoryFooEx1 staticFactoryFooEx1Sample2 = StaticFactoryFooEx1.getNewInstanceByNMAndAge("hone", 20);

        StaticFactoryFooEx2 staticFactoryFooEx2Sample1 = StaticFactoryFooEx2.STATIC_FACTORY_FOO_EX_2;
        StaticFactoryFooEx2 staticFactoryFooEx2Sample2 = StaticFactoryFooEx2.STATIC_FACTORY_FOO_EX_2;

        //true
        System.out.println(staticFactoryFooEx2Sample1 == staticFactoryFooEx2Sample2);

        StaticFactoryFooEx3 staticFactoryFooEx3 = StaticFactoryFooEx3.getNewInstance();
        //나는 자식
        staticFactoryFooEx3.getPrint();

        StaticFactoryFooEx4 staticFactoryFooEx4First = StaticFactoryFooEx4.getNewInstance(true);
        //나는 첫번째 자식
        staticFactoryFooEx4First.getPrint();

        StaticFactoryFooEx4 staticFactoryFooEx4Second = StaticFactoryFooEx4.getNewInstance(false);
        //나는 두번째 자식
        staticFactoryFooEx4Second.getPrint();

        StaticFactoryFooEx5 staticFactoryFooEx5 = StaticFactoryFooEx5.getNewInstance();
        //나는 다른 패키지에 있는 자식
        staticFactoryFooEx5.getPrint();
    }
}
