package io.atactic.android.utils;

import android.util.Log;

public class DistanceCalculator {


    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        /*
        LOG.debug("Distance "
                + "(" +lat1 + "," + lng1 + ")" + " to "
                + "(" +lat2 + "," + lng2 + ") - "
                + dist + "m");
        */
        return dist;
    }

    public static double disfFrom(double lat1, double lng1, double lat2, double lng2) {
        return distFrom((float)lat1, (float)lng1, (float)lat2, (float)lng2);
    }

}
