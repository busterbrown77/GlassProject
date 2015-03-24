package com.mstratton.jplapp;

/**
 * Created by Sam on 3/24/2015.
 */
import android.app.Activity;


public class DatabaseMaker extends Activity{
    DatabaseHelper mDatabaseHelper;

    public DatabaseMaker(){
        Create();
    }


    public void Create(){
        mDatabaseHelper = new DatabaseHelper(this);
        Part newPart = new Part("TEMP");

        //Part1
        newPart.setPartID("2119w078");
        newPart.setPartStatus("Connected");
        newPart.setPartSpecs("Width: 15 inches\n" +
                "Length: 30 inches\n" +
                "MaxTemp: 100F\n" +
                "Material: Sammium");
        newPart.setPartName("Actuator");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("Connect Socket");
        /*
        mDatabaseHelper.insertChecklistTask(newPart, 0, 0);
        newPart.setChecklistTask("Turn Handle");
        mDatabaseHelper.insertChecklistTask(newPart, 1, 0);
        newPart.setChecklistTask("Admire Work");
        mDatabaseHelper.insertChecklistTask(newPart, 2, 0);
        newPart.setChecklistTask("Collect Paycheck");
        mDatabaseHelper.insertChecklistTask(newPart, 3, 0);
        //newPart.setVideoPath();
        //newPart.setPhotoPath();
        */
        mDatabaseHelper.insertPart(newPart);
        //mDatabaseHelper.attachPicture(newPart);
        //mDatabaseHelper.attachPicture(newPart);


        //Part2
        newPart.setPartID("2119w080");
        newPart.setPartStatus("Destroyed");
        newPart.setPartSpecs("Really giant\n" +
                "Really round\n" +
                "Also very square" +
                "Running out of ideas");
        newPart.setPartName("Backpack");
        newPart.setChecklistSize(2);
        newPart.setChecklistTask("Zip Backpack");

        /*
        mDatabaseHelper.insertChecklistTask(newPart, 0, 0);
        newPart.setChecklistTask("Walk Around");
        mDatabaseHelper.insertChecklistTask(newPart, 1, 0);
        newPart.setChecklistTask("Look Cool");
        mDatabaseHelper.insertChecklistTask(newPart, 2, 0);
        newPart.setChecklistTask("Help Me");
        mDatabaseHelper.insertChecklistTask(newPart, 3, 0);
        newPart.setChecklistTask("Don't do anything");
        mDatabaseHelper.insertChecklistTask(newPart, 0, 1);
        newPart.setChecklistTask("second checklist works");
        mDatabaseHelper.insertChecklistTask(newPart, 1, 1);
        newPart.setChecklistTask("YAY");
        mDatabaseHelper.insertChecklistTask(newPart, 2, 1);
        newPart.setChecklistTask("WHO DAT!!!");
        mDatabaseHelper.insertChecklistTask(newPart, 3, 1);
        */
        //newPart.setVideoPath();
        //newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        //mDatabaseHelper.attachPicture(newPart);
        //mDatabaseHelper.attachPicture(newPart);


        //Part3
        newPart.setPartID("2119w990");
        newPart.setPartStatus("Platinum");
        newPart.setPartSpecs("SPEC SPEC SPEC SPEC SPEC test");
        newPart.setPartName("GLASSES");
        newPart.setChecklistSize(1);
        newPart.setChecklistTask("part 3 task 1");
        /*
        mDatabaseHelper.insertChecklistTask(newPart, 0, 0);
        newPart.setChecklistTask("part 3 task 2");
        mDatabaseHelper.insertChecklistTask(newPart, 1, 0);
        newPart.setChecklistTask("part 3 task 3");
        mDatabaseHelper.insertChecklistTask(newPart, 2, 0);
        newPart.setChecklistTask("You heard that new Fetty WAP?!?");
        mDatabaseHelper.insertChecklistTask(newPart, 3, 0);
        */
        //newPart.setVideoPath();
        //newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        //mDatabaseHelper.attachPicture(newPart);
        //mDatabaseHelper.attachPicture(newPart);
/*
        //Part4
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);

        //Part5
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);

        //Part6
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);

        //Part7
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);

        //Part8
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);

        //Part9
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);


        //Part10
        newPart.setPartID();
        newPart.setPartStatus();
        newPart.setPartSpecs();
        newPart.setPartName();
        newPart.setChecklistSize();
        newPart.setChecklistTask();
        newPart.setVideoPath();
        newPart.setPhotoPath();

        mDatabaseHelper.insertPart(newPart);
        mDatabaseHelper.attachPicture(newPart);
        mDatabaseHelper.attachPicture(newPart);
*/
    }
}






