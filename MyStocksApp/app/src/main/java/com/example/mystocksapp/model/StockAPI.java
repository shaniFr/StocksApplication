package com.example.mystocksapp.model;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockAPI {
    private static final String API_KEY = "YOUR_API_KEY";
    private static Handler handler;


    public StockAPI() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static void requestStockData(String symbol, NetworkRequestCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataFetched(responseData, symbol);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError();
                        }
                    });
                }
            }
        });
    }



    public interface NetworkRequestCallback {
        void onDataFetched(String response, String symbol);
        void onError();
    }
}
