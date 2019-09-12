package com.example.socketconnectionwebrtc.BootStrap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socketconnectionwebrtc.EventHandler.IEventListener;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.R;

public class myFragment extends DialogFragment {
    private static final String TAG = "myFragment";
    private View view;

    public myFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater infalte, ViewGroup container, Bundle savedInstanceState) {
        return infalte.inflate(R.layout.activity_my_fragment, container, false);

    }

}
