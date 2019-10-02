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
import org.webrtc.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConnectionClient {
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "SocketConnectionEchoCancel";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "SocketConnectionControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "SocketConnectionFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "SocketConnectionSuppression";
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;
    private static final String AUDIO_CODEC = "ISAC";
    private static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private final PCObserver pcObserver = new PCObserver();
    private final SDPObserver sdpObserver = new SDPObserver();
    private static final String TAG = "PeerConnectionClient";
    private final Context appContext;
    private final PeerConnectionParameters peerConnectionParameters;
    private final PeerConnectionEvents events;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final boolean dataChannelEnabled;
    private boolean isError;
    private final EglBase rootEglBase;

    private int videoWidth;
    private int videoHeight;
    private int videoFps;
    private boolean isInitiator;
    private WebRtcInterface.SignalingParameters signalingParameters;
    @org.jetbrains.annotations.Nullable
    private VideoCapturer videoCapturer;
    @org.jetbrains.annotations.Nullable
    private DataChannel dataChannel;
    @org.jetbrains.annotations.Nullable
    private SurfaceTextureHelper surfaceTextureHelper;
    @Nullable
    private VideoSink localrender;
    @Nullable
    private List<VideoSink> remoteSinks;
    private boolean renderVideo = true;
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


    public void setRemoteDescription(SessionDescription offerSdp) {
        Log.d(TAG, "setRemoteDescription: SetRemoteDescription");
        executor.execute(() -> {
            if (peerConnection == null || isError) {
                Log.d(TAG, "setRemoteDescription: PeerConnection == null");
                return;
            }
            String sdpDescription = offerSdp.description;
            SessionDescription sdpRemote = new SessionDescription(offerSdp.type, sdpDescription);
            peerConnection.setRemoteDescription(sdpObserver, sdpRemote);
        });
    }

    public void addRemoteiceCandidate(IceCandidate iceCandidate) {
        executor.execute(() -> {
                    if (peerConnection != null && !isError) {
                        if (queuedIceCandidates != null) {
                            queuedIceCandidates.add(iceCandidate);

                        } else {
                            peerConnection.addIceCandidate(iceCandidate);
                        }
                    }
                }
        );
    }

    public static class DataChannelParameters {
        public final boolean ordered;
        public final int maxRetransmitTimeMs;
        public final int maxRetransmits;
        public final String protocol;
        public final boolean negotiated;
        public final int id;

        public DataChannelParameters(boolean ordered, int maxRetransmitTimeMs, int maxRetransmits, String protocol, boolean negotiated, int id) {
            this.ordered = ordered;
            this.maxRetransmitTimeMs = maxRetransmitTimeMs;
            this.maxRetransmits = maxRetransmits;
            this.protocol = protocol;
            this.negotiated = negotiated;
            this.id = id;
        }
    }


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


    public PeerConnectionClient(Context appContext, EglBase rootEglBase, PeerConnectionParameters peerConnectionParameters, PeerConnectionEvents events) {

        this.appContext = appContext;
        this.events = events;
        this.rootEglBase = rootEglBase;
        this.peerConnectionParameters = peerConnectionParameters;
        this.dataChannelEnabled = peerConnectionParameters.dataChannelParameters != null;


        final String fieldTrials = getFieldTrials(peerConnectionParameters);

        executor.execute(() -> {
            PeerConnectionFactory.initialize(
                    PeerConnectionFactory.InitializationOptions.builder(appContext)
                            .setFieldTrials(fieldTrials)
                            .setEnableInternalTracer(true)
                            .createInitializationOptions()
            );

            Log.d(TAG, "PeerConnectionClient: Field Trails" + fieldTrials);
        });
    }


    public interface PeerConnectionEvents {
        void onLocalDescription(final SessionDescription sdp);

        void onIceCandidate(final IceCandidate candidate);

        void onIceCandidatesRemoved(final IceCandidate[] candidates);

        void onIceConnected();

        void onIceDisconnected();

        void onConnected();

        void onDsconnected();

        void onPeerConnectionClosed();

        void onPeerConnectionStateReady(final StatsReport[] reports);

        void onPeerConnectionError(final String description);
    }

    public void createPeerConnection(final VideoSink localrender,
                                     final List<VideoSink> remoteSinks,
                                     final VideoCapturer videoCapturer,
                                     final WebRtcInterface.SignalingParameters signalingParameters) {
        if (peerConnectionParameters == null) {
            Log.e(TAG, "Creating peer connection without initializing factory.");
            return;
        }
        this.localrender = localrender;
        this.remoteSinks = remoteSinks;
        this.videoCapturer = videoCapturer;
        this.signalingParameters = signalingParameters;
        executor.execute(() -> {
            try {
                createMediaConstraintsInternal();
                createPeerConnectionInternal();
                //TODO HAVE REMOVED EVENTLOG
            } catch (Exception e) {

                throw e;
            }
        });
    }

    private void createPeerConnectionInternal() {
        if (factory == null || isError) {
            Log.d(TAG, "createPeerConnectionInternal: PeerConnection Failed to be Created");
        }
        Log.d(TAG, "createPeerConnectionInternal: Creating peer Connection");

        queuedIceCandidates = new ArrayList<>();

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(signalingParameters.iceServer);


        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        // Enable DTLS for normal calls and disable for loopback calls.
        rtcConfig.enableDtlsSrtp = !peerConnectionParameters.loopback;
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        peerConnection = factory.createPeerConnection(rtcConfig, (PeerConnection.Observer) pcObserver);

        if (dataChannelEnabled) {
            DataChannel.Init init = new DataChannel.Init();
            init.ordered = peerConnectionParameters.dataChannelParameters.ordered;
            init.negotiated = peerConnectionParameters.dataChannelParameters.negotiated;
            init.maxRetransmits = peerConnectionParameters.dataChannelParameters.maxRetransmits;
            init.maxRetransmitTimeMs = peerConnectionParameters.dataChannelParameters.maxRetransmitTimeMs;
            init.id = peerConnectionParameters.dataChannelParameters.id;
            init.protocol = peerConnectionParameters.dataChannelParameters.protocol;
            dataChannel = peerConnection.createDataChannel("ApprtcDemo data", init);
        }
        isInitiator = false;


        List<String> mediaStreamLabels = Collections.singletonList("ARDAMS");
        if (isVideoCallEnabled()) {
            peerConnection.addTrack(createVideoTrack(videoCapturer), mediaStreamLabels);
            // We can add the renders now instead of waiting
            // answer to get remote track.
            //TODO HAVE REMOVED GETREMOTEVIDEOTRACK() BECAUSE I DONT THINK IT MAKES SENSE SINCE IT IS A ONE WAY VIDEO STREAM
            remoteVideoTrack.setEnabled(renderVideo);
            for (VideoSink remoteSink : remoteSinks) {
                remoteVideoTrack.addSink((VideoSink) remoteSinks);
            }
        }


    }

    private VideoTrack createVideoTrack(VideoCapturer videoCapturer) {
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
        videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, appContext, videoSource.getCapturerObserver());
        videoCapturer.startCapture(videoWidth, videoHeight, videoFps);

        localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(renderVideo);
        localVideoTrack.addSink(localrender);
        return localVideoTrack;
    }


    private void createPeerConnectionFactoryInternal(PeerConnectionFactory.Options options) {
        isError = false;

        if (peerConnectionParameters.tracing) {
            Log.d(TAG, "createPeerConnectionFactoryInternal: først if peeronnectionparamter.tracing");
        }
        preferIsac = peerConnectionParameters.audioCodec != null;

    }

    private boolean isVideoCallEnabled() {
        return peerConnectionParameters.videoCallEnabled && videoCapturer != null;
    }

    private void createMediaConstraintsInternal() {
        Log.d(TAG, "createMediaConstraintsInternal: Setting media Constraints or creating media Constraints ");
        //Creating a video constrains if video is enabled
        if (isVideoCallEnabled()) {
            videoWidth = peerConnectionParameters.videoWidth;
            videoHeight = peerConnectionParameters.videoHeight;
            videoFps = peerConnectionParameters.videoFps;

            // IF the video is not specified it will be by default setted to HD
            if (videoWidth == 0 || videoHeight == 0) {
                videoWidth = HD_VIDEO_WIDTH;
                videoHeight = HD_VIDEO_HEIGHT;
            }
            if (videoFps == 0) {
                videoFps = 30;
            }


        }
        audioConstraints = new MediaConstraints();

        if (peerConnectionParameters.noAudioProcessing) {
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
        }
        //Creating SDP constraints
        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", Boolean.toString(isVideoCallEnabled())));
    }


    private static String getFieldTrials(PeerConnectionParameters peerConnectionParameters) {
        //Change this return statement
        return String.valueOf(peerConnectionParameters);
    }

    public void createOffer() {
        executor.execute(() -> {
            if (peerConnection != null && !isError) {
                isInitiator = true;
                peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
            }
        });
    }

    public void createAnswer() {
        executor.execute(() -> {
            isInitiator = false;
            peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
        });
    }


    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            if (localSdp != null) {
                Log.d(TAG, "onCreateSuccess: MULTIPLE SdP");
                return;
            }
            String sdpDescription = sessionDescription.description;
            final SessionDescription sdp = new SessionDescription(sessionDescription.type, sdpDescription);
            localSdp = sdp;
            executor.execute(() -> {
                if (peerConnection != null && !isError) {
                    peerConnection.setLocalDescription(sdpObserver, sdp);
                }
            });
        }


        @Override
        public void onSetSuccess() {
            executor.execute(() -> {
                        if (peerConnection == null || isError) {
                            Log.d(TAG, "onSetSuccess: PeerConnection equel null");
                            return;
                        }
                        if (isInitiator) {
                            // Create Offer
                            // Set Local SDP
                            if (peerConnection.getRemoteDescription() == null) {
                                events.onLocalDescription(localSdp);
                            } else {
                                drainCandidates();
                            }
                        }


                    }
            );

        }

        @Override
        public void onCreateFailure(String s) {
            Log.d(TAG, "onCreateFailure:  " + isError);
        }

        @Override
        public void onSetFailure(String s) {
            Log.d(TAG, "onSetFailure: setSdp Error" + isError);
        }
    }

    private void drainCandidates() {
        if (queuedIceCandidates != null) {
            Log.d(TAG, "drainCandidates: add" + queuedIceCandidates.size() + " Remote Candidates");
            for (IceCandidate candidate : queuedIceCandidates) {
                peerConnection.addIceCandidate(candidate);
            }
            queuedIceCandidates = null;
        }
    }

    private class PCObserver implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d(TAG, "onSignalingChange: NewState fro Signaling!" + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            executor.execute(() -> {
                Log.d(TAG, "onIceConnectionChange:  " + iceConnectionState);
                if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
                    events.onIceConnected();
                } else if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                    events.onIceDisconnected();
                } else if (iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
                    Log.d(TAG, "onIceConnectionChange: IceConnectionState Failed");
                }
            });
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            executor.execute(() -> {
                events.onIceCandidate(iceCandidate);
            });
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {


            executor.execute(() -> {
                events.onIceCandidatesRemoved(iceCandidates);
            });

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

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

        }
    }
}
