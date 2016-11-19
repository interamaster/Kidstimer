package com.sfc.jrdv.kidstimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by esq00931 on 31/10/2016.
 * PARA EL DETECTATR CAMBIO HORA!!!
 * HAYQ UE AÑADIRLO EN MANIFEST TAMBIEN Y QUE FILTER POR EL INTENT android.intent.action.TIME_SET
 */

public class IntentoCambiarHoraIntentReceiver extends BroadcastReceiver {

    private boolean CambioDeHora;
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.TIME_SET".equals(intent.getAction())) {
            CambioDeHora=true;
            Intent pushIntent = new Intent(context, LockService.class);
            pushIntent.putExtra("cambio_de_hora", CambioDeHora);
            context.startService(pushIntent);
        }
    }
}
