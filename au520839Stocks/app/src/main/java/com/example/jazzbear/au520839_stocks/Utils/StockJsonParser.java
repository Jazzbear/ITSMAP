package com.example.jazzbear.au520839_stocks.Utils;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StockJsonParser {

    // Method for parsing a single json stock object to a stockQuote object.
    public static StockQuote parseSingleStockJson(String stockSymbol, String jsonInput) {
        Log.d(Globals.STOCK_LOG, stockSymbol);
        StockQuote stockQuote = new StockQuote(); // we make a new stock object
        try {
            //then make a json object from the string input, from the volley response
            JSONObject jsonStock = new JSONObject(jsonInput);
            // from the json object pick out the symbol object
            JSONObject jsonSymbol = jsonStock.getJSONObject(stockSymbol);
            // From the jsonSymbol object take out the quote.
            JSONObject jsonQuote = jsonSymbol.getJSONObject("quote");
            // then map all the stock properties
            stockQuote.setCompanyName(jsonQuote.getString("companyName"));
            stockQuote.setStockSymbol(jsonQuote.getString("symbol"));
            stockQuote.setPrimaryExchange(jsonQuote.getString("primaryExchange"));
            stockQuote.setLatestPrice(jsonQuote.getDouble("latestPrice"));
            stockQuote.setTimeStamp(jsonQuote.getString("latestTime"));
            stockQuote.setOpeningPrice(jsonQuote.getDouble("open"));
            stockQuote.setClosingPrice(jsonQuote.getDouble("close"));
            stockQuote.setChangePercentage(jsonQuote.getDouble("changePercent"));
            stockQuote.setSector(jsonQuote.getString("sector"));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(Globals.STOCK_LOG, "Something went wrong with single stock request:\n" + e);
        }

        return stockQuote;
    }

    // method for parsing json list of stock objects
    public static List<StockQuote> parseStockListJson(List<String> symbolList, String jsonInput) {
        // Receiving a string with json objects,
        // we extract the info and map it to a stockQuote object.

        List<StockQuote> listOfStocks = new ArrayList<StockQuote>(); // we make a new List of stock objects
        try {
            //then make a json object from the string input, from the volley response
            JSONObject jsonStockList = new JSONObject(jsonInput);
            // now instead of just mapping one object as in the method above.
            // we go through each stock symbol object in the json response,
            // and map it to the list of stock quotes.
            for (String symbol : symbolList) {
                StockQuote stockQuote = new StockQuote();
                JSONObject jsonSymbol = jsonStockList.getJSONObject(symbol);
                JSONObject jsonQuote = jsonSymbol.getJSONObject("quote");
                stockQuote.setCompanyName(jsonQuote.getString("companyName"));
                stockQuote.setStockSymbol(jsonQuote.getString("symbol"));
                stockQuote.setPrimaryExchange(jsonQuote.getString("primaryExchange"));
                stockQuote.setLatestPrice(jsonQuote.getDouble("latestPrice"));
                stockQuote.setTimeStamp(jsonQuote.getString("latestTime"));
                stockQuote.setOpeningPrice(jsonQuote.getDouble("open"));
                stockQuote.setClosingPrice(jsonQuote.getDouble("close"));
                stockQuote.setChangePercentage(jsonQuote.getDouble("changePercent"));
                stockQuote.setSector(jsonQuote.getString("sector"));
                listOfStocks.add(stockQuote);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(Globals.STOCK_LOG, "Something went terribly wrong, with the list of stock objects:\n" + e);
        }

        return listOfStocks;
    }
}
