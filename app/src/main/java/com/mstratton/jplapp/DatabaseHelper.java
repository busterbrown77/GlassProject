package com.mstratton.jplapp;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.widget.Toast;

/**
 * Created by Sam on 2/2/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "JPL_DATABASE.sqlite";
    private static int VERSION = 1;
    // References for all the tables rows and columns ----------------------------------------------

    private static final String TABLE_PART = "partDB";
    private static final String TABLE_CHECKLIST = "checklistPart";
    private static final String TABLE_SPECIAL = "specialPart";
    private static final String TABLE_PICTURE = "picturePart";
    private static final String TABLE_VIDEO = "videoPart";
    private static final String TABLE_SCANNED = "scannedPart";

    private static final String COLUMN_PART_ID = "_id";
    private static final String COLUMN_PART_DESCRIPTION ="partDescription";
    private static final String COLUMN_PART_NAME = "partName";
    private static final String COLUMN_PART_STATUS = "partStatus";
    private static final String COLUMN_CHECKLIST_SIZE = "checklistSize";

    private static final String COLUMN_SCAN_LAT = "locationLat";
    private static final String COLUMN_SCAN_LONG = "locationLong";
    private static final String COLUMN_SCAN_TIME = "scanTime";

    private static final String COLUMN_PICTURES = "picPaths";
    private static final String COLUMN_VIDEOS = "vidPaths";

    private static final String COLUMN_CHECKLIST_TASK = "process";
    private static final String COLUMN_CHECKLIST_TASKID = "taskID";
    private static final String COLUMN_CHECKLIST_CHECKLISTID = "checklistID";
    private static final String COLUMN_SPECIAL_ITEMS = "special_items";



    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table partDB("
                + "_id TEXT PRIMARY KEY,"
                + "partName TEXT,"
                + "partDescription TEXT,"
                + "partSpecs TEXT,"
                + "partStatus TEXT,"
                + "checklistSize INTEGER)");
        db.execSQL("create table checklistPart("
                + "_id TEXT,"
                + "process TEXT,"
                + "taskID INTEGER,"
                + "checklistID INTEGER,"
                + "FOREIGN KEY(_id) REFERENCES _id(partDB))");
        db.execSQL("create table scannedPart("
                + "_id TEXT,"
                + "scanTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                + "locationLat DOUBLE,"
                + "locationLong DOUBLE,"
                + "FOREIGN KEY(_id) REFERENCES _id(partDB))");
        db.execSQL("create table picturePart ("
                + "_id TEXT,"
                + "picPaths TEXT,"
                + "FOREIGN KEY(_id) REFERENCES _id(partDB))");
        db.execSQL("create table videoPart ("
                + "_id TEXT,"
                + "vidPaths TEXT,"
                + "FOREIGN KEY(_id) REFERENCES _id(partDB))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertPart(Part part){
        ContentValues cvPartDB = new ContentValues();
        cvPartDB.put(COLUMN_PART_ID, part.getPartID());
        cvPartDB.put(COLUMN_PART_DESCRIPTION, part.getPartSpecs());
        cvPartDB.put(COLUMN_PART_NAME, part.getPartName());
        cvPartDB.put(COLUMN_PART_STATUS, part.getPartStatus());
        cvPartDB.put(COLUMN_CHECKLIST_SIZE, part.getChecklistSize());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PART, null, cvPartDB);
        insertScanHistory(part);
    }

    public void insertChecklistTask(Part part, int taskNumber, int checklistNumber){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PART_ID, part.getPartID());
        cv.put(COLUMN_CHECKLIST_TASK, part.getChecklistTask());
        cv.put(COLUMN_CHECKLIST_CHECKLISTID, checklistNumber);
        cv.put(COLUMN_CHECKLIST_TASKID, taskNumber);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CHECKLIST, null, cv);
    }

    public void insertScanHistory(Part part){
        ContentValues cvScannedPart = new ContentValues();
        cvScannedPart.put(COLUMN_PART_ID, part.getPartID());
        cvScannedPart.put(COLUMN_SCAN_LAT, part.getLocationLat());
        cvScannedPart.put(COLUMN_SCAN_LONG, part.getLocationLong());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_SCANNED, null, cvScannedPart);
    }

    public long updatePart(Part part){
        String id = part.getPartID();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PART_ID, part.getPartID());
        cv.put(COLUMN_PART_DESCRIPTION, part.getPartSpecs());
        cv.put(COLUMN_PART_NAME, part.getPartName());
        String[] args = new String[]{id};
        return getWritableDatabase().update(TABLE_PART, cv, "_id=?", args);
    }

    public void attachPicture(Part part){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PICTURES, part.getPartID());
        cv.put(COLUMN_PICTURES, part.getPhotoPath());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PICTURE, null, cv);
    }

    public void attachVideo(Part part){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_VIDEOS, part.getPartID());
        cv.put(COLUMN_VIDEOS, part.getVideoPath());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_VIDEO, null, cv);
    }

    public PartCursor queryParts(){
        Cursor wrapped = getReadableDatabase().query(TABLE_PART, null, null,
                null, null, null, COLUMN_PART_ID + " asc");
        return new PartCursor(wrapped);
    }

    public PartCursor queryRecent(){
        Cursor wrapped = getReadableDatabase().query(TABLE_SCANNED, null, null,
                null, null, null, COLUMN_SCAN_TIME + " desc");
        return new PartCursor(wrapped);
    }

    public PartCursor queryPart(String part_id){
        Cursor wrapped = getReadableDatabase().query(TABLE_PART, null, COLUMN_PART_ID +" = ?",
                new String[]{part_id}, null, null, null, "1");
        return new PartCursor(wrapped);
    }

    public PartCursor queryChecklists(String part_id){
        Cursor wrapped = getReadableDatabase().query(TABLE_CHECKLIST, null,
                COLUMN_PART_ID + " = ?",
                new String[]{part_id}, null, null, null, null);
        return new PartCursor(wrapped);
    }

    public static class PartCursor extends CursorWrapper{
        public PartCursor(Cursor c){
            super(c);
        }

        public Part getPart(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            Part part = new Part();
            part.setPartID(getString(getColumnIndex(COLUMN_PART_ID)));
            part.setPartSpecs(getString(getColumnIndex(COLUMN_PART_DESCRIPTION)));
            part.setPartName(getString(getColumnIndex(COLUMN_PART_NAME)));
            part.setPartStatus(getString(getColumnIndex(COLUMN_PART_STATUS)));
            part.setChecklistSize(getInt(getColumnIndex(COLUMN_CHECKLIST_SIZE)));
            return part;
        }

        public Part getTime(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            Part part = new Part();
            part.setPartID(getString(getColumnIndex(COLUMN_PART_ID)));
            part.setScannedTime(getString(getColumnIndex(COLUMN_SCAN_TIME)));
            part.setLocationLat(getDouble(getColumnIndex(COLUMN_SCAN_LAT)));
            part.setLocationLong(getDouble(getColumnIndex(COLUMN_SCAN_LONG)));
            return part;
        }

        public Part getChecklist(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            Part part = new Part();
            part.setPartID(getString(getColumnIndex(COLUMN_PART_ID)));
            part.setChecklistTask(getString(getColumnIndex(COLUMN_CHECKLIST_TASK)));
            part.setChecklistID(getString(getColumnIndex(COLUMN_CHECKLIST_CHECKLISTID)));
            return part;
        }


    }

}
