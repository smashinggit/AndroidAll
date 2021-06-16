package com.cs.android.webview

import android.webkit.JavascriptInterface
import com.cs.common.utils.Logs

/**
 * @author ChenSen
 * @since 2021/6/10 22:06
 * @desc 给JavaScript 提供调用Native功能的接口
 */
class JsInterfaceBridge {

    @JavascriptInterface
    fun showLoading(msg: String = "") {

    }

    @JavascriptInterface
    fun hideLoading() {

    }

    @JavascriptInterface
    fun jsCalJava() {
        Logs.logd("js 调用 java 中的 jsCallJava()")
    }


    @JavascriptInterface
    fun jsCalJava(key: String) {
        Logs.logd("js 调用 java 中的 jsCallJava(String key) key = $key")
    }

}