package com.example.socketconnectionwebrtc.BootStrap;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: onCleared");
    }

    public MutableLiveData<String> eventMessage;
    public MutableLiveData<String> messageToWebRTC;
    public MyViewModel() {

        this.messageToWebRTC = new MutableLiveData<>();
        this.eventMessage = new MutableLiveData<>();
    }

    public LiveData<String> getMessageToWebRTC () {
        if (messageToWebRTC == null) {
            messageToWebRTC = new MutableLiveData<String>();
        }
        Log.d(TAG, "getMessageToWebRTC: Sending MessageToMain");
        return messageToWebRTC;
    }

    private static final String TAG =
            "MyViewModel";

    public LiveData<String> getEventMessage() {
        if (eventMessage == null) {
            eventMessage = new MutableLiveData<String>();

        }
        Log.d(TAG, "getMessageToUI: Sending Message");
        System.out.println(eventMessage.getValue());

        return eventMessage;
    }

    public void sendingMessage(String messageFromEventHandler) {
        Log.d(TAG, "sendingMessage: enter sendingMessage");
        eventMessage.postValue(messageFromEventHandler);

    }

}









