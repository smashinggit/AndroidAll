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

        findViewById<TextView>(R.id.tvInnerInfo).text = infoInner
        findViewById<TextView>(R.id.tvExternalInfo).text = infoExternal
    }

    private fun sprite(dirs: Array<File>): String {
        val result = StringBuffer()
        dirs.forEach {
            result.append("${it.absolutePath} \n")
        }
        return result.toString()
    }
}