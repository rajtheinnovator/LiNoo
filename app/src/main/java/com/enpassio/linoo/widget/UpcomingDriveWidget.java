package com.enpassio.linoo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.enpassio.linoo.R;
import com.enpassio.linoo.activities.HiringListActivity;
import com.enpassio.linoo.data.DriveContract;

/**
 * Implementation of App Widget functionality.
 */
public class UpcomingDriveWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upcoming_drive);

        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, HiringListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        Cursor cursor = context.getContentResolver().query(DriveContract.DriveEntry.CONTENT_URI, null, null, null, null);
        cursor.moveToLast();
        String driveDate = cursor.getString(cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_DRIVE_DATE));
        String companyName = cursor.getString(cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME));
        String jobProfile = cursor.getString(cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_JOB_POSITION));
        views.setTextViewText(R.id.drive_date, driveDate);
        views.setTextViewText(R.id.company_name, companyName);
        views.setTextViewText(R.id.job_profile, jobProfile);
        cursor.close();

        // Instruct the widget manager to update the widget
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

