package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity  implements StockListFragment.StockSelectionListener{

    public static final String STOCK_SYMBOL = "STOCK_SYMBOL";
    private static final String LOG_TAG = MainActivity.class.getName();

    private boolean mTwoSides;
    private String stockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        QuoteSyncJob.initialize(this);

        mTwoSides = findViewById(R.id.stockDetail) != null;
        Log.d(LOG_TAG, "Two side layout ? " + mTwoSides);
        if (savedInstanceState == null) {
            stockSymbol = getIntent().getStringExtra(MainActivity.STOCK_SYMBOL);
        } else {
            stockSymbol = savedInstanceState.getString(STOCK_SYMBOL);
        }

        if (stockSymbol != null) {
            if (mTwoSides) {
                // Create the detail fragment and add it to the activity
                // using a fragment transaction.
                stockSelected(stockSymbol);
            } else {
                StockListFragment stockListFragment = (StockListFragment) getSupportFragmentManager().findFragmentById(R.id.stockList);
                stockListFragment.setSelectedSymbol(stockSymbol);
                stockSelected(stockSymbol);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STOCK_SYMBOL, stockSymbol);
    }

    @Override
    public void stockSelected(String stockSymbol) {
        this.stockSymbol = stockSymbol;
        if (mTwoSides) {
            StockDetailFragment details = (StockDetailFragment) getSupportFragmentManager().findFragmentById(R.id.stockDetail);
            if (details == null || !stockSymbol.equals(details.getStockSymbol())) {
                // Make new fragment to show this selection.
                details = StockDetailFragment.newInstance(stockSymbol);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.stockDetail, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            Log.d(LOG_TAG,"Symbol clicked: "  + stockSymbol);
            Intent detail = new Intent(this, StockDetail.class);
            detail.putExtra(STOCK_SYMBOL, stockSymbol);

            startActivity(detail);
        }
    }
}
