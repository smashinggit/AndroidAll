package com.cs.test;

import java.nio.charset.StandardCharsets;

class EncoderTest {

    public static void main(String[] args) {

        try {
            char a = 'A';
            String b = "B";
            String name = "My name is 陈森";

            // 将字符串转换成字节数组
            byte[] bytesB = b.getBytes(StandardCharsets.UTF_8);
            byte[] bytesU8 = name.getBytes(StandardCharsets.UTF_8);
            byte[] bytesGBK = name.getBytes("GBK");



        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
