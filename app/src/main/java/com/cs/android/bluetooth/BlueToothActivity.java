package com.cs.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs.android.R;
import com.cs.android.bluetooth.adapter.BlueToothListAdapter;
import com.cs.common.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ChenSen
 * @desc
 * @since 2022/1/10 17:45
 **/
public class BlueToothActivity extends BaseActivity {
    public static final String TAG = BlueToothActivity.class.getSimpleName();

    private SwitchCompat mSwSwitch;
    private TextView mTvState;
    private TextView mTvLocalName;
    private TextView mTvLocalAddress;
    private TextView mTvConnectServer;
    private Button mBtScan;
    private Button mBtServer;
    private Button mBtClient;

    private RecyclerView mRvPairedDevices;
    private RecyclerView mRvDevices;
    private BlueToothListAdapter mPairedListAdapter;
    private BlueToothListAdapter mScanListAdapter;

    private final BluetoothReceiver mBlueToothReceiver = new BluetoothReceiver();
    private BluetoothAdapter mBlueToothAdapter;

    protected BluetoothDevice mConnectServer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBlueToothAdapter == null) {    //当 bluetoothAdapter 为空的时候说明应用不支持蓝牙
            toast("当前设备不支持蓝牙");
            return;
        }
        initView();


//        mBlueToothAdapter.getU


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBlueToothReceiver();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBlueToothReceiver);
        cancelDiscovering();
        super.onPause();
    }

    private void initView() {
        mSwSwitch = findViewById(R.id.swSwitch);
        mTvState = findViewById(R.id.tvState);
        mTvLocalName = findViewById(R.id.tvLocalName);
        mTvLocalAddress = findViewById(R.id.tvLocalAddress);
        mRvPairedDevices = findViewById(R.id.rvPairedDevices);
        mRvDevices = findViewById(R.id.rvDevices);
        mBtScan = findViewById(R.id.btScan);
        mTvConnectServer = findViewById(R.id.tvConnectServer);

        mTvLocalAddress.setText(mBlueToothAdapter.getAddress());
        mTvLocalName.setText(mBlueToothAdapter.getName());

        mSwSwitch.setChecked(mBlueToothAdapter.isEnabled());
        mSwSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                openBluetooth();   // 关闭蓝牙
            } else {
                closeBluetooth();   // 关闭蓝牙
            }
        });

        //已配对列表
        mPairedListAdapter = new BlueToothListAdapter(this);
        mRvPairedDevices.setAdapter(mPairedListAdapter);
        mRvPairedDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvPairedDevices.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //查询已配对设备
        Set<BluetoothDevice> pairedDevicesSet = mBlueToothAdapter.getBondedDevices();
        List<BluetoothDevice> pairedDevices = new ArrayList<>(pairedDevicesSet.size());
        pairedDevices.addAll(pairedDevicesSet);
        mPairedListAdapter.addAll(pairedDevices);

        //扫描列表
        mScanListAdapter = new BlueToothListAdapter(this);
        mRvDevices.setAdapter(mScanListAdapter);
        mRvDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvDevices.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //开启服务端
        findViewById(R.id.btnServer).setOnClickListener(v -> {
            startActivity(new Intent(this, BlueToothServerActivity.class));
        });

        //开启客户端
        findViewById(R.id.btnClient).setOnClickListener(v -> {
            if (mConnectServer != null) {
                Intent intent = new Intent(this, BlueToothClientActivity.class);
                intent.putExtra("device", mConnectServer);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请选则要连接的设备", Toast.LENGTH_SHORT).show();
            }
        });


        //开始扫描
        mBtScan.setOnClickListener(v -> {
            startScan();
        });

//        findViewById(R.id.btnSend).setOnClickListener(v -> {
//
//            File file = new File("/sdcard/test.txt");
//            Uri uri = FileProvider.getUriForFile(BlueToothActivity.this, "com.cs.android.FileProvider", file);
//
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("*/*");
//            intent.setPackage("com.android.bluetooth");
//            intent.putExtra(Intent.EXTRA_STREAM, uri);//path为文件的路径
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            Intent chooser = Intent.createChooser(intent, "Share app");
//            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(Intent.createChooser(chooser, "分享"));
//        });

        updateBlueToothState(mBlueToothAdapter.getState());
    }


    private void registerBlueToothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);               // 发现新设备(未配对的设备)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);  // 扫描开始
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 扫描结束
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);      // 监听蓝牙状态改变，例如打开关闭
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);  // 拉取远程设备UUID

//        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
//        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);

        registerReceiver(mBlueToothReceiver, intentFilter);
    }

    private void updateBlueToothState(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_ON:
                mTvState.setText("正在打开蓝牙");
                break;
            case BluetoothAdapter.STATE_ON:
                mTvState.setText("已打开");
                mSwSwitch.setChecked(true);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                mTvState.setText("正在关闭蓝牙");
                break;
            case BluetoothAdapter.STATE_OFF:
                mTvState.setText("已关闭");
                mSwSwitch.setChecked(false);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 打开蓝牙
     * 开启蓝牙有2种方式
     */
    private void openBluetooth() {
        if (!mBlueToothAdapter.enable()) {
//            // 方式一
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 0);

            // 方式二
            boolean result = mBlueToothAdapter.enable();
            if (!result) {
                Toast.makeText(this, "开启蓝牙失败!", Toast.LENGTH_SHORT).show();
            }

            //查询已配对设备
            Set<BluetoothDevice> pairedDevicesSet = mBlueToothAdapter.getBondedDevices();
            List<BluetoothDevice> pairedDevices = new ArrayList<>(pairedDevicesSet.size());
            pairedDevices.addAll(pairedDevicesSet);
            mPairedListAdapter.addAll(pairedDevices);
        }
    }

    /**
     * 关闭蓝牙
     */
    private void closeBluetooth() {
        boolean result = mBlueToothAdapter.disable();
        if (!result) {
            Toast.makeText(this, "关闭蓝牙失败!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 开始扫描
     */
    public void startScan() {
        if (mBlueToothAdapter.isEnabled()) {
            if (!mBlueToothAdapter.isDiscovering()) {
                mScanListAdapter.clear();
                mBlueToothAdapter.startDiscovery();
            }
        } else {
            toast("蓝牙开关未打开");
        }
    }

    /**
     * 判断是否正在搜索
     *
     * @return
     */
    public boolean isDiscovering() {
        return mBlueToothAdapter != null && mBlueToothAdapter.isDiscovering();
    }

    /**
     * 取消搜索
     *
     * @return
     */
    public void cancelDiscovering() {
        if (mBlueToothAdapter != null) {
            mBlueToothAdapter.cancelDiscovery();
        }
    }


    public void connect(BluetoothDevice device) {

    }


    public void setConnectServer(BluetoothDevice device) {
        this.mConnectServer = device;
        mTvConnectServer.setText("需要连接的设备：" + device.getName() + "(" + device.getAddress() + ")");
    }

    class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ParcelUuid[] uuids = device.getUuids();
                    Log.d(TAG, "发现设备 : " + device.getName() + ", 缓存支持的UUID ：" + uuids.toString());
                    boolean b = device.fetchUuidsWithSdp();
                    Log.d(TAG, "远程设备支持拉取UUID : " + b);

                    mScanListAdapter.add(device);
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(BlueToothActivity.this, "蓝牙扫描开始", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "蓝牙扫描开始");
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(BlueToothActivity.this, "蓝牙扫描结束", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "蓝牙扫描结束");
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    Log.d(TAG, "蓝牙状态改变 state :" + state);
                    updateBlueToothState(state);
                    break;

                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    Toast.makeText(BlueToothActivity.this, "蓝牙连接状态改变", Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothDevice.ACTION_UUID:
                    BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ParcelUuid remoteUuid = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
                    Log.d(TAG, "拉取远程设备UUID : " + remoteDevice.getName() + ", remoteUuid : " + remoteUuid.getUuid());
                    Toast.makeText(BlueToothActivity.this, "拉取远程设备UUID", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
