package com.example.socketconnectionwebrtc.Model;

public class OfferMessage {
    String sdp;
    String type;

    public OfferMessage(String sdp, String type) {
        this.sdp = sdp;
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
