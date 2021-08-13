package com.cs.code.advanced.algorithm;

import java.util.Arrays;

/**
 * @author ChenSen
 * @date 2021/8/12 17:57
 * @desc 归并排序
 */
public class MergeSort {

    private static final int[] data = {2, 5, 4, 1, 6, 3};

    public static void main(String[] args) {
        System.out.println("排序前 ：" + Arrays.toString(data));
        mergeSort(data, 0, data.length - 1);
        System.out.println("排序后 ：" + Arrays.toString(data));
    }


    /**
     * 归并排序
     * 把数组 data 根据下标 left 和 right 分解成两部分，
     * 然后对前后两部分分别排序，再将排好序的两部分合并在一起
     */
    public static void mergeSort(int[] data, int left, int right) {
        System.out.println("数组分割: left = " + left + " right = " + right);
        if (data == null || data.length < 2) {
            return;
        }

        //递归终止条件, 即已经分割得剩1个元素了，无法继续分割
        if (left >= right) {
            System.out.println("数组已无法分割: left = " + left + " right = " + right);
            return;
        }

        int mid = (left + right) / 2;

        // 分治递归, 分别再对数组 data[left .. mid] 和 data[mid +1,right] 进行分割
        mergeSort(data, left, mid);
        mergeSort(data, mid + 1, right);

        //合并数组
        merge(data, left, mid, right);
    }


    /**
     * 将左右两个数组的值按从小到大的顺序排列并赋值到 data[left..right]
     * 左边的数组为 data[left..mid]
     * 有边的数组为 data[mid+1..right]
     */
    private static void merge(int[] data, int left, int mid, int right) {
        System.out.println("数组排序和合并: left = " + left + " right = " + right);
        int[] temp = new int[right - left + 1];  //申请一个大小跟data[left...right]一样的临时数组

        int i = left;        // i代表左边数组的索引
        int j = mid + 1;     // j代表右边数组的索引
        int k = 0;           // k代表临时数组的索引


        for (; k < temp.length; k++) {
            if (i > mid) {                      //说明左边数组已经全部拷贝
                temp[k] = data[j];
                j++;
            } else if (j > right) {            //说明右边数组已经全部拷贝
                temp[k] = data[i];
                i++;
            } else if (data[i] <= data[j]) {   //两个数组都有数据时，比较大小
                temp[k] = data[i];
                i++;
            } else {                           //两个数组都有数据时，比较大小
                temp[k] = data[j];
                j++;
            }
        }

        for (int m = 0; m < temp.length; m++) {
            data[left + m] = temp[m];
        }
    }
}
