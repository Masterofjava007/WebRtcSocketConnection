package com.example.socketconnectionwebrtc.BootStrap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnectionwebrtc.ARCore.Pointer;
import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Login.LoginManager;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.Model.SessionSdp;
import com.example.socketconnectionwebrtc.Model.iceCandidateParamters;
import com.example.socketconnectionwebrtc.Model.sdpAnswer;
import com.example.socketconnectionwebrtc.Model.sendIceCandidate;
import com.example.socketconnectionwebrtc.R;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.example.socketconnectionwebrtc.WebRtc.PeerConnectionClient;
import com.example.socketconnectionwebrtc.WebRtc.WebRtcInterface;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import android.util.Size;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.nfc.NfcAdapter.EXTRA_ID;

public class MainActivity extends AppCompatActivity implements
        PeerConnectionClient.PeerConnectionEvents, WebRtcInterface.SignalingEvents, CallFragment.onCallEvents {
    public static final String EXTRA_VIDEO_WIDTH = "VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "VIDEO_HEIGHT";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "CAPTURETOTEXTURE";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_DISPLAY_HUD = "DISPLAY_HUD";
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};
    public static final String EXTRA_PROTOCOL = "PROTOCOL";
    public static final String EXTRA_CAMERA2 = "CAMERA2";
    public static final String EXTRA_NEGOTIATED = "NEGOTIATED";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "MAX_RETRANSMITS";
    public static final String EXTRA_LOOPBACK = "LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "VIDEO_CALL";
    public static final String EXTRA_VIDEO_FPS = "VIDEO_FPS";
    public static final String EXTRA_VIDEO_BITRATE = "VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "HWCODEC";
    public static final String EXTRA_FLEXFEC_ENABLED = "FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED = "NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "AECDUMP";
    public static final String EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED = "SAVE_INPUT_AUDIO_TO_FILE";
    public static final String EXTRA_OPENSLES_ENABLED = "OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "DISABLE_BUILT_IN_NS";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_CMDLINE = "CMDLINE";
    public static final String EXTRA_RUNTIME = "RUNTIME";
    public static final String EXTRA_ENABLE_RTCEVENTLOG = "oENABLE_RTCEVENTLOG";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SCREENCAPTURE = "SCREENCAPTURE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH = "SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT = "SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_TRACING = "TRACING";
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    private static final String TAG = "MainActivity";
    private static final String stunServer = "stun:firstlineconnect.com";
    private static final String turnServer = "turn:firstlineconnect.com";

    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;

    private final ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private boolean callControlFragmentVisible = true;

    @Nullable
    private SurfaceViewRenderer pipRenderer;
    @Nullable
    private SurfaceViewRenderer fullscreenRenderer;
    private boolean isTracking;

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

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }

    private Pointer pointer = new Pointer();
    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;
    private boolean isHitting;
    private final List<VideoSink> remoteSinks = new ArrayList<>();
    private boolean screencaptureEnabled;
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Nullable
    private WebRtcInterface webRtcInterface;
    @Nullable
    private WebRtcInterface.SignalingParameters signalingParameters;
    private int REQUEST_CODE_PERMISSION = 10;
    private FirebaseAuth mAuth;
    @Nullable
    private VideoFileRenderer videoFileRenderer;
    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private MyViewModel myViewModel;
    private SocketConnectionHandler socketConnectionHandler;
    private TextureView textureView;
    private WebRtcInterface.SignalingEvents events;
    private boolean commandLineRun;
    private boolean isSwappedFeeds;
    @Nullable
    private PeerConnectionClient peerConnectionClient;
    Gson gson = new Gson();
    private CallFragment callFragment;
    private fragment_hud hudFragment;
    private ModelLoader modelLoader;
    private ArFragment fragment;
    final EglBase eglBase = EglBase.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // fragment = (ArFragment)
        //   getSupportFragmentManager().findFragmentById(R.id.fullscreen_video_view);

        //  fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
        //     fragment.onUpdate(frameTime);
        //     onUpdate();
        //   });

        // modelLoader = new ModelLoader(new WeakReference<>(this));

        try {
            socketConnectionHandler = new SocketConnectionHandler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConnectToSocket();
        fullscreenRenderer = findViewById(R.id.fullscreen_video_view);
        pipRenderer = findViewById(R.id.pip_video_view);
        callFragment = new CallFragment();
        hudFragment = new fragment_hud();

        final Intent intent = getIntent();
        final EglBase eglBase = EglBase.create();

        pipRenderer.init(eglBase.getEglBaseContext(), null);
        pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(saveRemoteVideoToFile, videoOutWidth, videoOutHeight, eglBase.getEglBaseContext());
            } catch (IOException e) {
                throw new RuntimeException("Failed to open video file for output " + saveRemoteVideoToFile, e);
            }
        }
        fullscreenRenderer.init(eglBase.getEglBaseContext(), null);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true);
        fullscreenRenderer.setEnableHardwareScaler(false);

        setSwappedFeeds(true /* isSwappedFeeds */);



        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);

        DisplayMetrics displayMetrics = getDisplayMetrics();
        videoWidth = displayMetrics.widthPixels;
        videoHeight = displayMetrics.heightPixels;

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


        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient(
                getApplicationContext(), eglBase, peerConnectionParameters, MainActivity.this);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        peerConnectionClient.createPeerConnectionFactory(options);


        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
        startScreenCapture();

        callFragment.setArguments(intent.getExtras());
        hudFragment.setArguments(intent.getExtras());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();


        if (screencaptureEnabled) {
            startScreenCapture();
        } else {
            Log.d(TAG, "onCreate: Starting Screen Rammer else");
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSwappedFeeds(!isSwappedFeeds);
            }
        };
        pipRenderer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCallControlFragmentVisibility();
            }
        });

        fullscreenRenderer.setOnClickListener(listener);
        remoteSinks.add(remoteProxyRenderer);
        /*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath().build());
*/

        final Observer<String> nameObserver = newName -> {
            Log.d(TAG, "onCreate: " + newName);
            Log.d(TAG, "onCreate: HVAD ER NEWNAME" + newName);
            //TODO FIX DIALOG SÅ HVERGANG MAN KLIKKER IKKE SPAMMER DET SAMME
            dialog(newName);
        };
        final Observer<? super WebRtcInterface.SignalingParameters> webRtcObserver = newWebRTCMessage -> {
            Log.d(TAG, "onCreate: WebRTCMessageObserver");
            inistializeWebRtcClient();
            peerConnectionClient.settingRemoteDescription(newWebRTCMessage.offerSdp);
        };

        myViewModel.getEventMessage().observe(this, nameObserver);
        myViewModel.getMessageToWebRTC().observe(this, webRtcObserver);

/*
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.call_fragment_container);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });

 */
    }

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    private void initializeGarllery() {
        FrameLayout layoutFrame = findViewById(R.id.fullscreen_video_view);

        ImageView arrow = new ImageView(this);
        arrow.setContentDescription("arrow");
        layoutFrame.addView(arrow);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addObject(Uri model) {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    modelLoader.loadModel(hit.createAnchor(), model);
                    break;

                }
            }
        }
    }

    public class ModelLoader {
        private final WeakReference<MainActivity> owner;
        private static final String TAG = "ModelLoader";

        public ModelLoader(WeakReference<MainActivity> owner) {
            this.owner = owner;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void loadModel(Anchor anchor, Uri uri) {
            if (owner.get() == null) {
                Log.d(TAG, "loadModel: Cannot Define Load Model");
                return;
            }
            ModelRenderable.builder()
                    .setSource(owner.get(), uri)
                    .build()
                    .handle((modelRenderable, throwable) -> {
                        MainActivity activity = owner.get();
                        if (activity == null) {
                            return null;
                        } else if (throwable != null) {
                            activity.onException(throwable);
                        } else {
                            activity.addNodeToScene(anchor, modelRenderable);
                        }
                        return null;
                    });
            return;
        }
    }

    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    public void onException(Throwable throwable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(throwable.getMessage())
                .setTitle("Codelab error!");
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }


    private void initCamera() {

        final Intent intent = getIntent();
        final EglBase eglBase = EglBase.create();

        pipRenderer.init(eglBase.getEglBaseContext(), null);
        pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(saveRemoteVideoToFile, videoOutWidth, videoOutHeight, eglBase.getEglBaseContext());
            } catch (IOException e) {
                throw new RuntimeException("Failed to open video file for output " + saveRemoteVideoToFile, e);
            }
        }
        fullscreenRenderer.init(eglBase.getEglBaseContext(), null);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true);
        fullscreenRenderer.setEnableHardwareScaler(false);

        setSwappedFeeds(true /* isSwappedFeeds */);

        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
        startScreenCapture();

        callFragment.setArguments(intent.getExtras());
        hudFragment.setArguments(intent.getExtras());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();
    }

    private void toggleCallControlFragmentVisibility() {
        if (!callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
            ft.show(hudFragment);
        } else {
            ft.hide(callFragment);
            ft.hide(hudFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @TargetApi(17)
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager =
                (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    private @Nullable VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @TargetApi(21)
    private @Nullable VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
            }
        });
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    private @Nullable VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);

        videoCapturer = createCameraCapturer(new Camera2Enumerator(this));

        return videoCapturer;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    public void inistializeWebRtcClient() {

        Log.d(TAG, "inistializeWebRtcClient: Rammer Vi her Inistailiza WebRTCCLIENT");
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList();
        //iceServers.add(PeerConnection.IceServer.builder(stunServer).createIceServer());
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

    /*
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
                    executor.execute(() -> {
                        Log.d(TAG, "decider: Rammer vi her?");
                      String createMessage = "Steffen";
                      socketConnectionHandler.sendMessageToSocket(createMessage);

                        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList();
                        iceServers.add(PeerConnection.IceServer.builder(stunServer).createIceServer());
                        iceServers.add(PeerConnection.IceServer.builder(turnServer).setUsername("u").setPassword("p").createIceServer());

                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER, unCoverMessageToWebRTC.getPayload().getAnswer());

                        WebRtcInterface.SignalingParameters parameters = new WebRtcInterface.SignalingParameters(
                                iceServers,
                                false,
                                null,
                                sdp,
                                null
                        );

                    });
                    //onCreatingAnswerSdp(parameters);


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
    */

    private void onConnectedToRoomInternal(final WebRtcInterface.SignalingParameters params) {
        Log.d(TAG, "onConnectedToRoomInternal: OnconnecToRoomInternal");

        signalingParameters = params;
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(localProxyVideoSink, remoteSinks, videoCapturer, signalingParameters);

        if (params.iceCandidates != null) {
            Log.d(TAG, "onConnectedToRoomInternal: params.Icecandidates != null");
            for (IceCandidate iceCandidate : params.iceCandidates) {
                peerConnectionClient.addRemoteIceCandidate(iceCandidate);
            }
        }
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);
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
        runOnUiThread(() -> {
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
        });

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
                //startCamera();
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

    public void sendSdp(JSONObject jsonObject) {
        Log.d(TAG, "sendSdp: Rammer VI her SendSDp");
        //socketConnectionHandler.sendMessageToSocketFromOffer(String.valueOf(jsonObject));
    }

    public void sendingSdp(SessionDescription sdp) {
        executor.execute(() -> {

            String sdpType = sdp.type.canonicalForm().toLowerCase();
            Log.d(TAG, "sendingSdp: " + sdpType);

            Log.d(TAG, "sendMessageToSocketFromOffer: Sending Sdp To Socket");
            //sdpAnswer sdpAnswer = new sdpAnswer(typeAnswer, new RoomDetails("+4529933087", "Steffen"), new SessionSdp(sdp));
            BaseMessage baseSdp = new BaseMessage(MessageType.sendAnswer, new sdpAnswer("+4529933087", "Steffen", new SessionSdp(sdpType, sdp.description)));

            String Steffen = gson.toJson(baseSdp);

            socketConnectionHandler.sendMessageToSocket(Steffen);

        });
    }

    public void sendMesssage(String messasge) {
        socketConnectionHandler.sendMessageToSocket(messasge);
    }

    public void sendLocalIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "sendLocalIceCandidate: SENDING");
        executor.execute(() -> {

            BaseMessage base = new BaseMessage(MessageType.sendCandidate, new sendIceCandidate("+4529933087", "Steffen", new iceCandidateParamters(iceCandidate.sdp, iceCandidate.sdpMLineIndex, iceCandidate.sdpMid)));
            Log.d(TAG, "sendLocalIceCandidate: " + base);
            String Steffen = gson.toJson(base);
            Log.d(TAG, "sendLocalIceCandidate: " + Steffen);
            socketConnectionHandler.sendMessageToSocket(Steffen);

        });
    }

    @Override
    public void onCallHangup() {

    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(RendererCommon.ScalingType scalingType) {
        fullscreenRenderer.setScalingType(scalingType);

    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        return false;
    }

    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        Log.d(TAG, "onLocalDescription: " + sdp);
        sendingSdp(sdp);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "onIceCandidate: Trying to go to SendLocalIceCandidate");
        sendLocalIceCandidate(iceCandidate);

    }

    @Override
    public void onIceConnected() {
        runOnUiThread(() -> {
            Log.d(TAG, "onIceConnected: IceCandidate Connected");
        });
    }

    @Override
    public void onIceDisconnected() {

    }


    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {
        Log.d(TAG, "onRemoteIceCandidate: Rammer Her med ICeCandidate");
        peerConnectionClient.addRemoteIceCandidate(candidate);

    }

    @Override
    public void onRemoteIceCandidateRemoved(IceCandidate[] candidates) {

    }

    @Override
    public void onChannelClose() {

    }

    @Override
    public void onChannelError(String description) {

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser curretUser = mAuth.getCurrentUser();
        updateUi(curretUser);

    }

    private void updateUi(FirebaseUser user) {
        if (user != null) {
            //initCamera();
            //startCamera();
            getDisplayMetrics();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginManager.class);
            startActivity(intent);
        }
    }

}







