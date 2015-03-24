package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PartInfo extends Activity {
    int cardIndex;
    String partID;
    String retrievedFrom;
    private ArrayList<View> cardList;
    private ArrayList<String> headInfo;
    private ArrayList<String> subInfo;
    CardScrollView csvCardsView;

    DatabaseHelper mDatabaseHelper;
    Part scannedPart;


    public LocationListener mLocationListener;
    public Criteria criteria;
    boolean updated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*

        String count = "";
        Toast.makeText(this, count, Toast.LENGTH_SHORT).show();
        test = new DatabaseHelper(this);
        Toast.makeText(this, count, Toast.LENGTH_SHORT).show();
        Part newPart = new Part("2119w076");
        newPart.setPartSpecs("Winner");
        test.updatePart(newPart);
        DatabaseHelper.PartCursor c;
        c = test.queryPart("2119w076");
        c.moveToFirst();
        if(!c.isAfterLast()) {
            count = c.getPart().getPartSpecs();
        }else{
            count = "Broken!";
        }
        Toast.makeText(this, count, Toast.LENGTH_SHORT).show();

        */

        // Get partID passed from the Viewfinder Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            partID = extras.getString("KEY");
            retrievedFrom = extras.getString("RETRIEVED_FROM");
        }

        Part newPart = new Part(partID);
        newPart = randomPart(partID);
        scannedPart = new Part(partID);
        scannedPart = randomPart(partID);
        mDatabaseHelper = new DatabaseHelper(this);
        if (!retrievedFrom.equals("recentparts")) {
            mDatabaseHelper.insertPart(scannedPart);
            updated = false;
        }
        //mDatabaseHelper.insertScanHistory(scannedPart);
        DatabaseHelper.PartCursor dataCursor;
        dataCursor = mDatabaseHelper.queryPart(partID);
        dataCursor.moveToFirst();
        if (!dataCursor.isAfterLast()) {
            scannedPart = dataCursor.getPart();
        }
        dataCursor.close();

        // Get Info from database using the code from QR Scanner.
        // Stuff

        // Fill Array with information about part.
        headInfo = new ArrayList<String>(Arrays.asList("Part Detected!", "Video", "Checklists", "Logged History", "Specifications"));
        subInfo = new ArrayList<String>(Arrays.asList("Detected a " + scannedPart.getPartID() + " part. \n \n ",
                "Video will be Here, can be pinned to main screen.",
                "Checklists and Tutorials for Installation / Disassembly will be here, can be pinned to main screen.",
                "Any logged maintenance or issues will appear here.",
                scannedPart.getPartSpecs()));

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        if(!updated) {
            LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider;
            provider = locationManager.getBestProvider(criteria, true);
            boolean isEnabled = locationManager.isProviderEnabled(provider);
            if (isEnabled) {
                // Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {


                    @Override
                    public void onLocationChanged(Location location) {
                        makeUseOfNewLocation(location);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }

                };

                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
            }
        }

    }

    public void makeUseOfNewLocation(Location location){
        if(!updated) {
            scannedPart.setLocationLat(location.getLatitude());
            scannedPart.setLocationLong(location.getLongitude());
            mDatabaseHelper.insertScanHistory(scannedPart);
        }
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
    public Part randomPart(String id){
        Part createdPart = new Part(id);
        createdPart.setPartName("Actuator");
        createdPart.setPartSpecs("Coil Resistance(+/-10%) Ohms21 \n" +
                "Size(Length x Diameter(inch)2.5 x 0.6\n" +
                "Voltage 12\n" +
                "Holding Force (lbs @200C) 1.5\n" +
                "Power Consumption (W)7\n" +
                "Shaft Diameter(inch) 0,098\n" +
                "Stroke 0.24\n" +
                "Weight (lbs) 0.1");
        createdPart.setScannedTime("00000");
        createdPart.setLocationLat(11.01);
        createdPart.setLocationLong(88.08);
        return createdPart;
    }

}


