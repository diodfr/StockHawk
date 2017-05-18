package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.utils.FormatterUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private static final String LOG_TAG = StockAdapter.class.getName();
    private final Context context;
    private Cursor cursor;
    private final StockAdapterOnClickHandler clickHandler;
    private String selectedSymbol;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {

        cursor.moveToPosition(position);
        int colSymbolIdx = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
        return cursor.getString(colSymbolIdx);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        cursor.moveToPosition(position);


        int position_symbol = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
        int position_price = cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE);
        int position_absolute_change = cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);
        int position_percentage_change = cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);

        String stockSymbol = cursor.getString(position_symbol);
        holder.symbol.setText(stockSymbol);
        holder.price.setText(FormatterUtils.DOLLAR_FORMAT.format(cursor.getFloat(position_price)));

        float rawAbsoluteChange = cursor.getFloat(position_absolute_change);
        float percentageChange = cursor.getFloat(position_percentage_change);

        if (rawAbsoluteChange > 0) {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = FormatterUtils.DOLLAR_FORMAT_WITHPLUS.format(rawAbsoluteChange);
        String percentage = FormatterUtils.PERCENTAGE_FORMAT.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(context)
                .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            holder.change.setText(change);
        } else {
            holder.change.setText(percentage);
        }

        boolean selected = selectedSymbol != null && selectedSymbol.equals(stockSymbol);

        Log.d(LOG_TAG, stockSymbol + " selected = " + selected);
        
        holder.itemView.setSelected(selected);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }

    interface StockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    void setSelectedSymbol(String selectedSymbol) {
        String oldSymbol = this.selectedSymbol;
        this.selectedSymbol = selectedSymbol;

        if (oldSymbol != null && !oldSymbol.equals(selectedSymbol)) {
            int itemPosition = getItemPosition(oldSymbol);
            if (itemPosition >= 0) {
                notifyItemChanged(itemPosition);
            }
        }
    }

    private int getItemPosition(String selectedSymbol) {
        for (int i = 0; i < cursor.getCount(); i++) {
            if (selectedSymbol.equals(getSymbolAtPosition(i))) return i;
        }
        return -1;
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.change)
        TextView change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            String stockSymbol = cursor.getString(symbolColumn);
            clickHandler.onClick(stockSymbol);
            setSelectedSymbol(stockSymbol);
            notifyItemChanged(adapterPosition);
        }
    }
}
