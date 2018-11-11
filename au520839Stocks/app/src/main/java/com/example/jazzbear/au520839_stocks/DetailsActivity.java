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
import android.widget.TextView;
import android.widget.Toast;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.Globals;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private static final String DETAILS_LOG = "Details_Activity_Log";
    private boolean serviceBound = false;
    private boolean deletePressed = false;
    Button backButton, editButton, deleteButton;
    //All the text views
    private TextView detailSymbol, detailName, detailPrice, detailAmount,
            detailPriceDifference, detailSector, detailPrimExchange,
            detailCurrentVal, detailTimestamp, totalEarnings;

    private ServiceConnection serviceConnection;
    private StockService stockService;
    private StockQuote detailsStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState != null) {
            detailsStock = savedInstanceState.getParcelable(Globals.STOCK_STATE);
        } else {
            detailsStock = getIntent().getParcelableExtra(Globals.STOCK_OBJECT_EXTRA);
        }


        detailSymbol = findViewById(R.id.symbolDetails);
        detailName = findViewById(R.id.nameDetails);
        detailPrice = findViewById(R.id.priceDetails);
        detailAmount = findViewById(R.id.stockAmountDetails);
        detailSector = findViewById(R.id.sectorDetails);
        detailPrimExchange = findViewById(R.id.primExchangeDetails);
        detailCurrentVal = findViewById(R.id.currentValDetails);
        detailPriceDifference = findViewById(R.id.priceDifferenceDetails);
        detailTimestamp = findViewById(R.id.timestampDetails);
        totalEarnings = findViewById(R.id.totalEarningsDetails);
        //Update the ui with new info
        updateDetailsUI();
        // Register to receiver here so we use it when the detailsActivity is started.
        registerBroadcastReceiver();
        //Setup connection to service, so we can bind to it and get the stockList.
        setupServiceConnection();

        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonPressed();
            }
        });

        editButton = findViewById(R.id.editBtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtonPressed();
            }
        });

        deleteButton = findViewById(R.id.deleteBtn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtonPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to service once visible.
        bindToStockService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unbind from service when app is stopped, and no longer visible.
        unBindFromStockService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Here we do want to unregister receiver, no need to get broad casts for this activity
        // when its no longer used.
        unregisterBroadcastReceiver();
    }

    //Put it in a method in case i want to move it to a different lifecycle event.
    private void registerBroadcastReceiver() {
        Log.d(DETAILS_LOG, "registering receiver");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(StockService.LIST_OF_STOCKS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(detailsBroadcastReceiver, iFilter);
    }
    //Put it in a method in case i want to move it to a different lifecycle event.
    private void unregisterBroadcastReceiver() {
        Log.d(DETAILS_LOG, "unregistering receivers");
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(detailsBroadcastReceiver);
    }

    // Setting up connection to StockService so we can get the binder, so we can bind later and use it.
    // Inspired stunt code and slides from lecture 6, and also guide on:
    // http://developer.android.com/reference/android/app/Service.html
    private void setupServiceConnection() {
        // connect to service
        serviceConnection = new ServiceConnection() {
            @Override //Called when binding to service
            public void onServiceConnected(ComponentName className, IBinder service) {
                stockService = ((StockService.StockServiceBinder) service).getService();
                Log.d(DETAILS_LOG, "Stock service connected");
            }

            @Override //Called when unbinding from service
            public void onServiceDisconnected(ComponentName className) {
                stockService = null;
                Log.d(DETAILS_LOG, "Stock service disconnected");
            }
        };
    }

    private void updateDetailsUI() {
        //TODO: Should probably do some formatting here aswell like in the stock adaptor.
        //TODO: Possibly do the formatting in the StockQuote class. #THEREMUSTBEABETTERWAY
        detailSymbol.setText(detailsStock.getStockSymbol());
        detailName.setText(detailsStock.getCompanyName());
        detailPrice.setText(Double.toString(detailsStock.getStockPurchasePrice()));
        detailAmount.setText(Integer.toString(detailsStock.getAmountOfStocks()));
        detailSector.setText(detailsStock.getSector());
        detailPrimExchange.setText(detailsStock.getPrimaryExchange());
        detailCurrentVal.setText(Double.toString(detailsStock.getLatestStockValue()));
        detailPriceDifference.setText(String.format("%.3f", detailsStock.getPriceDifference()));
        detailTimestamp.setText(detailsStock.getTimeStamp());
        totalEarnings.setText(Double.toString(detailsStock.getTotalEarnings()));
    }

    //Send an intent result back to the the overview with no changes and destroy details view.
    private void backButtonPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void editButtonPressed() {
        Intent editIntent = new Intent(DetailsActivity.this, EditActivity.class);
        editIntent.putExtra(Globals.STOCK_OBJECT_EXTRA, detailsStock);
        startActivityForResult(editIntent, Globals.EDIT_REQUEST);
    }

    //Finish activity and return to overview. With a request for delete.
    private void deleteButtonPressed() {
        //Maybe a dialog here would be good, to confirm or cancel
//        Intent deleteData = new Intent();
        deletePressed = true;
        stockService.asyncDeleteSingleStock(detailsStock);
        setResult(Globals.RESULT_DELETE);
        finish();
    }

    void bindToStockService() {
        // Method for binding to service
        bindService(new Intent(DetailsActivity.this, StockService.class),
                serviceConnection, Context.BIND_AUTO_CREATE); // creates service it on binding if it isn't created.

        Log.d(DETAILS_LOG, "Binding to service");
        serviceBound = true;
    }

    void unBindFromStockService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            Log.d(DETAILS_LOG, "Unbinding from service");
            serviceBound = false;
        }
    }

    private BroadcastReceiver detailsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final @Nullable Intent intent) {
            Log.d(DETAILS_LOG, "details activity broadcast received");
            //This is used by the old get extra check.
//            String intenCode = intent.getStringExtra(StockService.BROADCAST_ACTION_RESULT_CODE);

            assert intent != null;
            if (intent.getAction().equals(StockService.LIST_OF_STOCKS_BROADCAST_ACTION) && !deletePressed) {
                //TODO: Should be changed to instead check for specific stock per id.
                //TODO: Instead of this inefficient way of checking for the update.
                //TODO: Deffinite bottleneck the bigger the list becomes.

                List<StockQuote> stockListFromService = stockService.getServiceStockList();
                // iterate through the list until we find the stock
                for (StockQuote updateStock : stockListFromService) {
                    // Match the Unique id's, if match, set the details stuck with the updated stock
                    if (updateStock.getUid() == detailsStock.getUid()) {
                        detailsStock = updateStock;
                        break; // break out of the loop, no need to check the rest of the list.
                    }
                }
                updateDetailsUI(); // Update ui so the stock info is up to date.
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Same request handler as from last hand-in
        if (requestCode == Globals.EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Return straight to overviewActivity
                assert data != null;
                setResult(RESULT_OK, data);
                finish();
            } else {
                toast(getResources().getString(R.string.toastCanceled));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Globals.STOCK_STATE, detailsStock);
        super.onSaveInstanceState(outState);
    }

    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}
