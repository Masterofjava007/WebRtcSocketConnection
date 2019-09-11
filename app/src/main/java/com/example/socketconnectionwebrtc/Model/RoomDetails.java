package com.example.socketconnectionwebrtc.Model;

public class RoomDetails {
    public String roomId;
    public String userName;

    public RoomDetails(String roomId, String userName) {
        this.roomId = roomId;
        this.userName = userName;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUserName() {
        return userName;
    }
}
