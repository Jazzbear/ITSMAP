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
import android.widget.EditText;
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
    private static final String OVERVIEW_LOG = "overview_log_tag";
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

    // ################### LIFE CYCLE METHODS ###################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup connection, so we can bind to stockService later, and we also start the service here.
        setupServiceConnection(); //Needs to be connected before we handle saved instance state.
        // For database debugging
        enableStethos();

        // Recreating after a onDestroy and instance state saved.
        if (savedInstanceState != null) {
            // On rotation or other means that destroy and recreate the app,
            // We restore the list position of the app.
            listPosition = savedInstanceState.getInt(Globals.LIST_STATE);
        }
        // Init the layout view for activity
        setContentView(R.layout.activity_overview);

        //Setup listView
        stockListView = findViewById(R.id.listViewStocks);
        listOfStockQuotes = new ArrayList<StockQuote>() {
        };
        //Register broadcast receiver and filters
        registerBroadcastReceiver();
        // Setup dialog window so we can use it when adding new stock symbol
        setupAddStockDialog();
        // Setup the adaptor
        setupListViewAdaptor();


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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindFromStockService();
        unregisterBroadcastReceiver();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //We save the list position of the view so when the activity gets destroyed,
        // and then recreated in land view mode, it can find its position in the list view again.
        outState.putInt(Globals.LIST_STATE, listPosition);
        super.onSaveInstanceState(outState);
    }

    // ################### BINDING AND SERVICE METHODS ###################

    //For registering broadcast receiver
    private void registerBroadcastReceiver() {
        Log.d(OVERVIEW_LOG, "registering receiver");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(StockService.SINGLE_STOCK_BROADCAST_ACTION);
        iFilter.addAction(StockService.LIST_OF_STOCKS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(overviewBroadcastReceiver, iFilter);
    }

    //Put it in a method in case i want to move it to a different lifecycle event.
    private void unregisterBroadcastReceiver() {
        Log.d(OVERVIEW_LOG, "unregistering receivers");
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(overviewBroadcastReceiver);
    }

    // Note: Inspired by the developer guide from:
    // https://developer.android.com/guide/topics/ui/dialogs
    private void setupAddStockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater(); // Get inflater for current activity context

        final View dialogView = inflater.inflate(R.layout.dialog_add_stock, null);
        builder.setView(dialogView)
                .setTitle(R.string.dialogTitle)
                .setPositiveButton(R.string.positiveDialogBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView symbolTextField = dialogView.findViewById(R.id.dialogSymbolField);
                        EditText amountTextField = dialogView.findViewById(R.id.dialogAmountField);
                        // In the service where we make the request we do error handling,
                        // if the symbol exist and the request succeeds, we add the symbol input,
                        // to the Global list of symbols. The input stock amount field, we just hold in a gl
                        final String symbol = symbolTextField.getText().toString().toUpperCase();
                        //Also we take the amount input and save in a local variable in the service.
                        //Then once the stock is found and about to be saved to the database,
                        //We manualy set the price and stock amount for the stock before its saved and broadcasted.
                        final int amount = Integer.parseInt(amountTextField.getText().toString());
                        stockService.requestSingleStock(symbol, amount);
                        symbolTextField.setText(""); //clear the text field, so its ready for next time.
                        amountTextField.setText("");
                    }
                }).setNegativeButton(R.string.negativeDialogBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nothing to be done here - default implementation does what it needs.
            }
        });
        // Create the dialog view with the above specifications, so we can use it later.
        dialog = builder.create();
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
                listPosition = position + 1;
                //Provided a new stock item is added to the list view.
                if (listViewStockItem != null) {
                    //Then get the details view.
                    getStockDetailsView(listViewStockItem);
                }
            }
        });
    }

    private void getStockDetailsView(StockQuote stockItem) {
        //Sent an intent to details and parse the stockQuote object.
        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra(Globals.STOCK_OBJECT_EXTRA, stockItem);
        startActivityForResult(detailsIntent, Globals.DETAILS_REQUEST);
    }

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
                Log.d(OVERVIEW_LOG, "Stock service connected");
                // Get the newest list.
                listOfStockQuotes = stockService.getServiceStockList();
                updateAdaptor();
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = null;
                Log.d(OVERVIEW_LOG, "Stock service disconnected");
            }
        };
        // Start the service so it runs in the background regardless
        startService(new Intent(this, StockService.class));
    }

    void bindToStockService() {
        // Method for binding to service
        bindService(new Intent(OverviewActivity.this, StockService.class),
                stockServiceConnection, Context.BIND_AUTO_CREATE); // creates service it on binding if it isn't created.

        Log.d(OVERVIEW_LOG, "Binding to service");
        serviceBound = true;
    }

    void unBindFromStockService() {
        if (serviceBound) {
            unbindService(stockServiceConnection);
            Log.d(OVERVIEW_LOG, "Unbinding from service");
            serviceBound = false;
        }
    }

    //define our broadcast receiver for (local) broadcasts.
    // Registered and unregistered in onStart() and onStop() methods
    private BroadcastReceiver overviewBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final @Nullable Intent intent) {
            Log.d(OVERVIEW_LOG, "Broadcast received from bg service");
            // Changed this to trigger on the broadcast action it self,
            // there was no need for the intent extra's i used as type of broadcast before.
            // and although the linter/compiler gives a warning about a possible null pointer exception.
            // It doesn't seem to have any issues handling the broadcasts.
            // So i chose to use it, to get less boilerplate code.
            assert intent != null;
            if (intent.getAction().equals(StockService.SINGLE_STOCK_BROADCAST_ACTION)) {
                Log.d(OVERVIEW_LOG, "Broadcast received, call the handler for single stock result");
                handleStockResult();
            } else if (intent.getAction().equals(StockService.LIST_OF_STOCKS_BROADCAST_ACTION)) {
                Log.d(OVERVIEW_LOG, "Broadcast received, call the handler for stockList result");
                handleStockListResult();
            }
        }
    };

    // ########## OTHER METHODS ###############

    private void handleStockResult() {
        //Get the newest version of the list of stock quotes
        listOfStockQuotes = stockService.getServiceStockList();
        //Update the size of the listView
        listPosition = listOfStockQuotes.size();

        // make an intent to go for details view
        Intent goToDetailsIntent = new Intent(getApplicationContext(), EditActivity.class)
                .putExtra(Globals.STOCK_OBJECT_EXTRA, listOfStockQuotes.get(listPosition - 1));
        startActivityForResult(goToDetailsIntent, Globals.DETAILS_REQUEST);

        //And update the list adaptor since we just got the newest list from the database.
        updateAdaptor();
    }

    private void handleStockListResult() {
        listOfStockQuotes = stockService.getServiceStockList();
        updateAdaptor();
    }

    // Method for updateAdaptor, since i call those 2 commands so many times
    private void updateAdaptor() {
        stockListAdaptor.setListOfStocks(listOfStockQuotes);
        stockListAdaptor.notifyDataSetChanged();
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
