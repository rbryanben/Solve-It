package com.wapazock.solveit.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.utils.globalShared;

import java.util.concurrent.TimeUnit;

public class signupActivity02 extends AppCompatActivity {

    /*
    xml file - activity_signup_page02

     notes :
          developer should implement
          - verification of mobile number
     */

    private static final String TAG = "signupActivity02";
    private EditText verificationNumber;
    private TextView verificationError;
    private ImageView verificationErrorImage;
    private Button verify, cancel, send;
    private String verificationID ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup_page02);

        //exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                ((globalShared) getApplication()).gotException(new Exception(e),0);
            }
        });

        this.verificationNumber = (EditText) findViewById(R.id.message_verification_code);
        this.verify = (Button) findViewById(R.id.message_verification_confirmButton);
        this.cancel = (Button) findViewById(R.id.message_verification_cancelButton);
        this.send = (Button)findViewById(R.id.message_verification_sendButton);
        this.verificationErrorImage = (ImageView)findViewById(R.id.activity_signup_page02_verificationImage) ;
        this.verificationError =(TextView)findViewById(R.id.activity_signup_page02_verificationErrorText);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((globalShared) getApplication()).setDefaultTheme(this);
        //check values

        //lose focus
        verificationNumber.setFocusable(false);

        //on touch
        setOnTouch();

        //clear error message
        verificationErrorImage.setImageBitmap(null);
        //listeners
        setCancelListener();
        setSendCodeListener();
        sendCode();
    }



    /*
        nb : do not delete
        This procedure is used to proceed to the next signUp page using the dissolve animation but on
        doing this we need to pass in the written parameter { username | email | password } , which we obtained
        from the user
        Goes to "signupActivity03"
     */
    private void gotoNextSignUpActivity() {
        Intent signUpActivity03 = new Intent(this, signupActivity03.class);
        signUpActivity03.putExtra("educationLevel",((globalShared) getApplication()).getTempSignupAccount().getEducationLevel());
        //start activity
        startActivity(signUpActivity03);
        finish();
        Animatoo.animateSlideLeft(this);
    }

    /*
       set on touch listener
     */
    private void setOnTouch() {
        this.verificationNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                verificationNumber.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    /*
       set cancel button listener
     */
    private void setCancelListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideRight(signupActivity02.this);
            }
        });
    }

    private void sendCode(){
        send.animate().alpha(0.5f).setDuration(300);
        //count down timer
        final int[] seconds = {60};
        CountDownTimer timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds[0] -= 1 ;
                send.setText("available in " + Integer.toString(seconds[0]));
            }

            @Override
            public void onFinish() {
                send.animate().alphaBy(1f).setDuration(300);
                send.setText("SEND CODE");
            }
        }.start();
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+263"+getIntent().getStringExtra("mobile"),        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    signupActivity02.this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
        catch (Exception ex){
            ((globalShared) getApplication()).gotException(ex,1);
            ((globalShared) getApplication()).showToast(signupActivity02.this,"check your connection and try again");
        }

    }

    private void setSendCodeListener() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.animate().alpha(0.5f).setDuration(300);
                //count down timer
                final int[] seconds = {60};
                CountDownTimer timer = new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                         seconds[0] -= 1 ;
                         send.setText("available in " + Integer.toString(seconds[0]));
                    }

                    @Override
                    public void onFinish() {
                        send.animate().alphaBy(1f).setDuration(300);
                        send.setText("SEND CODE");
                    }
                }.start();
                try {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+263"+getIntent().getStringExtra("mobile"),        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            signupActivity02.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
                catch (Exception ex){
                    ((globalShared) getApplication()).gotException(ex,1);
                    ((globalShared) getApplication()).showToast(signupActivity02.this,"check your connection and try again");
                }

            }

        });
    }

    /*
        This procedure will auto detect verification
     */
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            setupTempData();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            ((globalShared) getApplication()).showToast(signupActivity02.this,"failed");
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s ;
            manualVerificationListener();
        }
    };




    /*
        Manual verification
     */
    private void manualVerificationListener(){
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                ((globalShared) getApplication()).showToast(signupActivity02.this,"verifying");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, verificationNumber.getText().toString());
                signInWithPhoneAuthCredential(credential);}
                catch (Exception ex){
                    ((globalShared) getApplication()).gotException(ex,1);
                }
            }
        });
    }

    /*
        This procedure will attempt sign in Manual verification
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //unlink
                                FirebaseAuth.getInstance().getCurrentUser().
                                        unlink(PhoneAuthProvider.PROVIDER_ID).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //go to next intent
                                        setupTempData();
                                    }
                                });
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    verificationErrorImage.setImageResource(R.drawable.ic_wrong);
                                    verificationNumber.requestFocus();
                                }
                            }
                        }
                    });
        }
        catch (Exception ex){
            ((globalShared) getApplication()).gotException(ex,1);
        }
    }


    private void setupTempData(){
        gotoNextSignUpActivity();
    }

}
