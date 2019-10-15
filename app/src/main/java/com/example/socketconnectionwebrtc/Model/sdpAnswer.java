package com.example.socketconnectionwebrtc.Model;

import com.example.socketconnectionwebrtc.Enum.MessageType;

import org.webrtc.SessionDescription;

public class sdpAnswer<T, E> {
    private String type;
    T payload;
    E answer;

    public sdpAnswer(String type, T payload, E answer) {
        this.type = type;
        this.payload = payload;
        this.answer = answer;
    }

    public sdpAnswer(MessageType answer, RoomDetails steffen, SessionDescription sdp) {
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

    public E getAnswer() {
        return answer;
    }

    public void setAnswer(E answer) {
        this.answer = answer;
    }
}
