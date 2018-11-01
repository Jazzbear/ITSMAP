package com.example.jazzbear.au520839_stocks;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jazzbear.au520839_stocks.Models.Stock;
import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.Utils.Globals;
import com.example.jazzbear.au520839_stocks.Utils.StockJsonParser;

import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    //    static final String OVERVIEW_SAVED = "overview_is_set";
    TextView overviewStockName, stockPurchasePrice, responseView;
    ImageView imgView;
    Button detailsButton, testBtn, refreshBtn, translateBtn;
    Stock stock;

    // Request queue for volley
    RequestQueue rQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

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

//        translateBtn = findViewById(R.id.jsonTranslateBtn);
//        translateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (responseView.getText().toString() != null) {
//                    interpretStockJson(responseView.getText().toString());
//                }
//            }
//        });

        refreshBtn = findViewById(R.id.getStocksBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStockRequest();
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Globals.STOCK_STATE, stock);
    }

    private void testRequestUrl() {
//        List<String> symbolList = Globals.stockSymbolList;
//        int count = 0;
//        StringBuilder csvList = new StringBuilder();
//        for (String s : symbolList) {
//            csvList.append(s);
//            // Check if its the last item in the list, if not append a comma
//            if (count++ != symbolList.size() -1 ) {
//                csvList.append(",");
//            }
//        }
//
//        toast(Globals.STOCK_MARKET_STRING + csvList + Globals.STOCK_QUOTE_FILTER_STRING);
        String callUrl = Globals.STOCK_MARKET_STRING + "TSLA" + Globals.STOCK_QUOTE_FILTER_STRING;
        toast(callUrl);
    }

    private void sendStockRequest() {
        // send request using volley
        if (rQueue == null ) {
            // Instantiate new request que if one doesn't exist.
            rQueue = Volley.newRequestQueue(this);
        }
        // get the list of symbols
//        List<String> symbolList = Globals.stockSymbolList;
//        int count = 0; // iterator
//
//        StringBuilder csvList = new StringBuilder();
//        for (String s : symbolList) {
//            csvList.append(s);
//            // Check if its the last item in the list, if not append a comma
//            if (count++ != symbolList.size() -1 ) {
//                csvList.append(",");
//            }
//        }

//        String callUrl = Globals.STOCK_MARKET_STRING + csvList + Globals.STOCK_QUOTE_FILTER_STRING;
        final String symbol = "TSLA";
        String callUrl = Globals.STOCK_MARKET_STRING + symbol + Globals.STOCK_QUOTE_FILTER_STRING;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, callUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        responseView.setText(response);
                        StockQuote responseQuote = StockJsonParser.parseSingleStockJson(symbol, response);
                        String responseText = "Here is the response:\n" + responseQuote.getCompanyName()
                                + "\n" + responseQuote.getLatestValue();
                        responseView.setText(responseText);
//                        responseView.setText("Here is the reponse info:\n" + responseQuote.getCompanyName());
//                        interpretStockJson(response);
                        Log.d(Globals.STOCK_LOG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseView.setText("The request failed");
            }
        });

        rQueue.add(stringRequest);
    }

    public void interpretStockJson(String jsonText) {
//        StockQuote responseQuote = StockJsonParser.parseSingleStockJson(jsonText);
//        responseView.setText(responseQuote.getCompanyName());
//        Log.d(Globals.STOCK_LOG, responseQuote.getCompanyName());
    }

    // Used toasts for debugging
    private void toast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_LONG).show();
    }
}
