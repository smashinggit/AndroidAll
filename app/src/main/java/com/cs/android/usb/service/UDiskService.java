package com.cs.android.usb.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

/**
 * @author ChenSen
 * @desc
 * @since 2022/1/18 17:55
 **/
public class UDiskService extends Service {
    private StorageManager mStorageManager;
    private String mUDiskPath;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        mStorageManager = (StorageManager) getSystemService(STORAGE_SERVICE);


        for (StorageVolume storageVolume : mStorageManager.getStorageVolumes()) {
            String label = storageVolume.getDescription(this); //这个其实就是U盘的名称
            String status = storageVolume.getState(); //设备挂载的状态，如:mounted、unmounted
            boolean isEmulated = storageVolume.isEmulated(); //是否是内部存储设备
            boolean isRemovable = storageVolume.isRemovable(); //是否是可移除的外部存储设备
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void externalSDCardPath() {
        try {
            Class volumeClass = Class.forName("android.os.storage.StorageVolume");
            Method getPath = volumeClass.getDeclaredMethod("getPath");
            Method isRemovable = volumeClass.getDeclaredMethod("isRemovable");
            getPath.setAccessible(true);
            isRemovable.setAccessible(true);

            StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);

            for (StorageVolume storageVolume : storageManager.getStorageVolumes()) {
//                String label = storageVolume.getDescription(this); //这个其实就是U盘的名称
//                String status = storageVolume.getState(); //设备挂载的状态，如:mounted、unmounted
//                boolean isEmulated = storageVolume.isEmulated(); //是否是内部存储设备
//                boolean isRemovable = storageVolume.isRemovable(); //是否是可移除的外部存储设备

                String path = (String) getPath.invoke(storageVolume);
                Boolean isRemove = (Boolean) isRemovable.invoke(storageVolume);
                if (isRemove) {
                    mUDiskPath = path;
                    Log.d("tag", "找到U盘路径: " + path);
                }
                Log.d("tag", "paht " + path + ",isRemove " + isRemove);
            }
        } catch (Exception e) {
            Log.i("tag2", "e == " + e.getMessage());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
