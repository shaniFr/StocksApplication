package com.example.mystocksapp.application;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystocksapp.R;
import com.example.mystocksapp.model.Stock;
import com.example.mystocksapp.controller.StockViewModel;
import com.example.mystocksapp.model.StocksAdapter;

public class StocksTableFragment extends Fragment {
    StocksTableFragListener listener;
    private StockViewModel stockViewModel;
    private RecyclerView recyclerView;
    private Button btnAddStock;
    private StocksAdapter adapter;

    @Override
    public void onAttach(Context context) {
        try {
            this.listener = (StocksTableFragListener) context;
        } catch (ClassCastException e) {
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
        View view = inflater.inflate(R.layout.stock_table_fragment, container, false);
        recyclerView = view.findViewById(R.id.stocksRecyclerview);
        btnAddStock = view.findViewById(R.id.addStockButton);

        btnAddStock.setOnClickListener(v ->{
            listener.OnClickAddStock();
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //stockViewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        adapter = new StocksAdapter(stockViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        stockViewModel.getStocksLiveData().observe(getViewLifecycleOwner(), stocks -> {
            adapter.setStocks(stocks);
            adapter.notifyDataSetChanged();
        });

        adapter.setOnItemClickListener(new StocksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Stock stock) {
                //StockGraphFragment stockGraphFragment = new StockGraphFragment();
                //Bundle bundle = new Bundle();
                //bundle.putString("symbol", stock.getSymbol());
                //stockGraphFragment.setArguments(bundle);
                //stockViewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
                stockViewModel.selectStockItem(stock);
                listener.openStockGraphFragment();
            }
        });
    }

    public void loadStocksTable() {
    }

    interface StocksTableFragListener{
        public void OnClickAddStock();
        public void openStockGraphFragment();
    }
}
