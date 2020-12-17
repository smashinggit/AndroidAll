package com.cs.test;

class JavaBaseTest {

    public static void main(String[] args) {
//        system();  //进制
//        systemTransform();  // 进制转换
//        charTest(); // char类型
        operator();//操作符

    }

    private static void operator() {
        int x = 4;
        int y = 5;

        System.out.println("4 & 5 -> " + (x & y));
        System.out.println("4 | 5 -> " + (x | y));
        System.out.println("4 ^ 5 -> " + (x ^ y));
        System.out.println("~4  -> " + (~x));
        System.out.println("5<<2  -> " + (y << 2));
        System.out.println("5>>1  -> " + (y >> 1));


        int a = 1;
        int b = 2;

        System.out.println("1&=2  -> " + (a &= b));   //a = 0

        a = 1;
        System.out.println("1|=2  -> " + (a |= b));   //a = 3

        a = 1;
        System.out.println("1^=2  -> " + (a ^= b));   //a = 3

        a = 1;
        System.out.println("1~=2  -> " + (~b));     // a = -3

        a = 1;
        System.out.println("1>>=2  -> " + (a >>= b)); //a = 0

        a = 1;
        System.out.println("1<<=2  -> " + (a <<= b)); //a = 4
    }

    private static void charTest() {
        char a = '陈';
        char b = 'a';
        char c = 'A';
        char d = 10;

        System.out.println("a: " + a);
        System.out.println("a+1: " + (a + 1));
        System.out.println("b: " + b);
        System.out.println("b+1: " + (b + 1));
        System.out.println("c: " + c);
        System.out.println("c+1: " + (c + 1));
        System.out.println("d: " + d);
        System.out.println("d+1: " + (d + 1));
    }


    /**
     * 进制转换
     */
    private static void systemTransform() {
        int i = 17;

        System.out.println("十进制17转二进制 " + Integer.toBinaryString(i));   //10001
        System.out.println("十进制17转八进制 " + Integer.toOctalString(i));    //21
        System.out.println("十进制17转十六进制 " + Integer.toHexString(i));    //11


        System.out.println("二进制0b10001转十进制 " + Integer.valueOf("10001", 2));   //17
        System.out.println("八进制021转十进制 " + Integer.valueOf("21", 8));          //17
        System.out.println("十六进制0x11转十进制 " + Integer.valueOf("11", 16));       //17


        System.out.println("二进制0b10001转十进制 " + Integer.parseInt("10001", 2));   //17
        System.out.println("八进制021转十进制 " + Integer.parseInt("21", 8));          //17
        System.out.println("十六进制0x11转十进制 " + Integer.parseInt("11", 16));       //17
    }


    /**
     * 进制
     */
    private static void system() {
        int a = 0b11;  //声明二进制变量
        int b = 011;   //声明八进制变量
        int c = 11;    //声明十进制变量
        int d = 0x11;  //声明十六进制变量

        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("c: " + c);
        System.out.println("d: " + d);
    }
}
