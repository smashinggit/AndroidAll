package com.cs.code.advanced.datastructure;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/10 17:07
 * @desc 基于数组实现的队列，也叫做顺序队列
 */
public class ArrayQueue<E> {

    private int capacity;     //队列的大小
    private Object[] items;
    private int head;         //head表示队头下标
    private int tail;         //tail表示队尾下标


    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        items = new Object[capacity];
    }

    /**
     * 入队
     *
     * @param item
     */
    public boolean enqueue(E item) {
        if (tail == capacity) {  //队列末尾已经没有空间了

            if (head == 0) {  // tail ==capacity && head==0，表示整个队列都占满了
                return false;
            }

            System.out.println("进行数据搬移 head = " + head + "; tail = " + tail);
            // 数据搬移
            for (int i = head; i < tail; i++) {
                items[i - head] = items[i];
                items[i] = null;  //注意：这个操作不是必要的，因为我们通过tail指针移动来代表数据的移除
            }
            // 搬移完之后重新更新head和tail
            tail = tail - head;
            head = 0;
        }

        items[tail] = item;
        tail++;
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
        head++;
        return (E) item;
    }

    @Override
    public String toString() {
        return "ArrayQueue{" +
                "capacity=" + capacity +
                ", items=" + Arrays.toString(items) +
                ", head=" + head +
                ", tail=" + tail +
                '}';
    }

    public static void main(String[] args) {

        ArrayQueue<String> queue = new ArrayQueue<>(5);
        for (int i = 0; i < 6; i++) {
            boolean result = queue.enqueue("item " + i);
            System.out.println("入队 " + result + ";  " + queue.toString());
        }

        for (int i = 0; i < 2; i++) {
            String result = queue.dequeue();
            System.out.println("出队 " + result + ";  " + queue.toString());
        }

        for (int i = 0; i < 5; i++) {
            boolean result = queue.enqueue("item " + i);
            System.out.println("入队 " + result + ";  " + queue.toString());
        }

    }

}
