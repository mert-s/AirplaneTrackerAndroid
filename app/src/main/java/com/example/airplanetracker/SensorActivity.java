package com.example.airplanetracker;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class SensorActivity extends Activity implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i("SENSORCHANGED!!", ""+ sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    //nothing
    }
}
