package com.example.socketconnectionwebrtc.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.webrtc.SessionDescription;


public class OfferMessage {
    @SerializedName("sdp")
    @Expose
    String sdp;
    @SerializedName("type")
    @Expose
    String type;


    public OfferMessage(String sdp, String type) {
        this.sdp = sdp;
        this.type = type;
    }

    public OfferMessage() {

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
