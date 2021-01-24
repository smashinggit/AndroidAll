package com.cs.test.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ThreadTest {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    //线程安全的容器类
    ConcurrentHashMap map = new ConcurrentHashMap<String, String>();
    CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) {
//        callable();
//        interrupt(); //线程中断
//        join();
//        synchronize();
//        volatileTest();
//        conditionTest();
//        threadLocalTest();
//        threadPollTest();
//        futureTask();
//        atomic();
//        countdownLatch();
//        cyclicBarrier();
//        semaphore();
//        exchanger();

//        productorAndConsumer1();
        productorAndConsumer2();
//        productorAndConsumer3();


        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    }


    /**
     * 使用BlockingQueue实现生产者-消费者
     */
    static LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();

    private static void productorAndConsumer3() {
        ExecutorService executorService = Executors.newFixedThreadPool(15);
        for (int i = 0; i < 5; i++) {
            executorService.submit(new Productor3());
        }

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Consumer3());
        }
    }

    static class Productor3 implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    Random random = new Random();
                    int i = random.nextInt();
                    System.out.println("生产者" + Thread.currentThread().getName() + "生产数据" + i);

                    linkedBlockingQueue.put(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer3 implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    Integer integer = linkedBlockingQueue.take();
                    System.out.println("消费者" + Thread.currentThread().getName() + "正在消费数据" + integer);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //*********************************************************************************************

    /**
     * 采用lock中Conditon的消息通知原理来实现生产者-消费者问题
     */

    static int maxLength = 10;
    static List<Integer> container = new ArrayList<>();

    static Lock lock = new ReentrantLock();
    static Condition full = lock.newCondition();
    static Condition empty = lock.newCondition();

    private static void productorAndConsumer2() {
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        for (int i = 0; i < 5; i++) {
            executorService.submit(new Productor2());
        }

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Consumer2());
        }

    }

    static class Productor2 implements Runnable {

        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    while (container.size() == maxLength) {
                        System.out.println("生产者" + Thread.currentThread().getName() + "  list以达到最大容量，进行wait");
                        full.await();
                        System.out.println("生产者" + Thread.currentThread().getName() + "  退出wait");
                    }

                    Random random = new Random();
                    int i = random.nextInt();
                    System.out.println("生产者" + Thread.currentThread().getName() + " 生产数据" + i);
                    container.add(i);
                    empty.signalAll();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    static class Consumer2 implements Runnable {

        @Override
        public void run() {
            while (true) {

                lock.lock();
                try {
                    while (container.isEmpty()) {
                        System.out.println("消费者" + Thread.currentThread().getName() + "  list为空，进行wait");
                        empty.await();
                        System.out.println("消费者" + Thread.currentThread().getName() + "  退出wait");
                    }

                    Integer element = container.remove(0);
                    System.out.println("消费者" + Thread.currentThread().getName() + "  消费数据：" + element);
                    full.signalAll();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }


    //*********************************************************************************************

    /**
     * 使用 wait/notifyAll 完成生产者消费者
     */
    private static void productorAndConsumer1() {
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        for (int i = 0; i < 5; i++) {
            executorService.submit(new Productor1());
        }

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Consumer1());
        }

    }

    static class Productor1 implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (container) {
                    try {
                        while (container.size() == maxLength) {
                            System.out.println("生产者" + Thread.currentThread().getName() + "  list以达到最大容量，进行wait");
                            container.wait();
                            System.out.println("生产者" + Thread.currentThread().getName() + "  退出wait");
                        }

                        Random random = new Random();
                        int i = random.nextInt();
                        System.out.println("生产者" + Thread.currentThread().getName() + " 生产数据" + i);
                        container.add(i);
                        container.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    static class Consumer1 implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (container) {
                    try {
                        while (container.isEmpty()) {
                            System.out.println("消费者" + Thread.currentThread().getName() + "  list为空，进行wait");
                            container.wait();
                            System.out.println("消费者" + Thread.currentThread().getName() + "  退出wait");
                        }

                        Integer element = container.remove(0);
                        System.out.println("消费者" + Thread.currentThread().getName() + "  消费数据：" + element);
                        container.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 线程之间在某个同步点交换数据
     * <p>
     * 下课期间，男生经会给走廊里为自己喜欢的女孩子送情书。
     * 男孩会先到女孩教室门口，然后等女孩出来，教室那里就是一个同步点，
     * 然后彼此交换信物，也就是彼此交换了数据
     */
    private static void exchanger() {
        final Exchanger<String> exchanger = new Exchanger<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        //男孩
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //传入 男生对女生说的话，返回 女生对男生说的话
                    String girl = exchanger.exchange("我其实暗恋你很久了......");
                    System.out.println("女孩儿说：" + girl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //女孩
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("女生慢慢的从教室走出来......");
                    TimeUnit.SECONDS.sleep(3);

                    String boy = exchanger.exchange("我也很喜欢你.........");
                    System.out.println("男孩儿说：" + boy);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.shutdown();
    }

    /**
     * 对临界资源进行并发量控制
     * <p>
     * 我们来模拟这样一样场景。有一天，班主任需要班上20个同学到讲台上来填写一个表格，但是老师只准备了5支笔
     * 因此，只能保证同时只有5个同学能够拿到笔并填写表格，没有获取到笔的同学只能够等前面的同学用完之后，
     * 才能拿到笔去填写表格
     */
    private static void semaphore() {
        final Semaphore semaphore = new Semaphore(5, false);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "  同学准备获取笔......");
                        semaphore.acquire();  // 获取许可证，如果没有获取到会阻塞当前线程

                        System.out.println(Thread.currentThread().getName() + "  同学获取到笔");
                        System.out.println(Thread.currentThread().getName() + "  填写表格ing.....");
                        TimeUnit.SECONDS.sleep(3);

                        semaphore.release();  //释放许可证
                        System.out.println(Thread.currentThread().getName() + "  填写完表格，归还了笔！！！！！！");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();
    }

    private static void cyclicBarrier() {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(6, new Runnable() {
            @Override
            public void run() {
                System.out.println("所有运动员入场完毕，裁判员一声令下！！！！！");
            }
        });

        ExecutorService service = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 6; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + " 运动员，进场");

                        // 当6个运动员（线程）都到达了指定的临界点（barrier）时候，才能继续往下执行，否则，则会阻塞等待在调用await()处
                        cyclicBarrier.await(); // 此线程开始等待，直到所有的parties调用了cyclicBarrier.await()方法
                        System.out.println(Thread.currentThread().getName() + "  运动员出发");
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        System.out.println("运动员准备进场，全场欢呼............");
    }


    private static void countdownLatch() {
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch endSignal = new CountDownLatch(6);

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 6; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + " 运动员等待裁判员响哨！！！");
                        startSignal.await(); //所有运动员开始等待，直到startSignal的维护值为0

                        System.out.println(Thread.currentThread().getName() + "正在全力冲刺");
                        endSignal.countDown();
                        System.out.println(Thread.currentThread().getName() + "  到达终点");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        try {
            Thread.sleep(500);

            System.out.println("裁判员发号施令啦！！！");
            startSignal.countDown(); // 这里的值为0，所有运动员线程开始执行

            endSignal.await();  //主线程开始等待，直到endSignal的值为0
            System.out.println("所有运动员到达终点，比赛结束！");
            executorService.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void atomic() {

        AtomicInteger atomicInteger = new AtomicInteger(0);
        atomicInteger.addAndGet(2); // +2 并返回
        atomicInteger.incrementAndGet(); //+1 并返回
        atomicInteger.getAndSet(3); // 将值改为3并返回旧值
        atomicInteger.getAndIncrement();  //旧值+1，并返回旧值

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        atomicBoolean.getAndSet(true); //设置新值，返回旧值
        atomicBoolean.compareAndSet(false, true); // 如果当前值==传入的expect,那么将当前值更新为update

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        atomicIntegerArray.addAndGet(0, 1);
        atomicIntegerArray.incrementAndGet(0);
        atomicIntegerArray.compareAndSet(1, 0, 2); //如果位置1的元素当前值是0，那么将该值更新为2

        AtomicReferenceArray<Object> atomicReferenceArray = new AtomicReferenceArray<>(10);
        atomicReferenceArray.compareAndSet(0, "", "1");


    }

    private static void futureTask() {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        FutureTask<Integer> futureTask1 = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println(Thread.currentThread().getName() + " is run");
                Thread.sleep(4000);
                System.out.println(Thread.currentThread().getName() + " is done");
                return 1;
            }
        });

        FutureTask<Integer> futureTask2 = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println(Thread.currentThread().getName() + " is run");
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " is done");
                return 2;
            }
        });

        try {
            executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return null;
                }
            });


//            futureTask1.run();   //这样写并不会开启新的线程，还是运行在main线程中
//            futureTask2.run();
            executorService.execute(futureTask1);
            executorService.execute(futureTask2);
            System.out.println("结果是 " + (futureTask1.get() + futureTask2.get()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void threadPollTest() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
                5,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        ExecutorService executorService = Executors.newFixedThreadPool(2, Executors.defaultThreadFactory());

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {

            }
        }, 5, TimeUnit.SECONDS);

//        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("scheduleAtFixedRate ");
//            }
//        }, 0, 1, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("scheduleWithFixedDelay ");
            }
        }, 0, 2, TimeUnit.SECONDS);

        System.out.println("当前设备cpu个数 " + Runtime.getRuntime().availableProcessors());
    }

    /**
     * ThreadLocal 会根据不同的线程保存不同的值。线程间互不影响
     */
    private static void threadLocalTest() {
        ValueThread valueThread1 = new ValueThread();
        ValueThread valueThread2 = new ValueThread();
        ValueThread valueThread3 = new ValueThread();

        valueThread1.start();
        valueThread2.start();
        valueThread3.start();
    }

    static class ValueThread extends Thread {
        @Override
        public void run() {
            try {
                threadLocal.set(Thread.currentThread().getName());
                Thread.sleep(2000);
                System.out.println("in Thread " + Thread.currentThread().getName() + " the value is " + threadLocal.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    static volatile boolean conditionFlag = false;

    private static void conditionTest() {
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();


        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();

                try {
                    while (!conditionFlag) {
                        System.out.println(Thread.currentThread().getName() + "当前条件不满足等待");

                        condition.await(); // 当前线程开始等待
                    }

                    System.out.println(Thread.currentThread().getName() + "接收到通知条件满足");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread signaler = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();

                try {
                    Thread.sleep(2000);
                    conditionFlag = true;
                    condition.signalAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        });

        waiter.start();
        signaler.start();
    }


    volatile static int count = 0; //注意：volatile不能保证原子性
    volatile static boolean flag = false;  //注意。volatile保证每次读取变量的值都从主内存中读取

    private static void volatileTest() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!flag) {
                    System.out.println(" flag " + flag);
                }
                System.out.println(" flag " + flag);
            }
        });

        thread.start();
        try {
            Thread.sleep(500);
            flag = true;  //注意，在main线程修改,thread线程能立即感知到flag的变化，这体现了volatile的可见性

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void synchronize() {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        count++; //注意：这里的count++并不是原子操作
                    }
                }
            });
            thread.start();
        }

        try {
            Thread.sleep(5000);
            System.out.println("result: " + count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建了10个线程，每个线程都会等待前一个线程结束才会继续运行。
     * 可以通俗的理解成接力，前一个线程将接力棒传给下一个线程，然后又传给下一个线程
     */
    public static void join() {

        Thread previousThread = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            JoinThread curThread = new JoinThread(previousThread);
            curThread.start();
            previousThread = curThread;
        }

    }


    static class JoinThread extends Thread {
        private Thread thread;

        public JoinThread(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                thread.join();  //调用thread.join()，说明当前线程会等待thread执行完毕后才继续执行
                System.out.println(thread.getName() + " terminated.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 线程中断
     */
    public static void interrupt() {
        Thread.yield();

        Thread sleepThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread busyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                }
            }
        });


        sleepThread.start();
        busyThread.start();


        sleepThread.interrupt();  //因为此线程调用了Thread.sleep(1000)，所以调用interrupt会清除标志位并抛出异常
        busyThread.interrupt();  // 此线程是正常运行状态，所以调用interrupt不会清除标志位

        //while (sleepThread.isInterrupted()) 表示在Main中会持续监测sleepThread，一旦sleepThread的
        //中断标志位清零，即sleepThread.isInterrupted()返回为false时才会继续Main线程才会继续
        //往下执行。因此，中断操作可以看做线程间一种简便的交互方式
        while (sleepThread.isInterrupted()) {
            System.out.println("sleepThread.isInterrupted true"); //这里 isInterrupted() 为 true
        }

        System.out.println(" sleepThread.interrupt() " + sleepThread.isInterrupted() + "   current state " + sleepThread.getState()); //false
        System.out.println(" busyThread.interrupt() " + busyThread.isInterrupted() + "   current state " + busyThread.getState());   //true
    }

    public static void callable() {
        System.out.println("主线程开始执行");

        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("子线程开始执行");
                Thread.sleep(5000);
                System.out.println("子线程执行完毕");
                return 0;
            }
        });

        try {
            System.out.println("主线程开始获取数据 ");

            Integer result = future.get(); //这个方法是阻塞方法
            System.out.println("主线程获取数据完毕 " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
