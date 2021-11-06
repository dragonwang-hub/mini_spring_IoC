package mini.spring.ioc;


import mini.spring.ioc.annotation.Autowired;
import mini.spring.ioc.annotation.Component;
import mini.spring.ioc.annotation.Qualifier;
import mini.spring.ioc.annotation.Value;
import mini.spring.ioc.util.PackageUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        // 1. handle @Value to create object, set to IoC
        createObjectBasedOnBeanDefinition(beanDefinitions);
        // 2. handle @autowired to autowire bean for object. get bean from IoC, and overwrite object
        autowiredBeanForObject(beanDefinitions);

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

                // get fields
                Field[] declaredFields = beanClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    // handle @Value
                    handleValueAnnotation(beanClass, beanInstance, declaredField);
                }
                miniIoC.put(beanDefinition.getBeanName(), beanInstance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleValueAnnotation(Class beanClass, Object beanInstance, Field declaredField) throws IllegalAccessException {
        Value declaredFieldAnnotation = declaredField.getAnnotation(Value.class);
        if (declaredFieldAnnotation != null) {
            String value = declaredFieldAnnotation.value();

            // use set method to set value
            String setMethodName = "set" + StringUtils.capitalize(declaredField.getName());
            try {
                Method setMethod = beanClass.getMethod(setMethodName, declaredField.getType());

                // convert value type based on property type, because @Value return is String
                Object declaredValue = null;
                switch (declaredField.getType().getName()) {
                    case "java.lang.String":
                        declaredValue = value;
                        break;
                    case "java.lang.Integer":
                        declaredValue = Integer.parseInt(value);
                        break;
                    default:
                        break;
                }
                setMethod.invoke(beanInstance, declaredValue);
            } catch (NoSuchMethodException | InvocationTargetException e) {
                logger.warning("Get method issue: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void autowiredBeanForObject(Set<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition -> {
            Class beanClass = beanDefinition.getBeanClass();
            Object object = getBean(beanDefinition.getBeanName());
            // get fields
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                // handle @Value
                handleAutowiredAnnotation(beanClass, object, declaredField);
            }
            miniIoC.put(beanDefinition.getBeanName(), object);
        });
    }


    private void handleAutowiredAnnotation(Class beanClass, Object object, Field declaredField) {
        Autowired autowired = declaredField.getAnnotation(Autowired.class);

        if (autowired != null) {
            Qualifier qualifier = declaredField.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                // handle @Qualifier

            } else {
                // get bean according to type name
                String declaredFieldBeanName = StringUtils.uncapitalize(declaredField.getType().getSimpleName());
                Object autowiredBean = getBean(declaredFieldBeanName);

                // set autowired bean to object according to field name
                try {
                    String methodName = "set" + StringUtils.capitalize(declaredField.getName());
                    Method setBeanMethod = beanClass.getMethod(methodName, declaredField.getType());
                    setBeanMethod.invoke(object, autowiredBean);

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    logger.warning("Set bean method warning: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String declaredFieldBeanName) {
        return miniIoC.get(declaredFieldBeanName);
    }

}
