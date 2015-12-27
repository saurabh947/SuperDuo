/**
 * Created by Saurabh.
 */

package barqsoft.footballscores.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ui.ScoresWidgetProvider;

public class WidgetUpdateService extends Service {
    private String[] mDate = {new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())};

    @Override
    public void onStart(Intent intent, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_layout);
            try {
                Cursor cursor = updateScores();
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        remoteViews.setTextViewText(R.id.team_1, cursor.getString(3));
                        remoteViews.setTextViewText(R.id.team_2, cursor.getString(4));
                        remoteViews.setTextViewText(R.id.time, cursor.getString(2));
                        remoteViews.setTextViewText(R.id.score, cursor.getString(6) + " - " + cursor.getString(7));
                        cursor.close();
                    } else {
                        remoteViews.setTextViewText(R.id.team_1, getString(R.string.widget_error_text_1));
                        remoteViews.setTextViewText(R.id.team_2, getString(R.string.widget_error_text_2));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent clickIntent = new Intent(this.getApplicationContext(), ScoresWidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();

        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Cursor updateScores() throws Exception {
        LoadDataTask loadDataTask = new LoadDataTask();
        Cursor cursor = loadDataTask.execute((Object) null).get();
        if (cursor != null && cursor.getCount() > 0) {
            return cursor;
        } else {
            Intent service_start = new Intent(getApplicationContext(), myFetchService.class);
            getApplicationContext().startService(service_start);
            return loadDataTask.execute((Object) null).get();
        }
    }

    private class LoadDataTask extends AsyncTask<Object, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Object[] params) {
            return getApplicationContext().getContentResolver()
                    .query(DatabaseContract.scores_table.buildScoreWithDate(), null, null, mDate, null);
        }
    }
}
