package com.example.unit271.geofencetest1;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

    SharedPreferences teamNumData;
    private int teamNumber;
    private TextView numView;
    public boolean buttonPermission;
    public boolean startPerm;
//    public TextView rangeView;
    public static String filename = "NumberHolder";
//    public double startLat = 38.556427;
//    public double startLong = -121.751636;
    public boolean allowService;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        teamNumData = getSharedPreferences(filename, 0);

        numView = (TextView) findViewById(R.id.teamNumView2);
        if(!teamNumData.getBoolean("setupComplete", false)) {
            Intent numChange = new Intent(this, ChangeTeamNumber.class);
            startActivity(numChange);
        }

        teamNumber = teamNumData.getInt("newNumKey", 000);
        if(teamNumber == 0){
            startPerm = false;
            numView.setText("No Number");
            numView.setTextSize(30);
        } else {
            startPerm = true;
            numView.setText(teamNumber + "");
            numView.setTextSize(30);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 167801);
            buttonPermission = false;
            return;
        } else {
            buttonPermission = true;
        }

        if(startPerm && buttonPermission){
            startService();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case 167801 :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    buttonPermission = true;
                    if(startPerm){
                        startService();
                    }
                }
        }
    }


    public Intent startService() {
        Toast.makeText(getApplicationContext(), "Tracking Initialized.",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, TrackingService.class);
        startService(i);
        Log.i("INITIALIZATION", "SERVICE STARTED");
        return i;
    }

    public Intent stopService() {
        Intent i = new Intent(this, TrackingService.class);
        stopService(i);
        Log.i("INITIALIZATION", "SERVICE STOPPED");
        return i;
    }

    public void changeNumber(View view){
            Intent numChange = new Intent(this, ChangeTeamNumber.class);
            startActivity(numChange);
    }

    public void signInOther(){
        Intent signOther = new Intent(this, signInOther.class);
        startActivity(signOther);
    }
}
