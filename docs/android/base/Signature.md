[toc]
# Android 打包和签名机制 
[Android打包、签名与混淆](https://juejin.cn/post/6844904081903861773)
[Android V1及V2签名原理简析](https://juejin.cn/post/6844903839745720333)


# Android打包流程
![打包流程](/pics/android/base/打包流程.png)


1. 打包资源文件Resource Files，生成R.java文件（AAPT：Android Asset Packaging Tool）
2. 处理AIDL文件，生成java代码（AIDL：Android Interface Definition Language，实现进程间的通信）
3. 编译代码文件（Source code），生成对应的.class文件
4. .class文件转化为dex文件
5. 将dex文件和编译的资源文件组合成成apk（apk-builder）
6. 使用调试或发布密钥库签名（jarsigner）
7. 应用优化，减少运行时占用的内存（zipalign：归档对其工具 —> mmap内存映射 —> 减少运行时消耗的内存）



# 签名


## 什么是签名？

了解 HTTPS 通信的同学应该知道，在消息通信时，必须至少解决两个问题：
一是确保消息来源的真实性，
二是确保消息不会被第三方篡改。

在安装 APK 时，同样需要确保 APK 来源的真实性，以及 APK 没有被第三方篡改。如何解决这两个问题呢？
方法就是开发者对 APK 进行签名：在 APK 中写入一个「指纹」。
指纹写入以后，APK 中有任何修改，都会导致这个指纹无效，Android 系统在安装 APK 进行签名校验时就会不通过，从而保证了安全性。


Android签名机制不能阻止APK包被修改，但修改后的再签名无法与原先的签名保持一致（除非你拥有私钥）。
也就是说如果你的软件还是会被反编译的，但其他人反编译后再打包的话其实是另一个应用

综上所述：签名机制其实是对APK包完整性和发布机构唯一性的一种校验机制。能简单的保证apk的安全，但很有限，
应该说是一个软件最基本的防御。我们可以适当的提升下安全系数，在程序运行的时候进行签名对比，但被破解后，
让程序跳过签名对比的部分，就能破坏了这最基本的防御。









要了解如何实现签名，需要了解两个基本概念：消息摘要、数字签名和数字证书


## 消息摘要(Message Digest)
消息摘要（Message Digest），又称数字摘要（Digital Digest）或数字指纹（Finger Print）。
简单来说，消息摘要就是在消息数据上，执行一个单向的 Hash 函数，生成一个固定长度的Hash值，这个Hash值即是消息摘要。

上面提到的的加密 Hash 函数就是消息摘要算法。它有以下特征：
- 无论输入的消息有多长，计算出来的消息摘要的长度总是固定的。
- 消息摘要看起来是「随机的
  这些比特看上去是胡乱的杂凑在一起的。可以用大量的输入来检验其输出是否相同，一般，不同的输入会有不同的输出，
  而且输出的摘要消息可以通过随机性检验。但是，一个摘要并不是真正随机的，因为用相同的算法对相同的消息求两次摘要，
  其结果必然相同；而若是真正随机的，则无论如何都是无法重现的。因此消息摘要是「伪随机的」
  
- 消息摘要函数是单向函数，即只能进行正向的信息摘要，而无法从摘要中恢复出任何的消息，甚至根本就找不到任何与原信息相关的信息。
- 好的摘要算法，没有人能从中找到「碰撞」。或者说，无法找到两条消息，使它们的摘要相同。


消息摘要算法被广泛应用在「数字签名」领域，作为对明文的摘要算法。
著名的消息摘要算法有 RSA 公司的 **MD5 算法和 SHA-1 算法**及其大量的变体
SHA-256 是 SHA-1 的升级版，现在 Android 签名使用的默认算法都已经升级到 SHA-256 了。

消息摘要的这种特性，很适合来验证数据的完整性。
比如：在网络传输过程中下载一个大文件 BigFile，我们会同时从网络下载 BigFile 和 BigFile.md5，
BigFile.md5 保存 BigFile 的摘要，我们在本地生成 BigFile 的消息摘要和 BigFile.md5 比较，如果内容相同，则表示下载过程正确。


## 数字签名（Digital Signature）

数字签名方案是一种以电子形式存储消息签名的方法。一个完整的数字签名方案应该由两部分组成：签名算法和验证算法。
在讲数字签名之前，我们先简单介绍几个相关知识点：「公钥密码体制」、「对称加密算法」、「非对称加密算法」。

### 公钥密码体制（public-key cryptography）

公钥密码体制分为三个部分，公钥、私钥、加密解密算法，它的加密解密过程如下：
加密：通过加密算法和公钥对内容（或者说明文）进行加密，得到密文。加密过程需要用到公钥。
解密：通过解密算法和私钥对密文进行解密，得到明文。解密过程需要用到解密算法和私钥。
注意，由公钥加密的内容，只能由私钥进行解密，也就是说，由公钥加密的内容，如果不知道私钥，是无法解密的。

公钥密码体制的公钥和算法都是公开的（这是为什么叫公钥密码体制的原因），私钥是保密的。
大家都以使用公钥进行加密，但是只有私钥的持有者才能解密

在实际 的使用中，有需要的人会生成一对公钥和私钥，把公钥发布出去给别人使用，自己保留私钥。
目前使用最广泛的公钥密码体制是 RSA 密码体制。

### 对称加密算法（symmetric key algorithms）
在对称加密算法中，加密和解密都是使用的同一个密钥。
因此对称加密算法要保证安全性的话，密钥要做好保密，只能让使用的人知道，不能对外公开。

### 非对称加密算法（asymmetric key algorithms）
在非对称加密算法中，加密使用的密钥和解密使用的密钥是不相同的。
前面所说的公钥密码体制就是一种非对称加密算法，它的公钥和是私钥是不能相同的，
也就是说加密使用的密钥和解密使用的密钥不同，因此它是一个非对称加密算法。


## RSA 简介
RSA 密码体制是一种公钥密码体制，公钥公开，私钥保密，它的加密解密算法是公开的。 
由公钥加密的内容可以并且只能由私钥进行解密，而由私钥加密的内容可以并且只能由公钥进行解密。
也就是说，RSA 的这一对公钥、私钥都可以用来加密和解密，并且一方加密的内容可以由并且只能由对方进行解密。

- 加密：公钥加密，私钥解密的过程，称为「加密」。
因为公钥是公开的，任何公钥持有者都可以将想要发送给私钥持有者的信息进行加密后发送，而这个信息只有私钥持有者才能解密。

- 签名：私钥加密，公钥解密的过程，称为「签名」。
它和加密有什么区别呢？因为公钥是公开的，所以任何持有公钥的人都能解密私钥加密过的密文，
所以**这个过程并不能保证消息的安全性，但是它却能保证消息来源的准确性和不可否认性**，
也就是说，如果使用公钥能正常解密某一个密文，那么就能证明这段密文一定是由私钥持有者发布的，
而不是其他第三方发布的，并且私钥持有者不能否认他曾经发布过该消息。故此将该过程称为「签名」


# Android 中的签名方案

Android 的签名方案，发展到现在，不是一蹴而就的。Android 现在已经支持三种应用签名方案：

- v1 方案：基于 JAR 签名。
- v2 方案：APK 签名方案 v2，在 Android 7.0 引入
- v3 方案：APK 签名方案 v3，在 Android 9.0 引入

v1 到 v2 是颠覆性的，为了解决 JAR 签名方案的安全性问题，而到了 v3 方案，其实结构上并没有太大的调整，
可以理解为 v2 签名方案的升级版，有一些资料也把它称之为 v2+ 方案。

因为这种签名方案的升级，就是向下兼容的，所以只要使用得当，这个过程对开发者是透明的。

v1 到 v2 方案的升级，对开发者影响最大的，就是渠道签署的问题。
在当下这个大环境下，我们想让不同渠道、市场的安装包有所区别，携带渠道的唯一标识，这就是我们俗称的渠道包。
好在各大厂都开源了自己的签渠道方案，例如：Walle（美团）、VasDolly（腾讯）都是非常优秀的方案。


# V1 和 V2 签名

V1签名是通过META-INF中的三个文件保证签名及信息的完整性：
![V1](/pics/android/base/v1.png)



如果只有V2签名，那么APK包内容几乎是没有改动的，META_INF中不会有新增文件，
按Google官方文档：在使用v2签名方案进行签名时，会在APK文件中插入一个APK签名分块，
该分块位于zip中央目录部分之前并紧邻该部分。在APK签名分块内，签名和签名者身份信息会存储在APK签名方案v2分块中，
保证整个APK文件不可修改，

![V2](/pics/android/base/v2.png)



# 签名校验过程

Android 的签名方案，无论怎么升级，都是要确保向下兼容。

7.0以上的手机优先检测V2签名，如果V2签名不存在，再校验V1签名，对于7.0以下的手机，不存在V2签名校验机制，只会校验V1

在引入 v3 方案后，Android 9.0 及更高版本中，可以根据 APK 签名方案，v3 -> v2 -> v1 依次尝试验证 APK。
而较旧的平台会忽略 v3 签名并尝试 v2 签名，最后才去验证 v1 签名。


























