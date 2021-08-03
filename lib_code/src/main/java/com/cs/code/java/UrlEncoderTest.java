package com.cs.code.java;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author ChenSen
 * @date 2021/8/2 18:43
 * @desc
 */
public class UrlEncoderTest {


    public static void main(String[] args) {
        try {
            String rawStr = "张三123abc+-*/.-_!@#";

            // 将普通字符串转换成application/x-www-form-urlencoded字符串
            System.out.println("采用utf-8字符集编码:");
            String encoderResult1 = URLEncoder.encode(rawStr, "UTF-8");
            System.out.println(encoderResult1);

            System.out.println("采用GBK字符集编码:");
            String encoderResult2 = URLEncoder.encode(rawStr, "GBK");
            System.out.println(encoderResult2);


            String encodedStr = "%E5%BC%A0%E4%B8%89123abc%2B-*%2F.-_%21%40%23";  // 编码过的字符串(UTF-8)：张三123abc+-*/.-_!@#

            // 将application/x-www-form-urlencoded字符串转换成普通字符串
            System.out.println("采用utf-8字符集解码:");
            String decoderResult1 = URLDecoder.decode(encodedStr, "UTF-8");
            System.out.println(decoderResult1);

            System.out.println("采用GBK字符集解码:");
            String decoderResult2 = URLDecoder.decode(encodedStr, "GBK");
            System.out.println(decoderResult2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
