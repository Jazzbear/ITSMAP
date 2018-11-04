package com.example.jazzbear.au520839_stocks.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

@Entity(indices = {@Index(value = "stockSymbol", unique = true)})
public class StockQuote implements Parcelable {

    //TODO: maybe make it to GUIDs
    @PrimaryKey
    private String uid = UUID.randomUUID().toString();

    private String companyName;
    private String stockSymbol;
    private String primaryExchange;
    private double stockValue;
    private int amountOfStocks; //getters og setters need
    private double latestPrice;
    private String timeStamp;
//    private double openingPrice; // TODO: Maybe use this if there is time?
//    private double closingPrice; // TODO: And also this
    private double priceDifference; //TODO: also change parser method
    private String sector;

    public StockQuote() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
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

    // We should set the purchase value in dialog maybe? Or just set it to the latest price
    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }
    //TODO: These amount getter and setters needs to be used in dialog and edit
    public int getAmountOfStocks() {
        return amountOfStocks;
    }

    public void setAmountOfStocks(int amountOfStocks) {
        this.amountOfStocks = amountOfStocks;
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

//    public double getOpeningPrice() {
//        return openingPrice;
//    }
//
//    public void setOpeningPrice(double openingPrice) {
//        this.openingPrice = openingPrice;
//    }
//
//    public double getClosingPrice() {
//        return closingPrice;
//    }
//
//    public void setClosingPrice(double closingPrice) {
//        this.closingPrice = closingPrice;
//    }

    public double getPriceDifference() {
        return latestPrice - stockValue;
    }

//    public void setPriceDifference(double priceDifference) {
//        this.priceDifference = priceDifference;
//    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    protected StockQuote(Parcel in) {
        uid = in.readString();
        companyName = in.readString();
        stockSymbol = in.readString();
        primaryExchange = in.readString();
        stockValue = in.readDouble();
        amountOfStocks = in.readInt();
        latestPrice = in.readDouble();
        timeStamp = in.readString();
//        openingPrice = in.readDouble();
//        closingPrice = in.readDouble();
        priceDifference = in.readDouble();
        sector = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(companyName);
        dest.writeString(stockSymbol);
        dest.writeString(primaryExchange);
        dest.writeDouble(stockValue);
        dest.writeInt(amountOfStocks);
        dest.writeDouble(latestPrice);
        dest.writeString(timeStamp);
//        dest.writeDouble(openingPrice);
//        dest.writeDouble(closingPrice);
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