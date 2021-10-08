[toc]



#  一、Java基础

## 1.1 JVM

Java 虚拟机（JVM）是运行 Java 字节码的虚拟机。JVM 有针对不同系统的特定实现（Windows，Linux，macOS），
**目的是使用相同的字节码，它们都会给出相同的结果**。
字节码和不同系统的 JVM 实现是 Java 语言“一次编译，随处可以运行”的关键所在

在 Java 中，JVM 可以理解的代码就叫做`字节码`（即扩展名为 `.class` 的文件），它不面向任何特定的处理器，只面向虚拟机。
Java 语言通过字节码的方式，在一定程度上解决了传统解释型语言执行效率低的问题，同时又保留了解释型语言可移植的特点。
所以 Java 程序运行时比较高效，而且，由于字节码并不针对一种特定的机器，
因此，Java 程序无须重新编译便可在多种不同操作系统的计算机上运行


## 1.2 基本数据类型

Java**中**有 8 种基本数据类型，分别为：

- 6 种数字类型 ：byte、short、int、long、float、double
- 1 种字符类型：char
- 1 种布尔型：boolean

这八种基本类型都有对应的包装类分别为：Byte、Short、Integer、Long、Float、Double、Character、Boolean


- byte:  8位、有符号的，以二进制补码表示的整数,最小值是 -128（-2^7）,最大值是 127（2^7-1）,默认值为0

         byte 类型用在大型数组中节约空间，主要代替整数，
         因为 byte 变量占用的空间只有 int 类型的四分之一
         例子：byte a = 100，byte b = -50

- short：16位，有符号的，以二进制补码表示的整数，最小值是 -32768（-2^15），最大值是 32767（2^15 - 1）,默认值为0

         Short 数据类型也可以像 byte 那样节省空间。一个short变量是int型变量所占空间的二分之一
         例子：short s = 1000，short r = -20000

- int :  32位、有符号的以二进制补码表示的整数,最小值是 -2,147,483,648（-2^31）,最大值是 2,147,483,647（2^31 - 1）
         例子：int a = 100000, int b = -200000

- long : 64 位、有符号的以二进制补码表示的整数,最小值是 -9,223,372,036,854,775,808（-2^63）,默认值0L
         最大值是 9,223,372,036,854,775,807（2^63 -1）
         例子： long a = 100000L，Long b = -200000L

- float: 单精度、32位、符合IEEE 754标准的浮点数, 在储存大型浮点数组的时候可节省内存空间,默认值是 0.0f
         **浮点数不能用来表示精确的值，如货币**
         例子：float f1 = 234.5f

- double: 双精度、64 位、符合IEEE 754标准的浮点数, 默认值是 0.0d
          **double类型同样不能表示精确的值，如货币**
          例子：double d1 = 123.4

- boolean: 只有两个取值：true 和 false，默认值是 false

- char : char类型是一个单一的 16 位 Unicode 字符, 最小值是 \u0000（即为0）,最大值是 \uffff（即为65,535）
         **char 数据类型可以储存任何字符(只能放单个字符)**，默认值 'u0000'

         因为char是16位的，采取的Unicode的编码方式，所以char就有以下的初始化方式：
         char c = '陈';     //任意单个中文字，加单引号
         char c = 'A';      //任意单个字符，加单引号
         char c = 10;       //可以用整数赋值
         char c = '\u数字'; //用字符的编码值来初始化


         char类型是可以运算的,因为 char 在 ASCII 等字符编码表中有对应的数值
         在 Java 中，对 char 类型字符运行时，直接当做 ASCII 表对应的整数来对待


**byte、int、long、和short都可以用十进制、16进制以及8进制的方式来表示**
int decimal = 100;
int octal = 0144;
int hex =  0x64;

## 1.3  自动装箱与拆箱
- 装箱：将基本类型用它们对应的引用类型包装起来；
- 拆箱：将包装类型转换为基本数据类型；

## 1.4 continue、break、和 return 的区别是什么

在循环结构中，当循环条件不满足或者循环次数达到要求时，循环会正常结束。
但是，有时候可能需要在循环的过程中，当发生了某种条件之后 ，提前终止循环，这就需要用到下面几个关键词：

- continue ：指跳出当前的这一次循环，继续下一次循环
- break ：指跳出整个循环体，继续执行循环下面的语句
- return ：用于跳出所在方法，结束该方法的运行





## 1.5 ==和 equals 的区别

- == : 它的作用是**判断两个对象的地址是不是相等**,即判断两个对象是不是同一个对象。

**基本数据类型==比较的是值，引用数据类型==比较的是内存地址**


- equals() : 它的作用也是判断两个对象是否相等，它不能用于比较基本数据类型的变量
equals()方法存在于Object类中，而Object类是所有类的直接或间接父类

Object类equals()方法：
```
public boolean equals(Object obj) {
     return (this == obj);
}
```

equals() 方法存在两种使用情况：

情况 1：类没有覆盖 equals()方法。
则通过equals()比较该类的两个对象时，**等价于通过“==”比较这两个对象**。使用的默认是 Object类equals()方法。

情况 2：类覆盖了 equals()方法。一般，我们都覆盖 equals()方法来两个对象的内容相等；
若它们的内容相等，则返回 true(即，认为这两个对象相等)


注意：
String 中的 equals 方法是被重写过的，
因为 Object 的 equals 方法是比较的对象的内存地址，
而 String 的 equals 方法比较的是对象的值

### 什么重写 equals 时必须重写 hashCode 方法？
举例：
你把对象加入 HashSet 时，HashSet 会先计算对象的 hashcode 值来判断对象加入的位置，
同时也会与其他已经加入的对象的 hashcode 值作比较，如果没有相符的 hashcode，HashSet 会假设对象没有重复出现。
但是如果发现有相同 hashcode 值的对象，这时会调用 equals() 方法来检查 hashcode 相等的对象是否真的相同。
如果两者相同，HashSet 就不会让其加入操作成功。
如果不同的话，就会重新散列到其他位置。
这样我们就大大减少了 equals 的次数，相应就大大提高了执行速度。

为什么重写 equals 时必须重写 hashCode 方法？
如果两个对象相等，则 hashcode 一定也是相同的。两个对象相等,对两个对象分别调用 equals 方法都返回 true。
但是，**两个对象有相同的 hashcode 值，它们也不一定是相等的** ( 注意：这是在散列表中的情况。在非散列表中一定如此！)
因此，equals 方法被覆盖过，则 hashCode 方法也必须被覆盖

为什么两个对象有相同的 hashcode 值，它们也不一定是相等的？
因为 hashCode() 所使用的杂凑算法**也许刚好会让多个对象传回相同的杂凑值**
越糟糕的杂凑算法越容易碰撞，但这也与数据值域分布的特性有关（所谓碰撞也就是指的是不同的对象得到相同的 hashCode)

我们刚刚也提到了 HashSet,如果 HashSet 在对比的时候，
**同样的 hashcode 有多个对象，它会使用 equals() 来判断是否真的相同**
**也就是说 hashcode 只是用来缩小查找成本**



## 1.6  深拷贝 vs 浅拷贝
- 浅拷贝：对基本数据类型进行值传递，对引用数据类型进行**引用传递**般的拷贝，此为浅拷贝。
- 深拷贝：对基本数据类型进行值传递，对引用数据类型，**创建一个新的对象，并复制其内容**，此为深拷贝。

### 1.6.1 **⼏种浅拷⻉**

1、遍历循环复制

```
List<Person> destList=new ArrayList<Person>(srcList.size()); 
for(Person p : srcList){ 
 destList.add(p); 
}
```

2、使⽤List实现类的构造⽅法

```
List<Person> destList=new ArrayList<Person>(srcList);
```

3、使⽤list.addAll()⽅法

```
List<Person> destList=new ArrayList<Person>(); 
destList.addAll(srcList);
```

4、使⽤System.arraycopy()⽅法

```
Person[] srcPersons=srcList.toArray(new Person[0]); 
Person[] destPersons=new Person[srcPersons.length]; 
System.arraycopy(srcPersons, 0, destPersons, 0, srcPersons.length);
```



### 1.6.2  几种深拷贝

1.使⽤序列化⽅法

```
public static <T> List<T> deepCopy(List<T> src) throws IOException,ClassNotFoundException { 
 ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); 
 ObjectOutputStream out = new ObjectOutputStream(byteOut); 
 out.writeObject(src); 
 ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray()); 
 ObjectInputStream in = new ObjectInputStream(byteIn); 
 @SuppressWarnings("unchecked") 
 List<T> dest = (List<T>) in.readObject(); 
 return dest; 
} 
List<Person> destList=deepCopy(srcList); //调⽤该⽅法
```

2.clone⽅法

```
public class A implements Cloneable { 
 public String name[]; 
 public A(){ 
   name=new String[2]; 
 } 
 
 public Object clone() { 
   A o = null; 
   try { 
      o = (A) super.clone(); 
   } catch (CloneNotSupportedException e) { 
      e.printStackTrace(); 
   } 
       return o; 
  } 
} 
for(int i=0;i<n;i+=){
   copy.add((A)src.get(i).clone());
}
```





## 2.1 构造器 Constructor 是否可被 override?

Constructor 不能被 override（重写）,但是可以 overload（重载）,所以你可以看到一个类中有多个构造函数的情况。

## 2.2  在 Java 中定义一个不做事且没有参数的构造方法的作用

Java 程序在执行子类的构造方法之前，**如果没有用 super()来调用父类特定的构造方法，则会调用父类中“没有参数的构造方法”**
因此，如果父类中只定义了有参数的构造方法，而在子类的构造方法中又没有用 super()来调用父类中特定的构造方法，则编译时将发生错误，
因为 Java 程序在父类中找不到没有参数的构造方法可供执行。
解决办法是在父类里加上一个不做事且没有参数的构造方法。

在调用子类构造方法之前会先调用父类没有参数的构造方法,其目的是 **帮助子类做初始化工作**


## 2.3 对象实体与对象引用有何不同?

对象引用指向对象实例（对象引用存放在栈内存中）
一个对象引用可以指向 0 个或 1 个对象（一根绳子可以不系气球，也可以系一个气球）
一个对象可以有 n 个引用指向它（可以用 n 条绳子系住一个气球）

## 2.4  对象的相等与指向他们的引用相等,两者有什么不同?

对象的相等，比的是内存中存放的内容是否相等
而引用相等，比较的是他们指向的内存地址是否相等


## 2.5 面向对象三大特征 : 封装、继承、多态

- 封装： 封装是指把一个对象的状态信息（也就是属性）隐藏在对象内部，不允许外部对象直接访问对象的内部信息
但是可以提供一些可以被外界访问的方法来操作属性

- 继承：继承是使用已存在的类的定义作为基础建立新类的技术，新类的定义可以增加新的数据或新的功能，也可以用父类的功能

1. 子类拥有父类对象所有的属性和方法（包括私有属性和私有方法），但是父类中的私有属性和方法子类是无法访问，只是拥有。
2. 子类可以拥有自己属性和方法，即子类可以对父类进行扩展。
3. 子类可以用自己的方式实现父类的方法。（多态）

- 多态： 表示一个对象具有多种的状态。具体表现为**父类的引用指向子类的对象**

1. 对象类型和引用类型之间具有继承（类）/实现（接口）的关系；
2. 引用类型变量发出的方法调用的到底是哪个类中的方法，必须在程序运行期间才能确定；
3. 多态不能调用“只在子类存在但在父类不存在”的方法；
4. 如果子类重写了父类的方法，真正执行的是子类覆盖的方法，如果子类没有覆盖父类的方法，执行的是父类的方法


## 2.6 String 为什么是不可变的?

String 类中使用 final 关键字修饰字符数组来保存字符串，private final char value[]，所以 String 对象是不可变的。

> 在 Java 9 之后，String 、StringBuilder 与 StringBuffer 的实现改用 byte 数组存储字符串 private final byte[] value


## 2.7  String、StringBuffer 、StringBuilder 的区别是什么?

StringBuilder 与 StringBuffer 都继承自 AbstractStringBuilder 类，
在 AbstractStringBuilder 中也是使用字符数组保存字符串 char[] value 
**但是没有用 final 关键字修饰，所以这两种对象都是可变的**

### 线程安全性

- String 中的对象是不可变的，也就可以理解为常量，线程安全
- StringBuffer 对方法加了**同步锁或者对调用的方法加了同步锁**，所以是线程安全的
- StringBuilder 并没有对方法进行加同步锁，所以是非线程安全的


### 性能

- 每次对 String 类型进行改变的时候，都会生成一个新的 String 对象，然后将指针指向新的 String 对象
- StringBuffer 每次都会对 StringBuffer 对象本身进行操作，而不是生成新的对象并改变对象引用
- 相同情况下使用 StringBuilder 相比使用 StringBuffer 仅能获得 10%~15% 左右的性能提升，但却要冒多线程不安全的风险


### 对于三者使用的总结

1. 操作少量的数据: 适用 String
2. 单线程操作字符串缓冲区下操作大量数据: 适用 StringBuilder
3. 多线程操作字符串缓冲区下操作大量数据: 适用 StringBuffer





## 3.3 文件与IO

## Java 中 IO 流分为几种

- 按照流的流向分，可以分为输入流和输出流；
- 按照操作单元划分，可以划分为字节流和字符流；
- 按照流的角色划分为节点流和处理流


Java Io 流共涉及 40 多个类，这些类看上去很杂乱，但实际上很有规则，而且彼此之间存在非常紧密的联系， 
Java I0 流的 40 多个类都是从如下 4 个抽象类基类中派生出来的:

- InputStream/Reader: 所有的输入流的基类，前者是字节输入流，后者是字符输入流。
- OutputStream/Writer: 所有输出流的基类，前者是字节输出流，后者是字符输出流

## 既然有了字节流,为什么还要有字符流

问题本质是：不管是文件读写还是网络发送接收，信息的最小存储单元都是字节，
那为什么 I/O 流操作要分为字节流操作和字符流操作呢？

字符流是由 Java 虚拟机将字节转换得到的，问题就出在这个过程还算是非常耗时，
并且，如果我们不知道编码类型就很容易出现乱码问题。
所以， I/O 流就干脆提供了一个直接操作字符的接口，方便我们平时对字符进行流操作。

**如果音频文件、图片等媒体文件用字节流比较好，如果涉及到字符的话使用字符流比较好**



## 3.4 容器类

### 3.4.1 分类

Java的容器类分为List,Set,Queue和Map。

Java容器类库的主要用途是持有对象，通常两种不同的数据结构:

- Collection，独立元素的序列，这些元素都服从一条或多条规则。List、Set以及Queue都是Collection的一种，

- Map , 存储的是“键值对”对象，通过键来检索值
  Map：以Key-Value形式存储, key不可重复 value可以重复

### 3.4.2 List

有序的 collection（也称为序列）。此接口的用户可以对列表中每个元素的插入位置进行精确地控制。
用户可以根据元素的整数索引（在列表中的位置）访问元素，并搜索列表中的元素。
与 set 不同，列表通常允许重复的元素

- ArrayList<E>
  List 接口的大小可变数组的实现。实现了所有可选列表操作，并允许包括 null 在内的所有元素
  线程不安全

- LinkedList<E>
  List 接口的链接列表实现
  线程不安全

- Vector<E>
  Vector 类可以实现可增长的对象数组
  线程安全

### 3.4.3 Set

个不包含重复元素的 collection

- HashSet<E>
  此类实现 Set 接口，由哈希表（实际上是一个 HashMap 实例）支持
  线程不安全

- LinkedHashSet<E>
  具有可预知迭代顺序的 Set 接口的哈希表和链接列表实现。
  此实现与 HashSet 的不同之外在于，后者维护着一个运行于所有条目的双重链接列表
  线程不安全

- TreeSet<E>
  基于 TreeMap 的 NavigableSet 实现。使用元素的自然顺序对元素进行排序，
  或者根据创建 set 时提供的 Comparator 进行排序，具体取决于使用的构造方法
  线程不安全


### 3.4.4 Queue

在处理元素前用于保存元素的 collection。除了基本的 Collection 操作外，队列还提供其他的插入、提取和检查操作

- ArrayDeque<E>
  Deque 接口的大小可变数组的实现。数组双端队列没有容量限制；它们可根据需要增加以支持使用。
  线程不安全的


### 3.4.5 Map

将键映射到值的对象。一个映射不能包含重复的键；每个键最多只能映射到一个值

- HashMap<K,V>
  基于哈希表的 Map 接口的实现
  线程不安全
- LinkedHashMap<K,V>
  Map 接口的哈希表和链接列表实现，具有可预知的迭代顺序
  线程不安全
- Hashtable<K,V>
  此类实现一个哈希表，该哈希表将键映射到相应的值
  线程安全
- TreeMap<K,V>
  基于红黑树（Red-Black tree）的 NavigableMap 实现
  线程不安全的



***

 

# 二、进阶

## 2.1 泛型



### 2.1.1 概述

Java 泛型（generics）是 JDK 5 中引入的一个新特性, 泛型提供了**编译时类型安全检测机制**，
该机制允许程序员在编译时检测到非法的类型。
**泛型的本质是参数化类型**，也就是说所操作的数据类型被指定为一个参数

```
List<Integer> list = new ArrayList<>();

list.add(12);
//这里直接添加会报错
list.add("a");

Class<? extends List> clazz = list.getClass();
Method add = clazz.getDeclaredMethod("add", Object.class);
//但是通过反射添加，是可以的
add.invoke(list, "kl");

System.out.println(list)
```

**Java 的泛型是伪泛型，这是因为 Java 在编译期间，所有的泛型信息都会被擦掉，这也就是通常所说类型擦除 **

Java中的泛型，只在编译阶段有效。在编译过程中，正确检验泛型结果后，会将泛型的相关信息擦除，并且在对象进入和离开方法的边界处添加类型检查和类型转换的方法。也就是说，泛型信息不会进入到运行时阶段



**泛型是计算机程序中⼀种᯿要的思维⽅式，它将数据结构和算法与数据类型相分离，使得同⼀套数据结构和算法，能够应⽤于各种数据类型，⽽且还可以保证类型安全，提⾼可读性**。在Java中，泛型⼴泛应⽤于各种容器类中，理解泛型是深刻理解容器的基础



### 2.1.2  泛型通配符

常用的通配符为： T，E，K，V，？

- ？ 表示不确定的 java 类型
- T (type) 表示具体的一个 java 类型
- K V (key value) 分别代表 java 键值中的 Key Value
- E (element) 代表 Element



#### 无界通配符  ？



```
static int countLegs (List<? extends Animal > animals ) {
    int retVal = 0;
    for ( Animal animal : animals ) {
        retVal += animal.countLegs();
    }
    return retVal;
}

static int countLegs1 (List< Animal > animals ){
    int retVal = 0;
    for ( Animal animal : animals ){
        retVal += animal.countLegs();
    }
    return retVal;
}

public static void main(String[] args) {
    List<Dog> dogs = new ArrayList<>();
 	// 不会报错
    countLegs(dogs);
	// 报错
    countLegs1(dogs);
}

```

当调用 countLegs1 时，就会报错

**对于不确定或者不关心实际要操作的类型**，可以使用无限制通配符（尖括号里一个问号，即 <?> ），
表示可以持有任何类型



#### 上界通配符 <? extends E>

上界：用 extends 关键字声明，表示**参数化的类型可能是所指定的类型，或者是此类型的子类**

- 如果传入的类型不是 E 或者 E 的子类，编译不成功
- 泛型中可以使用 E 的方法，要不然还得强转成 E 才能使用

```
private <K extends A, E extends B> E test(K arg1, E arg2){
    E result = arg2;
    arg2.compareTo(arg1);
    //.....
    return result;
}
```

> 类型参数列表中如果有多个类型参数上限，用逗号分开



#### 下界通配符  <? super E>

下界: 用 super 进行声明，表示 **参数化的类型可能是所指定的类型，或者是此类型的父类型，直至 Object**

在类型参数中使用 super 表示 **这个泛型中的参数必须是 E 或者 E 的父类**

```
private <T> void test(List<? super T> dst, List<T> src){
    for (T t : src) {
        dst.add(t);
    }
}

public static void main(String[] args) {
    List<Dog> dogs = new ArrayList<>();
    List<Animal> animals = new ArrayList<>();
    new Test3().test(animals,dogs);
}

// Dog 是 Animal 的子类
class Dog extends Animal {
}

```

dst 类型 “大于等于” src 的类型，这里的“大于等于”是指 dst 表示的范围比 src 要大，
因此装得下 dst 的容器也就能装 src



### 2.1.3 ? 和 T 的区别

```
//元素的类型必须是T
List<T> list = new ArrayList<T>();
//元素可以是任何类型的
List<?> list = new ArrayList<?>();
```

？和 T 都表示不确定的类型，区别在于我们 **可以对 T 进行操作，但是对 ？不行**，

比如如下这种 ：

```
// 可以
T t = operate();

// 不可以
？ car = operate();
```

**T 是一个 确定的 类型，通常用于泛型类和泛型方法的定义**，
**？是一个 不确定 的类型，通常用于泛型方法的调用代码和形参，不能用于定义类和泛型方法**

```
// 通过 T 来 确保 泛型参数的一致性
public <T extends Number> void test(List<T> dest, List<T> src)

//通配符是 不确定的，所以这个方法不能保证两个 List 具有相同的元素类型
public void test(List<? extends Number> dest, List<? extends Number> src)

```

**通配符可以使用超类限定而类型参数不行**

类型参数 T 只具有 一种类型限定方式:

```
T extends A
```

但是通配符 ? 可以进行 两种限定：

```
? extends A
? super A
```



### 2.1.4 Class<T> 和 Class<?> 的区别

Class<T> 在实例化的时候，T 要替换成具体类。
Class<?> 它是个通配泛型，? 可以代表任何类型，所以主要用于声明时的限制情况

```
// 可以
public Class<?> clazz;

// 不可以，因为 T 需要指定类型
public Class<T> clazzT;
```

所以当不知道定声明什么类型的 Class 的时候可以定义一 个Class<?>

那如果也想 public Class<T> clazzT; 这样的话，就必须让当前的类也指定 T ，

```
public class Test3<T> {
    public Class<?> clazz;
    // 不会报错
    public Class<T> clazzT;

```

### 2.1.5 型变

Java 中的泛型是**不型变的**，这意味着 `List<String>` **不是** `List<Object>` 的子类型





## 3.1 反射

![详见/docs/java/Reflect.md](../../docs/java/Reflect.md)

JAVA 反射机制是在运行状态中，
对于任意一个类，都能够知道这个类的所有属性和方法；
对于任意一个对象，都能够调用它的任意一个方法和属性；
这种动态获取的信息以及动态调用对象的方法的功能称为 java 语言的反射机制


### 反射机制优缺点
优点： 运行期类型的判断，动态加载类，提高代码灵活度。
缺点： 
1,性能瓶颈：反射相当于一系列解释操作，通知 JVM 要做的事情，性能比直接的 java 代码要慢很多。
2,安全问题，让我们可以动态操作改变类的属性同时也增加了类的安全隐患。


### 反射的应用场景

反射是框架设计的灵魂
在我们平时的项目开发过程中，基本上很少会直接使用到反射机制，但这不能说明反射机制没有用，
实际上有很多设计、开发都与反射机制有关，例如模块化的开发，通过反射去调用对应的字节码；动态代理设计模式也采用了反射机制


## 3.2  多线程

### 简述线程、程序、进程的基本概念。以及他们之间关系是什么?

程序是含有指令和数据的文件，被存储在磁盘或其他的数据存储设备中，也就是说程序是静态的代码

进程是程序的一次执行过程，是系统运行程序的基本单位，因此进程是动态的。系统运行一个程序即是一个进程从创建，运行到消亡的过程。
简单来说，一个进程就是一个执行中的程序，它在计算机中一个指令接着一个指令地执行着，
同时，每个进程还占有某些系统资源如 CPU 时间，内存空间，文件，输入输出设备的使用权等

线程与进程相似，但线程是一个比进程更小的执行单位。一个进程在其执行的过程中可以产生多个线程。
与进程不同的是同类的多个线程共享同一块内存空间和一组系统资源，
所以系统在产生一个线程，或是在各个线程之间作切换工作时，负担要比进程小得多，
也正因为如此，线程也被称为轻量级进程


### 重点：多线程并发

![详见/docs/java/Thread.md](/docs/java/Thread.md) 






### 3.4.6 面试题

- 请说一下Java容器集合的分类，各自的继承结构

Collection下分为Set，List，Queue

Set的常用实现类有HashSet，TreeSet等
List的常用实现类有ArrayList，LinkedList等
Queue的常用实现类有LinkedList，ArrayBlockingQueue等

Map下没有进一步分类，它的常用实现类有HashMap，ConcurrentHashMap等

- 谈谈ArrayList和LinkedList的区别

本质的区别来源于两者的底层实现：
ArrayList的底层是数组，
LinkedList的底层是双向链表

数组拥有O(1)的查询效率，可以通过下标直接定位元素；
链表在查询元素的时候只能通过遍历的方式查询，效率比数组低

数组增删元素的效率比较低，通常要伴随拷贝数组的操作；
链表增删元素的效率很高，只需要调整对应位置的指针即可

- 谈谈ArrayList和Vector的区别

两者的底层实现相似，关键的不同在于Vector的对外提供操作的方法都是用synchronized修饰的，
也就是说Vector在并发环境下是线程安全的，
而ArrayList在并发环境下可能会出现线程安全问题

由于Vector的方法都是同步方法，执行起来会在同步上消耗一定的性能，
所以在单线程环境下，Vector的性能是不如ArrayList的

- 请介绍一下HashMap的实现原理

我们一般用HashMap存储key-value类型的数据，它的底层是一个数组，
当我们调用put方法的时候，首先会对key进行计算得出一个hash值，然后根据hash值计算出存放在数组上的位置

这个时候我们会遇到两种情况：
一是数组上该位置为空，可以直接放入数据；
还有一种情况是该位置已经存放值了，这就发生了哈希冲突

在现在使用较为普遍的JDK1.8中是这样处理哈希冲突的：
先用链表把冲突的元素串起来，如果链表的长度达到了8，并且哈希表的长度大于64，则把链表转为红黑树。（
在JDK1.7中没有转化为红黑树这一步，只用链表解决冲突）


## 3.5 异常

在 Java 中，所有的异常都有一个共同的祖先 java.lang 包中的 Throwable 类
Throwable 类有两个重要的子类 Exception（异常）和 Error（错误）
**Exception 能被程序本身处理(try-catch)， Error 是无法处理的(只能尽量避免)**

