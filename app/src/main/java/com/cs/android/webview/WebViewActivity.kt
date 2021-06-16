package com.cs.android.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.webkit.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.cs.android.databinding.ActivityWebviewBinding
import com.cs.common.base.BaseActivity
import com.cs.common.utils.Logs

/**
 * @author ChenSen
 * @since 2021/6/10 16:30
 * @desc  WebView的使用
 *
 *
 * 加载不同资源
 * 1. 加载一个网页：
 *    webView.loadUrl("http://www.baidu.com");
 * 2. 加载assets中的网页：
 *    webView.loadUrl(“file:///android_asset/index.html”);
 * 3. 加载手机本地的html页面
 *    webView.loadUrl(“content://com.android.htmlfileprovider/sdcard/index.html”);
 * 4. 加载 HTML 页面的一小段内容
 *    webView.loadData(String data, String mimeType, String encoding)
 *
 * Java调用JS
 * 1. 通过WebView的loadUrl（）
 * 2. 通过WebView的evaluateJavascript（"javascript:funName()"）
 *
 * JS调用Java
 * 1. 通过WebView的addJavascriptInterface（）进行对象映射
 * 2. 通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
 * 3. 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息
 *
 *
 * 如果在加载URL的时候想添加Header信息，可以复写loadUrl()方法
 * @Override
 * public void loadUrl(String url) {
 *   Map<String, String> map = new HashMap<String, String>();
 *   map.put("ajax", "true");
 *   map.put("appversion", "1.0.0");
 *   map.put("clientid", "111");
 *   loadUrl(url, map);
 * }
 *
 *
 * 设置WebView缓存
 * 当加载 html 页面时，WebView会在/data/data/包名目录下生成 database 与 cache 两个文件夹
 * 请求的 URL记录保存在 WebViewCache.db，而 URL的内容是保存在 WebViewCache 文件夹下
 *
 * 缓存模式如下：
 * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据
 * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
 * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据
 * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
 *
 *
 *  清除缓存数据
 *  清除网页访问留下的缓存. 由于内核缓存是全局的因此这个方法不仅仅针对 WebView而是针对整个应用程序
 *  WebView.clearCache(true)
 *
 *  混淆
 *  -keepattributes *Annotation*
 *  -keepattributes *JavascriptInterface*
 *  -keep public class packageName.JavaScriptInterfaceName{
 *    public <methods>;
 *  }
 *
 *
 * 为什么会引起内存泄漏?
 * WebView会关联一个activity，而WebView内部执行的操作是在新的线程中，它的时间activity是没有办法确定的，
 * WebView会一直持有activity的引用，不能回收
 *
 *
 *  解决内存泄漏的2个方法
 *  1. 独立进程，简单暴力，不过可能设计到进程间的通信。
 *  2. 动态添加WebView，对传入WebView中使用的Context使用弱引用，
 *  动态添加WebView意思在布局创建ViewGroup用来放置WebView，
 *  Activity创建时add进来，在Activity停止时把WebView remove掉。再销毁WebView，最后把WebView设置为null
 *
 *  net::ERR_CLEARTEXT_NOT_PERMITTED
 *  从Android 9.0（API级别28）开始，默认情况下限制了明文流量的网络请求，对未加密流量不再信任，直接放弃请求，
 *  因此http的url均无法在webview中加载，https 不受影响
 *  注：在使用OkHttp进行请求时也会出现此错误
 *  解决：
 *  <manifest ...>
 *     <application
 *      ...
 *       android:usesCleartextTraffic="true"
 *      ...>
 *      ...
 *     </application>
 *  </manifest>
 *
 *
 *  //todo
 *  // JS选择手机上的文件
 */
class WebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewBinding
    private var _mWebView: WebView? = null
    private val mWebView get() = _mWebView!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView() {
        _mWebView = WebView(this)
        binding.root.addView(_mWebView)

        mWebView.addJavascriptInterface(JsInterfaceBridge(), "injectedObject")

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


        //处理各种通知 & 请求事件
        mWebView.webViewClient = object : WebViewClient() {

            //注：如果使用的是Post请求方式，则此方法不会被回调
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                Logs.logd("shouldOverrideUrlLoading ${request.url}")
                val uri = request.url
                val scheme = uri.scheme

                if (scheme.isNullOrEmpty()) {
                    return true
                }

                if (scheme == "toast") {  //如果项目中需要处理传过来的URL是一个事件还是一个HTTP链接，可以通过此方法过滤

                } else if (scheme == "http" || scheme == "https") {
                    //处理http协议
//                    return if (uri.host.equals("www.example.com")) { // 内部网址，不拦截，用自己的WebView加载,此次无需再调用WebView#loadUrl(String)
//                        false
//                    } else {
//                        //跳转外部浏览器
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                        true
//                    }
                    return false
                }

                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Logs.logd("onPageFinished $url")
            }

            //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                //设定加载资源的操作
            }

            //加载页面出错(如404)或者某个资源(图片等)出错时会调用
            // sdk 23以上可以用 onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Logs.logd("onReceivedError errorCode:$errorCode url:$failingUrl")
                // 该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
                when (errorCode) {
                    ERROR_BAD_URL -> {
                    }
                }
            }

            //webView默认是不处理https请求的，页面显示空白，需要进行如下设置
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError
            ) {
                Logs.logd("onReceivedSslError ${error.url}")
                handler.proceed() //表示等待证书响应
                // handler.cancel() //表示挂起连接，为默认方式
                // handler.handleMessage(null) //可做其他处理
            }

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
                AlertDialog.Builder(this@WebViewActivity)
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

                AlertDialog.Builder(this@WebViewActivity)
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

                val editText = EditText(this@WebViewActivity)
                AlertDialog.Builder(this@WebViewActivity)
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

//        mWebView.loadUrl("https://www.taobao.com")
//        mWebView.loadUrl("https://www.baidu.com")
        mWebView.loadUrl("file:///android_asset/JsJava.html")
//        mWebView.loadUrl("file:///android_asset/demo.html")

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        mWebView.run {
            onResume()      // 通知内核尝试停止所有处理，如动画和地理位置
            resumeTimers()  //若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
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

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _mWebView?.run {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            binding.root.removeView(this)
            destroy()
        }
        _mWebView = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && _mWebView?.canGoBack() == true) {
            _mWebView?.goBack()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

}