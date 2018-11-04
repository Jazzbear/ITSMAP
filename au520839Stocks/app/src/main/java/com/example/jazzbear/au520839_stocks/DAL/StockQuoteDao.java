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

    @Query("SELECT * FROM stockquote WHERE uid in (:ids)")
    List<StockQuote> loadAllStocksById(int[] ids);

    @Query("SELECT * FROM stockquote where stockSymbol like :symbol")
    StockQuote getStockBySymbol(String symbol);

    @Query("SELECT * FROM stockquote WHERE stockSymbol in (:symbols)")
    List<StockQuote> loadAllStocksBySymbols(List<String> symbols);

    @Insert
    void insertSingleStock(StockQuote stock);

    // TODO: NOT USED
    @Insert
    void insertStockList(List<StockQuote> stockList);

    @Update
    void updateSingleStock(StockQuote stockQuote);

    //TODO: Should maybe return an int, which will indicate number of
    //TODO: tables updated in the database. That way there is something to check on.
    @Update
    void updateStockList(List<StockQuote> stockList);

    @Delete
    void deleteSingleStock(StockQuote stock);

    @Delete
    void deleteListOfStocks(List<StockQuote> stockList);
}
