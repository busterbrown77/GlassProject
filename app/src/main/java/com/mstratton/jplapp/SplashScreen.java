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

            //Generate Test Database Data
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
        newPart.setIntegrationStatus("None");
        newPart.setPartID("2119w078");
        newPart.setPartSpecs("2'x2'x2'\n" +
                "ASME Bot\n" +
                "Green tracks\n" +
                "Material: Plastic");
        newPart.setPartName("Access Panel");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("Remove 2 screws from top");

        mDatabaseHelper.insertChecklistTask(newPart, 0, "ASME Remove Chip");
        newPart.setChecklistTask("Lift top");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "ASME Remove Chip");
        newPart.setChecklistTask("Remove broken wireless chip and replace with new chip");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "ASME Remove Chip");
        newPart.setChecklistTask("Replace top and screw in screws");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "ASME Remove Chip");
        //newPart.setVideoPath();

        newPart.setReport("Broken Pins");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Water Damage");
        mDatabaseHelper.insertReport(newPart);

        mDatabaseHelper.insertPart(newPart);
        //mDatabaseHelper.attachPicture(newPart);
        //mDatabaseHelper.attachPicture(newPart);


        //Part2




        newPart.setPartID("2119w080");
        newPart.setPartSpecs("Desktop\n" +
                             "Color: black\n" +
                             "Very square\n" +
                             "FIT Lab\n");
        newPart.setPartName("Computer");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("Pull on latch and release side cover");


        mDatabaseHelper.insertChecklistTask(newPart, 0, "Remove RAM");
        newPart.setChecklistTask("Remove side covering");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Remove RAM");
        newPart.setChecklistTask("Locate RAM inside the case and release side latches");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Remove RAM");
        newPart.setChecklistTask("Remove RAM and replace with new RAM");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Remove RAM");
        newPart.setChecklistTask("Close case");
        mDatabaseHelper.insertChecklistTask(newPart, 0, "Remove RAM");
//        newPart.setChecklistTask("second checklist works");
//        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist1");
//        newPart.setChecklistTask("YAY");
//        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist1");
//        newPart.setChecklistTask("Complete");
//        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist1");

        //newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/jpllogo.jpg");
        //newPart.setPicName("Logo");
        //mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/nasa.jpg");
        newPart.setPicName("Nasa");
        //mDatabaseHelper.attachPicture(newPart);

        mDatabaseHelper.insertPart(newPart);

        newPart.setReport("Broken Pins");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Water Damage");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Overheated");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Cracked Motherboard");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Missing cable");
        mDatabaseHelper.insertReport(newPart);

        //Part3
        newPart.setPartID("2119w990");
        newPart.setPartSpecs("SPEC\nSPEC\nSPEC\nSPEC\nSPEC\ntest");
        newPart.setPartName("GLASSES");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("part 3 task 1");

        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist");
        newPart.setChecklistTask("part 3 task 2");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist");
        newPart.setChecklistTask("part 3 task 3");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist");
        newPart.setChecklistTask("Task?");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist");

        //newPart.setVideoPath();
        //newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        newPart.setReport("Fire");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Dropped");
        mDatabaseHelper.insertReport(newPart);


        //Part4
        newPart.setPartID("2119w995");
        newPart.setPartSpecs("SPE test");
        newPart.setPartName("FGDSLKRNFG");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("part 3 task 1");

        mDatabaseHelper.insertChecklistTask(newPart, 0, "Some Checklist");
        newPart.setChecklistTask("part 3 task 2");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Some Checklist");
        newPart.setChecklistTask("part 3 task 3");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Some Checklist");
        newPart.setChecklistTask("Turn");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Some Checklist");
        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/shuttle.png");
        newPart.setPicName("Shuttle");

        //mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.close();
    }

}


