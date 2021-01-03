[toc]
# Activity

# 一、生命周期

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

During this time the activity is in** visible, active and interacting with the user**


# 二、屏幕旋转 configuration

## 屏幕旋转时的生命周期

默认情况下会销毁当前Activity,并重新创建一个新的activity实例
如果想要不销毁，在AndroidManifest中配置configChanges参数


- 不进行任何配置和配置android:configChanges="orientation"参数

竖屏切换为横屏：
```
onPause                 
onStop                  
onSaveInstanceState     
onDestroy               
onStart                 
onRestoreInstanceState  
```




- 配置 android:configChanges="orientation|screenSize>参数

竖屏切换为横屏：
```
 onConfigurationChanged         

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



## configChanges 的值

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






# 二、显示布局
Activity class takes care of creating a window for you in which you can place your UI 
with {@link #setContentView}

 每一个 Activity 持有一个 PhoneWindow 的对象，
 而一个 PhoneWindow 对象持有一个 DecorView 的实例，
 所以 Activity 中 View 相关的操作其实大都是通过 DecorView 来完成。