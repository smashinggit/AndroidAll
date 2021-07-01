# 混淆
(文章地址)[https://juejin.cn/post/6966526844552085512#heading-0]


注意：
在打jar/aar，开启混淆后，会发现打的jar/aar里面没有代码
这是因为AS默认会把无用的包给清理掉，所以打的jar/aar包里面的文件应该是因为这个而被清理掉了

解决办法是
在module的proguard-rules.pro文件中添加一条指令 -dontshrink
```
-dontshrink   # 关闭压缩
```


# 一、混淆规则详解


## 1.1 混淆设置参数

```
-optimizationpasses 5                       # 代码混淆的压缩比例，值介于0-7，默认5
-verbose                                    # 混淆时记录日志
-dontoptimize                               # 不优化输入的类文件
-dontshrink                                 # 关闭压缩
-dontpreverify                              # 关闭预校验(作用于Java平台，Android不需要，去掉可加快混淆)
-dontoptimize                               # 关闭代码优化
-dontobfuscate                              # 关闭混淆
-ignorewarnings                             # 忽略警告
-dontwarn com.squareup.okhttp.**            # 指定类不输出警告信息
-dontusemixedcaseclassnames                 # 混淆后类型都为小写
-dontskipnonpubliclibraryclasses            # 不跳过非公共的库的类
-printmapping mapping.txt                   # 生成原类名与混淆后类名的映射文件mapping.txt
-useuniqueclassmembernames                  # 把混淆类中的方法名也混淆
-allowaccessmodification                    # 优化时允许访问并修改有修饰符的类及类的成员
-renamesourcefileattribute SourceFile       # 将源码中有意义的类名转换成SourceFile，用于混淆具体崩溃代码
-keepattributes SourceFile,LineNumberTable  # 保留行号
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod # 避免混淆注解、内部类、泛型、匿名类
-optimizations !code/simplification/cast,!field/ ,!class/merging/   # 指定混淆时采用的算法

```

## 1.2 保持不被混淆的设置

语法组成：
```
[保持命令] [类] {
    [成员] 
}
```

保持命令：
```
-keep                           # 防止类和类成员被移除或被混淆；
-keepnames                      # 防止类和类成员被混淆；
-keepclassmembers	            # 防止类成员被移除或被混淆；
-keepclassmembernames           # 防止类成员被混淆；
-keepclasseswithmembers         # 防止拥有该成员的类和类成员被移除或被混淆；
-keepclasseswithmembernames     # 防止拥有该成员的类和类成员被混淆；
```

类：
- 具体的类
- 访问修饰符 → public、private、protected
- 通配符(*) → 匹配任意长度字符，但不包含包名分隔符(.)
- 通配符(**) → 匹配任意长度字符，且包含包名分隔符(.)
- extends → 匹配实现了某个父类的子类
- implements → 匹配实现了某接口的类
- $ → 内部类

成员：
- 匹配所有构造器 → <init>
- 匹配所有域 → <field>
- 匹配所有方法 → <methods>
- 访问修饰符 → public、private、protected
- 除了 * 和 ** 通配符外，还支持 *** 通配符，匹配任意参数类型
- ... → 匹配任意长度的任意类型参数，如void test(...)可以匹配不同参数个数的test方法

常用自定义混淆规则范例：

```
# 不混淆某个类的类名，及类中的内容
-keep class cn.coderpig.myapp.example.Test { *; }

# 不混淆指定包名下的类名，不包括子包下的类名
-keep class cn.coderpig.myapp*

# 不混淆指定包名下的类名，包括子包下的类名
-keep class cn.coderpig.myapp**

# 不混淆指定包名下的类名，及类里的内容
-keep class cn.coderpig.myapp* {*;}

# 不混淆某个类的子类
-keep public class * extends cn.coderpig.myapp.base.BaseFragment

# 不混淆实现了某个接口的类
-keep class * implements cn.coderpig.myapp.dao.DaoImp

# 不混淆类名中包含了"entity"的类，及类中内容
-keep class **.*entity*.** {*;}

# 不混淆内部类中的所有public内容
-keep class cn.coderpig.myapp.widget.CustomView$OnClickInterface {
    public *;
}

# 不混淆指定类的所有方法
-keep cn.coderpig.myapp.example.Test {
    public <methods>;
}

# 不混淆指定类的所有字段
-keep cn.coderpig.myapp.example.Test {
    public <fields>;
}

# 不混淆指定类的所有构造方法
-keep cn.coderpig.myapp.example.Test {
    public <init>;
}

# 不混淆指定参数作为形参的方法
-keep cn.coderpig.myapp.example.Test {
    public <methods>(java.lang.String);
}

# 不混淆类的特定方法
-keep cn.coderpig.myapp.example.Test {
    public test(java.lang.String);
}

# 不混淆native方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 不混淆枚举类
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 不混淆自定义控件
-keep public class * entends android.view.View {
    *** get*();
    void set*(***);
    public <init>;
}

# 不混淆实现了Serializable接口的类成员，此处只是演示，也可以直接 *;
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 不混淆实现了parcelable接口的类成员
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 注意事项：
# 
# ① jni方法不可混淆，方法名需与native方法保持一致；
# ② 反射用到的类不混淆，否则反射可能出问题；
# ③ 四大组件、Application子类、Framework层下的类、自定义的View默认不会被混淆，无需另外配置；
# ④ WebView的JS调用接口方法不可混淆；
# ⑤ 注解相关的类不混淆；
# ⑥ GSON、Fastjson等解析的Bean数据类不可混淆；
# ⑦ 枚举enum类中的values和valuesof这两个方法不可混淆(反射调用)；
# ⑧ 继承Parceable和Serializable等可序列化的类不可混淆；
# ⑨ 第三方库或SDK，请参考第三方提供的混淆规则，没提供的话，建议第三方包全部不混淆；

```

混淆规则的叠加:

上面日常使用的创建的代码示例，proguard-rules.pro没有配置混淆规则，却混淆了？
其实是因为 **混淆规则是叠加的**，而混淆规则的来源不止主模块里的proguard-rules.pro，
还有这些：

- <module-dir>/proguard-rules.pro
不止主模块有proguard-rules.pro，子模块也可以有，因为规则是叠加的，故某个模块的配置都可能影响其它模块

- proguard-android-optimize.txt
AGP编译时生成，其中包含了对大多数Android项目都有用的规则，并且启用 @Keep* 注解。
AGP提供的规则文件还有proguard-defaults.txt或proguard-android.txt，
可通过 getDefaultProguardFile 进行设置，不过建议还是使用这个文件(多了些优化配置)。

- <module-dir>/build/intermediates/proguard-rules/debug/aapt_rules.txt
自动生成，AAPT2会根据对应用清单中的类、布局及其他应用资源的引用，生成保留规则，如不混淆每个Activity

- AAR库 → <library-dir>/proguard.txt

- Jar库 → <library-dir>/META-INF/proguard/



如果想查看所有规则 叠加后的混淆规则，可在主目录的 proguard-rules.pro 添加下述配置：
```
# 输出所有规则叠加后的混淆规则
-printconfiguration ./build/outputs/mapping/full-config.txt
```



资源压缩:
资源压缩其实分为两步：**资源合并** 与 **资源移除**，前者无论是否配置 shrinkResources true，AGP构建APK时都会执行，
当存在两个或更多名称相同的资源才会进行资源合并，AGP会从重复项中选择 优先级更高 的文件，并只将此资源传递给AAPT2，
以供在APK中分发


**开启资源压缩后，所有未被使用的资源默认会被移除**，
如果你想定义那些资源需要保留，可以在 res/raw/ 路径下创建一个xml文件，如 keep.xml，
配置示例如下 (此文件不会打包到APK中，支持通配符*，此类文件可有多份)：

```
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    <!-- 定义哪些资源要被保留 -->
    tools:keep="@layout/l_used*_c,@layout/l_used_a,@layout/l_used_b*"
    <!-- 定义哪些资源需要被移除 -->
    tools:discard="@layout/unused2"
    <!-- 开启严苟模式，可选值strict,safe，前者严格按照keep和discard指定的资源保留 -->
    <!-- 后者保守删除未引用资源，如代码中使用Resources.getIdentifier()引用的资源会保留 -->
    tools:shrinkMode="strict"/>
```

另外，还可以在build.gradle中添加 resConfigs 来移除不需要的备用资源文件，如只保留中文：
```
android {
    ...
    defaultConfig {
        resConfigs "zh-rCN" // 不用支持国际化只需打包中文资源
    }
}    
```

# 二、JVM、D8、R8

JVM的内部组成图如下：

![jvm架构图](/pics/java/jvm/jvm架构图.png)


 - 类加载器
 加载编译后的.class，链接、检测损坏的字节码，定位并初始化静态变量及静态代码
 
 - 运行时数据
 栈、堆、方法变量等；
 
 - 执行引擎
 执行已经加载的代码、清理生成的所有垃圾(gc)；
 
 运行程序时，Interpreter(解释器) 会将字节码解释为机器码然后运行，当发现有重复执行的代码时，会切换为 JIT编译器。
 JIT编译器会将重复的代码编译为本地机器码，当同样的方法被调用时，直接运行本地机器码，从而提高系统性能
 
 
 ![解释器的工作流程](/pics/java/jvm/解释器的工作流程.png)
 
 JVM的设计是面向无限电量/存储的设备，Android设备与之相比，太弱鸡了(电量、内存大小、存储等小的可怜)。
 
 不能直接使用，于是Google自己设计了一套用于Android平台的Java虚拟机——Dalvik，支持已转换为 .dex (Dalvik Executable)
  压缩格式的Java应用程序的运行。
  
 与JVM字节码基于栈不同，Dalvik基于寄存器(变量都存储在寄存器中)，后者更加高效且需要更少的空间。
 
 .java和.kt代码文件被Java、Kotlin编译器协作编译为.class，而后编译为.dex文件，最后打包到.apk文件中。
 
 把APK安装到设备上，当点击应用图标时，系统会启动一个新的Dalvik进程，并将应用包含的dex代码加载进来，
 在运行时交由Interpreter或JIT编译，然后就可以看到应用的界面了
 
 
 ![Dalvik工作流程](/pics/java/jvm/Dalvik工作流程.png)
 
 在Dalvik中，应用的每次运行都需要执行编译操作，而这段时间是计入程序的执行时间，所以程序的启动速度会有点慢，
 当然也有好处， 应用安装速度快。
 
 在Android 4.4.4后，Google开始引入 Dalvik 的替代品——ART，从JIT(Just In Time，即时编译) 到 
 AOT (Ahead-Of-Time，预编译)，应用在首次安装时用dex2oat将 dex 编译为 .oat 二进制文件。
 

 ![dex转oat](/pics/java/jvm/dex转oat.png)
 
 
 点击应用图标启动时，ART直接加载.oat文件并运行，启动速度明显提升，避免了重复编译，减少了CPU的使用频率，也降低了功耗，
 当然缺点也是有的：更长的应用安装时间 和 更大的存储空间占用。
 
 
 

 Android虚拟机采用 基于寄存器的指令集(opcodes)，这样会存在一个问题，更高版本Java新引入的语法特性不能在上面直接使用。
 
 为了让我们能使用上Java 8的特性，Google使用 Transformation 来增加了一步编译过程 → 脱糖(desugaring)。
 
 > 当使用当前Android版本不支持的高版本jdk语法时，在编译期转换为其支持的低版本jdk语法
 
 ![脱糖的发展](/pics/java/jvm/脱糖的发展.png)
 
 相信你对ProGuard、DX、D8和R8在混淆过程中起的作用有了一个基础的认知~
 
 
 ## 用 ProGuard 还是 R8?
 如果没有历史包袱，直接R8，毕竟兼容绝大部分的ProGuard规则，更快的编译速度，对Kotlin更友好。
 
 - ProGuard →  压缩、优化和混淆Java字节码文件的免费工具，
 - R8 →  ProGuard的替代工具，支持现有ProGuard规则，更快更强，AGP 3.4.0或更高版本，默认使用R8混淆编译器。
 
 
 使用ProGuard或R8构建项目会在 build\outputs\mapping\release 输出下述文件：
 
- mapping.txt → 原始与混淆过的类、方法、字段名称间的转换；
- seeds.txt → 未进行混淆的类与成员；
- usage.txt → APK中移除的代码；
- resources.txt → 资源优化记录文件，哪些资源引用了其他资源，哪些资源在使用，哪些资源被移除；
 
 D8的作用：
 **脱糖 + 将.class字节码转换成dex **
 
 R8的作用：
 - Java代码压缩
   1. 代码删除：通过语法树静态分析技术、发现并删除未使用的代码，如未实例化的Class等；
   2. 代码优化：对运行时代码进行优化，删除死代码、未使用的参数，选择性内联、类合并等；
   3. 代码混淆：优化标识符名字，减少代码量，会判断混淆规则中是否允许修改标识符名字；
   4. 行号重新映射 等。

 - 资源压缩
 
 
 
 
# 三、自定义混淆字典

之前在反编译人家的APP时看到标识符竟然不是abcd，而是中文和特殊字符，
怎么做到的呢？其实不难，自定义一个混淆字典就好，

在app的proguard-rules的同级目录创建一个文件，比如 dictionary，内容示例如下：
```
﹢
﹣
×
÷
...太长省略

```


接着在 proguard-rules 添加下述配置：
```
-obfuscationdictionary ./dictionary
-classobfuscationdictionary ./dictionary
-packageobfuscationdictionary ./dictionary
```




 
 
# 四、关于AAR混淆

Android 的打包顺序：
1. 分别打包各个的 library module（module 混淆）
2. 整合解压所有的依赖，包括本地的依赖和远程的依赖，和 application module 一起打整包（整体混淆）


**面对不同的情况，应该要有不同的混淆策略**：

- 如果是本地的 library module：
 **可以选择不在 library 做混淆，而只做全局的混淆**，
 这样就不会出现上文的 module 相互引用找不到类的情况，并且只需要维护一份配置文件；
 
- 如果是输出到外部的 SDK，一般又分两种：
  1. 闭源的：
    例如高德 SDK，友盟 SDK 等，**一般会做 AAR 的混淆，但是会隔离出一个包或者一个类专门提供 API**
    （也就是说明文档里的 API），这个包/类会 keep 住，但是大量的具体的实现会实施混淆，并且尽量
     把一些敏感内容、算法用 JNI 等方式去做调用
     
  2. 开源的：
     例如 Github 上的各类开源库，一般不做 AAR 的混淆，但是会提供一个 consumerProguardFiles 的配置项，
     用以保证库代码的关键部分不被混淆，如下（参考官方用户指南）









