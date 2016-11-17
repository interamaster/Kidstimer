package com.sfc.jrdv.kidstimer.teclado;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cardinalsolutions.android.arch.autowire.AndroidLayout;
import com.cardinalsolutions.android.arch.autowire.AndroidView;

import com.sfc.jrdv.kidstimer.LockService;
import com.sfc.jrdv.kidstimer.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;




@AndroidLayout(com.sfc.jrdv.kidstimer.R.layout.activity_login_pad)

public class LoginPadActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener {

	private static final String TAG = "LoginPadActivity";

	@AndroidView(com.sfc.jrdv.kidstimer.R.id.activity_login_access_code_value)
    private EditText mUserAccessCode;

   // @AndroidView(jrdv.mio.com.entrenatablasmultiplicacion.R.id.activity_login_access_code_login)
    private TextView mLoginButton;

  //  @AndroidView(jrdv.mio.com.entrenatablasmultiplicacion.R.id.login_button_progress)
    private ProgressBar mLoginProgress;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.one_button)
    private TextView mOneButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.two_button)
    private TextView mTwoButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.three_button)
    private TextView mThreeButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.four_button)
    private TextView mFourButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.five_button)
    private TextView mFiveButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.six_button)
    private TextView mSixButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.seven_button)
    private TextView mSevenButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.eight_button)
    private TextView mEightButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.nine_button)
    private TextView mNineButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.zero_button)
    private TextView mZeroButton;

    @AndroidView(com.sfc.jrdv.kidstimer.R.id.activity_login_access_code_delete)
    private TextView mDeleteButton;


    //new shake:

    private  Animation mShakeAnimation;

    private boolean mDeleteIsShowing = true;
    private boolean mFailedLogin = false;


	public static   int USER_PIN_MAX_CHAR = 4;




    //variables multiplicacion

    private int Horas;
    private int minutes;
    private int numeroFallos =0;
    private String numeroClaveFinal;

    //para hablar

    private TextToSpeech textToSpeech;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pad);

           configureViews();
            //configureAnimations();
            setEditTextListener();

                iniciaVoces();






               generaCodigoSecretosegunHora();



}





    private void iniciaVoces(){


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("es", "ES");

                    int result = textToSpeech.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }

                    // speak(saludoInicial);//aqui on habla de tiron!!!

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }

    //The speak() method takes a String parameter, which is the text you want Android to speak.
    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

//After speak() create another method to stop the TextToSpeech service when a user closes the app:

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }




    @Override
    protected void onRestart() {
        super.onRestart();

    }



	private void generaCodigoSecretosegunHora(){


        Calendar c = Calendar.getInstance();
         Horas = c.get(Calendar.HOUR_OF_DAY);//formato 24h
         minutes=c.get(Calendar.MINUTE);

        numeroClaveFinal=String.format("%02d", Horas)+String.format("%02d", minutes);
        Log.d("INFO","el numero secreto sin invertir es: "+numeroClaveFinal);

        numeroClaveFinal=new StringBuilder(numeroClaveFinal).reverse().toString();
        Log.d("INFO","el numero secreto YA INVERTIDO  es: "+numeroClaveFinal);

        USER_PIN_MAX_CHAR=4;

    }

    private void configureViews() {
      //  this.mLoginProgress.setVisibility(View.GONE);
       // this.mLoginButton.setOnClickListener(this);
      //  this.mLoginButton.setVisibility(View.GONE);
        this.mOneButton.setOnClickListener(this);
        this.mOneButton.setOnTouchListener(this);
        this.mTwoButton.setOnClickListener(this);
        this.mTwoButton.setOnTouchListener(this);
        this.mThreeButton.setOnClickListener(this);
        this.mThreeButton.setOnTouchListener(this);
        this.mFourButton.setOnClickListener(this);
        this.mFourButton.setOnTouchListener(this);
        this.mFiveButton.setOnClickListener(this);
        this.mFiveButton.setOnTouchListener(this);
        this.mSixButton.setOnClickListener(this);
        this.mSixButton.setOnTouchListener(this);
        this.mSevenButton.setOnClickListener(this);
        this.mSevenButton.setOnTouchListener(this);
        this.mEightButton.setOnClickListener(this);
        this.mEightButton.setOnTouchListener(this);
        this.mNineButton.setOnClickListener(this);
        this.mNineButton.setOnTouchListener(this);
        this.mZeroButton.setOnClickListener(this);
        this.mZeroButton.setOnTouchListener(this);
        this.mDeleteButton.setVisibility(View.VISIBLE);
        this.mDeleteButton.setOnClickListener(this);
    }

    private void setEditTextListener() {
        this.mUserAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (LoginPadActivity.this.mUserAccessCode.getText().length() == USER_PIN_MAX_CHAR) {

                    Log.e("INFO","hemos llegado al nuemro maximo de 4 numeros");


                    String numerometido=LoginPadActivity.this.mUserAccessCode.getText().toString();



                    if (numerometido.equals(numeroClaveFinal))//TODO calcular numeo correcto
                    {

                        //hemos acertado siguiente:
                        ChequeaResultado("OK");
                    }

                    else if (numerometido.equals("0408") || numerometido.equals("1972"))  {
                        //numero especial fijo da 1 hora mas

                        ChequeaResultado("ESPECIAL");

                    }
                    else{

                        //hemos fallado
                        ChequeaResultado("MAL");


                    }

                }
            }
        });
    }

    private void ChequeaResultado(String chequeanum) {



        //lo hago con el numero metido

        if (chequeanum.equals("MAL")) {
            //si hemos fallado:
          //  this.mUserAccessCode.startAnimation(this.mAnimSlideIn);


            //aumentamos numeor de fallos

            numeroFallos++;

            if (numeroFallos<3) {


                //que vibre al fallar

                // vibration for 800 milliseconds
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(800);

                speak("no");

            }

            else if (numeroFallos==3){
                // vibration for 1600 milliseconds
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1600);

                speak("Te queda un solo intento o me apagare");


            }

            else{

                //TODO apagar pantala
                // Initiate DevicePolicyManager.
                  DevicePolicyManager   mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }





        } else  if (chequeanum.equals("OK")){
            ////si hemos acertado:

            //this.mLoginProgress.setVisibility(View.GONE);
            // this.mUserAccessCode.startAnimation(this.mAnimSlideOut);
            Log.e("INFO", "acertaste!!!!!");



            //TODO genera 15 min extras

            //reiniicmaos el intet service psasndole un valor nuevo

            Intent intent =new Intent(this,LockService.class);
            intent.putExtra(LockService.EXTRA_MESSAGE,"tu nuevo timepo sera de 15 min mas 15*60*1000=900000");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
            intent.putExtra(LockService.EXTRA_TIME,"1800000");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
            startService(intent);


            finish();
        }

        else if(chequeanum.equals("ESPECIAL")){

            Log.e("INFO", "acertaste ESPECIAL!!!!!");



            //TODO genera 1 horas extra mas!!

            //reiniicmaos el intet service psasndole un valor nuevo

            Intent intent =new Intent(this,LockService.class);
            intent.putExtra(LockService.EXTRA_MESSAGE,"tu nuevo timepo sera de 15 min mas 15*60*1000=900000");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
            intent.putExtra(LockService.EXTRA_TIME,"3600000");//tu nuevo timepo sera de 15 min mas 15*60*1000=900000
            startService(intent);


            finish();

        }
    }

    @Override
    public void onClick(View vIn) {
        if (!this.mDeleteIsShowing) {
            crossFade(
                    getResources().getInteger(
                            android.R.integer.config_mediumAnimTime),
                    this.mDeleteButton,
                    getResources().getString(R.string.activity_login_delete));
            this.mDeleteIsShowing = true;
        }
        switch (vIn.getId()) {
            case  com.sfc.jrdv.kidstimer.R.id.one_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mOneButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.two_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mTwoButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.three_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mThreeButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.four_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mFourButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.five_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mFiveButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.six_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mSixButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.seven_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mSevenButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.eight_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mEightButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.nine_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mNineButton.getText());
                }
                break;
            case com.sfc.jrdv.kidstimer.R.id.zero_button:
                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
                    this.mUserAccessCode.append(this.mZeroButton.getText());
                }
                break;

        
            case com.sfc.jrdv.kidstimer.R.id.activity_login_access_code_delete:

               // if (this.mLoginButton.getVisibility() == View.VISIBLE) {
               //     ChequeaResultado(false);
               // }
                this.mUserAccessCode.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                if (isEditTextEmpty(this.mUserAccessCode)) {
                    crossFade(
                            getResources().getInteger(
                                    android.R.integer.config_mediumAnimTime),
                            this.mDeleteButton, null);
                    this.mDeleteIsShowing = false;
                }
                break;
        }
    }

    private void crossFade(int animTimeIn, TextView textViewIn,
                          String valueStringIn) {

        textViewIn.setText(valueStringIn);
        textViewIn.setAlpha(0f);
        textViewIn.setVisibility(View.VISIBLE);

        textViewIn.animate().alpha(1f).setDuration(animTimeIn)
                .setListener(null);
    }

    private static boolean isEditTextEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public boolean onTouch(View vIn, MotionEvent eventIn) {
        switch (vIn.getId()) {
            case com.sfc.jrdv.kidstimer.R.id.one_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.two_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.three_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.four_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.five_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.six_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.seven_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.eight_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.nine_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case com.sfc.jrdv.kidstimer.R.id.zero_button:
                toggleNumberColor(vIn, eventIn);
                break;
        }
        return false;
    }

    private void toggleNumberColor(View viewIn, MotionEvent eventIn) {
        if (eventIn.getAction() == MotionEvent.ACTION_DOWN) {
            ((TextView) viewIn).setTextColor(getResources().getColor(
                    R.color.blue));


        } else if (eventIn.getAction() == MotionEvent.ACTION_UP) {
                 ((TextView) viewIn).setTextColor(getResources().getColor(com.sfc.jrdv.kidstimer.R.color.white));
        }
    }


}