package com.example.jazzbear.assignmentone;

import android.os.Parcel;
import android.os.Parcelable;

public class Stock implements Parcelable {
    private String StockName;
    private int StockPrice;
    private int StockAmount;
    private String StockSector;

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public int getStockPrice() {
        return StockPrice;
    }

    public void setStockPrice(int stockPrice) {
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

    public Stock(String stockName, int stockPrice, int stockAmount, String stockSector) {
        this.StockName = stockName;
        this.StockPrice = stockPrice;
        this.StockAmount = stockAmount;
        this.StockSector = stockSector;
    }

    protected Stock(Parcel in) {
        StockName = in.readString();
        StockPrice = in.readInt();
        StockAmount = in.readInt();
        StockSector = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(StockName);
        dest.writeInt(StockPrice);
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
