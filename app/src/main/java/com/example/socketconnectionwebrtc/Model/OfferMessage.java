package com.example.socketconnectionwebrtc.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.webrtc.SessionDescription;


public class OfferMessage {
    String type;
    String sdp;



    public OfferMessage(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }
}
