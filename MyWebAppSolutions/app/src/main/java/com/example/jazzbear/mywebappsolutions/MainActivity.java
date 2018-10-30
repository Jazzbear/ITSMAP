package com.example.jazzbear.mywebappsolutions;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.jazzbear.mywebappsolutions.utils.GlobalConstants.CONNECT_LOG;

import com.example.jazzbear.mywebappsolutions.utils.GlobalConstants;
import com.example.jazzbear.mywebappsolutions.utils.NetworkChecker;
import com.example.jazzbear.mywebappsolutions.utils.WeatherJsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button btnCheckConnect, btnCheckAll, btnSendRequest, btnParseJson, btnSwitch;
    TextView responseView, jsonResponseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    //TODO: move last result to member variable instead of reading from TextView
                    interpretWeatherJSON(responseView.getText().toString());
                }
            }
        });

        btnSwitch = findViewById(R.id.btnSwitchMode);
        btnSwitch.setText("Switch to Volley");
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to start up the VolleyActivity class.
                // So it can take over the view.
                Intent intent = new Intent(MainActivity.this, VolleyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendWeatherRequest() {
        DownloadWeatherTask task = new DownloadWeatherTask();
        // Execute the asynchronous call to the weather api.
        task.execute(GlobalConstants.WEATHER_API_HTTPS_CALL);
    }

    //we extend Async task and can create any number of these new DownloadTasks as need - need to call execute()
    //this is so that we do not block the ui with our http/https requests.
    // As a reminder the AsyncTask 3 parameters in the deffiniton is params, progress and result, since we do not need progress update here.
    // progress is just left as void, since we expect no callback from progress. But input param and return values are strings.
    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            Log.d(CONNECT_LOG, "Starting background task");
            // calling the actual method that does the http request.
            return callUrl(urls[0]); // expecting one url at a time its the first one in the array.
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                responseView.setText(result);
            }
        }
    }

    private String callUrl(String inputUrl) {
        // We need an input stream so we can convert the json input to a string later.
        InputStream inputStream = null;

        try {
            URL url = new URL(inputUrl);

            //configure Http(s)URLConnection object (HTTP or HTTPs):
            //HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Normal http
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection(); // secure https

            connection.setReadTimeout(1000); // wait 1 second only
            connection.setConnectTimeout(1500); // time out 500 milliseconds after the read timeout
            connection.setRequestMethod("GET");
            connection.setDoInput(true); // Not really needed as it is set by default, but good convention

            // Start the request
            connection.connect();
            int response = connection.getResponseCode();

            //probably check on response code here!

            //give user feedback in case of error

            Log.d(CONNECT_LOG, "The response is: " + response);
            inputStream = connection.getInputStream();

            // Convert the InputStream into a string
            String contentToString = convertStreamToStringBuffered(inputStream);
            return contentToString;
        } catch (ProtocolException pe) {
            Log.d(CONNECT_LOG, "oh noes....ProtocolException"); // HTTP error something went wrong with the request
        } catch (UnsupportedEncodingException uee) {
            Log.d(CONNECT_LOG, "oh noes....UnsuportedEncodingException"); // unsupported file format
        } catch (IOException ioe) {
            Log.d(CONNECT_LOG, "oh noes....IOException"); // IO operation error, most likely the input stream or the convertBuffer
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); //Close the input stream when we are done to free resources
                } catch (IOException ioe) {
                    Log.d(CONNECT_LOG, "oh noes....could not close stream, IOException"); // Again IO error when trying to work with the input stream.
                }
            }
        }

        return null;
    }

    private String convertStreamToStringBuffered(InputStream inputStream) {
        String output = "";
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            while ((line = reader.readLine()) != null) {
                output += line;
            }
        } catch (IOException e) {
            Log.e(CONNECT_LOG, "ERROR reading HTTP response", e);
        }

        return output;
    }

    private void checkNetworkStatus() {
        String status = NetworkChecker.checkNetworkStatus(MainActivity.this);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    private void checkAllNetworks() {
        String status = NetworkChecker.getAllNetWorkStatus(this);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    //attempt to decode the json response from weather server
    public void interpretWeatherJSON(String jsonResonse){
        jsonResponseView.setText(WeatherJsonParser.parseCityWeatherJsonWithGson(jsonResonse));
    }
}
