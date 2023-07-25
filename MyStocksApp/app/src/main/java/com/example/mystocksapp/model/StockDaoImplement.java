package com.example.mystocksapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mystocksapp.model.Stock;
import com.example.mystocksapp.model.StockDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockDaoImplement implements StockDao {

    private SharedPreferences sharedPreferences;
    private static final String STOCKS_PREFS_KEY = "stocks_prefs";
    private static final String STOCKS_PREFS_LIST_KEY = "stocks_prefs_list";

    public StockDaoImplement(Context context) {
        sharedPreferences = context.getSharedPreferences(STOCKS_PREFS_KEY, Context.MODE_PRIVATE);
    }

    @Override
    public void saveStocks(List<Stock> stocks) {
        // Convert the list of stocks to a JSON array
        JSONArray jsonArray = new JSONArray();
        for (Stock s : stocks) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("symbol", s.getSymbol());
                jsonObject.put("price", s.getPrice());
                jsonObject.put("percentChange", s.getPercentChange());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Save the JSON array to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STOCKS_PREFS_LIST_KEY, jsonArray.toString());
        editor.apply();

    }

    @Override
    public List<Stock> loadStocks() {
        List<Stock> stocks = new ArrayList<>();
        String jsonString = sharedPreferences.getString(STOCKS_PREFS_LIST_KEY, null);
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String symbol = jsonObject.getString("symbol");
                    double price = jsonObject.getDouble("price");
                    double percentChange = jsonObject.getDouble("percentChange");
                    Stock stock = new Stock(symbol, price, percentChange);
                    stocks.add(stock);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  stocks;
    }

    @Override
    public void removeStock(List<Stock> stocksToUpdate) {
        // Convert the updated list of stocks to a JSON array
        JSONArray jsonArray = new JSONArray();
        for (Stock s : stocksToUpdate) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("symbol", s.getSymbol());
                jsonObject.put("price", s.getPrice());
                jsonObject.put("percentChange", s.getPercentChange());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Save the updated JSON array to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STOCKS_PREFS_LIST_KEY, jsonArray.toString());
        editor.apply();
    }

}
