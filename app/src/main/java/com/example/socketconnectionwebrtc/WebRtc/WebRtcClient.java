package com.example.socketconnectionwebrtc.WebRtc;


import android.util.Log;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceConfigurationError;


public class WebRtcClient {
    private static final String TAG = "WebRtcClient";
    private MediaConstraints pcConstraints = new MediaConstraints();
    private WebRtcInterface.SignalingEvents signalingEvents;
    private final static int MAX_PEER = 2;
    private MainActivity mainActivity;
    private PeerConnectionParameters pcParams;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionFactory factory;
    private ConnectionState roomState;
    private MediaStream localMS;
    private static String HOST_DOMAIN = "firstlineconnect.com";
    private static String SignalingServerHost = "wss://:1338";
    private RtcListener mListener;
    private Gson gson = new Gson();

    public void decider(String message) {
        try {


            Log.d(TAG, "decider: Hey");

            JsonObject newJson = new JsonObject();
            newJson.get(gson.toJson(message));

            String type = String.valueOf(newJson.get("receiveOffer"));

            Log.d(TAG, "decider: " + message);
            if (type.equals("candidate")) {
                signalingEvents.onRemoteIceCandidate(toJavaCandidate(newJson));

            } else if (type.equals("answer")) {
                SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), newJson.get("sdp").toString());

            } else if (type.equals("offer")) {
                Log.d(TAG, "decider: Rammer vi her?");
                SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), newJson.get("sdp").toString());

                WebRtcInterface.SignalingParameters parameteres = new WebRtcInterface.SignalingParameters(
                        new ArrayList<>(),
                        false,
                        null,
                        null,
                        null,
                        sdp,
                        null
                );
                roomState = ConnectionState.CONNECTIED;
                signalingEvents.onConnectedToRoom(parameteres);
            }

        } catch (Exception e) {
            Log.d(TAG, "decider: " + e);

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

    /*
    private class createOffcerCommand implements Command {

        @Override
        public void execute(String peerId, JsonObject jsonPayload) throws JSONException {
            Log.d(TAG, "execute: Entered createOfferCommand");
            Peer peer = peers.get(peerId);
            peer.pc.createOffer(peer, pcConstraints);
        }
    }
*/

    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);
        // socketConnectionHandler.sendMessageToSocket("eventMessage", message);
    }





        /*
        MediaConstraints constraints = new MediaConstraints();
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        PeerConnectionFactory peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory(iceServers, constraints);
*/
        /*
        final PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        PeerConnectionFactory factory = new PeerConnectionFactory(new PeerConnectionFactory.Options());
        MediaConstraints constraints = new MediaConstraints();
        PeerConnection peerConnection = factory.createPeerConnection(iceServers, constraints, new YourPeerConnectionObserver());
*/
    // SessionDescription receivedSDP; //decode this from the received offer

    //peerConnection.setRemoteDescription(this, receivedSDP);


    //TODO See if we capture frame from main


    //TODO Make this called from main
/*
    public void mapPayloadToSession(String payloadMessage) throws JSONException {

        Gson gson = new Gson();

        OfferMessage offer = new OfferMessage(payloadMessage, "offer");

        Log.d(TAG, "mapPayloadToSession: Rammer vi? " + payloadMessage);

        CreateAnswerCommand createAnswerCommand = new CreateAnswerCommand();

        String JsonString = gson.toJson(payloadMessage);

        Log.d(TAG, "mapPayloadToSession: " + JsonString);

        createAnswerCommand.execute("1", gson.toJson(offer));

    }

 */
}

