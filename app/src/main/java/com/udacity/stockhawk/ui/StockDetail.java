package com.udacity.stockhawk.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class StockDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        setContentView(R.layout.activity_stock_detail);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        String stockSymbol = getIntent().getStringExtra(MainActivity.STOCK_SYMBOL);

        setTitle(stockSymbol);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            StockDetailFragment fragment = StockDetailFragment.newInstance(stockSymbol);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, fragment)
                    .commit();

            // Being here means we are in animation mode
            supportPostponeEnterTransition();
        }
    }










}
