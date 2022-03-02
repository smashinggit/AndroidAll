package com.cs.android.usb;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cs.android.R;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/21 16:24
 **/
public class FileManagementDialog extends Dialog {

    private TYPE mType;

    private String mMessage;
    private String mConfirmText = "确定";
    private String mCancelText = "取消";

    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;

    private TranslationInfo mTranslationInfo;
    private ProgressBar mProgressBar;


    /**
     * Alert
     *
     * @param context
     * @param message
     * @param positiveText
     * @param onClick
     */
    public FileManagementDialog(@NonNull Context context, String message, String positiveText, OnClickListener onClick) {
        super(context);
        mType = TYPE.ALERT;
        this.mMessage = message;
        this.mConfirmText = positiveText;
        this.mPositiveListener = onClick;
    }


    /**
     * Query
     *
     * @param context
     * @param message
     * @param positiveText
     * @param positiveListener
     * @param negativeText
     * @param negativeListener
     */
    public FileManagementDialog(@NonNull Context context, String message,
                                String positiveText, OnClickListener positiveListener,
                                String negativeText, OnClickListener negativeListener) {
        super(context);
        mType = TYPE.QUERY;

        this.mMessage = message;
        this.mConfirmText = positiveText;
        this.mCancelText = negativeText;

        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
    }

    /**
     * Display
     *
     * @param context
     * @param translationInfo
     * @param positiveText
     * @param positiveListener
     * @param negativeText
     * @param negativeListener
     */
    public FileManagementDialog(@NonNull Context context, TranslationInfo translationInfo,
                                String positiveText, OnClickListener positiveListener,
                                String negativeText, OnClickListener negativeListener) {
        super(context);
        this.mType = TYPE.DISPLAY;
        this.mTranslationInfo = translationInfo;

        this.mConfirmText = positiveText;
        this.mCancelText = negativeText;

        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (mType) {
            case ALERT:
                setContentView(R.layout.filemanagement_item_dialog_alert);
                TextView tvAlertMessage = findViewById(R.id.tvMessage);
                Button btnAlertConfirm = findViewById(R.id.btnConfirm);

                tvAlertMessage.setText(mMessage);
                btnAlertConfirm.setText(mConfirmText);
                btnAlertConfirm.setOnClickListener(v -> mPositiveListener.onClick(FileManagementDialog.this));

                break;
            case QUERY:
                setContentView(R.layout.filemanagement_item_dialog_query);
                TextView tvQueryMessage = findViewById(R.id.tvMessage);
                Button btnQueryConfirm = findViewById(R.id.btnConfirm);
                Button btnQueryCancel = findViewById(R.id.btnCancel);

                tvQueryMessage.setText(mMessage);
                btnQueryConfirm.setText(mConfirmText);
                btnQueryCancel.setText(mCancelText);

                btnQueryConfirm.setOnClickListener(v -> mPositiveListener.onClick(FileManagementDialog.this));
                btnQueryCancel.setOnClickListener(v -> mNegativeListener.onClick(FileManagementDialog.this));

                break;
            case DISPLAY:
                setContentView(R.layout.filemanagement_item_dialog_display);
                TextView tvReceiver = findViewById(R.id.tvReceiver);
                TextView tvFileName = findViewById(R.id.tvFileName);
                TextView tvFileType = findViewById(R.id.tvFileType);
                TextView tvMessage = findViewById(R.id.tvMessage);
                mProgressBar = findViewById(R.id.pbProgress);


                Button btnDisplayConfirm = findViewById(R.id.btnConfirm);
                Button btnDisplayCancel = findViewById(R.id.btnCancel);

                btnDisplayConfirm.setOnClickListener(v -> mPositiveListener.onClick(FileManagementDialog.this));
                btnDisplayCancel.setOnClickListener(v -> mNegativeListener.onClick(FileManagementDialog.this));

                setFileInfo(mTranslationInfo);
                break;
        }

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);
    }


    @SuppressLint("SetTextI18n")
    public void setFileInfo(TranslationInfo translationInfo) {

        if (mType == TYPE.DISPLAY) {
            TextView tvReceiver = findViewById(R.id.tvReceiver);
            TextView tvFileName = findViewById(R.id.tvFileName);
            TextView tvFileType = findViewById(R.id.tvFileType);
            TextView tvMessage = findViewById(R.id.tvMessage);

            tvReceiver.setText("收件人：" + translationInfo.getReceiver());
            tvFileName.setText("文件：" + translationInfo.getName());
            tvFileType.setText("文件类型：" + translationInfo.getType() + "（" + translationInfo.getSize() + "）");

            switch (translationInfo.getState()) {
                case IDLE:
                    break;
                case WAIT_CONFIRM:
                    tvMessage.setText(R.string.filemanagement_wait_confirm);
                    break;
                case TRANSLATING:
                    tvMessage.setText(String.format(getContext().getResources().getString(R.string.filemanagement_translating), translationInfo.getProgress()));
                    mProgressBar.setProgress(translationInfo.getProgress());
                    break;
            }
        }
    }

    public void updateProgress(int progress) {
        if (mType == TYPE.DISPLAY) {
            mProgressBar.setProgress(progress);
        }
    }


    private enum TYPE {
        ALERT, QUERY, DISPLAY
    }


    interface OnClickListener {
        void onClick(Dialog dialog);
    }
}
