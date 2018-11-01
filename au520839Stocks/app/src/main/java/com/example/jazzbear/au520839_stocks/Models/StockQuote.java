package com.example.jazzbear.au520839_stocks.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class StockQuote implements Parcelable {
    private String CompanyName;
    private String StockSymbol;
    private String PrimaryExchange;
    private long LatestValue;
    private String TimeStamp;
    private double OpeningPrice;
    private double ClosingPrice;
    private long ChangePercentage;

    public StockQuote() {}

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

    public long getLatestValue() {
        return LatestValue;
    }

    public void setLatestValue(long latestValue) {
        LatestValue = latestValue;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public double getOpeningPrice() {
        return OpeningPrice;
    }

    public void setOpeningPrice(double openingPrice) {
        OpeningPrice = openingPrice;
    }

    public double getClosingPrice() {
        return ClosingPrice;
    }

    public void setClosingPrice(double closingPrice) {
        ClosingPrice = closingPrice;
    }

    public long getChangePercentage() {
        return ChangePercentage;
    }

    public void setChangePercentage(long changePercentage) {
        ChangePercentage = changePercentage;
    }


    protected StockQuote(Parcel in) {
        CompanyName = in.readString();
        StockSymbol = in.readString();
        PrimaryExchange = in.readString();
        LatestValue = in.readLong();
        TimeStamp = in.readString();
        OpeningPrice = in.readDouble();
        ClosingPrice = in.readDouble();
        ChangePercentage = in.readLong();
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
        dest.writeLong(LatestValue);
        dest.writeString(TimeStamp);
        dest.writeDouble(OpeningPrice);
        dest.writeDouble(ClosingPrice);
        dest.writeLong(ChangePercentage);
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