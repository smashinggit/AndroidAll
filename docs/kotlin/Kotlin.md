[toc]

# 基本类型
在 Kotlin 中，所有东西都是对象，在这个意义上讲我们可以**在任何变量上调用成员函数与属性**

## 数字 

类型   大小(比特数)    范围
Byte	8	        -128 ~ 127
Short	16          -32768 ~ 32767
Int	    32	        -2,147,483,648 (-231)	~  2,147,483,647 (231 - 1)
Long 	64	        -9,223,372,036,854,775,808 (-263)	~   9,223,372,036,854,775,807 (263 - 1)
    

类型	大小（比特数）	有效数字比特数	指数比特数	十进制位数
Float	  32	            24	          8	          6-7
Double	  64	            53	          11	      15-16



字面常量

数值常量字面值有以下几种:

- 二进制:    0b00001011
- 十进制：   123     Long 类型用大写 L 标记: 123L
- 十六进制： 0x0F

**注意: 不支持八进制**


每个数字类型支持如下的转换:

- toByte(): Byte
- toShort(): Short
- toInt(): Int
- toLong(): Long
- toFloat(): Float
- toDouble(): Double
- toChar(): Char

## 无符号整型
Kotlin 为无符号整数引入了以下类型：

- kotlin.UByte: 无符号 8 比特整数，范围是 0 到 255
- kotlin.UShort: 无符号 16 比特整数，范围是 0 到 65535
- kotlin.UInt: 无符号 32 比特整数，范围是 0 到 2^32 - 1
- kotlin.ULong: 无符号 64 比特整数，范围是 0 到 2^64 - 1


## 字符
字符用 Char 类型表示。它们**不能直接当作数字**

我们可以显式把字符转换为 Int 数字：      
```       
fun decimalDigitValue(c: Char): Int {
    if (c !in '0'..'9')
        throw IllegalArgumentException("Out of range")
    return c.toInt() - '0'.toInt() // 显式转换为数字
}
```




## 数组

数组在 Kotlin 中使用 Array 类来表示，它定义了 get 与 set 函数（按照运算符重载约定这会转变为 []）
以及 size 属性，以及一些其他有用的成员函数：

我们可以使用库函数 arrayOf() 来创建一个数组并传递元素值给它，这样 arrayOf(1, 2, 3) 创建了 array [1, 2, 3]。
或者，库函数 arrayOfNulls() 可以用于创建一个指定大小的、所有元素都为空的数组。

另一个选项是用接受数组大小以及一个函数参数的 Array 构造函数，用作参数的函数能够返回给定索引的每个元素
初始值：
```
// 创建一个 Array<String> 初始化为 ["0", "1", "4", "9", "16"]
val asc = Array(5) { i -> (i * i).toString() }
asc.forEach { println(it) }
```

## 原生类型数组
Kotlin 也有无装箱开销的专门的类来表示原生类型数组: ByteArray、 ShortArray、IntArray 等等。
**这些类与 Array 并没有继承关系**，但是它们有同样的方法属性集。它们也都有相应的工厂方法:



## 位运算

对于位运算，没有特殊字符来表示，而只可用中缀方式调用具名函数，例如:
```
val x = (1 shl 2) and 0x000FF000
```

这是完整的位运算列表（只用于 Int 与 Long）：
- shl(bits) – 有符号左移
- shr(bits) – 有符号右移
- ushr(bits) – 无符号右移
- and(bits) – 位与
- or(bits) – 位或
- xor(bits) – 位异或
- inv() – 位非



## 字符串
字符串用 String 类型表示。字符串是不可变的。 
字符串的元素——字符可以使用索引运算符访问: s[i]

Kotlin 有两种类型的字符串字面值: 
- 转义字符串   可以有转义字符
- 原始字符串   可以包含换行以及任意文本

下是转义字符串的一个示例:
```
val s = "Hello, world!\n"
```

原始字符串 使用三个引号（"""）分界符括起来，内部没有转义并且可以包含换行以及任何其他字符: 

```

val text = """
    for (c in "foo")
        print(c)
"""

val text = """ 
              Tell me and I forget.
              Teach me and I remember.
              Involve me and I learn.
              (Benjamin Franklin)
     """.trimIndent()  //去除空格


val text3 = """
    |Tell me and I forget.       // 默认 | 用作边界前缀，但你可以选择其他字符并作为参数传入，比如 trimMargin(">")
    |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
    """.trimMargin()    //可以通过 trimMargin() 函数去除前导空格

```

# 控制流 if、when、for、while

## if
在 Kotlin 中，if是一个表达式，即它会返回一个值。 因此就不需要三元运算符（条件 ? 然后 : 否则）

```
// 传统用法
var max = a 
if (a < b) max = b

// With else 
var max: Int
if (a > b) {
    max = a
} else {
    max = b
}
 
// 作为表达式
val max = if (a > b) a else b


// if 的分支可以是代码块，最后的表达式作为该块的值：
val max = if (a > b) {
    print("Choose a")
    a
} else {
    print("Choose b")
    b
}

```

## when
when 表达式取代了类 C 语言的 switch 语句。

```
when (x) {
    1 -> print("x == 1")
    2 -> print("x == 2")
    else -> { // 注意这个块
        print("x is neither 1 nor 2")
    }
}

// 如果很多分支需要用相同的方式处理，则可以把多个分支条件放在一起，用逗号分隔：
when (x) {
    0, 1 -> print("x == 0 or x == 1")
    else -> print("otherwise")
}

// 我们可以用任意表达式（而不只是常量）作为分支条件
when (x) {
    parseInt(s) -> print("s encodes x")
    else -> print("s does not encode x")
}

// 我们也可以检测一个值在（in）或者不在（!in）一个区间或者集合中：
when (x) {
    in 1..10 -> print("x is in the range")
    in validNumbers -> print("x is valid")
    !in 10..20 -> print("x is outside the range")
    else -> print("none of the above")
}

//另一种可能性是检测一个值是（is）或者不是（!is）一个特定类型的值
fun hasPrefix(x: Any) = when(x) {
    is String -> x.startsWith("prefix")
    else -> false
}

```

**可以使用以下语法将 when 的主语（subject，译注：指 when 所判断的表达式）捕获到变量中**
```
fun Request.getBody() =
        when (val response = executeRequest()) {
            is Success -> response.body
            is HttpError -> throw HttpException(response.status)
        }
```



## for

for 循环可以对任何提供迭代器（iterator）的对象进行遍历:
```
for (item in collection) print(item)
```


如需在数字区间上迭代，请使用区间表达式:
```
for (i in 1..3) {
    println(i)
}

for (i in 6 downTo 0 step 2) {
    println(i)
}
```


对区间或者数组的 for 循环会被编译为并不创建迭代器的基于索引的循环
如果你想要通过索引遍历一个数组或者一个 list，你可以这么做：
```
for (i in array.indices) {
    println(array[i])
}

// 或者你可以用库函数 withIndex
for ((index, value) in array.withIndex()) {
    println("the element at $index is $value")
}

```


## while

while 与 do..while 照常使用

```
while (x > 0) {
    x--
}

do {
  val y = retrieveData()
} while (y != null) // y 在此处可见
```





# 返回和跳转
Kotlin 有三种结构化跳转表达式：

- return。默认从最直接包围它的**函数或者匿名函数**返回。
- break。终止最直接包围它的循环。
- continue。继续下一次最直接包围它的循环。 


## Break 与 Continue 标签

在 Kotlin 中任何表达式都可以用标签（label）来标记。 
标签的格式为标识符后跟 @ 符号，例如：abc@、fooBar@都是有效的标签

要为一个表达式加标签，我们只要在其前加标签即可:
```
loop@ for (i in 1..100) {
    // ……
}
```

现在，我们可以用标签限制 break 或者continue：
```
        loop@ for (i in 0 until 4) {
            println("i = $i")

            for (j in 10 until 20) {
                println("j = $j")
                if (j == 12) {
                    break         //只有直接包围它的循环会中断，外层循环继续运行
                    break@loop   // 从标记处中断
                }
            }
        }
}

```

## 返回到标签
Kotlin 有函数字面量、局部函数和对象表达式。因此 Kotlin 的函数可以被嵌套。
标签限制的 return 允许我们从外层函数返回。 最重要的一个用途就是从 lambda 表达式中返回

```

fun foo() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return    // 非局部直接返回到 foo() 的调用者
        print(it)
    }
    println("this point is unreachable")
}
```
这个 return 表达式从最直接包围它的函数即 foo 中返回。
**如果我们需要从 lambda 表达式中返回，我们必须给它加标签并用以限制 return**
```

fun foo() {
    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit // 局部返回到该 lambda 表达式的调用者，即 forEach 循环
        print(it)
    }
    print(" done with explicit label")
}
```

现在，它只会从 lambda 表达式中返回。
**通常情况下使用隐式标签更方便。 该标签与接受该 lambda 的函数同名**
```
fun foo() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach // 局部返回到该 lambda 表达式的调用者，即 forEach 循环
        print(it)
    }
    print(" done with implicit label")
}
```

注意：
return@forEach // 局部返回到该 lambda 表达式的调用者，即 forEach 循环，
**这时forEach还是会继续进行下一次循环的*




# 类与对象

## 函数式（SAM）接口

只有一个抽象方法的接口称为函数式接口或 SAM（单一抽象方法）接口。
函数式接口可以有多个非抽象成员，但只能有一个抽象成员


## 扩展
Kotlin 能够扩展一个类的新功能而无需继承该类或者使用像装饰者这样的设计模式。
这通过叫做 **扩展** 的特殊声明完成。

例如，你可以为一个你不能修改的、来自第三方库中的类编写一个新的函数。 这个新增的函数就像那个原始类本来就有
的函数一样，可以用普通的方法调用。 这种机制称为 扩展函数 。此外，也有 扩展属性 ， 允许你为一个已经存在
的类添加新的属性

### 扩展函数

声明一个扩展函数，我们需要用一个 接收者类型 也就是被扩展的类型来作为他的前缀。 
下面代码为 MutableList<Int> 添加一个swap 函数：
```
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // “this”对应该列表
    this[index1] = this[index2]
    this[index2] = tmp
}
```
这个 this 关键字在扩展函数内部对应到接收者对象（传过来的在点符号前的对象)


### 扩展是静态解析的

扩展不能真正的修改他们所扩展的类。通过定义一个扩展，你并没有在一个类中插入新成员， 仅仅是可以通过该
类型的变量用点表达式去调用这个新函数

### 扩展属性

与函数类似，Kotlin 支持扩展属性：

```
val <T> List<T>.lastIndex: Int
    get() = size - 1


val House.number = 1 // 错误：扩展属性不能有初始化器
```

注意：
由于扩展没有实际的将成员插入类中，因此对扩展属性来说幕后字段是无效的。这就是为什么扩展属性不能有初始化器。
他们的行为只能由显式提供的 getters/setters 定义。


## 泛型

与 Java 类似，Kotlin 中的类也可以有类型参数：
```
class Box<T>(t: T) {
    var value = t
}
```

### 型变

Java 类型系统中最棘手的部分之一是通配符类型,而 Kotlin 中没有。
相反，它有两个其他的东西：
声明处型变（declaration-site variance）与类型投影（type projections）

首先，让我们思考为什么 Java 需要那些神秘的通配符.在 《Effective Java》第三版 解释了该问题——第 31 条：
利用有限制通配符来提升 API 的灵活性。 

**Java 中的泛型是不型变的**，这意味着 List<String> 并不是List<Object> 的子类型, 
为什么这样？ 如果 List 不是不型变的，它就没比 Java 的数组好到哪去，因为如下代码会通过编译然后导致运行时异常：

```
// Java
List<String> strs = new ArrayList<String>();
List<Object> objs = strs; // ！！！此处的编译器错误让我们避免了之后的运行时异常
objs.add(1); // 这里我们把一个整数放入一个字符串列表
String s = strs.get(0); // ！！！ ClassCastException：无法将整数转换为字符串

```
因此，Java 禁止这样的事情以保证运行时的安全。但这样会有一些影响。
例如，考虑 Collection 接口中的 addAll() 方法。该方法的签名应该是什么？直觉上，我们会这样:
```
// Java
interface Collection<E> …… {
  void addAll(Collection<E> items);
}
```

但随后，我们就无法做到以下简单的事情（这是完全安全）：
```
// Java
void copyAll(Collection<Object> to, Collection<String> from) {
  to.addAll(from);
  // ！！！对于这种简单声明的 addAll 将不能编译：
  // Collection<String> 不是 Collection<Object> 的子类型
}
```

这就是为什么 addAll() 的实际签名是以下这样：
```
// Java
interface Collection<E> …… {
  void addAll(Collection<? extends E> items);
}
```

**通配符类型参数 ? extends E 表示此方法接受 E 或者 E 的一些子类型对象的集合，而不只是 E 自身**。
这意味着我们可以安全地从其中（该集合中的元素是 E 的子类的实例）读取 E，但不能写入， 因为我们不知道什么对
象符合那个未知的 E 的子类型。 反过来，该限制可以让Collection<String>表示为Collection<? extends Object>
的子类型。

简而言之，**带 extends 限定（上界）的通配符类型使得类型是协变的（covariant）**。

理解为什么这个技巧能够工作的关键相当简单：
如果只能从集合中获取元素，那么使用 String 的集合， 并且从其中读取 Object 也没问题 。
反过来，如果只能向集合中 放入 元素，就可以用 Object 集合并向其中放入 String：
在 Java 中有 List<? super String> 是 List<Object> 的一个超类。

后者称为逆变性（contravariance），并且对于 List <? super String> 你只能调用接受 String 作为参数的方法 
（例如，你可以调用 add(String) 或者 set(int, String)），当然如果调用函数返回 List<T> 中的 T，你得到的并
非一个 String 而是一个 Object。







# 作用域函数  Scope Functions
[https://kotlinlang.org/docs/reference/scope-functions.html#let](https://kotlinlang.org/docs/reference/scope-functions.html#let)
[https://blog.csdn.net/qq_18242391/article/details/81068906](https://blog.csdn.net/qq_18242391/article/details/81068906)

The Kotlin standard library contains several functions whose sole purpose is to
**execute a block of code within the context of an object**

When you call such a function on an object with a lambda expression provided, it forms a temporary
scope. In this scope, you can access the object without its name. 
Such functions are called scope functions

There are five of them:  run、with、T.run、T.let、T.also 以及 T.apply

Basically, these functions do the same: **execute a block of code on an object**. 

What's different is 
**how this object becomes available inside the block** and
**what is the result of the whole expression**.




作用域函数的用法如下：
```
Person("Alice", 20, "Amsterdam").let {
    println(it)
    it.moveTo("London")
    it.incrementAge()
    println(it)
}
```

如果不使用作用函数，代码是这样的：
```
val alice = Person("Alice", 20, "Amsterdam")
println(alice)
alice.moveTo("London")
alice.incrementAge()
println(alice)
```

作用域函数不提供任何新的技术，它只是让代码变得更简洁、可读


由于这些函数非常的相似，如何选择使用是非常棘手的。
选择的标准通常取决于你的**意图**和在您的项目中使用的一致性


## 区别

它们的区别主要有2点：
- 访问 context 对象的方式
- 返回值


###  context 对象 ：this 或 it

```
fun main() {
    val str = "Hello"

    // this
    str.run {
        println("The receiver string length: $length")
        //println("The receiver string length: ${this.length}") // does the same
    }

    // it
    str.let {
        println("The receiver string's length is ${it.length}")
    }
}
```

#### this
函数 run、with、apply 通过 this 关键字访问 context 对象。
在 lambda 表达式中，可以省略 this 去直接访问对象的成员变量或者方法
如果 this 关键字省略了，很难区分变量或者方法是内部对象的还是外部的

所以，this 关键字： 主要用来**调用它的方法或者分配属性**
```
val adam = Person("Adam").apply { 
    age = 20                       // same as this.age = 20 or adam.age = 20
    city = "London"
}
println(adam)

```

#### it
相反的，函数 let、also 将 context 对象作为 lambda 表达式的参数
it 比 this 更简洁，更易读，但是在调用调用对象方法的时候无法像 this 一样省略关键字

因此， it 关键字 通常用来**作为方法调用的参数**

```
fun getRandomInt(): Int {
    return Random.nextInt(100).also {
        writeToLog("getRandomInt() generated value $it")
    }
}

val i = getRandomInt()
```

### 返回值

共两种区别：
- 返回对象本身： apply、also
- 返回lambda表达式的结果： let、with、run

#### 对象本身

apply、also 的返回值是对象本身，因此他们可以用来进行链式调用
```
val numberList = mutableListOf<Double>()
numberList.also { println("Populating the list") }
    .apply {
        add(2.71)
        add(3.14)
        add(1.0)
    }
    .also { println("Sorting the list") }
    .sort()

```

### lambda表达式结果
let、with、run 的返回值是对象本身，因此他们可以用来将表达式结果赋值给一个变量、链式操作的结果 等等

```
val numbers = mutableListOf("one", "two", "three")
val countEndsWithE = numbers.run { 
    add("four")
    add("five")
    count { it.endsWith("e") }
}
println("There are $countEndsWithE elements that end with e.")

```


## 如何使用及选择
实际上会有很多总情况，所以下面的例子只是展示了最常见的用法


## let
The context object is available as an argument (it). 
The return value is the lambda result.

let can be used to** invoke one or more functions on results of call chains**. 

```
val numbers = mutableListOf("one", "two", "three", "four", "five")
val resultList = numbers.map { it.length }.filter { it > 3 }
println(resultList)    

```


##  apply  配置对象的属性
The context object is available as a receiver (this). 
The return value is the object itself.

```
val myRectangle = Rectangle().apply {
    length = 4
    breadth = 5
    color = 0xFAFAFA
}
```

**这对于配置未出现在对象构造函数中的属性非常有用**


## with  对一个对象实例调用多个方法

```
class Turtle {
    fun penDown()
    fun penUp()
    fun turn(degrees: Double)
    fun forward(pixels: Double)
}

val myTurtle = Turtle()

with(myTurtle) { // 画一个 100 像素的正方形
    penDown()
    for (i in 1..4) {
        forward(100.0)
        turn(90.0)
    }
    penUp()
}


```

## run


## also


#



#