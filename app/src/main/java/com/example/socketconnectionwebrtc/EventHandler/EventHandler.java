package com.example.socketconnectionwebrtc.EventHandler;


import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Repos.RepoMessageHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class EventHandler implements IEventListener {
    private LiveData<List> liveData;
    RepoMessageHandler repoMessageHandler = new RepoMessageHandler();
    Gson gson = new Gson();

    private static final String TAG = "EventHandler";

    public void notifierInfinitiCall(String message) {
        Log.d(TAG, "onCoverMessage: WAS");


        System.out.println(message);
        BaseMessageHandler<InitiaeCallMessage> unCoverMessage = gson.fromJson
                (message, new TypeToken<BaseMessageHandler<InitiaeCallMessage>>
                        () {
                }.getType());
        MessageType[] decideMessage = MessageType.values();
        for (MessageType messageType : decideMessage) {


            switch (messageType) {
                case initiateCall:
                    Log.d(TAG, "notifierInfinitiCall: Virker");
                    observe(unCoverMessage);
                    //repoMessageHandler.setLiveData(unCoverMessage.getPayload().getName());

                    break;
                case offer:
                    break;
                case acceptCall:
                    break;
                case createRoom:
                    break;
                case dismissCall:
                    break;
                default:
                    Log.d(TAG, "notifierInfinitiCall: ajaj");

            }
        }
/*
        if (initiateCall.equals(gettingType)) {
            Log.d(TAG, "notifierInfinitiCall: The one Before");
            repoMessageHandler.setLiveData(initiateCall, unCoverMessage.getPayload().getName());
        }
        if (gettingType.equals(String.valueOf(initiateCall))) {
            Log.d(TAG, "onCoverMessage: Rammer vi her?");
            repoMessageHandler.setLiveData(initiateCall, unCoverMessage.getPayload().getName());


        } else {
            Log.d(TAG, "onCoverMessage: HVAD SÅ");
        }
    }
*/
    }

    @Override
    public LiveData<String> observe(BaseMessageHandler<InitiaeCallMessage> unCoverMessage) {
        return null;
    }
}
