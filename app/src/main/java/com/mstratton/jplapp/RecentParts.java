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

public class RecentParts extends Activity {
    String partID;
    private ArrayList<View> cardList;
    ArrayList<String> headInfo;
    CardScrollView csvCardsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fill Array with saved part IDs from past scans...
        // Stuff..
        headInfo =  new ArrayList<String>(Arrays.asList("Intake Filter", "Oil Filter", "Spark Plug"));

        // Create cards using information.
        // Cycle through the head and sub info arrays, each cell is a type of info.
        // 0 = Picture, 1 = Video, 2 = Specs, 3 = Specs, 4 = Specs
        cardList = new ArrayList<View>();
        for (int i = 0; i < headInfo.size(); i++) {

            View tempView = new CardBuilder(this, CardBuilder.Layout.CAPTION)
                    .setText(headInfo.get(i))
                    .setFootnote("Recently Scanned")
                    .setTimestamp("Today")
                    .getView();
            cardList.add(tempView);
        }

        csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Open partview for selected part
                openPartView();
            }
        });

        // Start Contextual Voice Commands
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

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

    public void openPartView () {
        // Get selected card info
        partID = headInfo.get(csvCardsView.getSelectedItemPosition());

        // Define Part View Class
        Intent myIntent = new Intent(RecentParts.this, PartInfo.class);
        // Attach the part info from viewfinder.
        myIntent.putExtra("KEY", partID);
        // Start the Part View class
        startActivity(myIntent);
    }

    // For Contextual Voice Commands --------------------------------------------------------

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.recentparts
                    , menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recentparts, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.menu_select:
                    openPartView();

                    break;
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
