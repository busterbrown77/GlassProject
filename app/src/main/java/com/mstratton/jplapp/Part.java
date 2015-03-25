package com.mstratton.jplapp;

import java.util.ArrayList;

public class Part {

    private String partID;
    private String partName;
    private String partSpecs;
    private String ScannedTime;
    private double locationLat;
    private double locationLong;
    private String photoPath;
    private String videoPath;
    private ArrayList<String> checklist;
    private int checklistSize;
    private String checklistID;
    private String checklistTask;
    private String partStatus;
    public String picName;
    public String vidName;

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getChecklistID() {
        return checklistID;
    }

    public void setChecklistID(String id){
        this.checklistID = id;
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

    public String getScannedTime() {
        return ScannedTime;
    }

    public void setScannedTime(String scannedTime) {
        ScannedTime = scannedTime;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
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

    public int getChecklistSize(){
        return checklistSize;
    }

    public void setChecklistSize(int size){
        this.checklistSize = size;
    }

    public void setPartStatus(String status){
        this.partStatus = status;
    }

    public String getPartStatus(){
        return partStatus;
    }

    public void setChecklistTask(String task){
        this.checklistTask = task;
    }

    public String getChecklistTask(){
        return checklistTask;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getVidName() {
        return vidName;
    }

    public void setVidName(String vidName) {
        this.vidName = vidName;
    }

    public Part(String partID){
        this.partID = partID;
    }

    public Part(){
        this.partID = partID;
        this.partName = "TEST";
    }

    public Part(String partID, String partName, String partSpecs, String ScannedTime,
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
