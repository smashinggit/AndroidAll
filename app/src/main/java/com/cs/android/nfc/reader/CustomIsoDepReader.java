package com.cs.android.nfc.reader;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChenSen
 * @desc IsoDep主要用于读取各城市公交卡信息如：武汉通，羊城通，深圳通，北京市政交通卡，长安通。
 * <p>
 * 深圳通：
 * byte[] DFN_SRV = { (byte) 'P', (byte) 'A', (byte) 'Y',
 * (byte) '.', (byte) 'S', (byte) 'Z', (byte) 'T' };
 * <p>
 * 武汉通：
 * byte[] DFN_SRV = { (byte) 0x41, (byte) 0x50,
 * (byte) 0x31, (byte) 0x2E, (byte) 0x57, (byte) 0x48, (byte) 0x43,
 * (byte) 0x54, (byte) 0x43, };
 * <p>
 * 羊城通：
 * byte[] DFN_SRV = { (byte) 'P', (byte) 'A', (byte) 'Y',
 * (byte) '.', (byte) 'A', (byte) 'P', (byte) 'P', (byte) 'Y', };
 * <p>
 * 长安通：
 * byte[] DFN_SRV = { (byte) 0xA0, (byte) 0x00,
 * (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x86, (byte) 0x98,
 * (byte) 0x07, (byte) 0x01, };
 * <p>
 * 北京市政交通卡：
 * byte[] DFI_EP = { (byte) 0x10, (byte) 0x01 };
 **/
public class CustomIsoDepReader extends CardReader {
    private static final String TAG = "IsoDepReader";

    protected final static byte TRANS_CSU = 6; // 如果等于0x06或者0x09，表示刷卡；否则是充值
    protected final static byte TRANS_CSU_CPX = 9; // 如果等于0x06或者0x09，表示刷卡；否则是充值


    @Override
    public String parse(Tag tag) {
        Log.d("tag", "CustomIsoDepReader pares");
        IsoDep isoDep = IsoDep.get(tag);
        try {
            isoDep.connect();
            StringBuffer buffer = new StringBuffer();
            if (isoDep.isConnected()) {
                Log.d("tag", "isoDep.isConnected"); // 判断是否连接上
                // 1.select PSF (1PAY.SYS.DDF01)
                // 选择支付系统文件，它的名字是1PAY.SYS.DDF01。
                byte[] DFN_PSE = {(byte) '1', (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y', (byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F', (byte) '0', (byte) '1',};
                isoDep.transceive(getSelectCommand(DFN_PSE));
                // 2.选择公交卡应用的名称
                byte[] DFN_SRV = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x01,};
                isoDep.transceive(getSelectCommand(DFN_SRV));
                // 3.读取余额
                byte[] ReadMoney = {(byte) 0x80, // CLA Class
                        (byte) 0x5C, // INS Instruction
                        (byte) 0x00, // P1 Parameter 1
                        (byte) 0x02, // P2 Parameter 2
                        (byte) 0x04, // Le
                };
                byte[] Money = isoDep.transceive(ReadMoney);
                if (Money != null && Money.length > 4) {
                    int cash = byteToInt(Money, 4);
                    float ba = cash / 100.0f;
                    buffer.append("余额:" + ba);
                }

                // 4.读取所有交易记录
                byte[] ReadRecord = {(byte) 0x00, // CLA Class
                        (byte) 0xB2, // INS Instruction
                        (byte) 0x01, // P1 Parameter 1
                        (byte) 0xC5, // P2 Parameter 2
                        (byte) 0x00, // Le
                };

                byte[] Records = isoDep.transceive(ReadRecord);
                // 处理Record
                Log.d("h_bl", "总消费记录" + Records);
                ArrayList<byte[]> ret = parseRecords(Records);
                List<String> retList = parseRecordsToStrings(ret);

                buffer.append("\n" + "消费记录如下：");
                for (String string : retList) {
                    Log.d("h_bl", "消费记录" + string);
                    buffer.append("\n" + string);
                }
            } else {
                buffer.append("not connected");
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            close(isoDep);
        }

    }


    private byte[] getSelectCommand(byte[] aid) {
        final ByteBuffer cmd_pse = ByteBuffer.allocate(aid.length + 6);
        cmd_pse.put((byte) 0x00)  // CLA Class
                .put((byte) 0xA4) // INS Instruction
                .put((byte) 0x04) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) aid.length) // Lc
                .put(aid).put((byte) 0x00); // Le
        return cmd_pse.array();
    }

    /**
     * 整条Records解析成ArrayList<byte[]>
     *
     * @param Records
     * @return
     */
    private ArrayList<byte[]> parseRecords(byte[] Records) {
        int max = Records.length / 23;
        Log.d("h_bl", "消费记录有" + max + "条");
        ArrayList<byte[]> ret = new ArrayList<byte[]>();
        for (int i = 0; i < max; i++) {
            byte[] aRecord = new byte[23];
            for (int j = 23 * i, k = 0; j < 23 * (i + 1); j++, k++) {
                aRecord[k] = Records[j];
            }
            ret.add(aRecord);
        }
        for (byte[] bs : ret) {
            Log.d("h_bl", "消费记录有byte[]" + bs); // 有数据。解析正确。
        }
        return ret;
    }

    /**
     * ArrayList<byte[]>记录分析List<String> 一条记录是23个字节byte[] data，对其解码如下
     * data[0]-data[1]:index data[2]-data[4]:over,金额溢出?？？ data[5]-data[8]:交易金额
     * ？？代码应该是（5，4） data[9]:如果等于0x06或者0x09，表示刷卡；否则是充值
     * data[10]-data[15]:刷卡机或充值机编号
     * data[16]-data[22]:日期String.format("%02X%02X.%02X.%02X %02X:%02X:%02X"
     * ,data[16], data[17], data[18], data[19], data[20], data[21], data[22]);
     *
     * @return
     */
    private List<String> parseRecordsToStrings(ArrayList<byte[]>... Records) {
        List<String> recordsList = new ArrayList<String>();
        for (ArrayList<byte[]> record : Records) {
            if (record == null)
                continue;
//            for (byte[] v : record) {
//                StringBuilder r = new StringBuilder();
//                int cash = Util.toInt(v, 5, 4);
//                char t = (v[9] == TRANS_CSU || v[9] == TRANS_CSU_CPX) ? '-' : '+';
//                r.append(String.format("%02X%02X.%02X.%02X %02X:%02X ", v[16], v[17], v[18], v[19], v[20], v[21], v[22]));
//                r.append("   " + t).append(Util.toAmountString(cash / 100.0f));
//                String aLog = r.toString();
//                recordsList.add(aLog);
//            }
        }
        return recordsList;
    }

    // byteArray转化为int
    private int byteToInt(byte[] b, int n) {
        int ret = 0;
        for (int i = 0; i < n; i++) {
            ret = ret << 8;
            ret |= b[i] & 0x00FF;
        }
        if (ret > 100000 || ret < -100000)
            ret -= 0x80000000;
        return ret;
    }

}
