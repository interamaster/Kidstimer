package com.sfc.jrdv.kidstimer.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sfc.jrdv.kidstimer.MainActivity;
import com.sfc.jrdv.kidstimer.Myapplication;

/**
 * Created by joseramondelgado on 05/11/16.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private DatabaseReference mDatabase;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // TODO: Implement this method to send any registration to your app's servers.
        guardarToken_enPREFYFireBase(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void guardarToken_enPREFYFireBase(String token) {
        // Add custom implementation, as needed.


        MainActivity.FireBaseUID=token;
        String ninoname = Myapplication.preferences.getString(Myapplication.PREF_NOmbre_Nino,"NO");
        KIDS newKid= new KIDS(token,ninoname);





        //si el nombre del niño es correcto(!=NO) lo guardamos/actualzaimos..si no lo hara en sucesivis arranques

        if (!ninoname.equals("NO")){
            Log.d(TAG, "KIDS  CREADO en onrefreshtoken"+newKid.getKidName() +" con UID "+newKid.getFirebaseuid());
            mDatabase.child("KIDS").child(newKid.getKidName()).setValue(newKid);

        }

    }


}
