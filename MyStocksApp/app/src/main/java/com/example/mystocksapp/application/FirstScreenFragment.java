package com.example.mystocksapp.application;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mystocksapp.R;
import com.example.mystocksapp.controller.StockViewModel;

public class FirstScreenFragment extends Fragment {

    private FirstScreenFragmentListener listener;
    private Button btnShowStocks;
    private StockViewModel stockViewModel;

    @Override
    public void onAttach(Context context) {
        try {
            this.listener = (FirstScreenFragmentListener) context;
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
        View view = inflater.inflate(R.layout.first_screen, container, false);
        btnShowStocks = view.findViewById(R.id.ShowStockButton);
        //stockViewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);

        btnShowStocks.setOnClickListener(v -> {
            stockViewModel.loadStocksData();
            listener.OnClickShowStocks();
        });

        return view;
    }

        interface FirstScreenFragmentListener{
            public void OnClickShowStocks();
        }
}
