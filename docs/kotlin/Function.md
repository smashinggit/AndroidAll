[toc]

#




# 高阶函数和 Lambda 表达式

Kotlin 函数都是头等的，这意味着它们可以
**存储在变量与数据结构中**、
**作为参数传递给其他高阶函数**以及
**从其他高阶函数返回**。

可以像操作任何其他非函数值一样操作函数。


## 高阶函数
高阶函数是将函数用作参数或返回值的函数。


### 函数类型

Kotlin 使用类似 (Int) -> String 的一系列**函数类型**来处理**函数的声明**：
val onClick: () -> Unit = ……。
 
- 所有函数类型都有一个圆括号括起来的参数类型列表以及一个返回类型：
(A, B) -> C 表示接受类型分别为 A 与 B 两个参数并返回一个 C 类型值的函数类型。
参数类型列表可以为空，如 () -> A。Unit 返回类型不可省略

- 函数类型可以有一个额外的接收者类型，它在表示法中的点之前指定： 
类型 A.(B) -> C 表示可以在 A 的接收者对象上以一个 B 类型参数来调用并返回一个 C 类型值的函数。
带有接收者的函数字面值通常与这些类型一起使用

- 挂起函数属于特殊种类的函数类型，它的表示法中有一个 suspend 修饰符 ，
例如 suspend () -> Unit 或者 suspend A.(B) -> C


函数类型表示法可以选择性地包含函数的参数名：(x: Int, y: Int) -> Point。 
这些名称可用于表明参数的含义。

如需将函数类型指定为可空，请使用圆括号：((Int, Int) -> Int)?


###  函数类型实例化

- 使用函数字面值的代码块，
采用以下形式之一：
1. lambda 表达式: { a, b -> a + b },
2. 匿名函数: fun(s: String): Int { return s.toIntOrNull() ?: 0 }

- 使用已有声明的可调用引用：
1. 顶层、局部、成员、扩展函数：  ::isOdd、 String::toInt
2. 顶层、成员、扩展属性：List<Int>::size，
3. 构造函数：::Regex

### 函数类型实例调用

函数类型的值可以通过其 invoke(……) 操作符调用：f.invoke(x) 或者直接 f(x)。

```
val stringPlus: (String, String) -> String = String::plus
val intPlus: Int.(Int) -> Int = Int::plus

println(stringPlus.invoke("<-", "->"))
println(stringPlus("Hello, ", "world!")) 

println(intPlus.invoke(1, 1))
println(intPlus(1, 2))
println(2.intPlus(3)) // 类扩展调用
```

## Lambda 表达式与匿名函数
lambda 表达式与匿名函数是“函数字面值”，即未声明的函数， 但立即做为表达式传递。
考虑下面的例子：
```
max(strings, { a, b -> a.length < b.length })
```

函数 max 是一个高阶函数，它接受一个函数作为第二个参数。
 其第二个参数是一个表达式，它本身是一个函数，即函数字面值，它等价于以下具名函数：
 
```
fun compare(a: String, b: String): Boolean = a.length < b.length
```


### Lambda 表达式语法

Lambda 表达式的完整语法形式如下：
```
val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y }
```
lambda 表达式总是括在花括号中， 完整语法形式的参数声明放在花括号内，并有可选的类型标注，
函数体跟在一个 -> 符号之后。如果推断出的该 lambda 的返回类型不是 Unit，
那么该 lambda 主体中的最后一个（或可能是单个） 表达式会视为返回值
```
val sum = { x: Int, y: Int -> x + y }
```

### 传递末尾的 lambda 表达式
在 Kotlin 中有一个约定：如果函数的最后一个参数是函数，那么作为相应参数传入的 lambda 表达式可以放在圆括号之外：
```
val product = items.fold(1) { acc, e -> acc * e }
```

这种语法也称为 拖尾 lambda 表达式。

如果该 lambda 表达式是调用时唯一的参数，那么圆括号可以完全省略：
```
run { println("...") }
```


# 内联函数

使用高阶函数会带来一些运行时的效率损失：
每一个函数都是一个对象，并且会捕获一个闭包,即那些在函数体内会访问到的变量。 
内存分配（对于函数对象和类）和虚拟调用会引入运行时间开销。

但是在许多情况下通过内联化 lambda 表达式可以消除这类的开销。 
下述函数是这种情况的很好的例子。即 lock() 函数可以很容易地在调用处内联。 考虑下面的情况：

```
lock(l) { foo() }
```

编译器没有为参数创建一个函数对象并生成一个调用。取而代之，编译器可以生成以下代码：
```
l.lock()
try {
    foo()
}
finally {
    l.unlock()
}
```
这个不是我们从一开始就想要的吗？
为了让编译器这么做，我们需要使用 inline 修饰符标记 lock() 函数：
```
inline fun <T> lock(lock: Lock, body: () -> T): T { …… }
```

**inline 修饰符影响函数本身和传给它的 lambda 表达式：所有这些都将内联到调用处**

内联可能导致生成的代码增加；不过如果我们使用得当（即避免内联过大函数），性能上会有所提升


# 具体化的类型参数






















