package com.example.socketconnectionwebrtc.SocketConnection;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;

import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;


public class WebRtcClient implements SdpObserver {
    private static final String TAG = "WebRtcClient";
    final PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
    // final PeerConnectionFactory pFactory;
    Gson gson = new Gson();
    SessionDescription sessionDescriptio;
    private PeerConnection peerConnection;
    private PeerConnection localPeer;
    private PeerConnectionFactory peerConnectionFactory;
    private WebSocket webSocket;
    private LinearLayout views_container;
    private AudioTrack localAudioTrack;
    private VideoTrack localVideoTrack;
    private static String HOST_DOMAIN = "firstlineconnect.com";


    private SurfaceViewRenderer localVideoView;
    private VideoCapturer videoGrabberAndroid;


    public void mapPayloadToSession(String payloadMessage) {
        Log.d(TAG, "mapPayloadToSession: Rammer vi?" + payloadMessage);

        BaseMessageHandler<OfferMessage> unCoverMessage = gson.fromJson
                (payloadMessage, new TypeToken<BaseMessageHandler<OfferMessage>>
                        () {
                }.getType());

        String messageType = unCoverMessage.getType();

        MessageType messageTypeEnum = MessageType.valueOf(messageType);

        // SessionDescription sessionDescription =  new SessionDescription(messageTypeEnum, payload);
        // Log.d(TAG, "mapPayloadToSession: Getting payload" + payload);

        Log.d(TAG, "mapPayloadToSession: Payload" + payloadMessage);
        peerConnection.setLocalDescription(this, unCoverMessage.getPayload().getSdp());
    }

    public WebRtcClient() {
        // pFactory = new PeerConnectionFactory(options);
    }


    private void createPeerConnection() {


        List<PeerConnection.IceServer> iceServers = new ArrayList<>();

        // Create peer connection
        PeerConnection.IceServer stun = PeerConnection.IceServer.builder("stun:(firstlineconnect.com)").createIceServer();
        PeerConnection.IceServer turn = PeerConnection.IceServer.builder("turn:(firstlineconnect.com").setUsername("u").setPassword("p").createIceServer();

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
    }

    private void createLocalPeerConnection(MediaConstraints sdpConstraints) {

        final List<PeerConnection.IceServer> iceServers = new ArrayList<>();

        PeerConnection.IceServer iceServer = new PeerConnection.IceServer(HOST_DOMAIN);
        iceServers.add(iceServer);

        //localPeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints )


    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(TAG, "onCreateSuccess: onCreateSuccess");

        // sessionDescription.type == SessionDescription.Type.OFFER;
        // here you'll get the answerSDP

    }

    @Override
    public void onSetSuccess() {
        MediaConstraints constraints = new MediaConstraints();
        Log.d(TAG, "onSetSuccess: onSuccess");
        peerConnection.createAnswer(this, constraints);
    }


    @Override
    public void onCreateFailure(String s) {
        Log.d(TAG, "onCreateFailure: onCreatefailure");
    }

    @Override
    public void onSetFailure(String s) {
        Log.d(TAG, "onSetFailure: onSetFailure");
    }
}
