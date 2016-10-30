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
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

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

    private AndroidProcesses ProcessManager;
    private String packageName;

    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
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
        Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

        instance = this;




        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            // YOUR CODE
            Log.e("PANTALLA ENCENDIDA ", String.valueOf( screenOn));
        } else {
            // YOUR CODE
            Log.e("PANTALLA APAGADA ", String.valueOf( screenOn));
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
              packageName =  ProcessManager.getRunningForegroundApps(getApplicationContext()).get(0).getPackageName();
            // Log.v("INFO currentapp: ", packageName);

        }
        else
        {
            String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
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

    public static void stop() {
        if (instance != null) {
            instance.stopSelf();
        }
    }
}