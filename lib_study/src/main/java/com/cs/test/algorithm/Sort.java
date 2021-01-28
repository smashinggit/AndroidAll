package com.cs.test.algorithm;

import java.util.Arrays;

public class Sort {

    private static int[] datas = new int[]{3, 48, 5, 2, 15, 19, 26, 7, 20, 1};
    private static int[] arr = new int[]{12, 23, 34, 45, 56, 67, 77, 89, 90};


    public static void main(String[] args) {
//        BubbleSort(datas);
//        SelectionSort(datas);
//        InsertSort(datas);
//        log("二分法查找 " + search(arr, 23));
    }

    /**
     * 二分法查找
     * <p>
     * 采用二分法查找时，数据需是有序不重复的
     */
    private static int search(int[] arr, int key) {
        int start = 0;
        int end = arr.length;

        while (start <= end) {

            int middle = (start + end) / 2;

            if (key < arr[middle]) {
                end = middle - 1;
            } else if (key > arr[middle]) {
                start = middle + 1;
            } else {
                return middle;
            }
        }
        return -1;
    }

    /**
     * 插入排序
     *
     * @param data
     */
    private static int[] InsertSort(int[] data) {
        log("排序前 " + Arrays.toString(data));

        for (int i = 1; i < data.length; i++) {
            int preIndex = i - 1;
            int currentData = data[i];

            while (preIndex >= 0 && data[preIndex] > currentData) {
                data[preIndex + 1] = data[preIndex];
                preIndex -= 1;
            }
            data[preIndex + 1] = currentData;

            log("排序中 " + Arrays.toString(data));
        }

        log("排序后 " + Arrays.toString(data));
        return data;
    }


    /**
     * 选择排序(从小到大排序)
     */
    private static int[] SelectionSort(int[] data) {
        log("排序前 " + Arrays.toString(data));

        for (int i = 0; i < data.length; i++) {

            int minIndex = i;

            for (int j = i + 1; j < data.length; j++) {
                if (data[minIndex] > data[j]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                int temp = data[minIndex];
                data[minIndex] = data[i];
                data[i] = temp;
            }
            log("排序中 " + Arrays.toString(data));

        }
        log("排序后 " + Arrays.toString(data));
        return data;
    }


    /**
     * 冒泡排序(从小到大排序)
     */
    private static int[] BubbleSort(int[] data) {
        log("排序前 " + Arrays.toString(data));

        for (int i = 0; i < data.length; i++) {

            //目的是将算法的最佳时间复杂度优化为 O(n)，即当原输入序列就是排序好的情况下，该算法的时间复杂度就是 O(n)
            boolean flag = true;

            for (int j = 0; j < data.length - 1; j++) {
                if (data[j] > data[j + 1]) {
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;

                    flag = false;
                }
            }
            log("排序中 " + Arrays.toString(data));

            if (flag) {
                break;
            }
        }

        log("排序后 " + Arrays.toString(data));
        return data;
    }


    public static void log(String msg) {
        System.out.println(msg);
    }

}