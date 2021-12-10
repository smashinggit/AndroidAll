package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.util.Log;

import com.cs.android.nfc.reader.CardReader;

import java.nio.charset.Charset;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 15:57
 **/
public class NfcAReader extends CardReader {
    @Override
    public String parse(Tag tag) {
        Log.d("tag", "NfcAReader pares");

        NfcA nfca = NfcA.get(tag);
        try {
            nfca.connect();
            if (nfca.isConnected()) { // NTAG216的芯片
                byte[] SELECT = {
                        (byte) 0x30,
                        (byte) 5 & 0x0ff,//0x05
                };
                byte[] response = nfca.transceive(SELECT);
                nfca.close();
                if (response != null) {
                    return new String(response, Charset.forName("utf-8"));
                } else {
                    return "response is null";
                }
            } else {
                return "not connected";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(nfca);
        }
    }
}
