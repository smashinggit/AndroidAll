package com.cs.code.advanced.datastructure;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/10 18:06
 * @desc 基于数组实现的循环队列
 * <p>
 * 循环队列可以避免数据迁移
 * <p>
 * 当队列满时，tail 指向的位置实际上是没有存储数据的，所以循环队列会浪费一个数组的存储空间
 */
public class CircularQueue<E> {

    private int capacity;     //队列的大小
    private Object[] items;
    private int head;         //head表示队头下标
    private int tail;         //tail表示队尾下标


    public CircularQueue(int capacity) {
        this.capacity = capacity;
        items = new Object[capacity];
    }

    /**
     * 入队
     *
     * @param item
     */
    public boolean enqueue(E item) {
        if ((tail + 1) % capacity == head) { //队列已满
            return false;
        }

        items[tail] = item;
        tail = (tail + 1) % capacity;
        return true;
    }

    /**
     * 出队
     *
     * @return
     */
    public E dequeue() {
        if (head == tail) {  //队列为空
            return null;
        }
        Object item = items[head];
        items[head] = null; //注意：这个操作不是必要的，因为我们通过head指针移动来代表数据的移除
        head = (head + 1) % capacity;
        return (E) item;
    }

    @Override
    public String toString() {
        return "CircularQueue{" +
                "capacity=" + capacity +
                ", items=" + Arrays.toString(items) +
                ", head=" + head +
                ", tail=" + tail +
                '}';
    }

    public static void main(String[] args) {

        CircularQueue<String> queue = new CircularQueue<>(5);
        for (int i = 0; i < 6; i++) {
            boolean result = queue.enqueue("item " + i);
            System.out.println("入队 " + result + ";  " + queue.toString());
        }

        for (int i = 0; i < 6; i++) {
            String result = queue.dequeue();
            System.out.println("出队 " + result + ";  " + queue.toString());
        }

        for (int i = 0; i < 5; i++) {
            boolean result = queue.enqueue("item " + i);
            System.out.println("入队 " + result + ";  " + queue.toString());
        }
    }

}
