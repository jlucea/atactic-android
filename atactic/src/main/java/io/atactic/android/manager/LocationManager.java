package io.atactic.android.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public final class LocationManager implements OnSuccessListener<Location> {

    private static final String LOG_TAG = "LocationManager";

    // Single instance
    private static LocationManager globalInstance = new LocationManager();

    private Location lastKnownLocation;

    /**
     * Private constructor to restrict instantiation of the class from other classes
     */
    private LocationManager(){
    }

    public static LocationManager getInstance(){
        return globalInstance;
    }

    /**
     *
     * @param activity
     */
    public void updateLocation(Activity activity) {

        // Get location provider
        FusedLocationProviderClient locationProvider =
                LocationServices.getFusedLocationProviderClient(activity);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.w("LocationManager", "WARNING: location permissions are DISABLED");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            String[] requiredPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            int requestCode = 99;
            ActivityCompat.requestPermissions(activity, requiredPermissions, requestCode);

        } else {
            Log.v(LOG_TAG,"LocationManager - Permissions OK. Adding onSuccessListener");
            locationProvider.getLastLocation().addOnSuccessListener(globalInstance);
        }
    }


    public Location getLastKnownLocation(){

        if (lastKnownLocation != null) {
            Log.d(LOG_TAG, "Last known location is "
                    + "Lat = " + lastKnownLocation.getLatitude()
                    + " | Lng = " + lastKnownLocation.getLongitude());

            return lastKnownLocation;

        }else{
            Log.w(LOG_TAG, "Last known location is NULL");
            return null;
        }
    }


    public void getLastLocation(Context context, OnSuccessListener<Location> listener){

        Log.v(LOG_TAG,"Getting last location for a custom location listener");

        FusedLocationProviderClient provider = LocationServices.getFusedLocationProviderClient(context);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e(LOG_TAG, "Permissions denied");

        }else{
            Log.v(LOG_TAG,"Permissions Ok");
            provider.getLastLocation().addOnSuccessListener(listener);
        }
    }

    private void requestLocationPermission() {

    }


    @Override
    public void onSuccess(Location location) {

        if (location != null) {
            Log.d(LOG_TAG, "Location updated "
                    + "Lat = " + location.getLatitude()
                    + " | Lng = " + location.getLongitude());

            this.lastKnownLocation = location;

        } else {
            Log.w(LOG_TAG, "Location is null");
        }
    }




}
