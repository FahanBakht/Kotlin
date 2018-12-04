package com.panacloud.demotoolbar;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    ImageView imageShow;
    ImageView imageArrow;
    LinearLayout show;
    LinearLayout search_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();


        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void initView() {
        imageShow = findViewById(R.id.imageShow);
        imageArrow = findViewById(R.id.imageArrow);
        show = findViewById(R.id.show);
        search_layout = findViewById(R.id.search_layout);
        LayoutTransition lt = search_layout.getLayoutTransition();
        lt.setDuration(650);
        lt.setStartDelay(LayoutTransition.APPEARING, 0);
        lt.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        lt.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        lt.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        imageShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (show.getVisibility() == View.GONE) {
                    show.setVisibility(View.VISIBLE);
                    imageArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));


                } else {

                    show.setVisibility(View.GONE);
                    imageArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));

                }
            }
        });
    }
}
