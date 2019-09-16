package com.example.socketconnectionwebrtc.BootStrap;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socketconnectionwebrtc.Repos.RepoMessageHandler;

public class MyViewModel extends AndroidViewModel {
    private RepoMessageHandler repoMessageHandler;
    private LiveData liveData;


    public MyViewModel(@NonNull Application application) {
        super(application);
        repoMessageHandler = new RepoMessageHandler(application);

        liveData = getAllLiveData();

    }

    public LiveData getAllLiveData() {
        return liveData;
    }

}








