package com.mstratton.qr;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
 
public class PartView extends Activity {
    int cardIndex;
    String partID;
    private ArrayList<View> cardList;
    private ArrayList<String> headInfo;
    private ArrayList<String> subInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            partID = extras.getString("KEY");
        }

        // Get Info from database using the code form QR Scanner.
        // Stuff

        // Fill Array with information about part.
        headInfo =  new ArrayList<String>(Arrays.asList("Part Detected!", "Video", "Tutorials", "Logged History", "Specifications"));
        subInfo = new ArrayList<String>(Arrays.asList("Detected a " + partID + " part. Also known as a " + partID,
                                                      "Video will be Here, can be pinned to main screen.",
                                                      "Checklists and Tutorials for Installation / Disassembly will be here, can be pinned to main screen.",
                                                      "Any logged maintenance or issues will sppear here.",
                                                      "Various Specifications will be here."));

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell is a type of info.
        // 0 = Picture, 1 = Video, 2 = Specs, 3 = Specs, 4 = Specs
        cardList = new ArrayList<View>();
        for (int i = 0; i < headInfo.size(); i++)
        {
            // Different Layouts for Certain Cards
            if (i < 1) {
                View tempView = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                        .setText(subInfo.get(i))
                        .setFootnote(headInfo.get(i))
                        .addImage(R.drawable.screw)
                        .addImage(R.drawable.screw2)
                        .addImage(R.drawable.screwcollection)
                        .getView();
                cardList.add(tempView);
            } else {
                View tempView = new CardBuilder(this, CardBuilder.Layout.TEXT_FIXED)
                        .setText(headInfo.get(i))
                        .setFootnote(subInfo.get(i))
                        .addImage(R.drawable.screw)
                        .addImage(R.drawable.screw2)
                        .addImage(R.drawable.screwcollection)
                        .getView();
                cardList.add(tempView);
            }

        }

        CardScrollView csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //save the card index that was selected
                cardIndex = position;

                if (position == 2) {
                    // new cardscrollview
                    openChecklist();
                }

                Toast.makeText(getApplicationContext(), "Clicked on " + headInfo.get(position) + " Card", Toast.LENGTH_SHORT).show();
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

    public void openChecklist () {
        ArrayList<View> stepList;
        final ArrayList<String> checklistInfo =  new ArrayList<String>(Arrays.asList("Step 1", "Step 2", "Step 3", "Step 4", "Step 5"));
        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell is a type of info.
        // 0 = Picture, 1 = Video, 2 = Specs, 3 = Specs, 4 = Specs
        stepList = new ArrayList<View>();
        for (int i = 0; i < checklistInfo.size(); i++) {

            View tempView = new CardBuilder(this, CardBuilder.Layout.TEXT_FIXED)
                    .setText(checklistInfo.get(i))
                    .addImage(R.drawable.screw)
                    .addImage(R.drawable.screw2)
                    .addImage(R.drawable.screwcollection)
                    .getView();
            stepList.add(tempView);

        }

        CardScrollView csvCardsView2 = new CardScrollView(this);
        csaAdapter cvAdapter2 = new csaAdapter();
        csvCardsView2.setAdapter(cvAdapter2);
        csvCardsView2.activate();
        csvCardsView2.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //save the card index that was selected
                cardIndex = position;

                Toast.makeText(getApplicationContext(), "Clicked on " + checklistInfo.get(position) + " Card", Toast.LENGTH_SHORT).show();
            }
        });

        setContentView(csvCardsView2);
    }

}
