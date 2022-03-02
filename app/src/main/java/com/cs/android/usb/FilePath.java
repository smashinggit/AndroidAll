package com.cs.android.usb;

import android.util.Log;

import com.cs.android.usb.bean.CommonFile;

import java.util.Stack;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/23 10:16
 **/
public class FilePath {
    public static final String TAG = FilePath.class.getSimpleName();

    private Stack<CommonFile> fileStack = new Stack();


    public CommonFile push(CommonFile file) {
        Log.d(TAG, "push file : " + file.toString());
        return fileStack.push(file);
    }

    public CommonFile pop() {
        Log.d(TAG, "pop file ");
        return fileStack.pop();
    }

    public CommonFile peek() {
        return fileStack.peek();
    }

    public int getPathLevel() {
        return fileStack.size();
    }

    public String getDisplayPath() {
        StringBuffer displayPath = new StringBuffer();
        int level = getPathLevel();

        if (level <= 3) {
            for (int i = 0; i < level; i++) {
                CommonFile file = fileStack.elementAt(i);
                displayPath.append(file.getDisplayName());
                if (i != level - 1) {
                    displayPath.append(" > ");
                }
            }
        } else {
            CommonFile rootFile = fileStack.elementAt(0);
            displayPath.append(rootFile.getDisplayName());
            displayPath.append(" > ");
            displayPath.append("...");
            displayPath.append(" > ");
            displayPath.append(clipName(fileStack.elementAt(level - 2).getDisplayName()));
            displayPath.append(" > ");
            displayPath.append(clipName(fileStack.elementAt(level - 1).getDisplayName()));
        }

        return displayPath.toString();
    }

    public String getRawPath() {
        StringBuffer totalPath = new StringBuffer();

        for (int i = 1; i < fileStack.size(); i++) {
            if (i == 1) {
                totalPath.append("rootPath");
            }
            totalPath.append("/" + fileStack.elementAt(i));

        }
        return totalPath.toString();
    }

    public void clear() {
        fileStack.clear();
    }

    /**
     * 名称长度大于8的部分，用...替代
     *
     * @param name
     * @return
     */
    public String clipName(String name) {
        return name.length() > 8 ? name.substring(0, 7) + "..." : name;
    }
}
