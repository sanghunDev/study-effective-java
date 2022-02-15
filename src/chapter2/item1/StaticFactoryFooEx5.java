package chapter2.item1;

public abstract class StaticFactoryFooEx5 {
    protected abstract void getPrint();

    public static StaticFactoryFooEx5 getNewInstance() {
        StaticFactoryFooEx5 child = null;

        try {
            Class<?> childCls = Class.forName("chapter2.demo.Ex5Child");
            child = (StaticFactoryFooEx5) childCls.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("클래스가 없어!!");
        } catch (InstantiationException e) {
            System.out.println("인스턴스화 실패!!");
        } catch (IllegalAccessException e) {
            System.out.println("접근 불가능!!");
        }

        return child;
    }
}