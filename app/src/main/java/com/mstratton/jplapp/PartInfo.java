package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

    DatabaseHelper database;
    Cursor stupid;
    Part scanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            partID = extras.getString("KEY");
        }

        // Get info from Database
        database = new DatabaseHelper(this);
        stupid = database.queryPart(partID);

        // Test Part
        Part scannedPart = new Part("test");//"2110w076");

        // Fill Array with information about part.
        headInfo =  new ArrayList<String>(Arrays.asList("Part Detected!", "Video", "Checklists", "Logged History", "Specifications"));


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

        View detectCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("Found a " + scannedPart.getPartName() + " (" + stupid.getString(stupid.getColumnIndex("_id")).toString() + ") part.")
                .setFootnote("Part Found!")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(detectCard);

        View specificationCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText(scannedPart.getPartSpecs())
                .setFootnote("Part Specifications")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(specificationCard);

        View checklistsCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("Broken")//scannedPart.getChecklist())
                .setFootnote("Part Checklists")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(checklistsCard);

        // For loop for photos
        View photosCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                //Image photo = part.getphoto;

                .setText("Photo1") //photo.getName())
                .setFootnote("Part Photos")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(photosCard);

        // For loop for videos
        View videosCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("Broken")//scannedPart.getChecklist())
                .setFootnote("Part Videos")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(videosCard);

        View historyCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("Simply not implemented")
                .setFootnote("Part History")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(historyCard);

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
