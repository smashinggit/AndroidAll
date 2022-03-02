package com.cs.android.usb;

import android.util.Log;

import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.IOException;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/20 16:56
 **/
public class UsbReader {
    public static final String TAG = UDiskActivity.class.getSimpleName();


    private UsbFile[] readFileList(FileSystem fileSystem) {

        UsbFile rootDirectory = fileSystem.getRootDirectory();
        try {
            UsbFile[] usbFiles = rootDirectory.listFiles();

            for (UsbFile file : usbFiles) {
                Log.d(TAG, "UsbFile-> FileName : " + file.getName() + ", FileSize ：" + file.getLength());
            }

            return usbFiles;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
