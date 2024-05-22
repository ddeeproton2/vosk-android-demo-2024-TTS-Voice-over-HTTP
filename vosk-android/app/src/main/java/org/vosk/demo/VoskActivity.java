// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.vosk.demo;

import static org.vosk.demo.R.*;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.vosk.demo.network.AllCertificatesAndHostsTruster;
import org.vosk.demo.network.WebClientHTTP;
import org.vosk.demo.network.WebServerHTTP;

import android.util.Log;

import fi.iki.elonen.NanoHTTPD;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

//Log.d(TAG, "Foreground service is running... ");

public class VoskActivity<temp_result> extends Activity {

    private static final String TAG = "VoskActivity";
    public final int STATE_START = 0;
    public final int STATE_READY = 1;
    public final int STATE_DONE = 2;
    public final int STATE_FILE = 3;
    public final int STATE_MIC = 4;

    public TextView resultView;
    public VoskRecognition voskrecognition;
    private TextToSpeech tts;
    WebServerHTTP web = null;
    private String url_output_voice = "https://127.0.0.1:24443/speak?msg=";
    private String listen_port = "12121";
    private Boolean use_microphone_on_speech = false;
    private Boolean use_speech_onrecognition = false;


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        config_load();

        setContentView(R.layout.main);

        // Setup layout
        resultView = findViewById(R.id.result_text);
        setUiState(STATE_START);

        voskrecognition = new VoskRecognition(this);
        findViewById(R.id.button_options).setOnClickListener(view -> button_options());
        findViewById(R.id.recognize_mic).setOnClickListener(view -> button_recognizeMicrophone());

        ((ToggleButton) findViewById(R.id.pause)).setOnCheckedChangeListener((view, isChecked) -> pause(isChecked));

        //OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BlurWorker.class).build();
        //WorkManager.getInstance(getApplicationContext()).enqueue(request);


        Intent serviceIntent = new Intent(this, AudioCaptureService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }

        foregroundServiceRunning();


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.getDefault());
                } else {
                    // Handle initialization failure
                    Log.e("TTS", "TextToSpeech initialization failed");
                }
            }

        });

        VoskActivity base = this;
        // If TextToSpeech running, pause listening
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                if(!base.use_microphone_on_speech) {
                    pause(true);
                }
            }

            @Override
            public void onDone(String utteranceId) {
                if(!base.use_microphone_on_speech) {
                    pause(false);
                }
            }

            @Override
            public void onError(String utteranceId) {
                if(!base.use_microphone_on_speech) {
                    pause(false);
                }
            }
        });

        if(web == null) {
            try {
                web = new WebServerHTTP("0.0.0.0", Integer.parseInt(this.listen_port), this);
            } catch (IOException e) {
                //throw new RuntimeException(e);
                button_options();
                TextView textView3 = findViewById(R.id.textView3);
                textView3.setText("Error port (allowed ports start: 1025 end : 65535)");
                textView3.setTypeface(null, Typeface.BOLD);
                textView3.setTextColor(Color.RED);
            }
        }

        AllCertificatesAndHostsTruster.apply();

    }

    public void config_save(){
        // Set CONFIG
        SharedPreferences sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("url_output_voice", this.url_output_voice);
        editor.putString("listen_port",this.listen_port);
        editor.putBoolean("use_microphone_on_speech",this.use_microphone_on_speech);
        editor.putBoolean("use_speech_onrecognition",this.use_speech_onrecognition);

        editor.apply();
    }

    public void config_load(){
        // Get CONFIG
        SharedPreferences sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        this.url_output_voice = sharedPreferences.getString("url_output_voice", this.url_output_voice);
        this.listen_port = sharedPreferences.getString("listen_port", this.listen_port);
        this.use_microphone_on_speech = sharedPreferences.getBoolean("use_microphone_on_speech",this.use_microphone_on_speech);
        this.use_speech_onrecognition = sharedPreferences.getBoolean("use_speech_onrecognition",this.use_speech_onrecognition);
    }




    public void button_options(){
        config_load();

        // Reference the view you want to remove
        View viewToRemove = findViewById(id.config_layout);

        // Remove the view from the main layout
        if (viewToRemove != null) {
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.config, null);
        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.addView(layout, 0);



        TextInputEditText url_output_voice = findViewById(R.id.url_output_voice);
        url_output_voice.setText(this.url_output_voice);

        EditText listen_port = findViewById(id.listen_port);
        listen_port.setText(this.listen_port);

        Button button_options = findViewById(id.button_options);
        button_options.setVisibility(View.GONE);

        VoskActivity base = this;

        Button button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewToRemove = findViewById(id.config_layout);
                if (viewToRemove != null) {
                    mainLayout.removeView(viewToRemove);
                    Button button_options = findViewById(id.button_options);
                    button_options.setVisibility(View.VISIBLE);
                }
            }
        });


        Button button_reset = findViewById(id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText url_output_voice = findViewById(R.id.url_output_voice);
                url_output_voice.setText("https://127.0.0.1:24443/speak?msg=");
            }
        });


        Button button_save = findViewById(id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText url_output_voice = findViewById(R.id.url_output_voice);
                base.url_output_voice = String.valueOf(url_output_voice.getText());

                EditText listen_port = findViewById(R.id.listen_port);
                base.listen_port = String.valueOf(listen_port.getText());

                if(web != null) {
                    web.stopServer();
                    web = null;
                }

                boolean isError = false;
                int port = Integer.parseInt(base.listen_port);
                if(port < 1024 || port > 65535){
                    isError = true;
                }
                if(!isError) {
                    try {
                        web = new WebServerHTTP("0.0.0.0", port, base);
                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                        isError = true;
                    }
                }
                TextView textView3 = findViewById(R.id.textView3);
                if(isError){
                    textView3.setText("Error port (allowed ports start: 1024 end : 65535)");
                    textView3.setTypeface(null, Typeface.BOLD);
                    textView3.setTextColor(Color.RED);
                }else{
                    base.config_save();
                    textView3.setText("http://127.0.0.1:PORT/?message=Test");
                    textView3.setTypeface(null, Typeface.NORMAL);
                    textView3.setTextColor(getResources().getColor(R.color.design_default_color_primary, getTheme()));

                    View viewToRemove = findViewById(R.id.config_layout);
                    if (viewToRemove != null) {
                        mainLayout.removeView(viewToRemove);
                        Button button_options = findViewById(R.id.button_options);
                        button_options.setVisibility(View.VISIBLE);
                    }
                }


            }
        });

        Button button_test_file_voice = findViewById(R.id.button_test_file_voice);
        button_test_file_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voskrecognition.recognizeFile();
                String url = base.url_output_voice + "one%20two%20three%20four%20five";
                WebClientHTTP.sendGetInBackground(url);
            }
        });


        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_use_microphone_on_speech = findViewById(R.id.use_microphone_on_speech);
        switch_use_microphone_on_speech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                base.use_microphone_on_speech = isChecked;
                config_save();
            }
        });

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_speech_onrecognition = findViewById(R.id.switch_speech_onrecognition);
        switch_speech_onrecognition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                base.use_speech_onrecognition = isChecked;
                config_save();
            }
        });

    }

    public void button_recognizeMicrophone(){
        voskrecognition.recognizeMicrophone();

    }

    public void say(String text){
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "");
        System.out.println(text);
    }

    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (AudioCaptureService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    public boolean isServiceRunning(Context context, Class<?> serviceClas) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClas.getName())) {
                return true;
            }
        }

        return false;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.voskrecognition.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        voskrecognition.onDestroy();
        if(web != null) {
            web.stopServer();
        }
    }

    String temp_result = "";


    public void onResult(String hypothesis) throws UnsupportedEncodingException {
        JSONObject jsonObject = JSONObject.parseObject(hypothesis);
        hypothesis = jsonObject.get("text").toString();
        if(!hypothesis.isEmpty()) {
            temp_result = temp_result + hypothesis + ". ";
            System.out.println(hypothesis + "result");
            resultView.setText(temp_result);
            if(this.use_speech_onrecognition) {
                this.say(hypothesis);
            }
            String encodedMessage = URLEncoder.encode(hypothesis, "UTF-8");
            String url = this.url_output_voice + encodedMessage;
            WebClientHTTP.sendGetInBackground(url);
        }
    }


    public void onFinalResult(String hypothesis) {
        JSONObject jsonObject = JSONObject.parseObject(hypothesis);
        hypothesis = jsonObject.get("text").toString();
        if(!hypothesis.isEmpty()) {
            resultView.setText(temp_result + hypothesis);
            System.out.println(hypothesis + "final");
            setUiState(STATE_DONE);
        }
    }


    public void onPartialResult(String hypothesis) {
        JSONObject jsonObject = JSONObject.parseObject(hypothesis);
        hypothesis = jsonObject.get("partial").toString();
        if(!hypothesis.isEmpty()) {
            System.out.println(hypothesis + "part");
            resultView.setText(temp_result + hypothesis);
        }
    }


    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }


    public void onTimeout() {
        setUiState(this.STATE_DONE);
    }


    public void setUiState(int state) {
        switch (state) {
            case STATE_START:
                resultView.setText(R.string.preparing);
                resultView.setMovementMethod(new ScrollingMovementMethod());
                //findViewById(R.id.button_test_file_voice).setEnabled(false);
                findViewById(R.id.recognize_mic).setEnabled(false);
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_READY:
                resultView.setText(R.string.ready);
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                //findViewById(R.id.button_test_file_voice).setEnabled(true);
                findViewById(R.id.recognize_mic).setEnabled(true);
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_DONE:
                //((Button) findViewById(R.id.button_options)).setText(R.string.options);
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                //findViewById(R.id.button_test_file_voice).setEnabled(true);
                findViewById(R.id.recognize_mic).setEnabled(true);
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_FILE:
                //((Button) findViewById(R.id.button_options)).setText(R.string.stop_file);
                resultView.setText(getString(R.string.starting));
                findViewById(R.id.recognize_mic).setEnabled(false);
                //findViewById(R.id.button_test_file_voice).setEnabled(true);
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_MIC:
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
                resultView.setText(getString(R.string.say_something));
                //findViewById(R.id.button_test_file_voice).setEnabled(false);
                findViewById(R.id.recognize_mic).setEnabled(true);
                findViewById(R.id.pause).setEnabled((true));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }



    public void setErrorState(String message) {
        resultView.setText(message);
        ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        findViewById(R.id.button_options).setEnabled(false);
        findViewById(R.id.recognize_mic).setEnabled(false);
    }


    private void pause(boolean checked) {
        voskrecognition.pause(checked);
    }



    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, List<String>> parms = session.getParameters();
        msg += "<form action='?' method='get'>\n  <p>Your message: <input type='text' name='message'></p>\n <input type=submit value=ok /></form>\n";
        if (parms.get("message") != null) {
            msg += "<p>Hello, " + parms.get("message") + "!</p>";
            this.say(parms.get("message").toString());
        }
        return newFixedLengthResponse(msg + "</body></html>\n");

    }
}
