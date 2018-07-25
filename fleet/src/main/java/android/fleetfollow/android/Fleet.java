package android.fleetfollow.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.fleetfollow.android.Listener.FleetLocationListener;
import android.fleetfollow.android.Utils.PermissionHelper;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fleet extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static Activity context;

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public FleetLocationListener listener;
    public PermissionHelper  permissionHelper;
    GeoFire geoFire = FleetFollow.geoFire;
    DatabaseReference ref;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    // public Location previousBestLocation = null;


    @SuppressLint("MissingPermission")
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("FleetFollow", "im starting the service");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new FleetLocationListener();
        permissionHelper =  new PermissionHelper();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 1, listener);
        super.onStart(intent, startId);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }


}
