package com.example.socketconnectionwebrtc.Repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RepoMessageHandler {
   public LiveData<String> liveData;



    private static final String TAG = "RepoMessageHandler";

    public RepoMessageHandler() {
    }

    public RepoMessageHandler(Application application) {

    }

    public LiveData<String> getLiveData() {
        return liveData;
    }

    public void setLiveData(LiveData<String> liveData) {
        this.liveData = liveData;
    }
}

