package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity  implements StockListFragment.StockSelectionListener{

    private static final String TAG = MainActivity.class.getName();

    public static final String STOCK_SYMBOL = "STOCK_SYMBOL";

    private boolean mTwoSides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        QuoteSyncJob.initialize(this);

        //FIXME Manage detail fragment
        // FIXME Manage details fragment from widget

        mTwoSides = findViewById(R.id.stockDetail) != null;
        if(mTwoSides) {
            if (savedInstanceState == null) {
                // Create the detail fragment and add it to the activity
                // using a fragment transaction.

                String stockSymbol = getIntent().getStringExtra(MainActivity.STOCK_SYMBOL);
                StockDetailFragment fragment = StockDetailFragment.newInstance(stockSymbol);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.stockDetail, fragment)
                        .commit();

                // Being here means we are in animation mode
                supportPostponeEnterTransition();
            }
        }

    }


    @Override
    public void stockSelected(String stockSymbol) {
        if (mTwoSides) {
            StockDetailFragment details = (StockDetailFragment) getSupportFragmentManager().findFragmentById(R.id.stockDetail);
            if (details == null || !stockSymbol.equals(details.getStockSymbol())) {
                // FIXME HIGHLIGHT SELECTION
                // FIXME KEEP SELECTION THROUGH ROTATION
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
            Timber.d("Symbol clicked: %s", stockSymbol);
            Intent detail = new Intent(this, StockDetail.class);
            detail.putExtra(STOCK_SYMBOL, stockSymbol);

            startActivity(detail);
        }
    }
}
