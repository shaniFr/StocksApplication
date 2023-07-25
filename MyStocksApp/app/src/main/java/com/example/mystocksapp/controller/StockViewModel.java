package com.example.mystocksapp.controller;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mystocksapp.model.Stock;
import com.example.mystocksapp.model.StockAPI;
import com.example.mystocksapp.model.StockDao;
import com.example.mystocksapp.model.StockDaoImplement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockViewModel extends AndroidViewModel {

    private static StockViewModel instance;

    private static final int UPDATE_INTERVAL = 10000; // 10 seconds

    private MutableLiveData<List<Stock>> stocksLiveData;
    private MutableLiveData<Stock> selectedStockItem;
    //private SharedPreferences sharedPreferences;
    private Handler updateHandler;
    private Runnable updateRunnable;
    private StockAPI stockAPI; // Add StockAPI instance
    boolean flag;
    private StockDao stockDao;
    int msg;

    private StockViewModel(@NonNull Application application) {
        super(application);
        stocksLiveData = new MutableLiveData<>();
        selectedStockItem = new MutableLiveData<>();
        //sharedPreferences = application.getSharedPreferences(STOCKS_PREFS_KEY, Context.MODE_PRIVATE);
        stockDao = new StockDaoImplement(application.getApplicationContext());
        updateHandler = new Handler();
        updateRunnable = this::updateStockData;
        stockAPI = new StockAPI(); // Instantiate StockAPI
        flag = true;

        scheduleDataUpdate();
    }

    public static synchronized StockViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new StockViewModel(application);
        }
        return instance;
    }


    public LiveData<List<Stock>> getStocksLiveData() {
        return stocksLiveData;
    }

    public void fetchStockData(String symbol) {
        stockAPI.requestStockData(symbol, new StockAPI.NetworkRequestCallback() {
            @Override
            public void onDataFetched(String response, String symbol) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    double price = jsonResponse.getDouble("c");
                    double percentChange = jsonResponse.getDouble("dp");

                    flag = false;
                    saveStockData(symbol, price, percentChange);
                    loadStocksFromSharedPreferences();
                    flag = true;
                    msg = 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg = -1;
                }
            }

            @Override
            public void onError() {
                // Handle error
            }
        });
    }

    private void saveStockData(String symbol, double price, double percentChange) {
        // Get the current list of stocks from SharedPreferences
        List<Stock> stocks = loadStocksFromSharedPreferences();

        boolean symbolExists = false;

        // Search for the stock with the matching symbol
        for (Stock stock : stocks) {
            if (stock.getSymbol().equals(symbol)) {
                // Update the existing stock with new data
                stock.setPrice(price);
                stock.setPercentChange(percentChange);
                symbolExists = true;
                break;
            }
        }

        if (!symbolExists) {
            // Create a new stock object
            Stock stock = new Stock(symbol, price, percentChange);
            // Add the new stock to the list
            stocks.add(stock);
        }

        stockDao.saveStocks(stocks);
    }

    public List<Stock> loadStocksFromSharedPreferences() {

        List<Stock> stocks = new ArrayList<>();
        stocks = stockDao.loadStocks();
        stocksLiveData.setValue(stocks);
        return stocks;
    }

    private void scheduleDataUpdate() {
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    private void updateStockData() {
        //while(!flag) {}
        List<Stock> stocks = stocksLiveData.getValue();
        if (stocks != null && !stocks.isEmpty()) {
            List<Stock> stocksToUpdate = new ArrayList<>();
            stocksToUpdate = loadStocksFromSharedPreferences();
            List<String> stocksSymbol = new ArrayList<>();
            for(Stock stock : stocks){
                stocksSymbol.add(stock.getSymbol());
            }
            for (Stock stock : stocksToUpdate) {
                // Check if the stock is removed
                if(!(stocksSymbol.contains(stock.getSymbol()))){
                    stocksToUpdate.remove(stock);
                    break;
                }
            }
            stockDao.removeStock(stocksToUpdate);

            stocksLiveData.setValue(stocksToUpdate);

            for (Stock stock : stocksToUpdate) {
                fetchStockData(stock.getSymbol());
            }
        }

        scheduleDataUpdate();

    }

    public void loadStocksData(){
        loadStocksFromSharedPreferences();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        updateHandler.removeCallbacks(updateRunnable);
    }

    public void selectStockItem(Stock stock) {
        selectedStockItem.setValue(stock);
    }

    public LiveData<Stock> getSelectedItem() {
        return selectedStockItem;
    }

}

