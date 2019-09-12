package com.example.socketconnectionwebrtc.EventHandler;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.google.firebase.events.Event;

public interface IEventHandler {


    void register(MessageType type, BaseMessageHandler baseMessageHandler);
    void notify(MessageType type, BaseMessageHandler<InitiaeCallMessage> initiaeCallMessageBaseMessage);
    void notifierInfinitiCall(String message);
/*
    void notifierInfinitiCall(MessageType type, IEventHandler listener);
    //void notifierInfinitiCall(BaseMessageHandler<InitiaeCallMessage> initiateCallMessage);
    void notifierOffer(BaseMessageHandler<OfferMessage> offerMessageBaseMessageHandler);
    void sendCallInitiator(BaseMessage<RoomDetails> sendCallinitator);
*/
}
