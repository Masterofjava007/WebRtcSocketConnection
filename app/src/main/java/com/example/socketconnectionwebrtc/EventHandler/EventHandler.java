package com.example.socketconnectionwebrtc.EventHandler;


import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.socketconnectionwebrtc.BootStrap.MyViewModel;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Model.OfferMessage;

import com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.ArrayList;



public class EventHandler {
    private AppCompatActivity mActivity;
    private String stringType, stringPayload;


    private static final String stunServer = "stun:firstlineconnect.com";
    private static final String turnServer = "turn:firstlineconnect.com";
    public EventHandler(AppCompatActivity activity) {
        mActivity = activity;
        myViewModel = ViewModelProviders.of(activity).get(MyViewModel.class);
    }

    Gson gson = new Gson();

    private MyViewModel myViewModel;

    private static final String TAG = "EventHandler";

    public void messageHandler(String message) {
        Log.d(TAG, "messageHandler: Entered messageHandler");

        BaseMessageHandler<InitiaeCallMessage> unCoverMessage = gson.fromJson
                (message, new TypeToken<BaseMessageHandler<InitiaeCallMessage>>
                        () {
                }.getType());

        String messageType = unCoverMessage.getType();

        MessageType messageTypeEnum = MessageType.valueOf(messageType);

        switch (messageTypeEnum) {
            case initiateCall:

                Log.d(TAG, "messageHandler: Entering initiateCall");
                String initiateCallPayload = unCoverMessage.getPayload().getName();
                myViewModel.sendingInitCallMessage(initiateCallPayload);

                break;

            case receiveOffer:
                Log.d(TAG, "messageHandler: Entering receivedOfferCall");

                decider(message);

                Log.d(TAG, "messageHandler: " + unCoverMessage);

                Log.d(TAG, "messageHandler: Do we hit?");

                break;
            case acceptCall:
                Log.d(TAG, "messageHandler: Entering AcceptingCall");
                break;
            case createRoom:
                Log.d(TAG, "messageHandler: Entering createRoom");
                break;
            case dismissCall:
                Log.d(TAG, "messageHandler: Entering dismissCall");
                break;
            case joinedRoomParticipant:
                Log.d(TAG, "messageHandler: Entering joinedRoomParticipant");
                myViewModel.sendingJoinRoomMessage(message);

                break;

            default:
                Log.d(TAG, "messageHandler: Entering default");
        }
    }
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

                        Log.d(TAG, "decider: Inside Offer");



                        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList();
                        iceServers.add(PeerConnection.IceServer.builder(stunServer).createIceServer());
                        iceServers.add(PeerConnection.IceServer.builder(turnServer).setUsername("u").setPassword("p").createIceServer());

                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER, unCoverMessageToWebRTC.getPayload().getSdp());

                        WebRtcInterface.SignalingParameters parameters = new WebRtcInterface.SignalingParameters(
                                iceServers,
                                false,
                                null,
                                sdp,
                                null
                        );
                        myViewModel.sendingMessageToWebRTC(parameters);




                case candidate:
                    Log.d(TAG, "decider: rammer vi her??????");
                    break;
                case answer:
                    //SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(message), newJson.get("sdp").toString());
                    //signalingEvents.onRemoteDescription(sdp);
                    break;
                case joinedRoomParticipant:
                    Log.d(TAG, "decider: JOINEDROOMPARTICIPANT");
            }
        }


}



