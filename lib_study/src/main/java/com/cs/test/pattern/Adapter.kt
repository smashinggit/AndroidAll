package com.cs.test.pattern

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc 适配器模式
 **/

//类适配器

interface VgaOrHdmi {
    fun getVgaOrHdmi(): String //输出 VGA 或是 Hdmi 接口
}

open class MiniDp {

    fun outPutMinkDp(): String {
        return "我是 mac 上的 MiniDp 输入接口"
    }
}

class MidiDp2VgaOrHdmiAdapter : MiniDp(), VgaOrHdmi {

    override fun getVgaOrHdmi(): String {
        return convertMiniDp2VgaOrHdmi()
    }

    // 把 MINIDP 转化成 VAG 或 HDMI 方法
    private fun convertMiniDp2VgaOrHdmi(): String {
        var source = outPutMinkDp()    //拿到原输出
        val result = "输出变成  VGA 和 HDMI 接口"  //更改为目标输出
        return result
    }
}


//对象适配器
class MidiDp2VgaOrHdmiAdapter2(private val mMinuDp: MiniDp) : VgaOrHdmi {

    override fun getVgaOrHdmi(): String {
        var source = mMinuDp.outPutMinkDp()  //源数据
        val result = "输出变成  VGA 和 HDMI 接口"  //更改为目标输出
        return result
    }
}


object AdapterTest {
    @JvmStatic
    fun main(args: Array<String>) {

    }
}