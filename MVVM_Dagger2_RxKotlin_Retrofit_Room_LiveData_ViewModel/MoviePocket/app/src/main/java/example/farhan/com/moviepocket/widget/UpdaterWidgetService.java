package example.farhan.com.moviepocket.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import example.farhan.com.moviepocket.R;

/**
 * Created by MuhammadFarhan on 15/10/2018.
 */
// Service which Update's widget list every time when there is some change in favorite database
public class UpdaterWidgetService extends IntentService {
    private static final String TAG = "UpdaterWidgetService";
    public static final String ACTION_GET_ALL_FAVORITE_FROM_DB = "action.get.all.favorite.from.db";

    public UpdaterWidgetService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        if (ACTION_GET_ALL_FAVORITE_FROM_DB.equals(action)) {
            handleActionGetAllFavoriteFromDb();
        }
    }

    public static void startActionGetAllFavoriteFromDb(Context context) {
        Intent intent = new Intent(context, UpdaterWidgetService.class);
        intent.setAction(ACTION_GET_ALL_FAVORITE_FROM_DB);
        context.startService(intent);
    }

    private void handleActionGetAllFavoriteFromDb() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MovieWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        //Now update all widgets
        MovieWidgetProvider.updatePlantWidgets(this, appWidgetManager, appWidgetIds);
    }

}
