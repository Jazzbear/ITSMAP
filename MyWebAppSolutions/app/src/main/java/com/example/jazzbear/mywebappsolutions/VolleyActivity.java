package com.example.jazzbear.mywebappsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jazzbear.mywebappsolutions.utils.GlobalConstants;
import com.example.jazzbear.mywebappsolutions.utils.NetworkChecker;
import com.example.jazzbear.mywebappsolutions.utils.WeatherJsonParser;

import java.util.ArrayList;
import java.util.List;

public class VolleyActivity extends AppCompatActivity {
    // don't forget to use volley it needs to be implemented in the app manifest

    Button btnCheckConnect, btnCheckAll, btnSendRequest, btnParseJson, btnSwitch, btnParseArray;
    TextView responseView, jsonResponseView;

    // we need a request que for volley:
    RequestQueue rQueue;
    List<String> symbolList;
    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // take over the content view of main.

        responseView = findViewById(R.id.txtResponse);
        responseView.setMovementMethod(new ScrollingMovementMethod());
        jsonResponseView = findViewById(R.id.txtJsonResult);

        btnCheckConnect = findViewById(R.id.btnCheckNetwork);
        btnCheckConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkStatus();
            }
        });

        btnCheckAll = findViewById(R.id.checkAllBtn);
        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllNetworks();
            }
        });

        btnSendRequest = findViewById(R.id.btnSendRequest);
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendWeatherRequest();
                sendStockRequest();
            }
        });

        btnParseJson = findViewById(R.id.btnJson);
        btnParseJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // provided an earlier request has been made,
                // and we have the response body in the text view.
                if (responseView.getText().toString() != null) {
                    //try to interpret JSON
                    interpretWeatherJSON(responseView.getText().toString());
//                    secondParseMethod(responseView.getText().toString());
                }
            }
        });

        btnSwitch = findViewById(R.id.btnSwitchMode);
        btnSwitch.setText("Switch to HttpsURLConnection");
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling finish to destroy VolleyActivity and go back to MainActivity,
                // letting it take over the content view again.
                finish();
            }
        });

        btnParseArray = findViewById(R.id.btnJsonArray);
        btnParseArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void checkNetworkStatus() {
        String status = NetworkChecker.checkNetworkStatus(this);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    private void checkAllNetworks() {
        String status = NetworkChecker.getAllNetWorkStatus(this);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

//    private void sendWeatherRequest() {
//        // send request using volley
//        if (rQueue == null ) {
//            // Instantiate new request que if one doesn't exist.
//            rQueue = Volley.newRequestQueue(this);
//        }
//
//        String url = GlobalConstants.WEATHER_API_CALL; //the http url volley uses
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        responseView.setText(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                responseView.setText("The request failed!");
//            }
//        });
//
//        // Fire the request by adding it to the request queue.
//        rQueue.add(stringRequest);
//    }

    private void sendStockRequest() {
        // send request using volley
        if (rQueue == null ) {
            // Instantiate new request que if one doesn't exist.
            rQueue = Volley.newRequestQueue(this);
        }
        // get the list of symbols
//        List<String> symbolList = GlobalConstants.stockSymbolList;
        symbolList = GlobalConstants.stockSymbolList;
        int count = 0; // iterator

        StringBuilder csvList = new StringBuilder();
        for (String s : symbolList) {
            csvList.append(s);
            // Check if its the last item in the list, if not append a comma
            if (count++ != symbolList.size() -1 ) {
                csvList.append(",");
            }
        }

        String callUrl = GlobalConstants.STOCK_MARKET_STRING + csvList + GlobalConstants.STOCK_QUOTE_FILTER_STRING;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, callUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseView.setText(response);
                        jsonResponse = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseView.setText("The request failed");
            }
        });

        rQueue.add(stringRequest);
    }

    //attempt to decode the json response from weather server
    public void interpretWeatherJSON(String jsonResp){
        jsonResponseView.setText(WeatherJsonParser.parseCityWeatherJsonWithGson(jsonResp));
    }

    public void secondParseMethod(String jsonResp) {
        // TODO: Here we need to split the json response string into sperate object strings so i can map them seperately
        jsonResponseView.setText(WeatherJsonParser.parseCityWeatherJson(jsonResp));
    }
}
