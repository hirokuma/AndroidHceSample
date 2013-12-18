package com.blogpost.hiro99ma.hcesample;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;


public class HostSampleService extends HostApduService {

    static enum CardStatus {
        INIT,
        CCFILE,
        REQ_DATA,
        OPEN,
        LEN,
        RET_DATA
    }
    final static byte[] SUCCESS = { (byte)0x90, (byte)0x00 };
    final static byte[] REQ_DATA = {
        (byte)0x00, (byte)0x0f,		//CCLEN
        (byte)0x20,					//Mapping Version
        (byte)0x00, (byte)0x40,		//MLe
        (byte)0x00, (byte)0x40,		//MLc

        //TLV(NDEF File Control)
        (byte)0x04, 				//Tag
        (byte)0x06,					//LEN
        (byte)0xe1, (byte)0x04,		//signature
        (byte)0x00, (byte)0x32,		//max ndef size
        (byte)0x00, 				//read access permission
        (byte)0x00,					//write access permission
        
        //success
        (byte)0x90, (byte)0x00
    };
    final static byte[] LEN = { (byte)0x00, (byte)0x03 };
    final static byte[] RET_DATA = {
        (byte)0x00, (byte)0x03,		//LEN
        //message
        (byte)0xd0, (byte)0x00, (byte)0x00,		//empty
        
        //success
        (byte)0x90, (byte)0x00
    };

    private final static String TAG = "HostSampleService";

    private CardStatus mCardStatus = CardStatus.INIT;

    @Override
    public void onDeactivated(int reason) {
        mCardStatus = CardStatus.INIT;
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        for (int i = 0; i < commandApdu.length; i++) {
            Log.d(TAG, Integer.toHexString(commandApdu[i] & 0xff));
        }
        switch (mCardStatus) {
            case INIT:
                Log.d(TAG, "init");
                mCardStatus = CardStatus.CCFILE;
                return SUCCESS;

            case CCFILE:
                Log.d(TAG, "ccfile");
                mCardStatus = CardStatus.REQ_DATA;
                return SUCCESS;

            case REQ_DATA:
                Log.d(TAG, "reqdata");
                mCardStatus = CardStatus.OPEN;
                return REQ_DATA;

            case OPEN:
                Log.d(TAG, "open");
                mCardStatus = CardStatus.LEN;
                return SUCCESS;

            case LEN:
                Log.d(TAG, "len");
                mCardStatus = CardStatus.RET_DATA;
                return LEN;

            case RET_DATA:
                Log.d(TAG, "retdata");
                mCardStatus = CardStatus.INIT;
                return RET_DATA;

            default:
                return SUCCESS;
        }
    }
}
