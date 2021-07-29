package com.cs.android.test

import android.os.AsyncTask

/**
 * @author ChenSen
 * @since 2021/7/26 10:38
 * @desc
 */
object AsyncTaskTest {

}

class MyAsyncTask : AsyncTask<String, Int, String>() {

    override fun doInBackground(vararg params: String): String {
        (0 until 10).forEach {
            publishProgress(it * 10)
        }
        return "complete"
    }

    //进度更新
    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        values?.forEach {
            println("progress $it")
        }
    }

    //执行结果
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        println("result : $result")
    }

}