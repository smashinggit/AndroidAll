package com.cs.android.nfc.reader;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 15:41
 **/
public class NdefReader extends CardReader {

    @Override
    public String parse(Tag tag) {
        Log.d("tag", "NdefReader pares");

        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            if (records == null || records.length <= 0) {
                return "getRecords() is null";
            }
            StringBuffer buffer = new StringBuffer();
            for (NdefRecord record : records) {
                buffer.append("\nNdefRecord:").append(record.toString());
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(ndef);
        }
    }
}
