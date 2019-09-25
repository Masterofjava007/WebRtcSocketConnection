package com.example.socketconnectionwebrtc.SocketConnection;

/*
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.EventHandler.EventHandler;
import com.example.socketconnectionwebrtc.EventHandler.IEventListener;
import com.example.socketconnectionwebrtc.EventHandler.SocketInterface;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public abstract class OkHttpSocketConnection extends WebSocketListener implements Handler.Callback {
    private static final String TAG = "OkHttpSocketConnection";
    private static final int NORMAL_CLOSURE = 4000;
    private OkHttpClient client = new OkHttpClient();
    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    private WebSocket webSocket;
    Gson gson = new Gson();
    private messagePasserInterface listener;

    private static final String URL_SOCKET_CONNECTION = "wss://firstlineconnect.com:1338";


    public boolean handleMessage(@NonNull String eventMessage) {
        Log.d(TAG, "handleMessage: HandlerMessage -> Okhttp" + eventMessage);
        return false;
    }


    public interface messagePasserInterface {

        void onMessage(WebSocket webSocket, String text);

        @MainThread
        void onMessageRecivedSendMain(String eventMessage);

        void onOpen();
    }


    public void connect()  {
        Log.d(TAG, "connect: Inside Connect");


        webSocket = client.newWebSocket(new Request
                        .Builder()
                        .url(URL_SOCKET_CONNECTION)
                        .build(),
                this);

    }

    public void disconnect() throws IOException {
        if (webSocket != null) {
            webSocket.close(NORMAL_CLOSURE, null);
        }
        listener = new messagePasserInterface() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {

            }

            @Override
            public void onMessageRecivedSendMain(String eventMessage) {

            }

            @Override
            public void onOpen() {
                BaseMessage baseMessage = new BaseMessage(MessageType.createRoom, new RoomDetails("+4529933087", "Steffen"));
                String sendingMessageToSocket = gson.toJson(baseMessage);
                sendMessage(sendingMessageToSocket);

            }
        };
    }

    public void sendMessage(String eventMessage) {
        webSocket.send(eventMessage);
        Log.d(TAG, "sendMessage: Message have been send and room is open waiting for incoming calls");
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "onOpen: Connection Opened");
        BaseMessage baseMessage = new BaseMessage(MessageType.createRoom, new RoomDetails("+4529933087", "Steffen"));
        String sendingMessageToSocket = gson.toJson(baseMessage);
        sendMessage(sendingMessageToSocket);

    }




    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "onMessage: Modtager vi en besked");
        handleMessage(text);


        MAIN_THREAD.post(() -> listener.onMessageRecivedSendMain(text));
    }



}

*/
