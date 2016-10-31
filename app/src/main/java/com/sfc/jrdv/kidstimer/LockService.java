package com.sfc.jrdv.kidstimer;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LockService extends Service {

    //String CURRENT_PACKAGE_NAME = {your this app packagename};
    private String CURRENT_PACKAGE_NAME ="com.sfc.jrdv.kidstimer";
    private String lastAppPN = "";
    boolean noDelay = false;
    public static LockService instance;

   // private AndroidProcesses ProcessManager;
    private String packageName;

    CountDownTimer cdt = null;

    private long tiempoTotalParaJugar = 30000;//aqui se guardar el tiempo tiempoTotalParaJugar!!!!

    //para saber el dia de la semana:
    private static Timer timer = new Timer();
    private Context ctx;




    @Override
    public void onCreate() {
        super.onCreate();


        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


        ctx = this;


        //emepezamos el timer de cada 24h at nidnight
        startTimerNewDay();



        Log.i("INFO", "Starting TIEMPO DE JUEGO TIMER  on create...");
        //en oncreate iniciamos el timer con el timepo predefinido:

        //lo hacemos desde la funcion que calcula que dia es y segun el dia le da un tiempo:

        CalcularNewDayTime4Play();

                // una vez sabida la cantidad creamos el timer!!
                //no lo podemois hacer en una funcion aparate porque da crash!!
/*
//ESTE TIMER SE EJECUTA PERO SE BORRA DE INMEDIATO CON EL DEL ONSTARTCOMMAND, ASI QUE LO QUITO
        cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i("INFO", "Countdown seconds remaining on create: " + millisUntilFinished / 1000);
                //update tiempoTotalParaJugar with the remaining time left
                tiempoTotalParaJugar = millisUntilFinished;
                // TODO CREAR NOTIFICACION QUE ACTUALIZE EL TIMEPO RESTANTE Y QUE SE QUITE AL ABRIRLA(UN TIMER COMO EL TIME IT PERO AL REVES)

            }

            @Override
            public void onFinish() {

                //TODO se acabo la tablet!!
                Log.i("INFO", "Timer finished");
            }
        };

        cdt.start();
*/

        /*
        //lo hacemos en metodo:

        cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i("INFO", "Countdown seconds remaining: " + millisUntilFinished / 1000);
                //update tiempoTotalParaJugar with the remaining time left
                tiempoTotalParaJugar = millisUntilFinished;

            }

            @Override
            public void onFinish() {

                //TODO se acabo la tablet!!
                Log.i("INFO", "Timer finished");
            }
        };

        cdt.start();

        */
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////TIEMPO DE JUEGO TIMER//////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void TimerTiempoJuegoIniciarOajustar(){


        //TODO pte implemntar logica si el servicio se ha parado para que no empieze de neuvo el timer
        //aqui ya sabemos el valor de  : tiempoTotalParaJugar
        //ESTE METODO NO SE PUEDE LLMAR DESDE ONCREATE!!!!
        //PERO SI DESDE ONSTARTCOMMAND!!!
        //NI SIQUIERA CON EL HANDLER ESTE:
/*
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                     @Override
                                                     public void run() {

                                                         cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {
                                                             @Override
                                                             public void onTick(long millisUntilFinished) {

                                                                 Log.i("INFO", "Countdown seconds remaining on TimerTiempoJuegoIniciarOajustar: " + millisUntilFinished / 1000);
                                                                 //update tiempoTotalParaJugar with the remaining time left
                                                                 tiempoTotalParaJugar = millisUntilFinished;
                                                                 //   AL ABRIRLA(UN TIMER COMO EL TIME IT PERO AL REVES)

                                                             }

                                                             @Override
                                                             public void onFinish() {

                                                                 //
                                                                 Log.i("INFO", "Timer finished");
                                                             }
                                                         };

                                                         cdt.start();
                                                     }

                                                 });



*/

        //ASI Q LO DEJAMOS COMO ESTABA PERO DESDE ON CREATE NO SE LLAMARA!!
        //original:

        //CON ESTO DEL LOOPER SE ARREGLA!!!!

        Looper.getMainLooper();

        if(Looper.myLooper() == null){
            Looper.prepare();
        }

        cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                Log.i("INFO", "Countdown seconds remaining on TimerTiempoJuegoIniciarOajustar:" + millisUntilFinished / 1000);
                //update tiempoTotalParaJugar with the remaining time left
                tiempoTotalParaJugar = millisUntilFinished;
                // TODO ACTUALIZAR NOTIFICACION QUE ACTUALIZE EL TIMEPO RESTANTE Y QUE SE QUITE AL ABRIRLA(UN TIMER COMO EL TIME IT PERO AL REVES)

            }

            @Override
            public void onFinish() {

                //TODO se acabo la tablet!!
                Log.i("INFO", "Timer finished");
            }
        };

        cdt.start();





    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////24 h timer//////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 private void startTimerNewDay() {


     // Schedule to run every day in midnight

    // today
     Calendar date = new GregorianCalendar();
     date.setTime(new Date());

     date.set(Calendar.HOUR_OF_DAY, 10);
     date.set(Calendar.MINUTE, 51);
     date.set(Calendar.SECOND, 0);
     date.set(Calendar.MILLISECOND, 0);



       //  int period = 10000;//10secs
        int perioddia= 1000 * 60 * 60 * 24 * 7;//24h

         timer.schedule(new mainTask(), date.getTime(), perioddia );
     //TODO PX EL TIMER SE EJECUTA AL CREARLO  TAMBIEN?¿?



        //original se repite cada 15 segs:
       // timer.scheduleAtFixedRate(new mainTask(), 0, 15000);//pruebo con 50 segs
    }

 private class mainTask extends TimerTask
    {

        public void run()
        {
            toastHandler.sendEmptyMessage(0);//TODO REEMPLZAR POR NOTIFICACION
            Log.i("INFO", "ES UN NUEVO DIA!!!");

            //TODO son las 12 de la noche dependidno del dia el valor del tiempototalJugar
          CalcularNewDayTime4Play();


            //si existe timer lo paramos
            if (cdt!=null){
                cdt.cancel();
            }


            //UNA VEZ AJUSTADO EL TIMEPO NUEVO, QUE SE REAJUSTE EL TIMER!!:
            TimerTiempoJuegoIniciarOajustar();





        }
    }




    private void CalcularNewDayTime4Play(){



    //1º)PARAMOS EL TIMER

    //si existe timer lo paramos
    if (cdt!=null){
        cdt.cancel();
    }


    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);

    switch (day) {


        case Calendar.MONDAY:
            // Current day is Monday
            //entre semana 1 HORA
            tiempoTotalParaJugar = 1*60 * 60 * 1000;

            break;

        case Calendar.TUESDAY:
            //entre semana 1 HORA
            tiempoTotalParaJugar = 1*60 * 60 * 1000;

            break;

        case Calendar.WEDNESDAY:
            //entre semana 1 HORA
            tiempoTotalParaJugar = 1*60 * 60 * 1000;

            break;


        case Calendar.THURSDAY:
            //entre semana 1 HORA
            tiempoTotalParaJugar = 1*60 * 60 * 1000;


            break;

        case Calendar.FRIDAY:
            //entre semana 1 HORA
            tiempoTotalParaJugar = 1*60 * 60 * 1000;


            break;

        case Calendar.SATURDAY:
            //entre semana 1 HORA
            tiempoTotalParaJugar = 3*60 * 60 * 1000;


            break;

        case Calendar.SUNDAY:
            //entre semana 1 HORA
            tiempoTotalParaJugar =3*60 * 60 * 1000;

            break;
    }
    //una vez sepamos el dia que ajuste el Timerdel timepo de jeugo con este valor: en TimerTiempoJuegoIniciarOajustar()
    //no px da erro de thread ?¿?
   // TimerTiempoJuegoIniciarOajustar();

}

 private final Handler toastHandler = new Handler() //TODO REEMPLZAR POR NOTIFICACION
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        // TODO: Return the communication channel to the service.
        return null;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////METODO QUE SE EJECUTA CADA VEZ QUE SE RELANZA ESTE SERVICE//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO OJO ESTE METODO SE EJECUTA CADA VEZ QUE SE LANZA UN INTENT DE ESTE SERVICE
        //SI YA ESTABA CREADO!!!
        //ASI QUE ES LA MEJO MANERA DE ACTUALIZAR LA INFO!!


        scheduleMethod();
        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
       // Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

        instance = this;




        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            // YOUR CODE
            Log.e("PANTALLA ENCENDIDA ", String.valueOf( screenOn));

            //reiniciamos el timercountdown
            //al encendr la pnatlla reinicimaos  el timer con el timepo que queda

            Log.i("INFO", "Restarting  timer...");

            //si existe timer lo paramos
            if (cdt!=null){
                cdt.cancel();
            }

            //reakustamos el timer al encender pantalla

            TimerTiempoJuegoIniciarOajustar();


            /*
            //lo hacemos emjor llmando al metodo creado
            cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Log.i("INFO", "Countdown seconds remaining: " + millisUntilFinished / 1000);
                    //update tiempoTotalParaJugar with the remaining time left
                    tiempoTotalParaJugar = millisUntilFinished;

                }

                @Override
                public void onFinish() {

                    //TODO se acabo la tablet!!
                    Log.i("INFO", "Timer finished");
                }
            };



            cdt.start();

            */

        } else {
            // YOUR CODE
            Log.e("PANTALLA APAGADA ", String.valueOf( screenOn));

            cdt.cancel();
        }

        return START_STICKY;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void scheduleMethod() {


        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                // This method will check for the Running apps after every 100ms
               // if(30 minutes spent){

                 if(12<10){
                    stop();
                }else{
                    //checkRunningApps2();
                    // retriveNewApp();
                     //gettopactivity();//con este hacen falta permisos
                     getTopactivitySinPermisos();
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }


public void gettopactivity() {

    //NECESITA PERMISOS !!!

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(),
                        usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                String currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                Log.v("INFO currentapp: ", currentApp);
            }
        }
    } else {
        ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
        String currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        Log.v("INFO currentapp: ", currentApp);

    }
}



public void getTopactivitySinPermisos(){

        ActivityManager activityManager = (ActivityManager) getSystemService (Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
        {

              packageName = activityManager.getRunningAppProcesses().get(0).processName;
            // Log.v("INFO currentapp: ", packageName);
        }
        else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {

            List<AndroidAppProcess> processes2= AndroidProcesses.getRunningForegroundApps(getApplicationContext() );

            if (processes2.size()>0){

                packageName =  AndroidProcesses.getRunningForegroundApps(getApplicationContext()).get((processes2.size()-1)).getPackageName();
            }

            else
            {



              packageName =  AndroidProcesses.getRunningForegroundApps(getApplicationContext()).get(0).getPackageName();
            // Log.v("INFO currentapp: ", packageName);
            }

            /*
            List<AndroidAppProcess> processes2= AndroidProcesses.getRunningForegroundApps(getApplicationContext() );//esto da 2 apps en foreground ?¿?

            //packageName =  AndroidProcesses.getRunningForegroundApps(getApplicationContext()).get(processes2.size()).getPackageName();

            packageName=processes2.get(processes2.size()).getPackageName();


          //  List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();//esto da too los procesos que haya  >20!!

          */

          //  Log.v("INFO currentapp: ", packageName);
        }
        else
        {
             packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
           // Log.v("INFO currentapp: ", packageName);
        }



        //para poner 1 solo log con la ultima abierta:

        if (!(lastAppPN.equals(packageName))) {
            lastAppPN = packageName;
            Log.v("INFO currentapp on get ", packageName);
        }



        // Provide the packagename(s) of apps here, you want to show password activity
        if (lastAppPN.contains("whaatspp") || lastAppPN.contains(CURRENT_PACKAGE_NAME)) {

          //TODO quitar para ver logging ; Log.v("INFO NO SE BLOQUEARIA: ",  lastAppPN);
            // Show Password Activity
        } else {
            // DO nothing
          //TODO quitar para ver logging : Log.v("INFO  sE BLOQUEARIA: ",  lastAppPN);
            /*
            //TODO bloquear
            Intent BlockedActivityIntent = new Intent(this, BlockedActivity.class);


            startActivity(BlockedActivityIntent);
            */
        }
    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i("INFO", "Timer cancelled");
        super.onDestroy();
    }




    public static void stop() {
        if (instance != null) {
            instance.stopSelf();

            Log.v("INFO  ",  "proceso parado!!!");


        }
    }
}
