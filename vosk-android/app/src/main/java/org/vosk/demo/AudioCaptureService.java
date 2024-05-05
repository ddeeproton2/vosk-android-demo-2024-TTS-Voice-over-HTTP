package org.vosk.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;
//Log.d(TAG, "Foreground service is running... ");

public class AudioCaptureService extends Service {
    private static final String TAG = "AudioCaptureService";
    public VoskRecognition voskrecognition;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        Log.d(TAG, "running... AudioCaptureService.onCreate()");

        //Activity voskactivity =(Activity) getApplicationContext();
        //voskrecognition = new VoskRecognition(voskactivity);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(new Intent(getApplicationContext(), AudioCaptureService.class));
        }else {
            startService(new Intent(getApplicationContext(), AudioCaptureService.class));
        }
         */
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "running... AudioCaptureService.onStartCommand()");

        //voskrecognition.recognizeMicrophone();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    "XXX69", "XXX69", NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        Intent intent1 = new Intent(this, VoskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, "XXX69")
                .setContentTitle("Service On")
                .setContentText("Your service is active")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);

        //countDownTimer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"running... AudioCaptureService.onDestroy()");
        stopForeground(true);
        stopSelf();
        //countDownTimer.cancel();
        super.onDestroy();
    }

}