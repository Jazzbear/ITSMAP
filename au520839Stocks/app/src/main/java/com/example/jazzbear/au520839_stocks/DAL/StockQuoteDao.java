package com.example.jazzbear.au520839_stocks.DAL;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.jazzbear.au520839_stocks.Models.StockQuote;

import java.util.List;

@Dao
public interface StockQuoteDao {
    @Query("SELECT * FROM stockquote WHERE uid LIKE (:id)")
    StockQuote getSingleStockQuote(int id);

    @Query("SELECT * FROM stockquote")
    List<StockQuote> getAllStockQuotes();

    @Query("SELECT * FROM stockquote WHERE uid IN (:ids)")
    List<StockQuote> loadAllStocksById(long[] ids);

    // INSERTS
    @Insert
    long insertSingleStock(StockQuote stock);


    @Insert
    void insertStockList(List<StockQuote> stockList);

    // UPDATES
    @Update
    void updateSingleStock(StockQuote stockQuote);

    @Update
    void updateStockList(List<StockQuote> stockList);

    // DELETES
    @Delete
    void deleteSingleStock(StockQuote stock);

}
