package com.cs.code.advanced.algorithm;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/12 14:56
 * @desc 插入排序
 * 时间复杂度：
 * 最好 -> O(n)
 * 最坏 -> O(n2)
 * 平均 -> O(n2)
 * <p>
 * 空间复杂度 -> O(1)
 * 原地排序算法
 * <p>
 * 稳定性算法
 */
public class InsertionSort {

    private static final int[] data = {2, 5, 4, 1, 6, 3};

    public static void main(String[] args) {
        insertionSort(data);
    }


    /**
     * 插入排序将数组分为2个区间，已排序区间和未排序区间
     * 每次取未排序区间中的元素，在已排序区间中找到合适的插入位置将其插入，并保证已排序区间数据一直有序
     * 重复这个过程，直到未排序区间中元素为空
     *
     * @param data
     * @return
     */
    public static int[] insertionSort(int[] data) {
        System.out.println("排序前：" + Arrays.toString(data));

        if (data == null || data.length < 2) {
            return data;
        }

        // 初始化状态下已排序区间就是第一个元素，所以这里从 i=1 开始
        // 外层循环对未排序区间数据依次比较
        // i 代表未排序区间的指针
        for (int i = 1; i < data.length; i++) {

            int value = data[i];        // data[i] 即未排序区间的第一个数据
            int j = i - 1;              // j 代表有插入的位置,初始值为已排序区间的最后一个位置

            for (; j >= 0; j--) {
                if (data[j] > value) {
                    data[j + 1] = data[j];  // 把比value大的数据后移一位
                } else {
                    break;
                }
            }

            // 经过内层循环的比较和赋值，j + 1 的值就是要插入的位置
            // 因为内层循环结束时会执行j--，所以真实的位置要+1
            data[j + 1] = value;     //插入数据

            System.out.println("第 " + i + " 轮排序后：" + Arrays.toString(data));
        }

        System.out.println("排序后：" + Arrays.toString(data));
        return data;
    }
}
