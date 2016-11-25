package com.sfc.jrdv.kidstimer.firebase;

/**
 * Created by joseramondelgado on 25/11/16.
 */

public class KIDS {
    String kidName;
    String firebaseuid;

    //constructor

    public KIDS(String firebaseuid, String kidName) {
        this.firebaseuid = firebaseuid;
        this.kidName = kidName;

    }
    //getter  no neceiata setters firebase

    public String getFirebaseuid() {
        return firebaseuid;
    }

    public String getKidName() {
        return kidName;
    }
}
