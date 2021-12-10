package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 16:01
 **/
public class NfcVReader extends CardReader {
    @Override
    public String parse(Tag tag) {
        Log.d("tag", "NfcVReader pares");

        NfcV nfcV = NfcV.get(tag);
        try {
            nfcV.connect();
            byte[] tagUid = tag.getId();
            int blockAddress = 0;
            int blocknum = 4;
            byte[] cmd = new byte[]{
                    (byte) 0x22,  // FLAGS
                    (byte) 0x23,  // 20-READ_SINGLE_BLOCK,23-所有块
                    0, 0, 0, 0, 0, 0, 0, 0,
                    (byte) (blockAddress & 0x0ff), (byte) (blocknum - 1 & 0x0ff)
            };
            System.arraycopy(tagUid, 0, cmd, 2, tagUid.length);
            byte[] response = nfcV.transceive(cmd);
            if (response != null) {
                return new String(response, Charset.forName("utf-8"));
            } else {
                return "respones is null";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(nfcV);
        }
    }
}
