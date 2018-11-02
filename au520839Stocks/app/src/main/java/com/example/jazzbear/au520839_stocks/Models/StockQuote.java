package com.example.jazzbear.au520839_stocks.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(indices = {@Index(value = "stockSymbol", unique = true)})
public class StockQuote implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String companyName;
    private String stockSymbol;
    private String primaryExchange;
    private long latestValue;
    private String timeStamp;
    private double openingPrice;
    private double closingPrice;
    private long changePercentage;

    public StockQuote() {}

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
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

    public long getLatestValue() {
        return latestValue;
    }

    public void setLatestValue(long latestValue) {
        this.latestValue = latestValue;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(double openingPrice) {
        this.openingPrice = openingPrice;
    }

    public double getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(double closingPrice) {
        this.closingPrice = closingPrice;
    }

    public long getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(long changePercentage) {
        this.changePercentage = changePercentage;
    }


    protected StockQuote(Parcel in) {
        companyName = in.readString();
        stockSymbol = in.readString();
        primaryExchange = in.readString();
        latestValue = in.readLong();
        timeStamp = in.readString();
        openingPrice = in.readDouble();
        closingPrice = in.readDouble();
        changePercentage = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyName);
        dest.writeString(stockSymbol);
        dest.writeString(primaryExchange);
        dest.writeLong(latestValue);
        dest.writeString(timeStamp);
        dest.writeDouble(openingPrice);
        dest.writeDouble(closingPrice);
        dest.writeLong(changePercentage);
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