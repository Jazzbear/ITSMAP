package com.example.assi90.i_will_be_back_ground;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BackgroundService extends Service {
    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT_ACTION = "com.jazzbear.android.BROADCAST_BACKGROUND_SERVICE_RESULT_ACTION";
    public static final String EXTRA_TASK_RESULT = "task_result";
    public static final String EXTRA_TASK_TIME_MS = "task_time";
    public static final String LOG = "BG_SERVICE"; // Tag for our debug log message
    private static final int NOTIFY_ID = 1337;
    private static final String CHANNEL_ID = "myChannel";

//    private static final String CHANNEL_DEFAULT_IMPORTANCE = LOW

    private boolean started = false;
    private long wait;
    private static final long LOOP_WAIT_TIME = 20*1000; // wait time = 20 seconds in milliseconds

    //whether to run as a ForegroundService (with permanent notification, harder to kill)
    private boolean runAsForegroundService = true;

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG, "Background service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Here we only want to start the service once so we check that started is false.
        // otherwise we wont jump into the the startup again, if started == true.
        if (!started && intent != null) {
            // set wait with the the intent extra. The other parameter is its default value 20 seconds.
            wait = intent.getLongExtra(EXTRA_TASK_TIME_MS, LOOP_WAIT_TIME);
            Log.d(LOG, "Background service onStartCommand with wait: " + wait + "ms");
            // set started to true so we keep it from starting the service again while running.
            started = true;

            if (runAsForegroundService) {
//                Intent notificationIntent = new Intent(BackgroundService.this, MainActivity.class);
//                PendingIntent pendingIntent =
//                        PendingIntent.getActivity(this, 0, notificationIntent, 0);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // Do not do this at home :)
                    CharSequence name = "Visible myChannel";
                    int importance = NotificationManager.IMPORTANCE_LOW;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                Notification notification =
                        new NotificationCompat.Builder(this, "myChannel")
                                .setContentTitle(getText(R.string.notification_title))
                                .setContentText(getText(R.string.notification_message))
                                .setSmallIcon(R.mipmap.ic_launcher)
//                                .setContentIntent(pendingIntent)
                                .setTicker(getText(R.string.ticker_text))
                                .setChannelId(CHANNEL_ID)
                                .build();

                //calling Android to
                startForeground(NOTIFY_ID, notification);
            }
            //do background thing
            doBackgroundThing(wait);
        } else {
            Log.d(LOG, "Background service onStartCommand - already started!");
        }
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // this service is not for binding: return null.
        return null;
    }

    private void doBackgroundThing(final long waitTimeInMillis) {

        //create asynch tasks that sleeps for waitTimeMillis ms and then sends broadcast
        @SuppressLint("StaticFieldLeak") AsyncTask<Object, Object, String> task
                = new AsyncTask<Object, Object, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Object[] params) {
                String s = "Background job";
                try {
                    Log.d(LOG, "Task started");
                    Thread.sleep(waitTimeInMillis);
                    Log.d(LOG, "Task completed");
                } catch (Exception e) {
                    s+= " did not finish due to error";
                    //e.printStackTrace();
                    return s;
                }

                s += " completed after " + waitTimeInMillis + "ms";
                return s;
            }


            @Override
            protected void onPostExecute(String stringResult) {
                super.onPostExecute(stringResult);
                doBroadcastResult(stringResult);

                //if Service is still running, keep doing this recursively
                if(started){
                    doBackgroundThing(waitTimeInMillis);
                }
            }
        };
        //start task
        task.execute();

    }

    private void doBroadcastResult(String result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_BACKGROUND_SERVICE_RESULT_ACTION);
        broadcastIntent.putExtra(EXTRA_TASK_RESULT, result);
        Log.d(LOG, "Broadcasting:" + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }

    @Override
    public void onDestroy() {
        // destroying the service after it no longer being in use. And setting started to false,
        // so that we can start it again later.
        started = false;
        Log.d(LOG, "Background service destroyed");
        super.onDestroy();
    }

    //    private final class ServiceHandler extends Handler {
//        public ServiceHandler(Looper looper) {
//            super(looper);
//        }
//        @Override
//        public void handleMessage(Message msg) {
//            // Normally we would do some work here, like download a file.
//            // For our sample, we just sleep for 5 seconds.
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Restore interrupt status.
//                Thread.currentThread().interrupt();
//            }
//            // Stop the service using the startId, so that we don't stop
//            // the service in the middle of handling another job
//            stopSelf(msg.arg1);
//        }
//    }
}
