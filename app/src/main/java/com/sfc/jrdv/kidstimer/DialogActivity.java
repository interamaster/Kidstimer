package com.sfc.jrdv.kidstimer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dialog);

        AlertDialog alertDialog = new AlertDialog.Builder(DialogActivity.this).create();
        alertDialog.setTitle("ATTENTION!!");
        alertDialog.setMessage("ONLY 5 MINUTES LEFT!!!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.show();


    }
}
