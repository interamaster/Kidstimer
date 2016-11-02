package com.sfc.jrdv.kidstimer;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //para el device manager

    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////device manager//////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        try
        {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdminDemo Receiver for active the component with different option
            mAdminName = new ComponentName(this, DeviceAdmin.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                // try to become active
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Pulsa activar app!!");
                startActivityForResult(intent, REQUEST_CODE);
            }
            else
            {
                // Already is a device administrator, can do security operations now.
                //TODO asi se puede bloquear!!! : mDPM.lockNow();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////device manager//////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        usageAccessSettingsPage();
    }

    public void usageAccessSettingsPage(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);

        if(intent.resolveActivity(getPackageManager()) != null) {


            //startActivityForResult(intent, 0);
            startActivity(intent);
        }

        else{

            //TODO
        }



    }

    public void startService(View view) {

        //Intent intent =new Intent(this,ejemploService.class);
        Intent intent =new Intent(this,LockService.class);
        intent.putExtra(LockService.EXTRA_MESSAGE,"texto 2 a pasar");
        intent.putExtra(LockService.EXTRA_TIME,"0");//al arranacra no le damos mas tiempo pero hay que psar el intent
        startService(intent);

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE == requestCode)
        {
            if(requestCode == Activity.RESULT_OK)
            {
                // done with activate to Device Admin
            }
            else
            {
                // cancle it.
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
