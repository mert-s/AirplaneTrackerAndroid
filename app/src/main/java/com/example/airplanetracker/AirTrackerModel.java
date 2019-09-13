package com.example.airplanetracker;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.Math;
import java.util.Iterator;

import openskyapi.java.src.main.java.org.opensky.api.OpenSkyApi;
import openskyapi.java.src.main.java.org.opensky.model.OpenSkyStates;
import openskyapi.java.src.main.java.org.opensky.model.StateVector;


public class AirTrackerModel {
    OpenSkyStates s;
    double userLat, userLon;
    private double distanceSetting = 0.5;


    AirTrackerModel(double userLat, double userLon) throws IOException {
        this.userLat = userLat;
        this.userLon = userLon;
        OpenSkyApi api = new OpenSkyApi();
        s = api.getStates(0, null,
                new OpenSkyApi.BoundingBox(userLat - distanceSetting, userLat + distanceSetting, userLon - distanceSetting, userLon + distanceSetting));
        //System.out.println(s.getStates().size());
    }

    public Collection<StateVector> getNearbyPlanes(){
        return s.getStates();
    }

    private int calcAngle(double planeLat, double planeLon){
        double ang = Math.atan2(planeLon - userLon, planeLat - userLat);

        Log.d("", "calcAngle: " + planeLon + ", " + userLon +", " + planeLat + ", " + userLat);
        ang = Math.toDegrees(ang);

        if(ang < 0){
            ang += 360;
        }

        Log.d("", "calcAngle: " + ang);
        return (int)ang;
    }

    public String printTest(){
        return "" + s.getStates().size();
    }

    public double getDistanceSetting(){
        return distanceSetting;
    }



    public ArrayList<StateVector> getPlanesNearBearing(int bearing){
        Collection<StateVector> planes  = getNearbyPlanes();
        Iterator<StateVector> iterator = planes.iterator();

        ArrayList<StateVector> list = new ArrayList<StateVector>();


        StateVector state = null;
        int angle = 0;

        while(iterator.hasNext()){
            state = iterator.next();
            angle = calcAngle(state.getLatitude(), state.getLongitude());
            if(Math.abs(angle - bearing) < 30){
                list.add(state);
            }
        }


        return list;
    }
}
