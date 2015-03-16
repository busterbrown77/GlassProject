package com.mstratton.jplapp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sam on 2/2/2015.
 */
public class Part {

    private String partID;
    private String partName;
    private String partSpecs;
    private Date ScannedTime;
    private double locationLat;
    private double locationLong;
    private String photoPath;
    private String videoPath;
    private ArrayList<String> checklist;

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public String getPartSpecs(){
        return partSpecs;
    }

    public void setPartSpecs(String partSpecs) {
        this.partSpecs = partSpecs;
    }

    public Date getScannedTime() {
        return ScannedTime;
    }

    public void setScannedTime(Date scannedTime) {
        ScannedTime = scannedTime;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        locationLat = locationLat;
    }

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) {
        this.locationLong = locationLong;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public ArrayList<String> getChecklist() {
        return checklist;
    }

    public void setChecklist(ArrayList<String> checklist) {
        this.checklist = checklist;
    }

    public Part(String partID){
        this.partID = partID;
    }

    public Part(){
        this.partID = partID;
        this.partName = "TEST";
    }

    public Part(String partID, String partName, String partSpecs, Date ScannedTime,
                double locationLat, double locationLong, String photoPath, String videoPath){

        this.partID = partID;
        this.partName = partName;
        this.partSpecs = partSpecs;
        this.ScannedTime = ScannedTime;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.photoPath = photoPath;
        this.videoPath = videoPath;
    }


}
