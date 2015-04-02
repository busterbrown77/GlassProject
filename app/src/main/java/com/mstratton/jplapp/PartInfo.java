package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
    ArrayList<String> photonames;
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
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUri;

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

        // Check last activity to determine whether or not to
        // update last scan time
        if (!retrievedFrom.equals("recentparts")) {
            updated = false;
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

        // Setup Photos and Media before Card Creation
        // For loop for photos
        ArrayList<String> mediaNames = new ArrayList<String>();
        dataCursor = mDatabaseHelper.queryPictures(partID);
        dataCursor.moveToFirst();
        while(!dataCursor.isAfterLast()){
            scannedPart = dataCursor.getPictures();
            mediaNames.add(scannedPart.getPicName());
            dataCursor.moveToNext();
        }
        dataCursor.close();

        //File f = new File(Environment.getExternalStorageDirectory()
        //       + File.separator + "DCIM/Camera/TrollFace.jpg");

        File f = new File("data/data/com.mstratton.jplapp/files/photos/gen.png");

        //File f = new File(scannedPart.getPhotoPath());
        Drawable photo = Drawable.createFromPath(f.getAbsolutePath());
        Resources res = getResources();

//        dataCursor = mDatabaseHelper.queryChecklist(partID, 0);
//        dataCursor.moveToFirst();
//        String count;
//
//        while(!dataCursor.isAfterLast()){
//            scannedPart = dataCursor.getChecklist();
//            count = scannedPart.getChecklistTask();
//            Toast.makeText(this, count, Toast.LENGTH_SHORT).show();
//            dataCursor.moveToNext();
//        }
//
//        dataCursor.close();

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

        // Create cards using information.
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

        View detectCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText("Found a " + scannedPart.getPartName() + " (" + scannedPart.getPartID() +") part.")
                .setFootnote("Part Detected!")
                 .addImage(photo)
                .getView();
        cardList.add(detectCard);

        View specificationCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText(scannedPart.getPartSpecs())
                .setFootnote("Part Specifications")
                .addImage(photo)
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
                    .setFootnote("Part Checklists")
                    .getView();
            cardList.add(checklistsCard);
        }

        // Add loop for photos
        View photosCard = new CardBuilder(this, CardBuilder.Layout.CAPTION)
                .setText(scannedPart.getPicName())
                .setFootnote("Part Photos")
                .addImage(photo)
                .getView();
        cardList.add(photosCard);

        // Add loop for videos
        View videosCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("BROKEN")//scannedPart.getChecklist())
                .setFootnote("Part Videos")
                 //.addImage(R.drawable.intake)
                .getView();
        cardList.add(videosCard);

        View historyCard = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                .setText("BROKEN")
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //save the card index that was selected
                cardIndex = position;

                if (position == 0) {
                    openVideoCamera();

                } else if (position == 1) {
                    openPhotoCamera();

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

    public void openVideoCamera () {

        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File appDir = getApplicationContext().getFilesDir();
        File videoDir = new File(appDir, "videos");
        File video = new File(videoDir, "video_001.mp4");
        Uri fileUri = Uri.fromFile(video);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

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

    // For Location Data --------------------------------------------------------------------

    public void makeUseOfNewLocation(Location location){
        if(!updated) {
            scannedPart.setLocationLat(location.getLatitude());
            scannedPart.setLocationLong(location.getLongitude());
            mDatabaseHelper.insertScanHistory(scannedPart);
        }
    }

}


