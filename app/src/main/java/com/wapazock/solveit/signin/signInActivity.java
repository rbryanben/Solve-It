package com.wapazock.solveit.signin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.signingInAlert;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.signup.signupActivity00;
import com.wapazock.solveit.signup.signupActivity04;
import com.wapazock.solveit.utils.globalShared;

import org.w3c.dom.Text;

public class signInActivity extends AppCompatActivity{
    /*
         developer should implement
         - sign in with email
         - sign in with Google Mail
         - implement loading
     */

    private static final String TAG = "signInActivity";
    //global
    EditText email,password;
    TextView recovery ;
    Button signInButton,signUp ;
    FirebaseAuth mAuth ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //assign references
        email = (EditText)findViewById(R.id.activity_signin_emailEdit);
        password = (EditText)findViewById(R.id.activity_signin_passwordEdit);
        signInButton = (Button)findViewById(R.id.activity_signing_signButton);
        recovery = (TextView)findViewById(R.id.activity_signin_recoveryText);
        mAuth = FirebaseAuth.getInstance();
        signUp = (Button)findViewById(R.id.activity_signin_signUpButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((globalShared) getApplication()).setDefaultTheme(this);

        //set default theme
        ((globalShared) getApplication()).setDefaultTheme(signInActivity.this);

        //sign in on click
        signInOnClick();
        recoveryOnClick();
        signupOnClick();
        onTouchEvents();
    }

    private void onTouchEvents() {
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                email.setFocusable(false);
                email.setFocusableInTouchMode(true);
                return false;
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setFocusable(true);
                password.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    private void signupOnClick() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(signInActivity.this, signupActivity00.class));
            }
        });
    }

    private void signInOnClick(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((globalShared) getApplication()).isAnEmail(email.getText().toString())){
                    ((globalShared) getApplication()).showToast(signInActivity.this,"enter a valid email");
                }
                else if (password.getText().toString().length() < 6){
                    ((globalShared) getApplication()).showToast(signInActivity.this,"enter a valid password");
                }
                else {
                    //attempt sign in
                    try {
                        signInWith(email.getText().toString(), password.getText().toString());
                    } catch (Exception ex) {
                        ((globalShared) getApplication()).gotException(ex, 0);
                    }
                }
            }
        });
    }


    private void recoveryOnClick(){
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recoveryIntent = new Intent(signInActivity.this,signInActivityRecoverPassword.class);
                startActivity(recoveryIntent);
                Animatoo.animateSlideLeft(signInActivity.this);
            }
        });
    }

    private void signInWith(String email, String password){
        try {
            ((globalShared) getApplication()).hideKeyboard(signInActivity.this);
            ((globalShared) getApplication()).themeKeyboardFixer(signInActivity.this);

            final signingInAlert signingInAlert = new signingInAlert(signInActivity.this);
            signingInAlert.startAlert();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                ((globalShared) getApplication()).loggedInUser = user ;;
                                signingInAlert.stopAlert();

                                //clear previous intents
                                Intent i = new Intent(signInActivity.this, mainActivity.class);
                                i.putExtra("fix","fix");
                                // set the new task and clear flags
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                Animatoo.animateZoom(signInActivity.this);
                            } else {
                                signingInAlert.stopAlert();
                                ((globalShared) getApplication()).showToast(signInActivity.this,"invalid username or password");
                            }
                        }
                    });
        }
        catch (Exception ex){
            ((globalShared) getApplication()).gotException(ex,0);
            ((globalShared) getApplication()).showToast(signInActivity.this,"invalid username or password");
        }
    }
}
