package com.cs.android.base

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cs.android.R
import java.io.File

/**
 * @author ChenSen
 * @since 2021/7/9 16:43
 * @desc
 *
 * 分区存储：
 * 1、App访问自身内部存储空间、访问外部存储空间-App私有目录不需要任何权限(这个与Android 10.0之前一致)
 * 2、外部存储空间-共享存储空间、外部存储空间-其它目录 App无法通过路径直接访问，不能新建、删除、修改目录/文件等
 * 3、外部存储空间-共享存储空间、外部存储空间-其它目录 需要通过Uri访问
 *
 */
class StorageActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)


        val infoInner = StringBuffer().apply {
            append("dataDir      : ${dataDir.absolutePath} \n \n")
            append("filesDir     : ${filesDir.absolutePath} \n \n")
            append("cacheDir     : ${cacheDir.absolutePath} \n \n")
            append("codeCacheDir : ${codeCacheDir.absolutePath} \n \n")
        }

        val infoExternal = StringBuffer().apply {
            append("externalCacheDir      : ${externalCacheDir?.absolutePath} \n \n")
            append("externalCacheDirs     : ${sprite(externalCacheDirs)} \n")
            append("externalMediaDirs     : ${sprite(externalMediaDirs)} \n")
            append("getExternalFilesDir(null)    : ${getExternalFilesDir(null)} \n \n")
            append("DIRECTORY_DCIM        : ${getExternalFilesDir(Environment.DIRECTORY_DCIM)} \n \n")
            append("DIRECTORY_PICTURES    : ${getExternalFilesDir(Environment.DIRECTORY_PICTURES)} \n \n")
            append(
                "Environment.DIRECTORY_PICTURES    : ${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )} \n \n"
            )
        }

        val infoExternalPublic = StringBuffer().apply {
            append(
                "Environment.DIRECTORY_PICTURES    : ${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )} \n \n"
            )
            append(
                "Environment.DIRECTORY_DCIM    : ${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                )} \n \n"
            )
            append(
                "Environment.DIRECTORY_DOWNLOADS    : ${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )} \n \n"
            )
        }

        findViewById<TextView>(R.id.tvInnerInfo).text = infoInner
        findViewById<TextView>(R.id.tvExternalInfo).text = infoExternal
        findViewById<TextView>(R.id.tvExternalPublicInfo).text = infoExternalPublic
    }

    private fun sprite(dirs: Array<File>): String {
        val result = StringBuffer()
        dirs.forEach {
            result.append("${it.absolutePath} \n")
        }
        return result.toString()
    }
}