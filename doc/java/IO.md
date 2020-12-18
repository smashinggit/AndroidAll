[toc]

# 概述

java.io 包几乎包含了所有操作输入、输出需要的类。所有这些流类代表了输入源和输出目标。
java.io 包中的流支持很多种格式，比如：基本类型、对象、本地化字符集等等。
一个流可以理解为一个数据的序列。输入流表示从一个源读取数据，输出流表示向一个目标写数据。
Java 为 I/O 提供了强大的而灵活的支持，使其更广泛地应用到文件传输和网络编程中。

Java 的 I/O 大概可以分成以下几类：

1. 磁盘操作：File
2. 字节操作：InputStream 和 OutputStream
3. 字符操作：Reader 和 Writer
4. 对象操作：Serializable
5. 网络操作：Socket
6. 新的输入/输出：NIO


# 一、磁盘操作 File 

## 简介
An abstract representation of file and directory pathnames

Java文件类在java.io包中，它以抽象的方式代表文件名和目录路径名。
该类主要用于获取文件和目录的属性，文件和目录的创建、查找、删除、重命名等,但不能进行文件的读写操作

File(File parent, String child)
通过给定的父抽象路径名和子路径名字符串创建一个新的File实例。

File(String pathname)
通过将给定路径名字符串转换成抽象路径名来创建一个新 File 实例。

File(String parent, String child)
根据 parent 路径名字符串和 child 路径名字符串创建一个新 File 实例。

File(URI uri)
通过将给定的 file: URI 转换成一个抽象路径名来创建一个新的 File 实例。


注意:
1.在各个操作系统中，路径的分隔符是不一样的，例如：Windows中使用反斜杠："\"，Linux|Unix中使用正斜杠："/"。
在使用反斜杠时要写成"\\"的形式，因为反斜杠要进行转义。
如果要让Java保持可移植性，应该使用File类的**静态常量File.pathSeparator**

2. **构建一个File实例并不会在机器上创建一个文件**。不管文件是否存在，都可以创建任意文件名的File实例。
可以调用File实例上的exists()方法来判断这个文件是否存在
**当把一个输出流绑定到一个不存在的File实例上时，会自动在机器上创建该文件，
如果文件已经存在，把输出流绑定到该文件上则会覆盖该文件，但这些都不是在创建File实例时进行的**

## 重要方法

- public boolean mkdir()
- public boolean mkdirs()
mkdir必须满足路径上的父文件夹全都存在
mkdirs是递归创建文件夹，允许在创建某文件夹时其父文件夹不存在,从而一同创建;

-  public long length() 
Returns the length of the file denoted by this abstract pathname
返回文件长度，目录无效

- public boolean renameTo(File dest)
重命名

-  public File[] listFiles() 
-  public String[] list()

- public long getTotalSpace()
Returns the size of the partition named by this abstract pathname

- public long getUsableSpace()
Returns the number of bytes available to this virtual machine on the partition named by this abstract pathname.

- public long getFreeSpace()
Returns the number of **unallocated bytes** in the partition named by this abstract path name

- public boolean delete()
Deletes the file or directory denoted by this abstract pathname. 
If this pathname denotes a directory, then **the directory must be empty** in order to be deleted
如果是目录的话，目录必须为空

- public void deleteOnExit() 
Requests that the file or directory denoted by this abstract pathname be deleted 
**when the virtual machine terminates**

-  public long lastModified()
-  public boolean setLastModified(long time) 
 Sets the last-modified time of the file or directory named by this abstract pathname



假设 /cache/pic 这两个目录不存在，使用以下方式创建:
```
        File file = new File("extra/cache/pic", "image.jpg");
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
```
# 流

## 简介
在Java程序中**所有的数据都是以流的方式进行传输或保存的**，
程序需要数据的时候要使用输入流读取数据，而当程序需要将一些数据保存起来的时候，就要使用输出流完成。
程序中的输入输出都是以流的形式保存的，**流中保存的实际上全都是字节文件**。
流涉及的领域很广：标准输入输出，文件的操作，网络上的数据流，字符串流，对象流，zip文件流等等

[流的传输.webp]


**流具有方向性**，至于是输入流还是输出流则是一个相对的概念，
一般以程序为参考，如果数据的流向是程序至设备，我们成为输出流，反之我们称为输入流

一个流可以理解为**一个数据的序列**。
输入流表示从一个源读取数据，输出流表示向一个目标写数据。


[IO流.png]


## 分类

### 1. 按操作数据类型分：字符流和字节流

编码（encode）和解码（decode）
计算机中储存的信息以二进制数表示，在屏幕上看到的信息（英文、汉字等字符）是二进制数转换而来的
按照某种规则将字符存储为二进制数，如'a'用什么表示，称为"编码"，反之，将二进制数解析并显示出来，称为"解码

**编码就是把字符转换为字节，而解码是把字节重新组合成字符**
**如果编码和解码过程使用不同的编码方式那么就出现了乱码**

- GBK 编码中，中文字符占 2 个字节，英文字符占 1 个字节；
- UTF-8 编码中，中文字符占 3 个字节，英文字符占 1 个字节；
- UTF-16be 编码中，中文字符和英文字符都占 2 个字节。

Java 使用双字节编码 UTF-16be，这不是指 Java 只支持这一种编码方式，而是说 char 这种类型使用UTF-16be 进行编码。
char 类型占 16 位，也就是两个字节，
Java 使用这种双字节编码是为了让一个中文或者一个英文都能使用一个 char 来存储。

#### 字节流
Java中的字节流处理的最基本单位为**单个字节(byte)**，它通常用来处理二进制数据,如果要得到字节对应的字符需要强制类型转换。


#### 字符流
Java中的字符流处理的最基本的单元是 **2字节的Unicode码元(char)**，它通常用来处理文本数据，如字符、字符数组
或字符串等

所谓Unicode码元，也就是一个Unicode代码单元，范围是0x0000~0xFFFF。在以上范围内的每个数字都与一个字符相对应
Java中的String类型默认就把**字符以Unicode规则编码而后存储在内存中**

然而与存储在内存中不同，存储在磁盘上的数据通常有着各种各样的编码方式。使用不同的编码方式，相同的字符会有不
同的二进制表示。
实际上字符流是这样工作的：
- 输出字符流：把要写入文件的字符序列(实际上是Unicode码元序列)转为指定编码方式下的字节序列，然后再写入到文件中。
- 输入字符流：把要读取的字节序列按指定编码方式解码为相应字符序列(实际上是Unicode码元序列从)从而可以存在内存中

也就是说，**所有的文件在硬盘或在传输时都是以字节的方式进行的**，包括图片等都是按字节的方式存储的，
而**字符是只有在内存中才会形成**


#### 字节流 vs 字符流

1. 字符流是由Java虚拟机将字节转化为2个字节的Unicode字符为单位的字符而成的，所以它对多国语言支持性较好
，如果要操作中文数据等，用字符流。
2. 字符流只用来处理文本数据，
   字节流还可以用来处理媒体数据，如视频、音频、图片等
3. 字符流的两个抽象基类为Reader和Writer，
   字节流的两个抽象基类为InputStream和OutputStream。它们的具体子类名以基类名为后缀进行扩展
4. 字节流在操作的时候不会用到缓冲区(内存)，是直接对文件本身操作的，
   而字符流在操作的时候使用缓冲区。
   
[字节流vs字符流.webp]

以向一个文件输出"Hello world!"为例，我们分别使用字节流和字符流进行输出，且在**使用完之后都不关闭流**

结果发现，
使用字节流，文本上会有内容显示，
使用字符流，文本上没有任何内容。 

这是因为**字符流操作时使用了缓冲区**，而在关闭字符流时会强制性地将缓冲区中的内容进行输出，但是如果程序没有
关闭字符流，缓冲区中的内容是无法输出的，所以得出结论：**字符流使用了缓冲区，而字节流没有使用缓冲区**

如果想让缓冲区中的内容输出，要么关闭流强制刷新缓冲区，要么调用flush方法冲刷缓冲区。可以简单地把缓冲区理解为
一段特殊的内存。某些情况下，如果一个程序频繁地操作一个资源(如文件或数据库)，则性能会很低，此时为了提升性能，
就可以将一部分数据暂时读入到内存的一块区域之中，以后直接从此区域中读取数据即可，因为读取内存速度会比较快，
这样可以提升程序的性能。
在字符流的操作中，所有的字符都是在内存中形成的，在输出前会将所有的内容暂时保存在内存之中，所以使用了缓冲区暂存数据。

#### 总结
1. 虽然不关闭字节流不影响数据的输出，且后续JVM会自动回收这部分内存，但还是建议在使用完任何流对象之后关闭流。
2. 在创建一个文件时，如果目录下有同名文件将被覆盖
3. 在写文件时，如果文件不存在，会在创建输出流对象并绑定文件时自动创建文件，不必使用File的exists方法提前检测
4. 在读取文件时，必须使用File的exists方法提前检测来保证该文件已存在，否则抛出FileNotFoundException


### 2. 按流向分：输入流和输出流

输入流：程序从输入流读取数据源。数据源包括外界(键盘、文件、网络等)，即是将数据源读入到程序的通信通道。
       输入流主要包括两个抽象基类：InputStream(字节输入流)和Reader(字符输入流)及其扩展的具体子类

输出流：程序向输出流写入数据。将程序中的数据输出到外界（显示器、打印机、文件、网络等）的通信通道。
       输出流主要包括两个抽象基类：OutputStream(字节输出流)和Writer(字符输出流)及其扩展的具体子类

### 3. 按功能分：节点流和处理流

按照流是否直接与特定的地方(如磁盘、内存、设备等)相连，分为节点流和处理流两类：
节点流：程序用于直接操作目标设备所对应的类叫节点流。(低级流)
处理流：程序通过一个间接流类去调用节点流类，以达到更加灵活方便地读写各种类型的数据，这个间接流类就是处理流。
        处理流可以看成是对已存在的流进行连接和封装的流。(高级流)
        
       
处理流中的Buffering缓冲流：
在读入或写出时，对数据进行缓存，以减少I/O的次数：
BufferedReader与BufferedWriter、BufferedInputStream与BufferedOutputStream
    
        
注意：
**在使用到处理流对流进行连接和封装时，读写完毕只需关闭处理流，不用关闭节点流。处理流关闭的时候，
  会调用其处理的节点流的关闭方法。如果将节点流关闭以后再关闭处理流，会抛出IO异常**

[节点流.webp]
[处理流.webp]


        
# 二、字节操作：InputStream 和 OutputStream

字节流主要是操作byte类型的数据，以byte数组为准，主要操作类是OutputStream、InputStream


## FileInputStream 、FileOutputStream

1. FileInputStream 

- public int available() throws IOException

- public int read() throws IOException
  读取一个字节，返回值为数据的下一个字节，读取不到数据时返回为-1
  
- public int read(byte b[]) throws IOException
  读取字节数组b.length 个数据并放入b中，
        
-  public int read(byte b[], int off, int len)
  从位置 off 处开始读，读取 len 个字节
  
  
注意: 
每调用一次read方法,当前读取在文件中的位置就会向后移动一个字节或者移动byte[]的长度(read的两个重载方法)，
已经到文件末尾会返回-1，可以通过read方法返回-1判断是否读到文件末尾，
也可以使用available方法返回下一次可以不受阻塞读取的字节数来读取。
**FileInputStream不支持mark和reset方法进行重复读取。BufferedInputStream支持此操作**

```
    /**
     * 读文件
     *
     * @param file
     */
    private static void readFile(File file) {
        try {
            System.out.println("文件大小 " + file.length());

            FileInputStream fileInputStream = new FileInputStream(file);
            System.out.println("可读大小 " + fileInputStream.available());

            StringBuffer result = new StringBuffer();
            byte[] buffer = new byte[1024]; //1kb

            while (fileInputStream.read(buffer) != -1) {
                result.append(new String(buffer));
            }

            System.out.println("读取结果： \n" + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



```  
2. FileOutputStream
该类用来创建一个文件并向文件中写数据。
**如果该流在打开文件进行输出前，目标文件不存在，那么该流会创建该文件**

```
    /**
     * 写文件
     */
    private static void writeFile(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);

            String content =
                    "锄禾日当午\n" +
                            "汗滴禾下土\n" +
                            "谁知盘中餐\n" +
                            "粒粒皆辛苦\n";

            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

# 三、字符操作：Reader 和 Writer

java提供了两个操作字符的字符流基类，分别是Writer和Reader。
先来了解两个用于读写文件的字符流FileReader(字符输入流)和FileWriter(字符输出流)

1. FileReader
2. FileWriter

字符流的操作比字节流操作方便一点，就是可以直接输出字符串。不在用再像之前那样进行字节转换操作了。
使用字符流默认情况下依然是覆盖已有的文件，如果想追加的话，则直接在FileWriter上增加一个可追加的标记即可




# 缓冲流(BufferedReader和BufferedWriter、BufferedInputStream和BufferedOutputStream)

缓冲流是一系列处理流(包装流)，目的是为了提高I/O效率，它们为I/O提供了内存缓冲区，这是一种常见的性能优化，
增加缓冲区的两个目的:
1. 允许Java的I/O一次不只操作一个字符，这样提高了整个系统的性能
2. 由于有缓冲区，使得在流上执行skip、mark和reset方法都成为可能

如果没有缓冲区，每次调用 read() 或 write()方法都会对文件进行读或写字节，在文件和内存之间发生字节和字符的转换，
这是极其低效的。


- BufferedInputStream

- BufferedOutputStream

- BufferedReader
BufferedReader是一个包装类，是为了提高读效率提供的，其可以接收一个Reader,然后用readLine()逐行读入字符流，
直到遇到换行符为止（相当于反复调用Reader类对象的read()方法读入多个字符）

**因此，建议用 BufferedReader 包装所有其 read() 操作可能开销很高的 Reader（如 FileReader
 和 InputStreamReader)**
 
- BufferedWriter
同理建议用BfferedWriter包装所有其write()操作可能开销很高的Writer(如FileWriter和OutputStreamWriter)

 
小结：
 (1）缓冲输入流BufferedInputStream除了支持read和skip方法意外，还支持其父类的mark和reset方法;
（2）BufferedReader提供了一种新的ReadLine方法用于读取一行字符串（以\r或\n分隔）;
（3）BufferedWriter提供了一种新的newLine方法用于写入一个行分隔符;
（4）对于输出的缓冲流，BufferedWriter和BufferedOutputStream，写出的数据会先在缓冲区(由缓冲流提供的一个
     字节数组，是不可见的)中缓存，直到缓冲区满了会自动写数据到输出流，如果缓冲区未满，可以使用flush方法将
     会使缓冲区的数据强制写出。关闭输出流也会造成缓冲区中残留的数据被写出。注意BufferedReader和
     BufferedInputStream没有flush方法，因为flush只用于输出到文件时



# 四、对象操作：Serializable

## 序列化与反序列化
Java序列化是指把Java对象转换为字节序列的过程，而Java反序列化是指把字节序列恢复为Java对象的过程。

序列化：
 对象序列化的最主要的用处就是在传递和保存对象的时候，保证对象的完整性和可传递性。
 序列化是**把对象转换成有序字节流，以便在网络上传输或者保存在本地文件中**。
 序列化后的字节流保存了Java对象的状态以及相关的描述信息。
 序列化机制的核心作用就是对象状态的保存与重建
 
反序列化：
客户端从文件中或网络上获得序列化后的对象字节流后，根据字节流中所保存的对象状态及描述信息，通过反序列化重建对象

## 实现序列化的前提
只有实现了 Serializable 或 Externalizable 接口的类的对象才能被序列化，否则抛出异常！

注意：
  Serializable接口和Cloneable接口一样是一个标记接口，即没有任何方法的接口。
  但只有一个类实现了Serializable接口，它才能被序列化为二进制流进行传输，否则会抛出NotSerializableException异常。
  一个类如果实现了Serializable接口，其子类自动实现序列化，不需要显式实现 Serializable 接口。



# 五、网络操作：Socket
# 六、新的输入/输出：NIO

# Stream

#




#