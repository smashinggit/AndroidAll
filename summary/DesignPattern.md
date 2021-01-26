[toc]

# 设计模式
[设计模式](https://blog.csdn.net/ddnosh/article/details/88917700)



# 计模式的六大原则

1. 单一职责原则(Single Responsibility Principle)
对象：类

here should never be more than one reason for a class to change.
改变一个类的原因不能超过一个


2. 里氏替换原则(Liskov Substitution Principle)
对象：子类和父类


子类可以对父类进行扩展，但是不能改变父类现有的功能。
具体就是：
1、子类继承父类后，可以扩展父类功能；
2、子类可以实现父类抽象方法，但是不能覆盖父类已实现的方法；




3. 依赖倒置原则(Dependence Inversion Principle)
对象：模块之间

底层模块：原子逻辑，不可分割的业务逻辑；
高层模块：原子逻辑的封装；
abstractons：接口或抽象类，不能被直接实例化；
details：接口或抽象类的实例化；
说白了就是“面向接口编程”。


4. 接口隔离原则(Interface Segregation Principle)
对象：类和接口

意思就是类实现的接口应该是这个类需要的。
换句话说，我们定义接口，应当做到尽量根据业务逻辑细化。


5. 迪米特法则(Law of Demeter)
对象：类和类

这个法则也好理解，就是说一个类尽可能少的暴露方法，能用private的就用private，尽可能少的让外界调用到你的方法。
一句话：高内聚，低耦合体现。
private方法主要实现类本身的逻辑功能;
protected方法主要暴露给子类用;
public方法主要是给其它类提供的接口;

6. 开闭原则(Open Closed Principle)
对象：类、模块、函数
一句话：对扩展开放，对修改关闭。



#  单例模式

```
/如何实现线程安全的懒汉式（双重检查加锁）：
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
```

# 工厂模式

所谓的工厂，通俗来讲就是用来生产产品的地方。
从代码角度来说，产品就是一个个具体的类的实例对象，工厂也是一个实例对象，用来生产这些实例产品。
工厂模式要解决的问题就是如何实例化对象

## 简单工厂

优点
工厂类承担创建所有产品的职责，只要有想创建的产品，都可以放到工厂类里面实现，简称“万能类”

缺点
显而易见，只要新增一个产品，就会对工厂类进行修改；
而且工厂类会随着产品种类的增多而变得庞大而且不易于管理维护；
违反了设计模式中的“开闭原则”，即对修改关闭（新增了产品需要修改工厂类，违反），对扩展开放（没有扩展）
```

interface Car {
    fun move()
}

class Benz : Car {
    override fun move() {
        print("Benz move")
    }
}

class BMW : Car {
    override fun move() {
        print("BMW move")
    }
}

// 简单工厂
object SimpleFactory {

    fun getCar(type: Int): Car? {
        return when (type) {
            0 -> {
                Benz()
            }
            1 -> {
                BMW()
            }
            else -> null
        }
    }
}

```

## 工厂方法

优点
**弱化一个工厂类通吃的概念，将生产产品的职责交给各自的产品工厂去完成**，也就是每一个产品都有一个工厂类，
负责完成本身产品的生产。
符合“开闭原则”，对修改关闭（无需修改工厂类），对拓展开放（新增产品对应的工厂类）。

缺点
相比简单工厂一个工厂类来说，工厂方法实现了多个工厂类，相对来说使用起来复杂一点。
缺少形成产品族的功能，这个后续可在抽象工厂模式中解决。

```
//工厂方法模式

interface Factory {
    fun getCar(): Car
}


class BenzFactory : Factory {
    override fun getCar(): Car {
        return Benz()
    }
}

class BMWFactory : Factory {
    override fun getCar(): Car {
        return BMW()
    }
}

// 使用
val benzfactory = BenzFactory()
val car1 = benzfactory.getCar()
//car1.move()

val bmwFactory = BMWFactory()
val car2 = bmwFactory.getCar()
//car2.move()

```


## 抽象工厂

优点
**抽象工厂是针对“产品族”概念拓展而来的**。
一个产品不止一个功能，比如我们为高端人群定制了一套出行方案，这个方案里面有配备的车辆，还有人群穿戴的衣服等，
这些功能合在一起就成为了“人群”这个产品的功能。
如果只是配备车辆，那就跟工厂方法模式一样，只有一个功能，这是极端情况。
**所谓的抽象指的是工厂不止生产某一具体产品，而是能扩展到生产一系列产品**

```

//抽象工厂
interface Clothes {
    fun wear()
}

class Gucci : Clothes {
    override fun wear() {
        println("wear Gucci")
    }
}

class Prada : Clothes {
    override fun wear() {
        println("wear Prada ")
    }
}


interface AbstractFactory {
    fun getCar(): Car
    fun getClothes(): Clothes
}


class Zhangsan : AbstractFactory {
    override fun getCar(): Car {
        return Benz()
    }

    override fun getClothes(): Clothes {
        return Gucci()
    }
}

class LiSi : AbstractFactory {
    override fun getCar(): Car {
        return BMW()
    }

    override fun getClothes(): Clothes {
      return Prada()
    }
}

```


# 建造者模式（Builder模式）

Builder模式主要用于解决初始化类时（也就是new一个类的实例出来），**类的构造函数种类过多，且不易管理的问题**

假如一个类有很多个属性，即有很多不同的构造方法
如果这些构造函数都写到类里面，不仅代码量大，代码不美观，而且调用起来容易搞错。

解决方案
我们可以设计一个内部类，这个内部类的参数跟Student类参数一样，而Student类的构造函数的参数，
我们就设定为这个内部类。所以我们只需要将这个内部类的变量初始化完即可。
内部类变量设定的时候，我们采用链式结构，这样可以通过setxx().setxx.()这样的形式一直写下去

```
class Student(val name: String, var age: String, var sex: String) {
    private var mName = ""
    private var mAge = ""
    private var mSex = ""

    init {
        this.mName = name
        this.mAge = age
        this.mSex = sex
    }


    class Builder {
        private var mName = ""
        private var mAge = ""
        private var mSex = ""

        fun setName(name: String): Builder {
            this.mName = name
            return this
        }

        fun setAge(age: String): Builder {
            this.mAge = age
            return this
        }

        fun setSex(sex: String): Builder {
            this.mSex = sex
            return this
        }

        fun build(): Student {
            return Student(this.mName, this.mAge, this.mSex)
        }
    }

}

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val zhangsan = Student.Builder()
            .setName("张三")
            .setAge("18")
            .setSex("男")
            .build()
    }
}

```

# 代理模式

代理，是一个中间者的角色，它屏蔽了访问方和委托方之间的直接接触。
也就是说访问方不直接调用委托方的这个对象，而是通过实例化一个跟委托方有同样接口的代理方，
通过这个代理方来完成对委托方的调用。
访问方只和代理方打交道，这个代理方有点像掮客的角色。现实生活中代理好比房产中介

什么时候需要用到代理模式呢？
1. 访问方不想和委托方直接接触，或者直接接触有困难。
2. 访问方对委托方的访问需要增加额外处理，比如访问前和访问后都做一些处理。
这种情况下我们不能直接对委托方的方法进行修改，这样同样违反了开闭原则。


## 静态代理
静态代理主要是代理类需要每次都要手工创建

```
class BenzProxy : Car {
    private var benz = Benz()

    override fun move() {
        //做一些前置工作，比如检查车辆的状况
        //before()
        benz.move()
        //做一些后置工作，比如检查结果
        //after()
    }
}
```


## 动态代理

动态代理的代理类可以根据委托类自动生成，而不需要像静态代理那样通过手工创建。
代理类的代码不是在java代码中定义的，而是在运行的时候动态生成的。

```
//动态代理

class CarHandler(private val car: Car) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {

        //做一些前置工作，比如检查车辆的状况
        before()

        val result = method.invoke(car, args)

        //做一些后置工作，比如检查结果
        //after()

        return result
    }
    private fun before() {
    }
}

object ProxyTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val car1 = Benz()

        val carProxy = Proxy.newProxyInstance(
            Car::class.java.classLoader,
            arrayOf(Car::class.java),
            CarHandler(car1)
        ) as Car

        carProxy.move()
    }
}
```


# 策略模式
策略模式本质是：分离算法，选择实现

策略模式就是定义一系列算法,将每个算法封装到具有公共接口的一系列策略类中，从而使它们可以相互替换让算法可在不
影响客户端的情况下发生变化,提高了系统的灵活性.

比如,我们日常的网上购物有打折的、有满减的、有返利的等等，其实不管商场如何进行促销，说到底都是一些算法，
这些算法本身只是一种策略，并且这些算法是随时都可能互相替换的，
比如针对同一件商品，今天打八折、明天满100减20，这些策略间是可以互换的


- Context
上下文角色，也叫Context封装角色，起承上启下的作用，屏蔽高层模块对策略、算法的直接访问，封装可能存在的变化。
  
- 策略角色
抽象策略角色，是对策略、算法家族的抽象，通常为接口，定义每个策略或算法必须具有的方法和属性

- 具体策略角色

```
//Context上下文角色
class Context(private val mStrategy: Strategy) {

    fun work() {
        mStrategy.algorithmInterface()
    }

}

//抽象策略角色
interface Strategy {
    fun algorithmInterface()
}

// 具体策略角色
class StrategyA : Strategy {
    override fun algorithmInterface() {
        println("执行策略A")
    }
}

// 具体策略角色
class StrategyB : Strategy {
    override fun algorithmInterface() {
        println("执行策略B")
    }
}


object StrategyTest {
    @JvmStatic
    fun main(args: Array<String>) {

        val strategyA = StrategyA()
        val strategyB = StrategyB()

        var context = Context(strategyA)
        context.work()  //使用策略A

        context = Context(strategyB)
        context.work()  //使用策略B
    }
}
```







# 观察者模式
观察者模式，包括观察者和被观察者。
观察者们将自己的需求告知被观察者，被观察者负责通知到观察者。

```
class Observable<T>(private var mValue: T? = null) {

    private val mObservers = arrayListOf<Observer<T>>()

    fun addObserver(observer: Observer<T>) {
        mObservers.add(observer)
    }

    fun removeObserver(observer: Observer<T>) {
        mObservers.remove(observer)
    }

    fun setValue(newValue: T) {
        this.mValue = newValue
        notify(newValue)
    }


    fun notify(value: T) {
        mObservers.forEach {
            it.update(value)
        }
    }

}

interface Observer<T> {
    fun update(value: T)
}

object ObserverTest {
    @JvmStatic
    fun main(args: Array<String>) {

        val observable = Observable<Int>(0)

        observable.addObserver(object : Observer<Int> {
            override fun update(value: Int) {
                println("update $value")
            }
        })

    }
}
```


# 适配器模式

适配器设计模式，也叫Adapter或者Wrapper设计模式，根据字面意思来理解，就是为了达到适配的目的而设计的开发模式。

Target	目标角色	是一个接口，也就是我们期待要转化成的接口
Adaptee	源角色	原始的类或接口对象
Adapter	适配器角色	把源角色转化成目标角色的类


## 类适配器
**适配器「Adapter」继承源类「Src」并且实现目标「Dst」接口,来实现 Src-->Dst 的转换**

MAC 电脑要连接投影仪器，需要一个 MINI DP 转 VGA & HDMI 适配器，然后才能连接上投影仪
所以这里目标是 VGAORHDMI ，源是 MINI DP 适配器就是上面的那根线

```
interface VgaOrHdmi {
    fun getVgaOrHdmi(): String //输出 VGA 或是 Hdmi 接口
}

open class MiniDp {

    fun outPutMinkDp(): String {
        return "我是 mac 上的 MiniDp 输入接口"
    }
}

class MidiDp2VgaOrHdmiAdapter : MiniDp(), VgaOrHdmi {

    override fun getVgaOrHdmi(): String {
        return convertMiniDp2VgaOrHdmi()
    }

    // 把 MINIDP 转化成 VAG 或 HDMI 方法
    private fun convertMiniDp2VgaOrHdmi(): String {
        var source = outPutMinkDp()    //拿到原输出
        val result = "输出变成  VGA 和 HDMI 接口"  //更改为目标输出
        return result
    }
}


```




## 对象适配器模式

**适配器「Adapter」持有源类「Src」的引用,并实现目标「Dst」接口,来实现 Src--> Dst 的转化**

```
//对象适配器
class MidiDp2VgaOrHdmiAdapter2(private val mMinuDp: MiniDp) : VgaOrHdmi {

    override fun getVgaOrHdmi(): String {
        var source = mMinuDp.outPutMinkDp()  //源数据
        val result = "输出变成  VGA 和 HDMI 接口"  //更改为目标输出
        return result
    }
}
```


# 模板设计

模板设计模式（Template Pattern）是一种基于代码复用的设计模式。
形象的描述就是架构师和开发人员之间的合作。架构师构造好实现的流程和轮廓，开发人员则完成具体的实现过程。

父类抽象模板：
- 定义abstract限定符方法交由子类实现；
- 定义非private方法，延迟至子类实现，此方法也可以完成一些通用操作；

子类实现模板：
- 实现父类abstract方法；
- 可以重写父类非private方法。

```

abstract class AbstractCar {

    fun startUp() {
        println("启动")
    }

    abstract fun move() ////强制要求实现

    fun stop() {
        println("熄火")
    }

    final fun operation() {//定义成final, 防止被重写
        //第一步：启动
        startUp()
        //第二步：驾驶
        move()
        //第三步：停止
        stop()
    }
}

class Wuling : AbstractCar() {
    override fun move() {
        print("五菱宏光跑起来")
    }
}

class Audi : AbstractCar() {
    override fun move() {
        println("奥迪跑起来")
    }
}

```


  
