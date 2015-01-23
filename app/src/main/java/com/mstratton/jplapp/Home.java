package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import java.util.List;

public class Home extends Activity {
    int cardIndex;
    private ArrayList<View> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardList = new ArrayList<View>();

        // Add menu cards
        View cardSettings = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Voice Search")
                .getView();
        cardList.add(cardSettings);

        View cardViewfinder = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Scan a Part")
                .getView();
        cardList.add(cardViewfinder);

        View cardRecent = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Recent Parts")
                .getView();
        cardList.add(cardRecent);

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
                    // settings
                    displaySpeechRecognizer();
                } else if (position == 1) {
                    // viewfinder
                    Intent intent = new Intent(Home.this, ViewFinder.class);
                    startActivity(intent);
                } else if (position == 2) {
                    // recent parts
                    Intent intent = new Intent(Home.this, RecentParts.class);
                    startActivity(intent);
                }
            }
        });

        // Start Contextual Voice Commands
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        // Start at the part info screen, not video or photo cards.
        csvCardsView.setSelection(1);
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

    // For Voice Input ----------------------------------------------------------------------

    private static final int SPEECH_REQUEST = 0;

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            // Define Part View Class
            Intent myIntent = new Intent(Home.this, PartInfo.class);
            // Attach the part info from viewfinder.
            myIntent.putExtra("KEY", spokenText);
            // Start the Part View class
            this.startActivity(myIntent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // For Contextual Voice Commands --------------------------------------------------------

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.home, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.menu_viewfinder:
                    // Open Viewfinder
                    Intent vintent = new Intent(Home.this, ViewFinder.class);
                    startActivity(vintent);
                    break;
                case R.id.menu_recentparts:
                    // Open Viewfinder
                    Intent rintent = new Intent(Home.this, RecentParts.class);
                    startActivity(rintent);
                    break;
                case R.id.menu_manual:
                    // Open Viewfinder
                    displaySpeechRecognizer();
                    break;
                case R.id.menu_exit:
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
