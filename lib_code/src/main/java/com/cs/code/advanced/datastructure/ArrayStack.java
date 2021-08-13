package com.cs.code.advanced.datastructure;


import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/6 16:57
 * @desc 使用数组实现的栈
 */
public class ArrayStack<E> {

    private int capacity; //栈的大小
    private Object[] items;
    private int count;  //栈中元素个数


    public ArrayStack(int capacity) {
        this.capacity = capacity;
        items = new Object[capacity];
        count = 0;
    }


    /**
     * 出栈
     *
     * @return
     */
    public E pop() {

        if (count == 0) {
            return null;
        }

        Object element = items[count - 1];
        items[count - 1] = null;
        count--;
        return (E) element;
    }

    /**
     * 入栈
     *
     * @param element
     * @param <E>
     */
    public <E> Boolean push(E element) {

        // 数组空间不够了，直接返回false，入栈失败
        if (count == capacity) {
            return false;
        }

        items[count] = element;
        count++;
        return true;
    }


    @Override
    public String toString() {
        return "ArrayStack{" +
                "capacity=" + capacity +
                ", items=" + Arrays.toString(items) +
                ", count=" + count +
                '}';
    }

    public static void main(String[] args) {
        ArrayStack stack = new ArrayStack<Integer>(5);
        for (int i = 0; i < 6; i++) {
            stack.push(i);
            System.out.println("入栈：" + stack.toString());
        }

        for (int i = 0; i < 6; i++) {
            stack.pop();
            System.out.println("出栈：" + stack.toString());
        }

    }

}
