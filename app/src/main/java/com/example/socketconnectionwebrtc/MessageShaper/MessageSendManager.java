package com.example.socketconnectionwebrtc.MessageShaper;

import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.SendMessageNotifier;
import com.google.gson.Gson;

public class MessageSendManager implements SendMessageNotifier {
    private static final String TAG = "MessageSendManager";


    @Override
    public void sendCallInitiator(BaseMessage<RoomDetails> sendCallinitator) {

    }
}
