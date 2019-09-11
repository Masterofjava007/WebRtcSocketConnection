package com.example.socketconnectionwebrtc.EventHandler;

import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;

public interface IEventStarter {

    void notifierInfinitiCall(BaseMessageHandler<InitiaeCallMessage> initiateCallMessage);

}
