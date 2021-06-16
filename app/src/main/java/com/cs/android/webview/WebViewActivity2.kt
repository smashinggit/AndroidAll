package com.cs.android.webview

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.cs.android.databinding.ActivityWebview2Binding
import com.cs.common.base.BaseActivity
import com.cs.common.utils.Logs
import com.cs.common.utils.toast
import com.github.lzyzsd.jsbridge.BridgeWebView

/**
 * @author ChenSen
 * @since 2021/6/11 9:16
 * @desc  https://github.com/lzyzsd/JsBridge  此项目好像已经未维护，所以这个类功能未完成
 */
class WebViewActivity2 : BaseActivity() {

    private lateinit var mViewBinding: ActivityWebview2Binding
    private var _mWebView: BridgeWebView? = null
    private val mWebView get() = _mWebView!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = ActivityWebview2Binding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        initWebView()

    }

    private fun initWebView() {
        _mWebView = BridgeWebView(this)
        mViewBinding.root.addView(_mWebView)


        mWebView.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = true   //将图片调整到适合WebView的大小
            loadsImagesAutomatically = true //支持自动加载图片

            //5.0 以后的WebView加载的链接为Https开头，但是链接里面的内容，比如图片为Http链接，这时候，图片就会加载不出来
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = mixedContentMode
            }

            setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提
            builtInZoomControls = true //置内置的缩放控件。若为false，则该WebView不可缩放
            loadWithOverviewMode = true //缩放至屏幕的大小
            displayZoomControls = false //隐藏原生的缩放控件

            allowFileAccess = true //设置可以访问文件
            javaScriptCanOpenWindowsAutomatically = true  //支持通过JS打开新窗口
            defaultTextEncodingName = "utf-8" //设置编码格式

            domStorageEnabled = true //开启 DOM storage API 功能  不设置的话有些网页无法加载，比如淘宝

            cacheMode = WebSettings.LOAD_DEFAULT
        }


        //辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等
        mWebView.webChromeClient = object : WebChromeClient() {
            //获得网页的加载进度
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            }

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {
                Logs.logd("onJsAlert message=$message")

                //回调这个方法时，JS会被阻塞
                AlertDialog.Builder(this@WebViewActivity2)
                    .setTitle("onJsAlert")
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        result.confirm() //通知JS继续执行
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()

                return true
            }

            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {
                Logs.logd("onJsConfirm message=$message")

                AlertDialog.Builder(this@WebViewActivity2)
                    .setTitle("onJsConfirm")
                    .setMessage(message)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, _ ->
                        result.confirm()
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, _ ->
                        result.cancel()
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()

                // To show a custom dialog, the app should return {@code true} from this
                // method, in which case the default dialog will not be shown and JavaScript
                // execution will be suspended
                // true表示点击了确认；false表示点击了取消
                return true
            }

            //支持javascript输入框
            //点击确认返回输入框中的值，点击取消返回 null。
            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult
            ): Boolean {
                Logs.logd("onJsPrompt message=$message")

                val editText = EditText(this@WebViewActivity2)
                AlertDialog.Builder(this@WebViewActivity2)
                    .setTitle(message)
                    .setView(editText)
                    .setPositiveButton("OK") { dialog, _ ->
                        result.confirm(editText.text.toString())
                        Logs.logd("onJsPrompt 给web端传值 ：${editText.text.toString()}")
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        result.cancel()
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()

                return true
            }
        }


        mWebView.registerHandler("JsCallJava") { data, function ->
            toast("收到JS的调用, handler = JsCallJava ,data = $data")
            Logs.logd("收到JS的调用, handler = JsCallJava ,data = $data")
            function.onCallBack("JsCallJava 执行完毕,返回数据给JS")
        }

        mWebView.callHandler("JavaCallJs", "Data from Java") { data ->
            Logs.logd("handler = JavaCallJs，data fromm web = $data")
        }

        mWebView.loadUrl("file:///android_asset/JsJava2.html")

    }


    override fun onResume() {
        mWebView.run {
            onResume()
            resumeTimers()
        }
        super.onResume()
    }

    override fun onPause() {
        mWebView.run {
            onPause()
            pauseTimers()
        }
        super.onPause()

    }

    override fun onDestroy() {
        _mWebView?.run {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            mViewBinding.root.removeView(this)
            destroy()
        }
        _mWebView = null
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

}