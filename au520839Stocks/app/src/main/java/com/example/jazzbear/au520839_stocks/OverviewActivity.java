package com.example.jazzbear.au520839_stocks;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.example.jazzbear.au520839_stocks.DAL.StockDatabase;
import com.example.jazzbear.au520839_stocks.Models.Stock;
import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.StockJsonParser;
import com.example.jazzbear.au520839_stocks.Utils.StockUpdaterService;
import com.example.jazzbear.au520839_stocks.Utils.Globals;

import com.facebook.stetho.Stetho;
import java.util.List;

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class OverviewActivity extends AppCompatActivity {

    //    static final String OVERVIEW_SAVED = "overview_is_set";
    TextView overviewStockName, stockPurchasePrice, responseView;
    ImageView imgView;
    Button detailsButton, testBtn, sendRequestBtn, stopRequestBtn;
    Stock stock;

    // Service variables
//    StockUpdaterService stockService;
    StockService stockService;
    private ServiceConnection stockServiceConnection;
    boolean serviceBound = false;
//    private long task_wait_time = 30*1000;

    // Request queue for volley
    RequestQueue rQueue;
    StockDatabase db;

    // ############ LIFE CYCLE METHODS ###############
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // For database debugging
        enableStethos();

        // Setup connection so we can bind to stockService later
        setupServiceConnectionToStockService();
//        startStockUpdaterService();

        // Recreating after and instance saved state.
        // Otherwise initializes first stock with default values
        if (savedInstanceState != null) {
            stock = savedInstanceState.getParcelable(Globals.STOCK_STATE);
//            toast("Refreshed UI"); // For debugs
        } else {
            // Getting the resources so we can set the language to the right locale
            // DA=Teknologi and EN=Technology
            String sector = getResources().getString(R.string.sectorTech);
            stock = new Stock("Facebook",
                    1000.00,
                    14,
                    sector);
        }

        // Init view elements
        overviewStockName = findViewById(R.id.overviewName);
        stockPurchasePrice = findViewById(R.id.overviewPurchased);
        detailsButton = findViewById(R.id.overviewButton);
        imgView = findViewById(R.id.imageView);
        responseView = findViewById(R.id.txtResponse);

        updateUI();

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsButtonClicked();
            }
        });

        testBtn = findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRequestUrl();
            }
        });

        stopRequestBtn = findViewById(R.id.stopBtn);
        stopRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendRequestBtn = findViewById(R.id.getStocksBtn);
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startStockUpdaterService();
                Globals.STOCK_SYMBOL = "AMD";
                stockService.requestSingleStock(Globals.STOCK_SYMBOL);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(STOCK_LOG, "registering receivers");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(StockService.LIST_OF_STOCKS_RESULT_BROADCAST);
        iFilter.addAction(StockService.SINGLE_STOCK_RESULT_BROADCAST);

//        //can use registerReceiver(...)
//        //but using local broadcasts for this service:
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(onBackgroundServiceResult, iFilter);

        bindToStockService();

        // we bind when app i started
//        Intent bindIntent = new Intent(this, StockService.class);
//        bindService(bindIntent, stockServiceConnection, Context.BIND_AUTO_CREATE);
//        serviceBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(STOCK_LOG, "unregistering receivers");
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(onBackgroundServiceResult);

        unBindFromStockService();
        // unbind from service when app is stopped, and no longer visible.
//        if (serviceBound) { // only continue in case it is indeed bound.
//            // Detach our existing connection.
//            unbindService(stockServiceConnection);
//            serviceBound = false;
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Globals.STOCK_STATE, stock);
    }

    // ########## OTHER METHODS ###############

    // Setting up connection to StockService so we can get the binder, so we can bind later and use it.
    private void setupServiceConnectionToStockService() {
        // connect to service
        stockServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                //ref: http://developer.android.com/reference/android/app/Service.html
                // and also ref: https://developer.android.com/guide/components/bound-services
                stockService = ((StockService.StockServiceBinder)service).getService();
                Log.d(Globals.STOCK_LOG, "Stock service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // This is called when the connection with the service has been
                //                // unexpectedly disconnected -- that is, its process crashed.
                //                // Because it is running in our same process, we should never
                //                // see this happen.
                //                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = null;
                Log.d(Globals.STOCK_LOG, "Stock service disconnected");
            }
        };
    }


    // TODO: needs to be re-implemented once stock service is done.
    // For starting stock service
    private void startStockUpdaterService() {
        Intent startServiceIntent = new Intent(OverviewActivity.this, StockUpdaterService.class);
        // this intent needs include the stocks we want to work on
//        startServiceIntent.putExtra(StockUpdaterService.EXTRA_TASK_TIME_MS, taskTime);
        startService(startServiceIntent);
    }

    private void detailsButtonClicked() {
        //Sent an intent to details and parse the stock object.
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(Globals.STOCKOBJECT_EXTRA, stock);
        startActivityForResult(detailsIntent, Globals.DETAILS_REQUEST);
    }

    private void updateUI() {
        overviewStockName.setText(stock.getStockName());
        String purchaseString = getResources().getString(R.string.stockPurchaseText) + " " + Double.toString(stock.getStockPrice());
        stockPurchasePrice.setText(purchaseString);
        setImageView();
    }

    private void setImageView() {
        // Getting resources so i can check on the string values for sector, for the right locale.
        String sectorValue = stock.getStockSector();
        String techSector = getResources().getString(R.string.sectorTech);
        String materialSector = getResources().getString(R.string.sectorMats);
        String healthSector = getResources().getString(R.string.sectorHealth);

        //commented out the setImageDrawable since its better to use icons as they scale in pixel density for each device.
        // but left them there to show the alternative.
        //Used this to find out how to set icons instead: https://stackoverflow.com/questions/30800708/how-to-load-images-from-mipmap-folder-programatically
        if (sectorValue != null) {
            if (sectorValue.equalsIgnoreCase(techSector)) {
                imgView.setImageResource(R.mipmap.ic_technology_foreground);
//                imgView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.technology));
            } else if (sectorValue.equalsIgnoreCase(materialSector)) {
                imgView.setImageResource(R.mipmap.ic_materials_foreground);
//                imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.materials));
            } else if (sectorValue.equalsIgnoreCase(healthSector)) {
                imgView.setImageResource(R.mipmap.ic_healthcare_foreground);
//                imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.healthcare));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Globals.DETAILS_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Update the stock object and update the ui
                assert data != null;
                stock = data.getParcelableExtra(Globals.STOCKOBJECT_EXTRA);
                updateUI();
//                toast("OK");
            }
        }
    }


    //TODO: Remove this, was just for testing
    private void testRequestUrl() {
        // TODO: WHEN THE USER OR ONE SELF ADDS MORE SYMBOLS, ADD IT TO THE SYMBOL LIST.
        // TODO: THAT WAY WE HAVE A ITTERABLE LIST TO GO THROUGH.
        // TODO: MIGHT NEED TO BE MODIFIED SO THAT WE CAN REMOVE STUFF FROM IT ASWELL.
        List<String> symbolList = Globals.stockSymbolList;
//        symbolList.add("MSFT");
        int count = 0;
        StringBuilder csvList = new StringBuilder();
        for (String s : symbolList) {
            csvList.append(s);
            // Check if its the last item in the list, if not append a comma
            if (count++ != symbolList.size() -1 ) {
                csvList.append(",");
            }
        }

        toast(Globals.STOCK_MARKET_STRING + csvList + Globals.STOCK_QUOTE_FILTER_STRING);
    }


    private void addTaskList(List<StockQuote> stockList) {
        db = StockDatabase.getDatabase(OverviewActivity.this);
        db.stockQuoteDao().insertStockList(stockList);
    }

    //TODO: Need to finish this
//    private void stopVolleyRequests() {
//        if (rQueue != null) {
//            rQueue.cancelAll(Globals.REQUEST_TAG);
//        }
//    }


    //define our broadcast receiver for (local) broadcasts.
    // Registered and unregistered in onStart() and onStop() methods
    private BroadcastReceiver onBackgroundServiceResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(STOCK_LOG, "Broadcast received from bg service");


            String intentCode = intent.getStringExtra(StockService.EXTRA_INTENT_CODE);
            if (intentCode.equalsIgnoreCase("111")) {
                String stockResult = intent.getStringExtra(StockService.EXTRA_STOCK_RESULT);
                Log.d(STOCK_LOG, "Here is the intent stock result: " + stockResult + "\n");
                String stockSymbol = intent.getStringExtra(StockService.EXTRA_STOCK_CALL_SYMBOL);
                Log.d(STOCK_LOG, "And here is the intent stock symbol: " + stockSymbol + "\n");

                if (stockResult == null /*&& stockSymbol == null*/) {
                    stockResult = getString(R.string.err_bg_service_result);
                    stockSymbol = "Derp";
                    Log.d(STOCK_LOG, "Error with single stock broadcast");
                }
                // Handle the broadcast, stockSymbol is nullable in case the if statement above is hit.
                handleStockResult(stockSymbol, stockResult);
            }

            if (intentCode.equalsIgnoreCase("222")) {
                // A list of stocks was broadcast.
                String stockListResult = intent.getStringExtra(StockService.EXTRA_STOCK_LIST_RESULT);
                if (stockListResult == null) {
                    stockListResult = getString(R.string.err_bg_service_result);
                    Log.d(STOCK_LOG, "This was hit");
                }
                // Handle the broadcast
                handleStockListResult(stockListResult);
            }


//            handleStockListResult(stockListResult);

//            handleStockListResult(stockListResult);

//            if (stockResult != null) {
//                handleStockListResult(stockResult);
//            } else if (stockListResult != null) {
//                handleStockListResult(stockListResult);
//            }
        }
    };

    // TODO: If the other fail make a new reciever to handle the other broadcast
//    BroadcastReceiver onSingleStockResult = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    };

    private void handleStockResult(String stockSymbol, String result) {
        // TODO: Hardcoded here
        // since when trying to parse it with an intent extra,
        // the symbol extra overwrites the result extra as well.
//        String symbol = "AMD";
//        Log.d(STOCK_LOG, symbol);
        Log.d(STOCK_LOG, "And the stock result to be handled: " + result);
        // Calling with a global string because apparently when i try to set 2 different string intent extras then one overwrites the other.
        StockQuote responseQuote = StockJsonParser.parseSingleStockJson(stockSymbol, result);
        String responseString = responseQuote.getCompanyName() + "\n" + responseQuote.getLatestPrice();

        Log.d(STOCK_LOG, "Broadcasting result with toast:\n" + responseString);
        toast("Got result from background service:\n" + responseString);
    }

    // TODO: Midlertidig broadcast handler for testing
    private void handleStockListResult(String result){
//        toast("Got result from background service:\n" + result);
        List<StockQuote> listOfResponseQuotes =
                StockJsonParser.parseStockListJson(Globals.stockSymbolList, result);

        String listString = "Response: \n";
        for (StockQuote quote : listOfResponseQuotes) {
            String responseValue = quote.getCompanyName() + "\n" + quote.getLatestPrice() + "\n" + quote.getOpeningPrice() + "\n\n";
//                            listString.append(responseString);
            listString += responseValue;
        }

        Log.d(STOCK_LOG, "Broadcasting result with toast:\n" + listString);
        toast("Got result from background service:\n" + listString);
    }

    void bindToStockService() {
        // Method for binding to service
        bindService(new Intent(OverviewActivity.this, StockService.class),
                stockServiceConnection, Context.BIND_AUTO_CREATE); // creates service it on binding if it isn't created.

        Log.d(STOCK_LOG, "Binding to service");
        serviceBound = true;
    }

    void unBindFromStockService() {
        if (serviceBound) {
            unbindService(stockServiceConnection);
            Log.d(STOCK_LOG, "Unbinding from service");
            serviceBound = false;
        }
    }



    // Used toasts for debugging
    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_LONG).show();
    }

    // Enabling stehos database debugging
    private void enableStethos() {

           /* Stetho initialization - allows for debugging features in Chrome browser
           See http://facebook.github.io/stetho/ for details
           1) Open chrome://inspect/ in a Chrome browse
           2) select 'inspect' on your app under the specific device/emulator
           3) select resources tab
           4) browse database tables under Web SQL
         */
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                        Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this))
                .build());
        /* end Stethos */
    }
}
