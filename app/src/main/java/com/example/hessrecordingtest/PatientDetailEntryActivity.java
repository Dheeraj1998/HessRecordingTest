package com.example.hessrecordingtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PatientDetailEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail_entry);
    }

    public void startTestActivity(View view) {
        Intent temp = new Intent(PatientDetailEntryActivity.this, TestingActivity.class);
        startActivity(temp);
    }
}
