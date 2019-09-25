package com.example.socketconnectionwebrtc.WebRtc;


import android.util.JsonReader;
import android.util.Log;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLContext;


public class WebRtcClient {
    private static final String TAG = "WebRtcClient";
    private MediaConstraints pcConstraints = new MediaConstraints();
    private HashMap<String, Peer> peers = new HashMap<>();
    private final static int MAX_PEER = 2;
    private MainActivity mainActivity;
    private PeerConnectionParameters pcParams;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionFactory factory;

    private MediaStream localMS;
    private static String HOST_DOMAIN = "firstlineconnect.com";
    private static String SignalingServerHost = "wss://:1338";
    private RtcListener rtcListener;
    private MessageHandler messageHandler;



    public interface RtcListener {
        void onCallReady(String callId);

        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onRemoveRemoteStream(int endPoint);
    }

    private interface Command {
        void execute(String peerid, String jsonPayload) throws JSONException;
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
    private class CreateAnswerCommand implements Command {

        @Override
        public void execute(String peerid, String jsonPayload) throws JSONException {
            Peer peer = peers.get(peerid);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm("offer"), jsonPayload);

            peer.pc.createAnswer(peer, pcConstraints);
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    /*
        private class SetRemoteSDPCommand implements Command {

            @Override
            public void execute(String peerId, JsonObject jsonPayload) throws JSONException {
                Peer peer = peers.get(peerId);
                SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(jsonPayload.getAsString("type")),
                        jsonPayload.get("sdp"));

                peer.pc.setRemoteDescription(peer, sdp);
            }
        }

        private class AddIceCandidate implements Command {

            @Override
            public void execute(String peerId, JsonObject jsonPayload) throws JSONException {
                PeerConnection pc = peers.get(peerId).pc;
                if (pc.getRemoteDescription() != null) {
                    IceCandidate candidate = new IceCandidate(
                            jsonPayload.getString("id"),
                            jsonPayload.getInt("label"),
                            jsonPayload.getString("candidate")
                    );
                    pc.addIceCandidate(candidate);
                }
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

    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;

        public Peer(String id, int endPoint) {

            Log.d(TAG, "Peer: " + this);
            this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;


            pc.addStream(localMS);

            rtcListener.onStatusChanged("CONNECTING");
        }


        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sessionDescription.type.canonicalForm());
                payload.put("sdp", sessionDescription.description);
                //TODO Get this to the main activity -> websocket
                sendMessage(id, sessionDescription.type.canonicalForm(), payload);
                pc.setLocalDescription(Peer.this, sessionDescription);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onSetSuccess() {

        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                removePeer(id);
                rtcListener.onStatusChanged("DISCONNECED");
            }

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {

            try {
                JSONObject payload = new JSONObject();
                payload.put("label", iceCandidate.sdpMLineIndex);
                payload.put("id", iceCandidate.sdpMid);
                payload.put("candidate", iceCandidate.sdp);
                sendMessage(id, "candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d(TAG, "onAddStream: inside onAddStream");
            rtcListener.onAddRemoteStream(mediaStream, endPoint + 1);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "onRemoveStream: Inside Remove Stream");
            removePeer(id);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }


        private Peer addPeer(String id, int endPoint) {
            Peer peer = new Peer(id, endPoint);
            peers.put(id, peer);

            endPoints[endPoint] = true;


            return peer;
        }

        private void removePeer(String id) {
            Peer peer = peers.get(id);
            rtcListener.onRemoveRemoteStream(peer.endPoint);

            peer.pc.close();

            peers.remove(peer.id);

            endPoints[peer.endPoint] = false;
        }


        public void WebRtcClienten(RtcListener listener, String host, PeerConnectionParameters params, EGLContext eglContext) {

            rtcListener = listener;
            pcParams = params;

            PeerConnectionFactory.initializeAndroidGlobals(listener, true, true, params.videoCodecHwAcceleration, eglContext);
            factory = new PeerConnectionFactory();


            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();


            options.networkIgnoreMask = 0;


            /*

            PeerConnection.IceServer stun = PeerConnection.IceServer.builder("stun:(firstlineconnect.com)").createIceServer();
            PeerConnection.IceServer turn = PeerConnection.IceServer.builder("turn:(firstlineconnect.com").setUsername("u").setPassword("p").createIceServer();

            iceServers.add(stun);
            iceServers.add(turn);

            */

            iceServers.add(new PeerConnection.IceServer("stun:firstlineconnect.com"));
            iceServers.add(new PeerConnection.IceServer("turn:firstlineconnect.com"));

            pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
            pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
            pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));


        }

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

    private void createLocalPeerConnection(MediaConstraints sdpConstraints) {

        final List<PeerConnection.IceServer> iceServers = new ArrayList<>();

        PeerConnection.IceServer iceServer = new PeerConnection.IceServer(HOST_DOMAIN);
        iceServers.add(iceServer);

        //localPeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints )


    }

    /*
    //TODO See if we capture frame from main
    public didCaptureFrame(Frame data) {

    }
    */

    public void start(String name) {

        try {
            JSONObject message = new JSONObject();
            message.put("name", name);
            // socketConnectionHandler.sendMessageToSocket("ReadyToStream", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MessageHandler {
        private HashMap<String, Command> commandMap;

        private MessageHandler() {
            this.commandMap = new HashMap<>();
            //commandMap.put("init", new createOffcerCommand());
            commandMap.put("offer", new CreateAnswerCommand());
            //commandMap.put("answer", new SetRemoteSDPCommand());
            //commandMap.put("candidate", new AddIceCandidate());

            //Log.d(TAG, "mapPayloadToSession: Rammer vi?" + payloadMessage);
        }
    }

    //TODO Make this called from main

    public void mapPayloadToSession(String payloadMessage) throws JSONException {
        Gson gson = new Gson();

        OfferMessage offer = new OfferMessage(payloadMessage, "offer");
        Log.d(TAG, "mapPayloadToSession: Rammer vi? " + payloadMessage);
        CreateAnswerCommand createAnswerCommand = new CreateAnswerCommand();




        String JsonString = gson.toJson(payloadMessage);
        Log.d(TAG, "mapPayloadToSession: " + JsonString);

        String passingJsonString = gson.toJson(payloadMessage);

        createAnswerCommand.execute("1",  gson.toJson(offer));

    }
}

