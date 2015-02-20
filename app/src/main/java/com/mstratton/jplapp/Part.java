package com.mstratton.jplapp;

/**
 * Created by Sam on 2/2/2015.
 */
public class Part {
    private int partID;
    private String testInfo;
    private int integrationStatus;

    public String getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(String testInfo) {
        this.testInfo = testInfo;
    }

    public int getPartID() {
        return partID;
    }

    public void setPartID(int partID) {
        this.partID = partID;
    }

    public void setIntegrationStatus(int status){
        integrationStatus = status;
    }

    public int getIntegrationStatus(){
        return integrationStatus;
    }


    public Part(int partID){
        this.partID = partID;
    }

    public Part(){
        this.partID = -1;
        this.integrationStatus = 0;
        this.testInfo = "TEST";
    }
}
