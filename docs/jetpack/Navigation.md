[toc]

# Navigation
[https://www.jianshu.com/p/66b93df4b7a6](https://www.jianshu.com/p/66b93df4b7a6)


# 简介

## 定义
Navigation是一个可简化Android导航的库和插件

更确切的来说，Navigation是用来管理Fragment的切换，并且可以通过可视化的方式，看见App的交互流程。
这完美的契合了Jake Wharton大神单Activity的建议。

## 优点

- 处理Fragment的切换（上文已说过）
- 默认情况下正确处理Fragment的前进和后退
- 为过渡和动画提供标准化的资源
- 实现和处理深层连接
- 可以绑定Toolbar、BottomNavigationView和ActionBar等
- SafeArgs（Gradle插件） 数据传递时提供类型安全性
- ViewModel支持



#  实战
##  Navigation三要素
            
- Navigation Graph(New XML resource)
这是一个新的资源文件，用户在可视化界面可以看出他能够到达的Destination(用户能够到达的屏幕界面)，
以及流程关系。

- NavHostFragment(Layout XML view)
当前Fragment的容器

- NavController(Kotlin/Java object)
导航的控制者

我们可以将Navigation Graph看作一个地图，NavHostFragment看作一个车，以及把NavController看作车中的方向盘，
Navigation Graph中可以看出各个地点（Destination）和通往各个地点的路径，
NavHostFragment可以到达地图中的各个目的地，
但是决定到什么目的地还是方向盘NavController，虽然它取决于开车人


# 使用

## 添加依赖

```
ext.navigationVersion = "2.0.0"
dependencies {
    //... 
    implementation "androidx.navigation:navigation-fragment-ktx:$rootProject.navigationVersion"
    implementation "androidx.navigation:navigation-ui-ktx:$rootProject.navigationVersion"
}
```
如果你要使用SafeArgs插件，还要在项目目录下的build.gradle文件添加：
```
buildscript {
    ext.navigationVersion = "2.0.0"
    dependencies {
        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion"
    }
}
```

以及模块下面的build.gradle文件添加：
```
apply plugin: 'kotlin-android-extensions'
apply plugin: 'androidx.navigation.safeargs'
```

## 创建navigation导航

- 创建基础目录：
资源文件res目录下创建navigation目录 -> 右击navigation目录New一个Navigation resource file

- 创建一个Destination，
如果说navigation是我们的导航工具，Destination是我们的目的地，
在此之前，我已经写好了一个WelcomeFragment、LoginFragment和RegisterFragment

```
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_login"
    app:startDestination="@id/welcomeFragment">


    <fragment
        android:id="@+id/welcomeFragment"
        android:name="com.cmcc.jetpack.nav.WelcomeFragment"
        android:label="fragment_welcome"
        tools:layout="@layout/fragment_welcome">
        <action
            android:id="@+id/action_loginFragment"
            app:destination="@id/loginFragment" />

        <action
            android:id="@+id/action_registerFragment"
            app:destination="@id/registerFragment" />
    </fragment>

    <fragment
        android:id="@+id/loginFragment"
        android:name="com.cmcc.jetpack.nav.LoginFragment"
        android:label="fragment_login"
        tools:layout="@layout/fragment_login" />

    <fragment
        android:id="@+id/registerFragment"
        android:name="com.cmcc.jetpack.nav.RegisterFragment"
        android:label="fragment_rejister"
        tools:layout="@layout/fragment_register" />

</navigation>
```


- 建立NavHostFragment

```
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
 ...
 >
    <fragment
        android:id="@+id/nav_host_fragment_container"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_test" />

</androidx.constraintlayout.widget.ConstraintLayout>
```


android:name   ->  值必须是androidx.navigation.fragment.NavHostFragment，声明这是一个NavHostFragment
app:navGraph   ->  存放的是第二步建好导航的资源文件，也就是确定了Navigation Graph
app:defaultNavHost="true"  -> 与系统的返回按钮相关联

在Activity中添加如下代码：

```
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp() =
            findNavController(this, R.id.my_nav_host_fragment).navigateUp()
}
```

onSupportNavigateUp()方法的重写，意味着Activity将它的 back键点击事件的委托出去，
如果当前并非栈中顶部的Fragment, 那么点击back键，返回上一个Fragment。


 
## 界面跳转、参数传递和动画

## 方式一：利用ID导航

目标：WelcomeFragment携带key为name的数据跳转到LoginFragment，LoginFragment接收后显示
```
    val navOptions = NavOptions.Builder()
                .build()

   val bundle = Bundle().apply {
        putString("name", "chensen")
    }

   findNavController().navigate(R.id.loginFragment, bundle, navOptions)

```

```


```
















#
















