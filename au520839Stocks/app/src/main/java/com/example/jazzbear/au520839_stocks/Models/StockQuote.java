package com.example.jazzbear.au520839_stocks.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

@Entity(indices = {@Index(value = "stockSymbol", unique = true)})
public class StockQuote implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    private String companyName;
    private String stockSymbol;
    private String primaryExchange;
    private double stockPurchasePrice;
    private int amountOfStocks;
    private double latestStockValue;
    private String timeStamp;
    private double priceDifference;
    private String sector;

    public StockQuote() {}

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getPrimaryExchange() {
        return primaryExchange;
    }

    public void setPrimaryExchange(String primaryExchange) {
        this.primaryExchange = primaryExchange;
    }

    public double getStockPurchasePrice() {
        return stockPurchasePrice;
    }

    public void setStockPurchasePrice(double stockPurchasePrice) {
        this.stockPurchasePrice = stockPurchasePrice;
    }

    public int getAmountOfStocks() {
        return amountOfStocks;
    }

    public void setAmountOfStocks(int amountOfStocks) {
        this.amountOfStocks = amountOfStocks;
    }

    public double getLatestStockValue() {
        return latestStockValue;
    }

    public void setLatestStockValue(double latestStockValue) {
        this.latestStockValue = latestStockValue;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getPriceDifference() {
        return latestStockValue - stockPurchasePrice;
    }

    public void setPriceDifference(double priceDifference) {
        this.priceDifference = priceDifference;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    protected StockQuote(Parcel in) {
        uid = in.readLong();
        companyName = in.readString();
        stockSymbol = in.readString();
        primaryExchange = in.readString();
        stockPurchasePrice = in.readDouble();
        amountOfStocks = in.readInt();
        latestStockValue = in.readDouble();
        timeStamp = in.readString();
        priceDifference = in.readDouble();
        sector = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(companyName);
        dest.writeString(stockSymbol);
        dest.writeString(primaryExchange);
        dest.writeDouble(stockPurchasePrice);
        dest.writeInt(amountOfStocks);
        dest.writeDouble(latestStockValue);
        dest.writeString(timeStamp);
        dest.writeDouble(priceDifference);
        dest.writeString(sector);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StockQuote> CREATOR = new Parcelable.Creator<StockQuote>() {
        @Override
        public StockQuote createFromParcel(Parcel in) {
            return new StockQuote(in);
        }

        @Override
        public StockQuote[] newArray(int size) {
            return new StockQuote[size];
        }
    };
}