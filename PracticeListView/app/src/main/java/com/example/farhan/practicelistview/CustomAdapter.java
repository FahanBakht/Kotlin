package com.example.farhan.practicelistview;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {

    private ArrayList<DataSource> mDataSoruce;

    public CustomAdapter(@NonNull Context context, ArrayList<DataSource> mDataSoruce ) {
        super(context,0,mDataSoruce );
        this.mDataSoruce = mDataSoruce;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;

        if(convertView == null){

            view = LayoutInflater.from(getContext()).inflate(R.layout.customlistview,parent,false);
        }else {
            view = convertView;
        }
        DataSource ds = mDataSoruce.get(position);

        ImageView imageView = (ImageView)  view.findViewById(R.id.mImage);
        TextView textView1 = (TextView)  view.findViewById(R.id.mTextView1);
        TextView textView2 = (TextView) view.findViewById(R.id.mTextView2);
        Button button =  view.findViewById(R.id.mbtn);

        imageView.setImageResource(ds.getImage());
        textView1.setText(String.valueOf(ds.getId()));
        textView2.setText(ds.getName());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Button Clicked: "+position, Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}
