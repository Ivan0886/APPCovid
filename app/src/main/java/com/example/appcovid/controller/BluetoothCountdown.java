package com.example.appcovid.controller;

import android.os.CountDownTimer;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

public class BluetoothCountdown extends CountDownTimer {
    private static final String TAG = "BluetoothCountdown";
    private int contador;
    private String mDeviceMac;
    private String mOwnMac;
    private DatabaseReference mRef;
    private String mDeviceName;
    private boolean mCounting;
    public BluetoothCountdown(String deviceMac, String ownMac, DatabaseReference ref, int milisInFuture, int countDownInterval, String deviceName) {
        super(milisInFuture, countDownInterval);
        mDeviceMac = deviceMac;
        mOwnMac = ownMac;
        mRef = ref;
        mDeviceName = deviceName;
        mCounting = true;
        contador = 1;
    }
    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick: Contando: " + mDeviceMac +  " " + mDeviceName + " " + contador++);

    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish: Se acabo " + this.mDeviceName);
        mRef.child(mOwnMac).child(mDeviceMac).setValue(mDeviceName);
        this.cancelCounting();
    }
        
    public void cancelCounting() {
        if (mCounting) {
            Log.d("CANCEL", "Cancelando: " + this.getmDeviceName() + " " + this.getmDeviceMac());
            this.cancel();

        }else {
            Log.d("CANCELLED", "cancelCounting: Ya esta cancelado wey");
        }
    }

    public String getmDeviceMac() {
        return mDeviceMac;
    }

    public String getmDeviceName() {
        return mDeviceName;
    }

    public boolean ismCounting() {
        return mCounting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothCountdown that = (BluetoothCountdown) o;
        return mDeviceMac.equals(that.mDeviceMac);
    }

    @Override
    public int hashCode() {
        return mDeviceMac.hashCode();
    }
}
