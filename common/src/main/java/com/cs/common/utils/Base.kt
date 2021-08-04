package com.cs.common.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.Serializable

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

inline fun <reified T> Context.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T> Context.startActivity(vararg pairs: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    addParamToIntent(intent, pairs)
    startActivity(intent)
}

fun addParamToIntent(
    intent: Intent,
    pairs: Array<out Pair<String, Any>>
) {
    pairs.forEach {
        when (val second = it.second) {
            is String -> intent.putExtra(it.first, second)
            is Int -> intent.putExtra(it.first, second)
            is Boolean -> intent.putExtra(it.first, second)
            is Serializable -> intent.putExtra(it.first, second)
            is Char -> intent.putExtra(it.first, second)
            is Long -> intent.putExtra(it.first, second)
            is Float -> intent.putExtra(it.first, second)
            //todo 完善常用类型
        }
    }
}

var enable = true
fun View.setOnValidClickListener(onClick: (View) -> Unit) {
    setOnClickListener {
        if (enable) {
            enable = false
            onClick(it)
        }

        postDelayed({
            enable = true
        }, 300)
    }
}










