package com.example.bullbearwar.Firebase;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bullbearwar.AutoTrading.AutoTradingRecordStruct;
import com.example.bullbearwar.R;
import com.google.common.collect.Lists;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TradingRecrodAdapter_Auto extends  RecyclerView.Adapter<TradingRecrodAdapter_Auto.StockViewHolder> {
    private Context mCtx;
    private List<AutoTradingRecordStruct> searchRecords;

    public TradingRecrodAdapter_Auto(Context mCtx, List<AutoTradingRecordStruct> searchRecords) {
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
        AutoTradingRecordStruct record = searchRecords.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
//        format.setCurrency(Currency.getInstance("CZK"));
        format.setMinimumFractionDigits(0);

        holder.stockSym.setText(record.getTargetStockSym());
        if (record.getTradeType().compareTo("Auto Buy") == 0){
            holder.sharesAndPrice.setText(String.valueOf(record.getTargetAskVolume())  + "*"+ format.format(record.getTargetAskPrice()));
            holder.status.setText(record.getStatus());
            holder.cancel_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else{
            holder.sharesAndPrice.setText(String.valueOf(record.getTargetBidVolume())  + "*"+ format.format(record.getTargetBidPrice()));
            holder.status.setText(record.getStatus());
            holder.cancel_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return searchRecords.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder {

        TextView stockSym, sharesAndPrice, transcationType, status;
        Button cancel_action;

        public StockViewHolder(View itemView) {
            super(itemView);

            stockSym = itemView.findViewById(R.id.stockSym);
            sharesAndPrice = itemView.findViewById(R.id.shares);
            status = itemView.findViewById(R.id.status);

            transcationType = itemView.findViewById(R.id.transcationType);
            cancel_action = itemView.findViewById(R.id.cancel_action);
        }
    }
}