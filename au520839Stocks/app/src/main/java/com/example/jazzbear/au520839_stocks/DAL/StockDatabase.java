package com.example.jazzbear.au520839_stocks.DAL;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;

@Database(entities = {StockQuote.class}, version = 3)
public abstract class StockDatabase extends RoomDatabase {
    private static volatile StockDatabase INSTANCE;
    // All publicly relevant database info and methods here
    public abstract StockQuoteDao stockQuoteDao();
    public static final String DATABASE_NAME = "au520839_stock_database";
    public static final String DATABASE_PATH = "//data/data/com.example.jazzbear.au520839_stocks/databases/" + DATABASE_NAME;
    //data/data/<Your-Application-Package-Name>/databases/<your-database-name>

    public static StockDatabase getDatabaseInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (StockDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        StockDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return INSTANCE;
    }
}
