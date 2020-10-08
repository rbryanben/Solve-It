package com.wapazock.solveit.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

public class signInActivityRecoverPassword extends AppCompatActivity{


    //global variables
    Button sendRecoveryEmail , cancel;
    EditText email ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siging_recover_password);


        //set references
        sendRecoveryEmail = (Button)findViewById(R.id.activity_signin_recover_sendEmail);
        cancel = (Button)findViewById(R.id.activity_signin_recover_cancelButton);
        email = (EditText)findViewById(R.id.activity_signin_recover_emailEdit);

        //cancel on click
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //send mail
        sendRecoveryEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((globalShared) getApplication()).hideKeyboard(signInActivityRecoverPassword.this);
                if (!((globalShared) getApplication()).isAnEmail(email.getText().toString())){
                    ((globalShared) getApplication()).showToast(signInActivityRecoverPassword.this,"enter a valid email");
                }
                else {
                    try {
                        //send recovery email
                        sendRecoveryEmail.animate().alpha(0.5f).setDuration(200);
                        ((globalShared) getApplication()).showToast(signInActivityRecoverPassword.this,"please wait");
                        sendRecoveryEmail.setEnabled(false);
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    ((globalShared) getApplication()).showToast(signInActivityRecoverPassword.this,"check your email for recovery");
                                    sendRecoveryEmail.animate().alpha(1f).setDuration(200);
                                    sendRecoveryEmail.setEnabled(true);
                            }
                        });
                    }
                    catch (Exception ex){
                        ((globalShared) getApplication()).gotException(ex,4);
                    }
                }

            }
        });

        //focusable
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                email.setFocusableInTouchMode(true);
                email.setFocusable(true);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((globalShared) getApplication()).setDefaultTheme(signInActivityRecoverPassword.this);
    }
}
