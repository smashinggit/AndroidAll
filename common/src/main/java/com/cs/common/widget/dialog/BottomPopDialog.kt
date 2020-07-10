package com.cs.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.cs.common.R

/***
 * 从底部弹出的对话框
 */
abstract class BottomPopDialog(context: Context) : Dialog(context, R.style.PopDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCanceledOnTouchOutside(true)
        setCancelable(true)
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.BottomDialogAnim)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,  //横向全屏
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}