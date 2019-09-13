package com.example.socketconnectionwebrtc.BootStrap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.socketconnectionwebrtc.EventHandler.IEventListener;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.R;

import java.util.zip.Inflater;

public class myFragment extends DialogFragment implements IEventListener {
    private static final String TAG = "myFragment";
    private View view;
    private String name;
    private FrameLayout frameLayout;
    private AlertDialog alertDialogBox;

    public myFragment() {

    }
/*
    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState){
        
    }
    */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = inflater.inflate(R.layout.activity_main, container, true);

        frameLayout = view.findViewById(R.id.frameLayout);



        alertDialogBox.setMessage(getName());

        alertDialogBox.setCancelable(false);

        alertDialogBox.show();
        Log.d(TAG, "DialogStarterBox: DialogStarter");




        return view;
    }




    @Override
    public void execute(String payload) {
        Log.d(TAG, "execute: JA");
        setName(payload);



    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
