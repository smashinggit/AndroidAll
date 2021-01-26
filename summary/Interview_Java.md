[toc]
# Java 相关面试题


# 注解是什么？有哪些元注解
注解，在我看来它是一种信息描述，不影响代码执行，但是可以用来配置一些代码或者功能。

常见的注解比如@Override,代表重写方法，看看它是怎么生成的：
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {
}
```
可以看到Override被@interface所修饰，代表注解，
同时上方还有两个注解@Target和@Retention，这种修饰注解的注解叫做元注解，很好理解吧，就是最基本的注解呗。

java中一共有四个元注解：
1. @Target：表示注解对象的作用范围。
2. @Retention：表示注解保留的生命周期
3. @Inherited：表示注解类型能被类自动继承。
4. @Documented：表示含有该注解类型的元素(带有注释的)会通过javadoc或类似工具进行文档化。

注解可以用来做什么
1. 降低项目的耦合度。
2. 自动完成一些规律性的代码。
3. 自动生成java代码，减轻开发者的工作量。


