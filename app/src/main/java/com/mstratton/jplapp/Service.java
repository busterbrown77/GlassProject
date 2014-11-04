package com.mstratton.jplapp;

import android.content.Intent;
import android.os.IBinder;
 
public class Service extends android.app.Service {
     
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
 
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start Static Card     
        Intent i = new Intent(this, ViewFinder.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        return START_STICKY;
       
    }
    
}
