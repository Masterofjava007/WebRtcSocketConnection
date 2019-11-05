package com.example.socketconnectionwebrtc.Model;


import com.example.socketconnectionwebrtc.Enum.MessageType;

public class arrowMessage<T> {
    MessageType type;
    int x;
    int y;

    public arrowMessage() {
    }

    public arrowMessage(MessageType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
