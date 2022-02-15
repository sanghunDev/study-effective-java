package chapter2.item1;

public abstract class StaticFactoryFooEx4 {
    abstract void getPrint();

    public static StaticFactoryFooEx4 getNewInstance(boolean useYn) {
        StaticFactoryFooEx4 child = null;

        if(useYn) {
            child = new Ex4FirstChild();
        } else {
            child = new Ex4SecondChild();
        }

        return child;
    }
}

class Ex4FirstChild extends StaticFactoryFooEx4 {
    @Override
    public void getPrint() {
        System.out.println("나는 첫번째 자식");
    }
}

class Ex4SecondChild extends StaticFactoryFooEx4 {
    @Override
    public void getPrint() {
        System.out.println("나는 두번째 자식");
    }
}
