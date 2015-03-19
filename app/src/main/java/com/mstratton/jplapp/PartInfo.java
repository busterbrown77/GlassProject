package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.Arrays;

public class PartInfo extends Activity {
    int cardIndex;
    String partID;
    private ArrayList<View> cardList;
    private ArrayList<String> headInfo;
    private ArrayList<String> subInfo;
    CardScrollView csvCardsView;
    DatabaseHelper database = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            partID = extras.getString("KEY");
        }

        // Get info from Database
        

//        if (database.queryPart() == null) {
//            Toast.makeText(PartInfo.this, "Part Not Found.", Toast.LENGTH_SHORT).show();
//        } else {
//            // Stuff
//        }

        // Fill Array with information about part.
        headInfo =  new ArrayList<String>(Arrays.asList("Part Detected!", "Video", "Checklists", "Logged History", "Specifications"));
        subInfo = new ArrayList<String>(Arrays.asList("Detected a " + partID + " part. \n \n ",
                                                      "Video will be Here, can be pinned to main screen.",
                                                      "Checklists and Tutorials for Installation / Disassembly will be here, can be pinned to main screen.",
                                                      "Any logged maintenance or issues will sppear here.",
                                                      "Various Specifications will be here."));

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell is a type of info.
        // 0 = Detail, 1 = Video, 2 = Checklists, 3 = History, 4 = Specs
        cardList = new ArrayList<View>();

        // Add photo and add video cards to left
        View addvideoCard = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Add Video")
                .setFootnote("Add a video for this part.")
                .getView();
        cardList.add(addvideoCard);

        View addphotoCard = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Add Photo")
                .setFootnote("Add a photo for this part.")
                .getView();
        cardList.add(addphotoCard);

        for (int i = 0; i < headInfo.size(); i++) {

            // Different layouts for certain info cards
            if (i < 1) {
                View tempCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                        .setText(subInfo.get(i))
                        .setFootnote(headInfo.get(i))
                        //.addImage(R.drawable.intake)
                        .getView();
                cardList.add(tempCard);
            } else {
                View tempCard = new CardBuilder(this, CardBuilder.Layout.TEXT)
                        .setText(headInfo.get(i))
                        .setFootnote(subInfo.get(i))
                        //.addImage(R.drawable.intake)
                        .getView();
                cardList.add(tempCard);
            }

        }

        csvCardsView = new CardScrollView(this);
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
                    openChecklistView();
                }
            }
        });

        // Start Contextual Voice Commands
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        // Start at the part info screen, not video or photo cards.
        csvCardsView.setSelection(2);

        // Show cardview after setup
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

    public void openChecklistView () {

        // Define CheckList View Class
        Intent myIntent = new Intent(PartInfo.this, CheckList.class);
        // Attach the part info from Part View.
        myIntent.putExtra("KEY", partID);
        // Start the CheckList View class
        startActivity(myIntent);
    }

    // For Contextual Voice Commands --------------------------------------------------------

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.partview, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.partview, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.menu_detail:
                    csvCardsView.setSelection(2);

                    break;
                case R.id.menu_video:
                    csvCardsView.setSelection(3);

                    break;
                case R.id.menu_checklist:
                    csvCardsView.setSelection(4);

                    // Must start Checklists class automatically, since no way
                    // to "select" a card using voice.
                    openChecklistView();

                    break;
                case R.id.menu_history:
                    csvCardsView.setSelection(5);

                    break;
                case R.id.menu_specs:
                    csvCardsView.setSelection(6);

                    break;
                case R.id.menu_back:
                    finish();

                    break;
                default:
                    return true;
            }
            return true;
        }
        // Good practice to pass through to super if not handled
        return super.onMenuItemSelected(featureId, item);
    }

}
