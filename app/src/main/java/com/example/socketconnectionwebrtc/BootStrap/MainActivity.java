package com.example.socketconnectionwebrtc.BootStrap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.Login.LoginManager;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.ParcableMessages;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.R;
//import com.example.socketconnectionwebrtc.SocketConnection.OkHttpSocketConnection;
//import com.example.socketconnectionwebrtc.SocketConnection.OkHttpSocketConnection;
import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import android.util.Size;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.PeerConnection;
import org.webrtc.IceCandidate;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import okhttp3.WebSocket;


public class MainActivity extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSION = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private MyViewModel myViewModel;
    private SocketConnectionHandler socketConnectionHandler;
    private String getPayload;
    private TextureView textureView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Andrei");
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        /*
        myViewModel.message.observe(this, message -> {
            Log.d(TAG, "onCreate: Working");
            // update UI
        });

         */

        // Create the observer which updates the UI.
        final Observer<String> nameObserver = newName -> {
            dialog(newName);

        };


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        myViewModel.getMessage().observe(this, nameObserver);


        //okHttpSocketConnection.connect();


        try {
            socketConnectionHandler = new SocketConnectionHandler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }


        RecyclerView recyclerView = findViewById(R.id.textViewRecycleerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        final Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);


        auth = FirebaseAuth.getInstance();
        textureView = findViewById(R.id.view_finder1);


        ConnectToSocket();


        try {
            startCamera();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e);
        }

    }

    private void dialog(String payload) {
        //Bundle data = getIntent().getExtras();

        //  String eventHandlerMessage = data.getString("eventHandlerValues");


        // Update the UI, in this case, a TextView.
        //BaseMessageHandler base = new BaseMessageHandler();

//        getPayload = base.getPayload().toString();
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
                    }

                }).show();
    }

    private void startCamera() {
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
}







