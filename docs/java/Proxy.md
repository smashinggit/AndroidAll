[toc]
# JAVA代理模式

[https://blog.csdn.net/justloveyou_/article/details/74203025](https://blog.csdn.net/justloveyou_/article/details/74203025)

## 代理模式定义
为其他对象提供一种代理以控制对象对这个对象的访问。

代理模式其实很常见，比如买火车票这件小事：
黄牛相当于是我们本人的的代理，我们可以通过黄牛买票。通过黄牛买票，我们可以避免与火车站的直接交互，可以省很多事，
并且还能享受到黄牛更好的服务(如果钱给够的话)。
在软件开发中，代理也具有类似的作用，并且一般可以分为静态代理和动态代理两种，上述的这个黄牛买票的例子就是静态代理


## 静态代理 与 动态代理
代理根据**代理类的产生方式和时机**分为静态代理和动态代理两种

静态代理与动态代理的区别是什么呢？
所谓静态代理，其实质是**自己手写(或者用工具生成)代理类**，也就是在程序运行前就已经存在的编译好的代理类。

但是，如果我们需要很多的代理，每一个都这么去创建实属浪费时间，而且会有大量的重复代码，此时我们就可以采用动态代理，
动态代理可以**在程序运行期间根据需要动态的创建代理类及其实例**来完成具体的功能。

总的来说，根据代理类的创建时机和创建方式的不同，我们可以将代理分为静态代理和动态代理两种形式。

## 代理模式的使用场景
当无法或不想直接访问某个对象或访问某个对象存在困难时可以通过一个代理对象来间接访问，
为了保证客户端使用的透明性，**委托对象与代理对象需要实现相同的接口**

就像我们也可以自己直接去买票一样，在软件开发中，方法直接调用就可以完成功能，为什么非要通过代理呢？
原因是采用代理模式可以有效的将**具体的实现(买票过程)与调用方(买票者)进行解耦**，通过面向接口进行编码完全将
具体的实现(买票过程)隐藏在内部(黄牛)。

此外，代理类不仅仅是一个隔离客户端和目标类的中介，我们还可以借助代理来在增加一些功能，而不需要修改原有代码，
是开闭原则的典型应用。代理类主要负责为目标类预处理消息、过滤消息、把消息转发给目标类，以及事后处理消息等

代理类与目标类之间通常会存在关联关系，一个代理类的对象与一个目标类的对象关联，
**代理类的对象本身并不真正实现服务，而是通过调用目标类的对象的相关方法**，来提供特定的服务

总结：
- 代理对象存在的价值主要用于**拦截对真实业务对象的访问**(我们不需要直接与火车站打交道，而是把这个任务委托给黄牛)
- 代理对象应该具有和目标对象(真实业务对象)相同的方法，即实现共同的接口或继承于同一个类；
- 代理对象应该是目标对象的增强，否则我们就没有必要使用代理了


事实上，真正的业务功能还是由目标类来实现，代理类只是用于扩展、增强目标类的行为。
例如，在项目开发中我们没有加入缓冲、日志这些功能而后期需要加入，我们就可以使用代理来实现，而没有必要去直接修
改已经封装好的目标类

## 代理模式
[代理.png]

代理模式包含如下几个角色：
- 客户端：客户端面向接口编程，使用代理角色完成某项功能。
- 抽象主题：一般实现为接口，是对(被代理对象的)行为的抽象。
- 被代理角色(目标类)：直接实现上述接口，是抽象主题的具体实现。
- 代理角色(代理类)：实现上述接口，是对被代理角色的增强。

代理模式的使用本质上是对开闭原则(Open for Extension, Close for Modification)的直接支持。
  

## 静态代理

静态代理的实现模式一般是：首先创建一个接口（JDK代理都是面向接口的），然后创建具体实现类来实现这个接口，
然后再创建一个代理类同样实现这个接口，
不同之处在于，具体实现类的方法中需要将接口中定义的方法的业务逻辑功能实现，
而代理类中的方法只要调用具体类中的对应方法即可，
这样我们在需要使用接口中的某个方法的功能时直接调用代理类的方法即可，将具体的实现类隐藏在底层


```
// 抽象主题或接口类
public abstract class Subject {
    /**
     * 一个普通的业务方法
     */
    public abstract void visit();
}

// 实现抽象主题的真实主题类
public class RealSubject extends Subject {
    @Override
    public void visit() {
        //RealSubject中的visit的具体逻辑
        System.out.print("Real subject");
    }
}   

// 代理类
public class ProxySubject extends Subject {
    //持有真正主题的引用
    private RealSubject mSubject;

    //通过构造传入真正主主题的引用
    public ProxySubject(RealSubject mSubject) {
        this.mSubject = mSubject;
    }

    @Override
    public void visit() {
        //通过真实主题的引用的对象调用真实主题中的逻辑方法
        mSubject.visit();
    }
}

// 客户类
 public class Client {
     public static void main(String[] args){
         //构造一个真实的主题对象
         RealSubject realSubject=new RealSubject();
         //通过真实的主题对象构造一个代理对象
         ProxySubject proxySubject=new ProxySubject(realSubject);
         //调用代理相关方法
         proxySubject.visit();
     }
 }  

```

- Subject:抽象主题类
该类的主要职责是声明真实主题的共同接口方法，该类可以 是一个抽象类也可以 是一个接口。

- RealSubject:真实主题类
该类也称为被委托类或被代理类，该类定义了代理所表示的真实对象，由其执行具体的业务逻辑方法。

- ProxySubject:代理类。
该类也称为委托类或代理类，该类持有一个对真实主题类的引用 ，在其所实现的接口方法 中调用真实主题类中相应 的接口方法

再来一个例子：
我们平常去电影院看电影的时候，在电影开始的阶段是不是经常会放广告呢？
电影是电影公司委托给影院进行播放的，但是影院可以在播放电影的时候，产生一些自己的经济收益，比如卖爆米花、可乐等，
然后在影片开始结束时播放一些广告，现在用代码来进行模拟

```
interface Movie {
    void play();
}

class RealMovie implements Movie {

    @Override
    public void play() {
        System.out.println("正在播放电影 肖申克的救赎");
    }
}

//Cinema 就是代理对象，它有一个 play() 方法。不过调用 play() 方法时，它进行了一些相关利益的处理，
//那就是广告。也就是说，Cinema(代理类) 与 RealMovie(目标类) 都可以播放电影，
//但是除此之外，Cinema(代理类)还对“播放电影”这个行为进行进一步增强，即增加了额外的处理，同时不影响RealMovie(目标类)的实现。
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

   public static void main(String[] args) {

        RealMovie realMovie = new RealMovie();
        Movie movie = new Cinema(realMovie);
        movie.play();

    }

}

```
现在可以看到，代理模式可以在不修改被代理对象的基础上(符合开闭原则)，通过扩展代理类，进行一些功能的附加与增强。
值得注意的是，**代理类和被代理类应该共同实现一个接口，或者是共同继承某个类**。

如前所述，由于Cinema(代理类)是**事先编写、编译好的，而不是在程序运行过程中动态生成的**，
因此这个例子是一个静态代理的应用


## 动态代理

代理模式大致分为两部分，一是静态代理 ，二是动态代理，
静态代理如上述示例那样，代理者的代码由程序员自己或通过一些自动化工具生成固定的代码再对其进行编译，
也就是说在我们的代码运行前代理类的class编译文件就已经存在，

动态代理可以在程序运行期间根据需要动态的创建代理类及其实例来完成具体的功能

动态代理则与静态代理相反，**通过反射机制动态地生成代理对象**，也就是说我们在code阶段压根就不需要知道代理谁，
代理谁我们将会在执行阶段决定，而Java也给我们提供了一个便捷的动态代理接口 InvocationHandler,实现该接口需要重
写其调用方法invoke()



```

// 抽象主题类
//首先得有一个接口，通用的接口是代理模式实现的基础。  
interface ISubmit {
    void submit(int number);
}

// 被代理角色(目标类)
class RealSubmit implements ISubmit {
    @Override
    public void submit(int number) {
        System.out.println("ReadSubmit submit");
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


// 客户端类
public class Client {
    public static void main(String[] args){

        ISubmit realSubmit = new RealSubmit();    //构造一个真实的主题对象

        //在实现了InvocationHandler接口后，我们就可以创建代理对象了。在Java的动态代理机制中，
        //我们使用Proxy类的静态方法newProxyInstance创建代理对象
        // 生成real的代理对象
        ISubmit proxy = (ISubmit) Proxy.newProxyInstance(realSubmit.getClass().getClassLoader(),
                new Class[]{ISubmit.class},
                new ProxyInvocationHandler(realSubmit));

        proxy.submit(11);
        System.out.println("代理对象的类型 ： " + proxy.getClass().getName());
        System.out.println("代理对象所在类的父类型 ： " + proxy.getClass().getGenericSuperclass());
    }
}
```
在动态代理中，代理类及其实例是程序自动生成的，因此我们不需要手动去创建代理类。
在Java的动态代理机制中，InvocationHandler(Interface) 接口和 Proxy(Class) 类是实现我们动态代理所必须用到的
事实上，Proxy 通过使用 InvocationHandler 对象生成具体的代理对象

## InvocationHandler
每个代理的实例都有一个与之关联的 InvocationHandler 实现类，
如果代理的方法被调用，那么代理便会通知和转发给内部的 InvocationHandler 实现类，由它决定处理

InvocationHandler中的invoke() 方法决定了怎么样处理代理传递过来的方法调用。

## Proxy

JDK通过 Proxy 的静态方法 newProxyInstance 动态地创建代理，该方法在Java中的声明如下：
```
  /**     
     * @param loader 类加载器
     * @param interfaces 目标类所实现的接口
     * @param h  InvocationHandler 实例
     * @return     
     */
    public static Object newProxyInstance(ClassLoader loader,
            Class<?>[] interfaces,
            InvocationHandler h)
```


事实上，Proxy 动态产生的代理对象调用目标方法时，代理对象会调用 InvocationHandler 实现类，
所以 InvocationHandler 是实际执行者


