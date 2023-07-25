package com.example.mystocksapp.model;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mystocksapp.R;
import com.example.mystocksapp.controller.StockViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StockGraphFragment extends Fragment {

    FragStockGraphListener listener;
    private static final String ARG_STOCK = "stock";

    //private WebView webView;
    private RadioGroup radioGroup;
    private StockViewModel stockViewModel;
    private LineChart lineChart;
    private RadioButton dailyRadioButton;
    private TextView txtSymbol;

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (FragStockGraphListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'FragAListener'");
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stockViewModel = StockViewModel.getInstance(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_graph, container, false);
        //webView = rootView.findViewById(R.id.webView);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        lineChart = rootView.findViewById(R.id.lineChart);
        dailyRadioButton = rootView.findViewById(R.id.radioButtonDaily);
        txtSymbol = rootView.findViewById(R.id.symbolTxt);
        // Set the desired radio button as checked
        dailyRadioButton.setChecked(true);
        //stockViewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        stockViewModel.getSelectedItem().observe(getViewLifecycleOwner(),stock -> {
            if (stock != null) {
                String symbol = stock.getSymbol();
                txtSymbol.setText(symbol);
                fetchChartData(symbol, "daily");
            }
        });

        // Set a listener for radio button changes
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null) {
                    String timeRange = getTimeRangeFromRadioButtonId(checkedId);
                    //stockViewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
                    stockViewModel.getSelectedItem().observe(getViewLifecycleOwner(),stock -> {
                        if (stock != null) {
                            String symbol = stock.getSymbol();
                            fetchChartData(symbol, timeRange);
                        }
                    });
                }
            }
        });

        return rootView;
    }

    private String getTimeRangeFromRadioButtonId(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.radioButtonDaily:
                return "daily";
            case R.id.radioButtonWeekly:
                return "weekly";
            case R.id.radioButtonMonthly:
                return "monthly";
            default:
                return "daily";
        }
    }

    private void fetchChartData(String symbol, String timeRange) {
        // Construct the API URL based on the symbol and time range
        String apiUrl = constructApiUrl(symbol, timeRange);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String response = convertStreamToString(inputStream);

                        // Parse the JSON response and extract the necessary data
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray pricesArray = jsonObject.getJSONArray("c");
                        JSONArray timestampsArray = jsonObject.getJSONArray("t");

                        List<Double> pricesList = new ArrayList<>();
                        List<Long> timestampsList = new ArrayList<>();

                        for (int i = 0; i < pricesArray.length(); i++) {
                            double price = pricesArray.getDouble(i);
                            long timestamp = timestampsArray.getLong(i);
                            pricesList.add(price);
                            timestampsList.add(timestamp);
                        }

                        // Update the chart with the retrieved data
                        updateChart(pricesList, timestampsList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String convertStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void updateChart(List<Double> prices, List<Long> timestamps) {
        // Convert the prices and timestamps lists to a suitable format for your chart library
        // Example: Create an array of Entry objects for MPAndroidChart library
        // Get the first price
        Double firstPrice = prices.get(0);

        // Get the last price
        Double lastPrice = prices.get(prices.size() - 1);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            entries.add(new Entry(timestamps.get(i), prices.get(i).floatValue()));
        }

        // Create a LineDataSet with the entries
        LineDataSet dataSet = new LineDataSet(entries, "Stock Price");

        // Customize the appearance of the dataset (e.g., line color, fill color, etc.)
        // dataSet.setColor(...);
        // dataSet.setFillColor(...);
        if(lastPrice - firstPrice < 0){
            dataSet.setColor(Color.RED);
            dataSet.setDrawFilled(true); // Enable filling area under the line
            dataSet.setFillColor(Color.RED);
        }
        else{
            dataSet.setColor(Color.GREEN);
            dataSet.setDrawFilled(true); // Enable filling area under the line
            dataSet.setFillColor(Color.GREEN);
        }
        // Create a LineData object and set the dataset
        LineData lineData = new LineData(dataSet);

        // Set the LineData object to the LineChart
        lineChart.setData(lineData);

        // Refresh the chart to update the display
        lineChart.invalidate();
    }

    private String constructApiUrl(String symbol, String timeRange) {
        // Construct the API URL based on the symbol and time range
        // Example: https://api.finnhub.io/api/v1/stock/candle?symbol=AAPL&resolution=D&from=1625230800&to=1627822799&token=YOUR_API_TOKEN
        String baseUrl = "https://api.finnhub.io/api/v1/stock/candle";
        String resolution;
        long fromTimestamp, toTimestamp;

        // Determine the resolution and timestamps based on the time range
        switch (timeRange) {
            case "daily":
                resolution = "D";
                // Calculate the timestamps for the past 30 days
                fromTimestamp = System.currentTimeMillis() / 1000L - 30 * 24 * 60 * 60;
                toTimestamp = System.currentTimeMillis() / 1000L;
                break;
            case "weekly":
                resolution = "W";
                // Calculate the timestamps for the past 12 weeks
                fromTimestamp = System.currentTimeMillis() / 1000L - 12 * 7 * 24 * 60 * 60;
                toTimestamp = System.currentTimeMillis() / 1000L;
                break;
            case "monthly":
                resolution = "M";
                // Calculate the timestamps for the past 12 months
                fromTimestamp = System.currentTimeMillis() / 1000L - 12 * 30 * 24 * 60 * 60;
                toTimestamp = System.currentTimeMillis() / 1000L;
                break;
            default:
                // Invalid time range, set default values
                resolution = "D";
                fromTimestamp = System.currentTimeMillis() / 1000L - 30 * 24 * 60 * 60;
                toTimestamp = System.currentTimeMillis() / 1000L;
                break;
        }

        // Construct the final API URL
        String apiUrl = baseUrl + "?symbol=" + symbol + "&resolution=" + resolution +
                "&from=" + fromTimestamp + "&to=" + toTimestamp + "&token=YOUR_API_KEY";

        return apiUrl;
    }

    public void OnClickStockItem() {

    }

    public interface FragStockGraphListener{ }
}
