package com.sfc.jrdv.kidstimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class DialogYaAcertasteAesperar extends AppCompatActivity {

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
/*
    private FrameLayout touchInterceptor;

    @Override
    protected void onPause() {
        if (touchInterceptor.getParent() == null) {
            ((ViewGroup) findViewById(android.R.id.content)).addView(touchInterceptor);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        ((ViewGroup) findViewById(android.R.id.content)).removeView(touchInterceptor);
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finish();
            return true;
        }
        return false;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {


       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_ya_acertaste_aesperar);

        // For intercepting clicks from dialog like activities
      //  touchInterceptor = new FrameLayout(this);
      //  touchInterceptor.setClickable(true);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////asi al pulsar fuera o no estar unlocked se queda colgado!!!/////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog)).create();
        alertDialog.setTitle("ATTENTION!!");
        //alertDialog.setMessage("WAIT 1 MINUTE TO ENTER A NEW CODE!!!");
        //segun idioma:
        alertDialog.setMessage(getString(R.string.waitoneminuteaviso));
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
        alertDialog.setMessage(getString(R.string.waitoneminuteaviso));
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

        handler.postDelayed(runnable, 3000);//3 segs y se cierra





    }
}
