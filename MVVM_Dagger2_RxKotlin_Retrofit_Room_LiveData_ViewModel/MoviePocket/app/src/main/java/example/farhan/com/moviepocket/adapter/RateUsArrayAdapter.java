package example.farhan.com.moviepocket.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import example.farhan.com.moviepocket.R;
import example.farhan.com.moviepocket.model.Rating;

public class RateUsArrayAdapter extends ArrayAdapter<Rating> {

    private ArrayList<Rating> ratingArrayList;

    public RateUsArrayAdapter(@NonNull Context context, ArrayList<Rating> ratingArrayList) {
        super(context, 0, ratingArrayList);
        this.ratingArrayList = ratingArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rateus_listview_item_view, parent, false);
        }

        CardView cardView = convertView.findViewById(R.id.rateUsCardView);
        TextView userName = convertView.findViewById(R.id.txtRateUsUserName);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingInRateUs);
        TextView txtRating = convertView.findViewById(R.id.txtRateUsRating);

        int customBlack = Color.parseColor("#262626");
        cardView.setCardBackgroundColor(customBlack);

        int customGoldActive = Color.parseColor("#EDB842");
        int customGoldBack = Color.parseColor("#fffde7");

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(customGoldActive, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(customGoldActive, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(customGoldBack, PorterDuff.Mode.SRC_ATOP);

        Rating rating = ratingArrayList.get(position);

        userName.setText(rating.getName());
        ratingBar.setRating(rating.getRating());
        txtRating.setText("Rated: "+String.valueOf(rating.getRating() * 2)+"/10");

        return convertView;
    }

}
