package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.Arrays;

public class PartHistory extends Activity {
    String partID;
    private ArrayList<View> cardList;
    private ArrayList<String> headInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Fill Array with saved part IDs from past scans...
        // Stuff..
        headInfo =  new ArrayList<String>(Arrays.asList("Intake Filter", "Oil Filter", "Spark Plug"));

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell is a type of info.
        // 0 = Picture, 1 = Video, 2 = Specs, 3 = Specs, 4 = Specs
        cardList = new ArrayList<View>();
//        for (int i = 0; i < headInfo.size(); i++) {
//
//            View tempView = new CardBuilder(this, CardBuilder.Layout.CAPTION)
//                    .setText(headInfo.get(i))
//                    .setFootnote("Recently Scanned")
//                    .setTimestamp("Today")
//
//                    //.addImage(R.drawable.screw2)
//                    //.addImage(R.drawable.screwcollection)
//                    .getView();
//            cardList.add(tempView);
//
//        }

        View tempView = new CardBuilder(this, CardBuilder.Layout.CAPTION)
                .setText(headInfo.get(0))
                .setFootnote("Scanned by Peter")
                .setTimestamp("Today")

                .addImage(R.drawable.intake)
                .getView();
        cardList.add(tempView);

        View intakeView = new CardBuilder(this, CardBuilder.Layout.CAPTION)
                .setText(headInfo.get(1))
                .setFootnote("Scanned by Peter")
                .setTimestamp("Today")

                .addImage(R.drawable.oil_filter)
                .getView();
        cardList.add(intakeView);

        View sparkView = new CardBuilder(this, CardBuilder.Layout.CAPTION)
                .setText(headInfo.get(2))
                .setFootnote("Scanned by Mike")
                .setTimestamp("Today")

                .addImage(R.drawable.spark)
                .getView();
        cardList.add(sparkView);


        CardScrollView csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Get selected card info
                partID = headInfo.get(position);

                // Define Part View Class
                Intent myIntent = new Intent(PartHistory.this, PartView.class);
                // Attach the part info from viewfinder.
                myIntent.putExtra("KEY", partID);
                // Start the Part View class
                PartHistory.this.startActivity(myIntent);
            }
        });

        setContentView(csvCardsView);
    }

    private class csaAdapter extends CardScrollAdapter
    {
        @Override
        public int getCount()
        {
            return cardList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return cardList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return cardList.get(position);
        }

        @Override
        public int getPosition(Object o)
        {
            return 0;
        }
    }
}
