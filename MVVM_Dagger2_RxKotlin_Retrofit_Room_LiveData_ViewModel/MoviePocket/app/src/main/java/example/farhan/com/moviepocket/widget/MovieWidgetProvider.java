package example.farhan.com.moviepocket.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import example.farhan.com.moviepocket.DetailActivity;
import example.farhan.com.moviepocket.MainActivity;
import example.farhan.com.moviepocket.R;

/**
 * Implementation of App Widget functionality.
 */
// Widget Parent class where all widget Related Operations are done Like Updating and change behaviour on Size
public class MovieWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "MovieWidgetProvider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;
        if (width < 200) {
            rv = getMovieSingleItemRemoteView(context);
        } else {
            rv = getFavoriteMovieListRemoteView(context);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    public static RemoteViews getMovieSingleItemRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_img_logo, pendingIntent);
        return views;
    }

    public static RemoteViews getFavoriteMovieListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.movie_widget_provider);

        // Set the ListWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        // Set the DetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, DetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

        // Handle empty gardens
        views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);
        return views;
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    // Runs every time when Widget Dimensions changes, So we need to call Our service to update accordingly.
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        UpdaterWidgetService.startActionGetAllFavoriteFromDb(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
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

