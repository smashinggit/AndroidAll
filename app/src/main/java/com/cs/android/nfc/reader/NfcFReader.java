package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.util.Log;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 16:00
 **/
public class NfcFReader extends CardReader {
    @Override
    public String parse(Tag tag) {
        Log.d("tag", "NfcFReader pares");

        NfcF nfcf = NfcF.get(tag);
        try {
//            nfcf.connect();
//            byte[] felicaIDm = new byte[]{0};
//            byte[] req = readWithoutEncryption(felicaIDm, 10);
//            byte[] res = nfcf.transceive(req);
//            return ByteArrayToHexString(res);
            return "NfcFReader 未实现";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(nfcf);
        }
    }
}
