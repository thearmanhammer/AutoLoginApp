package com.example.unit271.geofencetest1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class decreaseHours extends AppCompatActivity {

    String teamID;
    EditText decreaseHoursText, decreaseHoursTextFM, decreaseHoursTextCompetition;
    Button decreaseHoursButton;
    Firebase dataRef6;
    int SubtractHoursRobotics, SubtractHoursFM, SubtractHoursCompetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrease_hours);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Firebase.setAndroidContext(this);
        teamID = getIntent().getStringExtra("com.example.unit271.geofencetest1/MainActivity");
        dataRef6 = new Firebase("https://loginapptestcc.firebaseio.com/People/" + teamID);
        SubtractHoursRobotics = 0;
        SubtractHoursCompetition = 0;
        SubtractHoursFM = 0;
        decreaseHoursText = (EditText) findViewById(R.id.hoursEditText);
        decreaseHoursTextFM = (EditText) findViewById(R.id.hoursEditTextFM);
        decreaseHoursTextCompetition = (EditText) findViewById(R.id.hoursEditTextCompetition);
        decreaseHoursButton = (Button) findViewById(R.id.decreaseHoursButton);
        decreaseHoursButton.setEnabled(false);
        dataRef6.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot infoSnapshot2: dataSnapshot.getChildren()) {
                    if(infoSnapshot2.getKey().equals("SubtractHoursRobotics")){
                        SubtractHoursRobotics = ((Long) infoSnapshot2.getValue()).intValue();
                    }
                    if(infoSnapshot2.getKey().equals("SubtractHoursFM")){
                        SubtractHoursFM = ((Long) infoSnapshot2.getValue()).intValue();
                    }
                    if(infoSnapshot2.getKey().equals("SubtractHoursCompetition")){
                        SubtractHoursCompetition = ((Long) infoSnapshot2.getValue()).intValue();
                    }
                }
                decreaseHoursButton.setEnabled(true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void onDecreaseHoursClick(View view){
        if(!String.valueOf(decreaseHoursText.getText()).equals("")) {
            dataRef6.child("SubtractHoursRobotics").setValue(SubtractHoursRobotics + Integer.parseInt(String.valueOf(decreaseHoursText.getText())));
        } else {
            dataRef6.child("SubtractHoursRobotics").setValue(SubtractHoursRobotics);
        }
        if(!String.valueOf(decreaseHoursTextFM.getText()).equals("")) {
            dataRef6.child("SubtractHoursFM").setValue(SubtractHoursFM + Integer.parseInt(String.valueOf(decreaseHoursTextFM.getText())));
        } else {
            dataRef6.child("SubtractHoursFM").setValue(SubtractHoursFM);
        }
        if(!String.valueOf(decreaseHoursTextCompetition.getText()).equals("")) {
            dataRef6.child("SubtractHoursCompetition").setValue(SubtractHoursCompetition + Integer.parseInt(String.valueOf(decreaseHoursTextCompetition.getText())));
        } else {
            dataRef6.child("SubtractHoursCompetition").setValue(SubtractHoursCompetition);
        }
        Intent returnIntent = new Intent(this, MainActivity.class);
        startActivity(returnIntent);
    }
}
