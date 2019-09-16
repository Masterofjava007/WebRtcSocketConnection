package com.example.socketconnectionwebrtc.Repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RepoMessageHandler {
    private LiveData<List> liveData;



    private static final String TAG = "RepoMessageHandler";

    public RepoMessageHandler() {
    }

    public RepoMessageHandler(Application application) {

    }

    public LiveData<List> getLiveData() {
        return liveData;
    }

    public void setLiveData(LiveData<List> liveData) {
        this.liveData = liveData;
    }



}

