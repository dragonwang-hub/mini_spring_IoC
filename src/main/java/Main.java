import mini.spring.ioc.MiniAnnotationConfigApplicationContext;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String packageName = "mini.project";
        MiniAnnotationConfigApplicationContext miniAnnotationConfigApplicationContext
                = new MiniAnnotationConfigApplicationContext(packageName);

        Set<String> beanNames = miniAnnotationConfigApplicationContext.getBeanNames();
        beanNames.forEach(beanName -> {
            System.out.println(beanName + miniAnnotationConfigApplicationContext.getBean(beanName));
        });
    }
}
