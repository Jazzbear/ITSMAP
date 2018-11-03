package com.example.jazzbear.au520839_stocks;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.Globals;

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class StockService extends Service {
    // For broadcasting list of stocks
    public static final String LIST_OF_STOCKS_RESULT_BROADCAST = "com.jazzbear.android.LIST_OF_STOCKS_RESULT_BROADCAST";
    public static final String EXTRA_STOCK_LIST_RESULT = "stock_list_update_intent_extra";
    // for broadcasting a single stock
    public static final String SINGLE_STOCK_RESULT_BROADCAST = "com.jazzbear.android.SINGLE_STOCK_RESULT_BROADCAST";
    public static final String EXTRA_STOCK_RESULT = "single_stock_intent_extra";
    public static final String EXTRA_STOCK_CALL_SYMBOL = "stock_symbol_intent_extra";
    // For both
    public static final String EXTRA_INTENT_CODE = "intent_code_for_broadcasts";


    // For notifications
//    private static final int NOTIFY_ID = 1337;
//    private static final String CHANNEL_ID = "stock_channel";

    private boolean started = false;
    private boolean running = false;
    RequestQueue rQueue;
    private final IBinder stockServiceBinder = new StockServiceBinder();
//    private long wait_time = 15*1000; // 15 seconds wait time for thread
//    String testQuote;


    public class StockServiceBinder extends Binder {
        StockService getService() {
            return StockService.this;
        }
    }

    public StockService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Needs to be changed when we want to bind to it.
        //note that the onCreate() is only called when service is first bound to (=started)
        //this will only run once in the services life time.

        running = true; // once something binds to the service we want to run the thread

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        String callUrl = makeRequestString();
                        requestAllStocksInList(callUrl);
                        Thread.sleep(15 * 1000); // wait 15 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(STOCK_LOG, "Thread was interrupted");
                    }
                }
            }
        };
        // Create a thread for the runnable.
        Thread thread = new Thread(runnable);
        //And start it:
        thread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        Log.d(STOCK_LOG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stockServiceBinder;
    }

    // ############ STOCK REQUEST METHODS ############

    private String makeRequestString() {
        int count = 0; // iterator

        // We make a new comma separated list based on the symbols in the global symbol list.
        StringBuilder csvList = new StringBuilder();
        for (String symbol : Globals.stockSymbolList) {
            csvList.append(symbol);
            // Check if its the last item in the list, if not append a comma
            if (count++ != Globals.stockSymbolList.size() - 1) {
                csvList.append(",");
            }
        }
        // Then make a new request string with all the symbols in the symbolList
        return Globals.STOCK_MARKET_STRING + csvList + Globals.STOCK_QUOTE_FILTER_STRING;
    }

    // for requesting a single stock.
    public void requestSingleStock(final String requestSymbol) {
        if (rQueue == null) {
            rQueue = Volley.newRequestQueue(this);
        }

        // Make a new request string with a single symbol in it.
        String requestUrl = Globals.STOCK_MARKET_STRING + requestSymbol + Globals.STOCK_QUOTE_FILTER_STRING;
        Log.d(STOCK_LOG, "Here is the request url: \n" + requestUrl + "\n");

        StringRequest symbolRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // We broadcast the response, and it can then be parsed in the activity
                        doStockBroadcast(response, requestSymbol);
//                        testQuote = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                doStockBroadcast("The request for single stock failed with symbol: ", requestSymbol);
            }
        });

        rQueue.add(symbolRequest);
    }
    //Broadcast a single stock
    private void doStockBroadcast(String result, String requestSymbol) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SINGLE_STOCK_RESULT_BROADCAST); // Set the broadcast action filter
        broadcastIntent.putExtra(EXTRA_STOCK_RESULT, result); // append the result to the intent
        broadcastIntent.putExtra(EXTRA_STOCK_CALL_SYMBOL, requestSymbol);
        broadcastIntent.putExtra(EXTRA_INTENT_CODE, "111"); // Broadcast code for single stock
        Log.d(STOCK_LOG, "Broadcasting single stock:\n" + result + "\n and symbol used:\n" + requestSymbol);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent); // send it off to be caught in the other thread
    }

    // for requesting a list of stocks
    private void requestAllStocksInList(String requestUrl) {
        // Instantiate new request queue if one doesn't exist.
        if (rQueue == null) {
            rQueue = Volley.newRequestQueue(this);
        }

        StringRequest symbolListRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Getting a string with json object with nested objects
                        doBroadcastStockListResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                doBroadcastStockListResult("The request for a list of stocks failed");
            }
        });
        // REQUEST_TAG is so we can cancel the request from elsewhere.
        symbolListRequest.setTag(Globals.REQUEST_TAG);
        rQueue.add(symbolListRequest);
    }
    //Broadcast a list of stocks
    private void doBroadcastStockListResult(String result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LIST_OF_STOCKS_RESULT_BROADCAST); // Set the broadcast action filter
        broadcastIntent.putExtra(EXTRA_STOCK_LIST_RESULT, result); // append the result to the intent
        broadcastIntent.putExtra(EXTRA_INTENT_CODE, "222"); // Broadcast code for list of stocks
        Log.d(STOCK_LOG, "Broadcasting list of stock:" + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent); // send it off to be caught in the other thread
    }


}
