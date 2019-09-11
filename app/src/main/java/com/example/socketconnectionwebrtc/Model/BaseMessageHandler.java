package com.example.socketconnectionwebrtc.Model;


public class BaseMessageHandler<T> {
    String type;
    T payload;

    public BaseMessageHandler(String type) {
        this.type = type;
    }

    public BaseMessageHandler(String type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    public BaseMessageHandler(T payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}


