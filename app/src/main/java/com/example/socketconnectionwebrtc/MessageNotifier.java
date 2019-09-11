package com.example.socketconnectionwebrtc;

import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;

public interface MessageNotifier {

    void notifierInfinitiCall(BaseMessageHandler<InitiaeCallMessage> initiateCallMessage);
    void notifierOffer(BaseMessageHandler<OfferMessage> offerMessageBaseMessageHandler);


}
