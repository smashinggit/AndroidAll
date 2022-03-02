//package com.cs.android.usb;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbManager;
//import android.widget.Toast;
//
///**
// * @author ChenSen
// * @desc
// * @since 2021/12/22 14:16
// **/
//class UDiskReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        switch (intent.getAction()) {
//            case UsbManager.ACTION_USB_DEVICE_ATTACHED:
//                Toast.makeText(UDiskActivity.this, "U盘已插入", Toast.LENGTH_SHORT).show();
//                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if (device != null) {
//                    readUDiskDevsList();
//                } else {
//                    Toast.makeText(UDiskActivity.this, "U盘不可用", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case UsbManager.ACTION_USB_DEVICE_DETACHED:
//                Toast.makeText(UDiskActivity.this, "U盘已拔出", Toast.LENGTH_SHORT).show();
//                break;
//
//            case ACTION_USB_PERMISSION:
//                Toast.makeText(UDiskActivity.this, "申请权限返回", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
//}