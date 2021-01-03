package com.cs.android

import android.app.ProgressDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.cs.common.base.BaseActivity
import com.cs.common.utils.screenWidth
import com.cs.common.utils.setOnValidClickListener
import com.cs.common.utils.toast
import com.cs.common.widget.dialog.*
import kotlinx.android.synthetic.main.activity_dialog.*
import kotlinx.android.synthetic.main.dialog_pic.view.*
import kotlin.concurrent.thread

class DialogActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        initUi()
    }

    private fun initUi() {

        btnSimple.setOnValidClickListener {
            showSimpleDialog()
        }

        btnList.setOnValidClickListener {
            showListDialog()
        }

        btnSingleCheck.setOnValidClickListener {
            showSingleCheckDialog()
        }

        btnMultiCheck.setOnValidClickListener {
            showMultiCheckDialog()
        }

        btnRawLoading.setOnValidClickListener {
            showRawLoadingDialog()
        }

        btnRawProgress.setOnValidClickListener {
            showRawProgressDialog()
        }



        btnHalfCustom.setOnValidClickListener {
            showHalfCustomDialog()
        }


        btnAlert.setOnValidClickListener {
            showAlertDialog()
        }

        btnQuery.setOnValidClickListener {
            showQueryDialog()
        }

        btnLoading.setOnValidClickListener {
            showLoadingDialog()
        }

        btnProgress.setOnValidClickListener {
            showProgressDialog()
        }

        btnPicDialog.setOnValidClickListener {
            showPicDialog()
        }

        btnAddPic.setOnValidClickListener {
            showAddPicDialog()
        }

    }

    private fun showAddPicDialog() {
        val dialog = SelectPicDialog(this, {
            toast("相机")
        }, {
            toast("图库")
        })
        dialog.show()
    }

    private fun showPicDialog() {
        picDialog("提示", R.drawable.ic_success, "办理成功") {
            toast("确定")
        }
    }

    private fun showProgressDialog() {
        val progress = progress(100)
        thread {
            var current = 0
            while (current < 100) {
                current += 5
                Thread.sleep(200)
                runOnUiThread {
                    progress.setProgress(current)
                }
            }
        }
    }


    private fun showLoadingDialog() {
        val loading = loading("加载中...")
    }

    private fun showQueryDialog() {
        query("提示", "支付宝到账一亿元") {

        }
    }

    private fun showAlertDialog() {
        alert("提示", "支付宝到账一亿元") {

        }
    }

    /**
     * 半自定义
     * 可以修改大小、位置等
     */
    private fun showHalfCustomDialog() {

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_pic, null).apply {
            tvTitle.text = "通知"
            tvMessage.text = "操作成功"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .show()

        view.btnCancel.setOnClickListener {
            toast("取消")
            dialog.cancel()
        }
        view.btnConfirm.setOnClickListener {
            toast("确定")
            dialog.cancel()
        }

        val layoutParam = dialog.window?.attributes
        layoutParam?.width = (screenWidth() * 0.8).toInt()
        layoutParam?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam?.gravity = Gravity.CENTER

        dialog.window?.attributes = layoutParam
    }

    /**
     * 进度条对话框
     */
    private fun showRawProgressDialog() {
        val progressDialog = ProgressDialog(this).apply {
            max = 100
            progress = 0
            setMessage("下载中...")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        }
        progressDialog.show()
    }

    /**
     * Loading 对话框
     */
    private fun showRawLoadingDialog() {

        val progressDialog = ProgressDialog(this).apply {
            max = 100
            progress = 0
            setMessage("加载中...")
        }
        progressDialog.show()
    }

    /**
     * 多选对话框
     */
    private fun showMultiCheckDialog() {
        val array = arrayOf<CharSequence>("张飞", "关羽", "刘备", "曹操", "吕布", "黄忠", "马超", "夏侯惇", "张辽")
        val initChecked = BooleanArray(array.size) {
            return@BooleanArray false
        }

        val checkedResult = ArrayList<Int>()

        AlertDialog.Builder(this)
            .setTitle("请选择3位英雄")
            .setMultiChoiceItems(array, initChecked) { _, which, isChecked ->
                if (isChecked) {
                    checkedResult.add(which)
                } else {
                    checkedResult.remove(which)
                }
            }
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * 单选对话框
     */
    private fun showSingleCheckDialog() {
        val array = arrayOf("张飞", "关羽", "刘备", "曹操", "吕布", "黄忠", "马超", "夏侯惇", "张辽")
        var choice = 0
        AlertDialog.Builder(this)
            .setTitle("请选择您的英雄")
            .setSingleChoiceItems(array, choice) { _, which ->
                choice = which
            }
            .setPositiveButton("确定") { dialog, _ ->
                toast("选择了 ${array[choice]}")
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * 列表对话框
     */
    private fun showListDialog() {
        val array = arrayOf("张飞", "关羽", "刘备", "曹操", "吕布", "黄忠", "马超", "夏侯惇", "张辽")
        val dialog = AlertDialog.Builder(this)
            .setTitle("请选择您的英雄")
            .setItems(array) { dialog, which ->
                toast("选择了 ${array[which]}")
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }


    /**
     * 原生 Dialog
     */
    private fun showSimpleDialog() {

        val dialog = AlertDialog.Builder(this)
            .setTitle("通知")
            .setMessage("支付宝到账一亿元")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("中立") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}