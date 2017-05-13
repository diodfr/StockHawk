package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.utils.FormatterUtils;

public class StockAppWidgetService extends RemoteViewsService {
    public static final String[] COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };
    public static final String LOG_TAG = StockAppWidgetService.class.getName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(LOG_TAG, "onGetViewFactory " + intent.getAction());
        return new StockAppRemoteViewFactory(this.getApplicationContext(), intent);
    }

    private class StockAppRemoteViewFactory implements RemoteViewsFactory {
        private final Context mContext;
        private final int mAppWidgetId;
        private Cursor data;
        private int indexStockId;
        private int indexStockSymbol;
        private int indexStockPrice;
        private int indexStockPercentageChange;

        public StockAppRemoteViewFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            Log.i(LOG_TAG, "onCreate");
        }

        @Override
        public void onDataSetChanged() {
            if (data != null) {
                data.close();
            }
            Log.i(LOG_TAG, "DIDIER DIDIER DIDIER onDataSetChanged");
            // This method is called by the app hosting the widget (e.g., the launcher)
            // However, our ContentProvider is not exported so it doesn't have access to the
            // data. Therefore we need to clear (and finally restore) the calling identity so
            // that calls use our process and permission
            final long identityToken = Binder.clearCallingIdentity();
            data = getContentResolver().query(Contract.Quote.URI, COLUMNS, null,
                    null, Contract.Quote.COLUMN_SYMBOL);
            if (data != null) {
                Log.i(LOG_TAG, "onDataSetChanged " + data.getCount());
                indexStockId = data.getColumnIndex(Contract.Quote._ID);
                indexStockSymbol = data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
                indexStockPrice = data.getColumnIndex(Contract.Quote.COLUMN_PRICE);
                indexStockPercentageChange = data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
            }
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            Log.d(LOG_TAG, "onDestroy");
            if (data != null) {
                data.close();
                data = null;
            }
        }

        @Override
        public int getCount() {
            Log.d(LOG_TAG, "getCount");
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.i(LOG_TAG, "getViewAt " + position);
            if (position == AdapterView.INVALID_POSITION ||
                    data == null || !data.moveToPosition(position)) {
                return null;
            }
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.stock_widget_detail);

            String stockSymbol = data.getString(indexStockSymbol);
            String stockPrice = FormatterUtils.DOLLAR_FORMAT.format(data.getFloat(indexStockPrice));
            String stockVariation = FormatterUtils.PERCENTAGE_FORMAT.format(data.getFloat(indexStockPercentageChange) / 100);
            Log.i(LOG_TAG, "getViewAt " + position + " " + stockSymbol + "#" + stockPrice + "#" + stockVariation);
            views.setTextViewText(R.id.StockSymbol, stockSymbol);
            views.setTextViewText(R.id.StockQuotation, stockPrice);
            views.setTextViewText(R.id.StockVariation, stockVariation);

            Log.i(LOG_TAG, "getViewAt " + stockSymbol + "#" + stockPrice + "#" + stockVariation);

            Intent fillInIntent = new Intent();

            Bundle extras = new Bundle();
            extras.putString(MainActivity.STOCK_SYMBOL, stockSymbol);

            fillInIntent.putExtras(extras);

            views.setOnClickFillInIntent(R.id.widgetItem, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            Log.d(LOG_TAG, "getLoadingView");
            return new RemoteViews(getPackageName(), R.layout.stock_widget_loading);

        }

        @Override
        public int getViewTypeCount() {
            Log.d(LOG_TAG, "getViewTypeCount");
            return 1;
        }

        @Override
        public long getItemId(int position) {
            Log.d(LOG_TAG, "getItemId");
            if (data.moveToPosition(position))
                return data.getLong(indexStockId);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}