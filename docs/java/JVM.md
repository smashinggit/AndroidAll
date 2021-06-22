[doc]
# JVM


#

我们都知道Java是跨平台的，一次编译，到处运行，同一套Java代码可以在Windows、Linux、Mac上运行，
背后依赖于不同平台/版本的 JVM（Java虚拟机），
Java代码编译后生成 .class 字节码文件，再由JVM翻译成特定平台的 机器码，然后运行。


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
 
 
 # 用 ProGuard 还是 R8?
 如果没有历史包袱，直接R8，毕竟兼容绝大部分的ProGuard规则，更快的编译速度，对Kotlin更友好。
 
 - ProGuard →  压缩、优化和混淆Java字节码文件的免费工具，
 - R8 →  ProGuard的替代工具，支持现有ProGuard规则，更快更强，AGP 3.4.0或更高版本，默认使用R8混淆编译器。
 
 
 使用ProGuard或R8构建项目会在 build\outputs\mapping\release 输出下述文件：
 
- mapping.txt → 原始与混淆过的类、方法、字段名称间的转换；
- seeds.txt → 未进行混淆的类与成员；
- usage.txt → APK中移除的代码；
- resources.txt → 资源优化记录文件，哪些资源引用了其他资源，哪些资源在使用，哪些资源被移除；
 
 
 

 



