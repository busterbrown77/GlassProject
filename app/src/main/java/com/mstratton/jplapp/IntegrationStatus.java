package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

public class IntegrationStatus extends Activity {
    int cardIndex;
    String partID;
    String checklistID;
    private ArrayList<View> cardList;
    CardScrollView csvCardsView;
    DatabaseHelper mDatabaseHelper;
    String[] status = new String[]{"Mated", "Demated", "Torqued"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            partID = extras.getString("KEY");
            checklistID = extras.getString("KEY2");
        }

        // Fill Array with saved part IDs from past scans...
        ArrayList<String> recentParts = new ArrayList<String>();
        mDatabaseHelper = new DatabaseHelper(this);

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell has the info for each step.
        cardList = new ArrayList<View>();
        for (int i = 0; i < status.length; i++) {
            View tempView = new CardBuilder(this, CardBuilder.Layout.MENU)
                    .setText(status[i])
                    .getView();
            cardList.add(tempView);
        }

        csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        final Part updated = new Part("temp");
        csvCardsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //save the card index that was selected
                cardIndex = position;

                DatabaseHelper.PartCursor dataCursor;
                dataCursor = mDatabaseHelper.queryPart(partID);
                dataCursor.moveToFirst();
                Part temp = dataCursor.getPart();

                while(!dataCursor.isAfterLast()){
                    temp = dataCursor.getPart();
                    dataCursor.moveToNext();
                }

                temp.setIntegrationStatus(status[position]);
                mDatabaseHelper.updatePart(temp);
                openPartInfo();
            }
        });

        // Start Contextual Voice Commands
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        // Show cardview after setup
        setContentView(csvCardsView);
    }

    private class csaAdapter extends CardScrollAdapter {

        @Override
        public int getCount() {
            return cardList.size();
        }

        @Override
        public Object getItem(int position) {
            return cardList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return cardList.get(position);
        }

        @Override
        public int getPosition(Object o) {
            return 0;
        }

    }

    // For Contextual Voice Commands --------------------------------------------------------

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.checklist, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checklist, menu);
        return true;
    }

    public void openPartInfo () {
        // Define Part Info Class
        Intent myIntent = new Intent(IntegrationStatus.this, PartInfo.class);
        // Attach the part info from Part View.
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra("KEY", partID);
        // Start the Part View class
        startActivity(myIntent);
    }

}

