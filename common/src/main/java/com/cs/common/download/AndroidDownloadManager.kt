package com.cs.common.download

import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_LOCAL_URI
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import java.net.URI

/**
 * @Desc 下载管理器
 * @Author ChenSen
 * @Date 2020/4/28-22:38
 *
 *
 * startDownload方法中 置下载文件的mineType。
 *
 *  {@link #MIME_TYPE_APK} 这是安卓.apk文件的类型。
 *  有些机型必须设置此方法，才能在下载完成后，点击通知栏的Notification时，才能正确的打开安装界面。
 *  不然会弹出一个Toast（can not open file）
 *
 *  MIME_TYPE_FILE 如果设置了mimeType为application/cn.trinea.download.file，
 * 我们可以同时设置某个Activity的intent-filter为application/cn.trinea.download.file，
 * 用于响应点击的打开文件
 * 例如：
 * <intent-filter>
 *      <action android:name="android.intent.action.VIEW" />
 *      <category android:name="android.intent.category.DEFAULT" />
 *      <data android:mimeType="application/cn.trinea.download.file" />
 * </intent-filter>
 *
 */
class AndroidDownloadManager(private val mContext: Context) {

    companion object {
        const val MIME_TYPE_APK = "application/vnd.android.package-archive"
        const val MIME_TYPE_FILE = "application/cn.trinea.download.file"

        const val MSG_SUCCESS = 1
        const val MSG_FAiLED = 2
        const val MSG_PROGRESS = 3
        const val MSG_PAUSED = 4

        const val QUERY_INTERVAL = 500L
    }

    private var downloadManager =
        mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadId = -1L

    private var mCallback: DownloadStateCallback? = null
    private val mHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MSG_SUCCESS -> {
                val uri = it.obj as URI
                mCallback?.onSuccess(uri)
            }
            MSG_PROGRESS -> {
                mCallback?.onProgress(it.arg1)
            }
            MSG_FAiLED -> {
                mCallback?.onFailed(it.arg1)
            }
            MSG_PAUSED -> {
                mCallback?.onPaused(it.arg1)
            }
        }
        true
    }

    private val task = Runnable {
        getCurrentState(downloadId)
    }


    /**
     * 如果 [private] 为 true,文件会下载到 SD 卡中应用的私有目录，
     * 如  file:///storage/emulated/0/Android/data/com.cs.android/files/Download/newVersion.apk
     * 这个文件是你的应用所专用的,软件卸载后，下载的文件将随着卸载全部被删除
     *
     * 注意，Android Q 引入了分区储存功能，在外部存储设备中为每个应用提供了一个“隔离存储沙盒”
     * 所以下载到 SD 卡的文件也是应用私有的 ，下载的文件将随着卸载全部被删除
     *
     * 如果 [private] 为 false,文件会下载到 SD 卡中的公共目录
     * 如 file:///storage/emulated/0/Download/newVersion.apk"
     *  注意：在 Android N 以上，需要使用 FileProvider 去访问文件，
     */
    fun startDownload(
        url: String,
        savedName: String,
        mimeType: String,
        private: Boolean = true,
        location: String = Environment.DIRECTORY_DOWNLOADS,
        title: String = "下载管理",
        desc: String = "文件下载中..."
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setMimeType(mimeType)

        if (private) {
            request.setDestinationInExternalFilesDir(mContext, location, savedName)
        } else {
            request.setDestinationInExternalPublicDir(location, savedName)
        }

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()  //设置为可被媒体扫描器找到

        request.setTitle(title)
        request.setDescription(desc)

        mHandler.removeCallbacksAndMessages(null)
        downloadId = downloadManager.enqueue(request)
        getCurrentState(downloadId)
    }

    private fun getCurrentState(id: Long) {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_PENDING -> {
                    log("STATUS_PENDING")
                    mHandler.postDelayed(task, QUERY_INTERVAL) //查询
                }
                DownloadManager.STATUS_PAUSED -> {
                    val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                    log("STATUS_PAUSED $reason")

                    val message = Message.obtain()
                    message.what = MSG_PAUSED
                    message.arg1 = reason
                    mHandler.sendMessage(message)
                }
                DownloadManager.STATUS_FAILED -> {
                    val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                    log("STATUS_FAILED $reason")

                    val message = Message.obtain()
                    message.what = MSG_FAiLED
                    message.arg1 = reason
                    mHandler.sendMessage(message)
                    mHandler.removeMessages(MSG_PROGRESS)
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uri = URI.create(cursor.getString(cursor.getColumnIndex(COLUMN_LOCAL_URI)))
                    log("STATUS_SUCCESSFUL  $uri")


                    val message = Message.obtain()
                    message.what = MSG_SUCCESS
                    message.obj = uri
                    mHandler.sendMessage(message)
                    mHandler.removeMessages(MSG_PROGRESS)
                }
                DownloadManager.STATUS_RUNNING -> {
                    log("STATUS_RUNNING")
                    val downloadBytes =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalBytes =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val progress = (downloadBytes * 100f / totalBytes).toInt()

                    val progressMessage = Message.obtain()
                    progressMessage.what = MSG_PROGRESS
                    progressMessage.arg1 = progress
                    mHandler.sendMessage(progressMessage)
                    mHandler.postDelayed(task, QUERY_INTERVAL)//继续查询
                }
            }
            cursor.close()
        }
    }


    fun setStateCallback(listener: DownloadStateCallback) {
        this.mCallback = listener
    }


    private fun log(msg: String) {
        Log.e("DOWNLAOD", msg)
    }

    /**
     * onFailed 和 onPaused   中的 reason 参数为
     * DownloadManager.ERROR_*
     */
    interface DownloadStateCallback {
        fun onSuccess(uri: URI)
        fun onFailed(reason: Int)
        fun onPaused(reason: Int)
        fun onProgress(progress: Int)
    }
}