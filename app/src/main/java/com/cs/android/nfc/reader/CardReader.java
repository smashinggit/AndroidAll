package com.cs.android.nfc.reader;

import android.nfc.Tag;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 15:38
 **/
public abstract class CardReader {

    public static String getId(Tag tag) {
        return ByteArrayToHexString(tag.getId());
    }

    public static String[] getTechList(Tag tag) {
        return tag.getTechList();
    }

    public static String readTag(Tag tag) {
        CardReader reader = with(tag);
        if (reader == null) {
            return "暂不支持此卡类型";
        }
        return reader.parse(tag);
    }

    public static CardReader with(Tag tag) {
        List<String> techs = Arrays.asList(tag.getTechList());
        if (techs.contains("android.nfc.tech.IsoDep")) {
            return new CustomIsoDepReader();
        }
        if (techs.contains("android.nfc.tech.NfcBarcode")) {
            return new NfcBarcodeReader();
        }
        if (techs.contains("android.nfc.tech.MifareClassic")) {
            return new MifareClassicReader();
        }
        if (techs.contains("android.nfc.tech.MifareUltralight")) {
            return new MifareUltralightReader();
        }
        if (techs.contains("android.nfc.tech.Ndef")) {
            return new NdefReader();
        }
        if (techs.contains("android.nfc.tech.NfcA")) {
            return new NfcAReader();
        }
        if (techs.contains("android.nfc.tech.NfcB")) {
            return new NfcBReader();
        }
        if (techs.contains("android.nfc.tech.NfcV")) {
            return new NfcVReader();
        }
        if (techs.contains("android.nfc.tech.NfcF")) {
            return new NfcFReader();
        }
        if (techs.contains("android.nfc.tech.NdefFormatable")) {

        }
        return null;
    }


    public abstract String parse(Tag tag);


    /**
     * 将字节数组转换为字符串
     */
    protected static String ByteArrayToHexString(byte[] bytes) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < bytes.length; ++j) {
            in = (int) bytes[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    /**
     * Utility class to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     */
    protected static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    protected void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeable = null;
        }
    }

}
