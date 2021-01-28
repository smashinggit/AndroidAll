[toc]

# NDK
[https://blog.csdn.net/qq_41979349/article/details/106053194](https://blog.csdn.net/qq_41979349/article/details/106053194)

NDK全称Native Development Kit，是Android的一个工具开发包，能够快速开发C,C++的动态库，并自动将so和应用打包成APK。
而NDK的使用场景就是通过NDK在Android中使用JNI，
那么JNI又是啥呢？JNI全称是Java Native Interface，即Java的本地接口，
JNI可以使得Java与C，C++语言进行交互。
这么一来，通过NDK和JNI，就可以很方便的在Android的开发环境中使用c,c++的开源库

#  arm64-v8a、armeabi-v7a、x86、x86_64

arm 架构注重的是续航能力:大部分的移动设备
x86 架构注重的是性能:大部分的台式机和笔记本电脑

arm64-v8a :第8代、64位ARM处理器
armeabi-v7a :第7代及以上的 ARM处理器
x86：x86 架构的 CPU（Intel 的 CPU）
x86_64:x86 架构的64位 CPU（Intel 的 CPU）


# 配置输出的.os架构类型
  
可以通过app下的build.gradle来指定编译的.so类型
注意只有这四种类中，以前很多项目中存在abiFilters 'armeabi'但现在会崩

```
android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                abiFilters 'armeabi-v7a', 'arm64-v8a'
            }
        }
    }
```

这样清一下项目，再编译出来的只有'armeabi-v7a', 'arm64-v8a'
此时运行到模拟器上，会发现找不到类库，则说明模拟器去X86的。
运行到真机无误，则说明真机是arm的


# .so文件是什么?

如果说.dll估计你会说：哦，好像见过。
其实.so和.dll并没有本质的区别，它们都是一个C++实现的功能团。
只不过.so是用在linux上的，.dll是用在Windows上的。

如今操作系统三足鼎立，当然少不了MacOS,类似的在MacOS中有.dylib文件。
它们都是 C++ 的动态链接库(Dynamic Link Library )

而Android作为Linux的一员，C++ 编译出的.so便是顺理成章。
那如何将C++编译成.so库?这便是NDK在做的事，也是上面在做的事。
打包时gradle会将对应的.so包打到apk里,然后.so就能在linux里愉快的玩耍了。







C++与Java的相互作用,就是Java进行输入,经C++转化将有价值的东西传给Java端



# 二、CMake的方式编译生成so库

## 2.1 新建Native C++工程
## 2.2 分析AS创建和添加的文件

当点击Finish后，Android Studio会自动添加NDK开发相关的文件。cpp是AS帮我们自动生成的，里面有两个文件：
- CMakeLists.text：构建脚本
- Native-lib.cpp：示例C++源文件


## 2.3 CmakeLists中的配置

```
cmake_minimum_required(VERSION 3.4.1)
# 这里会把 native-lib.cpp转换成共享库，并命名为 native-lib
add_library( # 库的名字
        native-lib

        # 设置成共享库
        SHARED

        # 库的源文件(由于native-lib.app和CMakeLists.txt同处于一个包，因此可以直接写文件名
        # 不然的话需要写成src/main/cpp/native-lib.cpp)
        native-lib.cpp)

# 如果需要使用第三方库，可以使用 find-library来找到
find_library( # so库的变量路径名字，在关联的时候使用
        log-lib

        # 你需要关联的so名字
        log)

# 通过link将源文件的库和第三方库添加进来
target_link_libraries( 
        # 源文件库的名字
        native-lib

        # 添加第三方库的变量名 需要注意的是第三库是路径变量名，因此需要用${}方式引用
        ${log-lib})
```


## MainActivity
```
public class MainActivity extends AppCompatActivity {

    //加载so库
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView tv = findViewById(R.id.sample_text);
        //直接调用native方法
        tv.setText(stringFromJNI());
    }

    //native方法
    public native String stringFromJNI();
}
```

在这里我们验证了native-lib.cpp里面方法的命名格式，在MainActivity确实有一个stringFromJNI的方法。
在这里我们首先需要加载so库，so库的名称就是我们在CmakeList.txt定义的库的名字。
然后通过定义的native方法就可以调用C++层的Java_com_example_ndkdemo_MainActivity_stringFromJNI方法


# 三、 自己编写的so库

## 3.1 创建Java对应的加载类

在这里我们不准备在MainActivity中加载.so库，而是新建了一个JNI工具类来完成加载.so库和声明native方法的任务。
然后将MainActivity中的native方法复制过来，并且新建了一个helloFromJNI的方法。
另外为了在新项目中使用该so库，我们将so库的名字更改为hello,下面也会在CMakeList.txt中更改so库的名称。
(可以发现下面的native方法是红色的，这是因为我们还没有在C++层中实现这两个方法)

## 3.2 添加需要的C/C++文件

我们直接在cpp中新建一个就行，cpp->右键->new->c/c++ source File。
然后就可以命名一个c/c++文件了，并且勾选create an associated header，表示在创建才C/C++文件的同时会创建对应的头文件。

(1) 编写头文件hello.h，**你可以将头文件看成Java的接口**，在这里我们需要声明方法。
```
#ifndef NDK_HOST_H
#define NDK_HOST_H

//声明接口
extern const char *getHost();

#endif //NDK_HOST_H
```
(2) 然后在hello.cpp中实现这个头文件，可以发现在这里我们只是简单的返回了一个hello world的字符串
```
#include <jni.h>
#include <string>
#include "host.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ndk_JNI_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern const char *getHost() {
    return "http://www.baidu.com";
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ndk_JNI_getHost(
        JNIEnv *env,
        jobject /* this */) {
    std::string host = getHost();
    return env->NewStringUTF(host.c_str());
}
```

## 3.3 在native-lib中引入hello.h头文件
这个操作跟Java中的导包有点类似，并且我们新建了一个在前面JniUtil中声明的native方法，
注意的是由于我们将加载so库和声明native方法都放到了JniUtil中，因此我们需要更改之前stringFromJNI的包名和方法名



## 3.4 CMakeList.txt中加入hello.cpp的路径添加

这里需要注意的是，**如果是多次使用add_library，则会生成多个so库**。
在这里我们只是将多个本地文件编译到一个so库中，
因此只需要在原本的add_library中添加hello的相对路径。
并且为了方便在新项目中使用该so库，在这里我将之前native-lib的名字改成了hello。
因此生成so库的时候也会生成libhello.so文件（生成so库的时候会自动加上lib的前缀）
```
cmake_minimum_required(VERSION 3.10.2)

project("ndk")

# 这里会把 native-lib.cpp转换成共享库，并命名为 native-lib
# 注意,如果是多次使用add_library，则会生成多个so库
add_library( # Sets the name of the library.
             # 库的名字
             host

             # Sets the library as a shared library.
             # 设置成共享库
             SHARED

             # Provides a relative path to your source file(s).
             # 库的源文件(由于native-lib.app和CMakeLists.txt同处于一个包，因此可以直接写文件名
             # 不然的话需要写成src/main/cpp/native-lib.cpp)
              host.cpp
            )

# 如果需要使用第三方库，可以使用 find-library来找到
find_library( # Sets the name of the path variable.
              # so库的变量路径名字，在关联的时候使用
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              # 你需要关联的so名字
              log )

# 通过link将源文件的库和第三方库添加进来
target_link_libraries( # Specifies the target library.
                       # 源文件库的名字
                       host

                       # Links the target library to the log library
                       # included in the NDK.
                       # 添加第三方库的变量名 需要注意的是第三库是路径变量名，因此需要用${}方式引用
                       ${log-lib} )
```



## 3.5 在MainActivity中使用调用JniUtil中的native方法

```
//JNI
object JNI {

    init {
        System.loadLibrary("host")
    }

    external fun getHost(): String
    external fun stringFromJNI(): String

}


// MainActivity
findViewById<TextView>(R.id.sample_text).text = "从so中读取到的host: ${JNI.getHost()}"

```




# 四、使用CMake引入第三方so库

通常情况下，引入第三方.so库会有两种场景：

- JNI规范的so。比如返回的是JNI直接支持的类型，比如说上述NdkDemo中的native-lib.cpp中的两个方法
- 只提供.so库和头文件。第三方共享.so库一般情况下只提供.so文件和头文件，就是没有将C++文件直接暴露给JAVA层，
  也没有编写JNI方法的C++文件，比如上述的hello.cpp，这个C++文件中的方法并不是JNI直接支持的类型。
  
在实际开发中，更常见的是第二种场景。
两种场景的引入方法不同，第一种可以直接引入第三方so库，
而第二种需要引入自己的so库，然后将自己的so库与第三方so库和头文件进行相关联。

接下来我们就来分析这两种引入方式。

## 4.1 引入JNI规范的so

1. 新建一个普通的Android项目。引入JNI规范的.so库并不需要Native C++类型的项目。
   
2. 在main中新建一个jniLibs。我们在app->src->main中新建立一个jniLibs，然后将上面生成的libhello.so文件拷贝过来
，这里我们直接将上面cmake->debug->obj中的四个文件夹都拷贝过来
3. 新建一个JniUtil类。注意包名和类名都要跟引入so库中的暴露的JNI方法中的一致，接着就是加载hello这个so库，
然后声明native方法，这个native方法就是hello.so库中暴露的JNI方法。
其实你会发现这个JniUtil中的代码跟上述的NdkDemo中的是一样的。

可以发现引入JNI规范的.so库是很简单的，因为我们知道hello.so库中接口方法的命名方式，
不用CMake,不用编写C++文件，直接在JniUtil中声明native方法，然后运行即可。




## 4.2 引入第三方so库和头文件

这里我们使用场景就是，在Java层中要调用hello.so库中hello.cpp中的helloWorld方法


1. 新建一个Native C++工程。
**因为引入这种类型的so库，我们需要创建自己的so文件，然后在自己的so文件里再调用第三方so，最后在Java层中调用自己的so**，
因此需要进行NDK开发。
而上面我们已经分析了新建Native C++工程AS帮我们建立和修改的文件，因此如果你想在原有的项目中进行NDK开发的话，
其实就是自己手动增加和修改这些文件即可

2. 新增文件夹，用来存放要导入的第三方so库以及头文件。
主要是在cpp文件中新建include文件和在main中新建jniLibs，
然后将第三方的头文件放在include中，第三方so库放入jniLibs中。

3. 配置CMakeLists.txt。我们需要关联第三方头文件到native-lib,并配置好第三方so库以及头文件导入的路径。
这里需要注意的是set_target_properties这里配置的so库目录，你可以利用message打印，so库的路径是否正确，
CMAKE_SOURCE_DIR代表着CMakeLists.txt的路径，由于我的CMakeLists.txt在cpp中，
因此需要加上/…进行回退到上一级的main目录，然后配置libhello.so的相对路径。

```
cmake_minimum_required(VERSION 3.4.1)

# 利用这个打印路径
message("******************************************************************")
message("CMAKE_SOURCE_DIR=${CMAKE_SOURCE_DIR}")
message("******************************************************************")
# 这里会把 native-lib.cpp转换成共享库，并命名为 native-lib
add_library( # 库的名字
        native-lib

        # 设置成共享库
        SHARED

        # 库的源文件(由于native-lib.app和CMakeLists.txt同处于一个包，因此可以直接写文件名
        # 不然的话需要写成src/main/cpp/native-lib.cpp)

        native-lib.cpp)

# 如果需要使用第三方库，可以使用 find-library来找到
find_library( # so库的变量路径名字，在关联的时候使用
        log-lib

        # 你需要关联的so名字
        log)

#将native-lib关联到第三方库头文件
#由于我的inclue目录与CMakeList都在cpp目录，因此可以直接写include,否则需要写相对目录
include_directories(include)
#导入第三库，不同到第三方库需要分开导入，因为有4个so库需要导入，因此需要4次
add_library(hello SHARED IMPORTED)
#设置导入第三库名称，目标位置
set_target_properties(hello
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jnilibs/${ANDROID_ABI}/libhello.so)

# 通过link将源文件的库和第三方库添加进来
target_link_libraries(
        # 源文件库的名字
        native-lib
        #第三方库的名称
        hello

        # 添加第三方库的变量名
        ${log-lib})
```

4. 新建JniUtil用于加载so库和声明native方法。在引入JNI规范的so库时，我们特别强调了该类要与hello.so库中的JniUtil包名，
类名要一致。而在这里并不需要，因为在这里我们并不是引入hello.so库，而是引入自己的so库（native-lib）
，我们只是为了方便管理，然后取JniUtil。

5. 在native-lib.cpp中引入第三方头文件（hello.h）。在这里我们引入了hello.h的头文件，然后实现了对外的JNI方法，
在该方法中我们引用了第三方库中的hello.cpp中的helloWorld方法，而这也就是我们引入第三方so库和头文件的最终目的。


# 踩坑

1. 在引入JNI规范的.so库时一定要记得包名，类名要和引入的so库中的一致，
不然运行时会报No implementation found for java.lang.String com.example…之类的错误，然后闪退

2. 在引入第三so库的时候，如果你将so库放在src/main/jniLibs时，可以不在项目的build.gradle中配置so库路径，
因为AS默认加载so库的路径就是src/main/jniLibs。
但是如果放在其他地方的时候，或者不取名jniLibs时，比如我们放在了src/main/jniLib,这时候就得在build.gradle中配置

3. 引入第二种so库和头文件中配置CMakeList.txt的时候，我们会通过set_target_properties来设置目标so库的路径，
网上大部分的教程配置的路径都是：${CMAKE_SOURCE_DIR}/jnilibs/${ANDROID_ABI}/so库完整名字.so，
但其实是要看具体情况的，如果你编译或运行的时候出现了类似下面这种的错误，那么大概率是由于so库的路径配置错误导致的

这时候我们可以利用message来打印${CMAKE_SOURCE_DIR}/jnilibs/${ANDROID_ABI}/so库完整名字.so这个路径，
然后对比一下你引入第三方so库的位置，就可以进行判断是否路径配置错误。

添加打印信息后我们进行编译，如果编译错误的话，应该能够在build中看到打印的message信息，如果看不到的话，
可以查看app->.cxx->cmake->debug->随便一个机型->build_output.txt中的打印信息，然后对比你引入第三方库的位置。
因为我的jnilibs是放在main层,所以这个路径明显是错误的，因此需要在${CMAKE_SOURCE_DIR}加上/…进行回退一个目录，
即最终的目录应该为${CMAKE_SOURCE_DIR}/../jnilibs/${ANDROID_ABI}/libhello.so

如果你确定你的路径配置没有错误，那么你也可以看看报错的so库位置，也有可能是因为你运行的环境缺少了相关的机型
，比如在虚拟机中运行需要x86的环境，而你引入so库的时候没有将x86的so库导进来，
或者是说你的手机运行需要arm64-v8a或armeabi-v7a的环境，但是你没有引入对应的环境，
也有可能missing and no known rule to make it的错误，所以最好是将所有机型的so库文件拷贝过来。


4. 






