package com.example.assi90.i_will_be_back_ground;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class IntentServiceForOffloadingTasks extends IntentService {

    private static final String INTENT_SERVICE_LOG = "INTENT_SERVICE";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.assi90.i_will_be_back_ground.action.FOO";
    private static final String ACTION_BAZ = "com.example.assi90.i_will_be_back_ground.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.assi90.i_will_be_back_ground.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.assi90.i_will_be_back_ground.extra.PARAM2";

    public IntentServiceForOffloadingTasks() {
        super("IntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentServiceForOffloadingTasks
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, IntentServiceForOffloadingTasks.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentServiceForOffloadingTasks
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, IntentServiceForOffloadingTasks.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // This is pretty much the only place we need to do something.
        if (param1 == null) {
            param1 = "undefined";
        }
        if (param2 == null) {
            param2 = "undefined";
        }

        try {
            Log.d(INTENT_SERVICE_LOG, "Foo started: " + param1 + " : " + param2);
            //we can do async stuff here, because: intent service, has own HandlerThread
            Thread.sleep(500);
//            broadcastIntentServiceResult("Foo number: " + param1, 1);
            Log.d(INTENT_SERVICE_LOG, "Foo completed");
        } catch (InterruptedException e) {
            Log.d(INTENT_SERVICE_LOG, "Foo exception");
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {


        if(param1==null){
            param1 = "undefined";
        }
        if(param2==null){
            param2 = "undefined";
        }
        try {
            Log.d(INTENT_SERVICE_LOG, "Baz started: " + param1 + " : " + param2);
            //we can do async stuff here, because: intent service, has own HandlerThread
            Thread.sleep(1500);
//            broadcastIntentServiceResult("Baz number: " + param1, 2);
            Log.d(INTENT_SERVICE_LOG, "Baz completed");
        } catch (InterruptedException e) {
            Log.d(INTENT_SERVICE_LOG, "Baz exception");
            //e.printStackTrace();
        }
    }

    // TODO: Finish the handler in main
    private void broadcastIntentServiceResult(String result, int number) {
        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(Constants.BROADCAST_INTENT_SERVICE_ACTION_BAZ);
        switch (number) {
            case 1:
                broadcastIntent.setAction(Constants.BROADCAST_INTENT_SERVICE_ACTION_FOO);
                broadcastIntent.putExtra(Constants.EXTRA_INTENT_SERVICE_FOO, result);
                Log.d(INTENT_SERVICE_LOG, "Broadcasting:" + result);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                break;
            case 2:
                broadcastIntent.setAction(Constants.BROADCAST_INTENT_SERVICE_ACTION_BAZ);
                broadcastIntent.putExtra(Constants.EXTRA_INTENT_SERVICE_BAZ, result);
                Log.d(INTENT_SERVICE_LOG, "Broadcasting:" + result);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                break;
            default:
                Log.d(INTENT_SERVICE_LOG, "Neither foo or baz action was broadcasted");
        }
    }
}
