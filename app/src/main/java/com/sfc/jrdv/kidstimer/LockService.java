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



        Log.i("INFO", "Starting timer on create...");
        //en oncreate iniciamos el timer con el timepo predefinido:

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
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////24 h timer//////////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 private void startTimerNewDay() {


     // Schedule to run every day in midnight

    // today
     Calendar date = new GregorianCalendar();
     date.setTime(new Date());

     date.set(Calendar.HOUR_OF_DAY, 00);
     date.set(Calendar.MINUTE, 00);
     date.set(Calendar.SECOND, 0);
     date.set(Calendar.MILLISECOND, 0);



       //  int period = 10000;//10secs
        int perioddia= 1000 * 60 * 60 * 24 * 7;//24h

         timer.schedule(new mainTask(), date.getTime(), perioddia );



        //original se repite cada 15 segs:
       // timer.scheduleAtFixedRate(new mainTask(), 0, 15000);//pruebo con 50 segs
    }

 private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
            Log.i("INFO", "ES UN NUEVO DIA!!!");

            //TODO son las 12 de la noche dependidno del dia el valor del tiempototalJugar


        }
    }


 private final Handler toastHandler = new Handler()
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




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

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
            cdt.cancel();

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

        } else {
            // YOUR CODE
            Log.e("PANTALLA APAGADA ", String.valueOf( screenOn));

            cdt.cancel();
        }

        return START_STICKY;
    }

    private void scheduleMethod() {
        // TODO Auto-generated method stub

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
