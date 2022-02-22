package chapter3.item10;

public class Main {

    public static void main(String[] agrs) {

        /*****
        //대칭성 위배
        CaseInsensitivieString cis = new CaseInsensitivieString("hi");
        String testVal = "hi";

        System.out.println(cis.equals(testVal));    //true
        System.out.println(testVal.equals(cis));    //false
        ****/

        //대칭성 만족
        CaseInsensitivieStringGood cis = new CaseInsensitivieStringGood("hi");
        String testVal = "hi";

        System.out.println(cis.equals(testVal));    //true
        System.out.println(testVal.equals(cis));    //false

    }

}
