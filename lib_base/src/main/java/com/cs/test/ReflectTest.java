package com.cs.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ReflectTest {

    public static void main(String[] args) {
        reflect();
    }

    private static void reflect() {
//        Class<User> userClass = User.class;
//
//        User user = new User("张三", 18);
//        Class userClass2 = user.getClass();

        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


class User {
    public String sex;
    private String name;
    private int age;


    public User() {
        this.name = "";
        this.age = 0;
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private void secret() {
        System.out.println(name + "的秘密");
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}



