package com.example.jazzbear.mywebappsolutions.utils;

import java.util.ArrayList;
import java.util.List;

public class GlobalConstants {
    public static final String CONNECT_LOG = "CONNECTIVITY";

    public static final String WEATHER_API_KEY = "1d6e60935af8d2a787cf04a580e5983f";
    public static final long CITY_ID_AARHUS = 2624652;

    // The &APPID= header parameter is specified by the OpenWeatherMap api documentations
    // HTTP CALL STRING, used by volley
    // https://api.openweathermap.org/data/2.5/weather?id=2624652&APPID=1d6e60935af8d2a787cf04a580e5983f
    public static final String WEATHER_API_CALL = "https://api.openweathermap.org/data/2.5/weather?id=" + CITY_ID_AARHUS + "&APPID=" + WEATHER_API_KEY;
    // HTTPS CALL STRING, used by HttpsURLConnect
    public static final String WEATHER_API_HTTPS_CALL = "https://api.openweathermap.org/data/2.5/weather?id=" + CITY_ID_AARHUS + "&APPID=" + WEATHER_API_KEY;

    // Make this the first part of the request string.
    // and then append the symbols after it in a CSV manner.
    public static final String STOCK_MARKET_STRING = "https://api.iextrading.com/1.0/stock/market/batch?symbols=";
    // Append this string after all the symbols to filter the data collected.
    public static final String STOCK_QUOTE_FILTER_STRING = "&types=quote&filter=companyName,symbol,primaryExchange,latestPrice,latestTime";

    public static final List<String> stockSymbolList = new ArrayList<String>() {
        {
            add("TSLA");
            add("AAPL");
            add("ATVI");
        }
    };
}
