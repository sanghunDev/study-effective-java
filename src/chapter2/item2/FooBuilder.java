package chapter2.item2;

public class FooBuilder {
    private final String userName;
    private final int age;
    private final String address;
    private final String tel;

    public static class Builder {
        private final String userName;    //필수값
        private final int age;            //필수값
        private final String address;     //필수값
        private String tel;         //선택값

        public Builder(String userName, int age, String address) {
            this.userName = userName;
            this.age = age;
            this.address = address;
        }

        public Builder tel(String tel) {
            this.tel = tel;
            return this;
        }

        public FooBuilder build() {
            return new FooBuilder(this);
        }
    }

    private FooBuilder(Builder builder) {
        userName = builder.userName;
        age = builder.age;
        address = builder.address;
        tel = builder.tel;
    }

}
