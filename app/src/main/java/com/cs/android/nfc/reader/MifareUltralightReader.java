package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;

import java.io.IOException;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 15:49
 **/
public class MifareUltralightReader extends CardReader {

    public String parse(Tag tag) {
        Log.d("tag", "MifareUltralightReader pares");

        MifareUltralight mifareUltralight = MifareUltralight.get(tag);
        try {
            mifareUltralight.connect();
            StringBuffer buffer = new StringBuffer();
            int type = mifareUltralight.getType();
            switch (type) {
                case MifareUltralight.TYPE_ULTRALIGHT:
                    buffer.append("TYPE_ULTRALIGHT");
                    break;
                case MifareUltralight.TYPE_ULTRALIGHT_C:
                    buffer.append("TYPE_ULTRALIGHT_C");
                    break;
                case MifareUltralight.TYPE_UNKNOWN:
                    buffer.append("TYPE_UNKNOWN");
                    break;
            }
            byte[] payload = mifareUltralight.readPages(0);
            byte[] payload1 = mifareUltralight.readPages(4);
            byte[] payload2 = mifareUltralight.readPages(8);
            byte[] payload3 = mifareUltralight.readPages(12);
            buffer.append("\nPage0-3:").append(ByteArrayToHexString(payload));
            buffer.append("\npage4-7:").append(ByteArrayToHexString(payload1));
            buffer.append("\npage8-11:").append(ByteArrayToHexString(payload2));
            buffer.append("\npage12-15:").append(ByteArrayToHexString(payload3));

            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(mifareUltralight);
        }
    }
}
