package com.cs.test.java;

import androidx.annotation.IntDef;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Sex {
    String[] value() default "unknown";
}

class Person {

    @Sex
    public void empty() {
        System.out.println("empty");
    }

    @Sex(value = {"girl", "boy"})
    public void somebody(String name, int age) {
        System.out.println("somebody: " + name + ", " + age);
    }
}

/**
 * 注解在反射中的应用
 */
class AnnotationTest {

    public static void main(String[] args) {
        Person person = new Person();
        Class<Person> personClass = Person.class;

        try {
            Method method1 = personClass.getMethod("empty");
            method1.invoke(person);

            Method method2 = personClass.getMethod("somebody", String.class, int.class);
            method2.invoke(person, "张三", 15);

            iteratorMethodAnnotations(method1);
            iteratorMethodAnnotations(method2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void iteratorMethodAnnotations(Method method) {

        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println("Annotation : " + annotation);
        }

        if (method.isAnnotationPresent(Sex.class)) {  //判断是否有Sex注解
            Sex sexAnnotation = method.getAnnotation(Sex.class);

            for (String value : sexAnnotation.value()) {
                System.out.println("value " + value);
            }
            System.out.println(" ");
        }
    }

}


//*********************************************************************************

class TypedefTest {

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_LIST = 1;
    public static final int NAVIGATION_MODE_TABS = 2;

    @Retention(SOURCE)
    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_LIST, NAVIGATION_MODE_TABS})
    public @interface NavigationMode {
    }

    private static void setNavigationMode(@NavigationMode int mode) {
        System.out.println("mode " + mode);
    }

    public static void main(String[] args) {
        setNavigationMode(NAVIGATION_MODE_STANDARD);
    }
}








