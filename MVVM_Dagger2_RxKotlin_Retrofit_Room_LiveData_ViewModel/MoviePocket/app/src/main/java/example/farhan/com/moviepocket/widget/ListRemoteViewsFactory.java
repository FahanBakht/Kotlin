package example.farhan.com.moviepocket.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;

import java.util.List;

import example.farhan.com.moviepocket.R;
import example.farhan.com.moviepocket.architecture.components.set.up.AppDatabase;
import example.farhan.com.moviepocket.model.Favorite;
import example.farhan.com.moviepocket.utils.Utils;

import static example.farhan.com.moviepocket.utils.Constants.FAVORITE;
import static example.farhan.com.moviepocket.utils.Constants.FAVORITE_OBJECT;
import static example.farhan.com.moviepocket.utils.Constants.FROM;
import static example.farhan.com.moviepocket.utils.Constants.MOVIE_ID;
import static example.farhan.com.moviepocket.utils.Constants.MOVIE_NAME;

//Widget List view Adapter like
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Favorite> mFavoriteList;

    public ListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        mFavoriteList = AppDatabase.getInstance(mContext).favoriteDao().getAllFavoriteForWidget();
    }

    @Override
    public void onDestroy() {
        if (mFavoriteList != null)
            mFavoriteList.clear();
    }

    @Override
    public int getCount() {
        if (mFavoriteList == null) return 0;
        return mFavoriteList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Favorite favorite = mFavoriteList.get(position);

        int movieId = favorite.getId();
        Bitmap poster = Utils.getInstance().convertBase64ToBitmap(favorite.getImage());
        String overview = favorite.getOverview();
        String rating = favorite.getVoteAverage();

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_view);

        views.setImageViewBitmap(R.id.widget_list_item_view_movie_poster, poster);
        views.setTextViewText(R.id.widget_tv_overview, overview);
        views.setTextViewText(R.id.widget_tv_rating, rating + mContext.getString(R.string.widget_rating_10));


        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        Gson gson = new Gson();
        String favoriteString = gson.toJson(favorite);

        extras.putString(FROM, FAVORITE);
        extras.putLong(MOVIE_ID, Long.valueOf(movieId));
        extras.putString(FAVORITE_OBJECT, favoriteString);
        extras.putString(MOVIE_NAME, favorite.getTitle());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_list_view_root, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    // Treat all items in the GridView the same
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}


