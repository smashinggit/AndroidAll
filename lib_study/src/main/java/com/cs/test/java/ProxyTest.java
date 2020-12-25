package com.cs.test.java;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * java 代理模式
 */
class ProxyTest {


    public static void main(String[] args) {
//        staticProxy(); //静态代理
//        staticProxy2(); //静态代理
        dynamicProxy(); //静态代理
    }

    /**
     * 我们平常去电影院看电影的时候，在电影开始的阶段是不是经常会放广告呢？
     * 电影是电影公司委托给影院进行播放的，但是影院可以在播放电影的时候，产生一些自己的经济收益，比如卖爆米花、可乐等，
     * 然后在影片开始结束时播放一些广告，现在用代码来进行模拟
     */
    private static void staticProxy2() {

        RealMovie realMovie = new RealMovie();
        Movie movie = new Cinema(realMovie);
        movie.play();
    }


    private static void dynamicProxy() {
        ISubmit realSubmit = new RealSubmit();    //构造一个真实的主题对象

        // 生成real的代理对象
        ISubmit proxy = (ISubmit) Proxy.newProxyInstance(realSubmit.getClass().getClassLoader(),
                new Class[]{ISubmit.class},
                new ProxyInvocationHandler(realSubmit));

        proxy.submit(11);
        System.out.println("代理对象的类型 ： " + proxy.getClass().getName());
        System.out.println("代理对象所在类的父类型 ： " + proxy.getClass().getGenericSuperclass());
    }

    private static void staticProxy() {

        RealSubject realSubject = new RealSubject();
        ProxySubject proxySubject = new ProxySubject(realSubject);

        proxySubject.invoke();
    }

}

//************************* dynamicProxy ***************************************
interface ISubmit {
    void submit(int number);
}

class RealSubmit implements ISubmit {

    @Override
    public void submit(int number) {
        System.out.println("ReadSubmit submit " + number);
    }
}


//每个代理的实例都有一个与之关联的 InvocationHandler 实现类，
//如果代理的方法被调用，那么代理便会通知和转发给内部的 InvocationHandler 实现类
class ProxyInvocationHandler implements InvocationHandler {
    private Object object; //被代理类的引用

    public ProxyInvocationHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在转调具体目标对象之前，可以执行一些功能处理
        System.out.println("前置增强处理： yoyoyo...");

        // 转调具体目标对象的方法(三要素：实例对象 + 实例方法 + 实例方法的参数)
        Object result = method.invoke(object, args);

        // 在转调具体目标对象之后，可以执行一些功能处理
        System.out.println("后置增强处理：hahaha...");

        return result;
    }
}


//***********************************************************

interface ISubject {
    void invoke();
}

class RealSubject implements ISubject {
    @Override
    public void invoke() {
        System.out.println("RealSubject invoke");
    }
}

class ProxySubject implements ISubject {
    private RealSubject realSubject;

    public ProxySubject(RealSubject realSubject) {
        this.realSubject = realSubject;
    }

    @Override
    public void invoke() {
        realSubject.invoke();
    }
}


//***********************************************************
interface Movie {
    void play();
}

class RealMovie implements Movie {

    @Override
    public void play() {
        System.out.println("正在播放电影 肖申克的救赎");
    }
}


class Cinema implements Movie {
    RealMovie realMovie;

    public Cinema(RealMovie realMovie) {
        this.realMovie = realMovie;
    }

    @Override
    public void play() {
        guanggao(true);    // 代理类的增强处理
        realMovie.play();         // 代理类把具体业务委托给目标类，并没有直接实现
        guanggao(false);  // 代理类的增强处理
    }

    private void guanggao(boolean isStart) {
        if (isStart) {
            System.out.println("电影马上开始了，爆米花、可乐、口香糖9.8折，快来买啊！");
        } else {
            System.out.println("电影马上结束了，爆米花、可乐、口香糖9.8折，买回家吃吧！");
        }
    }

}
