package com.example.airplanetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import openskyapi.java.src.main.java.org.opensky.model.StateVector;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    int x1 = 0;
    private final int REQUEST_PERMISSION_FINE_LOCATION=1;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=1;

    private TextView t1;
    private TextView txt2;
    private TextView txt3;


    private SensorManager mSensorManager;
    int azimuth = 0;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private AirTrackerModel airModel;
    private Location userLocation;
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1 = (TextView)findViewById(R.id.text1x);
        txt2 = (TextView)findViewById(R.id.text2x);
        txt3 = (TextView)findViewById(R.id.text3x);
        userLocation = getLocation();

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);


        /*AsyncTask trackerModel = */ //new AccessInternetTask().execute(new Pair(userLocation.getLatitude(), userLocation.getLongitude())); //user latitude and longitude


        Button button = findViewById(R.id.button);

        layout = findViewById(R.id.linearlayout);

        TextView newView = new TextView(this);
        newView.setText("TEST");
        newView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(newView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
                if(airModel != null){
                    ArrayList<StateVector> planes = airModel.getPlanesNearBearing(azimuth);
                    layout.removeAllViews();
                    for(int i = 0; i < planes.size(); i++){
                        createText(planes.get(i));
                    }
                    if(planes.size()>0){
                        t1.setText("Number of airplanes near the bearing: " + azimuth + " is " + planes.size() + "-- includes: " + planes.get(0).getCallsign());
                    }
                }
            }
        });

/*
        Button button2 = findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });*/


        start();
        //new SensorActivity();
    }

    public void createText(StateVector plane){
        TextView callsignV = new TextView(this);
        callsignV.setText("Callsign: " + plane.getCallsign());
        TextView airlineV = new TextView(this);
        airlineV.setText("Airline: " + Airline.CallsignToAirline(plane.getCallsign()));
        TextView countryV = new TextView(this);
        countryV.setText("Registered country: " + plane.getOriginCountry());
        TextView icao24V = new TextView(this);
        icao24V.setText("ICAO24: " + plane.getIcao24());
        TextView angleV = new TextView(this);
        angleV.setText("Current relative bearing: " + airModel.calcAngle(plane.getLatitude(), plane.getLongitude()));

        layout.addView(callsignV);
        layout.addView(airlineV);
        layout.addView(countryV);
        layout.addView(icao24V);

        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        v.setBackgroundColor(Color.parseColor("#000000"));
        layout.addView(v);
    }

    public void refreshData(){
        if(userLocation != null){
            new AccessInternetTask().execute(new Pair<Double, Double>(userLocation.getLatitude(), userLocation.getLongitude()));
            try{
                Thread.sleep(200);
            }catch (InterruptedException e){}
        }
    }

    //private SensorManager sensorManager;

    public void postexec(AirTrackerModel t) {
        if(t != null){
            this.airModel = t;
            int numOfNearbyPlanes = t.getNearbyPlanes().size();
            t.getPlanesNearBearing(azimuth);

            //t1.setText("number of nearby airplanes within ~" + t.getDistanceSetting()*100 +  "km: " + numOfNearbyPlanes);
            Log.d("", "postexec: EXECUTED POSTEXEC!!!!!!!!!!!!!!" + numOfNearbyPlanes);
        }else{
            Log.d("", "postexec: t is null???");
        }
    }


    private Location getLocation() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = null;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_FINE_LOCATION);

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_COARSE_LOCATION);
        }

        if(lm != null){
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            txt3.setText("User latitude obtained: " + loc.getLatitude() + ", User longitude obtained: " + loc.getLongitude());
        }

        return loc;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            azimuth = (int)(Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]+360)%360);
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
            mLastAccelerometerSet = true;
        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }

        if(mLastAccelerometerSet && mLastMagnetometerSet){
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            azimuth = (int)(Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]+360)%360);
        }

        azimuth = Math.round(azimuth);
        txt2.setText("Current bearing " + azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void start(){
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
                noSensorAlert();
            }else{
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);

            }
        }else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);

        }
    }

    public void stop(){
        if(haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        }else{
            if(haveSensor){
                mSensorManager.unregisterListener(this, mRotationV);
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        start();
    }


    public void noSensorAlert(){
        AlertDialog.Builder alertDialer = new AlertDialog.Builder(this);
        alertDialer.setMessage("Your device is not supported.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
    }


    class AccessInternetTask extends AsyncTask<Pair<Double, Double>, Void, AirTrackerModel>{

        private AirTrackerModel t;
        private boolean finished = false;

        @Override
        protected AirTrackerModel doInBackground(Pair<Double, Double>... pairs) {
            try{
                Log.d("", "doInBackground: pairs[0].first: " + pairs[0].first + ", pairs[0].second" + pairs[0].second);
                t = new AirTrackerModel(pairs[0].first, pairs[0].second);
                Log.d("", "onCreate: " + t.printTest());
                return t;
            } catch(IOException ex){

            }

            return null;
        }


        @Override
        protected void onPostExecute(AirTrackerModel t){
            t = this.t;
            finished = true;
            postexec(t);
        }

        public boolean isFinished(){
            return finished;
        }
    }

}
