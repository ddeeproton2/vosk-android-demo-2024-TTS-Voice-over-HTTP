package org.vosk.demo;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
//Log.d(TAG, "Foreground service is running... ");

public class VoskRecognition implements RecognitionListener {

    //private static final String TAG = "VoskRecognition";
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private final Context context;
    VoskActivity base;
    String temp_result = "";
    public VoskRecognition(VoskActivity _base){
        //this.context = _context;
        this.base = _base;
        this.context = this.base.getApplicationContext();
        LibVosk.setLogLevel(LogLevel.INFO);


        // Check if user has given permission to record audio, init the model after permission is granted
        int permissionCheck = ContextCompat.checkSelfPermission(this.context, android.Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_base, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            this.initModel();
        }

        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            // Permission déjà accordée, activer le microphone ici
        }
         */

    }

    public void initModel() {

        StorageService.unpack(this.context, "vosk-model-small-fr-0.22", "model",
                (model) -> {
                    this.model = model;
                    this.base.setUiState(this.base.STATE_READY);
                },
                (exception) -> this.base.setErrorState("Failed to unpack the model" + exception.getMessage()));
    }

    public void recognizeMicrophone() {
        if (this.speechService != null) {
            this.micro_stop();
        } else {
            this.micro_start();
        }
    }

    public void micro_start(){
        this.base.setUiState(this.base.STATE_MIC);
        // ========== AVEC BLUETOOTH
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT){
                    // Pause playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Resume playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Stop playback
                }
            }
        };

        /*
        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(
                afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC, // There's no AudioAttributes, just the more general constant.
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN
        );
        */

        VoskRecognition self = this;
        this.base.registerReceiver(new BroadcastReceiver() {
            @SuppressLint("WakelockTimeout")
            @Override
            public void onReceive(Context context_, Intent intent) {

                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                //Log.d(TAG, "Audio SCO state: " + state);
                try {
                    //MyWakeLockManager.acquireWakeLock(getApplicationContext());
                    self.base.temp_result="";
                    Recognizer rec = new Recognizer(model, 16000.0f);
                    self.speechService = new SpeechService(rec, 16000.0f);
                    self.speechService.startListening(self);
                    self.base.unregisterReceiver(this);
                    //MyWakeLockManager.releaseWakeLock();
                } catch (IOException e) {
                    self.base.setErrorState(e.getMessage());
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
        //Log.d(TAG, "starting bluetooth");
        audioManager.startBluetoothSco();
    }


    public void micro_stop(){
        this.base.setUiState(this.base.STATE_DONE);
        this.speechService.stop();
        this.speechService = null;

    }

    public void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }


    public void recognizeFile() {
        if (speechStreamService != null) {
            base.setUiState(this.base.STATE_DONE);
            speechStreamService.stop();
            speechStreamService = null;
        } else {
            base.setUiState(this.base.STATE_FILE);
            try {
                Recognizer rec = new Recognizer(model, 16000.f, "[\"one zero zero zero one\", " +
                        "\"oh zero one two three four five six seven eight nine\", \"[unk]\"]");

                InputStream ais = base.getAssets().open(
                        "10001-90210-01803.wav");
                if (ais.skip(44) != 44) throw new IOException("File too short");

                speechStreamService = new SpeechStreamService(rec, ais, 16000);
                speechStreamService.start(this);
            } catch (IOException e) {
                base.setErrorState(e.getMessage());
            }
        }
    }



    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                //initModel();
                this.initModel();
            } else {
                this.base.finish();
            }
        }
    }


    @Override
    public void onPartialResult(String hypothesis) {
        base.onPartialResult(hypothesis);
    }

    @Override
    public void onResult(String hypothesis) {
        try {
            base.onResult(hypothesis);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFinalResult(String hypothesis) {
        base.onFinalResult(hypothesis);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onError(Exception exception) {
        base.onError(exception);
    }

    @Override
    public void onTimeout() {
        base.onTimeout();
    }

    public void onDestroy() {
        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
    }

}
