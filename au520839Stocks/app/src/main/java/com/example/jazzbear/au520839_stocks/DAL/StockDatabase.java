package com.example.jazzbear.au520839_stocks.DAL;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;

@Database(entities = {StockQuote.class}, version = 2)
public abstract class StockDatabase extends RoomDatabase {
    public abstract StockQuoteDao stockQuoteDao();

    private static volatile StockDatabase INSTANCE;

    public static StockDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StockDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        StockDatabase.class, "stock_database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            }
        }
        return INSTANCE;
    }
}
