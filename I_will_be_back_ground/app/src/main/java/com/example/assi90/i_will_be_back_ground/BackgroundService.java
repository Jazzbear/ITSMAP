package com.example.assi90.i_will_be_back_ground;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService extends Service {
    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.jazzbear.android.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";
    public static final String EXTRA_TASK_TIME_MS = "task_time";
    public static final String LOG = "BG_SERVICE";
    private static final int NOTIFY_ID = 1337;



    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
