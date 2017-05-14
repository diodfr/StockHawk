package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.common.collect.Lists;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utils.FormatterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = StockDetailFragment.class.getName();

    private static final int STOCK_LOADER_DETAIL = 100;

    @BindView(R.id.progressBarDetail)
    ProgressBar loadingPB;
    @BindView(R.id.name)
    TextView nameTV;
    @BindView(R.id.stockExchange)
    TextView stockExchangeTV;
    @BindView(R.id.changeDetail)
    TextView changeTV;
    @BindView(R.id.percentChangeDetail)
    TextView percentChangeTV;

    @BindView(R.id.price)
    TextView priceTV;

    @BindView(R.id.currency)
    TextView currencyTV;

    @BindView(R.id.ask)
    TextView askTV;

    @BindView(R.id.bid)
    TextView bidTV;

    @BindView(R.id.eps)
    TextView epsTV;

    @BindView(R.id.pe)
    TextView peTV;

    @BindView(R.id.peg)
    TextView pegTV;

    @BindView(R.id.historyChart)
    LineChart historyLC;

    @BindView(R.id.error)
    TextView errorTV;

    private String stockSymbol;

    public StockDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            stockSymbol = arguments.getString(MainActivity.STOCK_SYMBOL);
        }

        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);
        ButterKnife.bind(this, view);

        historyLC.setVisibility(View.INVISIBLE);
        loadingPB.setVisibility(View.VISIBLE);
        Log.d(TAG, "Loading detail : VISIBLE");

        if(stockSymbol != null)
            getActivity().getSupportLoaderManager().restartLoader(STOCK_LOADER_DETAIL, null, this);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "loader creation");
        return new CursorLoader(getActivity(), Contract.Quote.makeUriForStock(stockSymbol), null, null,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            Log.d(TAG, "LoadFinished");
            if (data.moveToNext()) {
                bindData(data);
                historyLC.setVisibility(View.VISIBLE);
                loadingPB.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Loading detail : INVISIBLE");
            }
            //FIXME check if there is data
        } finally {
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // error message
        errorTV.setVisibility(View.VISIBLE);
        if (getActivity() != null)
            errorTV.setText(getResources().getString(R.string.error_loading));
    }

    private void bindData(Cursor data) {
        //FIXME la rotation ne fonctionne pas

        int nameColIdx = data.getColumnIndex(Contract.Quote.COLUMN_NAME);
        int stockExchangeColIdx = data.getColumnIndex(Contract.Quote.COLUMN_STOCK_EXCHANGE);
        int priceColIdx = data.getColumnIndex(Contract.Quote.COLUMN_PRICE);
        int currencyColIdx = data.getColumnIndex(Contract.Quote.COLUMN_CURRENCY);
        int changeColIdx = data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);
        int percentChangeColIdx = data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
        int askColIdx = data.getColumnIndex(Contract.Quote.COLUMN_ASK);
        int bidColIdx = data.getColumnIndex(Contract.Quote.COLUMN_BID);
        int epsColIdx = data.getColumnIndex(Contract.Quote.COLUMN_EPS);
        int peColIdx = data.getColumnIndex(Contract.Quote.COLUMN_PE);
        int pegColIdx = data.getColumnIndex(Contract.Quote.COLUMN_PEG);
        int historyColIdx = data.getColumnIndex(Contract.Quote.COLUMN_HISTORY);

        nameTV.setText(data.getString(nameColIdx));
        stockExchangeTV.setText(data.getString(stockExchangeColIdx));
        priceTV.setText(FormatterUtils.DECIMAL_FORMAT.format(data.getDouble(priceColIdx)));
        currencyTV.setText(data.getString(currencyColIdx));

        double change = data.getDouble(changeColIdx);
        boolean gain = change > 0;
        int backgroudColor = gain ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red;

        changeTV.setBackgroundResource(backgroudColor);
        changeTV.setText(FormatterUtils.DECIMAL_FORMAT_WITHPLUS.format(change));
        percentChangeTV.setBackgroundResource(backgroudColor);
        percentChangeTV.setText(FormatterUtils.PERCENTAGE_FORMAT.format(data.getDouble(percentChangeColIdx)));

        askTV.setText(String.valueOf(data.getDouble(askColIdx)));
        bidTV.setText(String.valueOf(data.getDouble(bidColIdx)));
        epsTV.setText(String.valueOf(data.getDouble(epsColIdx)));
        peTV.setText(String.valueOf(data.getDouble(peColIdx)));
        pegTV.setText(String.valueOf(data.getDouble(pegColIdx)));

        updateHistory(data.getString(historyColIdx));
    }

    private void updateHistory(String formattedHistory) {
        Scanner historyScanner = new Scanner(formattedHistory);
        historyScanner.useDelimiter("[,\n]");

        List<Entry> historicalValues = new ArrayList<>();

        float min=Float.MAX_VALUE;
        float max=Float.MIN_VALUE;
        while (historyScanner.hasNext()) {
            long date = TimeUnit.DAYS.toDays(historyScanner.nextLong());
            float dateFloat = date/100000;
            float price = Float.parseFloat(historyScanner.next());

            min = Math.min(min, dateFloat);
            max = Math.max(max, dateFloat);
            historicalValues.add(new Entry(dateFloat, price));
            Log.d("GRAPH", date + "#" + dateFloat + "#" + price);
        }

        Log.d("GRAPH", min + "/" + max);
        historicalValues = Lists.reverse(historicalValues);
        LineDataSet dataSet = new LineDataSet(historicalValues, "History");
        dataSet.setLineWidth(1);
        dataSet.setCircleColor(Color.BLUE);

        LineData data = new LineData(dataSet);
        historyLC.setData(data);
        XAxis xAxis = historyLC.getXAxis();
        int axisColor = getResources().getColor(R.color.colorPrimary);
        xAxis.setTextColor(axisColor);
        xAxis.setAxisLineColor(axisColor);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            java.text.DateFormat dateFormatter = DateFormat.getDateFormat(getActivity());
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dateFormatter.format((long)value * 100000);
            }
        });

        YAxis leftAxis = historyLC.getAxisLeft();
        YAxis rightAxis = historyLC.getAxisRight();
        leftAxis.setTextColor(axisColor);
        leftAxis.setAxisLineColor(axisColor);
        rightAxis.setTextColor(axisColor);
        rightAxis.setAxisLineColor(axisColor);

        historyLC.getLegend().setEnabled(false);
        historyLC.invalidate();
    }
}
