package com.example.unit271.geofencetest1;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by unit271 on 3/19/16.
 */
public class TrackingService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback {
    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    public boolean timerNotificationState;
    public int normStart, normEnd, wedStart, wedEnd, thursStart, thursEnd;
    public boolean nPer1, nPer6, nPer7;
    CountDownTimer dTimer1;
    int numDataReturned;
    String weekDay;
    final Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
    HashMap school;
    SharedPreferences teamNumData;
    public static String filename = "NumberHolder";
    public boolean isRunning;
    final NotificationCompat.Builder loginBuilder = new NotificationCompat.Builder(this);
    final NotificationCompat.Builder logoutBuilder = new NotificationCompat.Builder(this);
    private String schoolName;
    public boolean rangeCheckerA;

    @Override
    public void onCreate() {
        Log.i("SERVICE1", "Service onCreate");
        super.onCreate();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Firebase.setAndroidContext(this);
        Log.i("SERVICE1", "Service onStartCommand");
        isRunning = false;
        teamNumData = getSharedPreferences(filename, 0);
        numDataReturned = teamNumData.getInt("newNumKey", 000);
        personListener();

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Constants constInstance = new Constants();
        constInstance.initializeMaps();
        schoolName = teamNumData.getString("schoolName", "NONE");
        school = constInstance.getSchool(schoolName);
        nPer1 = teamNumData.getBoolean("free1", false);
        nPer6 = teamNumData.getBoolean("free6", false);
        nPer7 = teamNumData.getBoolean("free7", false);

        if(nPer1){
            normStart = (Integer) school.get("N1");
            wedStart = (Integer) school.get("WedN1");
            thursStart = (Integer) school.get("ThuN1");
        } else {
            normStart = (Integer) school.get("AStart");
            wedStart = (Integer) school.get("WedAStart");
            thursStart = (Integer) school.get("ThuAStart");
        }
        if(nPer6 && nPer7){
            normEnd = (Integer) school.get("N6N7");
            wedEnd = (Integer) school.get("WedN6N7");
            thursEnd = (Integer) school.get("ThuN6N7");
        } else if(!nPer6 && nPer7) {
            normEnd = (Integer) school.get("N7");
            wedEnd = (Integer) school.get("wedN7");
            thursEnd = (Integer) school.get("ThuN7");
        } else {
            normEnd = (Integer) school.get("AEnd");
            wedEnd = (Integer) school.get("WedAEnd");
            thursEnd = (Integer) school.get("ThuAEnd");
        }


        loginBuilder.setContentTitle("1678 Login");
        loginBuilder.setContentText(numDataReturned + ", you have been signed in to Citrus Circuits");
        loginBuilder.setContentIntent(resultPendingIntent);
        loginBuilder.setSmallIcon(R.drawable.check);
        loginBuilder.setSound(alarmSound);
        loginBuilder.setLights(Color.GREEN, 500, 1000);

        logoutBuilder.setContentTitle("1678 Logout");
        logoutBuilder.setContentText(numDataReturned + ", you have been signed out of Citrus Circuits");
        logoutBuilder.setContentIntent(resultPendingIntent);
        logoutBuilder.setSmallIcon(R.drawable.doublecheck);
        logoutBuilder.setSound(alarmSound);
        logoutBuilder.setLights(Color.GREEN, 500, 1000);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        dTimer1 = new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("onTick", String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                for(int a = 0; a <= 7; a++)
                    isRunning = false;
                    Log.i("onFinish", "sendToForm");
                sendToForm();
                if(timerNotificationState){
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, loginBuilder.build());
                } else {
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, logoutBuilder.build());
                }
            }
        };

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Log.i("LOGS1", "GOOGLEAPICLIENTISNULL");
            mGoogleApiClient.connect();
        }


        return Service.START_STICKY;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation != null) {
//            latView.setText(mLastLocation.getLatitude() + "");
//            latView.setTextSize(20);
//            longView.setText(mLastLocation.getLongitude() + "");
//            longView.setTextSize(20);
        }
        createLocationRequest();

        Log.i("MAINACTIVTY", "onConnectedComplete");
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = mCurrentLocation;
        mCurrentLocation = location;

        double locLatitude = location.getLatitude();
        double locLongitude = location.getLongitude();

        weekDay = dayFormat.format(calendar.getTime());
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat hourFormatter = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormatter = new SimpleDateFormat("mm");
        String hourString = hourFormatter.format(date);
        String minuteString = minuteFormatter.format(date);
        int hourTimeInt = Integer.parseInt(hourString);
        int minuteTimeInt = Integer.parseInt(minuteString);
        int currentTime = (hourTimeInt * 100) + minuteTimeInt;

        if (!rangeCheckerA && (locLatitude >= 38.55608987 && locLatitude <= 38.557) && (locLongitude >= -121.7522 && locLongitude <= -121.75105237)) {
            if (weekDay.equals("Wednesday")) {
                if ((currentTime < wedStart) || (currentTime > wedEnd)) {
                    Log.i("RANGE", "WITHIN RANGE");
                    SharedPreferences.Editor editor = teamNumData.edit();
                    editor.putBoolean("rangeBoolean", true);
                    editor.commit();
                    timerNotificationState = true;
                    if (isRunning) {
                        Log.i("DTIMER", "CANCELLING");
                        dTimer1.cancel();
                        isRunning = false;
                    } else {
                        Log.i("DTIMER", "STARTING");
                        dTimer1.start();
                        isRunning = true;
                    }

                }
            } else if (weekDay.equals("Thursday")) {
                if ((currentTime < thursStart) || (currentTime > thursEnd)) {
                    Log.i("RANGE", "WITHIN RANGE");
                    SharedPreferences.Editor editor = teamNumData.edit();
                    editor.putBoolean("rangeBoolean", true);
                    editor.commit();
                    timerNotificationState = true;
                    if (isRunning) {
                        Log.i("DTIMER", "CANCELLING");
                        dTimer1.cancel();
                        isRunning = false;
                    } else {
                        Log.i("DTIMER", "STARTING");
                        dTimer1.start();
                        isRunning = true;
                    }

                }
            } else if (weekDay.equals("Saturday") || weekDay.equals("Sunday")) {
                Log.i("RANGE", "WITHIN RANGE");
                SharedPreferences.Editor editor = teamNumData.edit();
                editor.putBoolean("rangeBoolean", true);
                editor.commit();
                timerNotificationState = true;
                if(isRunning) {
                    Log.i("DTIMER", "CANCELLING");
                    dTimer1.cancel();
                    isRunning = false;
                } else {
                    Log.i("DTIMER", "STARTING");
                    dTimer1.start();
                    isRunning = true;
                }
            } else {
                if ((currentTime < normStart) || (currentTime > normEnd)) {
                    Log.i("RANGE", "WITHIN RANGE");
                    SharedPreferences.Editor editor = teamNumData.edit();
                    editor.putBoolean("rangeBoolean", true);
                    editor.commit();
                    timerNotificationState = true;
                    if (isRunning) {
                        Log.i("DTIMER", "CANCELLING");
                        dTimer1.cancel();
                        isRunning = false;
                    } else {
                        Log.i("DTIMER", "STARTING");
                        dTimer1.start();
                        isRunning = true;
                    }

                }
            }
        } else if (rangeCheckerA && ((locLatitude < 38.55608987 || locLatitude > 38.557) || (locLongitude < -121.7522 || locLongitude > -121.75105237))) {
            Log.i("RANGE", "OUTSIDE RANGE");
            timerNotificationState = false;

            SharedPreferences.Editor editor = teamNumData.edit();
            editor.putBoolean("rangeBoolean", false);
            editor.commit();
            if(isRunning) {
                Log.i("DTIMER", "CANCELLING");
                dTimer1.cancel();
                isRunning = false;
            } else {
                Log.i("DTIMER", "STARTING");
                dTimer1.start();
                isRunning = true;
            }
        }
    }

    public void sendToForm(){
        final int tmpTeamNumber = numDataReturned;
        final boolean tmpRangeCheckerA = rangeCheckerA;
        //TODO : Create team number from shared preferences
        new Thread() {
            @Override
            public void run() {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat uploadFormatter = new SimpleDateFormat("yyyy-MM-dd-HHmm", Locale.US);
                String uploadDate = uploadFormatter.format(date);

                Firebase dataRef = new Firebase("https://cc-sign-in-out-app.firebaseio.com/#");
                Firebase personRef = dataRef.child(String.valueOf(tmpTeamNumber));
                if(tmpRangeCheckerA){
                    personRef.setValue("CurrentlySignedIn", false);
                    personRef.child(uploadDate).setValue("Out");
                } else {
                    personRef.setValue("CurrentlySignedIn", true);
                    personRef.child(uploadDate).setValue("In");
                }
            }
        }.start();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.i("GEOINFO", "ONRESULT: ");
    }

    public void personListener(){
        Firebase dataRef = new Firebase("https://cc-sign-in-out-app.firebaseio.com/#");
        Firebase personRef = dataRef.child(String.valueOf(numDataReturned)).child("CurrentlySignedIn");
        personRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    rangeCheckerA = (Boolean) dataSnapshot.getValue();
                } else {
                    rangeCheckerA = false; //Works because it should always sign in after first use
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}


