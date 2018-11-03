package com.example.jazzbear.au520839_stocks.Utils;

import java.util.ArrayList;
import java.util.List;

public class Globals {
    public static final int DETAILS_REQUEST = 101;
    public static final int EDIT_REQUEST = 201;
    public static final String STOCKOBJECT_EXTRA = "com.jazzbear.stockobject";
    public static final String STOCK_STATE = "state_of_stock";
    public static final String STOCK_LOG = "STOCK_DEBUG_LOGGING";

    public static final String REQUEST_TAG = "stock_request_tag";

    public static final String API_URL_STRING = "https://api.iextrading.com/1.0/";
    // Make this the first part of the request string.
    // and then append the symbols after it in a CSV manner.
    public static final String STOCK_MARKET_STRING = "https://api.iextrading.com/1.0/stock/market/batch?symbols=";
    // Append this string after all the symbols to filter the data collected.
    public static final String STOCK_QUOTE_FILTER_STRING = "&types=quote&filter=companyName,symbol,primaryExchange,latestPrice,latestTime,open,close,changePercent,sector";

    public static String STOCK_SYMBOL = "";

    public static final List<String> stockSymbolList = new ArrayList<String>() {
        {
            add("TSLA");
            add("AAPL");
            add("ATVI");
        }
    };
}
