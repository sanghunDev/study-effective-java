package chapter2.demo;

import chapter2.item1.StaticFactoryFooEx5;

public class Ex5Child extends StaticFactoryFooEx5 {

    @Override
    protected void getPrint() {
        System.out.println("나는 다른 패키지에 있는 자식");
    }
}
