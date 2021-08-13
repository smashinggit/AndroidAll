[toc]





一、概述

Retrofit是一个网络请求框架的封装，把每一个http的api请求变成java接口，只是一个restful风格网络请求框架的封装，而不是网络请求框架，其主要工作都由其内核okhttp来完成。Retrofit主要完成数据的转化与适配。Retrofit设计模式使用了许多设计模式，所以具有很多的扩展性，能与Rxjava、json、okhttp等主流库进行无缝对接。

















# 二、CONVERTERS  转换器



### 概述

**默认情况下，Retrofit 只能将 HTTP body 反序列化为 OkHttp 的 `ResponseBody`类型，并且  `@Body` 的类型只能是 `RequestBody`**

转换器 CONVERTERS 可以添加其他的类型

例如：
```kotlin
    api "com.squareup.retrofit2:converter-gson:2.0.2"
    api "org.ligboy.retrofit2:converter-fastjson-android:2.2.0"
```



### 自定义 Converter



```
```













# 三、Adapter