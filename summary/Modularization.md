[toc]

# 组件化


# 一、组件化的理解

## 1.1 模块化

在Android Studio中，新建工程默认有一个App module，然后还可以通过File->New->New Module 新建module。
那么这里的“module” 实际和我们说的“模块”基本是一个概念了

原本一个 App模块 承载了所有的功能，而**模块化就是拆分成多个模块放在不同的Module里面**，
每个功能的代码都在自己所属的 module 中添加

另外通常还会有一个通用基础模块module_common，提供BaseActivity/BaseFragment、图片加载、网络请求等基础能力，
然后每个业务模块都会依赖这个基础模块


那么业务模块之间有没有依赖呢？很显然是有的
例如 “首页”、“分类”、“发现”、“购物车”、“我的”，都是需要跳转到“商品详情” 的，必然是依赖“商品详情” ；
而“商品详情”是需要能添加到“购物车”能力的；
而“首页”点击搜索显然是“分类”中的搜索功能。所以这些模块之间存在复杂的依赖关系


![模块化依赖关系](/pics/android/modularization/模块化依赖关系.png)



模块化 在各个业务功能比较独立的情况下是比较合理的，但多个模块中肯定会有页面跳转、数据传递、方法调用 等情况，
所以必然存在以上这种依赖关系，即模块间有着高耦合度。

为了 解决模块间的高耦合度问题，就要进行组件化了。


## 1.2 组件化介绍 — 优势及架构

组件化，去除模块间的耦合，使得每个业务模块可以独立当做App存在，对于其他模块没有直接的依赖关系。 
此时业务模块就成为了业务组件。

而除了业务组件，还有抽离出来的业务基础组件，是提供给业务组件使用，但不是独立的业务，
例如分享组件、广告组件；还有基础组件，即单独的基础功能，与业务无关，例如 图片加载、网络请求等

组件化带来的好处 就显而易见了：
1. 加快编译速度：每个业务功能都是一个单独的工程，可独立编译运行，拆分后代码量较少，编译自然变快
2. 提高协作效率：解耦 使得组件之间 彼此互不打扰，组件内部代码相关性极高。
   团队中每个人有自己的责任组件，不会影响其他组件；降低团队成员熟悉项目的成本，只需熟悉责任组件即可；
   对测试来说，只需重点测试改动的组件，而不是全盘回归测试
3. 功能重用：组件 类似我们引用的第三方库，只需维护好每个组件，一建引用集成即可。
   业务组件可上可下，灵活多变；而基础组件，为新业务随时集成提供了基础，减少重复开发和维护工作量   
   
   
![组件化架构](/pics/android/modularization/组件化架构.png) 
   
1. 组件依赖关系是上层依赖下层，修改频率是上层高于下层。
    
2. 基础组件是通用基础能力，修改频率极低，作为SDK可共公司所有项目集成使用。

3. common组件，作为支撑业务组件、业务基础组件的基础（BaseActivity/BaseFragment等基础能力），
   同时依赖所有的基础组件，提供多数业务组件需要的基本功能，并且统一了基础组件的版本号。
   所以 业务组件、业务基础组件 所需的基础能力只需要依赖common组件即可获得。
   
4. 业务组件、业务基础组件，都依赖common组件。但业务组件之间不存在依赖关系，业务基础组件之间不存在依赖关系。
   而 业务组件 是依赖所需的业务基础组件的，例如几乎所有业务组件都会依赖广告组件 来展示Banner广告、弹窗广告等 
   
5. 最上层则是主工程，即所谓的“壳工程”，主要是集成所有的业务组件、提供Application唯一实现、gradle、manifest配置，
   整合成完备的App  
   
   
## 1.3 组件化开发的问题点    

我们了解了组件化的概念、优点及架构特点，那么要想实施组件化，首先要搞清楚 要解决问题点有哪些？

核心问题是 **业务组件去耦合**。那么存在哪些耦合的情况呢？前面有提到过，页面跳转、方法调用、事件通知。
而基础组件、业务基础组件，不存在耦合的问题，所以只需要抽离封装成库即可。

所以针对业务组件有以下问题：
1. 业务组件，如何实现单独运行调试？
2. 业务组件间 没有依赖，如何实现页面的跳转？
3. 业务组件间 没有依赖，如何实现组件间通信/方法调用？
4. 业务组件间 没有依赖，如何获取fragment实例？
5. 业务组件不能反向依赖壳工程，如何获取Application实例、如何获取Application onCreate()回调（用于任务初始化）？   
   
# 二、组件独立调试

每个 业务组件 都是一个完整的整体，可以当做独立的App，需要满足单独运行及调试的要求，这样可以提升编译速度提高效率

如何做到组件独立调试呢？有两种方案：
单工程方案，组件以module形式存在，动态配置组件的工程类型；
多工程方案，业务组件以library module形式存在于独立的工程，且只有这一个library module


## 2.1 单工程方案

### 2.1.1 动态配置组件工程类型
单工程模式，整个项目只有一个工程，它包含：App module 加上各个业务组件module，就是所有的代码，这就是单工程模式。

如何做到组件单独调试呢？
我们知道，在 AndroidStudio 开发 Android 项目时，使用的是 Gradle 来构建，Android Gradle 中提供了三种插件，
在开发中可以通过配置不同的插件来配置不同的module类型。
```
Application 插件，id: com.android.application
Library 插件，id: com.android.library
```
显然我们的 App module配置的就是Application 插件，业务组件module 配置的是 Library 插件。
想要实现 业务组件的独立调试，这就需要把配置改为 Application 插件；
而独立开发调试完成后，又需要变回Library 插件进行集成调试

如何让组件在这两种调试模式之间自动转换呢？手动修改组件的 gradle 文件，切换 Application 和 library ？
如果项目只有两三个组件那么是可行的，但在大型项目中可能会有十几个业务组件，一个个手动修改显得费力笨拙

我们知道用AndroidStudio创建一个Android项目后，会在根目录中生成一个gradle.properties文件。
在这个文件定义的常量，可以被任何一个build.gradle读取。

所以我们可以在gradle.properties中定义一个常量值 isModule，true为即独立调试；false为集成调试。
然后在业务组件的build.gradle中读取 isModule，设置成对应的插件即可。

代码如下：
```
//gradle.properties
#组件独立调试开关, 每次更改值后要同步工程
isModule = false

//build.gradle
//注意gradle.properties中的数据类型都是String类型，使用其他数据类型需要自行转换
if (isModule.toBoolean()){
    apply plugin: 'com.android.application'
}else {
    apply plugin: 'com.android.library'
}
```

### 2.1.2 动态配置ApplicationId 和 AndroidManifest

我们知道一个 App 是需要一个 ApplicationId的 ，而组件在独立调试时也是一个App，所以也需要一个 ApplicationId，
集成调试时组件是不需要ApplicationId的；另外一个 APP 也只有一个启动页， 
而组件在独立调试时也需要一个启动页，在集成调试时就不需要了

所以ApplicationId、AndroidManifest也是需要 isModule 来进行配置的。
```
//build.gradle (module_cart)
android {
...
    defaultConfig {
...
        if (isModule.toBoolean()) {
            // 独立调试时添加 applicationId ，集成调试时移除
            applicationId "com.hfy.componentlearning.cart"
        }
...
    }

    sourceSets {
        main {
            // 独立调试与集成调试时使用不同的 AndroidManifest.xml 文件
            if (isModule.toBoolean()) {
                manifest.srcFile 'src/main/moduleManifest/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/AndroidManifest.xml'
            }
        }
    }
...
}
```

moduleManifest中新建的manifest文件 指定了Application、启动activity：
```
//moduleManifest/AndroidManifest.xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.hfy.module_cart" >
    <application android:name=".CartApplication"
        android:allowBackup="true"
        android:label="Cart"
        android:theme="@style/Theme.AppCompat">
        <activity android:name=".CartActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
}
```

原本自动生成的manifest，未指定Application、启动activity：
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.hfy.module_cart">
    <application>
        <activity android:name=".CartActivity"></activity>
    </application>
</manifest>
```


## 2.2 多工程方案

多工程方案，业务组件以library module形式存在于独立的工程。独立工程 自然就可以独立调试了，不再需要进行上面那些配置了。
例如，购物车组件 就是 新建的工程Cart 的 module_cart模块，业务代码就写在module_cart中即可。
app模块是依赖module_cart。app模块只是一个组件的入口，或者是一些demo测试代码

那么当所有业务组件都拆分成独立组件时，原本的工程就变成一个只有app模块的壳工程了，壳工程就是用来集成所有业务组件的。

## 2.2.1 maven引用组件

那么如何进行集成调试呢？
使用maven引用组件：
1、发布组件的arr包 到公司的maven仓库，
2、然后在壳工程中就使用implemention依赖就可以了，和使用第三方库一毛一样。
另外arr包 分为 快照版本（SNAPSHOT） 和 正（Realease）式版本，快照版本是开发阶段调试使用，正式版本是正式发版使用。

具体如下：
首先，在module_cart模块中新建maven_push.gradle文件，和build.gradle同级目录
```
apply plugin: 'maven'


configurations {
    deployerJars
}

repositories {
    mavenCentral()
}

//上传到Maven仓库的task
uploadArchives {
    repositories {
        mavenDeployer {
            pom.version = '1.0.0' // 版本号
            pom.artifactId = 'cart' // 项目名称（通常为类库模块名称，也可以任意）
            pom.groupId = 'com.hfy.cart' // 唯一标识（通常为模块包名，也可以任意）

            //指定快照版本 maven仓库url， todo 请改为自己的maven服务器地址、账号密码
            snapshotRepository(url: 'http://xxx/maven-snapshots/') {
                authentication(userName: '***', password: '***')
            }
            //指定正式版本 maven仓库url， todo 请改为自己的maven服务器地址、账号密码
            repository(url: 'http://xxx/maven-releases/') {
                authentication(userName: '***', password: '***')
            }
        }
    }
}

// type显示指定任务类型或任务, 这里指定要执行Javadoc这个task,这个task在gradle中已经定义
task androidJavadocs(type: Javadoc) {
    // 设置源码所在的位置
    source = android.sourceSets.main.java.sourceFiles
}

// 生成javadoc.jar
task androidJavadocsJar(type: Jar) {
    // 指定文档名称
    classifier = 'javadoc'
    from androidJavadocs.destinationDir
}

// 打包main目录下代码和资源的task，生成sources.jar
task androidSourcesJar(type: Jar) {
    classifier = 'sources'
    from android.sourceSets.main.java.sourceFiles
}

//配置需要上传到maven仓库的文件
artifacts {
    archives androidSourcesJar
    archives androidJavadocsJar
}
```

maven_push.gradle主要就是发布组件ARR的配置：ARR的版本号、名称、maven仓地址账号等。
然后，再build.gradle中引用：
```
//build.gradle
apply from: 'maven_push.gradle'
```

接着，点击Sync后，点击Gradle任务uploadArchives，即可打包并发布arr到maven仓。

最后，壳工程要引用组件ARR，需要先在壳工程的根目录下build.gradle中添加maven仓库地址：
```
allprojects {
    repositories {
        google()
        jcenter()
        //私有服务器仓库地址
        maven {
            url 'http://xxx'
        }
    }
}
```

接着在app的build.gradle中添加依赖即可：
```
dependencies {
    ...
    implementation 'com.hfy.cart:cart:1.0.0'
    //以及其他业务组件
}
```

可见，多工程方案 和我们平时使用第三方库是一样的，只是我们把组件ARR发布到公司的私有maven仓而已


# 三. 页面跳转

## 3.1 方案一 ARouter
前面说到，组件化的核心就是解耦，所以组件间是不能有依赖的，那么如何实现组件间的页面跳转呢？

例如 在首页模块 点击 购物车按钮 需要跳转到 购物车模块的购物车页面，两个模块之间没有依赖，
也就说不能直接使用 显示启动 来打开购物车Activity，那么隐式启动呢？


隐式启动是可以实现跳转的，但是隐式 Intent 需要通过 AndroidManifest 配置和管理，协作开发显得比较麻烦。
这里我们采用业界通用的方式—路由

比较著名的路由框架 有阿里的[ARouter](https://github.com/alibaba/ARouter)、美团的[WMRouter](https://github.com/meituan/WMRouter)，
它们原理基本是一致的

这里我们采用使用更广泛的ARouter：“一个用于帮助 Android App 进行组件化改造的框架 —— 支持模块间的路由、通信、解耦”。


## 3.2 ARouter实现路由跳转

前面提到，所有的业务组件都依赖了 Common 组件，所以我们在 Common 组件中使用关键字**“api”**添加的依赖，业务组件都能访问。
我们要使用 ARouter 进行界面跳转，需要Common 组件添加 ARouter 的依赖（另外，其它组件共同依赖的库也要都放到 Common 中统一依赖）


### 3.2.1 引入依赖

因为ARouter比较特殊，“arouter-compiler ” 的annotationProcessor依赖 需要所有使用到 ARouter 的组件中都单独添加，
不然无法在 apt 中生成索引文件，就无法跳转成功

并且在每个使用到 ARouter 的组件的 build.gradle 文件中，其 android{} 中的 javaCompileOptions 中也需要添加特定配置。
然后壳工程需要依赖业务组件。如下所示：
```
//common组件的build.gradle
dependencies {
    ...
    api 'com.alibaba:arouter-api:1.4.0'
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.1'
    //业务组件、业务基础组件 共同依赖的库（网络库、图片库等）都写在这里~
}

//业务组件的build.gradle
android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [AROUTER_MODULE_NAME: project.getName()]
            }
        }
    }
...
}
dependencies {
...
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.1'
    implementation 'com.github.hufeiyang:Common:1.0.0'//业务组件依赖common组件
}


//壳工程app module的build.gradle
dependencies {
    ...
    //这里没有使用私有maven仓，而是发到JitPack仓，一样的意思~
//    implementation 'com.hfy.cart:cart:1.0.0'
    implementation 'com.github.hufeiyang:Cart:1.0.1' //依赖购物车组件
    implementation 'com.github.hufeiyang:HomePage:1.0.2' //依赖首页组件

    //壳工程内 也需要依赖Common组件，因为需要初始化ARouter
    implementation 'com.github.hufeiyang:Common:1.0.0'
}

```

### 3.2.2 初始化
```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            // 打印日志
            ARouter.openLog();
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this);
    }
}
```

### 3.2.3 路由跳转

好了，准备工作都完成了。并且知道 首页组件是没有依赖购物车组件的，下面就来实现前面提到的 首页组件 无依赖 跳转到 购物车组件页面。

而使用ARouter进行简单路由跳转，只有两步：添加注解路径、通过路径路由跳转。

1、在支持路由的页面上添加注解@Route(path = "/xx/xx")，路径需要注意的是至少需要有两级，/xx/xx。
这里就是购物车组件的CartActivity：
```
@Route(path = "/cart/cartActivity")
public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }
}
```

2、然后在首页组件的HomeActivity 发起路由操作—点击按钮跳转到购物车，
调用ARouter.getInstance().build("/xx/xx").navigation()即可：
```
@Route(path = "/homepage/homeActivity")
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.btn_go_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过路由跳转到 购物车组件的购物车页面（但没有依赖购物车组件）
                ARouter.getInstance()
                        .build("/cart/cartActivity")
                        .withString("key1","value1")//携带参数1
                        .withString("key2","value2")//携带参数2
                        .navigation();
            }
        });
    }
}
```

# 四、组件间通信

组件间没有依赖，又如何进行通信呢？

例如，首页需要展示购物车中商品的数量，而查询购物车中商品数量 这个能力是购物车组件内部的，这咋办呢？


## 4.1 服务暴露组件

平时开发中 我们常用 接口 进行解耦，对接口的实现不用关心，避免接口调用与业务逻辑实现紧密关联。
这里组件间的解耦也是相同的思路，仅依赖和调用服务接口，不会依赖接口的实现。

可能你会有疑问了：既然首页组件可以访问购物车组件接口了，那就需要依赖购物车组件啊，
这俩组件还是耦合了啊，那咋办啊？
答案是组件拆分出可暴露服务。见下图：

![组件暴露接口](/pics/android/modularization/组件暴露接口.png)

左侧是组件间可以调用对方服务 但是有依赖耦合。
右侧，发现多了export_home、export_cart，这是对应拆分出来的专门用于提供服务的暴露组件。

操作说明如下：
- 暴露组件 只存放 服务接口、服务接口相关的实体类、路由信息、便于服务调用的util等
- 服务调用方 只依赖 服务提供方的 露组件，如module_home依赖export_cart，而不依赖module_cart
- 组件 需要依赖 自己的暴露组件，并实现服务接口，如module_cart依赖export_cart 并实现其中的服务接口
- 接口的实现注入 依然是由ARouter完成，和页面跳转一样使用路由信息


下面按照此方案 来实施 首页调用购物车服务 来获取商品数量，更好地说明和理解。

## 4.2 实施

### 4.2.1 新建export_cart

首先，在购物车工程中新建module即export_cart，在其中新建接口类ICartService并定义获取购物车商品数量方法，
注意接口必须继承IProvider，是为了使用ARouter的实现注入：

```
/**
 * 购物车组件对外暴露的服务
 * 必须继承IProvider
 * @author hufeiyang
 */
public interface ICartService extends IProvider {

    /**
     * 获取购物车中商品数量
     * @return
     */
    CartInfo getProductCountInCart();
}


//CartInfo是购物车信息，包含商品数量：
/**
 * 购物车信息

 * @author hufeiyang
 */
public class CartInfo {

    /**
     * 商品数量
     */
    public int productCount;
}
```
接着，创建路由表信息，存放购物车组件对外提供跳转的页面、服务的路由地址：
```
/**
 * 购物车组件路由表
 * 即 购物车组件中 所有可以从外部跳转的页面 的路由信息
 * @author hufeiyang
 */
public interface CartRouterTable {

    /**
     * 购物车页面
     */
    String PATH_PAGE_CART = "/cart/cartActivity";

    /**
     * 购物车服务
     */
    String PATH_SERVICE_CART = "/cart/service";

}
```

前面说页面跳转时是直接使用 路径字符串 进行路由跳转，这里是和服务路由都放在这里统一管理。
然后，为了外部组件使用方便新建CartServiceUtil：
```
/**
 * 购物车组件服务工具类
 * 其他组件直接使用此类即可：页面跳转、获取服务。
 * @author hufeiyang
 */
public class CartServiceUtil {

    /**
     * 跳转到购物车页面
     * @param param1
     * @param param2
     */
    public static void navigateCartPage(String param1, String param2){
        ARouter.getInstance()
                .build(CartRouterTable.PATH_PAGE_CART)
                .withString("key1",param1)
                .withString("key2",param2)
                .navigation();
    }

    /**
     * 获取服务
     * @return
     */
    public static ICartService getService(){
        //return ARouter.getInstance().navigation(ICartService.class);//如果只有一个实现，这种方式也可以
        return (ICartService) ARouter.getInstance().build(CartRouterTable.PATH_SERVICE_CART).navigation();
    }

    /**
     * 获取购物车中商品数量
     * @return
     */
    public static CartInfo getCartProductCount(){
        return getService().getProductCountInCart();
    }
}
```

注意到，这里使用静态方法 分别提供了页面跳转、服务获取、服务具体方法获取。
其中服务获取 和页面跳转 同样是使用路由，并且服务接口实现类 也是需要添加@Route注解指定路径的。


### 4.2.2  module_cart的实现

首先，module_cart需要依赖export_cart：
```
//module_cart的Build.gradle
dependencies {
    ...
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.1'
    implementation 'com.github.hufeiyang:Common:1.0.0'

    //依赖export_cart
    implementation 'com.github.hufeiyang.Cart:export_cart:1.0.5'
}
```

点击sync后，接着CartActivity的path改为路由表提供：

```
@Route(path = CartRouterTable.PATH_PAGE_CART)
public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }
}
```

然后，新建服务接口的实现类来实现ICartService，添加@Route注解指定CartRouterTable中定义的服务路由
```
/**
 * 购物车组件服务的实现
 * 需要@Route注解、指定CartRouterTable中定义的服务路由
 * @author hufeiyang
 */
@Route(path = CartRouterTable.PATH_SERVICE_CART)
public class CartServiceImpl implements ICartService {

    @Override
    public CartInfo getProductCountInCart() {
        //这里实际项目中 应该是 请求接口 或查询数据库
        CartInfo cartInfo = new CartInfo();
        cartInfo.productCount = 666;
        return cartInfo;
    }

    @Override
    public void init(Context context) {
        //初始化工作，服务注入时会调用，可忽略
    }
}
```

### 4.2.3 module_home中的使用和调试

module_home需要依赖export_cart：
```
//module_home的Build.gradle
dependencies {
    ...
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.1'
    implementation 'com.github.hufeiyang:Common:1.0.0'

    //注意这里只依赖export_cart（module_cart由壳工程引入）
    implementation 'com.github.hufeiyang.Cart:export_cart:1.0.5'
}
```

在HomeActivity中新增TextView，调用CartServiceUtil获取并展示购物车商品数量：

```
@Route(path = "/homepage/homeActivity")
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //跳转到购物车页面
        findViewById(R.id.btn_go_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过路由跳转到 购物车组件的购物车页面（但没有依赖购物车组件）
//                ARouter.getInstance()
//                        .build("/cart/cartActivity")
//                        .withString("key1","param1")//携带参数1
//                        .withString("key2","param2")//携带参数2
//                        .navigation();

                CartServiceUtil.navigateCartPage("param1", "param1");
            }
        });

        //调用购物车组件服务：获取购物车商品数量
        TextView tvCartProductCount = findViewById(R.id.tv_cart_product_count);
        tvCartProductCount.setText("购物车商品数量:"+ CartServiceUtil.getCartProductCount().productCount);
    }
}

```

**另外，除了组件间方法调用，使用EventBus在组件间传递信息也是ok的（注意Event实体类要定义在export_xxx中)**





# 五、fragment实例获取

上面介绍了Activity 的跳转，我们也会经常使用 Fragment。
例如常见的应用主页HomeActivity 中包含了多个属于不同组件的 Fragment、或者有一个Fragment多个组件都需要用到。
通常我们直接访问具体 Fragment 类来new一个Fragment 实例，但这里组件间没有直接依赖，那咋办呢？答案依然是ARoute

先在module_cart中创建CartFragment：

```
//添加注解@Route，指定路径
@Route(path = CartRouterTable.PATH_FRAGMENT_CART)
public class CartFragment extends Fragment {
    ...
    public CartFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ...
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //显示“cart_fragment"
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }
}
```

同时是fragment添加注解@Route，指定路由路径，路由还是定义在export_cart的CartRouterTable中，
所以export_cart需要先发一个ARR，module_cart来依赖，然后module_cart发布ARR。

然后再module_home中依赖export_cart，使用ARouter获取Fragment实例：

```
@Route(path = "/homepage/homeActivity")
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ...
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();

        //使用ARouter获取Fragment实例 并添加
        Fragment userFragment = (Fragment) ARouter.getInstance().build(CartRouterTable.PATH_FRAGMENT_CART).navigation();
        transaction.add(R.id.fl_test_fragment, userFragment, "tag");
        transaction.commit();
    }
}
```

# 六、Application生命周期分发


我们通常会在Application的onCreate中做一些初始化任务，例如前面提到的ARouter初始化。
而业务组件有时也需要获取应用的Application，也要在应用启动时进行一些初始化任务。

你可能会说，直接在壳工程Application的onCreate操作就可以啊。
但是这样做会带来问题：因为我们希望壳工程和业务组件 代码隔离（虽然有依赖），
并且 我们希望**组件内部的任务要在业务组件内部完成**

那么如何做到 各业务组件 无侵入地获取 Application生命周期 呢？
答案是 使用AppLifeCycle插件，它专门用于在Android组件化开发中，Application生命周期主动分发到组件。

具体使用如下：

1. common组件依赖 applifecycle-api
```
    api 'com.github.hufeiyang.Android-AppLifecycleMgr:applifecycle-api:1.0.4'

```
2. 业务组件依赖applifecycle-compiler、实现接口+注解

各业务组件都要 依赖最新common组件，并添加 applifecycle-compiler 的依赖：
```
    annotationProcessor 'com.github.hufeiyang.Android-AppLifecycleMgr:applifecycle-compiler:1.0.4'
```

3. sync后，新建类来实现接口IApplicationLifecycleCallbacks用于接收Application生命周期，且添加@AppLifecycle注解
```
@AppLifecycle
public class CartApplication implements IApplicationLifecycleCallbacks {

    public  Context context;

    /**
      * 用于设置优先级，即多个组件onCreate方法调用的优先顺序
      * @return
     */
    @Override
    public int getPriority() {
        return NORM_PRIORITY;
    }

    @Override
    public void onCreate(Context context) {
        //可在此处做初始化任务，相当于Application的onCreate方法
        this.context = context;

        Log.i("CartApplication", "onCreate");
    }

    @Override
    public void onTerminate() {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int level) {
    }
}
```
实现的方法 有onCreate、onTerminate、onLowMemory、onTrimMemory。最重要的就是onCreate方法了，
相当于Application的onCreate方法，可在此处做初始化任务。
并且还可以通过getPriority()方法设置回调 多个组件onCreate方法调用的优先顺序，无特殊要求设置NORM_PRIORITY即可


3. 壳工程引入AppLifecycle插件、触发回调
   
壳工程引入新的common组件、业务组件，以及 引入AppLifecycle插件：

```

buildscript {

    repositories {
        google()
        jcenter()

        //applifecycle插件仓也是jitpack
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'

        //加载插件applifecycle
        classpath 'com.github.hufeiyang.Android-AppLifecycleMgr:applifecycle-plugin:1.0.3'
    }
}

```


#七、 老项目组件化

通常情况 我们去做组件化，都是为了改造 已有老项目。
可能老项目内部的模块之间耦合严重，没有严格的业务模块划分，并且组件化改造是大工作量的事情，且要全量回归测试，
总体来说，是需要全员参与、有较大难度的事


## 组件划分

根据前面介绍的组件化架构图，组件分为 基础组件、业务基础组件、业务组件。


- 基础组件，不用多说，就是基础功能，例如网络请求、日志框架、图片加载，
这些与业务毫无关联，可用于公司所有项目，是底层最稳定的组件。这里就比较容易识别和拆分。

- 业务基础组件，主要是供业务组件依赖使用，
例如 分享、支付组件，通常是一个完整的功能，是较为最稳定的组件。这部分通常也是比较容易识别的

- 业务组件，完整的业务块，
例如前面提到京东的 “首页”、“分类”、“发现”、“购物车”、“我的”。业务组件是日常需求开发的主战场

## 组件拆分：基础组件、Common组件

基础组件最容易拆分，它依赖最少，功能单一纯粹。把基础组件依赖的东西，从老工程中抽取出来，放在单独的工程，做成单独的组件，发布ARR到公司maven仓。注意不能存在任何业务相关代码。
新建Common组件，使用 “api” 依赖 所有基础组件，这样依赖 Common组件的组件 就能使用所有基础组件的功能了。接着，就是前面提到的 ARouter、AppLifeCycle、以及其他第三方库的依赖。
另外，Common组件，还有一个重要部分：提供BaseActivity、BaseFragment，这里Base需要完成基础能力的添加，例如页面进入、退出的埋点上报、统一页面标题样式、打开关闭EventBus等等

## 组件拆分：业务基础组件、业务组件

业务基础组件 基本上只依赖common，功能也是单一纯粹。同样是把依赖的东西抽取出来，放在单独的工程，做成单独的组件，发布ARR到公司maven仓。
业务组件，首先要识别组件的边界，可以按照页面入口和出口作为判断。然后，需要识别对 业务基础组件的依赖；以及 最重要的，对其他 业务组件的依赖。
可以先把代码抽离到单独的工程，然后依赖common组件、需要的业务基础组件，此时依然报错的地方就是 对其他 业务组件的依赖了。这时就可以给对应组件负责人提需求，在export_xxx中提供跳转和服务。然后你只需要依赖export_xxx使用即可。




   



























































   
   


     

    
 
   
   











