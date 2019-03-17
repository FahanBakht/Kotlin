package example.farhan.com.moviepocket.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import example.farhan.com.moviepocket.R;
import example.farhan.com.moviepocket.model.Cast;
import example.farhan.com.moviepocket.utils.Constants;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastAdapterViewHolder> {

    private Context context;
    private ArrayList<Cast> castArrayList;
    private CastAdapterOnClickHandler mClickHandler;
    private int lastPosition = -1;

    public interface CastAdapterOnClickHandler {
        void onClick(Cast cast);
    }

    public void setCastAdapterOnClickHandler(CastAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public CastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layout = R.layout.item_view_movie_cast;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new CastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CastAdapterViewHolder holder, int position) {
        final Cast cast = castArrayList.get(holder.getAdapterPosition());

        if (cast.getCharacter() != null) {
            holder.actorName.setText(cast.getCharacter());
        } else {
            holder.actorName.setText(R.string.unavailable);
        }

        if (cast.getProfile_path() != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(Constants.IMAGE_URL_CAST + cast.getProfile_path())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().error(R.drawable.error_blank_image))
                    .into(holder.catImg);
        } else {
            Glide.with(context).load(R.drawable.error_blank_image).into(holder.catImg);
        }

        holder.catImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickHandler != null) {
                    mClickHandler.onClick(cast);
                }
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context,
                (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        if (null == castArrayList) return 0;
        return castArrayList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull CastAdapterViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    class CastAdapterViewHolder extends RecyclerView.ViewHolder {

        com.wang.avi.AVLoadingIndicatorView loadingIndicatorView;
        ImageView catImg;
        android.support.v7.widget.CardView rootView;
        TextView actorName;

        CastAdapterViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.cast_root_view);
            loadingIndicatorView = itemView.findViewById(R.id.cast_item_view_image_loading_indicator);
            catImg = itemView.findViewById(R.id.img_cast);
            actorName = itemView.findViewById(R.id.tv_cast_actor_name);
        }
    }

    public void setCastData(ArrayList<Cast> castArrayList) {
        this.castArrayList = castArrayList;
        notifyDataSetChanged();
    }
}


