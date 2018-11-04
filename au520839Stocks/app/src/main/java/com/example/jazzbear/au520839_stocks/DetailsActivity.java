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

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class DetailsActivity extends AppCompatActivity {

    private static final String DETAILS_LOG = "Details_Activity_Log";
    private boolean serviceBound = false;
    Button backButton, editButton, deleteButton;
    private TextView detailSymbol, detailName, detailPrice, detailAmount;
    private TextView detailSector, detailPrimExchange, detailCurrentVal, detailTimestamp;
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
            detailsStock = getIntent().getParcelableExtra(Globals.STOCKOBJECT_EXTRA);
        }


        detailSymbol = findViewById(R.id.symbolDetails);
        detailName = findViewById(R.id.nameDetails);
        detailPrice = findViewById(R.id.priceDetails);
        detailAmount = findViewById(R.id.stocksDetails);
        detailSector = findViewById(R.id.sectorDetails);
        detailPrimExchange = findViewById(R.id.primExchangeDetails);
        detailCurrentVal = findViewById(R.id.currentValDetails);
        detailTimestamp = findViewById(R.id.timestampDetails);

        updateDetailsUI();
        //Setup connection to service
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
        // Register to receiver here so we use it when the detailsActivity is in view.
        registerBroadcastReceiver();
        // bind to service once visible.
        bindToStockService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Here we do want to unregister receiver, no need to get broad casts for this activity
        // when its no longer visible.
        unregisterBroadcastReceiver();
        // unbind from service when app is stopped, and no longer visible.
        unBindFromStockService();
    }

    private void registerBroadcastReceiver() {
        Log.d(DETAILS_LOG, "registering receiver");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(StockService.LIST_OF_STOCKS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(detailsBroadcastReceiver, iFilter);
    }

    private void unregisterBroadcastReceiver() {
        Log.d(STOCK_LOG, "unregistering receivers");
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
                Log.d(Globals.STOCK_LOG, "Stock service connected");
            }

            @Override //Called when unbinding from service
            public void onServiceDisconnected(ComponentName className) {
                stockService = null;
                Log.d(Globals.STOCK_LOG, "Stock service disconnected");
            }
        };
    }

    private void updateDetailsUI() {
        detailSymbol.setText(detailsStock.getStockSymbol());
        detailName.setText(detailsStock.getCompanyName());
        detailPrice.setText(Double.toString(detailsStock.getStockPurchasePrice()));
        detailAmount.setText(Integer.toString(detailsStock.getAmountOfStocks()));
        detailSector.setText(detailsStock.getSector());
        detailPrimExchange.setText(detailsStock.getPrimaryExchange());
        detailCurrentVal.setText(Double.toString(detailsStock.getLatestStockValue()));
        detailTimestamp.setText(detailsStock.getTimeStamp());
    }

    //Send an intent result back to the the overview with no changes and destroy details view.
    private void backButtonPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void editButtonPressed() {
        Intent editIntent = new Intent(DetailsActivity.this, EditActivity.class);
        editIntent.putExtra(Globals.STOCKOBJECT_EXTRA, detailsStock);
        startActivityForResult(editIntent, Globals.EDIT_REQUEST);
    }

    //Finish activity and return to overview. With a request for delete.
    private void deleteButtonPressed() {
        //Maybe a dialog here would be good, to confirm or cancel
        Intent deleteData = new Intent();
        setResult(Globals.RESULT_DELETE, deleteData);
        finish();
    }

    void bindToStockService() {
        // Method for binding to service
        bindService(new Intent(DetailsActivity.this, StockService.class),
                serviceConnection, Context.BIND_AUTO_CREATE); // creates service it on binding if it isn't created.

        Log.d(STOCK_LOG, "Binding to service");
        serviceBound = true;
    }

    void unBindFromStockService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            Log.d(STOCK_LOG, "Unbinding from service");
            serviceBound = false;
        }
    }

    private BroadcastReceiver detailsBroadcastReceiver = new BroadcastReceiver() {
        @Override //TODO: Might not need to be final nullable
        public void onReceive(Context context, final @Nullable Intent intent) {
            Log.d(DETAILS_LOG, "details activity broadcast received");

            String intenCode = intent.getStringExtra(StockService.BROADCAST_ACTION_RESULT_CODE);
            if (intenCode.equalsIgnoreCase(getResources().getString(R.string.broadcastActionMultiStock))) {
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
