package android.fleetfollow.android;

import java.io.Serializable;

public class Location implements Serializable {
    double longitude;
    double latitude;

    Location(double longitude, double latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
