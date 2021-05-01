package com.example.appcovid.model;

import org.threeten.bp.LocalDate;

public class DeviceItem {

    private String mAddress;
    private boolean mConnected;
    private LocalDate mFecha;

    public DeviceItem(String mAddress, String mConnected) {
        // this.mFecha = LocalDate.now();
        this.mAddress = mAddress;
        if (mConnected.equals("true")) {
            this.mConnected = true;
        } else {
            this.mConnected = false;
        }
    }

    public boolean getmConnected() {
        return mConnected;
    }

    public String getmAddress() {
        return mAddress;
    }

    public LocalDate getmFecha() {
        return mFecha;
    }
}
