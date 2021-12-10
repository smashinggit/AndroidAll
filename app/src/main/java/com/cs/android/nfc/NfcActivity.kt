package com.cs.android.nfc

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.os.Bundle
import android.provider.Settings
import com.cs.android.databinding.ActivityNfcBinding
import com.cs.android.nfc.reader.CardReader
import com.cs.common.base.BaseActivity
import com.cs.common.utils.log
import com.cs.common.utils.toast

/**
 * @author ChenSen
 * @since 2021/11/30 10:44
 * @desc
 *
 * NFC类型虽多，常见的NfcA、NfcB、IsoDep三个系出ISO14443标准（即RFID卡标准），它们仨各自用于生活中的几种场合，说明如下：
 * 1、NfcA遵循ISO14443-3A标准，常用于门禁卡；
 * 2、NfcB遵循ISO14443-3B标准，常用于二代身份证；
 * 3、IsoDep遵循ISO14443-4标准，常用于公交卡；

 *
 **/
@SuppressLint("UnspecifiedImmutableFlag")
class NfcActivity : BaseActivity() {

    private val mBinding: ActivityNfcBinding by lazy {
        ActivityNfcBinding.inflate(layoutInflater)
    }

    private val mAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    // 探测到NFC卡片后，必须以FLAG_ACTIVITY_SINGLE_TOP方式启动Activity，
    // 或者在AndroidManifest.xml中设置launchMode属性为singleTop或者singleTask，
    // 保证无论NFC标签靠近手机多少次，Activity实例都只有一个。
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val mFilters = arrayOf(
        IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
        IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*"),
        IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
    )

    private val mTechList =
        arrayOf(
            arrayOf(
                NfcA::class.java.name,
                NfcB::class.java.name,
                IsoDep::class.java.name,
                MifareClassic::class.java.name
            )
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        "onCreate".log()

        initNFC()
    }

    private fun initNFC() {
        if (mAdapter == null) {
            toast("该设备不支持NFC！")
            mBinding.tvState.text = "该设备不支持NFC！"
        } else {
            if (!mAdapter!!.isEnabled) {
                showNFCNSettingDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (getNfcState()) {
            mAdapter?.enableForegroundDispatch(this, pendingIntent, mFilters, mTechList)
            mBinding.swNfc.isChecked = true
            mBinding.tvState.text = "请将卡片至于手机背部NFC识别区！"
        } else {
            mBinding.swNfc.isChecked = false
            mBinding.tvState.text = "请前往设置页面打开NFC开关！"
        }

        "onResume".log()
    }

    private fun getNfcState(): Boolean {
        return mAdapter != null && mAdapter!!.isEnabled
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
    }

    /**
     * 处理 NFC 标签
     */
    @SuppressLint("SetTextI18n")
    private fun handleNfcIntent(intent: Intent) {
        "ACTION: ${intent.action}".log()

        when (intent.action) {
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                mBinding.tvAction.text = "ACTION_NDEF_DISCOVERED"
            }

            NfcAdapter.ACTION_TECH_DISCOVERED -> {
                mBinding.tvAction.text = "ACTION_TECH_DISCOVERED"
            }

            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                mBinding.tvAction.text = "ACTION_TAG_DISCOVERED"
            }
        }

        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.also {
            val id = CardReader.getId(it)
            "id: $id".log()

            val techList = CardReader.getTechList(it)
            val techBuffer = StringBuffer()
            techList.forEach { tech ->
                techBuffer.append("$tech \n")
                "techList: $tech".log()
            }

            val readResult = CardReader.readTag(it)

            mBinding.tvSerial.text = id
            mBinding.tvSupportTech.text = techBuffer.toString()
            mBinding.tvReadTech.text = "解析用的类在Logcat中输出"
            mBinding.tvReadResult.text = readResult
        }
    }

    private fun showNFCNSettingDialog() {
        AlertDialog.Builder(this)
            .setTitle("NFC开关未开启")
            .setMessage("请前往设置页面打开NFC开关")
            .setPositiveButton("去设置") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                dialog.dismiss()
            }
            .setPositiveButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}