package com.cs.common.widget.dialog

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import com.cs.common.R
import com.cs.common.utils.screenWidth
import com.cs.common.utils.setOnValidClickListener
import kotlinx.android.synthetic.main.dialog_common.tvCancel
import kotlinx.android.synthetic.main.dialog_common.btnConfirm
import kotlinx.android.synthetic.main.dialog_common.divider
import kotlinx.android.synthetic.main.dialog_common.tvMessage
import kotlinx.android.synthetic.main.dialog_common.tvTitle
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.dialog_pic.*
import kotlinx.android.synthetic.main.dialog_progress.*

class CommonDialog(
    context: Context,
    private val title: String = "提示",
    private val message: String = "新的消息",
    private val confirmText: String = "确定",
    private val cancelText: String? = "取消",
    private val onConfirm: () -> Unit,
    private val onCancel: (() -> Unit)? = null,
    private val type: Type = Type.ALTER
) : Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_common)

        tvTitle.text = title
        tvMessage.text = message

        btnConfirm.text = confirmText
        btnConfirm.setOnClickListener {
            dismiss()
            onConfirm()
        }

        tvCancel.text = cancelText
        tvCancel.setOnClickListener {
            dismiss()
            onCancel?.invoke()
        }

        if (type == Type.ALTER) {
            tvCancel.visibility = View.GONE
            divider.visibility = View.GONE
        }

        val layoutParam = window?.attributes
        layoutParam?.width = (context.screenWidth() * 0.8).toInt()
        layoutParam?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam?.gravity = Gravity.CENTER
        window?.attributes = layoutParam
    }

}


enum class Type {
    ALTER, QUERY
}

class PicDialog(
    context: Context,
    private val title: String = "提示",
    @DrawableRes
    private val pic: Int,
    private val msg: String = "",
    private val confirmText: String = "确定",
    private val cancelText: String = "取消",
    private val onConfirm: () -> Unit,
    private val onCancel: (() -> Unit)? = null,
    private val type: Type = Type.ALTER
) : Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pic)

        tvTitle.text = title
        ivPic.setImageResource(pic)
        if (msg.isEmpty()) {
            tvMessage.visibility = View.GONE
        } else {
            tvMessage.text = msg
        }

        btnConfirm.text = confirmText
        btnConfirm.setOnValidClickListener {
            dismiss()
            onConfirm()
        }

        tvCancel.text = cancelText
        tvCancel.setOnValidClickListener {
            dismiss()
            onCancel?.invoke()
        }

        if (type == Type.ALTER) {
            tvCancel.visibility = View.GONE
            divider.visibility = View.GONE
        }

        setCancelable(false)

        val layoutParam = window?.attributes
        layoutParam?.width = (context.screenWidth() * 0.8).toInt()
        layoutParam?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam?.gravity = Gravity.CENTER
        window?.attributes = layoutParam
    }
}

class LoadingDialog(context: Context, private val tip: String = "加载中...") :
    Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)


        tvTip.text = tip
        startRotation(ivLoading)
//        setCancelable(false)

        val layoutParam = window?.attributes
        layoutParam?.width = (context.screenWidth() * 0.8).toInt()
        layoutParam?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam?.gravity = Gravity.CENTER
        window?.attributes = layoutParam
    }

    private fun startRotation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        animator.interpolator = LinearInterpolator()
        animator.duration = 1000
        animator.repeatMode = ObjectAnimator.RESTART
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()
    }
}

class ProgressDialog(
    context: Context,
    private val max: Int = 100
) : Dialog(context, R.style.CustomDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)

//        setCancelable(false)
        progressBar.setMax(max)

        val layoutParam = window?.attributes
        layoutParam?.width = (context.screenWidth() * 0.8).toInt()
        layoutParam?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam?.gravity = Gravity.CENTER
        window?.attributes = layoutParam
    }

    fun setMax(max: Int) {
        progressBar.setMax(max)
    }

    fun setProgress(progress: Int) {
        progressBar.setProgress(progress)
    }
}


fun Activity.alert(title: String, msg: String, onConfirm: () -> Unit): CommonDialog {
    val dialog =
        CommonDialog(this, title = title, message = msg, onConfirm = onConfirm, type = Type.ALTER)
    if (isFinishing.not()) {
        dialog.show()
    }
    return dialog
}

fun Activity.query(title: String, msg: String, onConfirm: () -> Unit): CommonDialog {
    val dialog =
        CommonDialog(this, title = title, message = msg, onConfirm = onConfirm, type = Type.QUERY)
    if (isFinishing.not()) {
        dialog.show()
    }
    return dialog
}

fun Activity.picDialog(
    title: String,
    @DrawableRes pic: Int,
    msg: String,
    onConfirm: () -> Unit
): PicDialog {
    val dialog =
        PicDialog(context = this, title = title, pic = pic, msg = msg, onConfirm = onConfirm)
    if (isFinishing.not()) {
        dialog.show()
    }
    return dialog
}


fun Activity.loading(tip: String): LoadingDialog {
    val dialog = LoadingDialog(this, tip)
    if (isFinishing.not()) {
        dialog.show()
    }
    return dialog
}

fun Activity.progress(max: Int = 100): ProgressDialog {
    val dialog = ProgressDialog(this, max)
    if (isFinishing.not()) {
        dialog.show()
    }
    return dialog
}


