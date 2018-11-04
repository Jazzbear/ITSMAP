package com.example.jazzbear.au520839_stocks;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.Globals;

import com.example.jazzbear.au520839_stocks.Utils.StockListAdaptor;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import static com.example.jazzbear.au520839_stocks.Utils.Globals.STOCK_LOG;

public class OverviewActivity extends AppCompatActivity {
    // View elements
    Button addButton, refreshButton;
    private ListView stockListView;// Stock list we use to populate adaptor
    private StockListAdaptor stockListAdaptor;
    private List<StockQuote> listOfStockQuotes;
    private int listPosition; // Position iterator for listView, this is set according to stock list size
    private AlertDialog dialog;
    // Service related
    private ServiceConnection stockServiceConnection;
    StockService stockService;
    boolean serviceBound = false;

    // ############ LIFE CYCLE METHODS ###############
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        // For database debugging
        enableStethos();

        //Setup listView
        stockListView = findViewById(R.id.listViewStocks);
        listOfStockQuotes = new ArrayList<StockQuote>(){};
        //Register broadcast receiver and filters
        registerBroadcastReciever();
        // Setup connection, so we can bind to stockService later, and we also start the service here.
        setupServiceConnection();
        // Setup dialog window so we can use it when adding new stock symbol
        setupAddStockDialog();
        // Setup the adaptor
        setupListViewAdaptor();

        // Recreating after and instance saved state.
        // Otherwise initializes first stock with default values
        //TODO: need to re-implement savedState for listview
//        if (savedInstanceState != null) {
//
//        } else {
//
//        }

        addButton = findViewById(R.id.addStockBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(); // open the dialog window so we can use it.
            }
        });

        refreshButton = findViewById(R.id.btnRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stockService != null) {
                    stockService.requestRefreshStockList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to service, we need this so we can access the get stock list method
        bindToStockService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: Probably don't want to unregister from broadcasts here.
        unBindFromStockService();
    }

    //For registering broadcast receiver
    private void registerBroadcastReciever() {
        Log.d(STOCK_LOG, "registering receiver");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(StockService.SINGLE_STOCK_BROADCAST_ACTION);
        iFilter.addAction(StockService.LIST_OF_STOCKS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(overviewBroadcastReceiver, iFilter);
    }

    // this is inspired by Kasper Løvborg Jensens's earlier stuncode examples, from Lecture 4 and 5.
    private void setupListViewAdaptor() {
        stockListAdaptor = new StockListAdaptor(this, listOfStockQuotes);
        stockListView.setAdapter(stockListAdaptor);
        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Set a clickListener for each item in the adaptor view making the stocks clickable.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // we set the current list position so we stay in that position,
                // when the user comes back from details or edit activity.
                StockQuote listViewStockItem = listOfStockQuotes.get(position);
                //TODO save the position so we know what to update/remove on return. Remember to save it in savedInstanceState

                listPosition = position + 1;
                //Provided we get an item.
                if (listViewStockItem != null) {
                    getStockDetailsView(listViewStockItem);
                }
            }
        });
    }

    private void getStockDetailsView(StockQuote stockItem) {
        //Sent an intent to details and parse the stockQuote object.
        // TODO: Should maybe be application context or overview context
        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra(Globals.STOCKOBJECT_EXTRA, stockItem);
        startActivityForResult(detailsIntent, Globals.DETAILS_REQUEST);
    }


    //TODO: Need to change this to handle the stocklist
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(Globals.STOCK_STATE, stock);
    }

    // Note: Inspired by the developer guide from:
    // https://developer.android.com/guide/topics/ui/dialogs
    private void setupAddStockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater(); // Get inflater for current activity context
        //
        final View dialogView = inflater.inflate(R.layout.dialog_add_stock, null);
        builder.setView(dialogView)
                .setTitle(R.string.dialogTitle)
                .setPositiveButton(R.string.positiveDialogBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView symbolTextField = dialogView.findViewById(R.id.dialogSymbolField);
                        // In the service where we make the request we do error handling,
                        // if the symbol exist and the request succeeds, we add the symbol input,
                        // to the Global list of symbols.
                        final String symbol = symbolTextField.getText().toString();
                        //TODO: Should probably change so we can decide how many stocks you want to buy as well.
                        //TODO: And maybe also purchase price? We could add 2 more fields 2 the dialog.
                        //TODO: and then just handle it accordingly in the service, and methods that saves to the database.
                        stockService.requestSingleStock(symbol);
                        symbolTextField.setText(""); //clear the text field, so its ready for next time.
                    }
                }).setNegativeButton(R.string.negativeDialogBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Does nothing for no - should just cancel the window i guess
            }
        });
        // Create the dialog view with the above specifications, so we can use it later.
        dialog = builder.create();
    }

    // ########## OTHER METHODS ###############

    // Setting up connection to StockService so we can get the binder, so we can bind later and use it.
    private void setupServiceConnection() {
        // connect to service
        stockServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                //ref: http://developer.android.com/reference/android/app/Service.html
                // and also ref: https://developer.android.com/guide/components/bound-services
                stockService = ((StockService.StockServiceBinder) service).getService();
                Log.d(Globals.STOCK_LOG, "Stock service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = null;
                Log.d(Globals.STOCK_LOG, "Stock service disconnected");
            }
        };
        // Start the service so it runs in the background regardless
        //TODO: if this dont work make an intent first instead of this anonymous intent method
        startService(new Intent(this, StockService.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Globals.DETAILS_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Update the stock object and update the ui
//                assert data != null;
                if (data != null) {
                    //When we get some data from details activity intent result.
                    // First get the data
                    StockQuote stockQuote = data.getParcelableExtra(Globals.STOCKOBJECT_EXTRA);
                    //We update stocks properties in the stock list,
                    // because they could have changed, while in detailsActivity
                    stockQuote.setLatestStockValue(listOfStockQuotes.get(listPosition - 1).getLatestStockValue());
                    stockQuote.setStockPurchasePrice(listOfStockQuotes.get(listPosition - 1).getStockPurchasePrice());
                    stockQuote.setTimeStamp(listOfStockQuotes.get(listPosition - 1).getTimeStamp());
                    //TODO: Actualy i should update price difference here as well, and i need to add the field in details activity
                    //Now update the list and the list view adaptor
                    stockService.asyncUpdateSingleStock(stockQuote);
                    listOfStockQuotes.set(listPosition - 1, stockQuote);
                    stockListAdaptor.setListOfStocks(listOfStockQuotes);
                    stockListAdaptor.notifyDataSetChanged();
                }
            }
            else if (resultCode == Globals.RESULT_DELETE) {
                //Get the stock we want to delete
                StockQuote stockToDelete = listOfStockQuotes.get(listPosition - 1);
                //delete it and remove it from the list.
                stockService.asyncDeleteSingleStock(stockToDelete);
                // We remove it from the local list, and the async method before,
                // will do the same for the service's stockList
                listOfStockQuotes.remove(listPosition - 1);
                //update the list view adaptor with the changes
                stockListAdaptor.setListOfStocks(listOfStockQuotes);
                stockListAdaptor.notifyDataSetChanged();
            }
        }
    }


    //define our broadcast receiver for (local) broadcasts.
    // Registered and unregistered in onStart() and onStop() methods
    private BroadcastReceiver overviewBroadcastReceiver = new BroadcastReceiver() {
        //TODO: Should maybe have a final @Nullable here, if i want to check on intent actions instead.
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(STOCK_LOG, "Broadcast received from bg service");
            //First get the intent action code. Then handle it accordingly
            //I could use intent.getAction here instead,
            // but then i have to make the broadcast receiver accept nullable intent.
            String intentCode = intent.getStringExtra(StockService.BROADCAST_ACTION_RESULT_CODE);
            // A single stock was broadcast
            if (intentCode.equalsIgnoreCase(getResources().getString(R.string.broadcastActionSingleStock))) {
                Log.d(STOCK_LOG, "Broadcast received, call the handler for single stock result");
                handleStockResult();
            }
            // A list of stocks was broadcast.
            if (intentCode.equalsIgnoreCase(getResources().getString(R.string.broadcastActionMultiStock))) {
                Log.d(STOCK_LOG, "Broadcast received, call the handler for stockList result");
                handleStockListResult();
            }
        }
    };


    private void handleStockResult() {
        //Get the newest version of the list of stock quotes
        listOfStockQuotes = stockService.getServiceStockList();
        //Update the size of the listView
        listPosition = listOfStockQuotes.size();
        // make an intent to go for details view
        Intent goToDetailsIntent = new Intent(getApplicationContext(), EditActivity.class)
                .putExtra(Globals.STOCKOBJECT_EXTRA, listOfStockQuotes.get(listPosition -1));
        startActivityForResult(goToDetailsIntent, Globals.DETAILS_REQUEST);
        //And update the list adaptor since we just got the newest list from the database.
        stockListAdaptor.setListOfStocks(listOfStockQuotes);
        stockListAdaptor.notifyDataSetChanged();
    }

    private void handleStockListResult() {
        listOfStockQuotes = stockService.getServiceStockList();
        stockListAdaptor.setListOfStocks(listOfStockQuotes);
        stockListAdaptor.notifyDataSetChanged();
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
