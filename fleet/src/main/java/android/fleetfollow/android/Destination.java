package android.fleetfollow.android;

import android.location.Location;

import java.io.Serializable;

public class Destination implements Serializable {
    Location location;
    String address;
    String arrivalTime;

    Destination(Location location, String address, String arrivalTime){
        this.location = location;
        this.address = address;
        this.arrivalTime = arrivalTime;
    }
}
