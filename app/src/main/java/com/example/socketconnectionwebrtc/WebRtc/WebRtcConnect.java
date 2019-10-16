package com.example.socketconnectionwebrtc.WebRtc;

import android.net.IpSecManager;
import android.util.Log;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebRtcConnect implements WebRtcInterface {
    private static final int DEFAULT_PORT  = 8080;
    private static final String TAG = "WebRtcConnect";
    private final ExecutorService executorService;
    private final SignalingParameters connectionParameteres;
    private ConnectionState roomState;
    private MainActivity mainActivity;
    public WebRtcConnect( SignalingParameters connectionParameteres) {
        this.connectionParameteres = connectionParameteres;
        this.executorService = Executors.newSingleThreadExecutor();
        roomState = ConnectionState.NEW;

    }

public interface onSending{
        void sendMessageString();
        void sendMessageJson();
}

    public void sendMessage(String message){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                MainActivity mainActivity = new MainActivity();
                mainActivity.sendMesssage(message);
            }
        });

    }


    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (roomState != ConnectionState.CONNECTIED){
                    Log.d(TAG, "run: SendOfferSdp is not connected");
                    return;
                }
                JsonObject json = new JsonObject();
                json.addProperty("sdp", sdp.description);
                json.addProperty("type", "answer");
                sendMessage(json.toString());
            }
        });

    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        Log.d(TAG, "sendAnswerSdp: Hallo!");
        executorService.execute(() ->{
            JSONObject json = new JSONObject();
             jsonPut(json,"sdp", sdp.description);
             jsonPut(json, "type", "answer");
            mainActivity.sendSdp(json);

        });
    }
    private static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void sendLocalIceandidate(IceCandidate candidate) {

    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {

    }

    @Override
    public void disconnectFromRoom() {

    }
}
