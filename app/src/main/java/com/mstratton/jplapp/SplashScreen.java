package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.widget.Slider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class SplashScreen extends Activity {
    // For Loading Screen
    private Slider.Indeterminate mIndeterminate;
    private Slider mSlider;
    private Context mContext;
    private ArrayList<View> cardList;
    CardScrollView csvCardsView;
    DatabaseHelper mDatabaseHelper;

    // For Startup Checks
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cardList = new ArrayList<View>();

        // Create the Loading Card
        View cardLoad = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Loading...")
                .getView();
        cardList.add(cardLoad);

        // Create the CardScrollView
        csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();

        // Set the View and Mode for the Slider
        mSlider = Slider.from(csvCardsView);
        mIndeterminate = mSlider.startIndeterminate();

        // Show Card View
        setContentView(csvCardsView);

        // Run Data Checks
        handler.postDelayed(runData, 600);
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

    //For Startup Checks ---------------------------------------------------------------------

    public boolean checkData() {
        // Get root appdata dir
        File appDir = getApplicationContext().getFilesDir();
        // Attempt to find info file in appdata dir
        File file = new File(appDir, "info.txt");

        if (file.exists()) {
            // File Exists, start next activity now
            handler.postDelayed(runDone,800);
            return true;
        } else {
            // File does not exist, must create it
            // Start next activity in a few seconds (ensure files / db below are created)
            handler.postDelayed(runDone, 1100);

            // Debug Msg
            Toast.makeText(SplashScreen.this, "Creating Initial App Data", Toast.LENGTH_SHORT).show();

            // Create Directories and Data
            File photoDir = new File(appDir, "photos");
            if( !photoDir.exists() )
                photoDir.mkdir();

            File videoDir = new File(appDir, "videos");
            if( !videoDir.exists() )
                videoDir.mkdir();

            // Write App Info File
            File outFile3 = new File(appDir, "info.txt");

            // Info File Data
            String data = "JPLAPP v1.0";
            try {
                FileOutputStream os = new FileOutputStream(outFile3);
                os.write(data.getBytes());
                os.close();

            } catch (IOException nf) {
                return false;
            }

            //Generate Initial Database Data
            TestData();

            return true;
        }
    }

    Runnable runData = new Runnable() {
        @Override
        public void run() {
            // File Check
            if (checkData()) {

            } else {
                // Data Check Fail
                Toast.makeText(SplashScreen.this, "Data Check Failed!", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        }
    };

    Runnable runDone = new Runnable() {
        @Override
        public void run() {
            // Loaded, Start Application
            Intent intent = new Intent(SplashScreen.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

    public void TestData(){
        mDatabaseHelper = new DatabaseHelper(this);
        Part newPart = new Part("TEMP");

        //Part1
        newPart.setPartID("2119w078");
        newPart.setPartStatus("Connected");
        newPart.setPartSpecs("Width: 15 inches" +
                "Length: 30 inches" +
                "MaxTemp: 100F" +
                "Material: Sammium");
        newPart.setPartName("Actuator");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("Connect Socket");
        newPart.setIntegrationStatus("Bisexual");

        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist");
        newPart.setChecklistTask("Turn Handle");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist");
        newPart.setChecklistTask("Admire Work");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist");
        newPart.setChecklistTask("Collect Paycheck");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist");
        //newPart.setVideoPath();
        //newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        //mDatabaseHelper.attachPicture(newPart);
        //mDatabaseHelper.attachPicture(newPart);


        //Part2
        newPart.setPartID("2119w080");
        newPart.setPartStatus("Destroyed");
        newPart.setPartSpecs("Really giant" +
                "Really round" +
                "Also very square" +
                "Running out of ideas");
        newPart.setPartName("Backpack");
        newPart.setChecklistSize(2);
        newPart.setChecklistTask("Zip Backpack");


        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist");
        newPart.setChecklistTask("Walk Around");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist");
        newPart.setChecklistTask("Look Cool");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist");
        newPart.setChecklistTask("Help Me");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist");
        newPart.setChecklistTask("Don't do anything");
        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist1");
        newPart.setChecklistTask("second checklist works");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist1");
        newPart.setChecklistTask("YAY");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist1");
        newPart.setChecklistTask("WHO DAT!!!");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist1");

        //newPart.setVideoPath(getApplicationContext().getFilesDir() + "/videos/awesomesauce.mp4");
        //newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/6PKW2Al.jpg");
        newPart.setPhotoPath("/data/media/0/DCIM/Camera/test.jpg");
        newPart.setPicName("/data/media/0/DCIM/Camera/test.jpg");
        mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath("/data/media/0/DCIM/Camera/test2.jpg");
        newPart.setPicName("/data/media/0/DCIM/Camera/test2.jpg");

        //newPart.setPicName("AHAHAHHANO");
        //newPart.setVidName("AWESOME");

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachVideo(newPart);


        //Part3
        newPart.setPartID("2119w990");
        newPart.setPartStatus("Platinum");
        newPart.setPartSpecs("SPEC SPEC SPEC SPEC SPEC test");
        newPart.setPartName("GLASSES");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("part 3 task 1");

        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist");
        newPart.setChecklistTask("part 3 task 2");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist");
        newPart.setChecklistTask("part 3 task 3");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist");
        newPart.setChecklistTask("You heard that new Fetty WAP?!?");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist");

        //newPart.setVideoPath();
        //newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);


        //Part4
        newPart.setPartID("2119w995");
        newPart.setPartStatus("Unobtainium");
        newPart.setPartSpecs("SPE test");
        newPart.setPartName("FGDSLKRNFG");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("part 3 task 1");

        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist");
        newPart.setChecklistTask("part 3 task 2");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist");
        newPart.setChecklistTask("part 3 task 3");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist");
        newPart.setChecklistTask("You heard that new Fetty WAP?!?");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist");

        //newPart.setVideoPath();
        //newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.close();
    }


}


