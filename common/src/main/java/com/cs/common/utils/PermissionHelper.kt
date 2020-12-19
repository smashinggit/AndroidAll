package com.cs.common.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @Desc
 * @Author ChenSen
 * @Date 2020/5/6-20:59
 *
 * 用户点击了不再提示之后，权限申请时不会再弹出权限申请提示框了，而是直接返回失败，
 * 在这种情况下，我们应该引导用户去设置里把权限打开，
 * 但是android API中并没有判断用户是否勾选了“不再询问”的监听，那我们开发者怎么去判断用户是否勾选了“不再询问”呢？
 * 这个可以通过shouldShowRequestPermissionRationale()，
 * 该方法的作用是是否需要向用户解释为何需要申请该权限
 * 用户拒绝后，该方法返回 true，
 * 用户勾选了“不再询问”时该方法返回 false
 * 因此我们可以推导出，如果用户第二次申请权限被拒绝并且shouldShowRequestPermissionRationale()返回false
 * 那么用户一定是勾选了不再询问，接下来可以通过以下代码引导用户前往设置页面打开权限：
 *
 */
class PermissionHelper {

    companion object {
        const val PERMISSION_REQUEST_CODE_ALTER_WINDOW = 0x3003
        const val PERMISSION_REQUEST_CODE = 0x1001
        const val PERMISSION_SETTING_CODE = 0X2002
    }

    private var onGranted: (ArrayList<String>) -> Unit = {}  // 获得权限后的回调
    private var onDenied: (ArrayList<String>) -> Unit = {}   // 权限被拒绝的回调
    private var onPermanentDenied: (ArrayList<String>) -> Unit = {}   // 勾选不在询问权限被拒绝的回调
    private lateinit var mContext: Activity


    fun with(context: Activity): PermissionHelper {
        this.mContext = context
        return this
    }

    /**
     * 检查权限 [permissions]，如果有权限未获得，则申请权限
     */
    fun checkPermissions(permissions: Array<String>): PermissionHelper {
        val grantedPermissions = arrayListOf<String>()   // 已经获得的权限
        val deniedPermissions = arrayListOf<String>()    // 没有获得的权限

        //逐个判断你要的权限是否已经获得
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                grantedPermissions.add(it)
            } else {
                deniedPermissions.add(it)
            }
        }

        if (grantedPermissions.isNotEmpty()) {
            onGranted(grantedPermissions)
        }

        if (deniedPermissions.isNotEmpty()) {
            requestPermissions(deniedPermissions.toTypedArray())
        }

        return this
    }

    /**
     * 申请权限 [permissions]
     */
    private fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(mContext, permissions, PERMISSION_REQUEST_CODE)
    }

    /**
     * 申请权限的结果
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            val grantedPermissions = ArrayList<String>()  //申请到的权限
            val deniedPermissions = ArrayList<String>()   //未申请到的权限
            val permanentDeniedPermissions = ArrayList<String>()   //勾选了不再询问的未申请到的权限

            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions.add(permissions[i])
                } else {
                    grantedPermissions.add(permissions[i])
                }
            }

            if (grantedPermissions.isNotEmpty()) {
                onGranted(grantedPermissions)
            }

            if (deniedPermissions.isNotEmpty()) { //当有权限被拒绝的时候
                deniedPermissions.forEach {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, it)
                    ) {
                        //用户点了拒绝，但是没有勾选不在提示

                    } else {
                        permanentDeniedPermissions.add(it)
                    }
                }

                deniedPermissions.removeAll(permanentDeniedPermissions)
            }

            if (deniedPermissions.isNotEmpty()) {
                onDenied(deniedPermissions)
            }

            if (permanentDeniedPermissions.isNotEmpty()) {
                onPermanentDenied(permanentDeniedPermissions)
            }
        }
    }


    fun onGranted(onGranted: (ArrayList<String>) -> Unit): PermissionHelper {
        this.onGranted = onGranted
        return this
    }

    fun onDenied(onDenied: (ArrayList<String>) -> Unit): PermissionHelper {
        this.onDenied = onDenied
        return this
    }

    fun onPermanentDenied(onPermanentDenied: (ArrayList<String>) -> Unit): PermissionHelper {
        this.onPermanentDenied = onPermanentDenied
        return this
    }

    /**
     * 当用户拒绝并且勾选了不在提示，再次申请权限的时候就不会再弹出权限申请的对话框，
     * 而是直接在 Activity#onRequestPermissionsResult 返回 PackageManager.PERMISSION_DENIED
     *
     * 这种情况下，弹出一个标题为 [title]，内容为 [message] 的对话框，引导用户去设置页面打开权限
     *
     */
    fun showPermissionSettingDialog(
        title: String = "开启权限",
        message: String = "由于您拒绝了相关的权限，请您到应用设置页面打开相关的权限，否则程序的部分功能无法正常使用",
        onCancel: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which -> }
    ) {
        AlertDialog.Builder(mContext)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                val uri = Uri.fromParts("package", mContext.packageName, null)
                intent.data = uri
                mContext.startActivityForResult(intent, PERMISSION_SETTING_CODE)
                dialog.cancel()
            }
            .setNegativeButton("取消", onCancel)
            .show()
    }

    /**
     *请求悬浮窗权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun checkoutSystemAlterWindow(context: Activity, onGranted: () -> Unit) {
        if (Settings.canDrawOverlays(context)) {
            onGranted()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivityForResult(intent, PERMISSION_REQUEST_CODE_ALTER_WINDOW)
        }
    }
}