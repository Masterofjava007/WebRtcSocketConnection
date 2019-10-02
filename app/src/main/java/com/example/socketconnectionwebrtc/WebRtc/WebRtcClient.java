package com.example.socketconnectionwebrtc.WebRtc;


import android.util.Log;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Enum.WebRTCEnums;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;

import static com.example.socketconnectionwebrtc.Enum.WebRTCEnums.offer;
import static com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface.*;


public class WebRtcClient implements WebRtcInterface.sendingMessage{
    private static final String TAG = "WebRtcClient";
    private MediaConstraints pcConstraints = new MediaConstraints();
    private PeerConnectionClient peerConnectionClient;
    private final static int MAX_PEER = 2;


    private boolean[] endPoints = new boolean[MAX_PEER];


    private static String HOST_DOMAIN = "firstlineconnect.com";
    private static String SignalingServerHost = "wss://:1338";

    private Gson gson = new Gson();
    private RoomConnectionParameters connectionParameters;


    public void decider(String message) {

        Log.d(TAG, "decider: " + message);


        BaseMessageHandler<OfferMessage> unCoverMessageToWebRTC = gson.fromJson
                (message, new TypeToken<BaseMessageHandler<OfferMessage>>() {
                }.getType());
        Log.d(TAG, "decider: " + unCoverMessageToWebRTC);
        String messageType = unCoverMessageToWebRTC.getPayload().getType();
        Log.d(TAG, "decider: " + messageType);
        MessageType webRTCEnums = MessageType.valueOf(messageType);

        switch (webRTCEnums) {


            case offer:
                Log.d(TAG, "decider: Rammer vi her?");

                SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(unCoverMessageToWebRTC.getPayload().getType()), unCoverMessageToWebRTC.getPayload().getSdp());
                Log.d(TAG, "decider: This is the SDP " + sdp);

                System.out.println(sdp);
                sendSdp(sdp);


                break;
            case candidate:
                // signalingEvents.onRemoteIceCandidate(toJavaCandidate(newJson));
                break;
            case answer:
                //SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(message), newJson.get("sdp").toString());
                //signalingEvents.onRemoteDescription(sdp);
                break;

        }
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
    public void sendSdp(SessionDescription offerMessage) {

    }

    public interface RtcListener {
        void onCallReady(String callId);

        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onRemoveRemoteStream(int endPoint);
    }

    private interface Command {
        void execute(String peerid, JsonObject jsonPayload) throws JSONException;

    }

    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);
        // socketConnectionHandler.sendMessageToSocket("eventMessage", message);
    }



    //TODO See if we capture frame from main


    //TODO Make this called from main

}

