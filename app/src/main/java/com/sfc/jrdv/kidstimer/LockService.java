package com.sfc.jrdv.kidstimer;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
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

    private long total = 30000;//aqui se guardar el tiempo total!!!!


    @Override
    public void onCreate() {
        super.onCreate();


        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);



        Log.i("INFO", "Starting timer on create...");
        //en oncreate iniciamos el timer con el timepo predefinido:

        cdt = new CountDownTimer(total, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i("INFO", "Countdown seconds remaining: " + millisUntilFinished / 1000);
                //update total with the remaining time left
                total = millisUntilFinished;

            }

            @Override
            public void onFinish() {

                //TODO se acabo la tablet!!
                Log.i("INFO", "Timer finished");
            }
        };

        cdt.start();
    }

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

            cdt = new CountDownTimer(total, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Log.i("INFO", "Countdown seconds remaining: " + millisUntilFinished / 1000);
                    //update total with the remaining time left
                    total = millisUntilFinished;

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

            Log.v("INFO currentapp: ", packageName);
        }
        else
        {
             packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
           // Log.v("INFO currentapp: ", packageName);
        }



        //para poner 1 solo log con la ultima abierta:

        if (!(lastAppPN.equals(packageName))) {
            lastAppPN = packageName;
            Log.v("INFO currentapp: ", packageName);
        }



        // Provide the packagename(s) of apps here, you want to show password activity
        if (lastAppPN.contains("whaatspp") || lastAppPN.contains(CURRENT_PACKAGE_NAME)) {

            Log.v("INFO NO SE BLOQUEARIA: ",  lastAppPN);
            // Show Password Activity
        } else {
            // DO nothing
            Log.v("INFO  sE BLOQUEARIA: ",  lastAppPN);
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
