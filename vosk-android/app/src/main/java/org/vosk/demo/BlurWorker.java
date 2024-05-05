package org.vosk.demo;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.util.Log;
//Log.d(TAG, "Foreground service is running... ");

public class BlurWorker extends Worker {
    //private static final String TAG = "BlurWorker";
    public BlurWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
