package com.example.socketconnectionwebrtc.SocketConnection;
/*
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.socketconnectionwebrtc.EventHandler.EventHandler;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class SocketConnectionHandler {
    private static final String TAG = "SocketConnectionHandler";

    private FirebaseAuth auth;
    EventHandler eventHandler = new EventHandler();

    public SocketConnectionHandler() {
    }

    //pass callback in parameters
    public void socketConnect() throws IOException {

        WebSocketFactory factory = new WebSocketFactory();
        Log.d(TAG, "run: InSide try");
        final WebSocket wss = factory.createSocket("wss://firstlineconnect.com:1338");
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "connectSocket: Entered connectSocket Method");
                try {
                    Log.d(TAG, "run: ");
                    Log.d(TAG, "run: Connected");
                    wss.addListener(new WebSocketAdapter() {

                        @Override
                        public void onTextMessage(WebSocket webSocket, String message) throws Exception {

                            Log.d(TAG, "onTextMessage: Message from socket: " + " -- " + message);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    eventHandler.notifierInfinitiCall(message);
                                }
                            });
                            eventHandler.notifierInfinitiCall(message);


                        }

                        @Override
                        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {

                            Log.d(TAG, "onError: Message from socket: " + cause);
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            Log.d(TAG, "onConnectError: Message From Socket" + exception);
                        }

                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {

                            Log.d(TAG, "onConnected: On Connected");
                            Gson gson = new Gson();
                            BaseMessage base = new BaseMessage(MessageType.createRoom, new RoomDetails("+4529933087", "Steffen"));
                            String json = gson.toJson(base);
                            Log.d(TAG, "onConnected: Connected!!  ");
                            wss.sendText(json);


                            Log.d(TAG, "onConnected: works");


                        }
                    });
                    wss.connect();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


}
*/



