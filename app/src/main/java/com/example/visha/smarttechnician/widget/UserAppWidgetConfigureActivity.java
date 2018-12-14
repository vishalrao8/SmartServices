package com.example.visha.smarttechnician.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.visha.smarttechnician.R;
import com.example.visha.smarttechnician.service.UserAppWidgetService;
import com.example.visha.smarttechnician.ui.MainActivity;
import com.example.visha.smarttechnician.ui.user.HomeActivity;

/**
 * The configuration screen for the {@link UserAppWidget UserAppWidget} AppWidget.
 */
public class UserAppWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        // It is the responsibility of the configuration activity to update the app widget
        if (com.example.visha.smarttechnician.utils.SharedPreferences.isUserLoggedIn(this)) {

            // Make sure we pass back the original appWidgetId
            updateWidget();
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
            
        } else {

            Toast.makeText(this, getString(R.string.widget_user_logged_out_message), Toast.LENGTH_SHORT).show();
            Intent intentToMainActivity = new Intent(this, MainActivity.class);
            startActivity(intentToMainActivity);
            finish();

        }
    }
    
    public void updateWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Intent intentToRemoteAdapter = new Intent(this, UserAppWidgetService.class);
        intentToRemoteAdapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intentToRemoteAdapter.setData(Uri.parse(intentToRemoteAdapter.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.user_app_widget);

        views.setRemoteAdapter(R.id.listview_widget_services, intentToRemoteAdapter);

        Intent intentToApp = new Intent(this, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentToApp,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.listview_widget_services, pendingIntent);

        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        
    }
}

