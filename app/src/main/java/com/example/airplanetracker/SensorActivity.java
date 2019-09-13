package com.example.airplanetracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.w3c.dom.Text;

public class SensorActivity extends Activity implements SensorEventListener {
    TextView txt2;
    Button button1;
    private SensorManager mSensorManager;
    int azimuth;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        txt2 = (TextView)findViewById(R.id.text2x);
        button1 = (Button)findViewById(R.id.button);

        Log.d("", "onCreate: RAN SENSORACTIVITY");
        start();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.i("SENSORCHANGED!!", ""+ sensorEvent.values[0]);
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
        txt2.setText("" + azimuth);
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    //nothing
    }




    public void start(){
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
                noSensorAlert();
            }else{
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


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
}
