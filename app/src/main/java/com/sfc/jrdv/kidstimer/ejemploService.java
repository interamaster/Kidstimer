package com.sfc.jrdv.kidstimer;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ejemploService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String  EXTRA_MESSAGE="mensaje";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.sfc.jrdv.kidstimer.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.sfc.jrdv.kidstimer.extra.PARAM2";

    public ejemploService() {
        super("ejemploService");
    }

    @Override

    //este metodo contine el codigo que querems ejecutar cuando el service reciba el intent!!
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            synchronized (this){

                try {
                    wait(10000);//wait 10 secs
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String textRecibidoIntent=intent.getStringExtra(EXTRA_MESSAGE);

                showText(textRecibidoIntent);

            }

        }
    }

    private void showText(String textRecibidoIntent) {


        Log.v("TASK","El mensaje es: "+ textRecibidoIntent);

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
