package mini.spring.ioc;


import mini.spring.ioc.annotation.Component;
import mini.spring.ioc.util.PackageUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Dragon
 */
public class MiniAnnotationConfigApplicationContext {

    private static final Logger logger = Logger.getLogger(MiniAnnotationConfigApplicationContext.class.getName());

    public MiniAnnotationConfigApplicationContext(String packageName) {
        // get all target classes on the package
        logger.info("Get all classes with annotation on specified package.");
        Set<Class<?>> classesWithAnnotation = getAnnotationClassesByFilter(packageName);

        // create bean set according to beanDefinition
        logger.info("Get all classes with annotation on specified package.");
        Set<BeanDefinition> beanDefinitions = createBeanDefinitions(classesWithAnnotation);

        // autowire bean to miniIoC

    }

    private Set<Class<?>> getAnnotationClassesByFilter(String packageName) {
        Set<Class<?>> classes = PackageUtil.getClasses(packageName);
        return classes.stream().filter(clazz -> {
            // whether has annotation
            Component clazzAnnotation = clazz.getAnnotation(Component.class);
            return clazzAnnotation != null;
        }).collect(Collectors.toSet());
    }


    private Set<BeanDefinition> createBeanDefinitions(Set<Class<?>> classesWithAnnotation) {

        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();

        classesWithAnnotation.forEach(clazz -> {
            //beanName
            String annotationValue = clazz.getAnnotation(Component.class).value();
            String beanName = annotationValue;
            if ("".equals(beanName)) {
                // update it to class name
                beanName = StringUtils.uncapitalize(clazz.getSimpleName());
            }

            BeanDefinition beanDefinition = new BeanDefinition(beanName, clazz);
            beanDefinitions.add(beanDefinition);
        });

        return beanDefinitions;
    }
}
