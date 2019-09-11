package com.example.airplanetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    int x1 = 0;
    private final int REQUEST_PERMISSION_FINE_LOCATION=1;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=1;

    private TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Location userLocation = getLocation();
        t1 = (TextView)findViewById(R.id.text1x);


        AsyncTask trackerModel = new AccessInternetTask().execute(new Pair(userLocation.getLatitude(), userLocation.getLongitude())); //user latitude and longitude


        final Button button = findViewById(R.id.button);

    }

    private SensorManager sensorManager;

    public void postexec(AirTrackerModel t) {
        if(t != null){
            int numOfNearbyPlanes = t.getNearbyPlanes().size();

            t1.setText("" + numOfNearbyPlanes);
            Log.d("", "postexec: EXECUTED POSTEXEC!!!!!!!!!!!!!!" + numOfNearbyPlanes);
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
        }

        return loc;
    }


    class AccessInternetTask extends AsyncTask<Pair<Double, Double>, Void, AirTrackerModel>{

        private AirTrackerModel t;
        @Override
        protected AirTrackerModel doInBackground(Pair<Double, Double>... pairs) {

            try{
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
            postexec(t);
        }
    }

}
