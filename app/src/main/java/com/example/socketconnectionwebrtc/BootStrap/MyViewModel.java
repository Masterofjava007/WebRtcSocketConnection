package com.example.socketconnectionwebrtc.BootStrap;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Repos.RepoMessageHandler;

public class MyViewModel extends AndroidViewModel {
    private RepoMessageHandler repoMessageHandler;
    private LiveData<BaseMessageHandler<InitiaeCallMessage>> allInfo;



    private static final String TAG = "ViewModel";

    public MyViewModel(@NonNull Application application) {
        super(application);
        repoMessageHandler = new RepoMessageHandler(application);
        allInfo = repoMessageHandler.getLiveData();
    }

    public LiveData<BaseMessageHandler<InitiaeCallMessage>> getAllInfo() {
        return getAllInfo();
    }




    }

