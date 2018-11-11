package com.example.jazzbear.au520839_stocks.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;
import com.example.jazzbear.au520839_stocks.R;

import java.util.List;

// Note: The code for this adaptor class is inspired/influenced by the stunt code of lecture 5,
// provided by Kasper Løvborg Jensen. One of the main changes though is the use of BaseAdaptor.
public class StockListAdaptor extends BaseAdapter {

    private List<StockQuote> listOfStocks;
    private Context context;
    private StockQuote itemListStock;

    // Constructor for instantiating the adapter.
    public StockListAdaptor(Context context, List<StockQuote> stockList) {
        this.context = context;
        listOfStocks = stockList;
    }

    // Returns the amount if list items as the size of the stock list, this is needed for iteration
    @Override
    public int getCount() {
        if (listOfStocks == null) {
            return 0;
        }
        return listOfStocks.size();
    }

    //Grab specific listView item.
    @Override
    public Object getItem(int position) {
        // check that list isn't empty and that position is lower than the size,
        // so that we don't go out of bounds.
        if (listOfStocks != null && listOfStocks.size() > position) {
            return listOfStocks.get(position);
        }
        return null;
    }

    //One of the default methods implemented.
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check that the convertView isn't already instantiated, if not, inflate the view
        // with our custom item layout: item_stocklist.
        if (convertView == null) {
            LayoutInflater inflater;
            // Get the layout inflater from the calling activity context.
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_stocklist, null);
        }

        // again first check the stockList isn't empty and that we aren't at the end of the list.
        if (listOfStocks != null && listOfStocks.size() > position) {
            itemListStock = listOfStocks.get(position); // get the positions of of the stock list.
            // Adaptor view elements.
            TextView symbolText = convertView.findViewById(R.id.itemSymbol);
            TextView companyNameText = convertView.findViewById(R.id.itemCompanyName);
            TextView priceText = convertView.findViewById(R.id.itemCurrentPrice);
            TextView priceDifference = convertView.findViewById(R.id.itemPriceDifference);
            TextView timeStamp = convertView.findViewById(R.id.itemTimestamp);
            TextView stockAmount = convertView.findViewById(R.id.itemStockAmount);
            TextView totalEarnins = convertView.findViewById(R.id.itemTotalEarnings);
            symbolText.setText(itemListStock.getStockSymbol());
            companyNameText.setText(itemListStock.getCompanyName());
            priceText.setText(Double.toString(itemListStock.getLatestStockValue()));

            // Added the functionality of coloring the text view.
            // Coloring price difference by using the technique from this post:
            // https://stackoverflow.com/questions/4602902/how-to-set-the-text-color-of-textview-in-code
            Double priceDiff = itemListStock.getPriceDifference();
            if (priceDiff < 0) {
                //Setting the max decimals for the double. Im only interested in the last 3 digits here.
                priceDifference.setText(String.format("%.3f", itemListStock.getPriceDifference()));
                priceDifference.setTextColor(Color.parseColor("#ff0000")); //Red
            }
            else if (priceDiff > 0) {
                priceDifference.setText(String.format("%.3f", itemListStock.getPriceDifference()));
                priceDifference.setTextColor(Color.parseColor("#00cc00")); //green
            } else {
                priceDifference.setText(String.format("%.3f", itemListStock.getPriceDifference()));
//                priceDifference.setTextColor(Color.parseColor("#000000")); //black
            }

            timeStamp.setText(itemListStock.getTimeStamp());
            stockAmount.setText(context.getString(R.string.adaptorNumberOfStocks, Integer.toString(itemListStock.getAmountOfStocks())));
            //Only want the last 2 digits here.
            totalEarnins.setText(context.getString(R.string.adaptorTotalValue, String.format("%.2f", itemListStock.getTotalEarnings())));
            return convertView;
        }
        // Until the list is populated we return null, and keep the listView empty
        return null;
    }

    public List<StockQuote> getListOfStocks() {
        return listOfStocks;
    }

    public void setListOfStocks(List<StockQuote> stockList) {
        this.listOfStocks = stockList;
    }
}
