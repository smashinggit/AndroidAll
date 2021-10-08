package com.cs.android.mqtt

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import com.cs.android.databinding.ActivityMqttBinding
import com.cs.common.base.BaseActivity
import com.cs.common.utils.log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author ChenSen
 * @since 2021/9/29 10:04
 * @desc 一个控制空调开关和温度的 Android 端 demo
 *   使用本地服务器是 [EMQ X 开源版](https://www.emqx.com/zh/downloads?product=broker)
 *   使用的可视化客户端是 [MQTT X](https://mqttx.app/zh)
 *
 *   实现效果如下：
 *   通过手机端来控制空调端(假定可视化客户端是空调端)： 打开/关闭空调、调节空调度数
 *   手机端的操作会改变空调端的状态，如果一个空调端绑定多个手机端，空调端的改变会同时通知到所有绑定的手机端
 *
 *
 *  QoS 发布服务质量标志:
 *  - QoS   0（默认）： 最多发一次
 *  - QoS   1： 最少发一次
 *  - QoS   2： 正好发一次
 */
class MqttActivity : BaseActivity() {

    lateinit var mBinding: ActivityMqttBinding

    var mBinder: MQTTService.MQTTBinder? = null
    var mService: MQTTService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinder = service as MQTTService.MQTTBinder
            mService = mBinder?.getService()
            mService?.connect()              //连接服务器
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBinder = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMqttBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        bindService(Intent(this, MQTTService::class.java), connection, BIND_AUTO_CREATE)
        EventBus.getDefault().register(this)
        init()
    }

    override fun onDestroy() {
        unbindService(connection)
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun init() {

        mBinding.btnConnect.setOnClickListener {
            mService?.connect()
        }

        mBinding.swSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mService?.publish(
                    MQTTService.QOS_EXACTLY_ONCE,
                    MQTTService.TOPIC_AIR_OPEN,
                    "{\"msg\":\"open\"}",
                    false
                )
            } else {
                mService?.publish(
                    MQTTService.QOS_EXACTLY_ONCE,
                    MQTTService.TOPIC_AIR_OPEN,
                    "{\"msg\":\"close\"}",
                    false
                )
            }
        }

        mBinding.btReduce.setOnClickListener {
            var tem = mBinding.tvTemperature.text.toString().toInt()
            mService?.publish(
                MQTTService.QOS_EXACTLY_ONCE,
                MQTTService.TOPIC_AIR_TEMPERATURE,
                "{\"msg\":\"${--tem}\"}",
                false
            )
        }

        mBinding.btAdd.setOnClickListener {

            var tem = mBinding.tvTemperature.text.toString().toInt()
            mService?.publish(
                MQTTService.QOS_EXACTLY_ONCE,
                MQTTService.TOPIC_AIR_TEMPERATURE,
                "{\"msg\":\"${++tem}\"}",
                false
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenEvent(event: OpenEvent) {
        "onOpenEvent! open: ${event.open}".log()

        if (event.open) {
            mBinding.bg.setBackgroundColor(Color.GREEN)
            mBinding.tvState.text = "空调状态：打开"
        } else {
            mBinding.bg.setBackgroundColor(Color.RED)
            mBinding.tvState.text = "空调状态：关闭"
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTemperatureEvent(event: TemperatureEvent) {
        "onTemperatureEvent! TemperatureEvent: ${event.temperature}".log()
        mBinding.tvTemperature.text = event.temperature.toString()
    }

}