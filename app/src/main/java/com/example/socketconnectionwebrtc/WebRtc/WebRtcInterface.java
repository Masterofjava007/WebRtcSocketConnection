package com.example.socketconnectionwebrtc.WebRtc;

import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.List;

public interface WebRtcInterface {


    class RoomConnectionParameters {

        public final String roomId;
        public final boolean loopback;
        public final String urlParamteres;

        public RoomConnectionParameters(String roomId, boolean loopback, String urlParamteres) {
            this.roomId = roomId;
            this.loopback = loopback;
            this.urlParamteres = urlParamteres;
        }

        public RoomConnectionParameters(String roomId, boolean loopback) {
            this(roomId, loopback, null);

        }
    }

    void sendOfferSdp(final SessionDescription sdp);

    void sendAnswerSdp(final SessionDescription sdp);

    void sendLocalIceandidate(final IceCandidate candidate);

    void sendLocalIceCandidateRemovals(final IceCandidate[] candidates);

    void disconnectFromRoom();


    class SignalingParameters {
        public final List<PeerConnection.IceServer> iceServer;
        public final boolean initiator;
        public final String clientId;
        public final SessionDescription offerSdp;
        public final List<IceCandidate> iceCandidates;

        public SignalingParameters(List<PeerConnection.IceServer> iceServer, boolean initiator,
                                   String clientId,
                                   SessionDescription offerSdp, List<IceCandidate> iceCandidates) {
            this.iceServer = iceServer;
            this.initiator = initiator;
            this.clientId = clientId;
            this.offerSdp = offerSdp;
            this.iceCandidates = iceCandidates;
        }
    }


    interface SignalingEvents {



        void onRemoteIceCandidate(final IceCandidate candidate);

        void onRemoteIceCandidateRemoved(final IceCandidate[] candidates);

        void onChannelClose();

        void onChannelError(final String description);

    }
}
