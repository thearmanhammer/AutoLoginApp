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

public class ChangeTeamNumber extends AppCompatActivity {

    public static String filename = "NumberHolder";
    SharedPreferences teamNumData;
    private int teamNumber;
    private String selection;
    private CheckBox cb7;
    private CheckBox cb6;
    private CheckBox cb1;
    Boolean free1;
    Boolean free6;
    Boolean free7;
    String schoolName;
    RadioButton rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_team_num);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Setup");

        cb1 = (CheckBox) findViewById(R.id.checkPer1);
        cb6 = (CheckBox) findViewById(R.id.checkPer6);
        cb7 = (CheckBox) findViewById(R.id.checkPer7);

        teamNumData = getSharedPreferences(filename, 0);
        teamNumber = teamNumData.getInt("newNumKey", 000);
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

        if(teamNumber != 000){
            EditText et = (EditText) findViewById(R.id.newIdEdit);
            et.setText(String.valueOf(teamNumber));
        }

    }

    public void onSaveClick(View view){
        SharedPreferences.Editor editor = teamNumData.edit();
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.radioGroup1);
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
            int changeToNumber = Integer.parseInt(changeToString);
            if(changeToNumber != 0){
               editor.putInt("newNumKey", changeToNumber);
                if(!teamNumData.getBoolean("setupComplete", false)) {
                    editor.putBoolean("setupComplete", true);
                }
                editor.commit();
                Toast.makeText(getApplicationContext(), ("New ID : " + changeToNumber),
                        Toast.LENGTH_SHORT).show();

                Intent returnIntent = new Intent(this, MainActivity.class);
                startActivity(returnIntent);

            } else {
                Toast.makeText(getApplicationContext(), "Invalid Entry",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Entry",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
