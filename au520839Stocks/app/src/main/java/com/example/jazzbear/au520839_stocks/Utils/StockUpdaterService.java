package com.example.jazzbear.au520839_stocks.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jazzbear.au520839_stocks.Models.StockQuote;

import java.util.List;

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class StockUpdaterService extends Service {
//    public static final String LIST_OF_STOCKS_RESULT_BROADCAST = "com.jazzbear.android.BROADCAST_STOCKSERVICE_ACTION_RESULT";
//    public static final String EXTRA_STOCK_LIST_RESULT = "stock_update_intent_result";
    public static final String EXTRA_TASK_TIME_MS = "task_time";
//    private static final int NOTIFY_ID = 1337;
//    private static final String CHANNEL_ID = "stock_channel";

    // Symbol list for symbols we want to request
    final List<String> symbolList = Globals.stockSymbolList;

    RequestQueue rQueue;
    private boolean started = false;
    private long wait_time;
    private static final long DEFAULT_LOOP_WAIT_TIME = 20*1000; // wait time = 20 seconds in milliseconds

    private boolean runAsForegroundService = true;


//    private final IBinder serviceBinder = new StockUpdaterServiceBinder();

    //TODO: Implementer binding igen
//    public class StockUpdaterServiceBinder extends Binder {
//        public StockUpdaterService getService() {
//            return StockUpdaterService.this;
//        }
//    }

    public StockUpdaterService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(STOCK_LOG, "stock service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId); // not needed

        // Here we only want to start the service once so we check that started is false.
        // otherwise we wont jump into the the startup again, if started == true.
        // Since we are flipping started it needs to be !started.
        if (!started && intent != null) {
//            wait_time = intent.getLongExtra(EXTRA_TASK_TIME_MS, 5000);
            wait_time = 15*1000; // wait 15 seconds
            started = true;

            getStocksInBackground();

        } else {
            Log.d(STOCK_LOG, "Background service onStartCommand - already started!");
        }

        return START_STICKY;
    }

    // TODO: The string given here needs to be the one updated with the symbols, called from activity as well
    private void getStocksInBackground() {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Object, Object, String> asyncRequestTask = new AsyncTask<Object, Object, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d(STOCK_LOG, "Async task onPreExecute");
            }

            @Override
            protected String doInBackground(Object... objects) {
                String s = "Background job";
                try {
                    Log.d(STOCK_LOG, "Task started");
                    Thread.sleep(wait_time);

                    /* TODO: We migt want to move this out of the async task
                    * That way we can better control what request is made and with what symbols*/
                    String callUrl = makeRequestString(); // makes the request string with the newest symbols
                    // TODO: Implement stock request here.
                    sendStockRequest(callUrl);

                    Log.d(STOCK_LOG, "Task completed");
                } catch (Exception e) {
                    s+= " did not finish due to error";
                    //e.printStackTrace();
                    return s;
                }
                s += " completed after " + wait_time + "ms";
                return s;
            }

            @Override
            protected void onPostExecute(String stringResult) {
                super.onPostExecute(stringResult);
//                doBroadcastResult(stringResult);

                //if Service is still running, keep doing this recursively
                if (started) {
                    getStocksInBackground();
                }
            }
        };

        asyncRequestTask.execute();
    }

    private String makeRequestString() {
        // get the list of symbols
        final List<String> symbolList = Globals.stockSymbolList;
        int count = 0; // iterator

        StringBuilder csvList = new StringBuilder();
        for (String s : symbolList) {
            csvList.append(s);
            // Check if its the last item in the list, if not append a comma
            if (count++ != symbolList.size() -1 ) {
                csvList.append(",");
            }
        }

        return Globals.STOCK_MARKET_STRING + csvList + Globals.STOCK_QUOTE_FILTER_STRING;
    }

    private void sendStockRequest(String callUrl) {
        // send request using volley
        if (rQueue == null ) {
            // Instantiate new request que if one doesn't exist.
            rQueue = Volley.newRequestQueue(this);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, callUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        responseView.setText(response);
                        List<StockQuote> listOfResponseQuotes = StockJsonParser.parseStockListJson(symbolList, response);
                        String listString = "Response: \n";
                        for (StockQuote quote : listOfResponseQuotes) {
                            String responseValue = quote.getCompanyName() + "\n" + quote.getLatestPrice() + "\n\n";
//                            listString.append(responseString);
                            listString += responseValue;
                        }

                        // we broadcast the result to OverviewActivity
//                        responseView.setText(listString);
//                        doBroadcastResult(listString);

                        // TODO: This was for writing to database, this needs to be moved to another task
//                        addTaskList(listOfResponseQuotes);

                        Log.d(STOCK_LOG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                responseView.setText("The request failed");
//                doBroadcastResult("The request failed");
            }
        });

//        stringRequest.setTag(Globals.REQUEST_TAG);
        rQueue.add(stringRequest);
    }

//    private void doBroadcastResult(String result) {
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(LIST_OF_STOCKS_RESULT_BROADCAST); // Set the broadcast action filter
//        broadcastIntent.putExtra(EXTRA_STOCK_LIST_RESULT, result); // append the result to the intent
//        Log.d(STOCK_LOG, "Broadcasting:" + result);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent); // send it off to be caught in the other thread
//    }


    //    @Override
//    public void onCreate() {
//        super.onCreate();
//
//            if (runAsForegroundService) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    CharSequence name = getString(R.string.notifyChannelName);
//                    String description = getString(R.string.notifyChannelDescription);
//                    int importance = NotificationManager.IMPORTANCE_LOW;
//                    NotificationChannel stockNotifyChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//                    stockNotifyChannel.setDescription(description);
//                    // Register the channel with the system; you can't change the importance
//                    // or other notification behaviors after this
//                    NotificationManager stockNotifyManager =
//                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    stockNotifyManager.createNotificationChannel(stockNotifyChannel);
//
//                    // Documentation way of doing it.
////                NotificationManager stockNotifyManager = getSystemService(NotificationManager.class);
////                stockNotifyManager.createNotificationChannel(stockNotifyChannel);
//                }
//
//                // NOTE that if we want the notification to hold more info/be long we can
//                // enable expandable notification by adding a style template with setStyle()
//                // link for explanation: https://developer.android.com/training/notify-user/build-notification#builder
//                // and this: https://developer.android.com/training/notify-user/expanded
//                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.mipmap.ic_stockmarket_foreground)
//                        .setContentTitle("Stock notify")
//                        .setContentText("This is a placeholder, we need to update this as we go")
////                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                        .setTicker("A service for handling stocks")
//                        .setChannelId(CHANNEL_ID)
//                        .build();
//
//                startForeground(NOTIFY_ID, notification);
//            }
//
//        Log.d(Globals.STOCK_LOG, "Stock_background service created");
//    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // destroying the service after it no longer being in use. And setting started to false,
        // so that we can start it again later.
        started = false;
        Log.d(STOCK_LOG, "stock service destroyed");
        super.onDestroy();
    }
}
