package com.example.socketconnectionwebrtc.WebRtc;

import android.content.Context;
import android.opengl.EGLContext;
import android.util.Log;

import androidx.annotation.Nullable;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PeerConnectionClient {
    private final PCObserver pcObserver = new PCObserver();
    private final SDPObserver sdpObserver = new SDPObserver();
    private final EGLContext eglContext;
    private final Context appContext;
    private final PeerConnectionParameters peerConnectionParameters;
    private final PeerConnectionEvents events;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final boolean dataChannelEnabled;
    private boolean isError;
    @Nullable
    private PeerConnectionFactory factory;
    @Nullable
    private PeerConnection peerConnection;
    @Nullable
    private VideoSource videoSource;
    @Nullable
    private SessionDescription localSdp;
    @Nullable
    private VideoTrack localVideoTrack;
    @Nullable
    private VideoTrack remoteVideoTrack;
    @Nullable
    private List<IceCandidate> queuedIceCandidates;
    private MediaConstraints audioConstraints;
    private MediaConstraints sdpMediaConstraints;
    private boolean preferIsac;

    public static class PeerConnectionParameters {
        public final boolean videoCallEnabled;
        public final boolean loopback;
        public final boolean tracing;
        public final int videoWidth;
        public final int videoHeight;
        public final int videoFps;
        public final int videoMaxBitrate;
        public final String videoCodec;
        public final boolean videoCodecHwAcceleration;
        public final boolean videoFlexfecEnabled;
        public final int audioStartBitrate;
        public final String audioCodec;
        public final boolean noAudioProcessing;
        public final boolean aecDump;
        public final boolean saveInputAudioToFile;
        public final boolean useOpenSLES;
        public final boolean disableBuiltInAEC;
        public final boolean disableBuiltInAGC;
        public final boolean disableBuiltInNS;
        public final boolean disableWebRtcAGCAndHPF;
        public final boolean enableRtcEventLog;
        private final DataChannelParameters dataChannelParameters;

        public PeerConnectionParameters(boolean videoCallEnabled, boolean loopback, boolean tracing, int videoWidth,
                                        int videoHeight, int videoFps, int videoMaxBitrate, String videoCodec, boolean videoCodecHwAcceleration
                , boolean videoFlexfecEnabled, int audioStartBitrate, String audioCodec, boolean noAudioProcessing, boolean aecDump, boolean saveInputAudioToFile
                , boolean useOpenSLES, boolean disableBuiltInAEC, boolean disableBuiltInAGC, boolean disableBuiltInNS, boolean disableWebRtcAGCAndHPF, boolean enableRtcEventLog
                , DataChannelParameters dataChannelParameters) {
            this.videoCallEnabled = videoCallEnabled;
            this.loopback = loopback;
            this.tracing = tracing;
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            this.videoFps = videoFps;
            this.videoMaxBitrate = videoMaxBitrate;
            this.videoCodec = videoCodec;
            this.videoCodecHwAcceleration = videoCodecHwAcceleration;
            this.videoFlexfecEnabled = videoFlexfecEnabled;
            this.audioStartBitrate = audioStartBitrate;
            this.audioCodec = audioCodec;
            this.noAudioProcessing = noAudioProcessing;
            this.aecDump = aecDump;
            this.saveInputAudioToFile = saveInputAudioToFile;
            this.useOpenSLES = useOpenSLES;
            this.disableBuiltInAEC = disableBuiltInAEC;
            this.disableBuiltInAGC = disableBuiltInAGC;
            this.disableBuiltInNS = disableBuiltInNS;
            this.disableWebRtcAGCAndHPF = disableWebRtcAGCAndHPF;
            this.enableRtcEventLog = enableRtcEventLog;
            this.dataChannelParameters = dataChannelParameters;
        }
    }

    //TODO Do something about options almost 100% not working.
    public PeerConnectionClient(Context appContext, EGLContext eglContext, PeerConnectionParameters peerConnectionParameters, PeerConnectionEvents events) {
        this.appContext = appContext;
        this.events = events;
        this.eglContext = eglContext;
        this.peerConnectionParameters = peerConnectionParameters;
        this.dataChannelEnabled = peerConnectionParameters.dataChannelParameters != null;

        final String fieldTrials = getFieldTrials(peerConnectionParameters);
        executor.execute(() -> {
            Log.d(TAG, "PeerConnectionClient: Field Trails" + fieldTrials);
            PeerConnectionFactory.initializeAndroidGlobals(fieldTrials, true, true, peerConnectionParameters.videoCodecHwAcceleration, eglContext);
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        });
    }
    //This should only be called once
    public void createPeerConnectionFactory(PeerConnectionFactory.Options options) {
        if (factory != null) {
            throw new IllegalStateException("PeerConnectionFactory has already been constructed");
        }
        executor.execute(() -> createPeerConnectionFactoryInternal(options));
    }

    private void createPeerConnection(final VideoSink localRender, final VideoSink remoteSink, final VideoCapturer videoCapturer, final WebRtcInterface.SignalingParameters signalingParameters) {


    }
    private void createPeerConnectionFactoryInternal(PeerConnectionFactory.Options options) {
        isError = false;

        if (peerConnectionParameters.tracing) {
            Log.d(TAG, "createPeerConnectionFactoryInternal: først if peeronnectionparamter.tracing");
        }
        preferIsac = peerConnectionParameters.audioCodec != null;

    }



    private static String getFieldTrials(PeerConnectionParameters peerConnectionParameters) {

    }


    public static class DataChannelParameters {
        public final boolean ordered;
        public final int maxRetransmitTimeMs;
        public final int maxRetransmits;
        public final String protocol;
        public final boolean negotiated;
        public final int id;

        public DataChannelParameters(boolean ordered, int maxRetransmitTimeMs, int maxRetransmits,
                                     String protocol, boolean negotiated, int id) {
            this.ordered = ordered;
            this.maxRetransmitTimeMs = maxRetransmitTimeMs;
            this.maxRetransmits = maxRetransmits;
            this.protocol = protocol;
            this.negotiated = negotiated;
            this.id = id;
        }
    }

    public interface PeerConnectionEvents {

    }

    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {

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
    }

    private class PCObserver implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }
    }
}
