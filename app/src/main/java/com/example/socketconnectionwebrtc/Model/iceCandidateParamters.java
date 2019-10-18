package com.example.socketconnectionwebrtc.Model;

public class iceCandidateParamters<T> {
    String candidate;
    T sdpMLineIndex;
    String sdpMid;

    public iceCandidateParamters(String candidate, T sdpMLineIndex, String sdpMid) {
        this.candidate = candidate;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpMid = sdpMid;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public T getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(T sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }
}
