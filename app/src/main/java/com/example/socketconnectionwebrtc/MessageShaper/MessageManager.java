package com.example.socketconnectionwebrtc.MessageShaper;

import android.util.Log;

import com.example.socketconnectionwebrtc.Enum.MessageType;
import com.example.socketconnectionwebrtc.EventHandler.EventHandler;
import com.example.socketconnectionwebrtc.MessageNotifier;
import com.example.socketconnectionwebrtc.Model.BaseMessageHandler;
import com.example.socketconnectionwebrtc.Model.InitiaeCallMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class MessageManager {
    private static String TAG = "MessageManager";
    Gson gson = new Gson();
    public MessageNotifier messageNotifier;

    public MessageManager(MessageNotifier messageNotifier) throws Exception {
        this.messageNotifier = messageNotifier;
    }


    public void HandlerJson(String json) {

        Log.d(TAG, "HandlerJson: Entered Method");
        Map map = gson.fromJson(json, Map.class);

        for (Object types : map.keySet()) {
            String type = (String) map.get(types);
            Log.d(TAG, "HandlerJson: In For Loop");


            switch (type) {
                case "initiateCall":

                    Log.d(TAG, "HandlerJson: InitiateCallSwitch");
                    BaseMessageHandler<InitiaeCallMessage> messageInitate =
                            gson.fromJson(json, new TypeToken<BaseMessageHandler<InitiaeCallMessage>>() {
                            }.getType());

                    Log.d(TAG, "HandlerJson: Converted Json Object");
                    //(messageInitate);
                    Log.d(TAG, "HandlerJson: Have Called Notifier");

                    break;
                case "Offer":

                    Log.d(TAG, "HandlerJson: InSide Offer");

                    break;
                case "dismissCall":
                    Log.d(TAG, "HandlerJson: DismissCalled in Switch");

                    break;

                default:
                    throw new RuntimeException("You Suck");
            }
        }
    }

}
