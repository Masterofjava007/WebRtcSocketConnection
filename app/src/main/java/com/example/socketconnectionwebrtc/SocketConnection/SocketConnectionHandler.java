package com.example.socketconnectionwebrtc.SocketConnection;

import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socketconnectionwebrtc.EventHandler.EventHandler;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.Model.SessionSdp;
import com.example.socketconnectionwebrtc.Model.sdpAnswer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SocketConnectionHandler {
    private static final String TAG = "SocketConnectionHandler";

    private AppCompatActivity mActivity;
    EventHandler eventHandler;
    final WebSocket wss;
    Gson gson = new Gson();

    public SocketConnectionHandler(AppCompatActivity activity) throws IOException {
        mActivity = activity;
        eventHandler = new EventHandler(mActivity);
        WebSocketFactory factory = new WebSocketFactory();
        wss = factory.createSocket("wss://firstlineconnect.com:1338");
    }

    //pass callback in parameters
    public void socketConnect() throws IOException {


        Log.d(TAG, "run: InSide try");

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
                            eventHandler.messageHandler(message);

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

                            BaseMessage base = new BaseMessage(MessageType.createRoom, new RoomDetails("+4529933087", "Steffen"));
                            sendMessageToSocket(base);

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

    public void sendMessageToSocket(BaseMessage payload) {
        Log.d(TAG, "sendMessageToSocket: Socket on Send");
        Gson gson = new Gson();
        String toJson = gson.toJson(payload);
        if (wss != null) {
            wss.sendText(toJson);
        } else {
            Log.d(TAG, "sendMessageToSocket: dont work");
        }

    }

    public void sendMessageToSocketFromOffer(SessionSdp offerMessage) throws JSONException {
        String toJSon = gson.toJson(offerMessage);
        Log.d(TAG, "sendMessageToSocketFromOffer: " + toJSon);
        wss.sendText(toJSon);

    }


    public void sendMessageToSocket(String message) {
        Log.d(TAG, "sendMessageToSocket: WebRtcClient sending to socket");
        wss.sendText(message);


    }
    public void sendJsonObject(JSONObject jsonObject) {
        Log.d(TAG, "sendJsonObject: " + jsonObject);
        wss.sendText(jsonObject.toString());
    }
}




