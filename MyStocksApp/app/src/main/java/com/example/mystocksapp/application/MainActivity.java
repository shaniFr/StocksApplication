package com.example.mystocksapp.application;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mystocksapp.R;
import com.example.mystocksapp.model.StockGraphFragment;

public class MainActivity extends AppCompatActivity implements FirstScreenFragment.FirstScreenFragmentListener, AddStockFragment.AddStockFragmentListener, StocksTableFragment.StocksTableFragListener, StockGraphFragment.FragStockGraphListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void OnClickShowStocks() {
        //replaceFragment(new StocksTableFragment());

        StocksTableFragment stocksTableFragment;

        //this block create fragB dynamically in the stack
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragContainer, StocksTableFragment.class, null, "STOCKTABLEFRAGMENT")
                .addToBackStack("BBB")
                .commit();
        getSupportFragmentManager().executePendingTransactions();

        stocksTableFragment = (StocksTableFragment) getSupportFragmentManager().findFragmentByTag("STOCKTABLEFRAGMENT");
        stocksTableFragment.loadStocksTable();
    }

    @Override
    public void OnClickSearch() {
        OnClickShowStocks();
    }

    @Override
    public void OnClickAddStock() {
        AddStockFragment addStockFragment;

        //this block create fragB dynamically in the stack
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragContainer, AddStockFragment.class, null, "FRAGADDSTOCK")
                .addToBackStack("BBB")
                .commit();
        getSupportFragmentManager().executePendingTransactions();

        addStockFragment = (AddStockFragment) getSupportFragmentManager().findFragmentByTag("FRAGADDSTOCK");
        addStockFragment.OnClickAddStock();
    }

    @Override
    public void openStockGraphFragment() {
        StockGraphFragment stockGraphFragment;

        //Bundle bundle = new Bundle();
        //bundle.putString("symbol", symbol);
        //stockGraphFragment.setArguments(bundle);

        //this block create fragB dynamically in the stack

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragContainer, StockGraphFragment.class, null, "FRAGSTOCKGRAPH")
                .addToBackStack("BBB")
                .commit();


        getSupportFragmentManager().executePendingTransactions();

        stockGraphFragment = (StockGraphFragment) getSupportFragmentManager().findFragmentByTag("FRAGSTOCKGRAPH");
        stockGraphFragment.OnClickStockItem();

    }

}
