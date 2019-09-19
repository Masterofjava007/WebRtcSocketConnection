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

    public MutableLiveData<String> message;

    public MyViewModel() {

        this.message = new MutableLiveData<>();
    }

    private static final String TAG =
            "MyViewModel";

    public LiveData<String> getMessage() {
        if (message == null) {
            message = new MutableLiveData<String>();

        }
        Log.d(TAG, "getMessageToUI: Sending Message");
        System.out.println(message.getValue());

        return message;
    }

    public void sendingMessage(String unCoverMessage) {
        Log.d(TAG, "sendingMessage: enter sendingMessage");
        message.postValue(unCoverMessage);

    }
}









