
# 一、 签名

## 配置
```
android{
    signingConfigs {
        release {
            storeFile file('E:\\AndroidProject\\Study\\AndroidAll\\key.jks')
            storePassword '123456789'
            keyAlias = 'key0'
            keyPassword '123456789'
        }
    }
    ...
    ...
}
```

7.0以上的手机优先检测V2签名，如果V2签名不存在，再校验V1签名，
对于7.0以下的手机，不存在V2签名校验机制，只会校验V1，
所以，如果你的App的miniSdkVersion<24(N)，那么你的签名方式必须内含V1签名

!(签名校验流程)["./pics/sign.png"]

## v1

在apk的META-INFO中生成3个文件
- MANIFEST.MF：摘要文件，存储文件名与文件SHA1摘要（Base64格式）键值对
- CERT.SF ：二次摘要文件，存储文件名与MANIFEST.MF摘要条目的SHA1摘要（Base64格式）键值对
- CERT.RSA ： 证书（公钥）及签名文件，存储keystore的公钥、发行信息、以及对CERT.SF文件摘要的签名信息（利用keystore的私钥进行加密过）

> 除了CERT.RSA文件，其余两个签名文件其实跟keystore没什么关系，主要是文件自身的摘要及二次摘要，
用不同的keystore进行签名，生成的MANIFEST.MF与CERT.SF都是一样的，不同的只有CERT.RSA签名文件。
也就是说前两者主要保证各个文件的完整性，CERT.RSA从整体上保证APK的来源及完整性，
不过META_INF中的文件不在校验范围中，这也是V1的一个缺点

## v2

v2签名不针对单个文件校验了，
而是针对APK进行校验，将APK分成1M的块，对每个块计算值摘要，
之后针对所有摘要进行摘要，再利用摘要进行签名

## 覆盖安装
覆盖安装除了检验APK自己的完整性以外，还要校验证书是否一致只有证书一致（同一个keystore签名），
才有可能覆盖升级。覆盖安装同全新安装相比较多了几个校验：
- 包名一致
- 证书一致
- versioncode不能降低

## 签名总结：
- V1签名靠META_INFO文件夹下的签名文件
- V2签名依靠中央目录前的V2签名快，ZIP的目录结构不会改变，当然结尾偏移要改
- V1 V2签名可以同时存在（miniSdkVersion 7.0以下如果没有V1签名是不可以的）

## 反编译工具
- ApkTool: https://ibotpeaches.github.io/Apktool/
- Dex2Jar: https://sourceforge.net/projects/dex2jar/
- JD-GUI: http://jd.benow.ca/jd-eclipse/update/#jd-gui-download


## 版本更新 && 下载 && DownloadManager
> https://juejin.im/post/5d23ff32f265da1b904c06e8#heading-17
> https://github.com/NewHuLe/AppUpdate
!(版本更新)["../pics/update.png"]


# 二、代码混淆


# 三、 内部存储/外部存储
### 摘要
Android 使用 VFS (Virtual File System) 虚拟文件系统。
VFS提供了供存储设备挂载的节点，同一存储设备经过分区后，不同的分区可以挂载到不同的节点上，如手机的内置存储卡

### 关键字
- 内置存储卡 / 外置SD卡
- 内部存储 / 外部存储

### 目录结构
!(存储结构)["./pics/storage.png"]

# 四、权限
 详见 PermissionHelper 类

# 五、Bitmap
## Bitmap 的大小
 一个 BitMap 位图占用的内存  = 图片长度 * 图片宽度 * 单位像素占用的字节数
 使用 BitmapFactory 来 decode 一张 bitmap 时，
 其单位像素占用的字节数由其参数 BitmapFactory.Options 的 inPreferredConfig 变量决定。
 (注：drawable目录下有的png图使用Bitmap.Config.RGB_565和ARGB_8888decode出来的大小一样，未解)

- ALPHA_8: 只有alpha值，没有RGB值，占一个字节。计算：size = wh
- ARGB_4444: 一个像素占用2个字节，alpha(A)值，Red（R）值，Green(G)值，Blue（B）值各占4个bites,共16bites，
             这种格式的图片，看起来质量太差，已经不推荐使用。计算：size=wh2
- ARGB_8888: 一个像素占用4个字节，alpha(A)值，Red（R）值，Green(G)值，Blue（B）值各占8个bites,共32bites,即4个字节。
             这是一种高质量的图片格式，电脑上普通采用的格式。android2.3开始的默认格式。计算：size=wh4
- RGB_565:   一个像素占用2个字节，没有alpha(A)值，即不支持透明和半透明，Red（R）值占5个bites ，Green(G)值占6个bites
            ，Blue（B）值占5个bites,共16bites,即2个字节。对于没有透明和半透明颜色的图片并且不需要颜色鲜艳的来说，
            该格式的图片能够达到比较的呈现效果，相对于ARGB_8888来说也能减少一半的内存开销，因此它是一个不错的选择。
            计算：size=wh2

## Bitmap 加载到内存中所占用内存的大小

## Bitmap 转换与操作