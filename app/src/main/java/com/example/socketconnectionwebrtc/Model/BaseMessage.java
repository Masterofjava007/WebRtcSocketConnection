package com.example.socketconnectionwebrtc.Model;

import com.example.socketconnectionwebrtc.Enum.MessageType;

public class BaseMessage<T> {
    MessageType type;
    T payload;

    public BaseMessage() {

    }

    public MessageType getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }

    public BaseMessage(MessageType type, T payload) {
        this.type = type;
        this.payload = payload;

    }
}













