package com.example.farhan.practicelistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<DataSource> mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataSource = new ArrayList<>();
        mDataSource.add(new DataSource(0,"Naruto",R.drawable.naruto));
        mDataSource.add(new DataSource(1,"Sasuke",R.drawable.sasuke));
        mDataSource.add(new DataSource(2,"Goku",R.drawable.goku));
        mDataSource.add(new DataSource(3,"Vegeta",R.drawable.vegeta));

        ListView listView = (ListView) findViewById(R.id.mlistview);
        CustomAdapter customAdapter = new CustomAdapter(this,mDataSource);
        listView.setAdapter(customAdapter);


    }

}
