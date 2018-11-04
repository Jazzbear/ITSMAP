package com.example.jazzbear.au520839_stocks.Utils;

import android.content.Context;
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
            itemListStock = listOfStocks.get(position);
            TextView symbolText = convertView.findViewById(R.id.itemSymbol);
            symbolText.setText(itemListStock.getStockSymbol());
            TextView companyNameText = convertView.findViewById(R.id.itemCompanyName);
            companyNameText.setText(itemListStock.getCompanyName());
            TextView priceText = convertView.findViewById(R.id.itemCurrentPrice);
            priceText.setText(Double.toString(itemListStock.getLatestPrice()));
            TextView priceDifference = convertView.findViewById(R.id.itemPriceDifference);
            priceDifference.setText(String.format("%.2f", itemListStock.getPriceDifference()));
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
