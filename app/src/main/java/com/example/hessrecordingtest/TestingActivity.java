package com.example.hessrecordingtest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class TestingActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private View mContentView;

    private static final int UI_ANIMATION_DELAY = 300;
    private View mControlsView;
    private boolean mVisible;

    //region Custom global variables
    private IntentFilter mIntentFilter;
    private ImageView left_imageview;
    private ImageView right_imageview;

    private int resolution_value = 1200;
    private int circle_radius = 15;
    private int correction_value = 150;

    private Canvas left_canvas;
    private Canvas right_canvas;
    private Paint left_paint;
    private Paint right_paint;
    private Bitmap left_imageBitmap;
    private Bitmap right_imageBitmap;

    private int pointer_location_x = resolution_value/2;
    // The correction value is used to compensate the height of the screen properly
    private int pointer_location_y = (resolution_value + correction_value) / 2;

    private double outer_point_x = 0.098;
    private double outer_point_y = 0.135;
    private double inner_point_horizontal = 0.165;
    private double inner_point_vertical = 0.208;

    private int[] test_x_coordinates = {resolution_value/2,
            (int) (resolution_value * outer_point_x),
            (resolution_value/2),
            (int) (resolution_value * (1 - outer_point_x)),
            (int) (resolution_value * (1 - inner_point_horizontal)),
            (int) (resolution_value * (1 - outer_point_x)),
            (resolution_value/2),
            (int) (resolution_value * outer_point_x),
            (int) (resolution_value * inner_point_horizontal)};

    private int[] test_y_coordinates = {(resolution_value + correction_value) / 2,
            (int) (resolution_value * outer_point_y),
            (int) (resolution_value * inner_point_vertical),
            (int) (resolution_value * outer_point_y),
            (resolution_value/2),
            (int) (resolution_value * (1 - outer_point_y)),
            (int) (resolution_value * (1 - inner_point_vertical)),
            (int) (resolution_value * (1 - outer_point_y)),
            (resolution_value/2)};

    private int test_point_index = 0;
    private int marker_move = 8;
    private boolean reference_image_displayed = false;

    private boolean start_joystick = false;
    private int[][] detected_locations = new int[2][9];
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region Code for initialising default parameters
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_testing);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        //endregion

        // Setting up the Image views
        left_imageview = (ImageView) findViewById(R.id.imageview_left);
        right_imageview = (ImageView) findViewById(R.id.imageview_right);

        // Setting up the variable to control joystick
        start_joystick = false;

        // Setting up the intent filter for handling the game-pad buttons
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        mIntentFilter.setPriority(999999999);

        registerReceiver(mReceiver, mIntentFilter);

        startHessTest();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //region Code for initialising the Hess testing
    private void startHessTest(){
        //region Setting up the left Image View
        left_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
        left_canvas = new Canvas(left_imageBitmap);

        left_paint = new Paint();
        left_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        left_paint.setColor(getResources().getColor(R.color.red_color));

        left_canvas.drawCircle(resolution_value / 2, (resolution_value + correction_value) / 2, circle_radius, left_paint);
        left_imageview.setImageBitmap(left_imageBitmap);
        //endregion

        //region Setting up the right Image View
        right_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
        right_canvas = new Canvas(right_imageBitmap);

        right_paint = new Paint();
        right_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        right_paint.setColor(getResources().getColor(R.color.green_color));
        //endregion

        showTestPoint();
        start_joystick = true;
    }
    //endregion

    //region Code for handling the canvas according to joystick movements
    private void movePointer(String direction) {

        // Check if the canvas variables have been initialised
        if(start_joystick) {
            if(direction.equals("UP")) {
                left_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
                left_canvas = new Canvas(left_imageBitmap);

                pointer_location_y -= marker_move;

                left_canvas.drawCircle(pointer_location_x, pointer_location_y, circle_radius, left_paint);
                left_imageview.setImageBitmap(left_imageBitmap);
            }

            else if(direction.equals("DOWN")) {
                left_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
                left_canvas = new Canvas(left_imageBitmap);

                pointer_location_y += marker_move;

                left_canvas.drawCircle(pointer_location_x, pointer_location_y, circle_radius, left_paint);
                left_imageview.setImageBitmap(left_imageBitmap);
            }

            else if(direction.equals("LEFT")) {
                left_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
                left_canvas = new Canvas(left_imageBitmap);

                pointer_location_x -= marker_move;

                left_canvas.drawCircle(pointer_location_x, pointer_location_y, circle_radius, left_paint);
                left_imageview.setImageBitmap(left_imageBitmap);
            }

            else if(direction.equals("RIGHT")) {
                left_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
                left_canvas = new Canvas(left_imageBitmap);

                pointer_location_x += marker_move;

                left_canvas.drawCircle(pointer_location_x, pointer_location_y, circle_radius, left_paint);
                left_imageview.setImageBitmap(left_imageBitmap);
            }
        }

        if(reference_image_displayed){
            left_imageview.setBackgroundResource(R.drawable.pincushion_distortion);
        }

        else{
            left_imageview.setBackgroundResource(0);
        }
    }
    //endregion

    //region Code for handling the showing of test points
    private void showTestPoint(){
        right_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value + correction_value, Bitmap.Config.ARGB_8888);
        right_canvas = new Canvas(right_imageBitmap);

        right_canvas.drawCircle(test_x_coordinates[test_point_index], test_y_coordinates[test_point_index], circle_radius, right_paint);
        right_imageview.setImageBitmap(right_imageBitmap);

        if(reference_image_displayed){
            right_imageview.setBackgroundResource(R.drawable.pincushion_distortion);
        }

        else{
            right_imageview.setBackgroundResource(0);
        }

        // Storing the detected values in an array
        if(test_point_index > 0) {
            detected_locations[0][test_point_index - 1] = pointer_location_x;
            detected_locations[1][test_point_index - 1] = pointer_location_y;
        }

        test_point_index++;
    }
    //endregion

    //region Code for displaying the reference images (Pincushion Distortion Image)
    private void displayReferenceImage(){
        if(!reference_image_displayed){
            left_imageview.setBackgroundResource(R.drawable.pincushion_distortion);
            right_imageview.setBackgroundResource(R.drawable.pincushion_distortion);

            reference_image_displayed = true;
        }

        else{
            left_imageview.setImageBitmap(left_imageBitmap);
            right_imageview.setImageBitmap(right_imageBitmap);

            left_imageview.setBackgroundResource(0);
            right_imageview.setBackgroundResource(0);

            reference_image_displayed = false;
        }
    }
    //endregion

    //region Code for setting up the BroadcastReceiver and the gamepad buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 97){
            Log.i("custom", "Button 2");

            if(test_point_index > 8){
                detected_locations[0][8] = pointer_location_x;
                detected_locations[1][8] = pointer_location_y;

                for (int index = 0; index < detected_locations[0].length; index++) {
                    Log.i("custom", "\nPoint " + (index + 1) + " - " + detected_locations[0][index]);
                    Log.i("custom", "Point " + (index + 1) + " - " + detected_locations[1][index]);
                }

                // Initialising the intent and bundling the data
                Intent temp = new Intent(TestingActivity.this, TestReportActivity.class);

                int array_test_x_cords[] = test_x_coordinates;
                int array_test_y_cords[] = test_y_coordinates;
                int array_generated_x_cords[] = detected_locations[0];
                int array_generated_y_cords[] = detected_locations[1];

                temp.putExtra("test_x_coordinates", array_test_x_cords);
                temp.putExtra("test_y_coordinates", array_test_y_cords);
                temp.putExtra("x_detected_locations", array_generated_x_cords);
                temp.putExtra("y_detected_locations", array_generated_y_cords);

                startActivity(temp);
                finish();
            }

            else {
                showTestPoint();
            }
        }

        else if(keyCode == 96){
            Log.i("custom", "Button 1");
        }

        else if(keyCode == 24){
            Log.i("custom", "Joystick is up");
            movePointer("UP");
        }

        else if(keyCode == 25){
            Log.i("custom", "Joystick is down");
            movePointer("DOWN");
        }

        else if(keyCode == 21 || keyCode == 88){
            Log.i("custom", "Joystick is left");
            movePointer("LEFT");
        }

        else if(keyCode == 22 || keyCode == 87){
            Log.i("custom", "Joystick is right");
            movePointer("RIGHT");
        }

        else if(keyCode == 85) {
            Log.i("custom", "Button A");
            displayReferenceImage();
        }

        return true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                return;
            }
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }
            int action = event.getAction();

            if (action == KeyEvent.ACTION_DOWN) {

            }
            abortBroadcast();

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }
    //endregion

    //region Code for handling the fullscreen UI
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    //endregion
}
