package com.example.appcovid.model;

public class DeviceItem {

    private String address;
    private boolean connected;

    public DeviceItem(String address, String connected){
        this.address = address;
        if (connected == "true") {
            this.connected = true;
        }
        else {
            this.connected = false;
        }
    }

    public boolean getConnected() {
        return connected;
    }

    public String getAddress() {
        return address;
    }
}
