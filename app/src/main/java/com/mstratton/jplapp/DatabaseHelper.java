package com.mstratton.jplapp;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.CursorWrapper;

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
    private static final String COLUMN_PICTURES = "picture_location";
    private static final String COLUMN_VIDEOS = "videos_location";
    private static final String COLUMN_CHECKLIST_TASK = "checklist_task";
    private static final String COLUMN_INTEGRATION_STATUS = "integration_status";
    private static final String COLUMN_SPECIAL_ITEMS = "special_items";



    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table partDB("
                + " _id TEXT PRIMARY KEY,"
                + " partName TEXT,"
                + " partDescription TEXT,"
                + " partSpecs TEXT)");
        db.execSQL("create table checklistPart("
                + " _id TEXT,"
                + " process TEXT,"
                + " FOREIGN KEY(_id) REFERENCES _id(partDB))");
        db.execSQL("create table scannedPart("
                + " _id TEXT,"
                + " scanTime DATETIME,"
                + " locationLat DOUBLE,"
                + " locationLong DOUBLE,"
                + " FOREIGN KEY(_id) REFERENCES _id(partDB))");
        db.execSQL("create table picturePart ("
                + " _id TEXT,"
                + " picPaths TEXT,"
                + " FOREIGN KEY(_id) REFERENCES _id(partDB))");
        db.execSQL("create table videoPart ("
                + " _id TEXT,"
                + " vidPaths TEXT,"
                + " FOREIGN KEY(_id) REFERENCES _id(partDB))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertPart(Part part){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PART_ID, part.getPartID());
        return getWritableDatabase().insert(TABLE_PART, null, cv);
    }

    public PartCursor queryParts(){
        Cursor wrapped = getReadableDatabase().query(TABLE_PART, null, null,
                null, null, null, COLUMN_PART_ID + " asc");
        return new PartCursor(wrapped);
    }

    public PartCursor queryPart(String part_id){
        Cursor wrapped = getReadableDatabase().query(TABLE_PART, null, COLUMN_PART_ID +" = ?",
                new String[]{part_id}, null, null, null, "1");
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
            return part;

        }
    }

}
