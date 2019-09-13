package com.example.socketconnectionwebrtc.EventHandler;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;

public interface IEventListener {
    void execute(String payload);
}
