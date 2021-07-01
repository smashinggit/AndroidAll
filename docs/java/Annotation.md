[toc]
# Annotation

# 概述
Java 语言中的类、方法、变量、参数和包等都可以被标注。
Java 标注可以通过**反射**获取标注内容。在编译器生成类文件时，标注可以被嵌入到字节码中。Java 虚拟机可以
保留标注内容，在运行时可以获取到标注内容


# 



# Java 内置注解
- @Override - 检查该方法是否是重写方法。如果发现其父类，或者是引用的接口中并没有该方法时，会报编译错误。
- @Deprecated - 标记过时方法。如果使用该方法，会报编译警告。
- @SuppressWarnings - 指示编译器去忽略注解中声明的警告
- @Retention - 标识这个注解怎么保存，是只在源码中（编译期），还是编入class文件类加载）中，或者是在运行时（JVM中运行)可以通过反射访问。
- @Documented - 标记这些注解是否包含在用户文档中。
- @Target - 标记这个注解应该是哪种 Java 成员。
- @Inherited - 标记这个注解是继承于哪个注解类(默认 注解并没有继承于任何子类)
- @SafeVarargs - Java 7 开始支持，忽略任何使用参数为泛型变量的方法或构造函数调用产生的警告。
- @FunctionalInterface - Java 8 开始支持，标识一个匿名函数或函数式接口。
- @Repeatable - Java 8 开始支持，标识某注解可以在同一个声明上使用多次。

# Annotation 架构
![注解](/pics/java/annotation1.jpg)

java Annotation 的组成中，有 3 个非常重要的主干类。它们分别是：
- Annotation 
Annotation 就是个接口。
每 1 个 Annotation 对象，都会有唯一的 RetentionPolicy 属性；
                        至于 ElementType 属性，则有 1~n 个

- ElementType 
ElementType 是 Enum 枚举类型，它用来指定 Annotation 的类型
例如，若一个 Annotation 对象是 METHOD 类型，则该 Annotation 只能用来修饰方法

- RetentionPolicy 

  若 Annotation 的类型为 SOURCE，Annotation 仅存在于编译器处理期间，编译器处理完之后，该 Annotation 就没用了
  
  若 Annotation 的类型为 CLASS，编译器将 Annotation 存储于类对应的 .class 文件中，它是 Annotation 的默认行为
  
  若 Annotation 的类型为 RUNTIME，编译器将 Annotation 存储于 .class 文件中，并且可由JVM读入
  
  **每 1 个 Annotation" 都与 "1 个 RetentionPolicy" 关联，并且与 "1～n 个 ElementType" 关联**


# Annotation 实现类的语法定义

```
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationTest {
    
}

```

定义了 AnnotationTest 之后，我们可以在代码中通过 "@AnnotationTest" 来使用它。
其它的，@Documented, @Target, @Retention, @interface 都是来修饰 AnnotationTest 的

- @interface
使用 @interface 定义注解时，意味着它实现了 java.lang.annotation.Annotation 接口，即该注解就是一个Annotation。
定义 Annotation 时，@interface 是必须的
Annotation 接口的实现细节都由编译器完成

- @Documented
类和方法的 Annotation 在缺省情况下是不出现在 javadoc 中的。
如果使用 @Documented 修饰该 Annotation，则表示它可以出现在 javadoc 中
定义 Annotation 时，@Documented 可有可无

- @Target(ElementType.TYPE)
ElementType 是 Annotation 的类型属性。而 @Target 的作用，就是来指定 Annotation 的类型属性
定义 Annotation 时，@Target 可有可无
若有，则该 Annotation 只能用于它所指定的地方；
若没有，则该 Annotation 可以用于任何地方

- @Retention(RetentionPolicy.RUNTIME)
RetentionPolicy 是 Annotation 的策略属性，而 @Retention 的作用，就是指定 Annotation 的策略属性
定义 Annotation 时，@Retention 可有可无。若没有 @Retention，则默认是 RetentionPolicy.CLASS

- @Inherited 
它所标注的Annotation将具有继承性

# Annotation 的作用
- 编译检查
Annotation 具有"让编译器进行编译检查的作用"。

例如，@SuppressWarnings, @Deprecated 和 @Override 都具有编译检查作用

- 根据 Annotation 生成帮助文档
通过给 Annotation 注解加上 @Documented 标签，能使该 Annotation 标签出现在 javadoc 中

- 在反射中使用 Annotation
在反射的 Class, Method, Field 等函数中，有许多于 Annotation 相关的接口

```

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
```









 
 


