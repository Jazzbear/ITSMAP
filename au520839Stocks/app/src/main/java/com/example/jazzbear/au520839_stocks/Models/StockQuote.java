package com.example.jazzbear.au520839_stocks.Models;

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
    private double purchasedValue;
    private double latestPrice;
    private String timeStamp;
    private double openingPrice;
    private double closingPrice;
    private double changePercentage;
    private String sector;

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

    public double getPurchasedValue() {
        return purchasedValue;
    }

    public void setPurchasedValue(double purchasedValue) {
        this.purchasedValue = purchasedValue;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
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

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    protected StockQuote(Parcel in) {
        companyName = in.readString();
        stockSymbol = in.readString();
        primaryExchange = in.readString();
        purchasedValue = in.readDouble();
        latestPrice = in.readDouble();
        timeStamp = in.readString();
        openingPrice = in.readDouble();
        closingPrice = in.readDouble();
        changePercentage = in.readDouble();
        sector = in.readString();
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
        dest.writeDouble(purchasedValue);
        dest.writeDouble(latestPrice);
        dest.writeString(timeStamp);
        dest.writeDouble(openingPrice);
        dest.writeDouble(closingPrice);
        dest.writeDouble(changePercentage);
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