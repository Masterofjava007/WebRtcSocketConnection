package com.example.socketconnectionwebrtc.EventHandler;


import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.socketconnectionwebrtc.BootStrap.MyViewModel;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.WebRtc.WebRtcClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class EventHandler {
    private AppCompatActivity mActivity;
    private String stringType, stringPayload;

    public EventHandler(AppCompatActivity activity) {
        mActivity = activity;
        myViewModel = ViewModelProviders.of(activity).get(MyViewModel.class);
    }

    Gson gson = new Gson();

    private MyViewModel myViewModel;

    private static final String TAG = "EventHandler";

    public void messageHandler(String message) {
        Log.d(TAG, "messageHandler: Entered messageHandler");

        BaseMessageHandler<InitiaeCallMessage> unCoverMessage = gson.fromJson
                (message, new TypeToken<BaseMessageHandler<InitiaeCallMessage>>
                        () {
                }.getType());

        String messageType = unCoverMessage.getType();

        MessageType messageTypeEnum = MessageType.valueOf(messageType);

        switch (messageTypeEnum) {
            case initiateCall:

                Log.d(TAG, "messageHandler: Entering initiateCall");
                String initiateCallPayload = unCoverMessage.getPayload().getName();
                myViewModel.sendingMessage(initiateCallPayload);

                break;

            case receiveOffer:
                Log.d(TAG, "messageHandler: Entering OfferCall");

                myViewModel.sendingMessageToWebRTC(message);
                Log.d(TAG, "messageHandler: " + unCoverMessage);

                Log.d(TAG, "messageHandler: Do we hit?");

                break;
            case acceptCall:
                Log.d(TAG, "messageHandler: Entering AcceptingCall");
                break;
            case createRoom:
                Log.d(TAG, "messageHandler: Entering createRoom");
                break;
            case dismissCall:
                Log.d(TAG, "messageHandler: Entering dismissCall");
                break;
            case joinedRoomParticipant:
                Log.d(TAG, "messageHandler: Entering joinedRoomParticipant");
               // myViewModel.sendingMessageToWebRTC(message);

                break;
            default:
                Log.d(TAG, "messageHandler: Entering default");


        }
    }
}



