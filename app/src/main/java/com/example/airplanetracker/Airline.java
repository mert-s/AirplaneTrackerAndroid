package com.example.airplanetracker;

public class Airline {
    public static String CallsignToAirline(String callsign){
        String toReturn = "Unknown";
        if(callsign.length() > 3) {
            switch (callsign.substring(0, 3)) {
                case "RYR":
                    toReturn = "RyanAir";
                    break;
                case "EZY":
                    toReturn = "EasyJet";
                    break;
                case "DLH":
                    toReturn = "Lufthansa";
                    break;
                case "BAW":
                    toReturn = "British Airways";
                    break;
                case "BEL":
                    toReturn = "Brussels Airlines";
                    break;
                case "WZZ":
                    toReturn = "WizzAir";
                    break;
                case "EXS":
                    toReturn = "Jet2";
                    break;
                case "UAL":
                    toReturn = "United Airlines";
                    break;
                case "TAP":
                    toReturn = "TAP Air Portugal";
                    break;
                case "UAE":
                    toReturn = "Emirates";
                    break;
                case "SHT":
                    toReturn = "British Airways Shuttle";
                    break;
                case "EIN":
                    toReturn = "Aer Lingus";
                    break;
                case "CPA":
                    toReturn = "Cathay Pacific";
                    break;
                case "NJE":
                    toReturn = "NetJets Europe";
                    break;
                case "QTR":
                    toReturn = "Qatar Airways";
                    break;
                case "VLG":
                    toReturn = "Vueling";
                    break;
                case "EGL":
                    toReturn = "Capital Air Ambulance";
                    break;
            }
        }

        return toReturn;
    }
}
