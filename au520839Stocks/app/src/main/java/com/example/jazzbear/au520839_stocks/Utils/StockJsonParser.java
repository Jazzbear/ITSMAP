package com.example.jazzbear.au520839_stocks.Utils;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StockJsonParser {
    // method for parsing json stock object
    public static List<StockQuote> parseSingleStockJson(List<String> symbolList, String jsonInput) {
        // Receiving a string with single json object,
        // we extract the info and map it to a book object.
        List<StockQuote> listOfStocks = new ArrayList<StockQuote>();
        try {
            JSONObject jsonStock = new JSONObject(jsonInput);
            for (String symbol : symbolList) {
                StockQuote stockQuote = new StockQuote();
                JSONObject jsonSymbol = jsonStock.getJSONObject(symbol);
                JSONObject jsonQuote = jsonSymbol.getJSONObject("quote");
                stockQuote.setCompanyName(jsonQuote.getString("companyName"));
                stockQuote.setStockSymbol(jsonQuote.getString("symbol"));
                stockQuote.setPrimaryExchange(jsonQuote.getString("primaryExchange"));
                stockQuote.setLatestValue(jsonQuote.getLong("latestPrice"));
                stockQuote.setTimeStamp(jsonQuote.getString("latestTime"));
                stockQuote.setOpeningPrice(jsonQuote.getDouble("open"));
                stockQuote.setClosingPrice(jsonQuote.getDouble("close"));
                stockQuote.setChangePercentage(jsonQuote.getLong("changePercent"));
                listOfStocks.add(stockQuote);
            }
//            Log.d(Globals.STOCK_LOG, stockQuote.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(Globals.STOCK_LOG, "Something went terribly wrong: " + e);
        }

        return listOfStocks;
    }
}
