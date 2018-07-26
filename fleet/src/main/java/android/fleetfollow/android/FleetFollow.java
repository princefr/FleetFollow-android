package android.fleetfollow.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

    public static Activity context;
    public static User UserModel;
    private FirebaseAuth mAuth;
    public static GeoFire geoFire;
    DatabaseReference ref;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    public Handler handler = null;
    public static Runnable runnable = null;
    public static FirebaseUser currentUser;

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
            Login();
        }
    }


    /**
     *
     */
    private void Login() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
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
        Fleet.context = context;
        context.startService(new Intent(context, Fleet.class));
    }



}
