package com.cs.android.usb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs.android.R;
import com.cs.android.usb.bean.CommonFile;
import com.cs.android.usb.widget.DividerItemDecoration;
import com.cs.common.base.BaseActivity;
import com.github.mjdev.libaums.fs.UsbFile;

import java.util.List;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/20 9:40
 * <p>
 * 权限相关
 * https://www.jianshu.com/p/e94cea26e213
 **/
public class UDiskActivity extends BaseActivity {
    public static final String TAG = UDiskActivity.class.getSimpleName();

    // android 11.0  授予文件管理权限
    ActivityResultLauncher<Intent> mFileManagementLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onActivityResult(ActivityResult result) {
            if (Environment.isExternalStorageManager()) {   // 获得文件管理权限
                readLocalFiles();
            } else {
                Toast.makeText(UDiskActivity.this, "未获得权限，功能无法实现", Toast.LENGTH_SHORT).show();
            }
        }
    });

    // android 6.0~10.0，用于申请权限
    private ActivityResultLauncher<String> mRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(UDiskActivity.this, "已获得权限", Toast.LENGTH_SHORT).show();
                readLocalFiles();
            } else {
                Toast.makeText(UDiskActivity.this, "未获得权限，去设置", Toast.LENGTH_SHORT).show();
                showOpenPermissionDialog();
            }
        }
    });

    // android 6.0~10.0，用于跳转到权限设置页面
    private ActivityResultLauncher<Intent> mOpenPermissionPageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (ActivityCompat.checkSelfPermission(UDiskActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                readLocalFiles();

            } else { // 未获得权限
                Toast.makeText(UDiskActivity.this, "未获得权限，功能无法实现", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private UDiskReceiver mUDiskReceiver = new UDiskReceiver();

    private Button mBtnBack;
    private Button mBtnBluetoothShare;
    private TextView mTvCapacity;
    private TextView mTvLocalPath;
    private TextView mTvUsbPath;
    private CheckBox mCbLocalCheckAll;
    private CheckBox mCbUSBCheckAll;
    private Button mBtnRefresh;
    private Button mBtnCopyIn;
    private Button mBtnCopyOut;
    private Button mBtnDelete;
    private Button mBtnCancel;
    private ConstraintLayout mClUSB;
    private ConstraintLayout mClNoUSB;

    private RecyclerView mRvLocalFileList;
    private RecyclerView mRvUSBFileList;
    private FileListAdapter mLocalFileAdapter;
    private FileListAdapter mUSBFileAdapter;

    private FilePath mLocalFilePath;
    private FilePath mUSBFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udisk);

        float xdpi = ScreenUtils.screenXDpi(this);
        float ydpi = ScreenUtils.screenYDpi(this);
        Log.d(TAG, "xdpi : " + xdpi + ", ydpi:" + ydpi);

        mLocalFilePath = new FilePath();
        mUSBFilePath = new FilePath();

        initView();
        checkPermission();
        registerUSBReceiver();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        mBtnBack = findViewById(R.id.btnBack);
        mBtnBluetoothShare = findViewById(R.id.btnBluetoothShare);
        mTvCapacity = findViewById(R.id.tvCapacity);
        mTvLocalPath = findViewById(R.id.tvLocalPath);
        mTvUsbPath = findViewById(R.id.tvUsbPath);
        mCbLocalCheckAll = findViewById(R.id.cbLocalCheckAll);
        mCbUSBCheckAll = findViewById(R.id.cbUSBCheckAll);
        Button btnLocalFileBack = findViewById(R.id.btnLocalFileBack);
        mBtnRefresh = findViewById(R.id.btnRefresh);
        mBtnCopyIn = findViewById(R.id.btnCopyIn);
        mBtnCopyOut = findViewById(R.id.btnCopyOut);
        mBtnDelete = findViewById(R.id.btnDelete);
        mBtnCancel = findViewById(R.id.btnCancel);
        mClUSB = findViewById(R.id.clUSB);
        mClNoUSB = findViewById(R.id.clNoUSB);
        mRvLocalFileList = findViewById(R.id.rvLocalFile);
        mRvUSBFileList = findViewById(R.id.rvUSBFile);


        mLocalFileAdapter = new FileListAdapter(this);
        mRvLocalFileList.setAdapter(mLocalFileAdapter);
        mRvLocalFileList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvLocalFileList.addItemDecoration(new DividerItemDecoration(getResources().getColor(R.color.public_divider),
                ScreenUtils.dp2px(this, 2),
                ScreenUtils.dp2px(this, 41),
                ScreenUtils.dp2px(this, 30)));

        mUSBFileAdapter = new FileListAdapter(this);
        mRvUSBFileList.setAdapter(mUSBFileAdapter);
        mRvUSBFileList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvUSBFileList.addItemDecoration(new DividerItemDecoration(getResources().getColor(R.color.public_divider),
                ScreenUtils.dp2px(this, 2),
                ScreenUtils.dp2px(this, 41),
                ScreenUtils.dp2px(this, 30)));


        mBtnBack.setOnClickListener(v -> finish());

        TranslationInfo info = new TranslationInfo("urI", TranslationInfo.STATE.TRANSLATING, "吴彦祖", "哥斯拉.mp3", ".exe", "15", 50);
        mBtnBluetoothShare.setOnClickListener(v -> {
            new FileManagementDialog(this, info, "确定", dialog -> {
                dialog.dismiss();
            }, "取消", dialog -> {
                dialog.dismiss();
            }).show();
        });

        mCbLocalCheckAll.setOnClickListener(v -> {
            if (mCbLocalCheckAll.isChecked()) {
                FileManager.addChecked(mLocalFilePath.peek());
            } else {
                FileManager.removeChecked(mLocalFilePath.peek());
            }
            mLocalFileAdapter.notifyDataSetChanged();
        });

        FileManager.setOnCheckedChangeCallBack((currentDir, isAllChecked) -> {
            Log.d(TAG, "setOnCheckedChangeCallBack currentDir:" + currentDir.getPath() + ", isAllChecked:" + isAllChecked);

            if (currentDir.getPath().equals(mLocalFilePath.peek().getPath())) {
                mCbLocalCheckAll.setChecked(isAllChecked);
            }
        });

        mCbUSBCheckAll.setOnCheckedChangeListener((buttonView, isChecked) -> {

        });

        btnLocalFileBack.setOnClickListener(v -> {
            backToParent(mLocalFilePath.peek());
        });

        mBtnRefresh.setOnClickListener(v -> {
            refresh(mLocalFilePath.peek());
        });
        mBtnCopyIn.setOnClickListener(v -> {
        });
        mBtnCopyOut.setOnClickListener(v -> {
        });
        mBtnDelete.setOnClickListener(v -> {
        });
        mBtnCancel.setOnClickListener(v -> {
        });
    }

    /**
     * 读取本地文件列表
     */
    private void readLocalFiles() {
        mLocalFilePath.clear();
        CommonFile localRootFile = FileManager.getLocalRootFile();
        enterToChild(localRootFile);
    }

    /**
     * 进入子目录
     *
     * @param file
     * @return 返回子目录是否被全选中
     */
    public void enterToChild(CommonFile file) {
        if (file == null)
            return;

        Log.d(TAG, "enterToChild : " + file.getPath());

        List<CommonFile> childrenFiles = FileManager.readFileList(file);
        if (childrenFiles != null) {
            mLocalFileAdapter.setData(childrenFiles);

            mLocalFilePath.push(file);
            mTvLocalPath.setText(mLocalFilePath.getDisplayPath());
        }

        // 更新全选按钮
        if (file.isChecked()) {
            mCbLocalCheckAll.setChecked(true);
        } else {
            mCbLocalCheckAll.setChecked(false);
        }
    }

    /**
     * 返回给定文件的上级目录
     *
     * @param
     */
    public void backToParent(CommonFile file) {
        if (file == null || file.getParent() == null)
            return;

        Log.d(TAG, "backToParent parent : " + file.getPath());
        List<CommonFile> parentFileList = FileManager.readFileList(file.getParent());

        if (parentFileList != null) {
            mLocalFileAdapter.setData(parentFileList);
            mLocalFilePath.pop();
            mTvLocalPath.setText(mLocalFilePath.getDisplayPath());
        }

        // 更新全选按钮
        if (file.getParent().isChecked()) {
            mCbLocalCheckAll.setChecked(true);
        } else {
            mCbLocalCheckAll.setChecked(false);
        }
    }



    /**
     * 刷新指定目录
     *
     * @param file
     */
    public void refresh(CommonFile file) {
        Log.d(TAG, "refresh : " + file.getPath());
        List<CommonFile> list = FileManager.readFileList(file);
        if (list != null) {
            mLocalFileAdapter.setData(list);
        }
    }


    /**
     *
     */
    private void readUSBFiles() {
        List<UsbFile> usbFiles = FileManager.readUDiskFiles();
//        mUSBFileAdapter.setData(usbFiles);
    }


    /**
     * 存储权限，
     * 在 Android 6.0 之后就变成了危险权限，
     * 在 Android 11 上面变成了特殊权限
     * 最明显的区别是一个是通过 Dialog 展示给用户看，另外一个是通过 Activity 展现给用户看。
     * <p>
     * 如果 targetSdkVersion >= 29 上，还需要在清单文件中加上
     * <application android:requestLegacyExternalStorage="true">
     * 否则就算申请了存储权限，在安卓 10.0 的设备上将无法正常读写外部存储上的文件
     */
    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {       // 适配 android 11 读写权限

            if (Environment.isExternalStorageManager()) {           // 判断有没有权限
                readLocalFiles();
            } else {
                showFileManagementDialog();
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // 6.0~10.0

            // 判断权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                readLocalFiles();
            } else { // 请求权限
                mRequestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }


    /**
     * 6.0 ~ 10.0  提示用户打开权限设置页面
     */
    private void showOpenPermissionDialog() {
        new FileManagementDialog(this, "请打开存储权限", "确定", dialog -> {
            dialog.dismiss();

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            mOpenPermissionPageLauncher.launch(intent);

        }, "取消", dialog -> {
            dialog.dismiss();
            Toast.makeText(UDiskActivity.this, "未获得权限，功能无法实现", Toast.LENGTH_SHORT).show();
        }).show();
    }

    /**
     * 11.0  提示用户授予所有文件的管理权限
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void showFileManagementDialog() {
        new FileManagementDialog(this, "请打开存储权限", "确定", dialog -> {
            dialog.dismiss();

            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            mFileManagementLauncher.launch(intent);

        }, "取消", dialog -> {
            dialog.dismiss();
            Toast.makeText(UDiskActivity.this, "未获得权限，功能无法实现", Toast.LENGTH_SHORT).show();
        }).show();
    }

    private void registerUSBReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUDiskReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUDiskReceiver);
        FileManager.removeAllSelected();
        super.onDestroy();
    }

    private void showUsb() {
        mClUSB.setVisibility(View.VISIBLE);
        mClNoUSB.setVisibility(View.GONE);
    }

    private void showNoUsb() {
        mClUSB.setVisibility(View.GONE);
        mClNoUSB.setVisibility(View.VISIBLE);
    }


    class UDiskReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Toast.makeText(UDiskActivity.this, "U盘已插入", Toast.LENGTH_SHORT).show();
                    showUsb();
                    readUSBFiles();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Toast.makeText(UDiskActivity.this, "U盘已拔出", Toast.LENGTH_SHORT).show();
                    showNoUsb();
                    break;

                case FileManager.ACTION_USB_PERMISSION:
                    Toast.makeText(UDiskActivity.this, "申请权限返回", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
