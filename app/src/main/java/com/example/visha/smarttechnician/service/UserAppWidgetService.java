package com.example.visha.smarttechnician.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.visha.smarttechnician.R;

public class UserAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewFactory(this.getApplicationContext(), intent);
    }

    public class RemoteViewFactory implements RemoteViewsFactory {

        private static final String CATEGORY_POSITION = "category_position";

        private final int LENGTH = 5;
        private String[] serviceTitle;

        private final Context context;
        private final int appWidgetId;

        private RemoteViewFactory(Context applicationContext, Intent intent) {

            this.context = applicationContext;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            serviceTitle = context
                    .getResources()
                    .getStringArray(R.array.services_name);

        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return LENGTH;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            // Constructing a remote views item based on the app widget item XML file,
            // and setting the text based on the position.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_app_widget);
            views.setTextViewText(R.id.textview_widget_serviceTitle, serviceTitle[position]);

            // Setting a fill-intent, which will be used to fill in the pending intent template
            // that is set on the list view in RecipeWidgetProvider.

            Bundle extras = new Bundle();
            extras.putInt(CATEGORY_POSITION, position);

            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);

            views.setOnClickFillInIntent(R.id.textview_widget_serviceTitle, fillInIntent);

            // Returning the remote views object.
            return views;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
