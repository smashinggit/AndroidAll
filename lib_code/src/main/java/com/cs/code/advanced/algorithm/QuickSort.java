package com.cs.code.advanced.algorithm;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/13 21:46
 * @desc 快速排序
 * <p>
 * 时间复杂度：
 * 最好 -> O(nlogn)
 * 最坏 -> O(n2)
 * 平均 -> O(nlogn)
 * <p>
 * 空间复杂度 -> O(1)
 * 原地排序算法
 * <p>
 * 不是稳定性算法
 */
public class QuickSort {
    private static final int[] data = {2, 5, 4, 1, 6, 3};

    public static void main(String[] args) {
        System.out.println("排序前 ：" + Arrays.toString(data));
        quickSort(data, 0, data.length - 1);
        System.out.println("排序后 ：" + Arrays.toString(data));
    }


    /**
     * 快速排序
     * 选择数组中的任意一个数字(一般为数组中最后一个)作为 pivot(分区点)，
     * 然后遍历数组，把小于 pivot 的放到左边，大于 pivot 的放到右边，pivot 放到中间
     * 根据分治、递归的思想，重复上面的步骤，直到区间缩为 1 ，则排序完成
     *
     * @param data
     * @param left  数组左下标
     * @param right 数组右下标
     */
    private static void quickSort(int[] data, int left, int right) {

        if (data == null || data.length < 2) {
            return;
        }

        if (left >= right) {
            return;
        }


        int pivotIndex = partition(data, left, right);

        quickSort(data, left, pivotIndex - 1);
        quickSort(data, pivotIndex + 1, right);
    }

    /**
     * 对数组进行分区
     * 此函数完成了以下功能：
     * 1. 从数组中选取一个数作为 pivot
     * 2. 以 pivot 为比较标准划分出小于这个数的左数组和大于这个数的右数组
     * 3. 统计出左数组的个数，得到划分点的下标(即指针 i)
     * 4. 将 pivot 放到数组中间(这个中间并不是数组长度的中间，而是左边数组和右边数组的中间)
     */
    private static int partition(int[] data, int left, int right) {
        int pivot = data[right];   // 最后一个数据作为分区点
        int i = left;              // i 代表左边数组的指针

        for (int j = left; j < right; j++) {   // j 代表遍历数组的指针

            if (data[j] < pivot) {             // 如果某个数据小于 pivot，则将其放到左边数组，并更新指针 i
                int temp = data[j];
                data[j] = data[i];
                data[i] = temp;
                i++;
            }
        }

        // 将 pivot 与 data[i] 交换，即将 pivot 放到了数组中间，左边全小于 pivot，右边全大于 pivot
        data[right] = data[i];
        data[i] = pivot;
        System.out.println("分区点的坐标为 " + i + ",  分区后数组为 " + Arrays.toString(data));
        return i;
    }
}
