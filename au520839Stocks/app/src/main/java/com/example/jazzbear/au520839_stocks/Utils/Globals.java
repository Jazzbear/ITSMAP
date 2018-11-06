package com.example.jazzbear.au520839_stocks.Utils;

import java.util.ArrayList;
import java.util.List;

public class Globals {
    // Activity intent request codes.
    public static final int DETAILS_REQUEST = 101;
    public static final int EDIT_REQUEST = 201;
    //For communicating between the activities.
    public static final String STOCK_OBJECT_EXTRA = "com.jazzbear.stockobject";
    public static final String STOCK_STATE = "state_of_stock";
    public static final String LIST_STATE = "position_of_list";
    public static final String STOCK_LOG = "STOCK_DEBUG_LOGGING";
    public static final String REQUEST_TAG = "stock_request_tag";
    //Custom result code used in onActivityResult, between details and overview
    public static final int RESULT_DELETE = 999;
    //Appended before symbols
    public static final String STOCK_MARKET_ENDPOINT = "https://api.iextrading.com/1.0/stock/market/batch?symbols=";
    // Append this string after all the symbols to filter the data collected.
    public static final String STOCK_QUOTE_FILTER_STRING = "&types=quote&filter=companyName,symbol,primaryExchange,latestPrice,latestTime,open,close,changePercent,sector";
    // For notifications
    public static final String CHANNEL_ID = "stock_channel";
    public static final int NOTIFY_ID = 1337;

    // List used to first setup database.
    public static final List<String> initDataBaseSymbolList = new ArrayList<String>() {
        {
            add("T"); //AT&T Corp. Huge american corp. Is or used to be the biggest phone and networks company in the us i think?
            add("TSLA"); //Tesla
            add("AAPL"); //Appel Inc.
            add("INTC"); //Intel Corp.
            add("AMD"); //AMD The main competitor to intel.
            add("EA"); // Electronic Arts. Watch as this stock will continue to go down.
            add("ATVI"); //Activision blizzard.
            add("TTWO"); //Take-Two interactive, they own Rockstar studio.
            add("CSCO"); //Cisco systems inc.
            add("NFLX"); //Netflix
        }
    };
}
