package com.example.jazzbear.au520839_stocks;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StockService extends Service {
    // for broadcasting a single stock
    public static final String SINGLE_STOCK_BROADCAST_ACTION = "com.jazzbear.android.SINGLE_STOCK_BROADCAST_ACTION";
    // For broadcasting list of stocks
    public static final String LIST_OF_STOCKS_BROADCAST_ACTION = "com.jazzbear.android.LIST_OF_STOCKS_BROADCAST_ACTION";

    private static final String SERVICE_LOG = "service_log_tag";
    private boolean started = false;
    private boolean running = false;
    private final IBinder stockServiceBinder = new StockServiceBinder();
    private StockDatabase db;
    RequestQueue rQueue;
    private int wait_time = 2*60*1000; // 2 minutes wait

    // Needed so we change it in multiple places.
    NotificationCompat.Builder serviceNotification = null;
    NotificationManagerCompat serviceNotifyManager;

    // Implicit constructor
    public StockService() {
    }

    // This is used as the updated list, when the list is updated, a broadcast is sent,
    // to the services that implement the binder can then call the public method to get the list.
    private List<StockQuote> serviceStockList;
    // Unique symbol list, making sure we only ever query or request for one occurrence of each symbol
    private List<String> localStockSymbolList = new ArrayList<>();
    private List<String> uniqueSymbolList = new ArrayList<>();

    public List<StockQuote> getServiceStockList() {
        return serviceStockList;
    }
    // The setter needs to be synchronized so that there are no overlaps when updating the list.
    private synchronized void setServiceStockList(List<StockQuote> serviceStockList) {
        this.serviceStockList = serviceStockList;
    }


    // Returns the StockService context interface reference so we can bind to the service from activities
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
        Log.d(SERVICE_LOG, "Stock service onCreate, initializing database instance");
        db = StockDatabase.getDatabaseInstance(this);
        serviceStockList = new ArrayList<>(); // was null when in onStart
        // Moved notification here to onCreate, to improve performance
        setupNotifications();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup, if the service is destroyed, we want to flip the boolean flags back.
        // So that the thread runnable stops running, and so we can start the service up again.
        started = false;
        running = false;
        Log.d(SERVICE_LOG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Inspired by Stunt code from Lecture 6
        //Wait until something starts the service, with either startService() or by binding to it.
        if (!started && intent != null) {
            // Instantiate new request queue if one doesn't exist.
            if (rQueue == null) {
                rQueue = Volley.newRequestQueue(this);
            }
            //First check if initial setup has been made. I.e a database exist and is populated.
            if (!checkInitDataBase()) {
                firstTimeDataSetup(); // if not, run the setup methods.
            }
            started = true; //flip it so it stays started, until service is destroyed.
            running = true; // once something starts the service we want to start the runnable as well.

            Runnable runnable = new Runnable() { // Setup the runnable
                @Override
                public void run() {
                    //We want to make sure we only request stocks that exists in the database,
                    // by getting them first. And then setting the local serviceStockList.
                    setServiceStockList(db.stockQuoteDao().getAllStockQuotes());
                    broadcastDatabaseStockListUpdate();

                    // now iterate through the list updated from the database. And then stores it in the local list
                    // Then later the local list is run through the csv maker which also populates a unique symbol list.
                    // using hash-map, that way, all queries and requests are always only once per. unique stock symbol.
                    for (StockQuote index : serviceStockList) {
                        localStockSymbolList.add(index.getStockSymbol());
                    }

                    while (running) {
                        try {
                            // make CSV from the local symbol list.
                            String csvSymbolString = makeCsvSymbolString();
                            if (!csvSymbolString.equals("")) {
                                // When the symbol list gets populated after init setup,
                                // or just as long as something is in the symbolList/database.
                                // then run auto updates on the list.
                                requestAllStocksInList(csvSymbolString);
                            } else {
                                Log.d(SERVICE_LOG, "No symbols added to the list yet");
                            }
                            //Wait 2 minutes before calling again
                            Thread.sleep(wait_time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d(SERVICE_LOG, "Thread was interrupted");
                        }
                    }
                }
            };
            // Create/init a thread for the runnable.
            Thread thread = new Thread(runnable);
            thread.start();
        }
        Log.d(SERVICE_LOG, "Stock service onStartCommand");
        return START_STICKY;
    }

    // used the method from here: https://stackoverflow.com/questions/3386667/query-if-android-database-exists
    private boolean checkInitDataBase() {
        SQLiteDatabase checkRoomDB = null;
        try {
            //Check if the database by doing a read on the local database created with app name,
            //which is the same as the one defined in the gradle.
            checkRoomDB = SQLiteDatabase.openDatabase(StockDatabase.DATABASE_PATH,
                    null, SQLiteDatabase.OPEN_READONLY);
            checkRoomDB.close(); // Closing the connection afterwards so we don't have a hanging connection
        } catch (SQLException e) {
            Log.d(SERVICE_LOG, "No database found. Error: " + e);
            e.printStackTrace();
        }
        // use the local SQLite instance to flip the boolean, which depends on whether we get an instance or not.
        return checkRoomDB != null;
    }

    // First time data setup volley requests are handled here.
    private void firstTimeDataSetup() {
        //Populate the local list with the global list with 10 initial symbols
        localStockSymbolList = Globals.initDataBaseSymbolList;
        String initSymbolsCsv = makeCsvSymbolString();
        String initRequestUrl = Globals.STOCK_MARKET_ENDPOINT + initSymbolsCsv + Globals.STOCK_QUOTE_FILTER_STRING;

        StringRequest initStockListRequest = new StringRequest(Request.Method.GET, initRequestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(SERVICE_LOG, "First time setup request, response is: " + response);
                        handleFirstTimeData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(SERVICE_LOG, "Error getting init list.");
                        //Make the bad request toast.
                        Toast badRequestToast = Toast.makeText(getApplicationContext(), "Error getting init list.", Toast.LENGTH_SHORT);
                        //Set the gravity so the toast appears in center of the device screen.
                        badRequestToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        badRequestToast.show();
                    }
                });
        rQueue.add(initStockListRequest);
    }

    public void handleFirstTimeData(final String response) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncSaveAndBroadcastInitData = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(SERVICE_LOG, "Saving first time data to database with response data: " + response);
                // The response is handled, by parsing the json data to a stockQuote list, which is saved,
                // in the database, and then a query afterwards to get all info and autogenerated id's
                // for the serviceStockList. This is is need for matching later.
                List<StockQuote> initStockList = StockJsonParser.parseStockListJson(response, uniqueSymbolList);
                for (StockQuote initStock : initStockList) {
                    initStock.setStockPurchasePrice(initStock.getLatestStockValue());
                }

                db.stockQuoteDao().insertStockList(initStockList); // inserting the list to the database
                setServiceStockList(db.stockQuoteDao().getAllStockQuotes()); //getting out the list again, so we also have id's
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                PendingIntent pIntent = setupNotifyNavigation();
                //setup a notification now that the list is updated.
                serviceNotification = makeUpdateNotification(pIntent);
                //And then create and notify the manager with it.
                serviceNotifyManager.notify(Globals.NOTIFY_ID, serviceNotification.build());
                // now broadcast it to the overview activity so it can get the initial list of 10 stocks.
                broadcastDatabaseStockListUpdate();
            }
        };
        asyncSaveAndBroadcastInitData.execute();
    }

    // ################# STOCK REQUEST METHODS #################

    private String makeCsvSymbolString() {
        int count = 0; // iterator
        StringBuilder csvString = new StringBuilder();
        //First check if there are any symbols in the list
        if (!localStockSymbolList.isEmpty()) {
            //Since the local symbolist can have more than one of the same symbol.
            //But for both the request and a later database query we only want 1 of each symbol.
            //Create a hash-map for the symbolist, that way multiple occurrences of the same symbol is ignored.
            Set<String> symbol_hash_map = new HashSet<>(localStockSymbolList);
            uniqueSymbolList.clear();
            uniqueSymbolList.addAll(symbol_hash_map); //Add the uniquely hashed list to a local symbol list.

            // We make a new comma separated list based on the hash-map containing only one
            // occurrence of each symbol.
            for (String symbol : symbol_hash_map) {
                //Then build a comma separated string.
                csvString.append(symbol);
                // Check if its the last item in the list, if not append a comma.
                if (count++ != localStockSymbolList.size() - 1) {
                    csvString.append(",");
                }
            }
        } else {
            // in case no symbols added yet, we make a empty string
            // then check on it back in the thread.
            csvString.append("");
            Log.d(SERVICE_LOG, "Here is the empty csv string: " + csvString.toString());
        }
        //Return the string of comma separated symbols
        return csvString.toString();
    }

    //################# FOR REQUESTING AND ADDING SINGLE STOCK #################

    private void badStockRequest() {
        Log.d(SERVICE_LOG, "StockQuote belonging to symbol was not found.");
        //Make the bad request toast.
        Toast badRequestToast = Toast.makeText(getApplicationContext(), R.string.badStockRequestToastText, Toast.LENGTH_SHORT);
        //Set the gravity so the toast appears in center of the device screen.
        badRequestToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        badRequestToast.show();
    }

    // for requesting a single stock.
    public void requestSingleStock(final String requestSymbol, final int amountOfStocks) {

        // Make a new request string with a single symbol in it.
        String requestUrl = Globals.STOCK_MARKET_ENDPOINT + requestSymbol + Globals.STOCK_QUOTE_FILTER_STRING;

        //Setup the request, with the single symbol.
        StringRequest symbolRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // We check if the response actually contains the symbol,
                        // if it does then we can assume a success.
                        if (response.contains(requestSymbol)) {
                            // on successful request handle response
                            Log.d(SERVICE_LOG, "Request successful, the stock was found");
                            //Since the request was a success add it to the local symbol list
                            //So it automatically gets updated on next auto request
                            localStockSymbolList.add(requestSymbol);
                            asyncHandleSingleStockResponse(response, requestSymbol, amountOfStocks);
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
                        Log.d(SERVICE_LOG, "Volley request error");
                        badStockRequest();
                    }
                });
        rQueue.add(symbolRequest);
    }

    //Save and broadcast a single stock, params has to be final so they can be accessed of the inner async body.
    private void asyncHandleSingleStockResponse(final String result, final String requestSymbol, final int amountOfStocks) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncInsertSingleStock = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                StockQuote newStockQuote = StockJsonParser.parseSingleStockJson(result, requestSymbol);
                //Set the purchase value to the latest price, the first time its created.
                newStockQuote.setStockPurchasePrice(newStockQuote.getLatestStockValue());
                //Also set the initial amount of stocks bought.
                newStockQuote.setAmountOfStocks(amountOfStocks);
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
                Log.d(SERVICE_LOG, "Broadcasting after single stock requested:\n"
                        + result + "\n and symbol used:\n" + requestSymbol);
                // send it off to be caught in the other thread
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
        asyncInsertSingleStock.execute();
    }

    public void requestRefreshStockList() {
        // Call with the latest version of the local symbol list
        String requestString = makeCsvSymbolString();
        requestAllStocksInList(requestString);
        Log.d(SERVICE_LOG, "Forced update request from user");
    }


    //################# FOR REQUESTING, UPDATING AND BROADCASTING MULTIPLE STOCKS #################

    // for requesting a list of stocks
    private void requestAllStocksInList(String requestCsvSymbols) {
        // Make the request string, using the comma separated string of symbols.
        String requestUrl = Globals.STOCK_MARKET_ENDPOINT + requestCsvSymbols + Globals.STOCK_QUOTE_FILTER_STRING;
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
                        Log.d(SERVICE_LOG, "Error requesting stock list");
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
                setServiceStockList(db.stockQuoteDao().getAllStockQuotes());
                return null;
            }

            // After the async task returns broadcast the changes
            @Override
            protected void onPostExecute(Void aVoid) {
                //make an update notification.
                PendingIntent pIntent = setupNotifyNavigation();
                serviceNotification = makeUpdateNotification(pIntent);
                serviceNotifyManager.notify(Globals.NOTIFY_ID, serviceNotification.build());

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(LIST_OF_STOCKS_BROADCAST_ACTION); // Set the broadcast action filter
                Log.d(SERVICE_LOG, "Broadcasting list of stock:" + response);
                // send it off to be caught in the mainActivity thread
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
        asyncUpdateDatabaseStockList.execute();
    }



    //################# UPDATE SINGLE STOCKS IN DATABASE HERE #################

    //Used to update the single stock after changing its amount of stocks and price in editActivity
    public void asyncUpdateSingleStock(final StockQuote stockQuote) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncUpdateSingleStock = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //Save single updated stock from the edit view.
                db.stockQuoteDao().updateSingleStock(stockQuote);
                setServiceStockList(db.stockQuoteDao().getAllStockQuotes());
                requestRefreshStockList();
                return null;
            }
        };
        asyncUpdateSingleStock.execute();
    }

    //################# DELETING STOCKS HERE #################

    public void asyncDeleteSingleStock(final StockQuote stockQuote) {
        for (Iterator<String> iterator = localStockSymbolList.listIterator(); iterator.hasNext(); ) {
            //We iterate through the list till we find the stock symbol,
            // then remove it from the local symbol list, so that it is no longer included in auto updates.
            if (iterator.next().equals(stockQuote.getStockSymbol())) {
                iterator.remove();
                Log.d(SERVICE_LOG, "Removing Global stock symbol: " + iterator);
                Log.d(SERVICE_LOG, "Now the list holds: " + localStockSymbolList);
                break;
            }
        }
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncDeleteSingleStock = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //Now update the change in the database by deleting the stock.
                db.stockQuoteDao().deleteSingleStock(stockQuote);
                setServiceStockList(db.stockQuoteDao().getAllStockQuotes());
                requestRefreshStockList();
                return null;
            }
        };
        asyncDeleteSingleStock.execute();
    }


    // ############################## NOTIFICATION SETUP ##############################

    // To help me with this i used Kasper Løvborg Jensens ServiceDemo from lecture 6,
    // and the android developer guide: https://developer.android.com/training/notify-user/build-notification
    private void setupNotifications() {
        /*  Setup up notification channel, we check if the api level is higher than 26,
         * As previous to that creating and registering custom channels was not implemented.
         * The reason this is not the best way to check here, as Kasper wrote in his stunt code, ("don't do this at home")
         * is because, when we try to push changes to the channel later, but the app is hosted on an,
         * older android device, it would cause problems.
         * So really to proper handle it, all places that uses the notification should, check version first.
         * And then decide to use notifications or not. */

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.notifyChannelName); //Set channel name.
            String channelDescription = getString(R.string.notifyChannelDescription); // for channel description, to user.
            int importanceLevel = NotificationManager.IMPORTANCE_LOW; // medium level importance
            //Create a new channel.
            NotificationChannel serviceChannel = new NotificationChannel(Globals.CHANNEL_ID, channelName, importanceLevel);
            serviceChannel.setDescription(channelDescription);
            // Now register the channel in the system.
            NotificationManager serviceNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            serviceNotifyManager.createNotificationChannel(serviceChannel);
        }

        PendingIntent pIntent = setupNotifyNavigation();

        // Set the local notification manager. And use it with the service as context.
        serviceNotifyManager = NotificationManagerCompat.from(this);
        //Create a new notification about the initial update
        serviceNotification = makeUpdateNotification(pIntent);
        // And now start the service as a foreground service with the new notification.
        startForeground(Globals.NOTIFY_ID, serviceNotification.build());
    }

    // First time notification builder
    private NotificationCompat.Builder makeUpdateNotification(PendingIntent pIntent) {
        return new NotificationCompat.Builder(this, Globals.CHANNEL_ID)
                .setContentTitle(getText(R.string.notificationTitle))
                .setContentText(getText(R.string.notificationUpdateString) + " " + getCurrentDateTime())
                .setSmallIcon(R.mipmap.ic_stockmarket_round)
                .setContentIntent(pIntent);
    }

    // for getting a current date and time stamp for notifications
    private String getCurrentDateTime() {
        Date currentDateAndTime = Calendar.getInstance().getTime();
        return currentDateAndTime.toString();
    }

    //Used to immediately init and update the view.
    private void broadcastDatabaseStockListUpdate() {
        Intent firstTimeUpdate = new Intent();
        firstTimeUpdate.setAction(LIST_OF_STOCKS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(firstTimeUpdate);
    }

    private PendingIntent setupNotifyNavigation() {
        // Added the functionality of clicking on the notification brings you to overview.
        // With help from the android dev guide: https://developer.android.com/training/notify-user/navigation
        Intent notifyNavigateIntent = new Intent(this, OverviewActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notifyNavigateIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
