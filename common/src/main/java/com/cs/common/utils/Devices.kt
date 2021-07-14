package com.cs.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import java.util.*

/**
 * @author ChenSen
 * @since 2021/7/12 15:35
 * @desc
 */
object Devices {+

    /**
     * 获取 AndroidId
     * @return
     */
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    /**
     * 获取手机型号
     * @return 手机型号
     */
    fun getPhoneModel(): String {
        return Build.MODEL
    }

    /**
     * 获取手机厂商名
     */
    fun getDeviceManufacturer(): String {
        return Build.MANUFACTURER
    }

    /**
     * 获取手机厂商
     * @return 手机厂商
     */
    fun getPhoneBrand(): String {
        return Build.BRAND
    }


    /**
     * 获取当前手机系统版本号
     * @return 系统版本号
     */
    fun getVersionRelease(): String {
        return Build.VERSION.RELEASE
    }


    /**
     * 获取当前手机设备名
     * 设备统一型号,不是"关于手机"的中设备名
     */
    fun getDeviceName(): String {
        return Build.DEVICE
    }


    /**
     * 获取手机主板名
     */
    fun getDeviceBoard(): String {
        return Build.BOARD
    }


    /**
     * 相机是否可用
     *
     * @return
     */
    fun isSupportCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }


    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    fun getSystemLanguageList(): Array<Locale> {
        return Locale.getAvailableLocales()
    }


    /**
     * 手机详情
     * HUAWEI HWELE ELE-AL00 10
     */
    fun getPhoneDetail(): String {
        return "${getPhoneBrand()} ${getDeviceName()} ${getPhoneModel()} ${getVersionRelease()}"
    }

}