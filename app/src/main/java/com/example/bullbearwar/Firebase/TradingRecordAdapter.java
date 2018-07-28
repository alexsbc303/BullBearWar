package com.example.bullbearwar.Firebase;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bullbearwar.R;
import com.google.common.collect.Lists;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class TradingRecordAdapter extends  RecyclerView.Adapter<TradingRecordAdapter.StockViewHolder> {
    private Context mCtx;
    private List<TradeRecordStruct> searchRecords;

    public TradingRecordAdapter(Context mCtx, List<TradeRecordStruct> searchRecords) {
        this.mCtx = mCtx;
        this.searchRecords = Lists.reverse(searchRecords);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.trading_record, null);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        TradeRecordStruct record = searchRecords.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
//        format.setCurrency(Currency.getInstance("CZK"));
        format.setMinimumFractionDigits(0);

        holder.transcationTime.setText(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", record.getTradeDateTime()).toString());
        holder.stockSym.setText(record.getStockSym());
        holder.totalPrice.setText(format.format(record.getTransactionPrice()));

        holder.shares.setText(String.valueOf(record.getShares()));
        if (record.getTransactionType()){
            holder.transcationType.setText("Sell");
            String profitString = format.format(record.getProfit());

            holder.avgPrice.setText(profitString);
            if (record.getProfit() <= 0){
                holder.avgPrice.setTextColor(Color.RED);
            }else{
                holder.avgPrice.setTextColor(Color.GREEN);
            }

        }else{
            holder.transcationType.setText("Buy");

            holder.avgPrice.setText(format.format(record.getTransactionPrice() / record.getShares()));
        }

    }

    @Override
    public int getItemCount() {
        return searchRecords.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder {

        TextView transcationTime;
        TextView stockSym, shares, avgPrice, totalPrice, transcationType;

        public StockViewHolder(View itemView) {
            super(itemView);
            transcationTime = itemView.findViewById(R.id.transcationTime);
            stockSym = itemView.findViewById(R.id.stockSym);
            shares = itemView.findViewById(R.id.shares);
            avgPrice = itemView.findViewById(R.id.avgPrice);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            transcationType = itemView.findViewById(R.id.transcationType);
        }
    }
}