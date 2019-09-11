package com.example.socketconnectionwebrtc;

import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;

public interface SendMessageNotifier {

    void sendCallInitiator(BaseMessage<RoomDetails> sendCallinitator);

}
