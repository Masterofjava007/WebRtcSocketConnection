package com.example.socketconnectionwebrtc.BootStrap;

import android.graphics.LightingColorFilter;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface;


public class MyViewModel extends ViewModel  {

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: onCleared");
    }
    public MutableLiveData<String> joinRoomMessage;
    public MutableLiveData<String> eventMessage;
    public MutableLiveData<WebRtcInterface.SignalingParameters> messageToWebRTC;
    public MyViewModel() {
        this.joinRoomMessage = new MutableLiveData<>();
        this.messageToWebRTC = new MutableLiveData<WebRtcInterface.SignalingParameters>();
        this.eventMessage = new MutableLiveData<>();
    }

    public LiveData<WebRtcInterface.SignalingParameters> getMessageToWebRTC () {
        if (messageToWebRTC == null) {
            messageToWebRTC = new MutableLiveData<WebRtcInterface.SignalingParameters>();
        }
        Log.d(TAG, "getMessageToWebRTC: Sending MessageToMain");
        return messageToWebRTC;
    }

    private static final String TAG =
            "MyViewModel";
    public LiveData<String> getJoinRoomMessage() {
        return joinRoomMessage;
    }

    public LiveData<String> getEventMessage() {
        if (eventMessage == null) {
            eventMessage = new MutableLiveData<String>();

        }
        Log.d(TAG, "getMessageToUI: Sending Message");
        System.out.println(eventMessage.getValue());

        return eventMessage;
    }


    public void sendingInitCallMessage(String messageFromEventHandler) {
        Log.d(TAG, "sendingInitCallMessage: enter sendingInitCallMessage");
        eventMessage.postValue(messageFromEventHandler);

    }
    public void sendingMessageToWebRTC (WebRtcInterface.SignalingParameters messagetoWebRtc){
        messageToWebRTC.postValue(messagetoWebRtc);
    }
    public void sendingJoinRoomMessage(String messageJoinRoom){

    }

}









