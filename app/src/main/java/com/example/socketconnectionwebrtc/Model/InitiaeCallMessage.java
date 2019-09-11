package com.example.socketconnectionwebrtc.Model;

public class InitiaeCallMessage {
    String name;

    public InitiaeCallMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
