[toc]
# 反射


# 概述
JAVA 反射机制是在**运行状态**中，
**对于任意一个类，都能够知道这个类的所有属性和方法**
**对于任意一个对象，都能够调用它的任意一个方法和属性**
这种动态获取的信息以及动态调用对象的方法的功能称为 Java 语言的反射机制

这里需要说到 Java 有两种对象：
一个是 Class 对象，也就是类对象，还有一个是实例对象
实例对象就是通过 Class 对象创建出来的。而**反射就是针对 Class 对象进行的处理**


# 获取 Class 对象
Class objects are constructed automatically by the Java Virtual Machine as classes are loaded
 
java 代码运行时，需要首先将 java 代码编译成二进制字节码，保存在.class文件中，
然后 JVM 将.class文件载入内存，并且创建一个对应的 Class 对象

获取 Class 对象有以下三种方式：
- 通过类名获取Class 

  Class userClass = User.class;
 
- 通过实例对象获取Class

  User user = new User("张三", 18);
  Class userClass = user.getClass();

  
- 通过完成类名路径获取Class

  try {
      Class<?> userClass3 = Class.forName("com.cs.test.User");
  } catch (Exception e) {
      e.printStackTrace();
  }


# 获取属性、字段、及相关调用
```

              Class<?> userClass = Class.forName("com.cs.test.User");
  
              //通过Class对象创建实例对象
              User user = ((User) userClass.getConstructor(String.class, int.class).newInstance("李四", 16));
              //或者
              Object userObject = userClass.getConstructor().newInstance();
  
              System.out.println("通过Class对象创建实例对象 " + user.toString());
  
              //获取所有方法
              Method[] methods = userClass.getMethods(); //获取所有共有方法
              Method[] declareMethods = userClass.getDeclaredMethods(); //获取公共、私有、受保护、默认方法
              for (Method method : methods) {
                  System.out.println("getMethods " + method.getName());
              }
              for (Method method : declareMethods) {
                  System.out.println("getDeclaredMethods " + method.getName());
              }

              // 获取指定方法并调用
              Method method1 = userClass.getMethod("setName", String.class);
              method1.invoke(userObject, "王五");
  
              Method method2 = userClass.getDeclaredMethod("secret");
              method2.setAccessible(true);
              method2.invoke(userObject);
  
              System.out.println("获取指定方法setName并调用 " + method1.invoke(userObject, "王五"));
              System.out.println("获取指定方法secret并调用 " + method2.invoke(userObject));
  
  
              //获取所有属性
              Field[] fields = userClass.getFields();
              Field[] declaredFields = userClass.getDeclaredFields();
              for (Field field : fields) {
                  System.out.println("getFields " + field.getName());
              }
              for (Field field : declaredFields) {
                  System.out.println("getDeclaredFields " + field.getName());
              }
  
              //获取共有字段并赋值
              Field field = userClass.getField("sex");
              field.set(userObject, "男");
  
              //获取私有字段并赋值
              Field field2 = userClass.getDeclaredField("name");
              field2.setAccessible(true);
              field2.set(userObject, "王");
  
```

# 反射与 Annotation

通过反射获取的 Method 有两个关于 Annotation 的属性：
- isAnnotationPresent：判断Annotation是不是某个类型；
- getAnnotation：  获取Annotation的值

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


