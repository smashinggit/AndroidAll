package com.cs.code;

/**
 * @author ChenSen
 * @date 2021/9/6 17:51
 * @desc 死锁
 * <p>
 * 两个或两个以上的线程在执⾏过程中，因争夺资源⽽造成的互相等待的现象，在⽆外⼒作⽤的情况下，
 * 这些线程会⼀直互相等待⽽⽆法继续运⾏下去.
 * <p>
 * - 互斥： 某种资源一次只允许一个进程访问，即该资源一旦分配给某个进程，其他进程就不能再访问，直到该进程访问结束
 * - 占有且等待： 一个进程本身占有资源（一种或多种），同时还有资源未得到满足，正在等待其他进程释放该资源。
 * - 不可抢占： 别人已经占有了某项资源，你不能因为自己也需要该资源，就去把别人的资源抢过来。
 * - 循环等待： 存在一个进程链，使得每个进程都占有下一个进程所需的至少一种资源。
 */
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
