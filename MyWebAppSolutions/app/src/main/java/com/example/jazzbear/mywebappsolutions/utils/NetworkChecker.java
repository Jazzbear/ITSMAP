package com.example.jazzbear.mywebappsolutions.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import static com.example.jazzbear.mywebappsolutions.utils.GlobalConstants.CONNECT_LOG;

//utility class for checking network status and properties
public class NetworkChecker {

    //gets network status of currently active network
    public static String checkNetworkStatus(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            Log.d(CONNECT_LOG, "Got connection to web" + netInfo.toString());

            return "Connection Success" + netInfo.toString();
        } else {
            Log.d(CONNECT_LOG, "No connections maaaan");
            return "Connection unsuccessful";
        }
    }

    //checks all available networks
    public static String getAllNetWorkStatus(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos;
        //Check that we have the correct minimum SKD build
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            assert connManager != null;
            Network[] networks = connManager.getAllNetworks();
            networkInfos = new NetworkInfo[networks.length];
            for (int i = 0; i < networks.length ; i++) {
                // iterate through the array of networks, and append the information to the array of network-info's
                networkInfos[i] = connManager.getNetworkInfo(networks[i]);
            }

        } else {
            networkInfos = connManager.getAllNetworkInfo();
        }

        StringBuilder s = new StringBuilder();
        if (networkInfos != null && networkInfos.length > 0) {
            for (NetworkInfo info : networkInfos) {
                if (info != null) {
                    s.append(info.toString()).append("\n **************************** \n");
                }
            }
            Log.d(CONNECT_LOG, "Got connections:\n" + s);
            return "Got connections" + s;
        } else {
            //oh no, no connection
            Log.d(CONNECT_LOG, "No connections");
            return "No connections";
        }
    }
}
