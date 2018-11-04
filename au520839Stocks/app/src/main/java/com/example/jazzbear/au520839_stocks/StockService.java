package com.example.jazzbear.au520839_stocks;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class StockService extends Service {
    // for broadcasting a single stock
    public static final String SINGLE_STOCK_BROADCAST_ACTION = "com.jazzbear.android.SINGLE_STOCK_BROADCAST_ACTION";
//    public static final String EXTRA_STOCK_RESULT = "single_stock_intent_extra";
    //    public static final String EXTRA_STOCK_CALL_SYMBOL = "stock_symbol_intent_extra";
    // For broadcasting list of stocks
    public static final String LIST_OF_STOCKS_BROADCAST_ACTION = "com.jazzbear.android.LIST_OF_STOCKS_BROADCAST_ACTION";
//    public static final String EXTRA_STOCK_LIST_RESULT = "stock_list_update_intent_extra";
    // This is so we can set it to either to either single_stock_action or multi_stock_action
    public static final String BROADCAST_ACTION_RESULT_CODE = "extra_for_action_decision";

    //TODO: Should be removed from here and in overviewactivity.
    //TODO: Not being used anymore due to error handling done in response
//    public static final String RESULT_SUCCESS_INTENT_CODE = "RESULT_SUCCESS";
//    public static final String RESULT_FAILURE_INTENT_CODE = "RESULT_FAILURE";

    // For notifications
//    private static final int NOTIFY_ID = 1337;
//    private static final String CHANNEL_ID = "stock_channel";

    private boolean started = false;
    private boolean running = false;
    private final IBinder stockServiceBinder = new StockServiceBinder();
    private StockDatabase db;
    RequestQueue rQueue;
//    private String csvSymbols;
//    private List<String> databaseStockSymbols = new ArrayList<>();


    // Implicit constructor
    public StockService() {
    }

    // This is used as the updated list.
    // When a update stock list request is done, the list is set.
    // and we we send a broadcast method to the activities bound to the service.
    // then they can access the new list through the public getter and setters.
    private List<StockQuote> serviceStockList;
    private List<String> uniqueSymbolList = new ArrayList<>();

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
        Log.d(STOCK_LOG, "Stock service onCreate");
        db = StockDatabase.getDatabaseInstance(this);
        serviceStockList = new ArrayList<>(); // was null when in onStart
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO: IMPORTANT!!!!!!!!!!! THIS MIGHT NEED TO BE REMOVED LATER.
        //TODO: Depends if the service keeps running once the app closes etc. Because it should.
        // Cleanup so the thread does not continue to run in the background
//        running = false;
        Log.d(STOCK_LOG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Inspired by Stunt code from Lecture 6
        //Wait until something starts the service, with either startService() or by binding to it.
        if (!started && intent != null) {
            started = true; //flip it so it stays started
            // Instantiate new request queue if one doesn't exist.
            if (rQueue == null) {
                rQueue = Volley.newRequestQueue(this);
            }
            // once something starts the service we want to start the runnable
            running = true;
            // Setup the runnable
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //We want to make sure we only request stocks that exists in the database

                    //TODO: Need to fix this right now i will just end up matching with and empty database list down in the async task.
                    serviceStockList = db.stockQuoteDao().getAllStocks();
                    firstTimeUpdateOverview();
                    for (StockQuote index : serviceStockList) {
                        Globals.stockSymbolList.add(index.getStockSymbol());
                    }
                    while (running) {
                        try {
                            String csvSymbolString = makeCsvSymbolString();
                            if (!csvSymbolString.equals("")) {
                                //TODO: This could also be handled by waiting to append the symbol
                                //TODO: to the global symbolList till after the request comes back.

                                // When the symbol list gets populated after a user adds an item
                                // wait a bit first so previous operations with singlesStockRequest
                                // are sure to be done.
//                                Thread.sleep(5 * 1000);
                                requestAllStocksInList(csvSymbolString);
                            } else {
                                Log.d(STOCK_LOG, "No symbols added to the list yet");
                            }
                            // TODO: Need to be changed so we wait 2 MINUTTES!
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
            //TODO: Start foreground thingy should be done here also
        }
        Log.d(STOCK_LOG, "Stock service onStartCommand");
        return START_STICKY;
    }

    private void firstTimeUpdateOverview() {
        Intent firstTimeUpdate = new Intent();
        firstTimeUpdate.setAction(LIST_OF_STOCKS_BROADCAST_ACTION);
        firstTimeUpdate.putExtra(BROADCAST_ACTION_RESULT_CODE, getResources().getString(R.string.broadcastActionMultiStock));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(firstTimeUpdate);
    }

    // ################# STOCK REQUEST METHODS #################

    private String makeCsvSymbolString() {
        int count = 0; // iterator
        StringBuilder csvString = new StringBuilder();
        //First check if there are any symbols in the list
        if (!Globals.stockSymbolList.isEmpty()) {
            //Since the global symbolist can have more than one of the same symbol.
            //But for both the request and a later database query we only want 1 of each symbol.
            //Create a hash-map for the symbolist, that way multiple occurrences of the same symbol is ignored.
            Set<String> symbol_hash_map = new HashSet<>(Globals.stockSymbolList);
            uniqueSymbolList.clear();
            uniqueSymbolList.addAll(symbol_hash_map); //Add the uniquely hashed list to a local symbol list.

            // We make a new comma separated list based on the hash-map containing only one
            // occurrence of each symbol.
            for (String symbol : symbol_hash_map) {
                //Then build a comma separated string.
                csvString.append(symbol);
                // Check if its the last item in the list, if not append a comma.
                if (count++ != Globals.stockSymbolList.size() - 1) {
                    csvString.append(",");
                }
            }
        } else {
            // in case no symbols added yet, we make a empty string
            // then check on it back in the thread.
            csvString.append("");
            Log.d(STOCK_LOG, "Here is the empty csv string: " + csvString.toString());
        }
        //Return the string of comma separated symbols
        return csvString.toString();
    }

    //################# FOR REQUESTING AND ADDING SINGLE STOCK #################

    private void badStockRequest() {
        Log.d(STOCK_LOG, "StockQuote belonging to symbol was not found.");
        //Make the bad request toast.
        Toast badRequestToast = Toast.makeText(getApplicationContext(), R.string.badStockRequestToastText, Toast.LENGTH_SHORT);
        //Set the gravity so the toast appears in center of the device screen.
        badRequestToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        badRequestToast.show();
    }

    // for requesting a single stock.
    public void requestSingleStock(final String requestSymbol) {
        //First add the new symbol to the stock symbol list
//        Globals.stockSymbolList.add(requestSymbol); //TODO: Should be added to the stock symbol list only after the success

        // Make a new request string with a single symbol in it.
        String requestUrl = Globals.STOCK_MARKET_STRING + requestSymbol + Globals.STOCK_QUOTE_FILTER_STRING;

        //Setup the request, with the single symbol.
        StringRequest symbolRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If the symbol doesn't exist the api returns an empty object body: {}
                        //So if its long than that we got something back.
                        // TODO: Try alternative with: response.contains(requestSymbol)
                        if (response.length() > 2) {
                            // on successful request handle response
                            Log.d(STOCK_LOG, "Request successful, the stock was found");
                            asyncHandleSingleStockResponse(response, requestSymbol);
                            //Since the request was a success add it to the global symbol list
                            //So it automatically gets updated on next auto request
                            Globals.stockSymbolList.add(requestSymbol);
                        } else {
                            //If we get an empty body, cause the symbol wasn't found.
                            //Make a toast to the user explaining what happened.
                            badStockRequest();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(STOCK_LOG, "Volley request error");
                        badStockRequest();
                    }
                });
        rQueue.add(symbolRequest);
    }

    //Save and broadcast a single stock, params has to be final so they can be accessed of the inner async body.
    private void asyncHandleSingleStockResponse(final String result, final String requestSymbol) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncInsertSingleStock = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                StockQuote newStockQuote = StockJsonParser.parseSingleStockJson(result, requestSymbol);
                //Set the purchase value to the latest price, the first time its created.
//                newStockQuote.setStockPurchasePrice(newStockQuote.getLatestStockValue());
                //Insert the new stock in the database and then add it to the local list.
                //get the stock id after the insert
                newStockQuote.setUid(db.stockQuoteDao().insertSingleStock(newStockQuote));
                serviceStockList.add(newStockQuote);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Broadcast result to the activity
                Intent broadcastIntent = new Intent();
                // Set the broadcast action filter
                broadcastIntent.setAction(SINGLE_STOCK_BROADCAST_ACTION);
                // Broadcast action code for single stock
                broadcastIntent.putExtra(BROADCAST_ACTION_RESULT_CODE, getResources().getString(R.string.broadcastActionSingleStock));
                Log.d(STOCK_LOG, "Broadcasting single stock:\n" + result + "\n and symbol used:\n" + requestSymbol);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent); // send it off to be caught in the other thread
            }
        };
        asyncInsertSingleStock.execute();
    }

    public void requestRefreshStockList() {
        requestAllStocksInList(makeCsvSymbolString());
        Log.d(STOCK_LOG, "Forced update request from user");
    }


    //################# FOR REQUESTING, UPDATING AND BROADCASTING MULTIPLE STOCKS #################

    // for requesting a list of stocks
    private void requestAllStocksInList(String requestCsvSymbols) {
        // Make the request string, using the comma separated string of symbols.
        String requestUrl = Globals.STOCK_MARKET_STRING + requestCsvSymbols + Globals.STOCK_QUOTE_FILTER_STRING;
        //Setup the request with the url
        StringRequest symbolListRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Getting a string with json object with nested objects
                        asyncHandleStockListResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(STOCK_LOG, "Error requesting stock list");
                    }
                });
        // REQUEST_TAG is so we can cancel the request from elsewhere, if we want to.
        symbolListRequest.setTag(Globals.REQUEST_TAG);
        rQueue.add(symbolListRequest);
    }

    //Save and broadcast list of stocks, params has to be final so they can be accessed of the inner async body.
    private void asyncHandleStockListResponse(final String response) {
        //Async task to update database
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncUpdateDatabaseStockList = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //We want to parse the response from json, and save it to the database.
                //and we use our unique symbol list, for optimization.
                List<StockQuote> newTempStockList = StockJsonParser.parseStockListJson(response, uniqueSymbolList);
                //An outer loop for the the updated stock list returned by auto volley update request
                for (StockQuote newTempStock : newTempStockList) {
                    //The inner loop where we then mach each item of the new stock list
                    //With the old stock list.
                    for (StockQuote databaseStock : serviceStockList) {
                        //If we find a match in the stock lists, matching on symbols
                        //We update the old stocks we have with the new stockValue/stockPrice
                        //That way we don't overwrite the changes made by the user when updating
                        if (newTempStock.getStockSymbol().equals(databaseStock.getStockSymbol())) {
                            databaseStock.setLatestStockValue(newTempStock.getLatestStockValue());
                            databaseStock.setTimeStamp(newTempStock.getTimeStamp());
                        }
                    }
                }
                //Now that the database stocks are updated in the list. Save it to the database
                db.stockQuoteDao().updateStockList(serviceStockList);
                return null;
            }

            // After the async task returns broadcast the changes
            @Override
            protected void onPostExecute(Void aVoid) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(LIST_OF_STOCKS_BROADCAST_ACTION); // Set the broadcast action filter
                // Broadcast action code for list of stocks
                broadcastIntent.putExtra(BROADCAST_ACTION_RESULT_CODE, getResources().getString(R.string.broadcastActionMultiStock));
                Log.d(STOCK_LOG, "Broadcasting list of stock:" + response);
                // send it off to be caught in the mainActivity thread
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
        asyncUpdateDatabaseStockList.execute();
    }

    //################# UPDATE SINGLE STOCKS IN DATABASE HERE #################

    public void asyncUpdateSingleStock(final StockQuote stockQuote) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncUpdateSingleStock = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //Save single updated stock from the edit view.
                db.stockQuoteDao().updateSingleStock(stockQuote);
                return null;
            }
        };
        asyncUpdateSingleStock.execute();
    }

    //################# DELETING STOCKS HERE #################

    public void asyncDeleteSingleStock(final StockQuote stockQuote) {
        for (Iterator<String> iterator = Globals.stockSymbolList.listIterator(); iterator.hasNext(); ) {
            //We iterate through the list till we find the stock symbol,
            // then remove it from the global symbol list.
            if (iterator.next().equals(stockQuote.getStockSymbol())) {
                iterator.remove();
                break;
            }
        }
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncDeleteSingleStock = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //Now update the change in the database by deleting the stock.
                db.stockQuoteDao().deleteSingleStock(stockQuote);
                return null;
            }
        };
        asyncDeleteSingleStock.execute();
    }

}
