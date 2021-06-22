[toc]
# Gradle



***



# 一、概览

[Gradle官方文档](https://docs.gradle.org/current/userguide/tutorial_using_tasks.html#sec:projects_and_tasks)

## 1.1 gradle 是什么

> 使用纯Java编写，基于Ant、Maven概念开源的 **自动化构建工具**，专注于灵活性和性能，构建脚本 摒弃了基于XML的繁琐配置，
> 采用 Groovy 或 Kotlin 的 特定领域语言(DSL) 来编写

- 是构建⼯具，不是语⾔
- 它⽤了 Groovy 这个语⾔，创造了⼀种 DSL，但它本身不是语⾔


## 1.2 Apk构建流程


![Apk构建流程](/pics/android/打包流程.png)

- IDE将源代码转成dex，其他内容转换成编译后的资源；
- APK打包器将dex和编译后的资源整合成单个apk；
- 打包器使用zipalign工具对应用进行优化，为apk签名；


# 二、Gradle基础

## 2.1 Gradle构建生命周期

![gradle生命周期](/pics/android/gradle/gradle生命周期.png)


## 2.2  Groovy基础语法速成

Groovy是JVM上的 脚本语言，基于Java扩展的 动态语言，除了兼容Java外，还加入了闭包等新功能。
Gradle会把**.gradle** Groovy脚本编译成.class java字节码文件在JVM上运行。

Gradle是自动化构建工具，运行在JVM上的一个程序，
Groovy是基于JVM的一种语言

基础规则:

- 不以分号结尾；
- 单引号字符串不会对$符号转义，双引号字符串可以使用字符串模板，三引号是带格式的字符串；
- 方法括号可省略，可不写return，默认返回最后一行代码；
- 代码块可以作为参数传递

定义 (使用def关键字定义):

```
// 定义变量：Groovy支持动态类型，定义时可不指定类型，会自行推导
def a = 1               // 定义整型，Groovy编译器会将所有基本类型都包装成对象类型
def b = "字符串：${a}"  // 定义字符串模板
de double c = 1.0   // 定义Double类型
```

声明变量:
```
// 局部变量，仅在声明它们的范围内可见
def dest = "dest"
task copy(type: Copy) {
    from "source"
    into dest
}

// ext额外属性，Gradle域模型中所有增强对象都可以容纳额外的用户定义属性
ext {
    springVersion = "3.1.0.RELEASE"
    emailNotification = "build@master.org"
}

task printProperties {
    doLast {
        println springVersion
        println emailNotification
    }
}

// 用类型修饰符声明的变量在闭包中可见，但在方法中不可见
String localScope1 = 'localScope1'

println localScope1

closure = {
    println localScope1  //在闭包中可见
}

def method() {
    try {
        localScope1   //在方法中不可见
    } catch (MissingPropertyException e) {
        println 'localScope1NotAvailable'
    }
}

closure.call()
method()

// 输出结果：
// localScope1
// localScope1
// localScope1NotAvailable
```


函数：
```
/ 无返回值的函数需使用def关键字，最后一行代码的执行结果就是返回值
// 无参函数
def fun1() { }

// 有参函数
def fun2(def1, def2) { }

// 指定了函数返回类型，则可不加def关键字
String fun3() { return "返回值" }

// 简化下，效果同fun3
String fun4() { "返回值" }

// 函数调用可以不加括号
println fun3

```

循环：
```
// i前面b，输出5个测试
for(i = 0;i < 5;i++) {
    println("测试")
}

// 输出6个测试
for(i in 0..5) {
    println("测试")
}

// 如果想输出5个可改成:
for(i in 0..<5)

// 循环次数，从0循环到4
4.times{
    println("测试: ${it}")
}
```


三目运算符、判断:
```
// 和Java一致，判空还可以简写成这样：
def name = 'd'
def result = name ?: "abc"

// 还有用?判空，跟Kotlin的一样，person不为空 → Data属性不为空 → 才打印name
println person?.Data?.name

// asType是类型转换
def s3 = s1.asType(Integer)
```


闭包:
相当于可以被传递的代码块

```
// 定义闭包
def clouser = { String param1, int param2 -> // 箭头前面是参数定义，后面是代码
    println "代码部分"
}

// 调用闭包
clouser.call()
clouser()

// 闭包没定义参数的话，隐含一个it参数，和this作用类似
def noParamClosure = { it-> true }

// 函数最后一个参数是一个闭包，可以省略圆括号，类似于回调函数的用法
task CoderPig {
    doLast ({
      println "Test"  
    })
}

task CoderPig {
    doLast {
      println "Test"  
    }
}

// 闭包里的关键变量，没有闭包嵌套时都指向同一个，有闭包时：
// this：闭包定义处的类；
// owner，delegate：离他最近的哪个闭包对象；

// 闭包委托：
//每个闭包都有一个delegate对象，Groovy使用该对象来查找不是闭包的局部变量或参数的变量和方法引用，就是代理模式
class Info {
    int id;
    String code;

    def log() {
        println("code:${code};id:${id}")
    }
}

def info(Closure<Info> closure) {
    Info p = new Info()
    closure.delegate = p
    // 委托模式优先
    closure.setResolveStrategy(Closure.DELEGATE_FIRST)
    closure(p)
}

task configClosure {
    doLast {
        info {
            code = "cix"
            id = 1
            log()
        }
    }
}

// 输出：Task :configClosure
// code:cix;id:1
```



集合:
```
// 数组，定义方式扩展如下，其他和Java类似
def array1 = [1, 2, 3, 4, 5] as int[]
int[] array2 = [1, 2, 3, 4, 5]

/* List：链表，对应ArrayList，变量由[]定义，元素可以是任何对象。*/

// 定义列表
def testList = [1, 2, 3] 

// 添加元素，左移位添加新元素
testList << 300;    
testList.add(6)
testList.leftShift(7)

// 删除
testList.remove(7)
testList.removeAt(7)
testList.removeElement(6)
testList.removeAll { return it % 2 == 0 }   // 自定义规则

// 查找
int result = testList.find { return it % 2 == 0 }
def result2 = testList.findAll { return it % 2 != 0 }
def result3 = testList.any { return it % 2 != 0 }
def result4 = testList.every { return it % 2 == 0 }

// 获取最小值、最大值、满足条件的数量
list.min()
list.max(return Math.abs(it))
def num = findList.count { return it >= 2 }

//排序
testList.sort()      
sortList.sort { a, b -> 
    a == b ？0 : 
            Math.abs(a) < Math.abs(b) ? 1 : -1
} 

/* Map：键值表，对应LinkedHashMap，使用 : 冒号定义，key必须为字符串，可以不用引号包裹 */
Map<String, String> map = [key1:'value1', key2: 'value2']

// 存取
aMap.keyName
aMap['keyName']
aMap.anotherkey = "i am map"
aMap.anotherkey = [a: 1, b: 2]



// Create a tree using a map
tree = fileTree(dir: 'src', include: '**/*.java')
tree = fileTree(dir: 'src', includes: ['**/*.java', '**/*.xml'])
tree = fileTree(dir: 'src', include: '**/*.java', exclude: '**/*test*/**')

//Add include and exclude patterns to the tree
tree.include '**/*.java'
tree.exclude '**/Abstract*'


// Groovy will coerce named arguments into a single map argument  
//实际上，它会把参数 'java' 转换成一个 map
apply plugin: 'java'



// 遍历
def result = ""
[a:1, b:2].each { key, value -> 
    result += "$key$value" 
}
// 带索引遍历(从0开始的计数器，两个参数时传递的Map.Entry对象)
[a:1, b:2].eachWithIndex { entry，index, -> 
    result += "$entry$index" 
}

[a:1, b:2].eachWithIndex  { key, value，index, -> 
    result += "$key$value$index" 
}

// 分组
def group = students.groupBy { def student ->
    return student.value.score >= 60 ? '及格' : '不及格'
}

/* Range，范围，对List的一种扩展 */
def range = 1..5
println(range)  //输出：[1, 2, 3, 4, 5]
range.size()  // 长度
range.iterator() // 迭代器
def s1 = range.get(1)   // 获取标号为1的元素
range.contains(5)  // 是否包含元素5
range.last()    // 最后一个元素
range.remove(1) // 移除标号为1的元素
range.clear()   // 清空列表
println("第一个数据: "+range.from) //第一个数据
println("最后一个数据: "+range.to)   //最后一个数据

```


# 三、 组成

- Everything in Gradle sits on top of two basic concepts: **projects** and **tasks**

Every Gradle build is made up of one or more projects
Each project is made up of one or more tasks


## 3.1、Project

This interface is the main API you use to interact with Gradle from your build file。

更多API可进入Project类中自行查看

## 3.2、Task

A <code>Task</code> represents a single atomic piece of work for a build
Each task belongs to a {@link Project}

### 3.2.1 创建一个task
create a task :
```
 task myTask
 task myTask { configure closure }
 task myTask(type: SomeType)
 task myTask(type: SomeType) { configure closure }
```

### 3.2.2 doFirst 和 doLast

**A Task is made up of a sequence of {@link Action} objects**. 
When the task is executed, each of the actions is executed in turn, by calling {@link Action#execute}. 
You can add actions to a task by calling {@link #doFirst(Action)} or {@link #doLast(Action)}

```
task test {
    doFirst {
        println 'doFirst'
    }

    println("hello ")

    doLast {
        println 'doLast'
    }
}
```

我们定义一个名为 test 的 task, 当我们执行任意的 gradle 命令时，里面的 println("hello ") 都会执行。
所以，**我们自定义的操作一定要放在 doFirst 或者 doLast 中**

另外，doFirst 和 doLast的执行顺序是，先执行 task 里面的代码，然后再执行 doFist,最后是 doLast
也就是说 doFirst 和 doLast 的执行顺序是在 task 里面的代码 之后的

原理：
```
doFirst ->  map.add(0,action)
doLast ->   map.add(action)
```

注意： 在自定义task有type时，doFirst 和 doLast 里面的代码不执行，具体原因未知，待解决
```
task copySourceCode(type: Copy) {
    doLast{  //这里不执行，去掉doLast后能正常执行
       from "/src/main/java"
       into "/build/source"
    }
}
```












# 四、Gradle 常用命令
```
# 命令结构
gradle [taskName...] [--option-name...]

# 增量编译：同一个项目中, 同一个 task除非有必要, 否则不会被无意义的执行多次；
# 缓存：无论是否在同一个项目，只要Task输入没变就复用缓存结果，不必真的执行task；

# Tasks执行
gradle myTask   # 执行某个Task
gradle :my-subproject:taskName  # 执行子项目中的Task
gradle my-subproject:taskName   # 同上，不指定子项目，会执行所有子项目的此Task，如gradle clean；
gradle task1 task2  # 运行多个Task
gradle dist --exclude-task test # 将某个task排除在执行外
gradle dist -x test # 同上
gradle test --rerun-tasks   # 强制执行UP-TO-DATE的Task，即不走增量编译，执行全量编译；
gradle test --continue  # 默认情况下，一旦Task失败就会构建失败，通过此参数可继续执行；

# 常见任务(和插件间的Task约定)
gradle build
gradle run
gradle check
gradle clean    # 删除构建目录

# 构建细节
gradle projects # 列出所有子项目
gradle tasks    # 列出所有Task(分配给任务组的Task)
gradle tasks --group="build setup"  # 列出特定任务组的Task
gradle tasks --all  # 列出所有Task
gradle -q help --task libs  # 查看某个Task的详细信息
gradle myTask --scan    # 生成可视化的编译报告
gradle dependencies # 列出项目依赖
gradle -q project:properties    # 列出项目属性列表

# 调试选项
-?，-h，--help  # 帮助信息
-v，--version   # 版本信息
-s, --stacktrace    # 打印出异常堆栈跟踪信息；
-S, --full-stacktrace   # 比上面更完整的信息；

# 性能相关
--build-cache   # 复用缓存
--no-build-cache    # 不复用缓存，默认
--max-workers   # 最大处理器数量
--parallel  # 并行生成项目
--no-parallel   # 不并行生成项目
--priority  # Gradle启动的进程优先级
--profile   # 生成性能报告


# 守护进程
--daemon # 使用deamon进程构建
--no-daemon # 不使用deamon进程构建
--foreground    # 前台进程启动deamon进程
--status    # 查看运行中和最近停止的deamon进程；
--stop  # 停止所有同一版本的deamon进程；

# 日志选项
-q, --quiet # 只记录错误
-w, --warn
-i, --info 
-d, --debug
--console=(auto,plain,rich,verbose) # 指定输出类型
--warning-mode=(all,fail,none,summary)  # 指定警告级别

# 执行选项
--include-build # 复合构建
--offline   # 离线构建
--refresh-dependencies  # 强制清除依赖缓存
--dry-run # 在不实际执行Task的情况下看Task执行顺序
--no-rebuild # 不重复构建项目依赖

# 环境选项
-b, --build-file # 指定构建文件
-c, --settings-file # 指定设置文件
-g, --gradle-user-home  # 指定默认.Gradle目录
-p, --project-dir   # 指定Gradle的开始目录
--project-cache-dir # 指定缓存目录，默认.gradle
-D, --system-prop   # 设置JVM系统属性
-I, --init-script   # 指定初始化脚本
-P, --project-prop  # 指定根项目的项目属性；
```




