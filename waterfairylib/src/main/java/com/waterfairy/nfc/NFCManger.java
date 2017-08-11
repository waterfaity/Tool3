package com.waterfairy.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.text.TextUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Created by water_fairy on 2017/7/11.
 * 995637517@qq.com
 */

public class NFCManger {


    private NfcAdapter nfcAdapter;
    private static NFCManger NFC_MANGER;
    private PendingIntent pendingIntent;
    private Activity activity;
    private OnNFCReadListener onNFCReadListener;

    public NfcAdapter getNfcAdapter() {
        return nfcAdapter;
    }

    public static NFCManger getInstance() {
        if (NFC_MANGER == null) {
            NFC_MANGER = new NFCManger();
        }
        return NFC_MANGER;
    }

    public void init(Activity activity, Class aClass) {
        this.activity = activity;
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        pendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, aClass)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void onResume() {
        if (nfcAdapter != null) {
            try {
                nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null); //启动
                nfcAdapter.enableForegroundNdefPush(activity, new NdefMessage(new NdefRecord[]{}));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onPause() {
        if (nfcAdapter != null) {
            try {
                nfcAdapter.disableForegroundDispatch(activity);
                nfcAdapter.disableForegroundNdefPush(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onReceive(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.endsWith(action)) {
            processIntent(intent);
        }
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NFCBean nfcBean = new NFCBean();
//        nfcBean = readNFC(tagFromIntent, nfcBean);
        String CardId = new BigInteger(1, tagFromIntent.getId()).toString(10);
        nfcBean.setId(CardId);
        if (onNFCReadListener != null)
            onNFCReadListener.onReadNFCInfo(nfcBean);
    }

    private NFCBean readNFC(Tag tagFromIntent, NFCBean nfcBean) {
        String[] techList = tagFromIntent.getTechList();
        String tagType = "";
        for (String string : techList) {
            if (TextUtils.equals(string, NfcA.class.getName())) {
                NfcA nfcA = NfcA.get(tagFromIntent);
                int masLen = nfcA.getMaxTransceiveLength();
                nfcBean.setMaxSize(masLen);
                if (TextUtils.equals("", tagType)) {
                    nfcBean.setType(NFCBean.TYPE_CLASSICAL);
                }
            } else if (TextUtils.equals(string, MifareUltralight.class.getName())) {
                MifareUltralight mifareUltralight = MifareUltralight.get(tagFromIntent);
                int type = mifareUltralight.getType();
                switch (type) {
                    case NFCBean.TYPE_ULTRALIGHT:
                        nfcBean.setType(MifareUltralight.TYPE_ULTRALIGHT);
                        break;
                    case NFCBean.TYPE_ULTRALIGHT_C:
                        nfcBean.setType(MifareUltralight.TYPE_ULTRALIGHT_C);
                        break;
                    case NFCBean.TYPE_UNKNOWN:
                        nfcBean.setType(MifareUltralight.TYPE_UNKNOWN);
                        break;
                }
                int maxLen = mifareUltralight.getMaxTransceiveLength();
                nfcBean.setMaxSize(maxLen);

            } else if (TextUtils.equals(string, IsoDep.class.getName())) {
                IsoDep isoDep = IsoDep.get(tagFromIntent);
                int maxLen = isoDep.getMaxTransceiveLength();
                nfcBean.setMaxSize(maxLen);
                byte[] historicalBytes = isoDep.getHistoricalBytes();
                nfcBean.setMessage(new String(historicalBytes));
            } else if (TextUtils.equals(string, Ndef.class.getName())) {
                nfcBean = readNfcData(tagFromIntent, nfcBean);
            }
        }
        return nfcBean;
    }

    private NFCBean readNfcA(NfcA nfcA, NFCBean nfcBean) {
        if (nfcA != null) {
            try {
                nfcA.connect();
                byte[] transceive = nfcA.transceive(getSelectCommand(getSZT()));
                nfcBean.addMessage("\n" + new String(transceive));
                nfcA.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return nfcBean;
    }

    private NFCBean readClassical(MifareClassic mifareClassic, NFCBean nfcBean) {
        try {
            if (mifareClassic == null) return nfcBean;
            mifareClassic.connect();
            int blockCount = mifareClassic.getBlockCount();
            for (int i = 0; i < blockCount; i++) {
                byte[] bytes = mifareClassic.readBlock(blockCount);
                nfcBean.addMessage(new String(bytes) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nfcBean;
    }


    private NFCBean readNfcData(Tag tagFromIntent, NFCBean nfcBean) {
        if (tagFromIntent == null) {
            nfcBean.setMessage("连接断开");
        } else {
            Ndef nfcF = Ndef.get(tagFromIntent);
            try {
                if (nfcF == null) {
                    nfcBean.setMessage("读取错误");
                } else {
                    nfcF.connect();
                    String message = new String(nfcF.getNdefMessage().toByteArray());
                    nfcBean.setMessage(message);
                    nfcF.close();
                }

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                nfcBean.setMessage("读取错误");
            }
        }
        return nfcBean;
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F"};
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    public void setOnNFCReadListener(OnNFCReadListener onNFCReadListener) {
        this.onNFCReadListener = onNFCReadListener;
    }

    public void onDestroy() {
        activity = null;
        nfcAdapter = null;
        NFC_MANGER = null;
    }

    public interface OnNFCReadListener {
        void onReadNFCInfo(NFCBean nfcBean);

    }

    private byte[] getSelectCommand(byte[] aid) {
        final ByteBuffer cmd_pse = ByteBuffer.allocate(aid.length + 6);
        cmd_pse.put((byte) 0x00) // CLA Class
                .put((byte) 0xA4) // INS Instruction
                .put((byte) 0x04) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) aid.length) // Lc
                .put(aid).put((byte) 0x00); // Le
        return cmd_pse.array();
    }

    private byte[] getMF() {
//        1pay.sys.ddf01
        return new byte[]{(byte) '1', (byte) 'P',
                (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
                (byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F',
                (byte) '0', (byte) '1',};
    }

    private byte[] getSZT() {
        //select Main Application
        byte[] szt = {(byte) 'P', (byte) 'A', (byte) 'Y',
                (byte) '.', (byte) 'S', (byte) 'Z', (byte) 'T'};
        ;
        return szt;
    }


    //      <intent-filter>
//                <action android:name="android.nfc.action.TAG_DISCOVERED" />
//                <data android:mimeType="text/plain" />
//      </intent-filter>

// <uses-permission android:name="android.permission.NFC"/>
//    <uses-feature
//    android:name="android.hardware.nfc"
//    android:required="true"/>

//     <intent-filter>
//                <action android:name="android.nfc.action.TECH_DISCOVERED"/>
//            </intent-filter>
//
//            <meta-data
//    android:name="android.nfc.action.TECH_DISCOVERED"
//    android:resource="@xml/filter_nfc"/>
//            <intent-filter>
//                <action android:name="android.nfc.action.TAG_DISCOVERED"/>
//                <data android:mimeType="text/plain"/>
//                <category android:name="android.intent.category.DEFAULT"/>
//            </intent-filter>

//    <?xml version="1.0" encoding="utf-8"?>
//<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
//    <tech-list>
//        <tech>android.nfc.tech.NfcA</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.NfcB</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.NfcF</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.NfcV</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.Ndef</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.NdefFormatable</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.IsoDep</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.MifareClassic</tech>
//    </tech-list>
//    <tech-list>
//        <tech>android.nfc.tech.MifareUltralight</tech>
//    </tech-list>
//</resources>
}
