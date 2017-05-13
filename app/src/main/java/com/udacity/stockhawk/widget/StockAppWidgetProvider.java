package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.StockProvider;
import com.udacity.stockhawk.ui.MainActivity;

import java.util.Arrays;


public class StockAppWidgetProvider extends AppWidgetProvider {

    public static final String LOG_TAG = StockAppWidgetProvider.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(LOG_TAG, "onReceive " + action);
        //FIXME CATCHER L'EVENT
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Log.d(LOG_TAG, "onReceive EXTRAS");
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    Log.d(LOG_TAG, "onReceive Ids " + Arrays.toString(appWidgetIds));
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        } else if (StockProvider.ACTION_DATA_UPDATED.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            Log.d(LOG_TAG, "onReceive DATA_UPDATED " + Arrays.toString(appWidgetIds));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetStockList);
            Log.d(LOG_TAG, "notifyAppWidgetViewDataChanged DONE" + appWidgetManager.getInstalledProviders().size());
        } else super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(LOG_TAG, "onUpdate " + appWidgetIds.length);
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Sets up the intent that points to the StackViewService that will
            // provide the views for this collection.
            Intent intent = new Intent(context, StockAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            rv.setRemoteAdapter(R.id.widgetStockList, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.widgetStockList, R.id.widget_empty_view);

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            Intent activityIntent = new Intent(context, MainActivity.class);

            activityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);


            PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
            rv.setPendingIntentTemplate(R.id.widgetStockList, activityPendingIntent);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            Log.i(LOG_TAG, "ID " + appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
