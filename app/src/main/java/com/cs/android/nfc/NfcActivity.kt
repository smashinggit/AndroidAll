package com.cs.android.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import com.cs.android.R
import com.cs.common.base.BaseActivity
import com.cs.common.utils.log
import com.cs.common.utils.toHexString
import com.cs.common.utils.toast

/**
 * @author ChenSen
 * @since 2021/11/30 10:44
 * @desc  前台调度
 *
 **/
class NfcActivity : BaseActivity() {

    private val mAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        "onCreate".log()


        if (mAdapter == null) {
            toast("该设备不支持NFC！")
        } else {
            if (!mAdapter!!.isEnabled) {
                toast("请打开NFC开关")
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            } else {
                toast("NFC已打开")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        mAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
        "onResume".log()
    }

    override fun onPause() {
        super.onPause()
        mAdapter?.disableForegroundDispatch(this)
        "onPause".log()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        "onNewIntent".log()
        handleNfcIntent(intent)
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
    }

    /**
     * 处理NFC标签
     */
    private fun handleNfcIntent(intent: Intent) {
        "${intent.action}".log()
        when (intent.action) {
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                val tag = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_DATA) as Tag
                processTag(tag)
            }

            NfcAdapter.ACTION_TECH_DISCOVERED -> {

            }

            NfcAdapter.ACTION_TAG_DISCOVERED -> {

            }
        }
    }

    /**
     *
     */
    private fun processTag(tag: Tag) {
        //卡片的uuid
        val uuid = tag.id.toHexString()
        "uuid: $uuid".log()


        //打印被读取卡片支持的Tag列表：
        "techList: ${tag.techList}".log()


    }

}