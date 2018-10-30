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

public class VolleyActivity extends AppCompatActivity {
    // don't forget to use volley it needs to be implemented in the app manifest

    Button btnCheckConnect, btnCheckAll, btnSendRequest, btnParseJson, btnSwitch;
    TextView responseView, jsonResponseView;

    // we need a request que for volley:
    RequestQueue rQueue;

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
                sendWeatherRequest();
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
    }

    private void checkNetworkStatus() {
        String status = NetworkChecker.checkNetworkStatus(this);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    private void checkAllNetworks() {
        String status = NetworkChecker.getAllNetWorkStatus(this);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    private void sendWeatherRequest() {
        // send request using volley
        if (rQueue == null ) {
            // Instantiate new request que if one doesn't exist.
            rQueue = Volley.newRequestQueue(this);
        }

        String url = GlobalConstants.WEATHER_API_CALL; //the http url volley uses

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseView.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseView.setText("The request failed!");
            }
        });

        // Fire the request by adding it to the request queue.
        rQueue.add(stringRequest);
    }

    //attempt to decode the json response from weather server
    public void interpretWeatherJSON(String jsonResonse){
        jsonResponseView.setText(WeatherJsonParser.parseCityWeatherJsonWithGson(jsonResonse));
    }
}
