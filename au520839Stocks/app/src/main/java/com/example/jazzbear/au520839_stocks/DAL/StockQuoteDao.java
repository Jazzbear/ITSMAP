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
    @Query("SELECT * FROM stockquote")
    List<StockQuote> getAllStocks();

    @Query("SELECT * FROM stockquote WHERE uid IN (:ids)")
    List<StockQuote> loadAllStocksById(long[] ids);

//    @Query("SELECT * FROM stockquote where stockSymbol like :symbol")
//    StockQuote getStockBySymbol(String symbol);
//
//    @Query("SELECT * FROM stockquote WHERE stockSymbol in (:symbols)")
//    List<StockQuote> loadAllStocksBySymbols(List<String> symbols);

    // INSERTS
    @Insert
    long insertSingleStock(StockQuote stock);

//    // TODO: NOT USED
//    @Insert
//    void insertStockList(List<StockQuote> stockList);

    // UPDATES
    @Update
    void updateSingleStock(StockQuote stockQuote);

    @Update
    void updateStockList(List<StockQuote> stockList);

    // DELETES
    @Delete
    void deleteSingleStock(StockQuote stock);

}
