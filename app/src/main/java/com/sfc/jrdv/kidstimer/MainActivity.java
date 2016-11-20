package com.sfc.jrdv.kidstimer;

import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sfc.jrdv.kidstimer.teclado.LoginPadActivity;

public class MainActivity extends AppCompatActivity {


    EditText ChildrenName ;
    private String nombreHijo;

    //para el device manager

    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        Boolean ninoyaelegido = Myapplication.preferences.getBoolean(Myapplication.PREF_BOOL_NINOYAOK,false);//por defecto vale 0){
      //  Log.d("INFO","niñoa ya elegido: "+ninoyaelegido);


        //1º)si ya se eligio empieza del tiron

        if ( ninoyaelegido){

            String ninoname = Myapplication.preferences.getString(Myapplication.PREF_NOmbre_Nino,"NO");

         //   Log.d("INFO","su nombre es "+ninoname);

            StartServiceYa();

           // setContentView(R.layout.activity_main);//TODO quitar cunado no queramos probar ekl gewnerator


        }

        //2º)si no al lio


        // setContentView(R.layout.activity_main);//TODO quitar cunado no queramos probar ekl gewnerator

        setContentView(R.layout.activity_main_2);//TODO dponer en modo normal sin priobar el generator


        //To hide AppBar for fullscreen.
        ActionBar ab = getSupportActionBar();
        ab.hide();


        ChildrenName = (EditText) findViewById(R.id.txtname_check_children);





        //empezamos el service del tiron:
    /*
        Intent intent =new Intent(this,LockService.class);
        intent.putExtra(LockService.EXTRA_MESSAGE,"texto 2 a pasar");
        intent.putExtra(LockService.EXTRA_TIME,"0");//al arranacra no le damos mas tiempo pero hay que psar el intent
        startService(intent);

        finish();
*/

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////device manager//////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //usageAccessSettingsPage();//TODO esto no recuerdo porque lo puse..de omeonto lo quito
    }
/*
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

    */

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

    public void loggingCheck(View view) {

        //Intent intent =new Intent(this,ejemploService.class);
        Intent intent =new Intent(this,LoginPadActivity.class);
        startActivity(intent);

    }

    public void start(View view) {


        //1º)chequeamos nombre ok

        nombreHijo=ChildrenName.getText().toString();

        if (nombreHijo.isEmpty() || nombreHijo.length() < 4 || nombreHijo.length() > 8) {
            ChildrenName.setError("min 4 and max 8 characteres");


        }
        else {

            //esta nombre ok...empieza

            //avisamos del punto conseguido!!!

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialogalertlayout);
            dialog.setTitle(nombreHijo);



            ImageButton btnExit = (ImageButton) dialog.findViewById(R.id.btnExit);
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {



                    dialog.dismiss();

                    EnableAdmin();

                    //decimos que ya se eligio


                    Myapplication.preferences.edit().putBoolean(Myapplication.PREF_BOOL_NINOYAOK,true).commit();

                    //guaradsmo niño

                    Myapplication.preferences.edit().putString(Myapplication.PREF_NOmbre_Nino,nombreHijo).commit();

                    //empezamos

                    StartServiceYa();





                }
            });
            // show dialog on screen
            dialog.show();



        }

    }

    private void StartServiceYa() {

        Intent intent =new Intent(this,LockService.class);
        intent.putExtra(LockService.EXTRA_MESSAGE,"texto 2 a pasar");
        intent.putExtra(LockService.EXTRA_TIME,"0");//al arranacra no le damos mas tiempo pero hay que psar el intent
        startService(intent);

        finish();
    }

    private void EnableAdmin() {



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
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
