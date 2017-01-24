package com.sfc.jrdv.kidstimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.MotionEvent;

public class DialogEmergenciaActivity extends AppCompatActivity {

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////PARA DISMISS AL TOCAR FUERA//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            // Tapped outside so we finish the activity
            this.finish();
        }
        return super.dispatchTouchEvent(ev);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_emergencia);





/*
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////asi al pulsar fuera o no estar unlocked se queda colgado!!!/////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog)).create();
        alertDialog.setTitle("ATTENTION!!");
        //alertDialog.setMessage("USED EMERGENCY CODE,YOU HAVE 10  EXTRA MINUTES (TO UNISTALL APP IF YOU DO NOT LIKE IT)YOU WILL NOT HAVE ANY OTHER CHANCE TILL TOMORROW!!!");
        //segun idioma:
        alertDialog.setMessage(getString(R.string.emergencyaviso));
        alertDialog.setIcon(R.drawable.timer_icono);

        //para cancelara al pulsar fuera:
        //dialog.setCanceledOnTouchOutside(true)
        alertDialog.setCanceledOnTouchOutside(true);


        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                });
        alertDialog.show();


*/

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //idem con builder y autodismiss en 2 secs:
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        final  AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog));
        alertDialog.setTitle("ATTENTION!!");
        alertDialog.setMessage(getString(R.string.emergencyaviso));
        alertDialog.setIcon(R.drawable.timer_icono);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                finish();
            }
        });
        final AlertDialog alert = alertDialog.create();
        //para cancelara al pulsar fuera:
        alert.setCanceledOnTouchOutside(true);
        alert.show();

// Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                    finish();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 10000);//10 segs y se cierra






    }
}
