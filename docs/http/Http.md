[toc]

# Http


# 

















# TCP和UDP的区别：
TCP 提供面向连接的服务。在传送数据之前必须先建立连接，数据传送结束后要释放连接
UDP 在传送数据之前不需要先建立连接，远地主机在收到 UDP 报文后，不需要给出任何确认。
虽然 UDP 不提供可靠交付，但在某些情况下 UDP 确是一种最有效的工作方式（一般用于即时通信），比如： QQ 语音、 QQ 视频 、直播等等

1. TCP面向连接，UDP无连接
2. TCP面向字节流（文件传输），
   UDP是面向报文的，UDP没有拥塞控制，因此网络出现拥塞不会使源主机的发送速率降低（对IP电话，实时视频会议等）
3. TCP首部开销20字节，UDP的首部开销小，只有8个字节
4. TCP提供可靠的服务。也就是说，通过TCP连接传送的数据，无差错，不丢失，不重复，且按序到达;
   UDP尽最大努力交付，即不保证可靠交付   
5. 每一条TCP连接只能是点到点的；UDP支持一对一，一对多，多对一和多对多的交互通信   
6. TCP的逻辑通信信道是全双工的可靠信道，UDP则是不可靠信道


# HTTP和HTTPS的区别
  HTTP协议时超文本传输协议
  HTTPS是安全的超文本传输协议，是安全版的HTTP协议，使用安全套接字层(SSL)进行信息交换
  
HTTPS协议主要针对解决HTTP协议以下不足：
1. 通信使用明文（不加密），内容可能会被窃听
2. 不验证通信方身份，应此可能遭遇伪装
3. 无法证明报文的完整性（即准确性），所以可能已遭篡改

> HTTP+加密+认证+完整性保护=HTTPS

HTTP端口 80
HTTPS端口 443


# TCP 三次握手和四次挥手

## 三次握手

1. 客户端– 发送带有 SYN 标志的数据包–      一次握手–服务端
2. 服务端– 发送带有 SYN/ACK 标志的数据包–  二次握手–客户端
3. 客户端– 发送带有带有 ACK 标志的数据包–   三次握手–服务端

## 为什么要三次握手
三次握手的目的是建立可靠的通信信道，说到通讯，简单来说就是数据的发送与接收，
而三次握手最主要的目的就是双方确认自己与对方的发送与接收是正常的。

第一次握手：Client 什么都不能确认；Server 确认了对方发送正常，自己接收正常
第二次握手：Client 确认了：自己发送、接收正常，对方发送、接收正常；Server 确认了：对方发送正常，自己接收正常
第三次握手：Client 确认了：自己发送、接收正常，对方发送、接收正常；Server 确认了：自己发送、接收正常，对方发送、接收正常

所以三次握手就能确认双发收发功能都正常，缺一不可。



## 四次挥手

客户端-发送一个 FIN，用来关闭客户端到服务器的数据传送
服务器-收到这个 FIN，它发回一 个 ACK，确认序号为收到的序号加1 。和 SYN 一样，一个 FIN 将占用一个序号
服务器-关闭与客户端的连接，发送一个FIN给客户端
客户端-发回 ACK 报文确认，并将确认序号设置为收到序号加1

任何一方都可以在数据传送结束后发出连接释放的通知，待对方确认后进入半关闭状态。
当另一方也没有数据再发送的时候，则发出连接释放通知，对方确认后就完全关闭了TCP连接。

举个例子：A 和 B 打电话，通话即将结束后，A 说“我没啥要说的了”，B回答“我知道了”，
但是 B 可能还会有要说的话，A 不能要求 B 跟着自己的节奏结束通话，于是 B 可能又巴拉巴拉说了一通，
最后 B 说“我说完了”，A 回答“知道了”，这样通话才算结束。

# Cookie的作用是什么?和Session有什么区别

Cookie 和 Session都是用来跟踪浏览器用户身份的会话方式，但是两者的应用场景不太一样。
Cookie 数据保存在客户端(浏览器端)，
Session 数据保存在服务器端。

# URI和URL的区别是什么
- URI(Uniform Resource Identifier) 是统一资源标志符，可以唯一标识一个资源。
- URL(Uniform Resource Location) 是统一资源定位符，可以提供该资源的路径。
它是一种具体的 URI，即 URL 可以用来标识一个资源，而且还指明了如何 locate 这个资源

URI的作用像身份证号一样，
URL的作用更像家庭住址一样。URL是一种具体的URI，它不仅唯一标识资源，而且还提供了定位该资源的信息。