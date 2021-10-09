package com.cs.android.spi

import android.os.Bundle
import com.cs.common.base.BaseActivity
import java.util.*

/**
 * @author ChenSen
 * @since 2021/10/9 17:46
 * @desc  https://juejin.cn/post/7004729690485162015
 *
 *   SPI 机制，全称为 Service Provider Interface，是一种服务发现机制。
 *  它通过在 ClassPath 路径下的 META-INF/services 文件夹查找文件，自动加载文件里所定义的类
 *
 *  要使用 Java SPI，需要遵循如下约定：
 *  1、当服务提供者提供了接口的一种具体实现后，在jar包的META-INF/services目录下创建一个以“接口全限定名”为命名的文件，
 *      内容为实现类的全限定名
 *  2、接口实现类所在的jar包放在主程序的classpath中；
 *  3、主程序通过java.util.ServiceLoder动态装载实现模块，它通过扫描META-INF/services目录下的配置文件找到实现类的全限定名，
 *     把类加载到JVM；
 *  4、SPI的实现类必须携带一个不带参数的构造方法；
 */
class SpiActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceLoader = ServiceLoader.load(IShout::class.java)
        serviceLoader.iterator().forEach {
            print("class : ${it.javaClass.name}")
            it.shout()
        }

    }
}