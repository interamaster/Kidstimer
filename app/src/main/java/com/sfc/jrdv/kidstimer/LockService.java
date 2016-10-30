package com.sfc.jrdv.kidstimer;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LockService extends Service {

    //String CURRENT_PACKAGE_NAME = {your this app packagename};
    String CURRENT_PACKAGE_NAME ="com.sfc.jrdv.kidstimer";
    String lastAppPN = "";
    boolean noDelay = false;
    public static LockService instance;

    private TreeMap mySortedMap;

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
                     gettopactivity();
                }
            }
        }, 0, 10000, TimeUnit.MILLISECONDS);
    }



    public void checkRunningApps2() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String activityOnTop;
        if (Build.VERSION.SDK_INT > 20) {
            activityOnTop = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else {
            List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
            activityOnTop = ar.topActivity.getPackageName();
        }

        Log.e("activity on TOp", "" + activityOnTop);

        // Provide the packagename(s) of apps here, you want to show password activity
        if (activityOnTop.contains("whatsapp")  // you can make this check even better
                || activityOnTop.contains(CURRENT_PACKAGE_NAME)) {
            if (!(lastAppPN.equals(activityOnTop))) {
                lastAppPN = activityOnTop;
                Log.e("Whatsapp", "started");
            }
        } else {
            if (lastAppPN.contains("whatsapp")) {
                if (!(activityOnTop.equals(lastAppPN))) {
                    Log.e("Whatsapp", "stoped");
                    lastAppPN = "";
                }
            }
            // DO nothing
        }
    }






public void gettopactivity() {


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


    public static void stop() {
        if (instance != null) {
            instance.stopSelf();
        }
    }
}
