package com.example.socketconnectionwebrtc.Model;

public class sendIceCandidate<T> {
    private String roomId;
    private String userName;
    T candidate;

    public sendIceCandidate(String roomId, String userName, T candidate) {
        this.roomId = roomId;
        this.userName = userName;
        this.candidate = candidate;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public T getCandidate() {
        return candidate;
    }

    public void setCandidate(T candidate) {
        this.candidate = candidate;
    }
}
