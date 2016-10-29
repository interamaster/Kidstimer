package com.sfc.jrdv.kidstimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {

        Intent intent =new Intent(this,ejemploService.class);
        intent.putExtra(ejemploService.EXTRA_MESSAGE,"texto 2 a pasar");
        startService(intent);

    }
}
