package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
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
    private ArrayList<View> cardList;
    CardScrollView csvCardsView;
    private GestureDetector mGestureDetector;

    // For Startup Checks
    String filename = "testdata";
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardList = new ArrayList<View>();

        // Create the cards for the view
        createCards();

        csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.getKeepScreenOn();

        // Set the view for the Slider
        mSlider = Slider.from(csvCardsView);
        mIndeterminate = mSlider.startIndeterminate();

        // Show Card View
        setContentView(csvCardsView);

        /* Start Gesture Detector */
        mGestureDetector = createGestureDetector(this);

        // Animate the Loading Screen
        handler.postDelayed(informStorage, 600);
        handler.postDelayed(runStorage, 750);
        handler.postDelayed(informData, 900);
        handler.postDelayed(runData, 4500);
    }

    //For Loading Screen ---------------------------------------------------------------------

    private void createCards() {
        View cardLoad = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Loading...")
                .getView();
        cardList.add(cardLoad);

        View cardStorage = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Checking Storage...")
                .getView();
        cardList.add(cardStorage);

        View cardData = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Checking App Data...")
                .getView();
        cardList.add(cardData);

        View cardData2 = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Installing App Data...")
                .getView();
        cardList.add(cardData2);

        View cardDone = new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Done!")
                .getView();
        cardList.add(cardDone);
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

    public boolean checkStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean checkData() {
        File file = getBaseContext().getFileStreamPath(filename);

        if (file.exists()) {
            // File Exists, start next activity now
            handler.postDelayed(informDone, 6700);
            return true;
        } else {
            // File does not exist, must create it
            // Start next activity in a 10 seconds
            handler.postDelayed(informDone, 12000);

            // User Message
            csvCardsView.setSelection(3);
            mIndeterminate = mSlider.startIndeterminate();

            // Create Directories and Data
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/TestData/");
            dir.mkdirs();
            File newfile = new File(dir, filename);

            String data = "";
            try {
                FileOutputStream os = new FileOutputStream(newfile);
                os.write(data.getBytes());
                os.close();

            } catch (IOException nf) {
                return false;
            }

            return true;
        }
    }

    Runnable informStorage = new Runnable() {
        @Override
        public void run() {
            csvCardsView.setSelection(1);
            mIndeterminate = mSlider.startIndeterminate();
        }
    };

    Runnable runStorage = new Runnable() {
        @Override
        public void run() {
            // Storage Check
            if (checkStorage()) {

            } else {
                // Storage Check Fail
                Toast.makeText(SplashScreen.this, "Storage Check Failed!", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        }
    };

    Runnable informData = new Runnable() {
        @Override
        public void run() {
            csvCardsView.setSelection(2);
            mIndeterminate = mSlider.startIndeterminate();
        }
    };

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

    Runnable informDone = new Runnable() {
        @Override
        public void run() {
            csvCardsView.setSelection(4);
            mIndeterminate = mSlider.startIndeterminate();

            // Loaded, Start Application
            Intent intent = new Intent(SplashScreen.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

    //For Navigation -------------------------------------------------------------------------

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    //Toast.makeText(getApplicationContext(), "Tap", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    Toast.makeText(getApplicationContext(), "Two Tap", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    Toast.makeText(getApplicationContext(), "Swipe Right", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    Toast.makeText(getApplicationContext(), "Swipe Left", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.LONG_PRESS) {
                    Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN) {
                    Toast.makeText(getApplicationContext(), "Swipe Down", Toast.LENGTH_SHORT).show();
                    // Appears to be intercepted by CameraPreview. Nothing here works.

                    return false;
                } else if (gesture == Gesture.SWIPE_UP) {
                    Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.THREE_LONG_PRESS) {
                    Toast.makeText(getApplicationContext(), "Three Long Press", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.THREE_TAP) {
                    Toast.makeText(getApplicationContext(), "Three Tap", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.TWO_LONG_PRESS) {
                    Toast.makeText(getApplicationContext(), "Two Long Press", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.TWO_SWIPE_DOWN) {
                    Toast.makeText(getApplicationContext(), "Two Swipe Down", Toast.LENGTH_SHORT).show();
                    // Appears to be intercepted by CameraPreview. Nothing here works.

                    return false;
                } else if (gesture == Gesture.TWO_SWIPE_LEFT) {
                    Toast.makeText(getApplicationContext(), "Two Swipe Left", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.TWO_SWIPE_RIGHT) {
                    Toast.makeText(getApplicationContext(), "Two Swipe Right", Toast.LENGTH_SHORT).show();

                    return true;
                } else if (gesture == Gesture.TWO_SWIPE_UP) {
                    Toast.makeText(getApplicationContext(), "Two Swipe up", Toast.LENGTH_SHORT).show();

                    return true;
                }

                return false;
            }
        });
        return gestureDetector;
    }

    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

}


