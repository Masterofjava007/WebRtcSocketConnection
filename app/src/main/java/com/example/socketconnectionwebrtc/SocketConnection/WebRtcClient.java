package com.example.socketconnectionwebrtc.SocketConnection;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.List;

public class WebRtcClient implements SdpObserver {

    final PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
    final PeerConnectionFactory pFactory;
    private PeerConnection peerConnection;

    public WebRtcClient() {
        pFactory = new PeerConnectionFactory(options);
    }

    private void createPeerConnection() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>(new org.webrtc.PeerConnection.IceServer("stun:firstlineconnect.com:1337"));

        SessionDescription receivedSDP; //decode this from the received offer

        peerConnection.setRemoteDescription(this, receivedSDP);
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        //here you'll get the answerSDP
        peerConnection.setLocalDescription(this, sessionDescription);
    }

    @Override
    public void onSetSuccess() {
        MediaConstraints constraints = new MediaConstraints();
        peerConnection.createAnswer(this, constraints);
    }

    @Override
    public void onCreateFailure(String s) {

    }

    @Override
    public void onSetFailure(String s) {

    }
}
