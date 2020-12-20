package com.cs.test.java;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class IOTest {

    public static void main(String[] args) {
//        createFile();
//        renameFile();

//        File file = new File("extra/temp");
//        listFiles(file);
//        deleteFileAndDirs(file);


//        File file = new File("extra/滕王阁序.txt");
//        readFile(file);

        File file = new File("extra/悯农.txt");
        writeFile(file);

    }

    /**
     * 写文件
     */
    private static void writeFile(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            String content =
                    "锄禾日当午\n" +
                            "汗滴禾下土\n" +
                            "谁知盘中餐\n" +
                            "粒粒皆辛苦\n";

            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读写文件
     *
     * @param file
     */
    private static void readFile(File file) {
        try {
            System.out.println("文件大小 " + file.length());

            FileInputStream fileInputStream = new FileInputStream(file);
            System.out.println("可读大小 " + fileInputStream.available());

            StringBuffer result = new StringBuffer();
            byte[] buffer = new byte[1024]; //1kb

            while (fileInputStream.read(buffer) != -1) {
                result.append(new String(buffer));
            }

            System.out.println("读取结果： \n" + result.toString());
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 递归删除文件和目录
     *
     * @param rootFile
     */
    private static void deleteFileAndDirs(File rootFile) {
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFileAndDirs(file);  //递归删除
                } else {
                    file.delete();
                }
            }
        }
        rootFile.delete();
    }

    /**
     * 读取出某个文件下面的所有文件及文件夹
     */
    private static void listFiles(File rootFile) {
        if (!rootFile.exists() || !rootFile.isDirectory()) {
            return;
        }
        File[] listFiles = rootFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            return;
        }

        for (int i = 0; i < listFiles.length; i++) {
            File file = listFiles[i];
            if (file.isDirectory()) {

                File[] sonFiles = file.listFiles();
                if (sonFiles == null || sonFiles.length == 0) {
                    System.out.println("文件夹 " + file.getName() + " 为空");
                } else {
                    System.out.println("文件夹 " + file.getName() + " 下包含" + sonFiles.length + "个子文件");
                    listFiles(file); // 递归调用
                }
            } else {
                System.out.println("文件 " + file.getName());
            }
        }
    }


    /**
     * 重命名
     */
    private static void renameFile() {
        File oldFile = new File("extra/cache/pic", "image.jpg");
        File newFile = new File("extra/cache/pic/newImage.jpg");

        boolean result = oldFile.renameTo(newFile);
        System.out.println("重命名 " + result);
    }

    /**
     * 假设 /cache/pic 这两个目录不存在，使用以下方式创建:
     */
    private static void createFile() {
        File file = new File("extra/cache/pic", "image.jpg");
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
