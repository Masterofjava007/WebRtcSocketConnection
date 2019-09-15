package com.example.socketconnectionwebrtc.Repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;

public class RepoMessageHandler {
    private LiveData<BaseMessageHandler<InitiaeCallMessage>> liveData;
    private String name;

    public RepoMessageHandler(LiveData<BaseMessageHandler<InitiaeCallMessage>> liveData, String name) {
        this.liveData = liveData;
        this.name = name;
    }

    public RepoMessageHandler(Application application) {

    }

    public LiveData<BaseMessageHandler<InitiaeCallMessage>> getLiveData() {
        return liveData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLiveData(String gettingType, String name) {
        this.liveData = liveData;
    }
}
