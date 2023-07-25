package com.example.mystocksapp.model;

import com.example.mystocksapp.model.Stock;

import java.util.List;

public interface StockDao {
    public void saveStocks(List<Stock> stocks);
    public List<Stock> loadStocks();
    public void removeStock(List<Stock> stocksToUpdate);
}
