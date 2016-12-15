package com.sfc.jrdv.kidstimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;

public class DialogEmergenciaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_emergencia);



        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog)).create();
        alertDialog.setTitle("ATTENTION!!");
        //alertDialog.setMessage("USED EMERGENCY CODE,YOU HAVE 10  EXTRA MINUTES (TO UNISTALL APP IF YOU DO NOT LIKE IT)YOU WILL NOT HAVE ANY OTHER CHANCE TILL TOMORROW!!!");
        //segun idioma:
        alertDialog.setMessage(getString(R.string.emergencyaviso));
        alertDialog.setIcon(R.drawable.timer_icono);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                });
        alertDialog.show();


    }
}
