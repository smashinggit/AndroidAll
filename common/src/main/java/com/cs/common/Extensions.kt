package com.cs.common

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * @Desc 扩展函数
 * @Author ChenSen
 * @Date 2020/4/29-22:58
 *
 */

fun Context.log(msg: String) {
    Log.e("tag", msg)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}