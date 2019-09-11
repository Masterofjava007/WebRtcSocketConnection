package com.example.socketconnectionwebrtc.EventHandler;


import android.util.Log;
import android.view.contentcapture.ContentCaptureCondition;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.google.firebase.events.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static com.example.socketconnectionwebrtc.Enum.MessageType.initiateCall;
import static com.example.socketconnectionwebrtc.Enum.MessageType.offer;

public class EventHandler implements INotifier {

    Gson gson = new Gson();
    private static final String TAG = "EventHandler";
    private Map<MessageType, IEventListener> decisionMaker = new ConcurrentHashMap<>();

    private String gettingType;


    @Override
    public void notifierInfinitiCall(String message) {
        Log.d(TAG, "onCoverMessage: WAS");

        System.out.println(message);
        BaseMessageHandler<InitiaeCallMessage> unCoverMessage = gson.fromJson
                (message, new TypeToken<BaseMessageHandler<InitiaeCallMessage>>
                        () {
                }.getType());

        gettingType = unCoverMessage.getType();



        if (gettingType.equals(String.valueOf(initiateCall))) {
            Log.d(TAG, "onCoverMessage: Rammer vi her?");

            Log.d(TAG, "onCoverMessage: Virker det her?");
        } else {
            Log.d(TAG, "onCoverMessage: HVAD SÅ");
        }
    }
}
