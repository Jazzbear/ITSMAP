package com.example.jazzbear.au520839_stocks.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String CompanyName;
    private String StockSymbol;
    private String PrimaryExchange;
    private double LatestValue;
    private String TimeStamp;

    public Book(String companyName, String stockSymbol, String primaryExchange, double latestValue, String timeStamp) {
        CompanyName = companyName;
        StockSymbol = stockSymbol;
        PrimaryExchange = primaryExchange;
        LatestValue = latestValue;
        TimeStamp = timeStamp;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getStockSymbol() {
        return StockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        StockSymbol = stockSymbol;
    }

    public String getPrimaryExchange() {
        return PrimaryExchange;
    }

    public void setPrimaryExchange(String primaryExchange) {
        PrimaryExchange = primaryExchange;
    }

    public double getLatestValue() {
        return LatestValue;
    }

    public void setLatestValue(double latestValue) {
        LatestValue = latestValue;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }



    protected Book(Parcel in) {
        CompanyName = in.readString();
        StockSymbol = in.readString();
        PrimaryExchange = in.readString();
        LatestValue = in.readDouble();
        TimeStamp = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CompanyName);
        dest.writeString(StockSymbol);
        dest.writeString(PrimaryExchange);
        dest.writeDouble(LatestValue);
        dest.writeString(TimeStamp);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
