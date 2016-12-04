package com.sfc.jrdv.kidstimer;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.sfc.jrdv.kidstimer.teclado.LoginPadActivity;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    private String PACKAGEMALDITO1="com.android.launcher";//el home screen de LL
    private String lastAppPN = "";
    boolean noDelay = false;
    public static LockService instance;

   // private AndroidProcesses ProcessManager;
    private String packageName;

    CountDownTimer cdt = null;

    private long tiempoTotalParaJugar = 30000;//aqui se guardar el tiempo tiempoTotalParaJugar!!!!

    //para saber el dia de la semana:
    private static Timer timer ;
    private Context mContext;

    //para el intnt Extra info

    public static final String  EXTRA_MESSAGE="mensaje";
    public static final String  EXTRA_TIME="time";

//para la notificacion

    private NotificationCompat.Builder mBuilder = null;
    private NotificationManager mNotificationManager;



    //PARA LAS 24 CON ALARMAMANGER BROADCASTRECEIVER EN VEZ DE TIMER

    private BroadcastReceiver AlarmBrodCastReceiver;
    private AlarmManager MiAlarmManager;
    private PendingIntent pendingIntentAlarma;




    //para la repeticion cada seg

    ScheduledExecutorService scheduler;
    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("INFO","INICIADO onCreate EN SERVICE!!");

        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        //NO CREO Q SEA NECESARIO LA TENRELO EN MANIFEST!!!NO!!! SI LO QUITO NO FUNCIONA!!

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


        //IDEM PARA LA ALARMA RECEIVER:

        /*
        IntentFilter filter2 = new IntentFilter("MiIntentAlarmaReseteoTimers");
        AlarmBrodCastReceiver = new AlarmIntentReceiver();
        registerReceiver(AlarmBrodCastReceiver, filter2);
        */

        //NO LO HAGO COMOM DICE EN TODOS LADDOS CON UN PWENDING INTENT:
        //https://www.sitepoint.com/scheduling-background-tasks-android/

        Intent alarma =new Intent(this,AlarmIntentReceiver.class);
        pendingIntentAlarma=PendingIntent.getBroadcast(this,0,alarma,0);//1º 0=requestcode y 2º 0=flag




        mContext = this;

        //EN ONCRETE POENMOS A FALSE EL DEL TIME_SET(POR SI CAMBIA EN ALGUNOS DISPOSITIVOS=

        Myapplication.preferences.edit().putBoolean(Myapplication.PREF_BOOL_INTENTO_CAMBIO_HORA,false).commit();


        //emepezamos el timer de cada 24h at nidnight
        startTimerNewDay2();



       // Log.i("INFO", "Starting TIEMPO DE JUEGO TIMER  on create...");
        //en oncreate iniciamos el timer con el timepo predefinido:



        //1º)chequeamos si tenemos al valor restante guaradai(si ha intentado forzar el apagado)


        long tiempoRstanteenPREF = Myapplication.preferences.getLong(Myapplication.PREF_TiempoRestante,0);//por defecto vale 0


        if(tiempoRstanteenPREF>=1){

            //ya habiamos guardao el tiempo..lo recuperamos

            tiempoTotalParaJugar=tiempoRstanteenPREF;


        }
        else {


            Boolean IntnetoCambioHora = Myapplication.preferences.getBoolean(Myapplication.PREF_BOOL_INTENTO_CAMBIO_HORA,false);//por defecto vale 0


            Log.d("INFO"," INTENTO CAMBIO DE HORA EN ONCREAT SERVICE: "+IntnetoCambioHora);

        //lo hacemos desde la funcion que calcula que dia es y segun el dia le da un tiempo:

        CalcularNewDayTime4Play();

        }

                // una vez sabida la cantidad creamos el timer!!


        //no lo podemois hacer en una funcion aparate porque da crash!!
        //pero se modifico la funcion y ya si!!!

        //EN REALIDAD ESTO SE LLAMA DESDE ONSTARTCOMMAND!!!! AL PASARLE EL EXTRA DesdeMain
        //ASI Q LO QUITO DE AQUI
       // TimerTiempoJuegoIniciarOajustar();//TODO QUITADO OJO



        //iniciamos una notifiacion nada mas arranacar

        refreshNotifications("seconds remaining: " + tiempoTotalParaJugar / 1000);


                //no lo podemois hacer en una funcion aparate porque da crash!!
/*
//ESTE TIMER SE EJECUTA PERO SE BORRA DE INMEDIATO CON EL DEL ONSTARTCOMMAND, ASI QUE LO QUITO
        cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i("INFO", "Countdown seconds remaining on create: " + millisUntilFinished / 1000);
                //update tiempoTotalParaJugar with the remaining time left
                tiempoTotalParaJugar = millisUntilFinished;
                //   CREAR NOTIFICACION QUE ACTUALIZE EL TIMEPO RESTANTE Y QUE SE QUITE AL ABRIRLA(UN TIMER COMO EL TIME IT PERO AL REVES)

            }

            @Override
            public void onFinish() {


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

                //
                Log.i("INFO", "Timer finished");
            }
        };

        cdt.start();

        */





    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////METODO QUE SE EJECUTA CADA VEZ QUE SE RELANZA ESTE SERVICE//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO OJO ESTE METODO SE EJECUTA CADA VEZ QUE SE LANZA UN INTENT DE ESTE SERVICE
        //SI YA ESTABA CREADO!!!
        //ASI QUE ES LA MEJO MANERA DE ACTUALIZAR LA INFO!!

        //ej leer el extra del intent:


        Log.d("INFO","REINICIADO onStartCommand EN SERVICE!!");




        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
        // Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

        instance = this;


        if(intent!=null) {


            Log.d("INFO", "intent not null  onStartCommand EN SERVICE!!" + intent.getStringExtra(EXTRA_MESSAGE));

            //1º)sacamos los valores de EXTRA_TIME y EXTRA_MSG

            String intentExtra = intent.getStringExtra(EXTRA_MESSAGE);
            //  Log.v("TASK","El mensaje recibido en LockService es un timepo extra de: "+ intentExtra);

            String intentExtraTime = intent.getStringExtra(EXTRA_TIME);

           // Log.d("INFO", "intent not null  onStartCommand EN SERVICE!!" + intent.getStringExtra(EXTRA_TIME));

            //sumamos ese tiempo extra: si se puede
            //SI ES UN CASTIGO EL EXTRA MESSAGE ES =1..ASI PONEMOS QUE QUEDEN SOLO 5 SECS

            if (intentExtraTime != null) {


                if (intentExtraTime.equals("1")) {
                    //EL EXTRA ES 1..OSEA CASTIGO

                    tiempoTotalParaJugar = 5000;

                }

                tiempoTotalParaJugar = tiempoTotalParaJugar + Integer.valueOf(intentExtraTime);


                //si existe timer lo paramos
                if (cdt != null) {
                    cdt.cancel();
                    //y por seguridad lo anulamos
                    cdt=null;
                }


                //UNA VEZ AJUSTADO EL TIMEPO NUEVO, QUE SE REAJUSTE EL TIMER!!:
                TimerTiempoJuegoIniciarOajustar();


            }


            if (intentExtra!=null &&intentExtra.equals("DesdeMain")) {

                //  Log.d("INFO","intent  DESDEMAIN onStartCommand EN SERVICE!!" );

                //estamos arrancadno desde main!!! iniciamos el counter
                if (cdt != null) {
                    cdt.cancel();
                    //y por seguridad lo anulamos
                    cdt=null;
                }

                //reakustamos el timer al encender pantalla

                TimerTiempoJuegoIniciarOajustar();


            }


            //2º)chequeamos si es un intent de pantalla
            if (intentExtra!=null &&intentExtra.equals("screen_state")) {


                boolean screenOn = intent.getBooleanExtra("screen_state", true);
                if (!screenOn) {
                    // YOUR CODE
                    Log.e("PANTALLA ENCENDIDA ", String.valueOf(screenOn));

                    //reiniciamos el timercountdown
                    //al encendr la pnatlla reinicimaos  el timer con el timepo que queda

                    //  Log.i("INFO", "Restarting  timer...");

                    //si existe timer lo paramos
                    if (cdt != null) {
                        cdt.cancel();
                        //y por seguridad lo anulamos
                        cdt=null;
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

                    //  se acabo la tablet!!
                    Log.i("INFO", "Timer finished");
                }
            };



            cdt.start();

            */

                } else {
                    // YOUR CODE
                    Log.e("PANTALLA APAGADA ", String.valueOf(screenOn));

                    //si existe timer lo paramos
                    if (cdt != null) {
                        cdt.cancel();
                        //y por seguridad lo anulamos
                        cdt=null;
                    }
                }

            }
            //3º)chequeamos si es una de intento de cambio de hora

            if (intentExtra!=null && intentExtra.equals("cambio_de_hora")) {

                boolean intento_CambioHora = intent.getBooleanExtra("cambio_de_hora", false);
                if (intento_CambioHora) {
                    //intewnto cambiar hora


                    //toastHandler.sendEmptyMessage(0);//asi simempre pone "test"..npi =¿?=¿
                    //lo hago mejor asi


                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "DUE TO CHANGE TIME CHEAT,YOUR KIDS TIMER WILL NOT INCREASE TODAY TIME...XD(POR LISTO)", Toast.LENGTH_LONG).show();


                        }
                    });
                    //poenmos a true le intento


                    Myapplication.preferences.edit().putBoolean(Myapplication.PREF_BOOL_INTENTO_CAMBIO_HORA, true).commit();


                    //COMO AL CAMBIAR LA HORA EL TIMER.SCHEDUEL NO FUNCIONA LO RECREO

                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = null;


                    startTimerNewDay2();


                }
            }

            //4ºchequeamos si es una alarmaMagerBrodacast

            if (intentExtra!=null && intentExtra.equals("Alarma_reseteo_timers")) {
                boolean AlarmaMagerBrodacast = intent.getBooleanExtra("Alarma_reseteo_timers", false);
                if (AlarmaMagerBrodacast) {
                    //AlarmaReceiver de Broadcast recibida


                    //ESTO SERA LO QUE NOS LLAME DESDE EL BRODCASRECEIVER:AlarmIntentReceiver
                    //ASI QUE AQUI EJECUTAMOS EL RESTERO DE LOS TIMERS


                    Log.i("INFO", "ES UN NUEVO DIA!!!");

                    //TODO son las 12 de la noche dependidno del dia el valor del tiempototalJugar
                    CalcularNewDayTime4Play();


                    //si existe timer lo paramos
                    if (cdt != null) {
                        cdt.cancel();
                        //y por seguridad lo anulamos
                        cdt=null;
                    }


                    //UNA VEZ AJUSTADO EL TIMEPO NUEVO, QUE SE REAJUSTE EL TIMER!!:
                    TimerTiempoJuegoIniciarOajustar();


                }

            }
        }


        //inicamos la repeticion
        //NO !!!!!!:lo pongo en oncreate o se para y arranca cada vez que hay un nuevo intent(por ej apaagar pantalla)
        //si lo pongo en oncreate no empieza!!!
       scheduleMethod();


        return Service.START_STICKY;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////TIEMPO DE JUEGO TIMER//////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void TimerTiempoJuegoIniciarOajustar(){


        //SI LA PANTALLA ESTA APAGADA..ESTO VUELVE

        if (isScreenOn(this)) {
            Log.d("INFO","PANTALLA ON:"+ (isScreenOn(this)));//FUNCIONA OK
            //ES UNA DOBLE SEGURIDAD PX A VECES SGIGUE CONTANTO AUN SIN USAR....
                //solo se ejecuta si l apnatalla esta apagada

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
            //TODO COM YA NO SE LLAMA DESDE ON CREATE LO QUITAMOS!!!
            /*
            Looper.getMainLooper();

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            */

            cdt = new CountDownTimer(tiempoTotalParaJugar, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                   // Log.i("INFO", "Countdown seconds remaining on TimerTiempoJuegoIniciarOajustar:" + millisUntilFinished / 1000);
                    //update tiempoTotalParaJugar with the remaining time left
                    tiempoTotalParaJugar = millisUntilFinished;



                    if (!isScreenOn(mContext)){
                        // POR SEGURIDAD NOS ASEGUIRAMOS QEU SOLO FUNCIONE SI La PNATLLA ESTA ENCENDIDA
                        cdt.cancel();

                        //y por seguridad lo anulamos
                        cdt=null;

                        //y nos autollamamos..que solo empezara si realemnte esta encendida!!

                        TimerTiempoJuegoIniciarOajustar();
                    }

                }

                @Override
                public void onFinish() {

                    //TODO se acabo la tablet!!
                    //  Log.i("INFO", "Timer finished");
                }
            };

            cdt.start();


        }


    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////24 h timer NO FUNCIONA SI SE TOCA HORA/DIA!!////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 private void startTimerNewDay() {


        timer= new Timer();

     // Schedule to run every day in midnight

     // Scheduling task at today : 00:00:00 PM
     Calendar calendar = Calendar.getInstance();
     calendar.set(Calendar.HOUR_OF_DAY, 00);
     calendar.set(Calendar.MINUTE, 00);
     calendar.set(Calendar.SECOND, 22);

     //este time es en el pasado por eso se ejecuta del tiron
     //asi que le añado 24 horas(1 DIA)

      calendar.add(Calendar.DATE,1);

     Date time = calendar.getTime();

     Log.d("INFO"," TIMER EMPEZARA EL :  "+time.toString());




    // Read more at http://www.java2blog.com/2015/08/java-timer-example.html

       //  int period = 10000;//10secs
        int perioddia= 1000 * 60 * 60 * 24 * 1;//24h


     timer.schedule(new mainTask() ,time,perioddia);

     //To DO PX EL TIMER SE EJECUTA AL CREARLO  TAMBIEN?¿?
     //solucion no poner ls 00.00.00

    }

 private class mainTask extends TimerTask
    {

        public void run()
        {
            //toastHandler.sendEmptyMessage(0);//TODO REEMPLZAR POR NOTIFICACION
         Log.i("INFO", "ES UN NUEVO DIA!!!");

            //TODO son las 12 de la noche dependidno del dia el valor del tiempototalJugar
          CalcularNewDayTime4Play();


            //si existe timer lo paramos
            if (cdt!=null){
                cdt.cancel();

                //y por seguridad lo anulamos
                cdt=null;
            }


            //UNA VEZ AJUSTADO EL TIMEPO NUEVO, QUE SE REAJUSTE EL TIMER!!:
            TimerTiempoJuegoIniciarOajustar();





        }
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////24 h timer con AlarmManager Broadcast//////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void startTimerNewDay2(){

        // Schedule to run every day in midnight

        // Scheduling task at today : 00:00:22 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 01);
        calendar.set(Calendar.SECOND, 00);

        //este time es en el pasado por eso se ejecuta del tiron
        //asi que le añado 24 horas(1 DIA)

        calendar.add(Calendar.DATE,1);//TODO VOLVER A PONER EN RELAIDAD

        Date time = calendar.getTime();
        long timeinMilisec=calendar.getTimeInMillis();

        Log.d("INFO"," TIMER EMPEZARA EL :  "+time.toString());


        //  int period = 10000;//10secs
        int perioddia= 1000 * 60 * 60 * 24 * 1;//24h


        //primerom anualmos la alarma anterior si existe

        if (MiAlarmManager!=null){
            MiAlarmManager.cancel(pendingIntentAlarma);
            Log.d("INFO"," ANULADA LA ALARMA ");


        }


        //o  alo bestia!!!


        Intent alarmIntent = new Intent(this, AlarmIntentReceiver.class);
        pendingIntentAlarma = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        MiAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        MiAlarmManager.cancel(pendingIntentAlarma);
        Log.d("INFO"," ANULADA LA ALARMA  A LO BESTIA!! ");


         MiAlarmManager=(AlarmManager)  getSystemService(Context.ALARM_SERVICE);
         MiAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeinMilisec, perioddia, pendingIntentAlarma);



/*

     //   CHEQEUO FUNCIONA OK CADA 10 SEG!!:
        int interval = 10000;

        MiAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntentAlarma);


*/

        //SETEO LA ALARMA
        //OJO AQUI NO SE VA A RECOGER LA LLMADA DE LA ALARMA SINO EN EL AlarmIntentReceiver.CLASS
        //EN ELLA VAMOS A RELLAMAR A ESTE LOCKSERVICE CON UN EXTRA DE Alarma_reseteo_timers=TRUE
        //Y EN ONSTRATCOMMAND LO FILTRAMOS Y ACTUAMOS EN CONSECUENCIA


    }




    private void CalcularNewDayTime4Play(){



        Log.d("INFO","INICIADO CalcularNewDayTime4Play EN SERVICE!!");

        Boolean IntnetoCambioHora = Myapplication.preferences.getBoolean(Myapplication.PREF_BOOL_INTENTO_CAMBIO_HORA,false);//por defecto vale 0


        if (IntnetoCambioHora){

            //se intento cambiar la hora..
            Log.d("INFO","se decteto intento de cambio  en CalcularNewDayTime4Play");

            //lo ponemos de nuevo a normal:
            //poenmos a true le intento

            Myapplication.preferences.edit().putBoolean(Myapplication.PREF_BOOL_INTENTO_CAMBIO_HORA,false).commit();

        }

        else {

            Log.d("INFO"," AJSUTE TIMEPO OK EN  CalcularNewDayTime4Play EN SERVICE !!");

            //1º)PARAMOS EL TIMER

            //si existe timer lo paramos
            if (cdt != null) {
                cdt.cancel();
                //y por seguridad lo anulamos
                cdt=null;
            }


            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {


                case Calendar.MONDAY:
                    // Current day is Monday
                    //entre semana 1 HORA
                    tiempoTotalParaJugar = 1 * 60 * 60 * 1000;

                    break;

                case Calendar.TUESDAY:
                    //entre semana 1 HORA
                    tiempoTotalParaJugar = 1 * 60 * 60 * 1000;

                    break;

                case Calendar.WEDNESDAY:
                    //entre semana 1 HORA
                    tiempoTotalParaJugar = 1 * 60 * 60 * 1000;

                    break;


                case Calendar.THURSDAY:
                    //entre semana 1 HORA
                    tiempoTotalParaJugar = 1 * 60 * 60 * 1000;

                    // tiempoTotalParaJugar = 20* 1000;


                    break;

                case Calendar.FRIDAY:
                    //entre semana 1 HORA
                    tiempoTotalParaJugar = 1 * 60 * 60 * 1000;


                    //  tiempoTotalParaJugar = 10* 1000;
                    break;

                case Calendar.SATURDAY:
                    //FINDE 2 HORA
                    tiempoTotalParaJugar = 2 * 60 * 60 * 1000;


                    break;

                case Calendar.SUNDAY:
                    //FINDE  2 HORA
                    tiempoTotalParaJugar = 2 * 60 * 60 * 1000;

                    break;
            }
            //una vez sepamos el dia que ajuste el Timerdel timepo de jeugo con este valor: en TimerTiempoJuegoIniciarOajustar()
            //no px da erro de thread ?¿?
            // TimerTiempoJuegoIniciarOajustar();

        }

}

 private final Handler toastHandler = new Handler() //TODO REEMPLZAR POR NOTIFICACION
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), "DUE TO CHANGE TIME CHEAT,YOUR KIDS TIMER WILL NOT INCREASE TODAY TIME...XD(POR LISTO)", Toast.LENGTH_LONG).show();
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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void scheduleMethod() {


        //ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        //creamos una property emjor para deetctar si ya empezo o no
        //PERO EL PROBLEMA REAL ES  QUE LOS Executors SE PARA CUNADO LA COU SE PONE EN REPOSO(EJ APGARA PANTALLA!!)


        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {

                //checkRunningApps2();
                // retriveNewApp();
                //gettopactivity();//con este hacen falta permisos
                getTopactivitySinPermisos();
                //newgettopactivity();//ESTE TAMBIEN FUNCIONA EN LL

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
                //Log.v("INFO currentapp: ", currentApp);
            }
        }
    } else {
        ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
        String currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        //Log.v("INFO currentapp: ", currentApp);

    }
}


public void newgettopactivity(){
    /*
    //TODO ESTE METODO TAMBIEN FUNCIONA y SERIA MEJOR SI HUBIER UNA LISTA DE APS A BLOQUEAR:
   // http://stackoverflow.com/questions/36954701/how-to-display-lock-activity-quickly-when-user-click-on-another-installed-applic

     */

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { //For versions less than lollipop
        ActivityManager am = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(5);
        packageName = taskInfo.get(0).topActivity.getPackageName();
       // Log.v("INFO NEW", "top from Receiver app = " + packageName);
        for (ActivityManager.RunningTaskInfo info : taskInfo) {
            if (info.topActivity.getPackageName().equalsIgnoreCase("com.google.android.gms")) {

            }
        }
    } else { //For versions Lollipop and above
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningForegroundApps(mContext.getApplicationContext());
        Collections.sort(processes, new ProcessManager.ProcessComparator());
        for (int i = 0; i <= processes.size() - 1; i++) {
            if (!processes.get(i).name.isEmpty()) {
                packageName = processes.get(i).name;

             //   Log.e("INFO NEW", "top from Receiver app=" + packageName);




            }
            if (processes.get(i).name.equalsIgnoreCase("com.google.android.gms")) { //always the package name above/below this package is the top app
                if ((i + 1) <= processes.size() - 1) { //If processes.get(i+1) available, then that app is the top app
                    packageName = processes.get(i + 1).name;
                } else if (i != 0) { //If the last package name is "com.google.android.gms" then the package name above this is the top app
                    packageName = processes.get(i - 1).name;
                } else {
                    if (i == processes.size() - 1) { //If only one package name available
                        packageName = processes.get(i).name;
                    }
                }
             //   Log.v("INFO NEW", "top app from Receiver = " + packageName);
            }
        }
    }
}


    /**
     * Is the screen of the device on.
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }

public void getTopactivitySinPermisos(){

    //ESTE METODO ESTA FUNCIONANDO SIMEPRE QUE EL SERVICE ESTE VIVO!!

    //ASI Q CHEQUEAMOS LA PANTALLA Y EL CDT:

    if (cdt==null ){

       // Log.d("INFO","cdt es null!!");//FUNCIONA OK
        //SI NO HAY CDT ES PORQUE ESTA APAGADA LA PANTALLA ?¿
        if (isScreenOn(this)) {
            //si l pantalla esta encendida el cdt no puiede ser null!!!
            //lo reiniciamos

            TimerTiempoJuegoIniciarOajustar();

        }
    }



    //VAMOS A CHEQEUAR ESTADO DE PANTALLA:

   // Log.d("INFO","PANTALLA ON:"+ (isScreenOn(this)));//FUNCIONA OK

    //ACTUALIZAMOS LA NOTIFICACION


    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(tiempoTotalParaJugar),
            TimeUnit.MILLISECONDS.toMinutes(tiempoTotalParaJugar) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(tiempoTotalParaJugar) % TimeUnit.MINUTES.toSeconds(1));

  //  refreshNotifications("seconds remaining: " + tiempoTotalParaJugar / 1000);



    refreshNotifications("REMAINIG TIME: " + hms);


        ActivityManager activityManager = (ActivityManager) getSystemService (Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
        {

              packageName = activityManager.getRunningAppProcesses().get(0).processName;
            // Log.v("INFO currentapp: ", packageName); //com.android.settings es el de settings independiente de que parte!!
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
          //  Log.v("INFO currentapp on get ", packageName);


        }



              //guardamos el timepo restante

         Myapplication.preferences.edit().putLong(Myapplication.PREF_TiempoRestante,tiempoTotalParaJugar).commit();




        //al ser milisec es dificil piullarolo con un avalor exacto
        //lo convertimos a segs

    long minutes = TimeUnit.MILLISECONDS.toMinutes(tiempoTotalParaJugar);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(tiempoTotalParaJugar)%60;//el resto!!


    //Log.d("INFO"," TIMER quedan :  "+minutes +" y "+seconds);





    // Provide the packagename(s) of apps here, you want to show password activity
        if ((lastAppPN.contains(PACKAGEMALDITO1) || lastAppPN.contains(CURRENT_PACKAGE_NAME)) || tiempoTotalParaJugar>=2000) {//TODO PONER TIEMPO A >=1000

          //TODO quitar para ver logging ; Log.v("INFO NO SE BLOQUEARIA: ",  lastAppPN);
            // Show Password Activity

            if(minutes==5 &&  seconds==0 ){

                //ponemo le dialog de aviso

                Intent DialogIntent = new Intent(mContext, DialogActivity.class);
                DialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(DialogIntent);

            }

        }





        else {

            Intent lockIntent = new Intent(mContext, LoginPadActivity.class);
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(lockIntent);

        }





    }

    @Override
    public void onDestroy() {


       // Log.i("INFO", "Timer cancelled");
        super.onDestroy();

        cdt.cancel();
        //y por seguridad lo anulamos
        cdt=null;


        //para evitar que el user pueda para el proceso:
        //http://stackoverflow.com/questions/21550204/how-to-automatically-restart-a-service-even-if-user-force-close-it

        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }




    public static void stop() {
        if (instance != null) {
            instance.stopSelf();

           // Log.v("INFO  ",  "proceso parado!!!");


        }
    }






    public void refreshNotifications( String message) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setAutoCancel(false);
            mBuilder.setOngoing(true);
            mBuilder.setOnlyAlertOnce(true);
            mBuilder.setVisibility(Notification.VISIBILITY_PRIVATE);
            mBuilder.setSmallIcon(R.drawable.timer_icono);

            mBuilder.setOngoing(true);
           // mBuilder.setContentTitle(mContext.getString(R.string.downloading_file));
           // mBuilder.setContentTitle("titulo");
        }
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;
        int numMessages = 0;

       // mBuilder.setContentText(mContext.getString(R.string.total_progress, percentProgress));
       // mBuilder.setContentText("Texto de Notificacion");
       // mNotificationManager.notify(notifyID, mBuilder.build());

        // Start of a loop that processes data and then notifies the user
        mBuilder.setContentText(message).setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is updated.
        mNotificationManager.notify(
                notifyID,
                mBuilder.build());
    }




    public void onTaskRemoved(Intent rootIntent) {

        //unregister listeners
        //do any other cleanup if required

        //stop service
        //stopSelf();

        //guardamos el timepo restante

        Myapplication.preferences.edit().putLong(Myapplication.PREF_TiempoRestante,tiempoTotalParaJugar).commit();



      //  Log.v("INFO  ",  "proceso parado y tiempo guardado!!!");
    }

}
