package com.example.socketconnectionwebrtc.Model;

import org.webrtc.SessionDescription;

public class SessionSdp<T> {
    String type;
    T RoomDetails;
    SessionDescription sdp;

    public SessionSdp(String type, T roomDetails, SessionDescription sdp) {
        this.type = type;
        RoomDetails = roomDetails;
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getRoomDetails() {
        return RoomDetails;
    }

    public void setRoomDetails(T roomDetails) {
        RoomDetails = roomDetails;
    }

    public SessionDescription getSdp() {
        return sdp;
    }

    public void setSdp(SessionDescription sdp) {
        this.sdp = sdp;
    }
}
