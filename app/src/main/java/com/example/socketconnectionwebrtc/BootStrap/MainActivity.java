package com.example.socketconnectionwebrtc.BootStrap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.OfferMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.R;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.example.socketconnectionwebrtc.WebRtc.PeerConnectionClient;
import com.example.socketconnectionwebrtc.WebRtc.WebRtcClient;
import com.example.socketconnectionwebrtc.WebRtc.WebRtcConnect;
import com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.util.Size;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.nfc.NfcAdapter.EXTRA_ID;


public class MainActivity extends AppCompatActivity implements WebRtcClient.RtcListener
        , PeerConnectionClient.PeerConnectionEvents, WebRtcInterface.sendingMessage, WebRtcConnect.onSending {
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    private boolean commandLineRun;
    public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED =
            "org.appspot.apprtc.SAVE_INPUT_AUDIO_TO_FILE";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF =
            "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";

    public static final String EXTRA_ENABLE_RTCEVENTLOG = "org.appspot.apprtc.ENABLE_RTCEVENTLOG";

    @Nullable
    private WebRtcInterface.SignalingParameters signalingParameters;
    private int REQUEST_CODE_PERMISSION = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private final List<VideoSink> remoteSinks = new ArrayList<>();
    private static final String TAG = "MainActivity";
    //private FirebaseAuth mAuth;
    private Toast logToast;
    private MyViewModel myViewModel;
    private SocketConnectionHandler socketConnectionHandler;
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    private static final String stunServer = "stun:firstlineconnect.com";
    private static final String turnServer = "turn:firstlineconnect.com";
    private final ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private TextureView textureView;
    private WebRtcClient webRtcClient = new WebRtcClient();
    private WebRtcInterface.SignalingEvents events;
    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private PeerConnectionClient peerConnectionClient;
    Gson gson = new Gson();
    final EglBase eglBase = EglBase.create();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void sendMessageString() {


    }

    @Override
    public void sendMessageJson() {

    }


    public interface messageToWebRtcClient {
        void messageToWebRTC(String Message);
    }

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                return;
            }
            target.onFrame(frame);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO FIND UD AF EN MÅDE AT UNDGÅ ALLE DE HER NULLPOINTERE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        Log.d(TAG, "onCreate: Andrei");
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        signalingParameters = null;
        //TODO SPLIT DIALOG OG WEB-RTC PAYLOAD
        final Observer<String> nameObserver = newName -> {
            Log.d(TAG, "onCreate: " + newName);
            Log.d(TAG, "onCreate: HVAD ER NEWNAME" + newName);
            //TODO FIX DIALOG SÅ HVERGANG MAN KLIKKER IKKE SPAMMER DET SAMME

            dialog(newName);


        };


        startCamera();

        final Observer<String> webRtcObserver = newWebRTCMessage -> {
            Log.d(TAG, "onCreate: Does it worK?");
            Point display = new Point();
            getWindowManager().getDefaultDisplay().getSize(display);
            Log.d(TAG, "onCreate: " + newWebRTCMessage);

            decider(newWebRTCMessage);
        };


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        myViewModel.getEventMessage().observe(this, nameObserver);
        myViewModel.getMessageToWebRTC().observe(this, webRtcObserver);

        try {
            socketConnectionHandler = new SocketConnectionHandler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);
        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }

        peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false),
                        intent.getBooleanExtra(EXTRA_ENABLE_RTCEVENTLOG, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        int runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        RecyclerView recyclerView = findViewById(R.id.textViewRecycleerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);

        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient(
                getApplicationContext(), eglBase, peerConnectionParameters, MainActivity.this);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        if (loopback) {
            options.networkIgnoreMask = 0;
        }
        peerConnectionClient.createPeerConnectionFactory(options);

        //auth = FirebaseAuth.getInstance();
        textureView = findViewById(R.id.view_finder1);

        ConnectToSocket();
        inistializeWebRtcClient();


    }

    public void inistializeWebRtcClient() {
        Log.d(TAG, "inistializeWebRtcClient: Rammer Vi her Inistailiza WebRTCCLIENT");
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList();
        iceServers.add(PeerConnection.IceServer.builder(stunServer).createIceServer());
        iceServers.add(PeerConnection.IceServer.builder(turnServer).setUsername("u").setPassword("p").createIceServer());

        WebRtcInterface.SignalingParameters param = new WebRtcInterface.SignalingParameters(
                iceServers,
                false,
                null,
                null,
                null
        );
        onConnectedToRoomInternal(param);


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
                Log.d(TAG, "decider: Rammer vi her?");

                SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(unCoverMessageToWebRTC.getPayload().getType()), unCoverMessageToWebRTC.getPayload().getSdp());

                WebRtcInterface.SignalingParameters parameters = new WebRtcInterface.SignalingParameters(
                        new ArrayList<>(),
                        false,
                        null,
                        sdp,
                        null
                );
                onConnectedToRoomInternal(parameters);


                //onCreatingAnswerSdp(parameters);


                break;
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

    private void onCreatingAnswerSdp(WebRtcInterface.SignalingParameters parameters) {

        peerConnectionClient.setRemoteDescription(parameters.offerSdp);
        peerConnectionClient.createAnswer();
    }


    private void onConnectedToRoomInternal(final WebRtcInterface.SignalingParameters params) {
        Log.d(TAG, "onConnectedToRoomInternal: OnconnecToRoomInternal");
        signalingParameters = params;
        VideoCapturer videoCapturer = null;


        if (peerConnectionParameters.videoCallEnabled) {
            Log.d(TAG, "onConnectedToRoomInternal: Start Camera");
            startCamera();
        }

        peerConnectionClient.createPeerConnection(
                localProxyVideoSink, remoteSinks, videoCapturer, signalingParameters);

        if (params.initiator) {
            Log.d(TAG, "onConnectedToRoomInternal: CREATING OFFER");
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                Log.d(TAG, "onConnectedToRoomInternal: Offer Sdp");
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                //peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                Log.d(TAG, "onConnectedToRoomInternal: iceCandidates");
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    private void dialog(String payload) {
        Log.d(TAG, "onCreate: Working");

        new AlertDialog.Builder(this)
                .setTitle("InitiateCall")
                .setMessage(payload)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Dialog on click No");
                        socketConnectionHandler.sendMessageToSocket(new BaseMessage(MessageType.rejectCall, new RoomDetails("+4529933087", "Steffen")));
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Dialog On Click Yes");

                        socketConnectionHandler.sendMessageToSocket(new BaseMessage(MessageType.acceptCall, new RoomDetails("+4529933087", "Steffen")));
                        //TODO SIGNALING PARAMETERS NULLPOINTER

                    }

                }).show();
    }

    public void startCamera() {
        Log.d(TAG, "startCamera: Inside StartCamera");
        CameraX.unbindAll();


        //Starting Preview
        int RatioW = textureView.getWidth();
        int RatioH = textureView.getHeight();
        Rational ratio = new Rational(RatioW, RatioH);
        Size screen = new Size(RatioW, RatioH);


        //Config Obj for Preview/ViewFinder
        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(ratio).setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView, 0);


                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });
        ImageCaptureConfig imgCapConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imgCapConfig);


        CameraX.bindToLifecycle(this, imgCap, preview);
        Log.d(TAG, "startCamera: CameraStarted");

    }

    private void updateTransform() {

        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cx = w / 2f;
        float cy = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();


        switch (rotation) {
            case Surface
                    .ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface
                    .ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }
        mx.postRotate((float) rotationDgr, cx, cy);
        textureView.setTransform(mx);
    }

    public void ConnectToSocket() {
        try {
            Log.d(TAG, "ConnectToSocket: Tryinger");
            socketConnectionHandler.socketConnect();
            Log.d(TAG, "ConnectToSocket: JAJAJ");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                startCamera();
                Log.d(TAG, "onRequestPermissionsResult: Started Camera");
            } else {
                Toast.makeText(MainActivity.this, "Permission No Granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void notifierInfinitiCall() {
        Log.d(TAG, "DialogStarterBox: DialogStarter");
        AlertDialog.Builder alertDialogBox = new AlertDialog.Builder(MainActivity.this);
        alertDialogBox.setMessage("Steffen");
        alertDialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: Fair");
            }
        });
        alertDialogBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        alertDialogBox.setCancelable(false);
        AlertDialog finalDialog = alertDialogBox.create();
        finalDialog.show();
    }

    @Override
    public void onCallReady(String callId) {

    }

    @Override
    public void onStatusChanged(String newStatus) {

    }

    @Override
    public void onLocalStream(MediaStream localStream) {

    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

    }

/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Toast.makeText(this, "Signed in Success full", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(MainActivity.this, LoginManager.class);
            startActivity(intent);
        }
    }

 */

    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    public void sendSdp(JSONObject jsonObject) {
        Log.d(TAG, "sendSdp: Rammer VI her SendSDp");
        socketConnectionHandler.sendMessageToSocketFromOffer(jsonObject);

    }

    public void sendMesssage(String messasge) {
        socketConnectionHandler.sendMessageToSocket(messasge);
    }


    @Override
    public void onLocalDescription(SessionDescription sdp) {
        Log.d(TAG, "onLocalDescription: Hallo");
            runOnUiThread(() ->{
                Log.d(TAG, "onLocalDescription: Hva så?");
                webRtcClient.sendAnswerSdp(sdp);

            });
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {

    }

    @Override
    public void onIceConnected() {

    }

    @Override
    public void onIceDisconnected() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDsconnected() {

    }

    @Override
    public void onPeerConnectionClosed() {

    }

    @Override
    public void onPeerConnectionStateReady(StatsReport[] reports) {

    }

    @Override
    public void onPeerConnectionError(String description) {

    }


}







