package android.fleetfollow.fr.fleetfollow.Model;

import java.io.Serializable;

public class User implements Serializable {

    String firstname;
    String lastname;
    String phoneNumber;
    String id;
    String lastTimeSeen;
    String LastAdress;
    String InMoveStatus;
    String email;


    public User(String firstname, String lastname, String phoneNumber, String id, String lastTimeSeen){
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.lastTimeSeen = lastTimeSeen;
    }


    public void SetLastAdress(String lastAdress){
        this.LastAdress = lastAdress;
    }

    public void SetInMoveStatus(String inMoveStatus){
        this.InMoveStatus = inMoveStatus;
    }


    public void SetFirstName(String firstname){
        this.firstname = firstname;
    }


    public void SetLastName(String lastname){
        this.lastname = lastname;
    }


    public void SetPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }


    public void setID(String id){
        this.id = id;
    }


    public void SetlastTime(String lastTime){
        this.lastTimeSeen = lastTime;
    }




}
