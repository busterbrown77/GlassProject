package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.File;
import java.util.ArrayList;

public class PartInfo extends Activity {
    int cardIndex;
    String partID;
    String checklistID;
    String retrievedFrom;
    private ArrayList<View> cardList;
    ArrayList<String> names;
    CardScrollView csvCardsView;

    // For Database Functionality
    DatabaseHelper mDatabaseHelper;
    Part scannedPart;

    // For Location Functionality
    public LocationListener mLocationListener;
    public Criteria criteria;
    boolean updated = true;

    // For Camera Functionality
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get partID passed from the Viewfinder Activity
        // and previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            partID = extras.getString("KEY");
            retrievedFrom = extras.getString("RETRIEVED_FROM");
        }

        // Start Contextual Voice Commands
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        // Instantiate the Database
        mDatabaseHelper = new DatabaseHelper(this);
        DatabaseHelper.PartCursor dataCursor;

        dataCursor = mDatabaseHelper.queryPart(partID);
        dataCursor.moveToFirst();
        if (!dataCursor.isAfterLast()) {
            scannedPart = dataCursor.getPart();
        }

        if (scannedPart == null) {
            // Not Found, return to viewfinder
            Intent intent = new Intent(PartInfo.this, ViewFinder.class);
            intent.putExtra("KEY", "NOT_FOUND");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


        // Create cards using information.
        cardList = new ArrayList<View>();

        // Add photo and add video cards to left
        View addphotoCard = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Add Photo")
                .getView();
        cardList.add(addphotoCard);

        View integrationCard = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Integration Status")
                .getView();
        cardList.add(integrationCard);

        View detectCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("Found a " + scannedPart.getPartName() + " (" + scannedPart.getPartID() +") part.\n\n" + "Integration Status: " + scannedPart.getIntegrationStatus())
                .addImage(R.drawable.wrench)
                .getView();
        cardList.add(detectCard);

        View specificationCard = new CardBuilder(this, CardBuilder.Layout.TEXT_FIXED)
                .setText("Specifications\n\n" + scannedPart.getPartSpecs())
                .getView();
        cardList.add(specificationCard);

        // For loop for checklists
        names = new ArrayList<String>();
        dataCursor = mDatabaseHelper.queryChecklists(partID);
        dataCursor.moveToFirst();

        Part temp = new Part("temp");
        while(!dataCursor.isAfterLast()){
            temp = dataCursor.getChecklist();
            if(!names.contains(temp.getChecklistID())) {
                names.add(temp.getChecklistID());
            }
            dataCursor.moveToNext();
        }

        for (int i = 0; i < names.size(); i++) {
            View checklistsCard = new CardBuilder(this, CardBuilder.Layout.MENU)
                    .setText(names.get(i))
                    .getView();
            cardList.add(checklistsCard);
        }

        // Setup Photos and Media before Card Creation
        // For loop for photos
        ArrayList<String> photoNames = new ArrayList<String>();
        ArrayList<Drawable> drawables = new ArrayList<Drawable>();
        dataCursor = mDatabaseHelper.queryPictures(partID);
        dataCursor.moveToFirst();
        while(!dataCursor.isAfterLast()){
            scannedPart = dataCursor.getPictures();
            photoNames.add(scannedPart.getPicName());
            drawables.add(Drawable.createFromPath(scannedPart.getPhotoPath()));
            dataCursor.moveToNext();
        }
        dataCursor.close();

        // Add loop for photos
        for(int i = 0; i < drawables.size(); i++) {
            View photosCard = new CardBuilder(this, CardBuilder.Layout.CAPTION)
                    .setText(photoNames.get(i))
                    .addImage(drawables.get(i))
                    .getView();
            cardList.add(photosCard);
        }

        ArrayList<String> reports = new ArrayList<String>();
        dataCursor = mDatabaseHelper.queryReports(partID);
        dataCursor.moveToFirst();
        while(!dataCursor.isAfterLast()){
            scannedPart = dataCursor.getPictures();
            reports.add(scannedPart.getReport());
            dataCursor.moveToNext();
        }
        dataCursor.close();

        View historyCard = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.table)
                .getView();

        cardList.add(historyCard);

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
                    openPhotoCamera();

                } else if (position == 1) {
                    openIntegrationStatus();

                } else if (position >= 4 && position < (4 + names.size())) {
                    // Start CheckList View
                    openChecklistView(names.get(position - 4));
                }
            }
        });

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

    public void openPhotoCamera () {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File appDir = getApplicationContext().getFilesDir();
        File photoDir = new File(appDir, "photos");
        File image = new File(photoDir, "image_001.jpg");
        Uri fileUri = Uri.fromFile(image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void openChecklistView (String name) {
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        checklistID = name;

        // Define CheckList View Class
        Intent myIntent = new Intent(PartInfo.this, CheckList.class);
        // Attach the part info from Part View.
        myIntent.putExtra("KEY", partID);
        myIntent.putExtra("KEY2", checklistID);
        // Start the CheckList View class
        startActivity(myIntent);
    }

    public void openIntegrationStatus () {
        // Define Integration Status Class
        Intent myIntent = new Intent(PartInfo.this, IntegrationStatus.class);
        // Attach the part info from Part View.
        myIntent.putExtra("KEY", partID);
        // Start the Integration Status class
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

// MENU NEEDS GYRO SCROLLING
//                case R.id.menu_addvideo:
//                    csvCardsView.setSelection(0);
//
//                    break;
//                case R.id.menu_addphoto:
//                    csvCardsView.setSelection(1);
//
//                    break;
                case R.id.menu_detail:
                    csvCardsView.setSelection(2);

                    break;
                case R.id.menu_specs:
                    csvCardsView.setSelection(3);

                    break;
                case R.id.menu_checklist:
                    csvCardsView.setSelection(4);

                    // Must start Checklists class automatically, since no way
                    // to "select" a card using voice.

                    break;
                case R.id.menu_photo:
                    csvCardsView.setSelection(5);

                    break;
                case R.id.menu_video:
                    csvCardsView.setSelection(6);

                    break;
                case R.id.menu_history:
                    csvCardsView.setSelection(7);

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


