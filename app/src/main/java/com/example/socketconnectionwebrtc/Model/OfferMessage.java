package com.example.socketconnectionwebrtc.Model;

import org.webrtc.SessionDescription;


public class OfferMessage {
    SessionDescription sdp;
    String type;


    public OfferMessage(SessionDescription sdp, String type) {
        this.sdp = sdp;
        this.type = type;
    }

    public SessionDescription getSdp() {
        return sdp;
    }

    public void setSdp(SessionDescription sdp) {
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
