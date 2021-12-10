package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.NfcBarcode;
import android.util.Log;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 16:01
 **/
public class NfcBarcodeReader extends CardReader {
    @Override
    public String parse(Tag tag) {
        Log.d("tag", "NfcBarcodeReader pares");

        NfcBarcode barcode = NfcBarcode.get(tag);
        try {
            barcode.connect();
            byte[] bytes = barcode.getBarcode();
            return ByteArrayToHexString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(barcode);
        }
    }
}
