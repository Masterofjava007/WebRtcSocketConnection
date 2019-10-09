package com.example.socketconnectionwebrtc.WebRtc;


import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import static com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface.*;


public class WebRtcClient implements WebRtcInterface {
    private static final String TAG = "WebRtcClient";
    private MediaConstraints pcConstraints = new MediaConstraints();
    @Nullable
    private PeerConnectionClient peerConnectionClient;
    private final static int MAX_PEER = 2;


    private boolean[] endPoints = new boolean[MAX_PEER];


    private static String HOST_DOMAIN = "firstlineconnect.com";
    private static String SignalingServerHost = "wss://:1338";

    private boolean initiator;
    private SignalingEvents events;
    private ConnectionState roomState;
    private RoomConnectionParameters connectionParameters;
    private String messageUrl;
    private String leaveUrl;
    private Gson gson = new Gson();


    private void signalingParametersReady(final SignalingParameters signalingParameters) {
        Log.d(TAG, "Room connection completed.");
        if (connectionParameters.loopback
                && (!signalingParameters.initiator || signalingParameters.offerSdp != null)) {

            return;
        }
        if (!connectionParameters.loopback && !signalingParameters.initiator
                && signalingParameters.offerSdp == null) {
            Log.w(TAG, "No offer SDP in room response.");
        }
        initiator = signalingParameters.initiator;
        Log.d(TAG, "Message URL: " + messageUrl);
        Log.d(TAG, "Leave URL: " + leaveUrl);


        // Fire connection and signaling parameters events.
        events.onConnectedToRoom(signalingParameters);
    }


    private static IceCandidate toJavaCandidate(JsonObject jsonObject) throws JSONException {
        return new IceCandidate(jsonObject.get("name").toString(), jsonObject.get("label").getAsInt(), jsonObject.get("candidate").toString());
    }

    private static JSONObject toJsonCandidate(final IceCandidate candidate) {
        JSONObject json = new JSONObject();
        jsonPut(json, "id", candidate.sdpMid);
        jsonPut(json, "candidate", candidate.sdp);
        return json;
    }

    private static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    @Override
    public void sendOfferSdp(SessionDescription sdp) {

        JSONObject json = new JSONObject();
        jsonPut(json, "payload", sdp.description);
        jsonPut(json, "type", "offer");
        if (connectionParameters.loopback) {
            // In loopback mode rename this offer to answer and route it back.
            SessionDescription sdpAnswer = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm("answer"), sdp.description);
            events.onRemoteDescription(sdpAnswer);
        }

    }


    @Override
    public void sendAnswerSdp(SessionDescription sdp) {

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




    //TODO See if we capture frame from main


    //TODO Make this called from main

}

