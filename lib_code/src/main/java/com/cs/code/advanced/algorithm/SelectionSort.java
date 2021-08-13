package com.cs.code.advanced.algorithm;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/12 16:04
 * @desc 选择排序
 * <p>
 * 时间复杂度：
 * 最好 -> O(n2)
 * 最坏 -> O(n2)
 * 平均 -> O(n2)
 * <p>
 * 空间复杂度 -> O(1)
 * 原地排序算法
 * <p>
 * 不是稳定性算法
 */
public class SelectionSort {


    private static final int[] data = {2, 5, 4, 1, 6, 3};

    public static void main(String[] args) {
        selectionSort(data);
    }

    /**
     * 选择分已排序区间和未排序区间
     * 每次从未排序区间中找到最小的元素，将其放到已排序区间的末尾
     *
     * @param data
     * @return
     */
    public static int[] selectionSort(int[] data) {
        System.out.println("排序前：" + Arrays.toString(data));

        if (data == null || data.length < 2) {
            return data;
        }


        for (int i = 0; i < data.length - 1; i++) {

            int minIndex = i;

            // 在未排序数组中找到最小值
            // 初始状态下，把第一个数据当成已排序数组，所以这里 j = i +1
            for (int j = i + 1; j < data.length; j++) {
                if (data[minIndex] > data[j]) {
                    minIndex = j;
                }
            }

            //交换数据
            int temp = data[i];
            data[i] = data[minIndex];
            data[minIndex] = temp;

            System.out.println("第 " + (i + 1) + " 轮排序后：" + Arrays.toString(data));
        }
        System.out.println("排序后：" + Arrays.toString(data));
        return data;
    }
}
