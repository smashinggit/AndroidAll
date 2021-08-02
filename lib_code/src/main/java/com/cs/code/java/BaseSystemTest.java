package com.cs.code.java;

/**
 * @author ChenSen
 * @date 2021/7/30 11:34
 * @desc 进制转换
 */
public class BaseSystemTest {

    private static int binary = 0b10010;   //二进制
    private static int octal = 022;        //八进制
    private static int decimal = 18;       //十进制
    private static int hen = 0x12;         //十六进制

    public static void main(String[] args) {
        System.out.println("二进制 0b10010 = " + binary);
        System.out.println("八进制 022 = " + octal);
        System.out.println("十进制 18 = " + decimal);
        System.out.println("十六进制 0x12 = " + hen);


        // 十进制转其他进制
        System.out.println("十进制转二进制 18 -> " + Integer.toBinaryString(18));
        System.out.println("十进制转八进制 18 -> " + Integer.toOctalString(18));
        System.out.println("十进制转十六他进制 18 -> " + Integer.toHexString(18));

        //其他进制转十进制
        System.out.println("二进制转十进制  0b10010 -> " + Integer.valueOf("10010", 2));
        System.out.println("八进制转十进制 022 -> " + Integer.valueOf("22", 8));
        System.out.println("十六他进制转十进制 0x12 -> " + Integer.valueOf("12", 16));
        //或者
        System.out.println("二进制转十进制  0b10010 -> " + Integer.parseInt("10010", 2));
        System.out.println("八进制转十进制 022 -> " + Integer.parseInt("22", 8));
        System.out.println("十六他进制转十进制 0x12 -> " + Integer.parseInt("12", 16));

    }


}
