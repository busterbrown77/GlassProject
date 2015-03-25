package com.mstratton.jplapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

public class CheckList extends Activity {
    int cardIndex;
    String partID;
    String checklistID;
    private ArrayList<View> cardList;
    private ArrayList<String> checklistrow;
    private ArrayList<String> steps;
    CardScrollView csvCardsView;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            partID = extras.getString("KEY");
            checklistID = extras.getString("KEY2");
        }

        // Get Info from database using the code from QR Scanner.

        // Fill Array with saved part IDs from past scans...
        ArrayList<String> recentParts = new ArrayList<String>();
        mDatabaseHelper = new DatabaseHelper(this);

        DatabaseHelper.PartCursor dataCursor;
        dataCursor = mDatabaseHelper.queryChecklists(partID);
        dataCursor.moveToFirst();

        Part temp = new Part("temp");
        checklistrow = new ArrayList<String>();
        while(!dataCursor.isAfterLast()){
            temp = dataCursor.getChecklist();
            if(temp.getChecklistID().equals(checklistID)) {
                checklistrow.add(temp.getChecklistTask());
            }
            dataCursor.moveToNext();
        }

        dataCursor = mDatabaseHelper.queryPart(partID);
        steps = new ArrayList<String>();
        for (int i = 0; i < checklistrow.size(); i++) {
            steps.add(checklistrow.get(i));

        }

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell has the info for each step.
        cardList = new ArrayList<View>();
        for (int i = 0; i < steps.size(); i++) {

            View tempView = new CardBuilder(this, CardBuilder.Layout.MENU)
                    .setText(steps.get(i))
                    .getView();
            cardList.add(tempView);
        }

        csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //save the card index that was selected
                cardIndex = position;
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
            getMenuInflater().inflate(R.menu.checklist
                    , menu);
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

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.menu_next:
                    csvCardsView.setSelection(csvCardsView.getSelectedItemPosition() + 1);

                    break;
                case R.id.menu_prev:
                    csvCardsView.setSelection(csvCardsView.getSelectedItemPosition() - 1);

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

