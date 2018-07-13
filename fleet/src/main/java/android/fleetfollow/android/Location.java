package android.fleetfollow.android;

import java.io.Serializable;

public class Location implements Serializable {
    String longitude;
    String latitude;

    Location(String longitude, String latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
