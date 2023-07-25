package com.example.mystocksapp.model;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystocksapp.R;
import com.example.mystocksapp.controller.StockViewModel;

import java.util.ArrayList;
import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StockViewHolder> {
    private List<Stock> stocks = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private StockViewModel stockViewModel;

    public StocksAdapter(StockViewModel stockViewModel) {
        this.stockViewModel = stockViewModel;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_recycleview, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.fillData(stock);
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView symbolTextView;
        private TextView priceTextView;
        private TextView percentChangeTextView;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            percentChangeTextView = itemView.findViewById(R.id.dailyChangeTextView);

            itemView.setOnClickListener(this);

            // Set long press listener on the item view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Stock stock = stocks.get(position);
                        // Remove the stock from the shared preferences
                        //stockViewModel.removeStock(stock);
                        // Remove the item from the data source
                        stocks.remove(position);
                        // Notify the adapter of the item removal
                        notifyItemRemoved(position);
                        return true; // Consume the long press event
                    }
                    return false;
                }
            });
        }

        public void fillData(Stock stock) {
            symbolTextView.setText(stock.getSymbol());
            String priceString = String.format("%.2f", stock.getPrice());
            priceTextView.setText(priceString);
            String percentChangeString = String.format("%.2f", stock.getPercentChange()) + "%";
            if (stock.getPercentChange() > 0){
                percentChangeString = "+" +String.format("%.2f", stock.getPercentChange()) + "%";
                percentChangeTextView.setTextColor(Color.GREEN);
            }
            if (stock.getPercentChange() < 0){
                percentChangeTextView.setTextColor(Color.RED);
            }
            percentChangeTextView.setText(percentChangeString);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Stock stock = stocks.get(position);
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(stock);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
    }
}
