package com.example.assi90.i_will_be_back_ground;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_LOG = "MAIN_ACTIVITY_LOG_TAG";

    TextView txtToUpdate;
    Button startBtn, stopBtn, btnBind, btnUnbind, btnGetCount, btnFoo, btnBaz, btnExit;
//    String helperText = "Press start/stop to initiate or close a service";

    private long task_time = 4*1000; // 4 ms;

    //for bound counting service
    private BoundCountingService countingService;
    private ServiceConnection countingServiceConnection;
    private boolean bound = false;
    private int count;

    //counting foo and baz tasks requests
    private int fooCount, bazCount, fooBazCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "It Works", Toast.LENGTH_SHORT).show();
                startBackgroundService(task_time);
            }
        });

        stopBtn = findViewById(R.id.btnStop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This also works!", Toast.LENGTH_SHORT).show();
                stopBackgroundService();
            }
        });

        btnBind = findViewById(R.id.btnBindCoutningService);
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindToCountingService();
            }
        });

        btnUnbind = findViewById(R.id.btnUnbindCountingService);
        btnUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unBindFromCountingService();
            }
        });

        btnGetCount = findViewById(R.id.btnGetCount);
        btnGetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check first that we are bound to a service and the counting service instance is,
                // instantiated. Otherwise we send a message that the service isn't bound.
                if (bound && countingService != null) {
                    count = countingService.getCount();
                    //update textView
                    Toast.makeText(MainActivity.this, "Count is: " + count, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not bound yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnFoo = findViewById(R.id.btnFoo);
        btnFoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fooService();
            }
        });

        btnBaz = findViewById(R.id.btnBaz);
        btnBaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bazService();
            }
        });

        btnExit = findViewById(R.id.btExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtToUpdate = findViewById(R.id.helperTextView);

        // Setup the bound service connection.
        setupConnectionToCountingService();

        // As a reminder the AsyncTask 3 parameters in the definition is params,
        // progress and result, since we do not need progress update here.
        // progress is just left as void, since we expect no callback from progress.
        // But input param and return values are strings.
        @SuppressLint("StaticFieldLeak") AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String[] strings) {
                //everything in this method is asynch and will not block UI thread
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //txtToUpdate.setText(strings[0]); //this creates a compile time error
                return strings[0];

            }

            @Override
            protected void onPostExecute(String s) {
                //everything in this method is synched with main thread
                //you can update UI widgets from there.
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show()
                txtToUpdate.setText(s); //this is fine
            }
        };

        asyncTask.execute("Yo! from the background! (AsynchTask)");
    }

    // ####################### BACKGROUND SERVICE STUFF #########################

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MAIN_LOG, "registering receivers");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(BackgroundService.BROADCAST_BACKGROUND_SERVICE_RESULT_ACTION);

        //can use registerReceiver(...)
        //but using local broadcasts for this service:
        LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceResult, iFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MAIN_LOG, "unregistering receivers");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceResult);
    }

    // to start the background service
    private void startBackgroundService(long taskTime) {
        Intent backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
        backgroundServiceIntent.putExtra(BackgroundService.EXTRA_TASK_TIME_MS, taskTime);
        startService(backgroundServiceIntent);
    }

    // to stop the background service
    private void stopBackgroundService() {
        Intent backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
        stopService(backgroundServiceIntent);
    }

    private void handleBackgroundResult(String result){
        Toast.makeText(MainActivity.this, "Got result from background service:\n" + result, Toast.LENGTH_SHORT).show();
    }


    // ####################### BOUND COUNTING SERVICE STUFF #########################
    private void setupConnectionToCountingService () {
        countingServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                //ref: http://developer.android.com/reference/android/app/Service.html
                // and also ref: https://developer.android.com/guide/components/bound-services

                countingService = ((BoundCountingService.CountingServiceBinder)service).getService();
                Log.d(MAIN_LOG, "Counting service connected");

                //Alternative way, as shown in: https://developer.android.com/guide/components/bound-services#Binder
//                BoundCountingService.CountingServiceBinder binder = (BoundCountingService.CountingServiceBinder) service;
//                countingService = binder.getService();
//                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                //ref: http://developer.android.com/reference/android/app/Service.html
                countingService = null;
                Log.d(MAIN_LOG, "Counting service disconnected");
            }
        };
    }

    void bindToCountingService() {
        // Method for binding to service
        bindService(new Intent(MainActivity.this, BoundCountingService.class),
                countingServiceConnection, Context.BIND_AUTO_CREATE); // creates it on binding if it isn't created.
        bound = true;
    }

    void unBindFromCountingService() {
        if (bound) { // only continue in case it is indeed bound.
            // Detach our existing connection.
            unbindService(countingServiceConnection);
            bound = false;
        }
    }

    // ####################### INTENT SERVICE STUFF #########################

    private void fooService(){
        fooCount++;
        fooBazCount++;
        IntentServiceForOffloadingTasks.startActionFoo(this, "FOO" + fooCount,
                " FooBaz" + fooBazCount);
    }

    private void bazService(){
        bazCount++;
        fooBazCount++;
        IntentServiceForOffloadingTasks.startActionBaz(this, "BAZ" + bazCount,
                " FooBaz" + fooBazCount);
    }

    //define our broadcast receiver for (local) broadcasts.
    // Registered and unregistered in onStart() and onStop() methods
    private BroadcastReceiver onBackgroundServiceResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(MAIN_LOG, "Broadcast recieved from bg service");
            String result = intent.getStringExtra(BackgroundService.EXTRA_TASK_RESULT);
            if (result == null) {
                result = getString(R.string.err_bg_service_result);
            }

            handleBackgroundResult(result);
        }
    };
}
