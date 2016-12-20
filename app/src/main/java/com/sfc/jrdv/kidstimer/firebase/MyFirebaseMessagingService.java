package com.sfc.jrdv.kidstimer.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sfc.jrdv.kidstimer.LockService;
import com.sfc.jrdv.kidstimer.R;

import java.util.Map;

/**
 * Created by joseramondelgado on 04/11/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      //  Log.d("FIREBASE", remoteMessage.getNotification().getBody());




        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FIREBASE", "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("FIREBASE", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        /*
        {EXTRATIME=3600000, body=TE ACABAN DE DAR 1 HORA EXTRA, title=TE ACABAN DE DAR 1 HORA EXTRA}
         */

        Map<String, String> data = remoteMessage.getData();

        //you can get your text message here.
        String body= data.get("body");
        String title= data.get("title");
        String EXTRATIME=data.get("EXTRATIME");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

          NotificationCompat.Builder mBuilder;
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setAutoCancel(true);
            mBuilder.setOngoing(false);
         mBuilder.setVisibility(Notification.VISIBILITY_PRIVATE);
            mBuilder.setSmallIcon(R.drawable.timer_icono);
             mBuilder.setContentTitle(title);
          mBuilder.setContentText(body);
          mNotificationManager.notify(100000, mBuilder.build());//1000000=notifyID





    //aqui deberiamos ver el logging y el Body por ejmeplo
        //si se pulsa en el icono se abre la  Mainactivty!!
        //pero lo vamos a intentar hacer desde aqui:


        //reiniicmaos el intet service psasndole un valor nuevo

        Intent intent =new Intent(this,LockService.class);
        intent.putExtra(LockService.EXTRA_MESSAGE,"desde un notificacion!!!");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
        intent.putExtra(LockService.EXTRA_TIME,EXTRATIME);//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
        startService(intent);

        /*
        Y FUNCIONA!!!
        Message data payload: {EXTRATIME=3600000, body=TE ACABAN DE DAR 1 HORA EXTRA, title=TE ACABAN DE DAR 1 HORA EXTRA}

 D/INFO: REINICIADO onStartCommand EN SERVICE!!
 D/INFO: intent not null  onStartCommand EN SERVICE!!desde un notificacion!!!
  D/INFO: PANTALLA ON EN  TimerTiempoJuegoIniciarOajustar:true
         */

    }



}
