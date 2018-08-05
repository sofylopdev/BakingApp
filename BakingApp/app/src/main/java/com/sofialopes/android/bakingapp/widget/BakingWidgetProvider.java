package com.sofialopes.android.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.ui.MainActivity;

import timber.log.Timber;

import static com.sofialopes.android.bakingapp.utils.ConstantsClass.RECIPE_ID_PREFS;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.WIDGET_ID;


/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {


    public BakingWidgetProvider() {
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        views.setTextViewText(
                R.id.appwidget_title,
                BakingWidgetUtils.loadTitlePref(context, appWidgetId));
        views.setTextViewText(
                R.id.appwidget_ingredients,
                BakingWidgetUtils.loadIngredients(context, appWidgetId));

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(WIDGET_ID, appWidgetId);
        intent.putExtra(RECIPE_ID_PREFS, BakingWidgetUtils.loadId(context, appWidgetId));

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            Timber.d("Widget being deleted: " + appWidgetId);
            BakingWidgetUtils.removeWidgetFromSharedPreferences(appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}


