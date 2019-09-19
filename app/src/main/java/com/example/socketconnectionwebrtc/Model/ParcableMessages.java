package com.example.socketconnectionwebrtc.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcableMessages implements Parcelable {

    protected ParcableMessages(Parcel in) {
        this.messagePayload = in.readString();
        this.messageType = in.readString();
    }

    public static final Creator<ParcableMessages> CREATOR = new Creator<ParcableMessages>() {
        @Override
        public ParcableMessages createFromParcel(Parcel in) {
            return new ParcableMessages(in);
        }

        @Override
        public ParcableMessages[] newArray(int size) {
            return new ParcableMessages[size];
        }
    };

    private String messagePayload;
    private String messageType;

    public ParcableMessages(String messagePayload, String messageType) {
        this.messagePayload = messagePayload;
        this.messageType = messageType;
    }

    public String getMessagePayload() {
        return messagePayload;
    }

    public void setMessagePayload(String messagePayload) {
        this.messagePayload = messagePayload;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.messagePayload);
        parcel.writeString(this.messageType);
    }

    @Override
    public String toString() {
        return "ParcableMessages{" +
                "messagePayload='" + messagePayload + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
