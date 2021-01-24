package com.cs.common.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Activity.query(message: String, onConfirmListener: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle("注意")
        .setMessage(message)
        .setNegativeButton("确定",) { dialog, which ->
            onConfirmListener()
            dialog.dismiss()
        }
        .setPositiveButton("取消") { dialog, which ->
            dialog.dismiss()
        }
        .setCancelable(false)
        .create()
        .show()
}


fun Fragment.query(message: String, onConfirmListener: () -> Unit) {
    AlertDialog.Builder(requireContext())
        .setTitle("注意")
        .setMessage(message)
        .setNegativeButton("确定",) { dialog, which ->
            onConfirmListener()
            dialog.dismiss()
        }
        .setPositiveButton("取消") { dialog, which ->
            dialog.dismiss()
        }
        .setCancelable(false)
        .create()
        .show()
}