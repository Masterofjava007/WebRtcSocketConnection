package com.example.socketconnectionwebrtc.WebRtc;

import android.content.Context;
import android.util.Log;

import com.example.socketconnectionwebrtc.RecorderAudioToFileController.RecordedAudioToFileController;

import org.jetbrains.annotations.Nullable;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConnectionClient {
    private MediaConstraints audioConstraints;
    private MediaConstraints sdpMediaConstraints;
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String VIDEO_CODEC_H264_HIGH = "H264 High";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final String VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
    private static final String TAG = "PeerConnectionClient";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String AUDIO_CODEC_ISAC = "ISAC";

    private final PCObserver pcObserver = new PCObserver();
    @Nullable
    private VideoCapturer videoCapturer;
    @Nullable
    private SessionDescription localSdp;
    @Nullable
    private DataChannel dataChannel;
    @Nullable
    private PeerConnectionFactory factory;
    @Nullable
    private PeerConnection peerConnection;
    @Nullable
    private RecordedAudioToFileController saveRecordedAudioToFile;
    @Nullable
    private List<IceCandidate> queuedRemoteCandidates;


    private final SDPObserver sdpObserver = new SDPObserver();
    private final EglBase rootEglBase;
    private final Context appContext;
    private final PeerConnectionEvents events;
    private final PeerConnectionParameters peerConnectionParameters;
    private final boolean dataChannelEnabled;
    private WebRtcInterface.SignalingParameters signalingParameters;
    private boolean preferIsac;
    private boolean isInitiator;


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


        public PeerConnectionParameters(boolean videoCallEnabled, boolean loopback, boolean tracing,
                                        int videoWidth, int videoHeight, int videoFps, int videoMaxBitrate, String videoCodec,
                                        boolean videoCodecHwAcceleration, boolean videoFlexfecEnabled, int audioStartBitrate,
                                        String audioCodec, boolean noAudioProcessing, boolean aecDump, boolean saveInputAudioToFile,
                                        boolean useOpenSLES, boolean disableBuiltInAEC, boolean disableBuiltInAGC,
                                        boolean disableBuiltInNS, boolean disableWebRtcAGCAndHPF, boolean enableRtcEventLog,
                                        DataChannelParameters dataChannelParameters) {
            this.videoCallEnabled = videoCallEnabled;
            this.loopback = loopback;
            this.tracing = tracing;
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            this.videoFps = videoFps;
            this.videoMaxBitrate = videoMaxBitrate;
            this.videoCodec = videoCodec;
            this.videoFlexfecEnabled = videoFlexfecEnabled;
            this.videoCodecHwAcceleration = videoCodecHwAcceleration;
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


    public PeerConnectionClient(Context appContext, EglBase eglBase,
                                PeerConnectionParameters peerConnectionParameters, PeerConnectionEvents events) {
        this.rootEglBase = eglBase;
        this.appContext = appContext;
        this.events = events;
        this.peerConnectionParameters = peerConnectionParameters;
        this.dataChannelEnabled = peerConnectionParameters.dataChannelParameters != null;


        final String fieldTrials = getFieldTrials(peerConnectionParameters);

        Log.d(TAG, "Initialize WebRTC. Field trials: " + fieldTrials);
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(appContext)
                        .setFieldTrials(fieldTrials)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions());
    }


    public void createPeerConnection(final WebRtcInterface.SignalingParameters signalingParameters) {
        this.signalingParameters = signalingParameters;

        try {
            createMediaConstraintsInternal();
            createPeerConnectionInternal();
        } catch (Exception e) {
            throw e;
        }

    }

    public void createAnswer() {
        executor.execute(() -> {
            if (peerConnection != null) {
                Log.d(TAG, "PC create ANSWER");
                isInitiator = false;
                peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
            }
        });
    }


    public void createPeerConnectionFactory(PeerConnectionFactory.Options options) {

        createPeerConnectionFactoryInternal(options);

    }

    private void createPeerConnectionInternal() {

        Log.d(TAG, "Create peer connection.");
        queuedRemoteCandidates = new ArrayList<>();

        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(signalingParameters.iceServer);
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

        peerConnection = factory.createPeerConnection(rtcConfig, pcObserver);

        if (dataChannelEnabled) {
            DataChannel.Init init = new DataChannel.Init();
            init.ordered = peerConnectionParameters.dataChannelParameters.ordered;
            init.negotiated = peerConnectionParameters.dataChannelParameters.negotiated;
            init.maxRetransmits = peerConnectionParameters.dataChannelParameters.maxRetransmits;
            init.maxRetransmitTimeMs = peerConnectionParameters.dataChannelParameters.maxRetransmitTimeMs;
            init.id = peerConnectionParameters.dataChannelParameters.id;
            init.protocol = peerConnectionParameters.dataChannelParameters.protocol;
            dataChannel = peerConnection.createDataChannel("The Channel", init);
        }
    }


    private void createPeerConnectionFactoryInternal(PeerConnectionFactory.Options options) {
        Log.d(TAG, "createPeerConnectionFactoryInternal: Entered CreatePeerOCnnectionFactory INternal");

        preferIsac = peerConnectionParameters.audioCodec != null && peerConnectionParameters.audioCodec.equals(AUDIO_CODEC_ISAC);
        final boolean enableH264HighProfile =
                VIDEO_CODEC_H264_HIGH.equals(peerConnectionParameters.videoCodec);
        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;
        final AudioDeviceModule adm = createAudioJavaDevice();

        if (peerConnectionParameters.videoCodecHwAcceleration) {
            encoderFactory = new DefaultVideoEncoderFactory(
                    rootEglBase.getEglBaseContext(), true /* enableIntelVp8Encoder */, enableH264HighProfile);
            decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        } else {
            encoderFactory = new SoftwareVideoEncoderFactory();
            decoderFactory = new SoftwareVideoDecoderFactory();
        }


        factory = PeerConnectionFactory.builder().setOptions(options)
                .setAudioDeviceModule(adm).setVideoDecoderFactory(decoderFactory).setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory();
        Log.d(TAG, "createPeerConnectionFactoryInternal: peerConnectionFactory Created");
    }

    AudioDeviceModule createAudioJavaDevice() {
        if (!peerConnectionParameters.useOpenSLES) {

        }
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = new JavaAudioDeviceModule.AudioRecordErrorCallback() {
            @Override
            public void onWebRtcAudioRecordInitError(String s) {
                Log.d(TAG, "onWebRtcAudioRecordInitError: " + s);
            }

            @Override
            public void onWebRtcAudioRecordStartError(JavaAudioDeviceModule.AudioRecordStartErrorCode audioRecordStartErrorCode, String s) {

            }

            @Override
            public void onWebRtcAudioRecordError(String s) {

            }
        };
        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = new JavaAudioDeviceModule.AudioTrackErrorCallback() {
            @Override
            public void onWebRtcAudioTrackInitError(String s) {

            }

            @Override
            public void onWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode audioTrackStartErrorCode, String s) {

            }

            @Override
            public void onWebRtcAudioTrackError(String s) {

            }

        };

        return JavaAudioDeviceModule.builder(appContext)
                .setSamplesReadyCallback(saveRecordedAudioToFile)
                .setUseHardwareAcousticEchoCanceler(!peerConnectionParameters.disableBuiltInAEC)
                .setUseHardwareNoiseSuppressor(!peerConnectionParameters.disableBuiltInNS)
                .setAudioRecordErrorCallback(audioRecordErrorCallback)
                .setAudioTrackErrorCallback(audioTrackErrorCallback)
                .createAudioDeviceModule();

    }

    public interface PeerConnectionEvents {

        void onLocalDescription(final SessionDescription sdp);
        void onIceCandidate(final IceCandidate iceCandidate);


    }

    public void settingRemoteDescription(final SessionDescription sdp) {
        String sdpDescription = sdp.description;

        Log.d(TAG, "settingRemoteDescription: Hitting");
        try {

            localSdp = sdp;
            SessionDescription sdpRemote = new SessionDescription(sdp.type, sdpDescription);
            peerConnection.setRemoteDescription(sdpObserver, sdpRemote);

            Log.d(TAG, "settingRemoteDescription: done");
        } catch (Exception e) {
            Log.d(TAG, "settingRemoteDescription: Something have Catched  ");
        }
    }


    private class PCObserver implements PeerConnection.Observer {

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            executor.execute(()->
                    events.onIceCandidate(iceCandidate));
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

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

    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription origSdp) {
            String sdpDescription = origSdp.description;
            if (preferIsac) {
                Log.d(TAG, "onCreateSuccess: PreferIsac is true");
            }

            final SessionDescription sdp = new SessionDescription(origSdp.type, sdpDescription);
            localSdp = sdp;
            executor.execute(() -> {
                if (peerConnection != null) {
                    Log.d(TAG, "Set local SDP from " + sdp.type);
                    peerConnection.setLocalDescription(sdpObserver, sdp);
                }
            });
        }

        @Override
        public void onSetSuccess() {
            executor.execute(() -> {
                Log.d(TAG, "onSetSuccess: sdpRemote Rammer her");
                if (isInitiator) {
                    if (peerConnection.getRemoteDescription() == null) {
                        events.onLocalDescription(localSdp);
                    } else {
                        drainCandidates();
                    }
                } else {
                    if (peerConnection.getLocalDescription() != null) {
                        events.onLocalDescription(localSdp);
                        drainCandidates();

                    } else {
                        Log.d(TAG, "onSetSuccess: Waiting for Answer");
                        createAnswer();
                    }
                }
            });
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d(TAG, "onCreateFailure: sdpRemote Rammer her");
        }

        @Override
        public void onSetFailure(String s) {
            Log.d(TAG, "onSetFailure: sdpRemote Rammer her");
        }
    }

    private void drainCandidates() {
    }


    private static String getFieldTrials(PeerConnectionParameters peerConnectionParameters) {
        String fieldTrials = "";
        if (peerConnectionParameters.videoFlexfecEnabled) {
            fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL;
            Log.d(TAG, "Enable FlexFEC field trial.");
        }
        fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL;
        if (peerConnectionParameters.disableWebRtcAGCAndHPF) {
            fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL;
            Log.d(TAG, "Disable WebRTC AGC field trial.");
        }
        return fieldTrials;
    }


    private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
        final String mediaDescription = isAudio ? "m=audio " : "m=video ";
        for (int i = 0; i < sdpLines.length; ++i) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i;
            }
        }
        return -1;
    }


    private void createMediaConstraintsInternal() {
        // Create video constraints if video call is enabled.

        // If video resolution is not specified, default to HD.


        // If fps is not specified, default to 30.

        // Create audio constraints.
        audioConstraints = new MediaConstraints();
        // added for audio performance measurements
        if (peerConnectionParameters.noAudioProcessing) {
            Log.d(TAG, "Disabling audio processing");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
        }
        // Create SDP constraints.
        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", Boolean.toString(isVideoCallEnabled())));
    }

    private boolean isVideoCallEnabled() {
        return peerConnectionParameters.videoCallEnabled && videoCapturer != null;
    }

    public void addRemoteIceCandidate(final IceCandidate candidate) {
        executor.execute(() -> {
            if (peerConnection != null) {
                if (queuedRemoteCandidates != null) {
                    queuedRemoteCandidates.add(candidate);
                } else {
                    peerConnection.addIceCandidate(candidate);
                }
            }
        });
    }

}