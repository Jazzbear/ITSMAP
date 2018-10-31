package com.example.jazzbear.au520839_stocks.Models;

import android.os.Parcel;
import android.os.Parcelable;

/*DISCLIAMER!!!!!! I used these sites/examples to help me create my stock class:
 * https://en.proft.me/2017/02/28/pass-object-between-activities-android-parcelable/
 * http://www.parcelabler.com/ */

public class Stock implements Parcelable{
    private String StockName;
    private double StockPrice;
    private int StockAmount;
    private String StockSector;

    public String getStockName() {
        return StockName;
    }
    public void setStockName(String stockName) {
        StockName = stockName;
    }
    public double getStockPrice() {
        return StockPrice;
    }
    public void setStockPrice(double stockPrice) {
        StockPrice = stockPrice;
    }
    public int getStockAmount() {
        return StockAmount;
    }
    public void setStockAmount(int stockAmount) {
        StockAmount = stockAmount;
    }
    public String getStockSector() {
        return StockSector;
    }
    public void setStockSector(String stockSector) {
        StockSector = stockSector;
    }

    //implicit Constructor
    public Stock() {}

    //Explicit Constructor
    public Stock(String stockName, double stockPrice, int stockAmount, String stockSector) {
        this.StockName = stockName;
        this.StockPrice = stockPrice;
        this.StockAmount = stockAmount;
        this.StockSector = stockSector;
    }

    /*This is to retrieve the stock data from the parcel object.
     * The constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR */
    private Stock(Parcel in) {
        StockName = in.readString();
        StockPrice = in.readDouble();
        StockAmount = in.readInt();
        StockSector = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Storing the Stock data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(StockName);
        dest.writeDouble(StockPrice);
        dest.writeInt(StockAmount);
        dest.writeString(StockSector);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
