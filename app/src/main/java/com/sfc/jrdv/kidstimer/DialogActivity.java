package com.sfc.jrdv.kidstimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dialog);


        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog)).create();
        alertDialog.setTitle("ATTENTION!!");
        alertDialog.setMessage(getString(R.string.fiveminaviso));
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
