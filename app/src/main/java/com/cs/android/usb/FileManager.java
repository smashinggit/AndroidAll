package com.cs.android.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.cs.android.App;
import com.cs.android.usb.bean.CommonFile;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/20 16:40
 * <p>
 * 本地文件读取路径
 * 音频：/sdcard/media_sd/audio
 * 视频：/sdcard/media_sd/video
 * 图片：/sdcard/media_sd/picture
 * 广告：/sdcard/media_adv
 * 安全宣传片：/mnt/sdcard/media_mnt
 **/
public class FileManager {
    public static final String TAG = FileManager.class.getSimpleName();
    public static final String ACTION_USB_PERMISSION = "com.android.cs.USB_PERMISSION";
    public static final String LOCAL_ROOT_PATH = "/storage/emulated/0";
    public static final String AUDIO_PATH = "/storage/emulated/0/media_sd/audio";
    public static final String VIDEO_PATH = "/storage/emulated/0/media_sd/video";
    public static final String PICTURE_PATH = "/storage/emulated/0/media_sd/picture";
    public static final String AD_PATH = "/storage/emulated/0/media_adv";
    public static final String SECURE_PATH = "/storage/emulated/0/media_mnt";

    private static final CommonFile rootFile = getLocalRootFile();               //根目录
    private static final List<CommonFile> rootFileList = getLocalRootFileList(); //根目录下的文件列表
    private static final Set<String> selectedFiles = new HashSet<>();            //保存选中的文件路径
    private static OnCheckedChangeListener listener = null;

    /**
     * 获取本地根目录文件
     * 注意：本地根目录并不是真实存在的，而是是由几个不同的路径的文件组合而成
     *
     * @return
     */
    public static CommonFile getLocalRootFile() {
        CommonFile root = new CommonFile("本地", "本地",
                "本地", "本地", null, true, CommonFile.TYPE.RAW_FILE);
        root.setLocalRoot(true);
        return root;
    }

    /**
     * 本地根目录的文件列表
     *
     * @return
     */
    private static List<CommonFile> getLocalRootFileList() {

        CommonFile audio = new CommonFile(new File(FileManager.AUDIO_PATH));
        CommonFile video = new CommonFile(new File(FileManager.VIDEO_PATH));
        CommonFile picture = new CommonFile(new File(FileManager.PICTURE_PATH));
        CommonFile ad = new CommonFile(new File(FileManager.AD_PATH));
        CommonFile secure = new CommonFile(new File(FileManager.SECURE_PATH));

        audio.setParent(rootFile);
        video.setParent(rootFile);
        picture.setParent(rootFile);
        ad.setParent(rootFile);
        secure.setParent(rootFile);

        List<CommonFile> list = new ArrayList<>(5);
        list.add(audio);
        list.add(video);
        list.add(picture);
        list.add(ad);
        list.add(secure);

        return list;
    }

    /**
     * 读取指定目录的文件列表
     *
     * @param file
     */
    public static List<CommonFile> readFileList(CommonFile file) {
        if (file == null) {
            return null;
        }

        if (file.isLocalRoot()) {                       //本地文件根目录
            Log.d(TAG, "readFileList 读取根目录的列表 " + rootFileList.toString());
            return rootFileList;
        } else if (file.isDirectory()) {

            switch (file.getType()) {
                case RAW_FILE:                          //本地文件

                    File[] childFiles = new File(file.getPath()).listFiles();
                    if (childFiles != null) {
                        List<CommonFile> result = new ArrayList<>(childFiles.length);
                        for (File child : childFiles) {
                            CommonFile commonFile = new CommonFile(child);
                            commonFile.setParent(file);
                            result.add(commonFile);
                        }
                        Log.d(TAG, "readFileList 读取" + file.getPath() + " 的列表: " + result.toString());
                        return result;
                    }

                case USB_FILE:                          //USB文件
                    break;

                default:
                    return null;
            }
        }
        return null;
    }


    /**
     * 读取U盘文件
     *
     * @return
     */
    public static List<UsbFile> readUDiskFiles() {
        List<UsbFile> fileList = new ArrayList<>();

        // 设备管理器
        UsbManager usbManager = (UsbManager) App.Companion.getInstance().getSystemService(Context.USB_SERVICE);

        // 获取U盘存储设备
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(App.Companion.getInstance());

        if (storageDevices.length > 0) {
            //目前只支持插入一个U盘
            UsbMassStorageDevice device = storageDevices[0];

            if (usbManager.hasPermission(device.getUsbDevice())) { //读取设备是否有权限
                try {
                    device.init();
                    FileSystem currentFs = device.getPartitions().get(0).getFileSystem();

                    Log.d(TAG, "Capacity: " + currentFs.getCapacity());
                    Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
                    Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
                    Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());

                    UsbFile root = currentFs.getRootDirectory();
                    UsbFile[] files = root.listFiles();
                    for (UsbFile file : files) {
                        Log.d(TAG, file.getName());
                        if (file.isDirectory()) {
                            Log.d(TAG, "文件大小：" + file.getLength());
                        }
                        fileList.add(file);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(App.Companion.getInstance(), "USB设备初始化异常!请拔出U盘并重新插入！", Toast.LENGTH_SHORT).show();
                }
            } else {  // 没有权限，进行申请
                PendingIntent pendingIntent = PendingIntent.getBroadcast(App.Companion.getInstance(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
            }
        } else {
            Toast.makeText(App.Companion.getInstance(), "未识别到USB设备！", Toast.LENGTH_SHORT).show();
        }

        return fileList;
    }


    /**
     * 判断文件是否被选中
     *
     * @param file
     * @return
     */
    public static boolean isChecked(CommonFile file) {
        boolean isChecked = selectedFiles.contains(file.getPath());
        Log.d(TAG, "isChecked " + isChecked + " , " + file.getPath());
        return isChecked;
    }

    /**
     * 将指定文件路径添加到选中列表
     *
     * @param file
     * @return
     */
    public static void addChecked(CommonFile file) {
        handleCheckedFile(file);
        handleCheckedParent(file);
    }

    /**
     * 从选中列表中移除指定文件
     *
     * @param file
     * @return
     */
    public static void removeChecked(CommonFile file) {
        handleRemoveFile(file);
        handleRemoveParent(file);
    }

    /**
     * 处理选中的文件及其子文件
     *
     * @param file
     */
    private static void handleCheckedFile(CommonFile file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {                    // 如果是目录，则将其本身和所有子文件都选中

            if (file.isLocalRoot()) {                // 本地根目录特殊处理
                for (CommonFile commonFile : rootFileList) {
                    Log.d(TAG, "addChecked path root: " + commonFile.getPath());
                    selectedFiles.add(commonFile.getPath());
                    handleCheckedFile(commonFile);    // 递归调用
                }
            } else {
                selectedFiles.add(file.getPath());
                Log.d(TAG, "addChecked path " + file.getPath());

                List<CommonFile> children = readFileList(file);
                if (children != null) {
                    for (CommonFile child : children) {
                        handleCheckedFile(child);          // 递归调用
                    }
                }
            }
        } else {
            selectedFiles.add(file.getPath());
            Log.d(TAG, "addChecked path " + file.getPath());
        }
    }

    /**
     * 处理选中文件的父文件
     *
     * @param file
     */
    private static void handleCheckedParent(CommonFile file) {
        if (file == null || file.getParent() == null) {
            return;
        }
        CommonFile parent = file.getParent();

        List<CommonFile> parentFiles = readFileList(parent);
        if (parentFiles != null) {
            int selectedNum = 0;      //记录选中的数量
            for (CommonFile parentFile : parentFiles) {
                if (parentFile.isChecked()) {
                    selectedNum++;
                }
            }

            if (selectedNum == parentFiles.size()) {   // 父文件中的子文件全部被选中
                selectedFiles.add(parent.getPath());   // 将父文件也选中
                if (listener != null) {
                    listener.onChange(parent, true);
                }
                Log.d(TAG, "addChecked path parent : " + parent.getPath());

                handleCheckedParent(parent);  //递归调用
            } else {
                if (listener != null) {
                    listener.onChange(parent, false);
                }
            }
        }
    }

    /**
     * 移除选中的文件及其子文件
     *
     * @param file
     */
    private static void handleRemoveFile(CommonFile file) {
        if (file.isDirectory()) {                  // 如果指定文件是目录，则取消本身和所有子文件
            if (file.isLocalRoot()) {              // 本地根目录做特殊处理

                for (CommonFile commonFile : rootFileList) {
                    Log.d(TAG, "removeChecked path root: " + commonFile.getPath());
                    selectedFiles.remove(commonFile.getPath());
                    handleRemoveFile(commonFile);   // 递归调用
                }

            } else {
                selectedFiles.remove(file.getPath());
                Log.d(TAG, "removeChecked path :" + file.getPath());

                List<CommonFile> children = readFileList(file);
                if (children != null) {
                    for (CommonFile child : children) {
                        handleRemoveFile(child);    //递归移除所有子文件
                    }
                }
            }
        } else {
            selectedFiles.remove(file.getPath());
            Log.d(TAG, "removeChecked path :" + file.getPath());
        }
    }

    /**
     * 移除选中文件的父文件
     *
     * @param file
     */
    private static void handleRemoveParent(CommonFile file) {
        if (file == null || file.getParent() == null) {
            return;
        }

        CommonFile parent = file.getParent();
        List<CommonFile> parentFiles = readFileList(parent);
        if (parentFiles != null) {
            int selectedNum = 0;      //记录选中的数量
            for (CommonFile parentFile : parentFiles) {
                if (parentFile.isChecked()) {
                    selectedNum++;
                }
            }
            if (selectedNum == parentFiles.size() - 1) {   // 其他文件都是选中状态
                selectedFiles.remove(parent.getPath());   // 将父文件取消选中
                Log.d(TAG, "removeChecked path parent : " + parent.getPath());
                handleRemoveParent(parent);  //递归调用
            }

            if (listener != null) {
                listener.onChange(parent, false);
            }
        }
    }


    public static String translateFileName(File file) {
        switch (file.getPath()) {
            case FileManager.AUDIO_PATH:
                return "音频";
            case FileManager.VIDEO_PATH:
                return "视频";
            case FileManager.PICTURE_PATH:
                return "图片";
            case FileManager.AD_PATH:
                return "广告";
            case FileManager.SECURE_PATH:
                return "安全片";
            default:
                return file.getName();
        }
    }

    public static void removeAllSelected() {
        selectedFiles.clear();
    }


    public interface OnCheckedChangeListener {
        void onChange(CommonFile currentDir, boolean isAllChecked);
    }

    public static void setOnCheckedChangeCallBack(OnCheckedChangeListener onCheckedChangeListener) {
        listener = onCheckedChangeListener;
    }

}
