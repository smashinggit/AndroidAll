package com.cs.test.pattern;

/**
 * @author ChenSen
 * @date 2021/1/26
 * @desc 单例模式
 **/

// 懒汉式(需要用的时候才创建)
class Singleton1 {

    //1.构造方法私有化，外部不能new
    private Singleton1() {
    }

    //2.本类内部创建对象实例
    private static Singleton1 singleton1 = null;

    //3.提供一个公有的静态方法，返回实例对象
    public static Singleton1 getInstance() {

        if (singleton1 == null) {
            singleton1 = new Singleton1();
        }

        return singleton1;
    }
}

//如何实现线程安全的懒汉式（双重检查加锁）：
// 懒汉式2(需要用的时候才创建)
class Singleton2 {

    //1.构造方法私有化，外部不能new
    private Singleton2() {
    }

    //2.本类内部创建对象实例
    private volatile static Singleton2 instance = null;

    //3.提供一个公有的静态方法，返回实例对象
    public static Singleton2 getInstance() {

        if (instance == null) {
            synchronized (Singleton2.class) {
                if (instance == null) {
                    instance = new Singleton2();
                }
            }
        }
        return instance;
    }
}


// 饿汉式(直接初始化)
class Singleton3 {

    private void Singleton3() {
    }

    private static Singleton3 instance = new Singleton3();

    public Singleton3 getInstance() {
        return instance;
    }
}


