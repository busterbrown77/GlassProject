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

        //PartD ------------------------------------------------------------------------------------
        newPart.setPartID("D1255");
        newPart.setPartSpecs("Dimensions: [Size]\n" +
                "Color: [Color]\n" +
                "Description: [Description]\n" +
                "Material: [Material]"
        );
        newPart.setPartName("Demo Part");
        newPart.setIntegrationStatus("None");
        newPart.setChecklistSize(1);

        newPart.setChecklistTask("Step 1");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Demo Checklist");
        newPart.setChecklistTask("Step 2");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Demo Checklist");
        newPart.setChecklistTask("Step 3");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Demo Checklist");

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/wrench.png");
        newPart.setPicName("Part Photo");
        mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/wrench.png");
        newPart.setPicName("Part Photo 2");
        mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/wrench.png");
        newPart.setPicName("Part Photo 2");
        mDatabaseHelper.attachPicture(newPart);

        mDatabaseHelper.insertPart(newPart);

        newPart.setReport("Report 1");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Report 2");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Report 3");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Report 4");
        mDatabaseHelper.insertReport(newPart);

        //Part1
        newPart.setPartID("2119w078");
        newPart.setPartSpecs("2'x2'x2'\n" +
                "ASME Bot\n" +
                "Green tracks\n" +
                "Material: Plastic"
        );
        newPart.setPartName("Access Panel");
        newPart.setIntegrationStatus("Mated");
        newPart.setChecklistSize(1);

        newPart.setChecklistTask("Remove 2 Top Screws");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "ASME Remove Chip");
        newPart.setChecklistTask("Lift Top Panel");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "ASME Remove Chip");
        newPart.setChecklistTask("Locate Wireless Chip");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "ASME Remove Chip");
        newPart.setChecklistTask("Remove Broken Chip");
        mDatabaseHelper.insertChecklistTask(newPart, 4, "ASME Remove Chip");
        newPart.setChecklistTask("Insert New Chip");
        mDatabaseHelper.insertChecklistTask(newPart, 5, "ASME Remove Chip");
        newPart.setChecklistTask("Replace Top Panel");
        mDatabaseHelper.insertChecklistTask(newPart, 6, "ASME Remove Chip");
        newPart.setChecklistTask("Secure Top Screws");
        mDatabaseHelper.insertChecklistTask(newPart, 7, "ASME Remove Chip");

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/chip1.png");
        newPart.setPicName("Schematic");
        mDatabaseHelper.attachPicture(newPart);

        mDatabaseHelper.insertPart(newPart);

        newPart.setReport("Broken Pins");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Water Damage");
        mDatabaseHelper.insertReport(newPart);

        mDatabaseHelper.insertPart(newPart);


        //Part2 ------------------------------------------------------------------------------------
        newPart.setPartID("1cr80fs");
        newPart.setPartSpecs("Voltage 4-6 VDC\n" +
                             "PWM\n" +
                             "Dimensions: 2.2 x 0.8 x 1.6 in\n" + // VERIFY
                             "Operating Range: 14-122°F (-10 to +50°C)"
        );
        newPart.setPartName("CR Servo");
        newPart.setIntegrationStatus("Mated");
        newPart.setChecklistSize(1);

        newPart.setChecklistTask("Connect to Controller");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Install");
        newPart.setChecklistTask("Program Controller");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Install");
        newPart.setChecklistTask("Enter Desired Command");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Install");

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/crs1.png");
        newPart.setPicName("Photo");
        mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/crs2.png");
        newPart.setPicName("Schematic");
        mDatabaseHelper.attachPicture(newPart);

        mDatabaseHelper.insertPart(newPart);

        newPart.setReport("Clicking Sound");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Burned Out");
        mDatabaseHelper.insertReport(newPart);

        //Part3 ------------------------------------------------------------------------------------
        newPart.setPartID("907D4042");
        newPart.setPartSpecs("Titanium Pan Head Phillips Machine Screw\n" + // Verify
                             "4-40 Thread\n" +
                             "1/4\" Length"
        );
        newPart.setPartName("Machine Screw");
        newPart.setIntegrationStatus("Mated");
        newPart.setChecklistSize(2);

        newPart.setChecklistTask("Firmly and evenly place screw into hole.");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Installation");
        newPart.setChecklistTask("Turn screw in a clockwise direction by hand until it becomes difficult.");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Installation");
        newPart.setChecklistTask("Use Phillips head screw driver in to continue turning screw clockwise. Stop when screw is tight.");
        mDatabaseHelper.insertChecklistTask(newPart, 3, "Installation");

        newPart.setChecklistTask("Use Phillips head screw driver to turn screw counterclockwise until screw is very loose.");
        mDatabaseHelper.insertChecklistTask(newPart, 1, "Removal");
        newPart.setChecklistTask("Use hand to continue turning screw counterclockwise. Remove scew when it becomes free.");
        mDatabaseHelper.insertChecklistTask(newPart, 2, "Removal");

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/screw1.jpg");
        newPart.setPicName("Detail Photo");
        mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/screw2.png");
        newPart.setPicName("Schematic");
        mDatabaseHelper.attachPicture(newPart);

        mDatabaseHelper.insertPart(newPart);

        newPart.setReport("Screw Loose");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Screw Stripped/Worn");
        mDatabaseHelper.insertReport(newPart);

        //Part4 ------------------------------------------------------------------------------------
        newPart.setPartID("G3R47DFS");
        newPart.setPartSpecs("Resistance (ohm): 10000\n" +
                             "Power (Watts): 0.25\n" +
                             "Tolerance: (%) 5\n" +
                             "Package: AXIAL LEADED"
        );
        newPart.setPartName("Carbon Film Resistor");
        newPart.setIntegrationStatus("Mated");
        newPart.setChecklistSize(0);

//        newPart.setChecklistTask("Step 1");
//        mDatabaseHelper.insertChecklistTask(newPart, 1, "Demo Checklist");
//        newPart.setChecklistTask("Step 2");
//        mDatabaseHelper.insertChecklistTask(newPart, 2, "Demo Checklist");
//        newPart.setChecklistTask("Step 3");
//        mDatabaseHelper.insertChecklistTask(newPart, 3, "Demo Checklist");

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/cfr1.jpg");
        newPart.setPicName("Photo");
        mDatabaseHelper.attachPicture(newPart);

        newPart.setPhotoPath(getApplicationContext().getFilesDir() + "/photos/cfr2.jpg");
        newPart.setPicName("Diagram");
        mDatabaseHelper.attachPicture(newPart);

        mDatabaseHelper.insertPart(newPart);

        newPart.setReport("Report 1");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Report 2");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Report 3");
        mDatabaseHelper.insertReport(newPart);
        newPart.setReport("Report 4");
        mDatabaseHelper.insertReport(newPart);

        mDatabaseHelper.close();
    }

}


