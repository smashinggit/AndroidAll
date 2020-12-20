# Android 中的 Attr,Style 和 Theme

## Attr
attr其实就是一个文件，这里面定义了我们的控件中所使用的属性

### 1. 如何定义 Attr

```
    <declare-styleable name="ProgressBarWithText">
        <attr name="max" format="integer" />
        <attr name="backgroundColor" format="color" />
        <attr name="foregroundColor" format="color" />

        <attr name="progressType" format="enum">
            <enum name="ABOVE" value="0" />
            <enum name="END" value="1" />
        </attr>

    </declare-styleable>
```
### 2. 在自定义 View 中取 Attr 的值
```
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ProgressBar)

        val max = typedArray.getInt(R.styleable.ProgressBar_max, MAX)
        val backgroundColor = typedArray.getColor(R.styleable.ProgressBar_backgroundColor, GRAY)
        val foregroundColor = typedArray.getColor(R.styleable.ProgressBar_foregroundColor, BLUE)
        val style = typedArray.getInt(R.styleable.ProgressBar_progressType, 0)

        typedArray.recycle()
```



## Style
style就像单词意思一样，风格，在 Android 指属性的集合，
如果页面中有许多控件的属性值相同那么就可以把这些属性抽出来放到 style 里面，

### 1. 定义 Style
定义也很简单，在values文件下的styles里面创建就可以了

```
    <style name="PrimaryTextView">
        <item name="android:textColor">@color/PrimaryTextColor</item>
        <item name="android:textSize">@dimen/PrimaryTextSize</item>
    </style>

    <style name="SecondTextView">
        <item name="android:textColor">@color/SecondTextColor</item>
        <item name="android:textSize">@dimen/SecondTextSize</item>
    </style>

    <style name="ThirdTextView">
        <item name="android:textColor">@color/ThirdTextColor</item>
        <item name="android:textSize">@dimen/ThirdTextSize</item>
    </style>

```


### 2. 使用 Style
```
<TextView
        style="@style/PrimaryTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!" />
```


## Theme

Theme 和 Style 使用的是同一个元素标签 <style>
区别是： Theme 和 Style 作用的范围不同
Theme 要求你设置到 AndroidMainfest.xml 的 <application> 或者 <activity> 中




