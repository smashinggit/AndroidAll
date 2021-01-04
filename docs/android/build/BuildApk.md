[toc]

# 自定义构建配置

Gradle 和 Android 插件可帮助您完成以下方面的构建配置：

## 构建类型   buildTypes
构建类型定义 Gradle 在构建和打包应用时使用的某些属性，通常针对开发生命周期的不同阶段进行配置。
例如，调试构建类型支持调试选项，并使用调试密钥为 APK 签名；
而发布构建类型则会缩减 APK、对 APK 进行混淆处理，并使用发布密钥为 APK 签名以进行分发。
如需构建应用，您必须至少定义一个构建类型。Android Studio 默认会创建调试和发布两个构建类型




## 产品变种

产品变种代表您可以向用户发布的应用的不同版本，如应用的免费版和付费版。
您可以自定义产品变种以使用不同的代码和资源，同时共享和重复利用所有应用版本共用的部分。
产品变种是可选的，您必须手动创建





## 构建变体
构建变体是构建类型与产品变种的交叉产物，也是 Gradle 用来构建应用的配置。
利用构建变体，您可以在开发期间构建产品变种的调试版本，或者构建产品变种的已签名发布版本以供分发。
虽然您无法直接配置构建变体，但可以配置组成它们的构建类型和产品变种。
创建额外的构建类型或产品变种也会创建额外的构建变体


## 清单条目

您可以在构建变体配置中为清单文件的某些属性指定值。这些构建值会替换清单文件中的现有值。
如果您要为模块生成多个 APK，让每一个 APK 文件都具有不同的应用名称、最低 SDK 版本或目标 SDK 版本，便可运用这一技巧。
当存在多个清单时，Gradle 会合并清单设置。


## 依赖项
构建系统会管理来自本地文件系统以及来自远程代码库的项目依赖项。这样一来，您就不必手动搜索、下载依赖项的二进制文件包
以及将它们复制到项目目录中


## 签名
构建系统让您能够在构建配置中指定签名设置，并且可以在构建过程中自动为 APK 签名。
构建系统通过已知凭据使用默认密钥和证书为调试版本签名，以避免在构建时提示输入密码。
除非您为此构建明确定义签名配置，否则，构建系统不会为发布版本签名。
如果您没有发布密钥，可以按为应用签名中所述生成一个

## 代码和资源缩减
构建系统让您能够为每个构建变体指定不同的 ProGuard 规则文件。在构建应用时，构建系统会应用一组适当的规则以使用
其内置的缩减工具（如 R8）缩减您的代码和资源


##  多 APK 支持

构建系统让您能够自动构建不同的 APK，并且每个 APK 只包含特定屏幕密度或应用二进制接口 (ABI) 所需的代码和资源。



# 构建配置文件

## Gradle 设置文件
settings.gradle 文件位于项目的根目录下，用于指示 Gradle 在构建应用时应将哪些模块包含在内

##  顶层构建文件
顶层 build.gradle 文件位于项目的根目录下，用于**定义适用于项目中所有模块的构建配置**
默认情况下，顶层构建文件使用 buildscript 代码块定义项目中所有模块共用的 Gradle 代码库和依赖项。

以下代码示例说明了创建新项目后可在顶层 build.gradle 文件中找到的默认设置和 DSL 元素

```
/**
 * The buildscript block is where you configure the repositories and
 * dependencies for Gradle itself—meaning, you should not include dependencies
 * for your modules here. For example, this block includes the Android plugin for
 * Gradle as a dependency because it provides the additional instructions Gradle
 * needs to build Android app modules.
 */

buildscript {

    /**
     * The repositories block configures the repositories Gradle uses to
     * search or download the dependencies. Gradle pre-configures support for remote
     * repositories such as JCenter, Maven Central, and Ivy. You can also use local
     * repositories or define your own remote repositories. The code below defines
     * JCenter as the repository Gradle should use to look for its dependencies.
     *
     * New projects created using Android Studio 3.0 and higher also include
     * Google's Maven repository.
     */

    repositories {
        google()
        jcenter()
    }

    /**
     * The dependencies block configures the dependencies Gradle needs to use
     * to build your project. The following line adds Android plugin for Gradle
     * version 4.0.0 as a classpath dependency.
     */

    dependencies {
        classpath 'com.android.tools.build:gradle:4.0.0'
    }
}

/**
 * The allprojects block is where you configure the repositories and
 * dependencies used by all modules in your project, such as third-party plugins
 * or libraries. However, you should configure module-specific dependencies in
 * each module-level build.gradle file. For new projects, Android Studio
 * includes JCenter and Google's Maven repository by default, but it does not
 * configure any dependencies (unless you select a template that requires some).
 */

allprojects {
   repositories {
       google()
       jcenter()
   }
}
```

## 配置项目全局属性

对于包含多个模块的 Android 项目，可能有必要在项目级别定义某些属性并在所有模块之间共享这些属性。
为此，您可以将额外的属性添加到顶层 build.gradle 文件内的 ext 代码块中

```
buildscript {...}

allprojects {...}

// This block encapsulates custom properties and makes them available to all
// modules in the project.
ext {
    // The following are only a few examples of the types of properties you can define.
    compileSdkVersion = 28
    // You can also create properties to specify versions for dependencies.
    // Having consistent versions between modules can avoid conflicts with behavior.
    supportLibVersion = "28.0.0"
    ...
}

```
如需从同一项目中的模块访问这些属性，请在该模块的 build.gradle 文件（您可以在下一部分中详细了解此文件）中使用以下语法

```
android {
  // Use the following syntax to access properties you defined at the project level:
  // rootProject.ext.property_name
  compileSdkVersion rootProject.ext.compileSdkVersion
  ...
}
...
dependencies {
    implementation "com.android.support:appcompat-v7:${rootProject.ext.supportLibVersion}"
    ...
}
```


## 模块级构建文件

模块级 build.gradle 文件位于每个 project/module/ 目录下，用于为其所在的特定模块配置构建设置。
您可以通过配置这些构建设置提供自定义打包选项（如额外的构建类型和产品变种），以及替换 main/ 应用清单或
顶层 build.gradle 文件中的设置。

```
/**
 * The first line in the build configuration applies the Android plugin for
 * Gradle to this build and makes the android block available to specify
 * Android-specific build options.
 */

apply plugin: 'com.android.application'

/**
 * The android block is where you configure all your Android-specific build options.
 */
android {

  /**
   * compileSdkVersion specifies the Android API level Gradle should use to
   * compile your app. This means your app can use the API features included in
   * this API level and lower.
   */

  compileSdkVersion 28

  /**
   * buildToolsVersion specifies the version of the SDK build tools, command-line
   * utilities, and compiler that Gradle should use to build your app. You need to
   * download the build tools using the SDK Manager.
   *
   * This property is optional because the plugin uses a recommended version of
   * the build tools by default.
   */

  buildToolsVersion "29.0.2"

  /**
   * The defaultConfig block encapsulates default settings and entries for all
   * build variants, and can override some attributes in main/AndroidManifest.xml
   * dynamically from the build system. You can configure product flavors to override
   * these values for different versions of your app.
   */

  defaultConfig {

    /**
     * applicationId uniquely identifies the package for publishing.
     * However, your source code should still reference the package name
     * defined by the package attribute in the main/AndroidManifest.xml file.
     */

    applicationId 'com.example.myapp'

    // Defines the minimum API level required to run the app.
    minSdkVersion 15

    // Specifies the API level used to test the app.
    targetSdkVersion 28

    // Defines the version number of your app.
    versionCode 1

    // Defines a user-friendly version name for your app.
    versionName "1.0"
  }

  /**
   * The buildTypes block is where you can configure multiple build types.
   * By default, the build system defines two build types: debug and release. The
   * debug build type is not explicitly shown in the default build configuration,
   * but it includes debugging tools and is signed with the debug key. The release
   * build type applies Proguard settings and is not signed by default.
   */

  buildTypes {

    /**
     * By default, Android Studio configures the release build type to enable code
     * shrinking, using minifyEnabled, and specifies the default Proguard rules file.
     */

    release {
        minifyEnabled true // Enables code shrinking for the release build type.
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }
  }

  /**
   * The productFlavors block is where you can configure multiple product flavors.
   * This allows you to create different versions of your app that can
   * override the defaultConfig block with their own settings. Product flavors
   * are optional, and the build system does not create them by default.
   *
   * This example creates a free and paid product flavor. Each product flavor
   * then specifies its own application ID, so that they can exist on the Google
   * Play Store, or an Android device, simultaneously.
   *
   * If you declare product flavors, you must also declare flavor dimensions
   * and assign each flavor to a flavor dimension.
   */

  flavorDimensions "tier"
  productFlavors {
    free {
      dimension "tier"
      applicationId 'com.example.myapp.free'
    }

    paid {
      dimension "tier"
      applicationId 'com.example.myapp.paid'
    }
  }

  /**
   * The splits block is where you can configure different APK builds that
   * each contain only code and resources for a supported screen density or
   * ABI. You'll also need to configure your build so that each APK has a
   * different versionCode.
   */

  splits {
    // Settings to build multiple APKs based on screen density.
    density {

      // Enable or disable building multiple APKs.
      enable false

      // Exclude these densities when building multiple APKs.
      exclude "ldpi", "tvdpi", "xxxhdpi", "400dpi", "560dpi"
    }
  }
}

/**
 * The dependencies block in the module-level build configuration file
 * specifies dependencies required to build only the module itself.
 * To learn more, go to Add build dependencies.
 */

dependencies {
    implementation project(":lib")
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}
```


## Gradle 属性文件
Gradle 还包含两个属性文件，它们位于项目的根目录下，可用于指定 Gradle 构建工具包本身的设置：

- gradle.properties
您可以在其中配置项目全局 Gradle 设置，如 Gradle 守护程序的最大堆大小

- local.properties
为构建系统配置本地环境属性，其中包括：
1. ndk.dir - NDK 的路径。此属性已被弃用。NDK 的所有下载版本都将安装在 Android SDK 目录下的 ndk 目录中
2. sdk.dir - SDK 的路径
3. cmake.dir - CMake 的路径
4. dk.symlinkdir - 在 Android Studio 3.5 及更高版本中，创建指向 NDK 的符号链接，该符号链接的路径可比 NDK 安装路径短。



## 源代码集
Android Studio 按逻辑关系将每个模块的源代码和资源分组为源代码集。
模块的 main/ 源代码集包含其所有构建变体共用的代码和资源

其他源代码集目录是可选的，在您配置新的构建变体时，Android Studio 不会自动为您创建这些目录。
不过，创建类似于 main/ 的源代码集有助于组织 Gradle 仅在构建特定应用版本时才应使用的文件和资源：
- src/main/
 此源代码集包含所有构建变体共用的代码和资源
 
- src/buildType/
创建此源代码集可加入特定构建变体专用的代码和资源。(如：debug 或 release)

- src/productFlavor/
创建此源代码集可加入特定产品变种专用的代码和资源。 (如：free 或 paid)

注意：
如果配置构建以组合多个产品变种，则可以为变种维度之间的每个产品变种组合创建源代码集目录：src/productFlavor1ProductFlavor2/

- src/productFlavorBuildType/
创建此源代码集可加入特定构建变体专用的代码和资源。 (如：freeRelease)


例如，如需生成应用的“fullDebug”版本，构建系统需要合并来自以下源代码集的代码、设置和资源：

- src/fullDebug/（构建变体源代码集）
- src/debug/（构建类型源代码集）
- src/full/（产品变种源代码集）
- src/main/（主源代码集）




如果不同源代码集包含同一文件的不同版本，
Gradle 将按以下优先顺序决定使用哪一个文件（左侧源代码集替换右侧源代码集的文件和设置）

构建变体 > 构建类型 > 产品变种 > 主源代码集 > 库依赖项
这样一来，Gradle 便可使用专用于您试图构建的构建变体的文件，同时重复利用与应用的其他版本共用的 Activity、应用逻辑和资源。
在合并多个清单时，Gradle 会使用相同的优先顺序，这样每个构建变体都能在最终清单中定义不同的组件或权限



# 添加构建依赖项

## 依赖项类型

如需向您的项目添加依赖项，请在 build.gradle 文件的 dependencies 代码块中指定依赖项配置
```
apply plugin: 'com.android.application'

android { ... }

dependencies {
    // Dependency on a local library module
    implementation project(":mylibrary")

    // Dependency on local binaries
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    // Dependency on a remote binary
    implementation 'com.example.android:app-magic:12.3'
}

```

## 依赖项配置

在 dependencies 代码块内，您可以从多种不同的依赖项配置中选择其一（如上面所示的 implementation）来声明库依赖项。
每种依赖项配置都向 Gradle 提供了有关如何使用该依赖项的不同说明

- implementation

Gradle 会将依赖项添加到编译类路径，**并将依赖项打包到构建输出**。
不过，当您的模块配置 implementation 依赖项时，会让 Gradle 了解您**不希望该模块在编译时将该依赖项泄露给其他模块**。
也就是说，**其他模块只有在运行时才能使用该依赖项**

使用此依赖项配置代替 api 或 compile（已弃用）可以显著缩短构建时间，因为这样可以减少构建系统需要重新编译的模块数。
例如，如果 implementation 依赖项更改了其 API，Gradle 只会重新编译该依赖项以及直接依赖于它的模块。
大多数应用和测试模块都应使用此配置。

- api
Gradle 会将依赖项添加到编译类路径和构建输出。
当一个模块包含 api 依赖项时，会让 Gradle 了解该模块要**以传递方式将该依赖项导出到其他模块，以便这些模块在运行
时和编译时都可以使用该依赖项**。

此配置的行为类似于 compile（现已弃用），但使用它时应格外小心，只能对您需要以传递方式导出到其他上游消费者的依赖项
使用它。这是因为，如果 api 依赖项更改了其外部 API，Gradle 会在编译时重新编译所有有权访问该依赖项的模块。
因此，拥有大量的 api 依赖项会显著增加构建时间。除非要将依赖项的 API 公开给单独的模块，否则库模块应改用
implementation 依赖项

- compileOnly
Gradle 只会将依赖项添加到编译类路径（也就是说，**不会将其添加到构建输出**）。
如果您创建 Android 模块时在编译期间需要相应依赖项，但它在运行时可有可无，此配置会很有用。

如果您使用此配置，那么您的库模块必须包含一个运行时条件，用于检查是否提供了相应依赖项，然后适当地改变该模块的行为
，以使该模块在未提供相应依赖项的情况下仍可正常运行。这样做不会添加不重要的瞬时依赖项，因而有助于减小最终 APK 的
大小。此配置的行为类似于 provided（现已弃用）

- runtimeOnly
Gradle 只会将依赖项添加到构建输出，以便在运行时使用。
也就是说，4**不会将其添加到编译类路径**。此配置的行为类似于 apk（现已弃用）


- annotationProcessor
如需添加对作为注释处理器的库的依赖关系，您必须使用 annotationProcessor 配置将其添加到注释处理器类路径。
这是因为，使用此配置可以将编译类路径与注释处理器类路径分开，从而提高构建性能。如果 Gradle 在编译类路径上找到注释
处理器，则会禁用避免编译功能，这样会对构建时间产生负面影响（Gradle 5.0 及更高版本会忽略在编译类路径上找到的注释处理器）

如果 JAR 文件包含以下文件，则 Android Gradle 插件会假定依赖项是注释处理器：
META-INF/services/javax.annotation.processing.Processor。
如果插件检测到编译类路径上包含注释处理器，则会生成构建错误。


注意：**Kotlin 项目应使用 kapt 声明注解处理器依赖项**

- lintPublish
在 Android 库项目中使用此配置可以添加您希望 Gradle 编译成 lint.jar 文件并打包在 AAR 中的 lint 检查。
这会使得使用 AAR 的项目也应用这些 lint 检查。如果您之前使用 lintChecks 依赖项配置将 lint 检查包含在已发布的
 AAR 中，则需要迁移这些依赖项以改用 lintPublish 配置。



以上配置会将依赖项应用于所有构建变体。
如果您只想为特定的构建变体源代码集或测试源代码集声明依赖项，则必须**将配置名称的首字母大写，并在其前面加上构建变体
或测试源代码集的名称作为前缀**

例如，如需只向“free”产品变种添加 implementation 依赖项（使用远程二进制文件依赖项），请使用如下所示的代码：
```
dependencies {
    freeImplementation 'com.google.firebase:firebase-ads:9.8.0'
```


不过，如果您想为将产品变种和构建类型组合在一起的变体添加依赖项，就必须在 configurations 代码块中初始化配置名称。
以下示例向“freeDebug”构建变体添加了 runtimeOnly 依赖项（使用本地二进制文件依赖项）：
```
configurations {
    // Initializes a placeholder for the freeDebugRuntimeOnly dependency
    // configuration.
    freeDebugRuntimeOnly {}
}

dependencies {
    freeDebugRuntimeOnly fileTree(dir: 'libs', include: ['*.jar'])
}
```

如需为本地测试和插桩测试添加 implementation 依赖项，请使用如下所示的代码：
```
dependencies {
    // Adds a remote binary dependency only for local tests.
    testImplementation 'junit:junit:4.12'

    // Adds a remote binary dependency only for the instrumented test APK.
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
}
```


## 排除传递依赖项
随着应用的范围不断扩大，它可能会包含许多依赖项，包括直接依赖项和传递依赖项（应用中导入的库所依赖的库）。
如需排除不再需要的传递依赖项，您可以使用 exclude 关键字，如下所示：
```
dependencies {
    implementation('some-library') {
        exclude group: 'com.example.imgtools', module: 'native'
    }
}
```



# 合并多个清单文件

APK 文件只能包含一个 AndroidManifest.xml 文件，但 Android Studio 项目可以包含多个清单文件，这些清单文件由
主源代码集、构建变体和导入的库提供。因此，在构建应用时，Gradle 构建系统会将所有清单文件合并到一个打包在 APK 中
的清单文件中

## 合并优先级
合并工具会根据每个清单文件的优先级按顺序合并，将所有清单文件组合到一个文件中。
例如，如果您有三个清单文件，则会先将优先级最低的清单合并到优先级第二高的清单中，
然后再将合并后的清单合并到优先级最高的清单中

有三种基本的清单文件可以互相合并，它们的合并优先级如下（按优先级由高到低的顺序）：
1. 构建变体的清单文件
2. 应用模块的主清单文件
3. 所包含的库中的清单文件


# 将构建变量注入清单

如果您需要将变量插入在 build.gradle 文件中定义的 AndroidManifest.xml 文件，
可以使用 manifestPlaceholders 属性执行此操作。此属性采用键值对的映射，如下所示：
```
android {
    defaultConfig {
        manifestPlaceholders = [hostName:"www.example.com"]
    }
    ...
}
```

然后，您可以将某个占位符作为属性值插入清单文件，如下所示：
```
<intent-filter ... >
    <data android:scheme="http" android:host="${hostName}" ... />
    ...
</intent-filter>
```

# 缩减、混淆处理和优化应用

为了尽可能减小应用的大小，您应在发布 build 中启用缩减功能来移除不使用的代码和资源。
启用缩减功能后，您还会受益于两项功能，
一项是混淆处理功能，该功能会缩短应用的类和成员的名称；
另一项是优化功能，该功能会采用更积极的策略来进一步减小应用的大小


当您使用 Android Gradle 插件 3.4.0 或更高版本构建项目时，该插件不再使用 ProGuard 执行编译时代码优化，
而是与 R8 编译器协同工作，处理以下编译时任务:

- 代码缩减（即摇树优化）：从应用及其库依赖项中检测并安全地移除不使用的类、字段、方法和属性。
例如，如果您仅使用某个库依赖项的少数几个 API，缩减功能可以识别应用不使用的库代码并仅从应用中移除这部分代码

- 资源缩减：从封装应用中移除不使用的资源，包括应用库依赖项中不使用的资源。此功能可与代码缩减功能结合使用，
这样一来，移除不使用的代码后，也可以安全地移除不再引用的所有资源

- 混淆处理：缩短类和成员的名称，从而减小 DEX 文件的大小。

- 优化：检查并重写代码，以进一步减小应用的 DEX 文件的大小。
例如，如果 R8 检测到从未采用过给定 if/else 语句的 else {} 分支，则会移除 else {} 分支的代码


默认情况下，在构建应用的发布版本时，R8 会自动执行上述编译时任务。
不过，您也可以停用某些任务或通过 ProGuard 规则文件自定义 R8 的行为。
事实上，R8 支持所有现有 ProGuard 规则文件，因此您在更新 Android Gradle 插件以使用 R8 时，无需更改现有规则


## 启用缩减、混淆处理和优化功能
当您使用 Android Studio 3.4 或 Android Gradle 插件 3.4.0 及更高版本时，R8 是默认编译器，
用于将项目的 Java 字节码转换为在 Android 平台上运行的 DEX 格式

不过，当您使用 Android Studio 创建新项目时，**缩减、混淆处理和代码优化功能默认处于停用状态**。
这是因为，这些编译时优化功能会增加项目的构建时间，而且如果您没有充分自定义要保留的代码，还可能会引入错误

因此，在构建应用的最终版本（也就是在发布应用之前测试的版本）时，最好启用这些编译时任务。
如需启用缩减、混淆处理和优化功能，请在项目级 build.gradle 文件中添加以下代码。

```
android {
    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            minifyEnabled true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            shrinkResources true

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles getDefaultProguardFile(
                    'proguard-android-optimize.txt'),
                    'proguard-rules.pro'
        }
    }
    ...
}

```

## R8 配置文件
R8 使用 ProGuard 规则文件来修改其默认行为并更好地了解应用的结构

## 自定义要保留的代码
在大多数情况下，如要让 R8 仅移除不使用的代码，使用默认的 ProGuard 规则文件 (proguard-android- optimize.txt) 就已足够
不过，在某些情况下，R8 很难做出正确判断，因而可能会移除应用实际上需要的代码。

下面列举了几个示例，说明它在什么情况下可能会错误地移除代码：
- 当应用通过 Java 原生接口 (JNI) 调用方法时
- 当您的应用在运行时查询代码时（如使用反射）

如需修复错误并强制 R8 保留某些代码，请在 ProGuard 规则文件中添加 -keep 代码行。例如：

-keep public class MyClass

或者，您也可以为要保留的代码添加 @Keep 注释。
在类上添加 @Keep 可按原样保留整个类。在方法或字段上添加该注释，将使该方法/字段（及其名称）以及类名称保持不变


## 缩减资源
资源缩减只有在与代码缩减配合使用时才能发挥作用。
在代码缩减器移除所有不使用的代码后，资源缩减器便可确定应用仍要使用的资源，当您添加包含资源的代码库时尤其如此


## 自定义要保留的资源
如果您有想要保留或舍弃的特定资源，请在项目中创建一个包含 <resources> 标记的 XML 文件，并在 tools:keep 属性中
指定每个要保留的资源，在 tools:discard 属性中指定每个要舍弃的资源。
这两个属性都接受以逗号分隔的资源名称列表。您可以将星号字符用作通配符。

```
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:keep="@layout/l_used*_c,@layout/l_used_a,@layout/l_used_b*"
    tools:discard="@layout/unused2" />
```

将该文件保存在项目资源中，例如，保存在 res/raw/keep.xml 中。构建系统不会将此文件打包到 APK 中。













