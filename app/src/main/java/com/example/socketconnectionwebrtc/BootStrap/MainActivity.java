package com.example.socketconnectionwebrtc.BootStrap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.EventHandler.IEventListener;
import com.example.socketconnectionwebrtc.EventHandler.SocketInterface;
import com.example.socketconnectionwebrtc.Model.BaseMessage;
import com.example.socketconnectionwebrtc.Model.RoomDetails;
import com.example.socketconnectionwebrtc.R;
import com.example.socketconnectionwebrtc.Repos.RepoMessageHandler;
import com.example.socketconnectionwebrtc.SocketConnection.OkHttpSocketConnection;
//import com.example.socketconnectionwebrtc.SocketConnection.SocketConnectionHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import android.util.Size;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements IEventListener, SocketInterface {
    private int REQUEST_CODE_PERMISSION = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private MyViewModel myViewModel;
    private RepoMessageHandler repoMessageHandler = new RepoMessageHandler();
    //SocketConnectionHandler socketConnectionHandler = new SocketConnectionHandler();
    private FrameLayout frameLayout;
    Gson gson = new Gson();
    OkHttpSocketConnection okHttpSocketConnection = new OkHttpSocketConnection();
    private TextureView textureView;
    BaseMessage baseMessage = new BaseMessage(MessageType.createRoom, new RoomDetails("+4529933087", "Steffen"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpSocketConnection.connect();

        RecyclerView recyclerView = findViewById(R.id.textViewRecycleerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        textureView = findViewById(R.id.view_finder1);
        frameLayout = findViewById(R.id.frameLayout);

        //ConnectToSocket();

        try {
            // startCamera();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e);
        }


        Log.d(TAG, "onCreate: Andrei");
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        myViewModel.getMessage().observe(this, message -> {
            Log.d(TAG, "onCreate: Working");
            // update UI
        });
        //myViewModel.sendingMessage("Steffe");
    }



/*
            Log.d(TAG, "onCreate: Rammer vi her?");
            notifierInfinitiCall();
            Toast.makeText(MainActivity.this, "hallo", Toast.LENGTH_LONG).show();
            addFragment();
*/

/*
        ViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        ViewModel.getAllInfo().observe(this, new Observer<BaseMessageHandler<InitiaeCallMessage>>() {
            @Override
            public void onChanged(BaseMessageHandler<InitiaeCallMessage> initiaeCallMessageBaseMessageHandler) {
                Toast.makeText(MainActivity.this, "Hallo", Toast.LENGTH_SHORT).show();
                // adapter.setBaseMessageList();
            }
        });
*/

           /*     Log.d(TAG, "onChanged: Rammer VI her?");
                DialogFragment dialogFragment = new DialogFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, dialogFragment);
                ft.commit();
            */


    public void addFragment() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new Frag());
        ft.commit();
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

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                //StartCamera();
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
*/

    public void ConnectToSocket() {
        try {
            Log.d(TAG, "ConnectToSocket: Tryinger");
            //socketConnectionHandler.socketConnect();
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
    public void sendingMessage(String unCoverMessage) {
        addFragment();
    }


    @Override
    public void onOpen() {

    }

    @Override
    public void onMessageRecived(String message) {

    }

    @Override
    public void onMessageSending(String messsage) {

    }
}




/*
    @Override
    public void notifierInfinitiCall(BaseMessageHandler<InitiaeCallMessage> initiaeCallMessageBaseMessageHandler) {

        Log.d(TAG, "DialogStarterBox: DialogStarter");
        AlertDialog.Builder alertDialogBox = new AlertDialog.Builder(MainActivity.this);
        alertDialogBox.setMessage(initiaeCallMessageBaseMessageHandler.getPayload().getName());
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

}
*/

/*
@Override
    public void notifierOffer(final BaseMessageHandler<OfferMessage> offerMessageBaseMessageHandler) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "DialogStarterBox: DialogStarter");
                AlertDialog.Builder alertDialogBox = new AlertDialog.Builder(MainActivity.this);
                alertDialogBox.setMessage(offerMessageBaseMessageHandler.getPayload().getSdp());
                alertDialogBox.setCancelable(false);

                AlertDialog finalDialog = alertDialogBox.create();
                finalDialog.show();
            }
        });
    }
*/

/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

        updateUI(currentUser);
    }
*/
/*

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Log.d(TAG, "updateUI: User Already Registered");
            System.out.println(currentUser.toString());
            socketConnectionHandler.socketConnect(currentUser.getPhoneNumber());
        } else {
            Log.d(TAG, "updateUI: Here!");
            socketConnectionHandler.socketConnect(phoneNumber);
            Intent intent = new Intent(MainActivity.this, LoginManager.class);
            startActivity(intent);

        }

    }
    */







