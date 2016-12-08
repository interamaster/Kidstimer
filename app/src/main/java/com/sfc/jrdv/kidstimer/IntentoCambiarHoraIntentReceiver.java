package com.sfc.jrdv.kidstimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by esq00931 on 31/10/2016.
 * PARA EL DETECTATR CAMBIO HORA!!!
 * HAYQ UE AÑADIRLO EN MANIFEST TAMBIEN Y QUE FILTER POR EL INTENT android.intent.action.TIME_SET
 */

public class IntentoCambiarHoraIntentReceiver extends BroadcastReceiver {

    private boolean CambioDeHora;
    @Override
    public void onReceive(Context context, Intent intent) {

       int AutotimeesON= android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
        //0=OFF
        //1=ON


        if ("android.intent.action.TIME_SET".equals(intent.getAction() )&& AutotimeesON==0) {
            //PARA EVITAR FALSO TIME SET:
            //http://stackoverflow.com/questions/16684132/action-time-set-in-android-getting-called-many-times-without-changing-the-time-m
            //QUE SOLO LO EJECUTE SI EL AUTOTIME ESTA EN OFF!!!(=0)

            Log.d("INFO","RECIBIDI BROADCAST DE CHANGETIME!!");
            CambioDeHora=true;
            Intent pushIntent = new Intent(context, LockService.class);
            pushIntent.putExtra("cambio_de_hora", CambioDeHora);
            pushIntent.putExtra(LockService.EXTRA_MESSAGE,"cambio_de_hora");
            context.startService(pushIntent);
        }


        if ("android.intent.action.DATE_CHANGED".equals(intent.getAction())){
            //SI CAMBIA DE DIA ..DEL TIRON !!

            Log.d("INFO","RECIBIDI BROADCAST DE CHANGEDATE!!");
            CambioDeHora=true;
            Intent pushIntent = new Intent(context, LockService.class);
            pushIntent.putExtra("cambio_de_hora", CambioDeHora);
            pushIntent.putExtra(LockService.EXTRA_MESSAGE,"cambio_de_hora");
            context.startService(pushIntent);
        }
    }
}
