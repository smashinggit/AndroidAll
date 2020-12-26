[toc]

# SDK Build Tools 

Android SDK Build-Tools 是构建 Android 应用所需的一个 Android SDK 组件，安装在 <sdk>/build-tools/ 目录中

您应始终让 Build Tools 组件保持最新状态。为此，您可以使用 Android SDK 管理器下载该组件的最新版本。
如果您使用的是 Android Plugin for Gradle 3.0.0 或更高版本，那么您的项目会自动使用该插件指定的默认版本的 Build Tools。

如需使用其他版本的 Build Tools，请在模块的 build.gradle 中使用 buildToolsVersion 进行指定，如下所示
```
android {
    buildToolsVersion "29.0.2"
    ...
}
```

# Android Gradle 插件版本说明

Android Studio 构建系统以 Gradle 为基础，并且 Android Gradle 插件添加了几项专用于构建 Android 应用的功能。
虽然 Android 插件通常会与 Android Studio 的更新步调保持一致，但插件（以及 Gradle 系统的其余部分）可独立于 
Android Studio 运行并单独更新。


您可以在 Android Studio 的 File > Project Structure > Project 菜单中指定插件版本，
也可以在顶级 build.gradle 文件中进行指定。
**该插件版本将用于在相应 Android Studio 项目中构建的所有模块**

以下示例从 build.gradle 文件中将插件的版本设置为 4.0.0：

```
buildscript {
    repositories {
        // Gradle 4.1 and higher include support for Google's Maven repo using
        // the google() method. And you need to include this repo to download
        // Android Gradle plugin 3.0.0 or higher.
        google()
        ...
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:4.0.0'
    }
}
```

# 利用注释改进代码检查

## null 性注解
添加 @Nullable 和 @NonNull 注解，以检查给定变量、参数或返回值的 null 性。
@Nullable 注解用于指明可以为 null 的变量、参数或返回值，
@NonNull 则用于指明不可以为 null 的变量、参数或返回值


## 资源注解
验证资源类型可能非常有用，因为 Android 对资源（如可绘制对象和字符串资源）的引用以整数形式传递。
如果代码需要一个参数来引用特定类型的资源（例如可绘制对象），可以为该代码传递预期的引用类型 int，但它实际上会引用
其他类型的资源，如 R.string 资源。
```
abstract fun setTitle(@StringRes resId: Int)
```

其他资源类型的注解（例如 @DrawableRes、@DimenRes、@ColorRes 和 @InterpolatorRes）可以使用相同的注解格式添加，
并在代码检查期间运行。如果参数支持多种资源类型，您可以为给定参数添加其中多个注解。
使用 @AnyRes 可以指明添加了此类注解的参数可以是任何类型的 R 资源

## 线程注解

- @MainThread
- @UiThread
- @WorkerThread
- @BinderThread
- @AnyThread


## 值约束注解

使用 @IntRange、@FloatRange 和 @Size 注解可以验证所传递参数的值。
@IntRange 和 @FloatRange 在应用到用户可能会弄错范围的参数时最为有用
```
fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) { ... }

fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {...}
```

@Size 注解可以检查集合或数组的大小，以及字符串的长度
。@Size 注解可用于验证以下特性：
- 最小大小（例如 @Size(min=2)）
- 最大大小（例如 @Size(max=2)）
- 确切大小（例如 @Size(2)）
- 大小必须是指定数字的倍数（例如 @Size(multiple=2)）

例如，@Size(min=1) 可以检查某个集合是否不为空，@Size(3) 可以验证某个数组是否正好包含三个值。
以下示例可以确保 location 数组至少包含一个元素：
```           
fun getLocation(button: View, @Size(min=1) location: IntArray) {
    button.getLocationOnScreen(location)
}
```

## 权限注解
使用 @RequiresPermission 注解可以验证方法调用方的权限。
要检查有效权限列表中是否存在某个权限，请使用 anyOf 属性。
要检查是否具有某组权限，请使用 allOf 属性。

```
// 以下示例会为 setWallpaper() 方法添加注解，以确保方法调用方具有 permission.SET_WALLPAPERS 权限
@RequiresPermission(Manifest.permission.SET_WALLPAPER)
@Throws(IOException::class)
abstract fun setWallpaper(bitmap: Bitmap)


//以下示例要求 copyImageFile() 方法的调用方具有对外部存储空间的读取权限，以及对复制的映像中的位置元数据的读取权限：
@RequiresPermission(allOf = [
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_MEDIA_LOCATION
])
fun copyImageFile(dest: String, source: String) {
    ...
}

```

对于 intent 的权限，请在用来定义 intent 操作名称的字符串字段上添加权限要求：
```
@RequiresPermission(android.Manifest.permission.BLUETOOTH)
const val ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE"
```

如果您需要对内容提供程序拥有单独的读取和写入访问权限，则需要将每个权限要求封装在 @RequiresPermission.Read
或 @RequiresPermission.Write 注解中：
```
@RequiresPermission.Read(RequiresPermission(READ_HISTORY_BOOKMARKS))
@RequiresPermission.Write(RequiresPermission(WRITE_HISTORY_BOOKMARKS))
val BOOKMARKS_URI = Uri.parse("content://browser/bookmarks")
```

Typedef 注解

使用 @IntDef 和 @StringDef 注解，您可以创建整数集和字符串集的枚举注解来验证其他类型的代码引用。
Typedef 注解可以确保特定参数、返回值或字段引用一组特定的常量。这些注解还会启用代码补全功能，以自动提供允许的常量。

Typedef 注解使用 @interface 来声明新的枚举注解类型。@IntDef 和 @StringDef 注解以及 @Retention 可以对新注
解添加注解，是定义枚举类型所必需的。@Retention(RetentionPolicy.SOURCE) 注解可告诉编译器不要将枚举注解数据存
储在 .class 文件中

以下示例展示了创建某个注解的具体步骤，该注解可以确保作为方法参数传递的值引用某个已定义的常量：
```
// for java
class TypedefTest {

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_LIST = 1;
    public static final int NAVIGATION_MODE_TABS = 2;

    @Retention(SOURCE)
    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_LIST, NAVIGATION_MODE_TABS})
    public @interface NavigationMode {
    }

    private static void setNavigationMode(@NavigationMode int mode) {
        System.out.println("mode " + mode);
    }

    public static void main(String[] args) {
        //setNavigationMode(12);   //会警告，无法编译通过
        setNavigationMode(NAVIGATION_MODE_STANDARD); //正确
    }
}

// for kotlin




```

构建此代码时，如果 mode 参数未引用任何已定义的常量（ROTATION_0,ROTATION_90、ROTATION_180
 或 ROTATION_270），系统会生成一条警告



Keep 注解

使用 @Keep 注解可以确保以下情况：如果在构建时缩减代码大小，将不会移除带有该注解的类或方法。
该注解通常添加到通过反射访问的方法和类，以防止编译器将代码视为未使用。

注意：
使用 @Keep 添加注解的类和方法会始终包含在应用的 APK 中，即使您从未在应用逻辑中引用这些类和方法也是如此。
为使您的应用保持小巧，请考虑是否有必要在您的应用中保留每个 @Keep 注解。
如果使用反射来访问添加了此类注解的类或方法，请在 ProGuard 规则中使用 -if 条件语句来指定进行反射调用的类。
















 








