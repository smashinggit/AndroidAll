[toc]





# 一、快速排序



快排的思想：

如果要排序数组中下标从 p 到 r 之间的一组数据，我们选择 p 到 r 之间的任意一个数据作为 pivot（分区点）



我们遍历 p 到 r 之间的数据，将小于 pivot 的放到左边，将大于 pivot 的放到右边，将 pivot 放到中间。经过这一步骤之后，数组 p 到 r 之间的数据就被分成了三个部分，前面 p 到 q-1 之间都是小于 pivot 的，中间是 pivot，后面的 q+1 到 r 之间是大于 pivot 的

**快速排序分区是按选定的某个元素值来分的（与数组中元素的值有关）**，即大于某个元素值的分为一个区，小于某个值的分为一个区

**归并排序分区是按数组下标分区的（与数组长度有关）**，取中间下标，小于中间下标的划分为一个区，大于中间下标的划分为一个区

根据分治、递归的处理思想，我们可以用递归排序下标从 p 到 q-1 之间的数据和下标从 q+1 到 r 之间的数据，直到区间缩小为 1，就说明所有的数据都有序了

如果我们用递推公式来将上面的过程写出来的话，就是这样：

```
递推公式：
quick_sort(p…r) = quick_sort(p…q-1) + quick_sort(q+1… r)

终止条件：
p >= r
```



归并排序中有一个 merge() 合并函数，快速排序有一个 partition() 分区函数。
partition() 分区函数实际上就是随机选择一个元素作为 pivot（一般情况下，可以选择 p 到 r 区间的最后一个元素），
然后对 A[p...r]分区，函数返回 pivot 的下标





代码如下：

```
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
```

输出如下：

```
排序前 ：[2, 5, 4, 1, 6, 3]
分区点的坐标为 2,  分区后数组为 [2, 1, 3, 5, 6, 4]
分区点的坐标为 0,  分区后数组为 [1, 2, 3, 5, 6, 4]
分区点的坐标为 3,  分区后数组为 [1, 2, 3, 4, 6, 5]
分区点的坐标为 4,  分区后数组为 [1, 2, 3, 4, 5, 6]
排序后 ：[1, 2, 3, 4, 5, 6]
```



这个获得区分点的函数才是核心，其要解决最少三个问题：1.随机从数组中选取一个数，2.以选取的这个数为比较标准划分出小于这个数的左数组和大于这个数的右数组，3.统计出左数组的个数，得到划分点的下标，4.完成随机数下标的赋值操作。
 快速排序不用进行合并操作，因为中间值的下标就是最终排序的下标，当递归完成时，就已经完成了排序！



两个指针分别是 j 和 i，j 会一直向右顺序遍历，并且这个过程中会一直与 pivot 位置的数比较。只有当 j 位置的数小于 pivot 位置的数才会发生和 i 位置的交换（也就是swap A[ i ] with A [ j ]），同时 i 自增一次。直到遍历完成后， i 左边的数就都是小于pivot的，此时把 i 位置的数和pivot位置的数交换，那么pivot左边就都是比它小的数，而右边就都是比它大的数了,这样我们就成功完成了一个原地排序算法！



# 二、快速排序的优化



快速排序在最坏情况下的时间复杂度是 O(n2)，如何来解决这个“复杂度恶化”的问题呢？

如果数据原来就是有序的或者接近有序的，每次分区点都选择最后一个数据，那快速排序算法就会变得非常糟糕，时间复杂度就会退化为 O(n2)。实际上，**这种 O(n2) 时间复杂度出现的主要原因还是因为我们分区点选得不够合理**

最理想的分区点是：**被分区点分开的两个分区中，数据的数量差不多**

如果很粗暴地直接选择第一个或者最后一个数据作为分区点，不考虑数据的特点，肯定会出现之前讲的那样，在某些情况下，排序的最坏情况时间复杂度是 O(n2)。为了提高排序算法的性能，我们也要尽可能地让每次分区都比较平均

介绍两个比较常用、比较简单的分区算法：

1. **三数取中法**

   我们从区间的首、尾、中间，分别取出一个数，然后对比大小，取这 3 个数的中间值作为分区点。这样每间隔某个固定的长度，取数据出来比较，将中间值作为分区点的分区算法，肯定要比单纯取某一个数据更好。但是，如果要排序的数组比较大，那“三数取中”可能就不够了，可能要“五数取中”或者“十数取中”

2. **随机法**

   随机法就是每次从要排序的区间中，随机选择一个元素作为分区点。这种方法并不能保证每次分区点都选的比较好，但是从概率的角度来看，也不大可能会出现每次分区点都选得很差的情况，所以平均情况下，这样选的分区点是比较好的。时间复杂度退化为最糟糕的 O(n2) 的情况，出现的可能性不大



快速排序是用递归来实现的，递归要警惕堆栈溢出。
为了避免快速排序里，递归过深而堆栈过小，导致堆栈溢出，我们有两种解决办法：
第一种是限制递归深度。一旦递归过深，超过了我们事先设定的阈值，就停止递归。
第二种是通过在堆上模拟实现一个函数调用栈，手动模拟递归压栈、出栈的过程，这样就没有了系统栈大小的限制。



# 三、快速排序 VS 归并排序



快排和归并用的都是分治思想，递推公式和递归代码也非常相似，那它们的区别在哪里呢？

归并排序的处理过程是由下到上的，先处理子问题，然后再合并。
快排正好相反，它的处理过程是由上到下的，先分区，然后再处理子问题。

归并排序虽然是稳定的、时间复杂度为 O(nlogn) 的排序算法，但是它是非原地排序算法。归并之所以是非原地排序算法，主要原因是合并函数无法在原地执行
快速排序通过设计巧妙的原地分区函数，可以实现原地排序，解决了归并排序占用太多内存的问题





# 四、快速排序的性能分析



## 时间复杂度

- **最好情况时间复杂度是 O(nlogn)**

  如果每次分区操作，都能正好把数组分成大小接近相等的两个小区间，那快排的时间复杂度是 O(nlogn)

  

- **最坏情况时间复杂度为 O(n2)**

  如果数组中的数据原来已经是有序的了，比如 1，3，5，6，8。如果我们每次选择最后一个元素作为 pivot，那每次分区得到的两个区间都是不均等的。我们需要进行大约 n 次分区操作，才能完成快排的整个过程。每次分区我们平均要扫描大约 n/2 个元素，这种情况下，快排的时间复杂度就从 O(nlogn) 退化成了 O(n2)

  

- **平均情况下的时间复杂度就是O(nlogn)**

  

  

## 内存消耗

在分区函数中使用了巧妙的思路完成了分区工作，不需要额外的内存，所以它的**空间复杂度为 O(1)，是一个原地排序算法**



## 稳定性

因为分区的过程涉及交换操作，如果数组中有两个相同的元素，比如序列 6，8，7，6，3，5，9，4，在经过第一次分区操作之后，两个 6 的相对先后顺序就会改变。（数据 3 和 4比较的时候，3会被放到最前面，即和第一个位置的6进行交换位置，这时序列中的两个6就发生了位置改变）。

所以，**快速排序并不是一个稳定的排序算法**

