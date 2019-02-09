package com.unitedcreation.visha.smartservices.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.service.UserAppWidgetService;
import com.unitedcreation.visha.smartservices.ui.user.HomeActivity;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link UserAppWidgetConfigureActivity UserAppWidgetConfigureActivity}
 */
public class UserAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);
        Intent intentToRemoteAdapter = new Intent(context, UserAppWidgetService.class);
        intentToRemoteAdapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intentToRemoteAdapter.setData(Uri.parse(intentToRemoteAdapter.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.user_app_widget);

        views.setRemoteAdapter(R.id.listview_widget_services, intentToRemoteAdapter);

        Intent intentToApp = new Intent(context, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToApp,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.listview_widget_services, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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
}

