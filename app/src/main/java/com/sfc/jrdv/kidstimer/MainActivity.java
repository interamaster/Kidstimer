package com.sfc.jrdv.kidstimer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sfc.jrdv.kidstimer.firebase.KIDS;
import com.sfc.jrdv.kidstimer.teclado.LoginPadActivity;

import java.util.Calendar;





//v099991 quiatdo broadcast cambio de hora, ahora lo detecto al encender pantalla,quiatdo texto de logging pad y cambiado codigo 1972..29/4/17
//v099992 cambiada logica de dar timepo, en vez de alarmmanager que falla intermitentemenete, se detecta al encender pantalla, asi comom el intento de cambio de hora o dia(se han quitado del manifest el receiver del alarmmanager)
//v1 final googleplay mayo 17
//v1.1 arreglado code 0000 ,pref de num anuncios
public class MainActivity extends AppCompatActivity {


    EditText ChildrenName ;
    private String nombreHijo;

    //para el device manager

    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;


    //para firebase guardar nombre niños
    private DatabaseReference mDatabase;
    public static  String FireBaseUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //firebase:

        //En el arranque inicial de tu app, el SDK FCM genera un token de registro para la instancia de app cliente.
        Log.d("FCM", "Instance ID: " + FirebaseInstanceId.getInstance().getToken());

        //y lo guaradmos para poder enviar/recibir push
        FireBaseUID=FirebaseInstanceId.getInstance().getToken();

        //como puede ser que no tenga internewt o que falle se crea la class:MyFirebaseInstanceIdService
        //que en su metodo:  onTokenRefresh actualziar el Kids en firebase!!!


        //creamos la referencia
        mDatabase = FirebaseDatabase.getInstance().getReference();




        Boolean ninoyaelegido = Myapplication.preferences.getBoolean(Myapplication.PREF_BOOL_NINOYAOK,false);//por defecto vale 0){
      //  Log.d("INFO","niñoa ya elegido: "+ninoyaelegido);


        //1º)si ya se eligio empieza del tiron

        if ( ninoyaelegido){

            String ninoname = Myapplication.preferences.getString(Myapplication.PREF_NOmbre_Nino,"NO");

         //   Log.d("INFO","su nombre es "+ninoname);

           // StartServiceYa();

            //en vez de arrancarlo comprobamo si ya esta runnnig!!

            if (!isMyServiceRunning(LockService.class)){
                StartServiceYa();
            }



            //SOLO SI HACE MAS DE 1 MIN QUE SE METIO UN CODE, QUE VULEVA A PEDIRLO:


            //SI METIO UN CODIGO VALIDO PONEMOS UN LONG EN PREF PARA TENER QUE SEPERAR 60 SECS HASTA METER OTRO:

            Long lasttimeacertado=Myapplication.preferences.getLong(Myapplication.PREF_ULTIMA_VEZ_METIO_CODE_OK, 1);

            if ((lasttimeacertado+(60*1000))<System.currentTimeMillis() || (lasttimeacertado==1))
            {

                // y ademas q vaya al loggingpad

                Intent lockIntent = new Intent(this, LoginPadActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(lockIntent);

                finish();

            }

            else{

                //TODO MOSTAR DIALOG AVISANDO ESPERAR 1 MIN

                Log.d("INFO","ACERTASTE HACE MENOS DE 1 MIN");


                Intent DialogIntent = new Intent(this, DialogYaAcertasteAesperar.class);
                DialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(DialogIntent);

                finish();
            }




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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////saber si mi service esat runnig/////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }




    public void startService(View view) {
        //SOLO SE UNSA SI SE PONE LE XML ORIGINAL DE PULSAR BOTON PARA INICAR SERVICE!!!

        //Intent intent =new Intent(this,ejemploService.class);
        Intent intent =new Intent(this,LockService.class);
        intent.putExtra(LockService.EXTRA_MESSAGE,"DesdeMain");
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
        //SOLO SE UNSA SI SE PONE LE XML ORIGINAL DE PULSAR BOTON PARA INICAR SERVICE!!!

        //Intent intent =new Intent(this,ejemploService.class);
        Intent intent =new Intent(this,LoginPadActivity.class);
        startActivity(intent);

    }

    public void start(View view) {
        //SOLO SE USA AL CREAR EL NINO LA PRIMERA VEZ!!


        //1º)chequeamos nombre ok

        nombreHijo=ChildrenName.getText().toString();

        if (nombreHijo.isEmpty() || nombreHijo.length() < 4 || nombreHijo.length() > 8) {
            ChildrenName.setError("min 4 and max 8 characteres");


        }
        else {

            //esta nombre ok...empieza



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


                    //guardamos el niño en FireBase:


                    //KIDS newKid= new KIDS(FireBaseUID,nombreHijo);

                    //pero aparte delk nombre que le de vamos a añadir el año/mes/dia/hora/min para que si otro se llama igual no se cmbie en FB!!!

                    long millis=System.currentTimeMillis();
                    Calendar c=Calendar.getInstance();
                    c.setTimeInMillis(millis);

                    int year=c.get(Calendar.YEAR);
                    int mes=c.get(Calendar.MONTH);
                    int dia=c.get(Calendar.DAY_OF_MONTH);
                    int hours=c.get(Calendar.HOUR);
                    int minutes=c.get(Calendar.MINUTE);

                    //los unimos en un string:

                    String fechaaltaKid=String.valueOf(year)+"-"+String.valueOf(mes)+"-"+String.valueOf(dia)+"-"+String.valueOf(hours)+"-"+String.valueOf(minutes);


                    //guardamos el niño en FireBase:


                    KIDS newKid= new KIDS(FireBaseUID,nombreHijo+fechaaltaKid);



                    Log.d("INFO", "KIDS  CREADO en Main "+newKid.getKidName() +" con UID "+newKid.getFirebaseuid());


                    //3º)creamos en la Database de FireBase el padre
                    //con userid el nombre del padre:

                    mDatabase.child("KIDS").child(newKid.getKidName()).setValue(newKid);

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
        intent.putExtra(LockService.EXTRA_MESSAGE,"DesdeMain");
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
