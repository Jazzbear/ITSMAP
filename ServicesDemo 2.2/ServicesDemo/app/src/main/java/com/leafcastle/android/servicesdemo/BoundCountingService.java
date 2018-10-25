package com.leafcastle.android.servicesdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BoundCountingService extends Service {

    private int count;
    private boolean running = false;

    //The IBinder instance to return
    private final IBinder binder = new CountingServiceBinder();

    //extend the Binder class - we will return and instance of this in the onBind()
    public class CountingServiceBinder extends Binder {
        //return ref to service (or at least an interface) that activity can call public methods on
        BoundCountingService getService() {
            return BoundCountingService.this;
        }
    }

    public BoundCountingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //note that the onCreate() is only called when service is first bound to (=started)
        //this will only run once in the services life time

        count = 0;
        running = true;

        //create a good ol' java Thread and let it sleep for a second and count up in a loop
        //first we create the runnable, then we start the thread
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(running == true) {
                    count++;
                    try {
                        Thread.sleep(1000); //sleep in seperate Threads - not the classroom!! :)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    //note we do not override onStartCommand for this bound service
    //it is possible to both be a started service and support binding

    @Override
    //very important! return your IBinder (your custom Binder)
    public IBinder onBind(Intent intent) {
       return binder;
    }

    //simple method for returning current count
    public int getCount(){
        return count;
    }

}
