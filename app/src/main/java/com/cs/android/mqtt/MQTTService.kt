package com.cs.android.mqtt

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.alibaba.fastjson.JSON
import com.cs.common.utils.log
import com.cs.common.utils.toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.greenrobot.eventbus.EventBus

/**
 * @author ChenSen
 * @since 2021/9/29 16:56
 * @desc
 */
class MQTTService : Service() {

    companion object {
        const val TOPIC_AIR_OPEN = "air/open"                 // 空调开关
        const val TOPIC_AIR_TEMPERATURE = "air/temperature"   // 空调温度

        const val QOS_MOST_ONCE = 0                           // 最多一次
        const val QOS_LEAST_ONCE = 1                          // 至少一次
        const val QOS_EXACTLY_ONCE = 2                        // 精确一次
    }

    private var mServerUri =
        "tcp://192.168.137.240:1883"      // 服务器地址（协议+地址+端口号） 注意：如果是连接本地机器测试，不要写127.0.0.1，要写本地机器的实际ip，否则会 Unable to connect to server

    private val clientId = "android_mi10"                      // 客户端标识

    private val client = MqttAndroidClient(this, mServerUri, clientId)

    override fun onCreate() {
        super.onCreate()
        connect()
    }

    /**
     * 连接 MQTT服务器
     *
     */
    fun connect() {

        if (!client.isConnected) {

            client.setCallback(object : MqttCallbackExtended {

                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    //连接成功
                    "connectComplete".log()
                }

                override fun connectionLost(cause: Throwable?) {
                    //断开连接
                    toast("connectionLost!")
                    "connectionLost! ${cause?.message}".log()
                }

                override fun messageArrived(topic: String, message: MqttMessage?) {
                    //得到的消息
//                    toast("messageArrived! $topic  ${message?.payload}")
                    "messageArrived!   $topic   ${String(message?.payload ?: ByteArray(0))}".log()
                    handleMessage(topic, message)
                }


                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    //发送消息成功后的回调
//                    toast("deliveryComplete!")
                    "deliveryComplete! token: ${token?.messageId}".log()
                }
            })

            val connectOptions = MqttConnectOptions().apply {
                isCleanSession = true                                  // 清除缓存
                connectionTimeout = 10                                 // 置超时时间，单位：秒
                keepAliveInterval = 20                                 // 心跳包发送间隔，单位：秒
                maxInflight = 10                                       // 允许同时发送几条消息（未收到broker确认信息）
                mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1    // 选择MQTT版本

//        userName = _userName
//        password = passWord.toCharArray()

                // last will message
//        setWill(topic, "last will message".toByteArray(), 0, false)
            }

            // userContext：可选对象，用于向回调传递上下文。一般传null即可
            client.connect(connectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    toast("连接成功!")

                    subscribe(
                        arrayOf(TOPIC_AIR_OPEN, TOPIC_AIR_TEMPERATURE),
                        intArrayOf(
                            QOS_EXACTLY_ONCE, QOS_EXACTLY_ONCE
                        )
                    )
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    toast("连接失败！${exception?.message.toString()}")
                    "连接失败！${exception?.message.toString()}".log()
                }
            })

        } else {
            toast("已经连接")
        }
    }

    /**
     * 订阅消息
     */
    fun subscribe(topic: Array<String>, qos: IntArray) {

        client.subscribe(topic, qos, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                toast("订阅 $topic 成功")
                "订阅 $topic 成功".log()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                toast("订阅失败!${exception?.message}")
                "订阅失败!${exception?.message}".log()
            }
        })
    }

    /**
     * 发布消息
     *
     * [qos] 提供消息的服务质量，可传0、1或2
     * [retained] 是否在服务器保留断开连接后的最后一条消息
     */
    fun publish(qos: Int, topic: String, msg: String, retained: Boolean) {
        client.publish(topic, msg.toByteArray(), qos, retained, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                toast("发布成功！$topic")
                "发布成功！$topic".log()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                toast("发布失败！${exception?.message}")
                "发布失败！${exception?.message}".log()
            }
        })
    }


    private fun handleMessage(topic: String, message: MqttMessage?) {
        when (topic) {

            TOPIC_AIR_OPEN -> {
                message?.payload?.let {
                    val json = JSON.parseObject(String(it))
                    val key = json["msg"]

                    if (key == "open") {
                        EventBus.getDefault().post(OpenEvent(true))
                    } else if (key == "close") {
                        EventBus.getDefault().post(OpenEvent(false))
                    }
                }
            }

            TOPIC_AIR_TEMPERATURE -> {
                message?.payload?.let {
                    val json = JSON.parseObject(String(it))
                    val key = (json["msg"] as String).toInt()

                    EventBus.getDefault().post(TemperatureEvent(key))
                }
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return MQTTBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        try {
            client.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("断开连接异常！${e.message}")
        }
        stopSelf()
        super.onDestroy()
    }

    inner class MQTTBinder : Binder() {
        fun getService(): MQTTService {
            return this@MQTTService
        }
    }
}