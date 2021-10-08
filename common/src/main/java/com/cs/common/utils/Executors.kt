package com.cs.common.utils

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author ChenSen
 * @since 2021/9/27 15:45
 * @desc
 *
 *
 *  Queue任务队列类型：
 *
 *  1. 提交队列：SynchronousQueue
 *     使用SynchronousQueue队列，提交的任务不会被保存，总是会马上提交执行。
 *     如果用于执行任务的线程数量小于等于maximumPoolSize，则尝试创建新的进程，
 *     如果达到maximumPoolSize设置的最大值，则根据你设置的handler执行拒绝策略。
 *     因此这种方式你提交的任务不会被缓存起来，而是会被马上执行，
 *
 *  2. 有界的任务队列： ArrayBlockingQueue
 *     使用ArrayBlockingQueue有界任务队列，若有新的任务需要执行时，线程池会创建新的线程，
 *     直到创建的线程数量达到corePoolSize时，则会将新的任务加入到等待队列中。
 *     若等待队列已满，即超过ArrayBlockingQueue初始化的容量，则继续创建线程，直到线程数量达到maximumPoolSize
 *     设置的最大线程数量，若大于maximumPoolSize则执行拒绝策略。
 *     在这种情况下，线程数量的上限与有界任务队列的状态有直接关系。如果有界队列初始容量较大或者没有达到超负荷的状态，
 *     线程数将一直维持在corePoolSize以下，反之当任务队列已满时，则会以maximumPoolSize为最大线程数上限
 *
 *  3. 无界的任务队列： LinkedBlockingQueue
 *     使用无界任务队列，线程池的任务队列可以无限制的添加新的任务，而线程池创建的最大数量就是你corePoolSize
 *     设置的数量，也就是说在这种情况下maximumPoolSize这个参数是无效的，哪怕你的任务队列中缓存了很多未执行的任务，
 *     当线程池的线程数达到corePoolSize后，就不会再增加了；若后续有新的任务加入，则直接进入队列等待
 *
 *  4. 优先任务队列 ： PriorityBlockingQueue
 *    PriorityBlockingQueue它其实是一个特殊的无界队列，它其中无论添加了多少个任务，
 *    线程池创建的线程数也不会超过corePoolSize的数量，只不过其他队列一般是按照先进先出的规则处理任务，
 *    而PriorityBlockingQueue队列可以自定义规则根据任务的优先级顺序先后执行。
 *
 *
 *
 *    拒绝策略
 *  1. AbortPolicy策略：该策略会直接抛出异常，阻止系统正常工作；
 *  2. CallerRunsPolicy策略：如果线程池的线程数量达到上限，该策略会把任务队列中的任务放在调用者线程当中执行
 *  3. DiscardOldestPolicy策略：该策略会丢弃任务队列中最老的一个任务，也就是当前任务队列中最先被添加进去的，
 *    马上要被执行的那个任务，并尝试再次提交；
 *  4. DiscardPolicy策略：该策略会默默丢弃无法处理的任务，不予任何处理。当然使用此策略，业务场景中需允许任务的丢失
 *
 */
object Executors {

    private var executor = ThreadPoolExecutor(
        5,
        10,
        10,
        TimeUnit.SECONDS,
        ArrayBlockingQueue(2),
        Executors.defaultThreadFactory(),
        ThreadPoolExecutor.AbortPolicy()
    )


    fun execute(runnable: Runnable) {
        executor.execute(runnable)
    }

}