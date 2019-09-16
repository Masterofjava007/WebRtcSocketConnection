package com.example.socketconnectionwebrtc.EventHandler;


import androidx.lifecycle.LiveData;

import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;

public interface IEventListener {


    LiveData<String> observe(BaseMessageHandler<InitiaeCallMessage> unCoverMessage);



}
