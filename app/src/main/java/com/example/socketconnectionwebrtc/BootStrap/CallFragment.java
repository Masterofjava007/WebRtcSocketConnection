package com.example.socketconnectionwebrtc.BootStrap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socketconnectionwebrtc.R;

import org.webrtc.RendererCommon;

public class CallFragment extends Fragment {
    private TextView contactView;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private ImageButton toggleMuteButton;
    private TextView captureFormatText;
    private SeekBar captureFormatSlider;
    private onCallEvents callEvents;
    private RendererCommon.ScalingType scalingType;
    private boolean videoCAllEnabled = true;

    public interface onCallEvents {
        void onCallHangup();
        void onCameraSwitch();
        void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);
        void onCaptureFormatChange(int width, int height, int framerate);
        boolean onToggleMic();

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_call, container, false);

        contactView = controlView.findViewById(R.id.contact_name_call);
        ImageButton disconnectButton = controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = controlView.findViewById(R.id.button_call_scaling_mode);
        toggleMuteButton = controlView.findViewById(R.id.button_call_toggle_mic);
        captureFormatText = controlView.findViewById(R.id.capture_format_text_call);
        captureFormatSlider = controlView.findViewById(R.id.capture_format_slider_call);


        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEvents.onCallHangup();
            }
        });
        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEvents.onCameraSwitch();
            }
        });
        videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scalingType == RendererCommon.ScalingType.SCALE_ASPECT_FILL) {

                    scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
                } else {

                    scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
                }
                callEvents.onVideoScalingSwitch(scalingType);
            }
        });
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;

        toggleMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = callEvents.onToggleMic();
                toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
            }
        });

        return controlView;
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabeld = false;
        Bundle args = getArguments();
        if(args != null) {
            String contactName = args.getString(MainActivity.EXTRA_DATA_CHANNEL_ENABLED);
            contactView.setText(contactName);
            videoCAllEnabled = args.getBoolean(MainActivity.EXTRA_VIDEO_CALL, true);
            captureSliderEnabeld = videoCAllEnabled && args.getBoolean(MainActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, true);
        }
        if (!videoCAllEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
        if (captureSliderEnabeld) {
            captureFormatSlider.setOnSeekBarChangeListener(
                    new CapturerQualityController(captureFormatText, callEvents));
        } else {
            captureFormatText.setVisibility(View.GONE);
            captureFormatSlider.setVisibility(View.GONE);
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (onCallEvents) activity;
    }
}
