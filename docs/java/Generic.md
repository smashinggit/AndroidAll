[]

# 泛型

## 概念

泛型，即“参数化类型”。一提到参数，最熟悉的就是定义方法时有形参，然后调用此方法时传递实参。
那么参数化类型怎么理解呢？
顾名思义，就是将类型由原来的具体的类型参数化，类似于方法中的变量参数，此时类型也定义成参数形式
（可以称之为类型形参），然后在使用/调用时传入具体的类型（类型实参）。

**泛型只在编译阶段有效**。看下面的代码：

```
List<String> stringArrayList = new ArrayList<String>();
List<Integer> integerArrayList = new ArrayList<Integer>();

Class classStringArrayList = stringArrayList.getClass();
Class classIntegerArrayList = integerArrayList.getClass();

if(classStringArrayList.equals(classIntegerArrayList)){
    Log.d("泛型测试","类型相同");
}
```
输出结果：D/泛型测试: 类型相同。

通过上面的例子可以证明，在编译之后程序会采取去泛型化的措施。也就是说Java中的泛型，只在编译阶段有效。
**在编译过程中，正确检验泛型结果后，会将泛型的相关信息擦除**，并且在对象进入和离开方法的边界处添加类型检查
和类型转换的方法。也就是说，**泛型信息不会进入到运行时阶**

对此总结成一句话：**泛型类型在逻辑上可以看成是多个不同的类型，实际上都是相同的基本类型**。



## 泛型类

泛型类型用于类的定义中，被称为泛型类。通过泛型可以完成对一组类的操作对外开放相同的接口。
最典型的就是各种容器类，如：List、Set、Map

```
//此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
//在实例化泛型类时，必须指定T的具体类型
public class Generic<T>{ 
    //key这个成员变量的类型为T,T的类型由外部指定  
    private T key;

    public Generic(T key) { //泛型构造方法形参key的类型也为T，T的类型由外部指定
        this.key = key;
    }

    public T getKey(){ //泛型方法getKey的返回值类型为T，T的类型由外部指定
        return key;
    }
}
```

**泛型的类型参数只能是类类型，不能是简单类型。**


## 泛型接口

泛型接口与泛型类的定义及使用基本相同。泛型接口常被用在各种类的生产器中，可以看一个例子：

```
//定义一个泛型接口
public interface Generator<T> {
    public T next();
}
```

## 泛型通配符 ?

类型通配符一般是使用？代替具体的类型实参，
注意了，此处’？’是类型实参，而不是类型形参 
再直白点的意思就是，此处的？和 Number、String、Integer 一样都是一种**实际的类型**，
可以把？看成所有类型的父类,是一种真实的类型


## 泛型上下边界

在使用泛型的时候，我们还可以为传入的泛型类型实参进行上下边界的限制，
如：类型实参只准传入某种类型的父类或某种类型的子类。

为泛型添加上边界，即传入的类型实参必须是指定类型的子类型

```
public void showKeyValue(Generic<? extends Number> obj){
    Log.d("泛型测试","key value is " + obj.getKey());
}
```





