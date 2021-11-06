package mini.spring.ioc;


import mini.spring.ioc.annotation.Component;
import mini.spring.ioc.util.PackageUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Dragon
 */
public class MiniAnnotationConfigApplicationContext {

    private static final Logger logger = Logger.getLogger(MiniAnnotationConfigApplicationContext.class.getName());

    private Map<String, Object> miniIoC = new HashMap<>();

    public Set<String> getBeanNames() {
        return miniIoC.keySet();
    }
    // TODO delete later
    public Map<String, Object> getMiniIoC() {
        return miniIoC;
    }

    public MiniAnnotationConfigApplicationContext(String packageName) {
        // get all target classes on the package
        logger.info("Get all classes with annotation on specified package...");
        Set<Class<?>> classesWithAnnotation = getAnnotationClassesByFilter(packageName);

        // create bean set according to beanDefinition
        logger.info("Create beanDefinitions based on classes with annotation...");
        Set<BeanDefinition> beanDefinitions = createBeanDefinitions(classesWithAnnotation);

        // set object to IoC
        // 1.
        createObjectBasedOnBeanDefinition(beanDefinitions);

        // autowire bean for object

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

    private void createObjectBasedOnBeanDefinition(Set<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition -> {
            Class beanClass = beanDefinition.getBeanClass();
            try {
                Object beanInstance = beanClass.newInstance();
                miniIoC.put(beanDefinition.getBeanName(), beanInstance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        });
    }

}
