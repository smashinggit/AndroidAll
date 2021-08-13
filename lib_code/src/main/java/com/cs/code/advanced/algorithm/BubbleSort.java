package com.cs.code.advanced.algorithm;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/11 15:55
 * @desc 冒泡排序
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
public class BubbleSort {
    private static final int[] data = {2, 5, 4, 1, 6, 3};

    public static void main(String[] args) {
        int[] sortedData = bubbleSort(data);
    }

    public static int[] bubbleSort(int[] data) {
        System.out.println("排序前：" + Arrays.toString(data));

        if (data == null || data.length < 2) {
            return data;
        }

        // 因为每次对两个数据进行比较，所以只需要  data.length - 1 次排序
        // 即当 data.length - 1 个数据已经排好位置了，剩下的一个数据自然就在自己改在的位置上
        for (int i = 0; i < data.length - 1; i++) {

            // 提前退出冒泡循环的标志位
            boolean flag = false;

            // 每进行一次循环，都会让至少一个元素移动到它应该在的位置
            // 因为每进行一次排序，都会让有序数据下沉到数组最后，所以这里的条件是 data.length - 1 - i,
            // 即 不对最后已经排过序的数据进行比较
            for (int j = 0; j < data.length - 1 - i; j++) {
                if (data[j] > data[j + 1]) {
                    int temp = data[j + 1];
                    data[j + 1] = data[j];
                    data[j] = temp;

                    flag = true;  //表示有数据交换
                }
            }
            System.out.println("第 " + (i + 1) + " 次排序：" + Arrays.toString(data));

            if (!flag) {  // 没有数据交换，提前退出
                System.out.println("排序完成，提前退出");
                break;
            }
        }

        System.out.println("排序后：" + Arrays.toString(data));
        return data;
    }


//    public static int[] bubbleSort(int[] data) {
//        System.out.println("排序前：" + Arrays.toString(data));
//
//        // 因为每次对两个数据进行比较，所以只需要  data.length - 1 次排序
//        // 即当 data.length - 1 个数据已经排好位置了，剩下的一个数据自然就在自己改在的位置上
//        for (int i = 0; i < data.length - 1; i++) {
//
//            // 每进行一次循环，都会让至少一个元素移动到它应该在的位置
//            // 因为每进行一次排序，都会让有序数据下沉到数组最后，所以这里的条件是 data.length - 1 - i,
//            // 即 不对最后已经排过序的数据进行比较
//            for (int j = 0; j < data.length - 1 - i; j++) {
//                if (data[j] > data[j + 1]) {
//                    int temp = data[j + 1];
//                    data[j + 1] = data[j];
//                    data[j] = temp;
//                }
//            }
//            System.out.println("第 " + (i + 1) + " 次排序：" + Arrays.toString(data));
//        }
//
//        System.out.println("排序后：" + Arrays.toString(data));
//        return data;
//    }
}