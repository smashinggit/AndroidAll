package com.cs.common.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs.common.utils.PermissionHelper
import com.cs.common.utils.log

/**
 * @Desc
 * @Author ChenSen
 * @Date 2020/5/6-21:01
 *
 */
open class BaseActivity() : AppCompatActivity() {

    protected fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}