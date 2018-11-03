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

import com.example.jazzbear.au520839_stocks.DAL.StockDatabase;
import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.Globals;
import com.example.jazzbear.au520839_stocks.Utils.StockJsonParser;

import java.util.ArrayList;
import java.util.List;

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class StockService extends Service {
    // For broadcasting list of stocks
    public static final String LIST_OF_STOCKS_RESULT_BROADCAST = "com.jazzbear.android.LIST_OF_STOCKS_RESULT_BROADCAST";
    //TODO: Remove this, when response handler fixed
    public static final String EXTRA_STOCK_LIST_RESULT = "stock_list_update_intent_extra";
    // for broadcasting a single stock
    public static final String SINGLE_STOCK_RESULT_BROADCAST = "com.jazzbear.android.SINGLE_STOCK_RESULT_BROADCAST";
    public static final String EXTRA_STOCK_RESULT = "single_stock_intent_extra";
    public static final String EXTRA_STOCK_CALL_SYMBOL = "stock_symbol_intent_extra";
    // For both
    public static final String BROADCAST_ACTION_RESULT_CODE = "extra_for_action_decision";
    public static final String RESULT_SUCCESS_INTENT_CODE = "RESULT_SUCCESS";
    public static final String RESULT_FAILURE_INTENT_CODE = "RESULT_FAILURE";


    // For notifications
//    private static final int NOTIFY_ID = 1337;
//    private static final String CHANNEL_ID = "stock_channel";

    private boolean started = false;
    private boolean running = false;
    private final IBinder stockServiceBinder = new StockServiceBinder();
    private StockDatabase db;
    RequestQueue rQueue;
    private StockJsonParser jsonParser;

    // Implicit constructor
    public StockService() { }


    // This is used as the updated list.
    // When a update stock list request is done, the list is set.
    // and we we send a broadcast method to the activities bound to the service.
    // then they can access the new list through the public getter and setters.
    private List<StockQuote> serviceStockList;
    public List<StockQuote> getServiceStockList() {
        return serviceStockList;
    }
    // TODO: Setter might not be needed
//    public void setResponseStockList(List<StockQuote> serviceStockList) {
//        this.serviceStockList = serviceStockList;
//    }

    // Returns the StockService context interface so we can bind to the service from activities
    // and access the StockService's public methods. This is our only gate for communication.
    public class StockServiceBinder extends Binder {
        StockService getService() {
            return StockService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stockServiceBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        db = StockDatabase.getDatabaseInstance(this);
        Log.d(STOCK_LOG, "Stock service onCreate");

        // TODO: Needs to be changed when we want to bind to it.
        //note that the onCreate() is only called when service is first bound to (=started)
        //this will only run once in the services life time.

        // TODO: The runnable might need to be changed or moved a bit.
        running = true; // once something starts the service we want to run the thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        String csvSymbolString = makeCsvSymbolString();
                        if (!csvSymbolString.equals("")) {
                            //TODO: This could also be handled by waiting to append the symbol
                            //TODO: to the global symbolList till after the request comes back.
                            // When the symbol list gets populated after a user adds an item
                            // wait a bit first so previous operations with singlesStockRequest
                            // are sure to be done.
                            Thread.sleep(5*1000);
                            requestAllStocksInList(csvSymbolString);
                        } else {
                            Log.d(STOCK_LOG, "No symbols added to the list yet");
                        }
                        Thread.sleep(10 * 1000); // wait 15 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(STOCK_LOG, "Thread was interrupted");
                    }
                }
            }
        };
        // Create/init a thread for the runnable.
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);

        if (!started && intent != null) {
            started = true;
            // TODO: Maybe move the serviceStockList and json parser to on created
            serviceStockList = new ArrayList<StockQuote>();
            jsonParser = new StockJsonParser();

            //TODO: Maybe initialize request que here or in on created?
//            if (rQueue == null) {
//                rQueue = Volley.newRequestQueue(this);
//            }

            //TODO GET list from DB
            //TODO Setup as foreground and start the notification thingy
            //TODO Maybe move runnable auto request loop here?


        }
        Log.d(STOCK_LOG, "Stock service onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO: IMPORTANT!!!!!!!!!!! THIS NEED TO BE REMOVED LATER. SINCE WE DO WANT
        //TODO: TO CONTINUE RUNNING THE THREAD! THIS IS ONLY FOR TESTING.
        // Cleanup so the thread does not continue to run in the background
        running = false;
        Log.d(STOCK_LOG, "Service destroyed");
    }



    // ############ STOCK REQUEST METHODS ############

    private String makeCsvSymbolString() {
        int count = 0; // iterator
        StringBuilder csvString = new StringBuilder();
        if (!Globals.stockSymbolList.isEmpty()) {
            // We make a new comma separated list based on the symbols in the global symbol list.

            for (String symbol : Globals.stockSymbolList) {
                csvString.append(symbol);
                // Check if its the last item in the list, if not append a comma
                if (count++ != Globals.stockSymbolList.size() - 1) {
                    csvString.append(",");
                }
            }
        } else {
            // in case no symbols added yet, we make a empty string
            // then check on it back in the thread.
            csvString.append("");
            Log.d(STOCK_LOG,"Here is the empty csv string: " + csvString.toString());
        }
        // Then make a new request string with all the symbols in the symbolList
//        return Globals.STOCK_MARKET_STRING + csvString + Globals.STOCK_QUOTE_FILTER_STRING;
        return csvString.toString();
    }

    // for requesting a single stock.
    public void requestSingleStock(final String requestSymbol) {
        if (rQueue == null) {
            rQueue = Volley.newRequestQueue(this);
        }

        // Make a new request string with a single symbol in it.
        String requestUrl = Globals.STOCK_MARKET_STRING + requestSymbol + Globals.STOCK_QUOTE_FILTER_STRING;
//        Log.d(STOCK_LOG, "Here is the request url: \n" + requestUrl + "\n");

        StringRequest symbolRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // We broadcast the response, and it can then be parsed in the activity
                        handleSingleStockResponse(response, requestSymbol);
//                        testQuote = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleSingleStockResponse("The request for single stock failed with symbol: ", requestSymbol);
            }
        });

        rQueue.add(symbolRequest);
    }
    //Broadcast a single stock
    private void handleSingleStockResponse(String result, String requestSymbol) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SINGLE_STOCK_RESULT_BROADCAST); // Set the broadcast action filter
        broadcastIntent.putExtra(BROADCAST_ACTION_RESULT_CODE,
                getResources().getString(R.string.broadcastActionSingleStock));// Broadcast code for single stock

//        jsonParser.parseSingleStockJson()
        // TODO: No long send send the result as a string instead we want to parse
        // TODO: result to json parser and then from activity call getStockList when we have a broadcast
        broadcastIntent.putExtra(EXTRA_STOCK_RESULT, result); // append the result to the intent
        broadcastIntent.putExtra(EXTRA_STOCK_CALL_SYMBOL, requestSymbol);
        Log.d(STOCK_LOG, "Broadcasting single stock:\n" + result + "\n and symbol used:\n" + requestSymbol);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent); // send it off to be caught in the other thread
    }

    // for requesting a list of stocks
    private void requestAllStocksInList(String requestCsvSymbols) {
        // Instantiate new request queue if one doesn't exist.
        if (rQueue == null) {
            rQueue = Volley.newRequestQueue(this);
        }
        // TODO: IMPORTANT: In the request method we need to check if the symbol list is empty
        // Make the request string, using the comma separated string of symbols.
        String requestUrl = Globals.STOCK_MARKET_STRING + requestCsvSymbols + Globals.STOCK_QUOTE_FILTER_STRING;

        StringRequest symbolListRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Getting a string with json object with nested objects
                        handleStockListResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleStockListResponse("The request for a list of stocks failed");
            }
        });
        // REQUEST_TAG is so we can cancel the request from elsewhere.
        symbolListRequest.setTag(Globals.REQUEST_TAG);
        rQueue.add(symbolListRequest);
    }
    //Broadcast a list of stocks
    private void handleStockListResponse(String result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LIST_OF_STOCKS_RESULT_BROADCAST); // Set the broadcast action filter
        // TODO: No long send send the result as a string instead we want to parse
        // TODO: result to json parser and then from activity call getStockList when we have a broadcast

        broadcastIntent.putExtra(EXTRA_STOCK_LIST_RESULT, result); // append the result to the intent
        broadcastIntent.putExtra(BROADCAST_ACTION_RESULT_CODE, "multiple_stock_action"); // Broadcast code for list of stocks
        Log.d(STOCK_LOG, "Broadcasting list of stock:" + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent); // send it off to be caught in the other thread
    }


}
