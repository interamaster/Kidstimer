package com.sfc.jrdv.kidstimer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        intent.putExtra(ejemploService.EXTRA_MESSAGE,"texto 2 a pasar");
        startService(intent);

    }
}
