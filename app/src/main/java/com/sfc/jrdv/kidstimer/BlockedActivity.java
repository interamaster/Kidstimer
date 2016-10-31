package com.sfc.jrdv.kidstimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BlockedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);
    }

    public void Losebutton(View view) {

        //reiniicmaos el intet service psasndole un valor nuevo

        Intent intent =new Intent(this,LockService.class);
        intent.putExtra(LockService.EXTRA_MESSAGE,"tu nuevo timepo sera de 15 min mas 15*60*1000=900000");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
        intent.putExtra(LockService.EXTRA_TIME,"900000");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
        startService(intent);


        finish();



        /*

        IDEM PARA CANCEL:IR AL HOME SCREEN:
        Intent startHomescreen=new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startHomescreen);
         */
    }



}
