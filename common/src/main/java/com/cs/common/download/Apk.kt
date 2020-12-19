package com.cs.common.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.cs.common.toast
import java.io.File
import java.net.URI

/**
 * @Desc
 * @Author ChenSen
 * @Date 2020/5/5-9:33
 *
 *
 * 1. 为了支持 7.0 以上的系统，需要在 AndroidManifest 中添加以下代码
 * <provider
 *  android:name="android.support.v4.content.FileProvider"
 *  android:authorities="com.xxx.fileprovider"
 *  android:grantUriPermissions="true"
 *  android:exported="false">
 *    <meta-data
 *      android:name="android.support.FILE_PROVIDER_PATHS"
 *      android:resource="@xml/file_paths" />
 * </provider>
 *
 * 2. 在 res 文件下新建 xml 文件夹，然后创建 file_paths.xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
<!-- 表示应用程序内部存储区中的文件/子目录中的文件 物理路径相当于Context.getFilesDir() + /path/ -->
<files-path
name="name"
path="path" />
<!-- 表示应用程序内部存储区缓存子目录中的文件 物理路径相当于 Context.getCacheDir() + /path/-->
<cache-path
name="name"
path="path" />
<!-- 表示外部存储区根目录中的文件 物理路径相当于Environment.getExternalStorageDirectory() + /path/ -->
<external-path
name="name"
path="path" />
<!-- 表示应用程序外部存储区根目录中的文件 物理路径相当于**Context.getExternalFilesDir(String) **+ /path/ -->
<external-files-path
name="name"
path="path" />
<!-- 表示应用程序外部缓存区根目录中的文件 物理路径相当于 Context.getExternalCacheDir() + /path/  -->
<external-cache-path
name="nam"
path="path" />
</paths>
 *
 * 3.  Android8.0、9.0需要请求未知来源应用安装权限
 *  <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
 *
 */

object Apk {
    const val MIME_TYPE_APK = "application/vnd.android.package-archive"
    const val AUTHORITY =
        "com.cs.android.FileProvider"  //注意，这里需要修改为在AndroidManifest中设置的android:authorities

    fun install(context: Context, uri: URI) {
        try {
            val file = File(uri)
            if (!file.exists()) {
                context.toast("文件不存在!")
                return
            }

            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //在AndroidManifest中的android:authorities值
                val uri = FileProvider.getUriForFile(context, AUTHORITY, file)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(uri, MIME_TYPE_APK)
            } else {
                intent.setDataAndType(Uri.fromFile(file), MIME_TYPE_APK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            context.toast("文件解析失败! ${e.message}")
        }
    }

}