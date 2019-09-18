package com.example.socketconnectionwebrtc.EventHandler;

public interface SocketInterface {
    void onOpen();
    void onMessageRecived(String message);
    void onMessageSending(String messsage);
}
