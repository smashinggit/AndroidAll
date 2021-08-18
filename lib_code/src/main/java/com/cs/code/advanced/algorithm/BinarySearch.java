package com.cs.code.advanced.algorithm;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/17 14:22
 * @desc 二分查找算法
 * <p>
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
public class BinarySearch {
    private static final int[] data = {5, 6, 15, 26, 28, 31, 44, 45, 50};

    public static void main(String[] args) {
        int key = 15;
        System.out.println("开始二分查找: 在数组 " + Arrays.toString(data) + " 中查找 " + key + " 的位置");
        int index = binarySearch(data, key);
//        int index = binarySearchInternally(data, key, 0, data.length - 1);
        System.out.println("查找结束：" + key + " 的位置是 " + index);
    }

    /**
     * 简单的二分查找算法 (入参数组必须是无重复且有序)
     * 在数组 [data] 中查找值为 [key] 的数据，并返回其下标，没有找到返回 -1
     *
     * @param data
     * @param key
     * @return
     */
    public static int binarySearch(int[] data, int key) {
        int low = 0;
        int height = data.length - 1;

        while (low <= height) {
            int mid = (low + height) / 2;
            System.out.println("对数据进行二分, low = " + low + ", mid = " + mid + ", height = " + height);

            if (key == data[mid]) {
                return mid;
            } else if (key < data[mid]) {
                height = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }


    /**
     * 使用递归的方式实现简单二分查找
     *
     * @param data
     * @param key
     * @return
     */
    public static int binarySearchInternally(int[] data, int key, int low, int height) {
        if (low > height) {
            return -1;
        }

        int mid = (low + height) / 2;
        if (key == data[mid]) {
            return mid;
        } else if (key < data[mid]) {
            return binarySearchInternally(data, key, low, mid - 1);
        } else {
            return binarySearchInternally(data, key, mid + 1, height);
        }
    }
}
