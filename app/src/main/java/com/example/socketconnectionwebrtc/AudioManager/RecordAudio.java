package com.example.socketconnectionwebrtc.AudioManager;

import android.media.AudioFormat;
import android.os.Environment;
import android.util.Log;

import org.jetbrains.annotations.Nullable;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import static org.webrtc.EglBase.lock;

public class RecordAudio implements JavaAudioDeviceModule.SamplesReadyCallback {
    private static final String TAG = "RecordAudio";
    private static final long MAX_FILE_SIZE_IN_BYTES = 58348800L;
    @Nullable
    private OutputStream rawAudioFileOutputStream;
    private boolean isRunning;
    private long fileSizeInBytes;
    private final ExecutorService executor;

    public RecordAudio(ExecutorService executor) {
        this.executor = executor;
    }

    public boolean start () {
        if (!isExternalStorageWritable()) {
            return false;
        }
        synchronized (lock) {
            isRunning = true;
        }
        return true;
    }

    public void stop() {
        synchronized (lock) {
            isRunning = false;
            if (rawAudioFileOutputStream != null) {
                try {
                    rawAudioFileOutputStream.close();
                } catch (IOException e) {

                }
                rawAudioFileOutputStream = null;
            }
            fileSizeInBytes = 0;
        }
    }
    // Checks if external storage is available for read and write.
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples audioSamples) {
        if (audioSamples.getAudioFormat() != AudioFormat.ENCODING_PCM_16BIT) {
            return;
        }
        synchronized (lock) {
            if (!isRunning) {
                return;
            }
            if (rawAudioFileOutputStream == null) {
                openRawAudioOutputFile(audioSamples.getSampleRate(), audioSamples.getChannelCount());
                fileSizeInBytes = 0;
            }
        }
        executor.execute(()->{
            if (rawAudioFileOutputStream != null) {
                try {
                    if (fileSizeInBytes < MAX_FILE_SIZE_IN_BYTES) {
                        rawAudioFileOutputStream.write(audioSamples.getData());
                        fileSizeInBytes += audioSamples.getData().length;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openRawAudioOutputFile(int sampleRate, int channelCount) {
        final String fileName = Environment.getExternalStorageDirectory().getPath() + File.separator
                + "recorded_audio_16bits_" + String.valueOf(sampleRate) + "Hz"
                + ((channelCount == 1) ? "_mono" : "_stereo") + ".pcm";
        final File outputFile = new File(fileName);
        try {
            rawAudioFileOutputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to open audio output file: " + e.getMessage());
        }
        Log.d(TAG, "Opened file for recording: " + fileName);
    }
}
