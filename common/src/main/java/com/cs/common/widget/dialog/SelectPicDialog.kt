package com.cs.common.widget.dialog

import android.content.Context
import android.os.Bundle
import com.cs.common.R
import kotlinx.android.synthetic.main.select_pic.*

class SelectPicDialog(
    context: Context,
    private val onCamera: () -> Unit,
    private val onAlbum: () -> Unit,
    private val onCancel: (() -> Unit)? = null
) : BottomPopDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_pic)


        tvAlbum.setOnClickListener {
            dismiss()
            onAlbum()
        }

        tvCamera.setOnClickListener {
            dismiss()
            onCamera()
        }

        tvCancel.setOnClickListener {
            dismiss()
            onCancel?.invoke()
        }
    }

}