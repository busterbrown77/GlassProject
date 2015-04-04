package com.mstratton.jplapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/* Import ZBar Class files */

public class ViewFinder extends Activity {
    private static final String kTag = ViewFinder.class.getSimpleName();

    // For Menu
    private boolean mAttachedToWindow;
    private boolean mOptionsMenuOpen;

    // For QR Scanner
    String partID;

    private Camera mCamera;
    private CameraLib mPreview;
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

        // Determine if fail code received, and notify
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String CODE = extras.getString("KEY");

            if (CODE.equals("NOT_FOUND")) {
                Toast.makeText(this, "Part Not Found.", Toast.LENGTH_SHORT).show();
            }
        }

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

        mPreview = new CameraLib(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
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
                    Intent myIntent = new Intent(ViewFinder.this, PartInfo.class);
                    // Attach the part info from viewfinder.
                    myIntent.putExtra("KEY", partID);
                    myIntent.putExtra("RETRIEVED_FROM", "viewfinder");
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

}
