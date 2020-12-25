package com.cs.common.utils

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 利用 Kotlin 的委托属性完成此类的封装
 *
 *使用此类之前务必先调用 init(context: Context, name: String) 方法进行初始化
 *
 * 使用方法：
 * 1. 在此类中定义一个变量。例如 ：   var HOST: String by SharedPreferenceDelegates.string()
 * 2. 读取值：  SharedPreferencesUtils.HOST
 * 3. 保存值： SharedPreferencesUtils.HOST = "value"
 *
 */
object SharedPreferencesUtils {

    internal lateinit var sharedPreference: SharedPreferences

    fun init(context: Context, name: String) {
        sharedPreference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    var HOST: String by SharedPreferenceDelegates.string()
    var PORT: String by SharedPreferenceDelegates.string()

}

private object SharedPreferenceDelegates {


    fun int(defaultValue: Int = 0) = object : ReadWriteProperty<SharedPreferencesUtils, Int> {

        override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): Int {

            return thisRef.sharedPreference.getInt(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesUtils, property: KProperty<*>, value: Int) {
            thisRef.sharedPreference.edit().putInt(property.name, value).apply()
        }
    }

    fun long(defaultValue: Long = 0L) = object : ReadWriteProperty<SharedPreferencesUtils, Long> {

        override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): Long {
            return thisRef.sharedPreference.getLong(property.name, defaultValue)
        }

        override fun setValue(
            thisRef: SharedPreferencesUtils,
            property: KProperty<*>,
            value: Long
        ) {
            thisRef.sharedPreference.edit().putLong(property.name, value)
        }
    }

    fun float(defaultValue: Float = 0.0f) =
        object : ReadWriteProperty<SharedPreferencesUtils, Float> {
            override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): Float {
                return thisRef.sharedPreference.getFloat(property.name, defaultValue)
            }

            override fun setValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>,
                value: Float
            ) {
                thisRef.sharedPreference.edit().putFloat(property.name, value).apply()
            }
        }

    fun boolean(defaultValue: Boolean = false) =
        object : ReadWriteProperty<SharedPreferencesUtils, Boolean> {
            override fun getValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>
            ): Boolean {
                return thisRef.sharedPreference.getBoolean(property.name, defaultValue)
            }

            override fun setValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>,
                value: Boolean
            ) {
                thisRef.sharedPreference.edit().putBoolean(property.name, value).apply()
            }
        }


    fun string(defaultValue: String = "") =
        object : ReadWriteProperty<SharedPreferencesUtils, String> {
            override fun getValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>
            ): String {
                return thisRef.sharedPreference.getString(property.name, defaultValue) ?: ""
            }

            override fun setValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>,
                value: String
            ) {
                thisRef.sharedPreference.edit().putString(property.name, value).apply()
            }
        }

    fun stringSet(defaultValue: Set<String>? = null) =
        object : ReadWriteProperty<SharedPreferencesUtils, Set<String>?> {
            override fun getValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>
            ): Set<String>? {
                return thisRef.sharedPreference.getStringSet(property.name, defaultValue)
            }

            override fun setValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>,
                value: Set<String>?
            ) {
                thisRef.sharedPreference.edit().putStringSet(property.name, value).apply()
            }

        }
}

