package com.sfc.jrdv.kidstimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by joseramondelgado on 21/11/16.
 */

public class AlarmIntentReceiver extends BroadcastReceiver {
    private boolean Alarma;
    @Override
    public void onReceive(Context context, Intent intent) {


            Alarma=true;
            Intent pushIntent = new Intent(context, LockService.class);
            pushIntent.putExtra("Alarma_reseteo_timers", Alarma);
            pushIntent.putExtra(LockService.EXTRA_MESSAGE,"Alarma_reseteo_timers");
            context.startService(pushIntent);

    }
}