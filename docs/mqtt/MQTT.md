[toc]



#

MQTT（Message Queuing Telemetry Transport，消息队列遥测传输）是IBM开发的一个即时通讯协议。
它是一种发布/订阅，极其简单和轻量级的消息传递协议，专为受限设备和低带宽，高延迟或不可靠的网络而设计。
它的设计思想是轻巧、开放、简单、规范，易于实现。这些特点使得它对很多场景来说都是很好的选择，特别是对于受限的环
境如机器与机器的通信（M2M）以及物联网环境。相对于XMPP，MQTT更加轻量级，并且占用的宽带低


MQTT协议有以下特点：

使用发布/订阅消息模式，提供一对多的消息发布，解除应用程序耦合。
对负载内容屏蔽的消息传输。
使用 TCP/IP 提供网络连接。
有三种消息发布服务质量：
qos为0：“至多一次”，消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
qos为1：“至少一次”，确保消息到达，但消息重复可能会发生。这一级别可用于如下情况，你需要获得每一条消息，并且消息重复发送对你的使用场景无影响。
qos为2：“只有一次”，确保消息到达一次。这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果。
小型传输，开销很小（固定长度的头部是 2 字节），协议交换最小化，以降低网络流量。
使用 Last Will 和 Testament 特性通知有关各方客户端异常中断的机制





#  MQTT服务器搭建

想要使用MQTT，首先需要搭建一个MQTT的服务器（在公司一般是后台人员负责搭建）。
一般前端人员为了方便测试都会先使用第三方提供的服务器，官方推荐了很多种服务器，这里选用EMQ X 


[EMQ X 开源版](https://www.emqx.com/zh/downloads?product=broker) 



启用 Dashboard
EMQ X Dashboard 是一个 Web 应用程序，你可以直接通过浏览器来访问它，无需安装任何其他软件。

当 EMQ X 成功运行在你的本地计算机上且 EMQ X Dashboard 被默认启用时，你可以访问 http://localhost:18083 来查看你的 Dashboard，
默认用户名是 admin，密码是 public








# MQTT 客户端


[MQTT X](https://mqttx.app/zh)

MQTT X 是 EMQ 开源的一款优雅的跨平台 MQTT 5.0 桌面客户端，它支持 macOS, Linux, Windows。




# Android中 MQTT 的使用

Android中使用MQTT需要使用到 Paho Android Service 库，Paho Android Service 是一个用 Java 编写的 MQTT 客户端库。
GitHub地址：[Paho Android Service](https://github.com/eclipse/paho.mqtt.android)









