package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.Arrays;

public class PartView extends Activity {
    int cardIndex;
    String partID;
    private ArrayList<View> cardList;
    private ArrayList<String> headInfo;
    private ArrayList<String> subInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            partID = extras.getString("KEY");
        }

        // Get Info from database using the code from QR Scanner.
        // Stuff

        // Fill Array with information about part.
//        headInfo =  new ArrayList<String>(Arrays.asList("Part Detected!", "Video", "Tutorials", "Logged History", "Specifications"));
//        subInfo = new ArrayList<String>(Arrays.asList("Detected a " + partID + " part. Also known as a " + partID,
//                                                      "Video will be Here, can be pinned to main screen.",
//                                                      "Checklists and Tutorials for Installation / Disassembly will be here, can be pinned to main screen.",
//                                                      "Any logged maintenance or issues will sppear here.",
//                                                      "Various Specifications will be here."));
        headInfo =  new ArrayList<String>(Arrays.asList("Part Detected!", "Video", "Tutorial", "Logged History", "Specifications"));
        subInfo = new ArrayList<String>(Arrays.asList("Detected a intake filter (" + partID + ")",
                "Assembly Videos",
                "Replacement",
                "Last Accessed By: Peter",
                "Parts: 1 Screw Clamp,\n Torque: 8 in-lb "));

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell is a type of info.
        // 0 = Picture, 1 = Video, 2 = Specs, 3 = Specs, 4 = Specs
        cardList = new ArrayList<View>();

        // Add photo and add video cards to left
        View addvideoView = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Add Video")
                .setFootnote("Add a video for this part.")
                .getView();
        cardList.add(addvideoView);

        View addphotoView = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Add Photo")
                .setFootnote("Add a photo for this part.")
                .getView();
        cardList.add(addphotoView);

        for (int i = 0; i < headInfo.size(); i++) {

            // Different layouts for certain info cards
            if (i < 1) {
                View tempView = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                        .setText(subInfo.get(i))
                        .setFootnote(headInfo.get(i))
                        .addImage(R.drawable.intake)

                        .addImage(R.drawable.intake3)
                        //.addImage(R.drawable.screw)
                        //.addImage(R.drawable.screw2)
                        //.addImage(R.drawable.screwcollection)
                        .getView();
                cardList.add(tempView);
            } else {
                View tempView = new CardBuilder(this, CardBuilder.Layout.TEXT_FIXED)
                        .setText(headInfo.get(i))
                        .setFootnote(subInfo.get(i))
                        .addImage(R.drawable.intake)

                        .addImage(R.drawable.intake3)
                        //.addImage(R.drawable.screw)
                        //.addImage(R.drawable.screw2)
                        //.addImage(R.drawable.screwcollection)
                        .getView();
                cardList.add(tempView);
            }

        }

        final CardScrollView csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //save the card index that was selected
                cardIndex = position;

                if (position == 0) {
                    // video

                } else if (position == 1) {
                    // photo

                } else if (position == 4) {
                    // Start CheckList View
                    // Also sends the part ID to the activity, to look up data.

                    // Define CheckList View Class
                    Intent myIntent = new Intent(PartView.this, CheckListView.class);
                    // Attach the part info from Part View.
                    myIntent.putExtra("KEY", partID);
                    // Start the CheckList View class
                    PartView.this.startActivity(myIntent);
                }

                if (position >= 2) {
                    Toast.makeText(getApplicationContext(), "Clicked on " + headInfo.get(position - 2) + " Card", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Start at the part info screen, not video or photo cards.
        csvCardsView.setSelection(2);

        setContentView(csvCardsView);

    }

    private class csaAdapter extends CardScrollAdapter {
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
