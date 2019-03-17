package example.farhan.com.moviepocket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import example.farhan.com.moviepocket.R;
import example.farhan.com.moviepocket.model.Favorite;
import example.farhan.com.moviepocket.model.MovieShort;
import example.farhan.com.moviepocket.utils.Constants;
import example.farhan.com.moviepocket.utils.Utils;

import static example.farhan.com.moviepocket.utils.Constants.RUN_FOR_FAVORITE;
import static example.farhan.com.moviepocket.utils.Constants.RUN_FOR_MOVIE;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private Context context;
    private ArrayList<MovieShort> movieShortsArrayList;
    private ArrayList<Favorite> favoritesArrayList;
    private MoviesAdapterOnClickHandler mClickHandler;
    private MoviesAdapterOnClickHandlerForFavorite mClickHandleForFavorite;
    private String runFor;

    private int lastPosition = -1;

    public interface MoviesAdapterOnClickHandler {
        void onClick(MovieShort moviesShortObj);
    }

    public interface MoviesAdapterOnClickHandlerForFavorite {
        void onClick(Favorite favoriteObj);
    }

    public void setMoviesAdapterOnClickHandlerForFavorite(MoviesAdapterOnClickHandlerForFavorite mClickHandleForFavorite) {
        this.mClickHandleForFavorite = mClickHandleForFavorite;
    }

    public void setMoviesAdapterOnClickHandler(MoviesAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layout = R.layout.item_view_main_movie_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesAdapterViewHolder holder, int position) {
        if (runFor.equals(RUN_FOR_MOVIE)) {
            final MovieShort movies = movieShortsArrayList.get(holder.getAdapterPosition());
            holder.releaseDate.setText(Utils.getInstance().getYear(movies.getRelease_date()));
            holder.ratingBar.setRating(movies.getVote_average() / 2);
            if (movies.getPoster_path() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(Constants.IMAGE_URL + movies.getPoster_path())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().error(R.drawable.error_blank_image))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                holder.loadingIndicatorView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.loadingIndicatorView.setVisibility(View.GONE);
                                float ratio = (float) resource.getWidth() / resource.getHeight();
                                holder.moviePoster.setAspectRatio(ratio);
                                holder.moviePoster.setImageBitmap(resource);
                            }
                        });
            } else {
                holder.moviePoster.setImageDrawable(null);
                Glide.with(context).load(R.drawable.error_blank_image).into(holder.moviePoster);
            }
            holder.moviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickHandler != null) {
                        mClickHandler.onClick(movies);
                    }
                }
            });
        } else if (runFor.equals(RUN_FOR_FAVORITE)) {

            final Favorite favorite = favoritesArrayList.get(holder.getAdapterPosition());

            holder.releaseDate.setText(Utils.getInstance().getYear(favorite.getRelease_date()));
            holder.ratingBar.setRating(Float.parseFloat(favorite.getVoteAverage()) / 2);

            if (favorite.getImage() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(Utils.getInstance().convertBase64ToBitmap(favorite.getImage()))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().error(R.drawable.error_blank_image))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                holder.loadingIndicatorView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.loadingIndicatorView.setVisibility(View.GONE);
                                float ratio = (float) resource.getWidth() / resource.getHeight();
                                holder.moviePoster.setAspectRatio(ratio);
                                holder.moviePoster.setImageBitmap(resource);
                            }
                        });
            } else {
                holder.moviePoster.setImageDrawable(null);
                Glide.with(context).load(R.drawable.error_blank_image).into(holder.moviePoster);
            }

            holder.moviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickHandleForFavorite != null) {
                        mClickHandleForFavorite.onClick(favorite);
                    }
                }
            });

        }

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        if (runFor != null) {
            switch (runFor) {
                case RUN_FOR_FAVORITE:
                    return favoritesArrayList.size();

                case Constants.RUN_FOR_MOVIE:
                    return movieShortsArrayList.size();
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MoviesAdapterViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
        holder.movieContainer.clearAnimation();
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView releaseDate;
        example.farhan.com.moviepocket.utils.DynamicHeightImageView moviePoster;
        LinearLayout movieContainer;
        RatingBar ratingBar;
        com.wang.avi.AVLoadingIndicatorView loadingIndicatorView;

        MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            movieContainer = itemView.findViewById(R.id.short_movie_container);
            releaseDate = itemView.findViewById(R.id.movie_release_date);
            moviePoster = itemView.findViewById(R.id.thumbnail);
            ratingBar = itemView.findViewById(R.id.short_movie_rating);
            loadingIndicatorView = itemView.findViewById(R.id.item_view_image_loading_indicator);
        }
    }

    public void setFavoriteData(ArrayList<Favorite> favoritesArrayList, String runFor) {
        this.runFor = runFor;
        this.favoritesArrayList = favoritesArrayList;
        notifyDataSetChanged();
    }

    public void clearFavoriteData() {
        if (this.favoritesArrayList != null) {
            this.favoritesArrayList.clear();
            notifyDataSetChanged();
        }
    }

    public void setMovieData(ArrayList<MovieShort> moviesShortArrayList, String runFor) {
        this.runFor = runFor;
        this.movieShortsArrayList = moviesShortArrayList;
        notifyDataSetChanged();
    }

    public void appendMoviesData(ArrayList<MovieShort> moviesToAppend) {
        this.movieShortsArrayList.addAll(moviesToAppend);
        notifyDataSetChanged();
    }

    public void clearMoviesData() {
        if (this.movieShortsArrayList != null) {
            this.movieShortsArrayList.clear();
            notifyDataSetChanged();
        }
    }

}
