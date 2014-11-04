package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import android.view.MotionEvent;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import android.os.Handler;


import android.view.WindowManager;
import android.widget.FrameLayout;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;


/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class ViewFinder extends Activity {
    private static final String kTag = ViewFinder.class.getSimpleName();

    // For Menu
    private boolean mAttachedToWindow;
    private boolean mOptionsMenuOpen;

    // Define gesture detector
    private GestureDetector mGestureDetector;

    // For QR Scanner
    String partID;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewfinder);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        autoFocusHandler = new Handler();

        //For some reason, right after launching from the "ok, glass" menu the camera is locked
        //Try 3 times to grab the camera, with a short delay in between.
        for (int i = 0; i < 3; i++) {
            mCamera = getCameraInstance();
            if (mCamera != null) break;

            Log.d(kTag, "Couldn't lock camera, will try " + (2 - i) + " more times...");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mCamera == null) {
            Toast.makeText(this, "Camera cannot be locked", Toast.LENGTH_SHORT).show();
            finish();
        }

        /* Instantiate barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        /* Start Gesture Detector */
        mGestureDetector = createGestureDetector(this);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
            Log.d(kTag, "getCamera = " + c);
        } catch (Exception e) {
            Log.d(kTag, e.toString());
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            // Draw Reticule and Hint
            ImageView reticule = (ImageView) findViewById(R.id.reticuleView);
            TextView viewfinder_hint = (TextView) findViewById(R.id.hintText);
            reticule.bringToFront();
            viewfinder_hint.bringToFront();

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    // Store QR Code data
                    partID = sym.getData();

                    // Start Part View
                    // Also sends the part ID to the activity, to look up data.

                    // Define Part View Class
                    Intent myIntent = new Intent(ViewFinder.this, PartView.class);
                    // Attach the part info from viewfinder.
                    myIntent.putExtra("KEY", partID);
                    // Start the Part View class
                    ViewFinder.this.startActivity(myIntent);

                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    // Gesture Detection Code ----------------------------------------------------------------------

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

//    Weird, using this causes app to be locked on. No gestures, must exit through menu.
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//            super.openOptionsMenu();
//            return true;
//        }
//        return false;
//    }


    // Options Menu Code --------------------------------------------------------------------------- 

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        // This will open menu on launch
        //openOptionsMenu();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewfindermenu, menu);
        return true;
    }

    @Override
    public void openOptionsMenu() {
        if (!mOptionsMenuOpen && mAttachedToWindow) {
            super.openOptionsMenu();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        mOptionsMenuOpen = false;
    }

}
