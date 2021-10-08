[toc]
# Java 相关面试题


# 注解是什么？有哪些元注解
注解，在我看来它是一种信息描述，不影响代码执行，但是可以用来配置一些代码或者功能。

常见的注解比如@Override,代表重写方法，看看它是怎么生成的：
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {
}
```
可以看到Override被@interface所修饰，代表注解，
同时上方还有两个注解@Target和@Retention，这种修饰注解的注解叫做元注解，很好理解吧，就是最基本的注解呗。

java中一共有四个元注解：
1. @Target：表示注解对象的作用范围。
2. @Retention：表示注解保留的生命周期
3. @Inherited：表示注解类型能被类自动继承。
4. @Documented：表示含有该注解类型的元素(带有注释的)会通过javadoc或类似工具进行文档化。

注解可以用来做什么
1. 降低项目的耦合度。
2. 自动完成一些规律性的代码。
3. 自动生成java代码，减轻开发者的工作量。


# 集合相关

## ArrayList 和 Vector 的区别

相同点：
（1）两者都是基于索引的，内部由⼀个数组⽀持。 
（2）两者维护插⼊的顺序，我们可以根据插⼊顺序来获取元素。 
（3）ArrayList和Vector的迭代器实现都是fail-fast的。 
（4）ArrayList和Vector两者允许null值，也可以使⽤索引值对元素进⾏随机访问。

不同点：
（1）Vector是同步的，⽽ArrayList不是。然⽽，如果你寻求在迭代的时候对列表进⾏改变，你应
 该使⽤CopyOnWriteArrayList。 
（2）ArrayList⽐Vector快，它因为有同步，不会过载。 
（3）ArrayList更加通⽤，因为我们可以使⽤Collections⼯具类轻易地获取同步列表和只读列表。

Vector的⽅法都是同步的(Synchronized),是线程安全的(thread-safe)，⽽ArrayList，LinkedList的
⽅法不是，由于线程的同步必然要影响性能，因此,ArrayList的性能⽐Vector好。

## Array和 ArrayList有何区别？什么时候更适合⽤Array
- Array可以容纳基本类型和对象，⽽ArrayList只能容纳对象。 
- Array⼤⼩是固定的，⽽ArrayList⼤⼩是可以改变的。 
- Array没有提供ArrayList那么多功能，⽐如addAll、removeAll和iterator等。尽
  管ArrayList明显是更好的选择，但也有些时候Array⽐较好⽤。

## ArrayList和LinkedList有何区别
- ArrayList是由Array所⽀持的基于⼀个索引的数据结构，所以它提供对元素的随机访问，复
  杂度为O(1)，但LinkedList存储⼀系列的节点数据，每个节点都与前⼀个和下⼀个节点相连接。所
  以，尽管有使⽤索引获取元素的⽅法，内部实现是从起始点开始遍历，遍历到索引的节点然后返回
  元素，时间复杂度为O(n)，⽐ArrayList要慢

- 与ArrayList相⽐，在LinkedList中插⼊、添加
  和删除⼀个元素会更快，因为在⼀个元素被插⼊到中间的时候，不会涉及改变数组的⼤⼩，或更新
  索引
  
- LinkedList⽐ArrayList消耗更多的内存，因为LinkedList中的每个节点存储了前后节
  点的引⽤。  
  
  
## ArrayList的扩容机制  

ArrayList是List接⼝的实现类，它是⽀持根据需要⽽动态增⻓的数组。java中标准数组是定⻓的，
在数组被创建之后，它们不能被加⻓或缩短。这就意味着在创建数组时需要知道数组的所需⻓度，
但有时我们需要动态程序中获取数组⻓度。ArrayList就是为此⽽⽣的,但是它不是线程安全的，此外
ArrayList按照插⼊的顺序来存放数据 ①ArrayList扩容发⽣在add()⽅法调⽤的时候， 调⽤
ensureCapacityInternal()来扩容的， 通过⽅法calculateCapacity(elementData, minCapacity)获
取需要扩容的⻓度: ②ensureExplicitCapacity⽅法可以判断是否需要扩容： ③ArrayList扩容的关
键⽅法grow(): 获取到ArrayList中elementData数组的内存空间⻓度 扩容⾄原来的1.5倍 ④调⽤
Arrays.copyOf⽅法将elementData数组指向新的内存空间时newCapacity的连续空间 从此⽅法中
我们可以清晰的看出其实ArrayList扩容的本质就是计算出新的扩容数组的size后实例化，并将原有
数组内容复制到新数组中去

每个ArrayList实例都有一个容量，该容量是指来存储列表元素的数组的大小，该容量至少等于列表数组的大小，
随着ArrayList的不断添加元素，其容量也在自动增长，自动增长会将原来数组的元素向新的数组进行copy。
因此，如果根据业务场景来提前预判数据量的大小。可在构造ArrayList时指定其容量。
在添加大量元素前，应用程序可以使用ensureCapacity操作来增加ArrayList实例的容量，这样就可以减少递增式再分配数量的操作。

- 不指定ArrayList的初始容量，在第一次add的时候会把容量初始化为10个，这个数值是确定的
- ArrayList每次扩容是原来得1.5倍
- ArrayList的扩容时机为add的时候容量不足，扩容的后的大小为原来的1.5倍，扩容需要拷贝以前数组的所有元素到新数组。
- 代价是很高得，因此再实际使用时，我们因该避免数组容量得扩张。尽可能避免数据容量得扩张。尽可能，就至指定容量，避免数组扩容的发生


## BlockingQueue

Java.util.concurrent.BlockingQueue是⼀个队列，在进⾏检索或移除⼀个
元素的时候，它会等待队列变为⾮空；当在添加⼀个元素时，它会等待队列中的可⽤空间。
BlockingQueue接⼝是Java集合框架的⼀部分，主要⽤于实现⽣产者-消费者模式。我们不需要担⼼等待
⽣产者有可⽤的空间，或消费者有可⽤的对象，因为它都在BlockingQueue的实现类中被处理了。Java
提供了集中BlockingQueue的实现，⽐如ArrayBlockingQueue、LinkedBlockingQueue、
PriorityBlockingQueue,、SynchronousQueue等。



## Set是如何确保它的唯⼀性的

set保存的就⼀个value，如果每次进⾏add时都将新值与原来所有值进⾏⽐较，将是⼀个⼤⼤的性能浪
费，举例，set中有1000个值了，如果新增⼀个，那这个值是否要与前1000个进⾏equals⽐较呢，相同
进⾏过滤，没有相同则进⾏加⼊，这太慢了。 set⽤到了哈希⽅法，先进⾏取hashcode，在将得到的值
插⼊到指定算出来的地址上，如果下次有相同值对应这个地址，则进⾏equals⽐较，相同则过滤，不同
则通过解决冲突算法，将该值存⼊起来


## HashMap的原理

HashMap基于hashing原理，我们通过put()和get()⽅法储存和获取对象。当将键对象和值对象传递给
put()⽅法时，它调⽤键对象的hash⽅法来得到hash值然后根据hash&（length-1）运算得到bucket数组
下标值，然后根据下标找到bucket位置来储存值Entry对象。如果两个不同的键对象通过hash算法得到
相同bucket 数组下标即相同的bucket位置，此时会发⽣‘碰撞’值对象储存在同⼀个bucket位置的链表下
⼀个节点上（如果链表的⻓度⼤于8则会引⼊红⿊树来存储值Entry对象）。之后判断HashMap存储的数
量是否⼤于12（16*0.75）如果⼤于12则rehashing(扩容)到之前的两倍⼤⼩bucket数组，并且᯿新调整
map的⼤⼩，并将原来的对象放⼊新的bucket数组中。当获取对象时，get()⽅法通过键对象的hash⽅法
来得到hash值然后根据hash&（length-1）运算得到bucket数组下标值，然后根据下标找到bucket位置
后，会调⽤keys.equals()⽅法去找到链表中正确的节点，最终找到要找的值对象。
Java8新加，当链表⻓度⼤于8的时候使⽤红⿊树存储Entry对象（8是通过泊松算法经过⼀系列的计算得
到当链表⻓度为8时候的概率（0.00000006）趋近于0）。

- HashMap 初始化大小是 16 ，扩容因子默认0.75（可以指定初始化大小，和扩容因子）


## LinkedHashMap的原理

LinkedHashMap实现了Map接⼝，继承于HashMap,与HashMap不同的是**它维持有⼀个双链表，从⽽可以保证迭代时候的顺序**

1、能够保证插⼊元素的顺序。
深⼊⼀点讲，有两种迭代元素的⽅式，⼀种是按照插⼊元素时的顺序迭代，⽐如，插⼊A,B,C，那么迭代也是A,B,C，
另⼀种是按照访问顺序，⽐如，在迭代前，访问了B，那么迭代的顺序就是A,C,B，⽐如在迭代前，访问了B，接着⼜访问了A，那么迭代顺序为C,B,A，
⽐如，在迭代前访问了B，接着⼜访问了B，然后在访问了A，迭代顺序还是C,B,A。要说明的意思就是不是近期访问的次数最多，就放最后⾯迭代，⽽是看迭代前被访问的时间⻓短决定

2、内部存储的元素的模型。
entry是下⾯这样的，相⽐HashMap，多了两个属性，⼀个before，⼀个
after。next和after有时候会指向同⼀个entry，有时候next指向null，⽽after指向entry。这个具体后⾯分析。

3、linkedHashMap和HashMap在存储操作上是⼀样的，但是LinkedHashMap多的东⻄是会记住在此之前插⼊的元素，
这些元素不⼀定是在⼀个桶中，也就是说，对于linkedHashMap的基本操作还是和HashMap⼀样，在其上⾯加了两个属性，也就是为了
记录前⼀个插⼊的元素和记录后⼀个插⼊的元素。也就是只要和hashmap⼀样进⾏操作之后把这两个属
性的值设置好，就OK了。注意⼀点，会有⼀个header的实体，⽬的是为了记录第⼀个插⼊的元素是谁，在遍历的时候能够找到第⼀个元素。
实际上存储的样⼦就像上⾯这个图⼀样，这⾥要分清楚哦。实际上的存储⽅式是和hashMap⼀样，但是
同时增加了⼀个新的东⻄就是 **双向循环链表**。就是因为有了这个双向循环链表，LinkedHashMap才和
HashMap不⼀样。



## HashMap 和 HashTable 的区别


## 集合框架底层数据结构

### List
- ArrayList： Object数组
- Vector： Object数组
- LinkedList： 双向循环链表

### Set 
- HashSet（⽆序，唯⼀）：基于 HashMap 实现的，底层采⽤ HashMap 来保存元素
- LinkedHashSet： LinkedHashSet 继承与 HashSet，并且其内部是通过 LinkedHashMap 来实现的。
  有点类似于我们之前说的LinkedHashMap 其内部是基于 HashMap 实现⼀样，不过还是有⼀点点区别的。
- TreeSet（有序，唯⼀）： 红⿊树(⾃平衡的排序⼆叉树。)

### Map
- HashMap： JDK1.8之前HashMap由数组+链表组成的，数组是HashMap的主体，链表则是主要为
  了解决哈希冲突⽽存在的（“拉链法”解决冲突）.JDK1.8以后在解决哈希冲突时有了较⼤的变化，当
  链表⻓度⼤于阈值（默认为8）时，将链表转化为红⿊树，以减少搜索时间

- LinkedHashMap：LinkedHashMap 继承⾃ HashMap，所以它的底层仍然是基于拉链式散列结构
  即由数组和链表或红⿊树组成。另外，LinkedHashMap 在上⾯结构的基础上，增加了⼀条双向链
  表，使得上⾯的结构可以保持键值对的插⼊顺序。同时通过对链表进⾏相应的操作，实现了访问顺
  序相关逻辑

- HashTable： 数组+链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突⽽存在的

- TreeMap： 红⿊树（⾃平衡的排序⼆叉树）
  
  
## ConcurrentHashMap何实现并发访问的？
ConcurrentHashMap是Java5中新增加的⼀个线程安全的Map集合，可以⽤来替代HashTable。
HashTable容器在竞争激烈的并发环境下表现出效率低下的原因是所有访问HashTable的线程都必须竞争同⼀把锁，
那假如容器⾥有多把锁，每⼀把锁⽤于锁容器其中⼀部分数据，那么当多线程访问容器⾥不同数据段的数据时，线程间就不会存在锁竞争，
从⽽可以有效的提⾼并发访问效率，这就是ConcurrentHashMap所使⽤的**锁分段技术**，
**⾸先将数据分成⼀段⼀段的存储，然后给每⼀段数据配⼀把锁，当⼀个线程占⽤锁访问其中⼀个段数据的时候，
其他段的数据也能被其他线程访问**
  
  
  
## 与Java集合框架相关的有哪些最好的实践？
- 根据需要选择正确的集合类型。
  ⽐如，如果指定了⼤⼩，我们会选⽤Array⽽⾮ArrayList。
  如果我们想根据插⼊顺序遍历⼀个Map，我们需要使⽤TreeMap。
  如果我们不想重复，我们应该使⽤Set。 
  
- ⼀些集合类允许指定初始容量，所以如果我们能够估计到存储元素的数量，我们
可以使⽤它，就避免了᯿新哈希或⼤⼩调整。 

- 基于接⼝编程，⽽⾮基于实现编程，它允许我们后来轻易地改变实现。 
- 总是使⽤类型安全的泛型，避免在运⾏时出现ClassCastException。 
- 使⽤JDK提供的不可变类作为Map的key，可以避免⾃⼰实现hashCode()和equals()。 
- 尽可能使⽤Collections⼯具类，或者获取只读、同步或空的集合，⽽⾮编写⾃⼰的实现。它将会提供代码᯿⽤性，它有着更好的稳定性和可维护性。



# 死锁

## 概念

两个或两个以上的线程在执⾏过程中，因争夺资源⽽造成的互相等待的现象，在⽆外⼒作⽤的情况下，
这些线程会⼀直互相等待⽽⽆法继续运⾏下去.

## 4个必要条件

- 互斥： 某种资源一次只允许一个进程访问，即该资源一旦分配给某个进程，其他进程就不能再访问，直到该进程访问结束
- 占有且等待： 一个进程本身占有资源（一种或多种），同时还有资源未得到满足，正在等待其他进程释放该资源。
- 不可抢占： 别人已经占有了某项资源，你不能因为自己也需要该资源，就去把别人的资源抢过来。
- 循环等待： 存在一个进程链，使得每个进程都占有下一个进程所需的至少一种资源。


## 死锁示范
```
public class DieLock {

    //创建资源
    private static Object resourceA = new Object();
    private static Object resourceB = new Object();

    public static void main(String[] args) {


        Thread threadA = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println(Thread.currentThread() + " get ResourceA");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (resourceB) {
                    System.out.println(Thread.currentThread() + " get ResourceB");
                }
            }
        });


        Thread threadB = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println(Thread.currentThread() + " get ResourceB");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (resourceA) {
                    System.out.println(Thread.currentThread() + " get ResourceA");
                }
            }
        });

        threadA.start();
        threadB.start();
    }
}


```

### 避免死锁的⽅法

- 破坏“占有且等待”条件
方法1：所有的进程在开始运行之前，必须一次性地申请其在整个运行过程中所需要的全部资源。
方法2：该方法是对第一种方法的改进，允许进程只获得运行初期需要的资源，便开始运行，
在运行过程中逐步释放掉分配到的已经使用完毕的资源，然后再去请求新的资源。这样的话，资源的利用率会得到提高，也会减少进程的饥饿问题。


- 破坏“不可抢占”条件
当一个已经持有了一些资源的进程在提出新的资源请求没有得到满足时，它必须释放已经保持的所有资源，
待以后需要使用的时候再重新申请。这就意味着进程已占有的资源会被短暂地释放或者说是被抢占了

- 破坏“循环等待”条件
可以通过定义资源类型的线性顺序来预防，可将每个资源编号，当一个进程占有编号为i的资源时，
那么它下一次申请资源只能申请编号大于i的资源








