package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.util.Log;

import com.cs.android.nfc.reader.CardReader;

import java.io.IOException;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 15:58
 **/
public class NfcBReader extends CardReader {
    private static final String TAG = "NfcBReader";

    @Override
    public String parse(Tag tag) {
        Log.d("tag", "NfcBReader pares");

        NfcB nfcb = NfcB.get(tag);
        try {
            nfcb.connect();
            StringBuffer buffer = new StringBuffer();
            if (nfcb.isConnected()) {       //身份证已连接
                Log.d(TAG, "已连接");
                byte[] applicationData = nfcb.getApplicationData();
                byte[] protocolInfo = nfcb.getProtocolInfo();
                buffer.append("ApplicationData:").append(ByteArrayToHexString(applicationData));
                buffer.append("\nProtocolInfo:").append(ByteArrayToHexString(protocolInfo));
            } else {
                buffer.append("not connected");
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(nfcb);
        }

    }
}
