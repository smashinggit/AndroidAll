[toc]
# Activity


# 一、 什么是 Activity？ 
 Activity 是 Android 的四大组件之一，是用户操作的可视化界面，它为用户提供了一个完成操作指令的窗口。
 当我们创建完 Activity 之后，需要调用 setContentView(view) 方法来完成界面的显示，以此来为用户提供交互的入口
 
# 二、生命周期

```
 protected void onCreate(Bundle savedInstanceState);
 
 protected void onStart();      Called when the activity is becoming visible to the user
 
  protected void onRestart();   Called after your activity has been stopped, prior to it being started again
 
  protected void onResume();     Called when the activity will start interacting with the user
 
  protected void onPause();      Called when the activity loses foreground state, is no longer 
                                 focusable or before transition to stopped/hidden or destroyed state
 
  protected void onStop();       Called when the activity is no longer visible to the user
 
  protected void onDestroy();    The final call you receive before your activity is destroyed
```

## visible lifetime 可见生命周期
onStart
onResume
onPause
onStop     

这个生命周期内对用户可见
During this time the user can see the activity on-screen, 
though it may not be in the foreground and interacting with the user

## foreground lifetime 前台生命周期
onResume
onPause

During this time the activity is in **visible, active and interacting with the user**



Activity 本质上有四种状态：
- 运行：如果一个Activity被移到了前台（活动栈顶部）。
- 暂停：如果一个Activity被另一个非全屏的Activity所覆盖（比如一个 Dialog），那么该活动就失去了焦点，
  它将会暂停（但它仍然保留所有的状态和成员信息，并且仍然是依附在 WindowsManager 上），
  在系统内存积极缺乏的时候会将它杀死
- 停止：如果一个Activity被另一个全屏Activity完全覆盖，那么该活动处于停止状态（状态和成员信息会保留，
  但是 Activity 已经不再依附于 WindowManager 了）。
  同时，在系统缺乏资源的时候会将它杀死（它会比暂停状态的活动先杀死) 
- 重启：如果一个活动在处于停止或者暂停的状态下，系统内存缺乏时会将其结束（finish）或者杀死（kill）。
  这种非正常情况下，系统在杀死或者结束之前会调用 onSaveInstanceState() 方法来保存信息，
  同时，当 Activity 被移动到前台时，重新启动该Activity并调用 onRestoreInstanceState() 方法加载保留的信息，
  以保持原有的状态。
  
正常情况下的生命周期：

- onCreate()：与 onDestroy() 配对，表示 Activity 正在被创建，这是生命周期的第一个方法

- onRestart()：表示 Activity 正在重新启动。
   一般情况下，在当前 Activity 从不可见重新变为可见的状态时 onRestart() 就会被调用。
   这种情形一般是由于用户的行为所导致的，比如用户按下 Home 键切换到桌面或者打开了一个新的 Activity
   （这时当前 Activity 会暂停，也就是 onPause() 和 onStop() 被执行），接着用户有回到了这个 Activity，
   就会出现这种情况。
   
- onStart()：与 onStop() 配对，表示 Activity 正在被启动，并且即将开始
  但是这个时候要注意它与 onResume() 的区别。两者都表示 Activity 可见，但是 onStart() 时 Activity 还正在
  加载其他内容，正在向我们展示，用户还无法看到，即无法交互

- onResume()：与 onPause() 配对，表示 Activity 已经创建完成，并且可以开始活动了，这个时候用户已经可以看到界面了，
  并且即将与用户交互（完成该周期之后便可以响应用户的交互事件了）

- onPause()：与 onResume() 配对，表示 Activity 正在暂停，正常情况下，onStop() 接着就会被调用。
  在特殊情况下，如果这个时候用户快速地再回到当前的 Activity，那么 onResume() 会被调用（极端情况）
  一般来说，在这个生命周期状态下，可以做一些存储数据、停止动画的工作，
  但是不能太耗时，如果是由于启动新的 Activity 而唤醒的该状态，那会影响到新 Activity 的显示，
  原因是 onPause() 必须执行完，新的 Activity的 onResume() 才会执行

- onStop()：与 onStart() 配对，表示 Activity 即将停止，可以做一些稍微重量级的回收工作，
  同样也不能太耗时（可以比 onPause 稍微好一点）

- onDestroy()：与 onCreate() 配对，表示 Activity 即将被销毁，这是 Activity 生命周期的最后一个回调，
  我们可以做一些回收工作和最终的资源释放（如 Service、BroadReceiver、Map 等）  




## 屏幕旋转 configuration

### 屏幕旋转时的生命周期

默认情况下会销毁当前Activity,并重新创建一个新的activity实例
如果想要不销毁，在AndroidManifest中配置configChanges参数


- 不进行任何配置和配置android:configChanges="orientation"参数

竖屏切换为横屏：
```
A: ==> onCreate()
A: ==> onStart()
A: ==> onResume()
A: ==> onPause()
A: ==> onStop()
A: ==> onSaveInstanceState()
A: ==> onDestroy()

屏幕旋转后
A: ==> onCreate()
A: ==> onStart()
A: ==> onRestoreInstanceState()
A: ==> onResume()
```


- 配置 android:configChanges="orientation|screenSize>参数

竖屏切换为横屏：
```
A: ==> onCreate()
A: ==> onStart()
A: ==> onResume()
A: ==> onConfigurationChanged()    
```

监听屏幕变化：
```
@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    int orientation = newConfig.orientation;
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        // TODO: 18/2/22  竖屏操作
    }
    else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // TODO: 18/2/22  横屏操作
    }
}
```



### configChanges 的值

- orientation
屏幕方向改变了---横竖屏切换

注：
测试发现，单独配置此项,切换横竖屏时不生效，原因未知

- screenSize
屏幕大小改变了

测试时，如果单独设置此项，则切换横竖屏时，不重新创建Activity

- smallestScreenSize	
屏幕的物理大小改变了，如：连接到一个外部的屏幕上

- keyboard	
键盘发生了改变----例如用户用了外部的键盘

- keyboardHidden	
键盘的可用性发生了改变

- fontScale
字体比例发生了变化----选择了不同的全局字体



# 三、保存与恢复
Activity 为我们提供了两个回调方法 onSaveInstanceState() 和 onRestoreInstanceState() 用于当 Activity 
在不是用户主动意识关闭的情况下来进行页面数据的保存和恢复

那么那些情况下 onSaveInstanceState() 会被调用呢？分别有以下几种情况：

- 当用户按下 Home 键 App 处于后台，此时会调用 onSaveInstanceState() 方法
- 当用户按下电源键时，会调用 onSaveInstanceState() 方法
- 当 Activity 进行横竖屏切换的时候也会调用 onSaveInstanceState() 方法
- 从 AActivity 跳转到 BActivity 的时候 AActivity 也会调用 onSaveInstanceState()  方法

虽然以上四种情况会执行 onSaveInstanceState() 方法 但是并不是都会执行 onRestoreInstanceState() 方法，
只有第三种情况会调用 onRestoreInstanceState()，
因为当 Activity 横竖屏切换的时候会重新走一遍生命周期，所以 Activity 会被销毁创建，由此会执行 onRestoreInstanceState() 方法。

也就是说 **onSaveInstanceState 和 onRestoreInstanceState 并不是一定成双出现的**，
只有当 Activity 真正的被销毁的时候才会执行 onRestoreInstanceState()





# 四、启动模式

Activity 的启动模式有4种，分别是 Standard、SingleTop、SingleTask、SingleInstance。
可以在 AndroidManifest.xml 文件中指定每一个 Activity 的启动模式

一个 Android 应用一般都会有多个 Activity，系统会通过任务栈来管理这些 Activity，**栈是一种后进先出**的集合，
当前的 Activity 就在栈顶，按返回键，栈顶 Activity 就会退出。
Activity 启动模式不同，系统通过任务栈管理 Activity 的方式也会不同

- Standard

Standard 模式是 Android 的默认启动模式，你不在配置文件中做任何设置，那么这个 Activity 就是 Standard 模式。
这种模式下，Activity 可以有多个实例，每次启动 Activity，无论任务栈中是否已经有这个 Activity 的实例，
系统都会创建一个新的 Activity 实例

- SingleTop

SingleTop 模式和 Standard 模式非常相似，主要区别就是当一个 SingleTop 模式的 Activity 已经位于任务栈的栈顶，
再去启动它时，不会再创建新的实例。如果不位于栈顶，就会创建新的实例。

- SingleTask

SingleTask 模式的 Activity 在同一个 Task 内只有一个实例
如果 Activity 已经位于栈顶，系统不会创建新的 Activity 实例，和 SingleTop 模式一样。
但 Activity 已经存在但不位于栈顶时，系统就会把该 Activity 移到栈顶，**并把它上面的 Activity 出栈**

- SingleInstance

SingleInstance 模式也是单例的，但和 SingleTask 不同，SingleTask 只是任务栈内单例，
系统里是可以有多个 SingleTask Activity 实例的，
而 SingleInstance Activity 在**整个系统里只有一个实例，启动一个 SingleInstance 的 Activity 时，
系统会创建一个新的任务栈，并且这个任务栈只有他一个 Activity**

SingleInstance 模式并不常用，如果我们把一个 Activity 设置为 SingleInstance 模式，你会发现它启动时会慢一些，
切换效果不好，影响用户体验。


# 五、 Intent
Intent 分两种，显式 Intent 和隐式 Intent。
如果一个 Intent 明确指定了要启动的组件的完整类名，那么这个 Intent 就是显式 Intent，否则就是隐式 Intent

- 显式 Intent

```
Intent intent = new Intent(this, xxx.class);
startActivity(intent);

```
- 隐式 Intent

使用隐式 Intent 之前需要在 AndroidManifest.xml 中对标签增加设置
```
<activity android:name=".ui.activity.IntentActivity">
    <intent-filter>
        <action android:name="com.jeanboy.action.TEST" />
    </intent-filter>
</activity>

Intent intent = new Intent("com.jeanboy.action.TEST");
startActivity(intent);

```

# 六、 Intent Filter

如果 Intent 中的存在 category 那么所有的 category 都必须和 Activity 过滤规则中的 category 相同,才能和这个 Activity 匹配。
Intent 中的 category 数量可能少于 Activity 中配置的 category 数量，但是 Intent 中的这 category 必须和 Activity 中配置的 category 相同才能匹配

```
<activity android:name=".ui.activity.IntentActivity">
    <intent-filter>
        <action android:name="com.jeanboy.action.TEST" />
        <category android:name = "android.intent.category.DEFAULT" />
        <category android:name="aaa.bb.cc"/>
    </intent-filter>
</activity>


//运行以下代码可以匹配到 IntentActivity
Intent intent = new Intent("com.jeanboy.action.TEST");
intent.addCategory("aaa.bb.cc");
startActivity(intent);
```



# 显示布局
Activity class takes care of creating a window for you in which you can place your UI with {@link #setContentView}

 每一个 Activity 持有一个 PhoneWindow 的对象，
 而一个 PhoneWindow 对象持有一个 DecorView 的实例，
 所以 Activity 中 View 相关的操作其实大都是通过 DecorView 来完成
 
 

# 相关面试题

- 启动一个 Activity 的生命周期？

例如：A 启动 B，生命周期如下
```
A: ==> onCreate()
A: ==> onStart()
A: ==> onResume()
A: ==> onPause()

B: ==> onCreate()
B: ==> onStart()
B: ==> onResume()

A: ==> onStop()
```

- 按下Home 键的生命周期



- 下拉通知栏对生命周期的影响？
    没有影响！

- AlertDialog（对话框）对生命周期的影响？
    没有影响！
  
- Toast 对生命周期的影响？
    没有影响！
    
- 透明主题的 Activity 对生命周期的影响？
```
A: ==> onCreate()
A: ==> onStart()
A: ==> onResume()

如果弹出透明 Activity
A: ==> onPause()
```  

- 屏幕旋转对生命周期的影响？
  
没有配置 configChanges：  
```
A: ==> onCreate()
A: ==> onStart()
A: ==> onResume()
A: ==> onPause()
A: ==> onSaveInstanceState()
A: ==> onStop()
A: ==> onDestroy()

屏幕旋转后
A: ==> onCreate()
A: ==> onStart()
A: ==> onRestoreInstanceState()
A: ==> onResume()
```

配置 configChanges 后：
```
A: ==> onCreate()
A: ==> onStart()
A: ==> onResume()
A: ==> onConfigurationChanged()
```


