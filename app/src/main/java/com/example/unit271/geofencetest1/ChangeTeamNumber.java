package com.example.unit271.geofencetest1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ChangeTeamNumber extends AppCompatActivity {

    public static String filename = "NumberHolder";
    SharedPreferences teamNumData;
    private String teamID;
    private String selection;
    private CheckBox cb7;
    private CheckBox cb6;
    private CheckBox cb1;
    ArrayList<String> teamNameList2;
    Boolean free1;
    Boolean free6;
    Boolean free7;
    String schoolName;
    RadioButton rb;
    Firebase dataRef5, dataRef6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_team_num);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Firebase.setAndroidContext(this);
        setTitle("Setup");
        teamNameList2 = new ArrayList<String>();
        teamNameList2.clear();
        dataRef5 = new Firebase("https://loginapptestcc.firebaseio.com/");
        dataRef6 = dataRef5.child("People");

        dataRef6.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot personSnapshot: dataSnapshot.getChildren()) {
                    teamNameList2.add(personSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        cb1 = (CheckBox) findViewById(R.id.checkPer1);
        cb6 = (CheckBox) findViewById(R.id.checkPer6);
        cb7 = (CheckBox) findViewById(R.id.checkPer7);

        teamNumData = getSharedPreferences(filename, 0);
        teamID = teamNumData.getString("newIDKey", "NONE");
        free1 = teamNumData.getBoolean("free1", false);
        free6 = teamNumData.getBoolean("free6", false);
        free7 = teamNumData.getBoolean("free7", false);
        schoolName = teamNumData.getString("schoolName", "none");

        cb1.setChecked(free1);
        cb6.setChecked(free6);
        cb7.setChecked(free7);

        if(schoolName.equals("Holmes")){
            rb = (RadioButton) findViewById(R.id.radioHolJS);
            rb.setChecked(true);
        }else if(schoolName.equals("DHS")){
            rb = (RadioButton) findViewById(R.id.radioDHS);
            rb.setChecked(true);
        }else if(schoolName.equals("Harper")){
            rb = (RadioButton) findViewById(R.id.radioHarJS);
            rb.setChecked(true);
        }else if(schoolName.equals("Emerson")){
            rb = (RadioButton) findViewById(R.id.radioEmersonJS);
            rb.setChecked(true);
        }else if(schoolName.equals("DaVinci JrHigh")){
            rb = (RadioButton) findViewById(R.id.radioDavinciJS);
            rb.setChecked(true);
        } else if(schoolName.equals("DaVinci High")){
            rb = (RadioButton) findViewById(R.id.radioDavinciHS);
            rb.setChecked(true);
        } else if(schoolName.equals("none")){
        }

        if(!teamID.equals("NONE")){
            EditText et = (EditText) findViewById(R.id.newIdEdit);
            et.setText(teamID);
        }

    }

    public void onSaveClick(View view){
        SharedPreferences.Editor editor = teamNumData.edit();
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        boolean newPersonFirebase = true;
        if(rGroup.getCheckedRadioButtonId() != -1){
            int id = rGroup.getCheckedRadioButtonId();
            View radioButton = rGroup.findViewById(id);
            int radioId = rGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) rGroup.getChildAt(radioId);
            selection = (String) btn.getText();
            editor.putString("schoolName", selection);
        }
        editor.putBoolean("free1", cb1.isChecked());
        editor.putBoolean("free6", cb6.isChecked());
        editor.putBoolean("free7", cb7.isChecked());

        EditText newIdText = (EditText) findViewById(R.id.newIdEdit);
        String changeToString = newIdText.getText().toString();
        if(changeToString.length() > 0){
            if(!teamNumData.getBoolean("setupComplete", false)) {
                editor.putBoolean("setupComplete", true);
            }
            for (int a = 0; a <= teamNameList2.size() - 1; a++) {
                if (changeToString.equals(teamNameList2.get(a))) {
                    newPersonFirebase = false;
                }
            }
            if(newPersonFirebase){
                dataRef5.child("People").child(changeToString).child("CurrentlySignedInRobotics").setValue(false);
                dataRef5.child("People").child(changeToString).child("CurrentlySignedInFM").setValue(false);
                dataRef5.child("People").child(changeToString).child("CurrentlySignedInCompetition").setValue(false);
                dataRef5.child("People").child(changeToString).child("SubtractHoursRobotics").setValue(0);
                dataRef5.child("People").child(changeToString).child("SubtractHoursCompetition").setValue(0);
                dataRef5.child("People").child(changeToString).child("SubtractHoursFM").setValue(0);
            }
            editor.putString("newIDKey", changeToString);
            editor.commit();
            Toast.makeText(getApplicationContext(), ("New Profile : " + changeToString),
                    Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent(this, MainActivity.class);
            startActivity(returnIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Entry",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
