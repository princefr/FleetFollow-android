package android.fleetfollow.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.fleetfollow.android.Utils.DateUtils;
import android.fleetfollow.android.Utils.PermissionHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

public class FleetFollow {

    private Activity context;
    private User UserModel;
    private FirebaseAuth mAuth;
    GeoFire geoFire;
    DatabaseReference ref;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FusedLocationProviderClient fusedLocationProviderClient;
    public Handler handler = null;
    public static Runnable runnable = null;
    FirebaseUser currentUser;

    /**
     *
     * @param context
     * @param apiKey
     * @param user
     */
    public void Init(Activity context, String apiKey, User user){
        if(context == null){
            throw new Error("");
        }else if(apiKey.equals("")){
            throw new Error("");
        }else if(user == null){

        }else{
            this.context = context;
            this.UserModel = user;
            this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
            Login();
        }
    }


    /**
     *
     */
    private void Login() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this.context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = task.getResult().getUser();
                                final String user = task.getResult().getUser().getUid();
                                Log.i("FleetFollow", user + " " + "got my user here");
                                db.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            RequestPermission();
                                        }else{
                                            db.child("users").child(user).setValue(UserModel);
                                            RequestPermission();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInAnonymously:failure", task.getException());

                            }
                        }
                    });
        }else{
            RequestPermission();
        }
    }


    /**
     *
     */
    private void RequestPermission() {
        PermissionHelper permissionHelper = new PermissionHelper();
        permissionHelper.AskPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION, new PermissionHelper.OnRequestResponseListner() {
            @Override
            public void OnRequestError(String error) {

            }

            @Override
            public void OnRequestSuccess(String response) {
                Log.i("FleetFollow", "got the permissions i'm about to launch the upload");
                ref =  FirebaseDatabase.getInstance().getReference("geofire");
                geoFire = new GeoFire(ref);
                UploadPosition();
            }
        });
    }



    public void UploadPosition() {
        handler = new Handler();
        runnable = new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                handler.postDelayed(runnable, 5000);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        Log.w("TAG", "uploading the position");
                        geoFire.getLocation(currentUser.getUid(), new LocationCallback() {
                            @Override
                            public void onLocationResult(String key, GeoLocation LastLocation) {
                                Log.w("TAG", "Geofire is working" + " " + String.valueOf(LastLocation));

                                if(LastLocation != null){
                                    Location loc1 = new Location("");
                                    loc1.setLatitude(location.getLatitude());
                                    loc1.setLongitude(location.getLongitude());
                                    Location loc2 = new Location("");
                                    loc2.setLatitude(LastLocation.latitude);
                                    loc2.setLongitude(LastLocation.longitude);
                                    float distanceInMeters = loc1.distanceTo(loc2);
                                    float maximumToMove = (float) 0.01;
                                    int retval = Float.compare(distanceInMeters, maximumToMove);
                                    Log.i("FleetFollow", String.valueOf(retval));
                                    if(retval > 0) {
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


                                        // query destination ;

                                        db.child("users").child(currentUser.getUid()).child("destination").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                              if(dataSnapshot != null){

                                              }else{
                                                  Log.i("FleetFollow", "sdfssdfds");
                                              }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }else{
                                        UserModel.SetInMoveStatus("Inactif");
                                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(LastLocation.latitude, LastLocation.longitude, 1);
                                            String address = addresses.get(0).getAddressLine(0);
                                            UserModel.SetLastAdress(address);
                                            db.child("users").child(currentUser.getUid()).setValue(UserModel);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                }else{
                                    UserModel.SetInMoveStatus("Actif");
                                    try {
                                        UserModel.SetlastTime(DateUtils.FromDateToString(new Date()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    geoFire.setLocation(currentUser.getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()));
                                    db.child("users").child(currentUser.getUid()).setValue(UserModel);
                                    Log.w("TAG", "Geofire is null" );
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        };
        handler.post(runnable);
    }



    public void setDestination(Location location, String destination, String arrivakTime){
        String key = db.child("destination").push().getKey();
        db.child("destination").child(key).setValue(new Destination(location, destination, arrivakTime));
       // db.child('users').
    }

}
