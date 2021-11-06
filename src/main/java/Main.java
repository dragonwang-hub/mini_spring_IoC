import mini.spring.ioc.MiniAnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        String packageName = "mini.project";
        MiniAnnotationConfigApplicationContext miniAnnotationConfigApplicationContext
                = new MiniAnnotationConfigApplicationContext(packageName);
//        System.out.println(classes);
    }
}
