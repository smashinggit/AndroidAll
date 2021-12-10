package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.io.IOException;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/10 15:47
 **/
public class MifareClassicReader extends CardReader {
    private static final String TAG = "MifareClassicReader";

    @Override
    public String parse(Tag tag) {
        Log.d("tag", "MifareClassicReader pares");

        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
            int type = mifareClassic.getType();                 // 获取TAG的类型
            int sectorCount = mifareClassic.getSectorCount();   // 获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append("\n卡片类型：").append(typeS);
            buffer.append("\n共").append(sectorCount).append("个扇区");
            buffer.append("\n共").append(mifareClassic.getBlockCount()).append("个块");
            buffer.append("\n存储空间：").append(mifareClassic.getSize()).append("B");

            boolean auth;
            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                buffer.append("\nSector").append(j);
                auth = mifareClassic.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
                int bCount;
                int bIndex;
                if (auth) {
                    buffer.append("验证成功");
                    // 读取扇区中的块
                    bCount = mifareClassic.getBlockCountInSector(j);
                    bIndex = mifareClassic.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mifareClassic.readBlock(bIndex);
                        buffer.append("\nBlock " + bIndex + " : " + ByteArrayToHexString(data) + "");
                        bIndex++;
                    }
                } else {
                    buffer.append("验证失败");
                }
            }
            return buffer.toString();

        } catch (IOException e) {
            Log.d(TAG, "parse", e);
            return e.getMessage();
        } finally {
            close(mifareClassic);
        }

    }
}
