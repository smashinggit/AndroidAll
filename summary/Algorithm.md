[toc]

# 算法
[排序算法](https://www.guoyaohua.com/sorting.html#%E7%AE%97%E6%B3%95%E5%88%86%E7%B1%BB)

排序算法可以分为内部排序和外部排序，
内部排序是数据记录在内存中进行排序，
而外部排序是因排序的数据很大，一次不能容纳全部的排序记录，在排序过程中需要访问外存。

常见的内部排序算法有：插入排序、希尔排序、选择排序、冒泡排序、归并排序、快速排序、堆排序、基数排序等

![算法](/pics/advanced/algorithm/算法.png)

图片名词解释：
n：数据规模
k：“桶” 的个数
In-place：占用常数内存，不占用额外内存
Out-place：占用额外内存

术语说明：
稳定：如果 A 原本在 B 前面，而 A=B，排序之后 A 仍然在 B 的前面。
不稳定：如果 A 原本在 B 的前面，而 A=B，排序之后 A 可能会出现在 B 的后面。
内排序：所有排序操作都在内存中完成。
外排序：由于数据太大，因此把数据放在磁盘中，而排序通过磁盘和内存的数据传输才能进行。
时间复杂度： 定性描述一个算法执行所耗费的时间。
空间复杂度：定性描述一个算法执行所需内存的大小。


# 冒泡排序 (Bubble Sort)
冒泡排序是一种简单的排序算法。
它重复地遍历要排序的序列，依次比较两个元素，如果它们的顺序错误就把它们交换过来。
遍历序列的工作是重复地进行直到没有再需要交换为止，此时说明该序列已经排序完成。
这个算法的名字由来是因为越小的元素会经由交换慢慢 “浮” 到数列的顶端。

算法步骤:
1. 比较相邻的元素。如果第一个比第二个大，就交换它们两个；
2. 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对，这样在最后的元素应该会是最大的数；
3. 针对所有的元素重复以上的步骤，除了最后一个；
4. 重复步骤 1~3，直到排序完成。

```
  /**
     * 冒泡排序
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

```




# 选择排序 (Selection Sort)

选择排序是一种简单直观的排序算法，无论什么数据进去都是 O(n²) 的时间复杂度。
所以用到它的时候，数据规模越小越好。
唯一的好处可能就是不占用额外的内存空间了吧。
它的工作原理：首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，
然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。以此类推，直到所有元素均排序完毕

算法步骤
1. 首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置
2. 再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。
3. 重复第 2 步，直到所有元素均排序完毕。

```
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
        }
        log("排序后 " + Arrays.toString(data));
        return data;
    }

```

# 插入排序 (Insertion Sort)

插入排序是一种简单直观的排序算法。
它的工作原理是通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。
插入排序在实现上，通常采用 in-place 排序（即只需用到 O(1) 的额外空间的排序），
因而在从后向前扫描过程中，需要反复把已排序元素逐步向后挪位，为最新元素提供插入空间。

插入排序的代码实现虽然没有冒泡排序和选择排序那么简单粗暴，但它的原理应该是最容易理解的了，
因为只要打过扑克牌的人都应该能够秒懂。
插入排序是一种最简单直观的排序算法，它的工作原理是通过构建有序序列，
对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入


算法步骤
1. 从第一个元素开始，该元素可以认为已经被排序；
2. 取出下一个元素，在已经排序的元素序列中从后向前扫描；
3. 如果该元素（已排序）大于新元素，将该元素移到下一位置；
4. 重复步骤 3，直到找到已排序的元素小于或者等于新元素的位置；
5. 将新元素插入到该位置后；
6. 重复步骤 2~5。

```

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
```


# 快速排序 (Quick Sort)
快速排序用到了分治思想，同样的还有归并排序。乍看起来快速排序和归并排序非常相似，都是将问题变小，先排序子串，最后合并。
不同的是快速排序在划分子问题的时候经过多一步处理，将划分的两组数据划分为一大一小，
这样在最后合并的时候就不必像归并排序那样再进行比较。但也正因为如此，划分的不定性使得快速排序的时间复杂度并不稳定。

快速排序的基本思想：
通过一趟排序将待排序列分隔成独立的两部分，
其中一部分记录的元素均比另一部分的元素小，则可分别对这两部分子序列继续进行排序，以达到整个序列有序。

算法步骤
快速排序使用分治法（Divide and conquer）策略来把一个序列分为较小和较大的 2 个子序列，
然后递回地排序两个子序列。

具体算法描述如下：
1. 从序列中随机挑出一个元素，做为 “基准”(pivot)；
2. 重新排列序列，将所有比基准值小的元素摆放在基准前面，所有比基准值大的摆在基准的后面（相同的数可以到任一边）。
在这个操作结束之后，该基准就处于数列的中间位置。这个称为分区（partition）操作；
3. 递归地把小于基准值元素的子序列和大于基准值元素的子序列进行快速排序。



# 二分法查找

当数据量很大适宜采用该方法。**采用二分法查找时，数据需是有序不重复的**

基本思想：
假设数据是按升序排序的，对于给定值 x，从序列的中间位置开始比较，
如果当前位置值等于 x，则查找成功；
若 x 小于当前位置值，则在数列的前半段中查找；
若 x 大于当前位置值则在数列的后半段中继续查找，直到找到为

```
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
```




