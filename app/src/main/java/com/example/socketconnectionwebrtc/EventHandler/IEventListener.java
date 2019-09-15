package com.example.socketconnectionwebrtc.EventHandler;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;

public interface IEventListener<P> {

    @NonNull
    Dialog onCreateDialog(@NonNull Bundle savedInstanceState, BaseMessageHandler iniateCall);

    void execute(BaseMessageHandler<InitiaeCallMessage> initiaeCallMessageBaseMessage);
}
