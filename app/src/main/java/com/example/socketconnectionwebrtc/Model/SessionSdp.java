package com.example.socketconnectionwebrtc.Model;

import org.webrtc.SessionDescription;

public class SessionSdp {

String type;
    String sdp;

    public SessionSdp(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

}
