package com.example.mystocksapp.application;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mystocksapp.R;
import com.example.mystocksapp.controller.StockViewModel;

public class AddStockFragment extends Fragment {
    private EditText etSearchStock;
    private Button btnSearchStock;
    private StockViewModel stockViewModel;
    private AddStockFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        try {
            this.listener = (AddStockFragment.AddStockFragmentListener) context;
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
        View view = inflater.inflate(R.layout.fragment_add_stock, container, false);
        etSearchStock = view.findViewById(R.id.editTextSearchStock);
        btnSearchStock = view.findViewById(R.id.searchStockButton);

        //stockViewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);

        btnSearchStock.setOnClickListener(v -> {
            String symbol = etSearchStock.getText().toString().trim();
            if (!TextUtils.isEmpty(symbol)) {
                stockViewModel.fetchStockData(symbol);
            }
            listener.OnClickSearch();
        });

        return view;
    }

    public void OnClickAddStock() {

    }

    interface AddStockFragmentListener{
        public void OnClickSearch();
    }
}

