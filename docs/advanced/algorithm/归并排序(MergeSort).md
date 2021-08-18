[toc]





# 一、归并排序



归并排序的核心思想是，**如果要排序一个数组，我们先把数组从中间分成前后两部分，然后对前后两部分分别排序，再将排好序的两部分合并在一起**，这样整个数组就都有序了





归并排序使用的就是分治思想。分治，顾名思义，就是分而治之，将一个大问题分解成小的子问题来解决。小的子问题解决了，大问题也就解决了。







分治思想跟递归思想很像。分治算法一般都是用递归来实现的。
**分治是一种解决问题的处理思想，递归是一种编程技巧**，这两者并不冲突。



写递归代码的技巧就是，**分析得出递推公式，然后找到终止条件，最后将递推公式翻译成递归代码**。
所以，要想写出归并排序的代码，我们先写出归并排序的递推公式。

```
递推公式：
merge_sort(l…r) = merge(merge_sort(l…mid), merge_sort(mid+1…r))

终止条件：
l >= r 不用再继续分解
```



merge_sort(l…r) 表示，给下标从 l 到 r 之间的数组排序

我们将这个排序问题转化为了两个子问题，merge_sort(l…mid) 和 merge_sort(mid+1…r)，其中下标 mid 等于 l 和 r 的中间位置，也就是 (l+r)/2

当下标从 l 到 mid 和从 mid+1 到 r 这两个子数组都排好序之后，我们再将两个有序的子数组合并在一起，这样下标从 l 到 r 之间的数据就也排好序了





```
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
```



输出如下

```
排序前 ：[2, 5, 4, 1, 6, 3]
数组分割: left = 0 right = 5
数组分割: left = 0 right = 2
数组分割: left = 0 right = 1
数组分割: left = 0 right = 0
数组已无法分割: left = 0 right = 0
数组分割: left = 1 right = 1
数组已无法分割: left = 1 right = 1
数组排序和合并: left = 0 right = 1
数组分割: left = 2 right = 2
数组已无法分割: left = 2 right = 2
数组排序和合并: left = 0 right = 2
数组分割: left = 3 right = 5
数组分割: left = 3 right = 4
数组分割: left = 3 right = 3
数组已无法分割: left = 3 right = 3
数组分割: left = 4 right = 4
数组已无法分割: left = 4 right = 4
数组排序和合并: left = 3 right = 4
数组分割: left = 5 right = 5
数组已无法分割: left = 5 right = 5
数组排序和合并: left = 3 right = 5
数组排序和合并: left = 0 right = 5
排序后 ：[1, 2, 3, 4, 5, 6]
```





# 二、快速排序 VS 归并排序



快排和归并用的都是分治思想，递推公式和递归代码也非常相似，那它们的区别在哪里呢？

归并排序的处理过程是由下到上的，先处理子问题，然后再合并。
快排正好相反，它的处理过程是由上到下的，先分区，然后再处理子问题。

归并排序虽然是稳定的、时间复杂度为 O(nlogn) 的排序算法，但是它是非原地排序算法。归并之所以是非原地排序算法，主要原因是合并函数无法在原地执行
快速排序通过设计巧妙的原地分区函数，可以实现原地排序，解决了归并排序占用太多内存的问题



**快速排序分区是按选定的某个元素值来分的（与数组中元素的值有关）**，即大于某个元素值的分为一个区，小于某个值的分为一个区
**归并排序分区是按数组下标分区的（与数组长度有关）**，取中间下标，小于中间下标的划分为一个区，大于中间下标的划分为一个区



# 三、算法性能分析



## 时间复杂度



- 最好时间复杂度为  O(nlogn)

- 坏情况时间复杂度为 O(nlogn)

- 平均时间复杂度为 O(nlogn)


归并排序的执行效率与要排序的原始数组的有序程度无关，所以其时间复杂度是非常稳定的，不管是最好情况、最坏情况，还是平均情况，**时间复杂度都是 O(nlogn)**



## 内存消耗

归并排序并没有像快排那样，应用广泛，这是为什么呢？因为它有一个致命的“弱点”，那就是**归并排序不是原地排序算法**

归并排序的合并函数，在合并两个有序数组为一个有序数组时，需要借助额外的存储空间。

尽管每次合并操作都需要申请额外的内存空间，但在合并完成之后，临时开辟的内存空间就被释放掉了。在任意时刻，CPU 只会有一个函数在执行，也就只会有一个临时的内存空间在使用。临时内存空间最大也不会超过 n 个数据的大小，所以**空间复杂度是 O(n)**



## 稳定性

归并排序稳不稳定关键要看 merge() 函数，也就是两个有序子数组合并成一个有序数组的那部分代码

在合并的过程中，如果 A[p...q]和 A[q+1...r]之间有值相同的元素，那我们可以像代码中那样，先把 A[p...q]中的元素放入 tmp 数组。这样就保证了值相同的元素，在合并前后的先后顺序不变。所以，**归并排序是一个稳定的排序算法**
