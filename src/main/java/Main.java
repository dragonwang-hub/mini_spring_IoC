import mini.spring.ioc.util.PackageUtil;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Set<Class<?>> classes = PackageUtil.getClasses("mini.spring.ioc");
        System.out.println(classes);

    }
}
