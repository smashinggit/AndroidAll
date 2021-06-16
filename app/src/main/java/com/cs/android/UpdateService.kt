package com.cs.android

import android.app.IntentService
import android.content.Intent
import com.cs.common.download.AndroidDownloadManager
import com.cs.common.download.Apk
import com.cs.common.utils.log
import com.cs.common.utils.toast
import java.net.URI

/**
 * @Desc
 * @Author ChenSen
 * @Date 2020/5/6-20:05
 *
 */

class UpdateService : IntentService("UpdateService") {

    override fun onHandleIntent(intent: Intent?) {
        log("UpdateService onHandleIntent")
        showProgress()
        update()
    }

    private fun showProgress() {

    }


    private fun update() {
        val downloadManager = AndroidDownloadManager(this)

        val apkUrl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk"
        val apkName = "qq_release.apk"

        downloadManager.startDownload(
            apkUrl, apkName, AndroidDownloadManager.MIME_TYPE_APK,
            true
        )
        downloadManager.setStateCallback(object : AndroidDownloadManager.DownloadStateCallback {
            override fun onSuccess(uri: URI) {
                toast("下载成功")
                log("下载成功 $uri")
                Apk.install(this@UpdateService, uri)
            }

            override fun onFailed(reason: Int) {
                toast("下载失败")
            }

            override fun onPaused(reason: Int) {
            }

            override fun onProgress(progress: Int) {
                log("下载进度 $progress")
            }
        })
    }

}