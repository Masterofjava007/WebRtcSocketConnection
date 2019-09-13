package com.example.socketconnectionwebrtc.EventHandler;


import android.util.Log;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Repos.RepoMessageHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static com.example.socketconnectionwebrtc.Enum.MessageType.initiateCall;

public class EventHandler {
    RepoMessageHandler repoMessageHandler;
    Gson gson = new Gson();
    private static final String TAG = "EventHandler";
    private Map<MessageType, IEventListener> decisionMaker = new ConcurrentHashMap<>();


    private String gettingType;


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
            repoMessageHandler.getLiveData(unCoverMessage.getPayload().getName());

        } else {
            Log.d(TAG, "onCoverMessage: HVAD SÅ");
        }
    }


}
