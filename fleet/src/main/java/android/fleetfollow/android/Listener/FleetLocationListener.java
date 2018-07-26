package android.fleetfollow.android.Listener;

import android.app.Activity;
import android.fleetfollow.android.Destination;
import android.fleetfollow.android.FleetFollow;
import android.fleetfollow.android.User;
import android.fleetfollow.android.Utils.DateUtils;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FleetLocationListener implements LocationListener {

    DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("geofire");
    GeoFire geoFire = geoFire = new GeoFire(ref);
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static User UserModel = FleetFollow.UserModel;
    FirebaseUser currentUser = FleetFollow.currentUser;
    private Activity context = FleetFollow.context;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static Location LastLocation = null;

    @Override
    public void onLocationChanged(final Location location) {
        Log.i("FleetFollow", "je bouge");
        LastLocation = LastLocation == null ? location : LastLocation;
        float distanceInMeters = location.distanceTo( LastLocation);
        float maximumToMove = (float) 2.00;
        int retval = Float.compare(distanceInMeters, maximumToMove);
        if(retval > 0){
            Log.i("FleetFollow", "je bouge");
            UserModel.SetInMoveStatus("Actif");
            try {
                UserModel.SetlastTime(DateUtils.FromDateToString(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            geoFire.setLocation(currentUser.getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()));
            db.child("users").child(currentUser.getUid()).setValue(UserModel);
            String StorageKey = db.push().getKey();
            db.child("GeolocationArchive").child(currentUser.getUid()).child(StorageKey).setValue(new GeoLocation(location.getLatitude(), location.getLongitude()));
            LastLocation = location;
        }else{
            UserModel.SetInMoveStatus("Inactif");
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                UserModel.SetlastTime(DateUtils.FromDateToString(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String address = addresses.get(0).getAddressLine(0);
                UserModel.SetLastAdress(address);
                db.child("users").child(currentUser.getUid()).setValue(UserModel);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setDestination(Location location, String destination, String arrivakTime){
        String key = db.child("destination").push().getKey();
        db.child("destination").child(key).setValue(new Destination(location, destination, arrivakTime));
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
