package com.example.socketconnectionwebrtc.BootStrap;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface;

import org.json.JSONObject;
import org.webrtc.IceCandidate;


public class MyViewModel extends ViewModel  {

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: onCleared");
    }
    public MutableLiveData<JSONObject> iceCandidate;
    public MutableLiveData<String> eventMessage;
    public MutableLiveData<WebRtcInterface.SignalingParameters> messageToWebRTC;

    public MyViewModel() {
        this.iceCandidate = new MutableLiveData<>();
        this.messageToWebRTC = new MutableLiveData<>();
        this.eventMessage = new MutableLiveData<>();
    }

    public LiveData<WebRtcInterface.SignalingParameters> getMessageToWebRTC () {
        if (messageToWebRTC == null) {
            messageToWebRTC = new MutableLiveData<>();
        }
        Log.d(TAG, "getMessageToWebRTC: Sending MessageToMain");
        return messageToWebRTC;
    }

    private static final String TAG =
            "MyViewModel";
    public LiveData<JSONObject> getIceCandidate() {
        return iceCandidate;
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

    public void sendingIceCandidate(JSONObject jsonIceCandidate) {
       iceCandidate.postValue(jsonIceCandidate);
    }
}









