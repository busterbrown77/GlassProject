package com.mstratton.jplapp;

/**
 * Created by Sam on 2/2/2015.
 */
public class Part {
    private String partID;
    private String testInfo;

    public String getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(String testInfo) {
        this.testInfo = testInfo;
    }

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public Part(String partID){
        this.partID = partID;
    }

    public Part(){
        this.partID = "EMPTY!!";
        this.testInfo = "TEST";
    }
}
