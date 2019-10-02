package com.example.socketconnectionwebrtc.Enum;

public enum WebRTCEnums {
    offer,
    init,
    answer,
    candidate;

    private int getValue;

    public int getGetValue() {
        return getValue;
    }
}
