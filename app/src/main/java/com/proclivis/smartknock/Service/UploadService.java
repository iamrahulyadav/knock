package com.proclivis.smartknock.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class UploadService extends Service {
    public int counter=0;
    public UploadService(Context applicationContext) {
        super();
        Log.i("UploadService", "Service Constructor");
    }

    public UploadService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("UploadService", "Start of Service");
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UploadService", "Service Stopped");
        Log.i("EXIT", "ondestroy!");
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 30 seconds
        timer.schedule(timerTask, 30000, 30000); //
    }

    /**
     * it sets the timer to print the counter every x seconds 
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //Log.i("in timer", "in timer ++++  "+ (counter++));
                //if (ConnectivityStatus.isConnected()) {
                    Log.d("UploadService", "Calling Broadcast Receiver");
                    Intent broadcastIntent = new Intent("com.vms.knockknock.UploadBroadcast");
                    sendBroadcast(broadcastIntent);
                //}
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}