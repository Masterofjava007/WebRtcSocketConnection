package com.example.socketconnectionwebrtc.BootStrap;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.EventHandler.IEventListener;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Repos.RepoMessageHandler;

public class ViewModel extends AndroidViewModel {
    private RepoMessageHandler repoMessageHandler;
    public LiveData<String> liveData;

    private static final String TAG = "ViewModel";
    public ViewModel(@NonNull Application application) {
        super(application);







    }
}
