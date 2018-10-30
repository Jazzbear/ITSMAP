package com.example.jazzbear.assignment2_stockmonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StockService extends Service {
    
    public StockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
