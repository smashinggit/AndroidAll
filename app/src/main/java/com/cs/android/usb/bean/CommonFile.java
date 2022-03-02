package com.cs.android.usb.bean;

import com.cs.android.usb.FileManager;

import java.io.File;
import java.util.List;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/24 11:06
 **/
public class CommonFile {

    private String name;
    private String displayName;
    private String path;
    private String displayPath;
    private CommonFile parent;

    private boolean isDirectory;
    private boolean isLocalRoot;

    private TYPE type;


    public CommonFile(String name, String displayName, String path, String displayPath, CommonFile parent, boolean isDirectory, TYPE type) {
        this.name = name;
        this.displayName = displayName;
        this.path = path;
        this.displayPath = displayPath;
        this.parent = parent;
        this.isDirectory = isDirectory;
        this.type = type;
    }

    public CommonFile(File file) {
        this.name = file.getName();
        this.displayName = FileManager.translateFileName(file);
        this.path = file.getPath();
        this.displayPath = file.getPath();

        this.isDirectory = file.isDirectory();
        type = TYPE.RAW_FILE;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayPath() {
        return displayPath;
    }

    public void setDisplayPath(String displayPath) {
        this.displayPath = displayPath;
    }

    public CommonFile getParent() {
        return parent;
    }

    public void setParent(CommonFile parent) {
        this.parent = parent;
    }

    public boolean isLocalRoot() {
        return isLocalRoot;
    }

    public void setLocalRoot(boolean localRoot) {
        isLocalRoot = localRoot;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isChecked() {
        return FileManager.isChecked(this);
    }

    public void setChecked(boolean checked) {
        if (checked) {
            FileManager.addChecked(this);
        } else {
            FileManager.removeChecked(this);
        }
    }

    @Override
    public String toString() {
        return "CommonFile{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", path='" + path + '\'' +
                ", displayPath='" + displayPath + '\'' +
                ", isDirectory=" + isDirectory +
                ", type=" + type +
                ", parent=" + parent +
                '}';
    }

    public enum TYPE {
        RAW_FILE, USB_FILE
    }
}
