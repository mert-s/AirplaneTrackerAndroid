package com.example.airplanetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    int x1 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AccessInternetTask().execute("test");

        final Button button = findViewById(R.id.button);

    }

    private SensorManager sensorManager;

    public void sensorTest(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        // Rotation matrix based on current readings from accelerometer and magnetometer.
//        final float[] rotationMatrix = new float[9];
//        SensorManager.getRotationMatrix(rotationMatrix, null,
//                accelerometerReading, magnetometerReading);
//
//
//        // Express the updated rotation matrix as three orientation angles.
//        final float[] orientationAngles = new float[3];
//        SensorManager.getOrientation(rotationMatrix, orientationAngles);
    }


}

class AccessInternetTask extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... strings) {
        try{
            AirTrackerModel t = new AirTrackerModel();
            Log.d("", "onCreate: " + t.printTest());
        } catch(IOException ex){

        }
        return null;
    }
}
//
//
//
//
//
//
//public class SensorActivity extends Activity implements SensorEventListener {
//    private SensorManager mSensorManager;
//    private Sensor mSensor;
//    ImageView iv;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sensor_screen);
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//    }
//
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, mSensor,
//                SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(this);
//    }
//
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//    }
//
//    public void onSensorChanged(SensorEvent event) {
//        if (event.values[0] == 0) {
//            iv.setImageResource(R.drawable.near);
//        } else {
//            iv.setImageResource(R.drawable.far);
//        }
//    }
//}