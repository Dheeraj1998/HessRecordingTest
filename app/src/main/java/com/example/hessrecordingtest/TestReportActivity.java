package com.example.hessrecordingtest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class TestReportActivity extends AppCompatActivity {

    //region Global variables
    private int resolution_value = 1200;
    private int circle_radius = 15;

    private ImageView report_imageView;
    private Canvas report_canvas;
    private Paint report_paint;
    private Bitmap report_imageBitmap;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);

        report_imageView = (ImageView) findViewById(R.id.imageview_testreport);

        // Getting the data from the previous intent
        int[] test_x_coordinates = getIntent().getIntArrayExtra("test_x_coordinates");
        int[] test_y_coordinates = getIntent().getIntArrayExtra("test_y_coordinates");

        int[] x_detected_location = getIntent().getIntArrayExtra("x_detected_locations");
        int[] y_detected_location = getIntent().getIntArrayExtra("y_detected_locations");

        generateReport(x_detected_location, y_detected_location, test_x_coordinates, test_y_coordinates);
    }

    private void generateReport(int[] x_detected_location, int[] y_detected_location, int[] test_x_coordinates, int[] test_y_coordinates){

        //region Marking the points selected by the user
        report_imageBitmap = Bitmap.createBitmap(resolution_value, resolution_value, Bitmap.Config.ARGB_8888);
        report_canvas = new Canvas(report_imageBitmap);

        report_paint = new Paint();
        report_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        report_paint.setColor(getResources().getColor(R.color.red_color));

        for(int index = 0; index < x_detected_location.length; index++){
            report_canvas.drawCircle(x_detected_location[index], y_detected_location[index], circle_radius, report_paint);
        }
        report_imageView.setImageBitmap(report_imageBitmap);
        //endregion

        //region Marking the points in the original test
        report_paint = new Paint();
        report_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        report_paint.setColor(getResources().getColor(R.color.green_color));

        for(int index = 0; index < test_x_coordinates.length; index++){
            report_canvas.drawCircle(test_x_coordinates[index], test_y_coordinates[index], circle_radius, report_paint);
        }
        report_imageView.setImageBitmap(report_imageBitmap);
        //endregion

    }
}
