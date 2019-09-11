package com.example.socketconnectionwebrtc.EventHandler;


import android.util.Log;


public class UiHandler implements IUiHandler {
    public interface Callback {
        void notifyCamera();
    }
    private Callback callback;

    private static final String TAG = "UiHandler";

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    @Override
    public void notifyForViewCameraStart() {
        Log.d(TAG, "notifyForViewCameraStart: Hallo");

        //Broadcastreceiver i denne klasse

        //activty LocaleRecevier
        callback.notifyCamera();

  }

    @Override
    public void notifyCamera() {

    }
}
