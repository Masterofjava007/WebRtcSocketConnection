package com.example.socketconnectionwebrtc.Model;

public class sdpAnswer<T> {
    private String roomId;
    private String userName;
    T answer;

    public sdpAnswer(String roomId, String userName, T answer) {
        this.roomId = roomId;
        this.userName = userName;
        this.answer = answer;

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

    public T getanswer() {
        return answer;
    }

    public void setSdp(T sdp) {
        this.answer = sdp;
}
}
