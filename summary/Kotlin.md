[toc]
# Kotlin


# kotlin为什么被设计出来

完全兼容Java
更少的空指针异常
更少的代码量，更快的开发速度

# kotlin工作原理

首先，我们了解下Java的工作原理：
Java 代码是经过编译才能运行的。首先会编译成class文件，然后通过java虚拟机运行，在Android中也就是ART。
所以，任何语言只要能被编译成符合规格的class文件，就能被java虚拟机运行，也就能运行在我们的Android手机上，
kotlin亦是如此

# 扩展函数（Extension Function）  和 扩展属性

扩展函数，其实就是扩展类的函数，可以在已有的类中添加新的方法，比继承更加简洁优雅方便。

```
fun Activity.showToast( msgId:Int){
    Toast.makeText(this,msgId,Toast.LENGTH_SHORT).show()
}
```

```
var <T> MutableList<T>.lastData: T
    //获取List中最后一个对象
    get() = this[this.size - 1]
    //设置List中最后一个对象的值
    set(value) {
        this[this.size - 1] = value
    }
```

# 扩展原理

kotlin这个扩展功能确实设计的很巧妙，那就一起来研究下它的原理：
按照上面的方法，也就是Tools -> Kotlin -> Show Kotlin Bytecode -> Decomplie， 
我们得到showToast扩展函数和使用代码所对应的java代码：
```
//扩展函数
public final class UtilsKt {
   public static final void showToast(@NotNull Activity $this$showToast, int msgId) {
      Intrinsics.checkParameterIsNotNull($this$showToast, "$this$showToast");
      Toast.makeText((Context)$this$showToast, msgId, 0).show();
   }
}

//使用
UtilsKt.showToast(this, 1900026);
```

可以看到所谓的扩展函数不过就是自动生成一个带有当前对象的函数，扩展函数的所在类被public final修饰，
函数被public static final修饰，然后扩展的那个类被作为方法的一个参数传进去，这样就跟我们用java的时候写的工具类很像。


# let、apply、with、run

let 默认当前这个对象作为闭包的it参数，返回值为函数最后一行，或者return
apply 在apply函数范围内，可以任意调用该对象的任意方法，并返回该对象
with 返回值是最后一行，这点类似let。可以直接调用对象的方法，这点类似apply。
run  run和with很像，可以调用对象的任意函数，返回值是最后一行


# lateinit和by lazy