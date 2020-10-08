package com.wapazock.solveit.signup;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.alertDialouges.signingInAlert;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.signin.signInActivity;
import com.wapazock.solveit.sql_helper.helperSQL;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

public class signupActivity00 extends AppCompatActivity{

    /*
    notes :

          xml file - activity_signup_page00

          developer should implement the following
          - check if username exists and does'nt contain white spaces
          - check if email exists
          - password matches and is more than 6 chars long

          developer not to mind
          - already have account option
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page00);
        //exception handlers
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });


        //write file
        ((globalShared) getApplication()).writeNewDataFile("APPLICATIONRUN TRUE");
        ((globalShared) getApplication()).configurationDatabase = new helperSQL(getApplicationContext());

        //set presets
        try {
            ((globalShared) getApplication()).configurationDatabase.writeSQL("INSERT INTO configurations VALUES (1,'notifications','enabled') ");
        }
        catch (Exception ex){
            //finish();
        }

        //assign references
        this.emailCheck = (ImageView)findViewById(R.id.activity_signup_page00_emailCheck);
        this.usernameCheck = (ImageView)findViewById(R.id.activity_signup_page00_usernameCheck);
        this.passwordCheck = (ImageView)findViewById(R.id.activity_signup_page00_passwordCheck);
        this.passwordConfirmCheck = (ImageView)findViewById(R.id.activity_signup_page00_passwordConfirmCheck);

        this.emailEdit = (EditText)findViewById(R.id.activity_signup_page00_emailEdit);
        this.usernameEdit = (EditText)findViewById(R.id.activity_signup_page00_usernameEdit);
        this.passwordEdit = (EditText)findViewById(R.id.activity_signup_page00_passwordEdit);
        this.confirmPasswordEdit = (EditText)findViewById(R.id.activity_signup_page00_confirmPasswordEdit);

        this.usernameError = (TextView)findViewById(R.id.activity_signup_page00_usernameErrorText);
        this.emailError =  (TextView)findViewById(R.id.activity_signup_page00_emailErrorText);
        this.passwordError = (TextView)findViewById(R.id.activity_signup_page00_passwordErrorText);
        this.confirmPasswordError = (TextView)findViewById(R.id.activity_signup_page00_confirmPasswordErrorText);
        this.alreadyHasAccount = (TextView)findViewById(R.id.activity_signup_page00_alreadyHaveAccountLink);

        this.continueButton = (Button)findViewById(R.id.activity_signup_page00_finishButton);

        //hide checks
        hideCheckBoxes();
        //touch
        usernameTouchListener();
        emailTouchListener();
        passwordTouchListener();
        confirmTouchListener();

        //listeners
        usernameListener();
        emailListener();
        passwordListener();
        confirmPasswordListener();
        continueButtonListener();

        //links
        setUpAlreadyHasAccount();
    }

    // global
    private ImageView usernameCheck,emailCheck , passwordCheck , passwordConfirmCheck ;
    private EditText  usernameEdit, emailEdit, passwordEdit, confirmPasswordEdit;
    private TextView  usernameError,emailError, passwordError,confirmPasswordError,alreadyHasAccount;
    private FirebaseAuth mAuth , mAuth2;
    private Button continueButton;
    private static final String TAG = "signupActivity00";

    //flags
    private Boolean usernamePass = false ;
    private Boolean emailPass = false ;
    private Boolean passwordPass = false ;
    private Boolean confirmPass = false ;


    @Override
    protected void onResume() {
        super.onResume();
        ((globalShared) getApplication()).setDefaultTheme(this);

        //remove all focus
        usernameEdit.setFocusable(false);
        emailEdit.setFocusable(false);
        passwordEdit.setFocusable(false);
        confirmPasswordEdit.setFocusable(false);
    }

    /*
      username touched
     */
    private void usernameTouchListener(){
        usernameEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { ;
                usernameEdit.setFocusable(true);
                emailEdit.setFocusable(true);
                passwordEdit.setFocusable(true);
                confirmPasswordEdit.setFocusable(true);
                usernameEdit.setFocusableInTouchMode(true);
                emailEdit.setFocusableInTouchMode(true);
                passwordEdit.setFocusableInTouchMode(true);
                confirmPasswordEdit.setFocusableInTouchMode(true);
                return false;
            }
        });
        usernameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEdit.setFocusableInTouchMode(true);
                usernameEdit.setFocusable(true);
            }
        });
    }

    /*
     email touched
    */
    private void emailTouchListener(){
        emailEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { ;
                usernameEdit.setFocusable(true);
                emailEdit.setFocusable(true);
                passwordEdit.setFocusable(true);
                confirmPasswordEdit.setFocusable(true);
                usernameEdit.setFocusableInTouchMode(true);
                emailEdit.setFocusableInTouchMode(true);
                passwordEdit.setFocusableInTouchMode(true);
                confirmPasswordEdit.setFocusableInTouchMode(true);
                return false;
            }
        });
        emailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEdit.setFocusableInTouchMode(true);
                usernameEdit.setFocusable(true);
            }
        });
    }

    /*
     username touched
    */
    private void passwordTouchListener(){
        passwordEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { ;
                usernameEdit.setFocusable(true);
                emailEdit.setFocusable(true);
                passwordEdit.setFocusable(true);
                confirmPasswordEdit.setFocusable(true);
                usernameEdit.setFocusableInTouchMode(true);
                emailEdit.setFocusableInTouchMode(true);
                passwordEdit.setFocusableInTouchMode(true);
                confirmPasswordEdit.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    /*
     username touched
    */
    private void confirmTouchListener(){
        confirmPasswordEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { ;
                usernameEdit.setFocusable(true);
                emailEdit.setFocusable(true);
                passwordEdit.setFocusable(true);
                confirmPasswordEdit.setFocusable(true);
                usernameEdit.setFocusableInTouchMode(true);
                emailEdit.setFocusableInTouchMode(true);
                passwordEdit.setFocusableInTouchMode(true);
                confirmPasswordEdit.setFocusableInTouchMode(true);
                return false;
            }
        });

    }




    /*
            nb : do not delete
            This procedure is used to proceed to the next signUp page using the dissolve animation but on
            doing this we need to pass in the written parameter { username | email | password } , which we obtained
            from the user
            Goes to "signupActivity01"
         */
    private void gotoNextSignUpActivity(String username, String email , String password){
        Intent signUpActivity01 = new Intent(this,signupActivity01.class);
        //activity parameters
        signUpActivity01.putExtra("username",username);
        signUpActivity01.putExtra("email",email);
        signUpActivity01.putExtra("password",password);

        //start activity
        startActivity(signUpActivity01);
        Animatoo.animateSlideLeft(this);
    }


    /*
    This procedure will hide all check boxes and error texts
     */
    private void hideCheckBoxes(){
        usernameCheck.setVisibility(View.GONE);
        emailCheck.setVisibility(View.GONE);
        passwordCheck.setVisibility(View.GONE);
        passwordConfirmCheck.setVisibility(View.GONE);

        usernameError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmPasswordError.setText("");
    }


    /*
    This procedure will listen on username edit is done and carry out checks
     */
    private void usernameListener(){
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //has lost focus and username text is blank
                if (usernameEdit.getText().toString().isEmpty()){
                    usernameCheck.setImageResource(R.drawable.ic_wrong);
                    usernameCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    usernameError.setText("Username should'nt be empty");
                    usernamePass = false ;
                }
                else if (usernameEdit.getText().toString().contains(" ")){
                    usernameCheck.setImageResource(R.drawable.ic_wrong);
                    usernameCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    usernameError.setText("Username should'nt contain spaces");
                    usernamePass = false ;
                }
                else if (usernameEdit.getText().toString().length() < 4){
                    usernameCheck.setImageResource(R.drawable.ic_wrong);
                    usernameCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    usernameError.setText("Username should have at least 4 chars");
                    usernamePass = false ;
                }

                //user has lost focus and is not blank, then check if username exists on database
                else if (!usernameEdit.getText().toString().isEmpty() && usernameEdit.getText().toString().length() > 3){

                    //check username on database
                    try {
                        //set text to checking
                        usernameError.setText("checking");
                        DatabaseReference usernameCheckRef = FirebaseDatabase.getInstance().getReference();
                        usernameCheckRef.child("userAccounts").orderByChild("username").equalTo(usernameEdit.getText().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() == null) {
                                            //username doesn't - exist correct
                                            usernameCheck.setImageResource(R.drawable.ic_correct);
                                            usernameCheck.setVisibility(View.VISIBLE);
                                            usernameError.setText("");
                                            usernamePass = true ;
                                        } else {
                                            //username exists - exist correct
                                            usernameCheck.setImageResource(R.drawable.ic_wrong);
                                            usernameCheck.setVisibility(View.VISIBLE);
                                            usernameError.setText("Username already exists");
                                            usernamePass = false ;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }
                    catch (Exception ex) {
                        ((globalShared) getApplication()).gotException(ex,0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //has lost focus and username text is blank
                if (usernameEdit.getText().toString().isEmpty()){
                    usernameCheck.setImageResource(R.drawable.ic_wrong);
                    usernameCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    usernameError.setText("Username should'nt be empty");
                    usernamePass = false ;
                }
            }
        });

    }

    /*
    This procedure will listen on the email edit done and carry out checks
     */
    private void emailListener(){
        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (usernameEdit.getText().toString().isEmpty()){
                    usernameCheck.setImageResource(R.drawable.ic_wrong);
                    usernameCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    usernameError.setText("Username should'nt be empty");
                    usernamePass = false ;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //has lost focus and email text is blank
                if (emailEdit.getText().toString().isEmpty()) {
                    //false
                    emailCheck.setImageResource(R.drawable.ic_wrong);
                    emailCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    emailError.setText("Email should'nt be empty");
                } else if (!((globalShared) getApplication()).isAnEmail(emailEdit.getText().toString())) {
                    //false
                    emailCheck.setImageResource(R.drawable.ic_wrong);
                    emailCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    emailError.setText("Invalid email");
                }

                //email has lost focus and is not blank, then check if email exists on database
                else if (!emailEdit.getText().toString().isEmpty() && emailEdit.getText().toString().length() > 3) {
                    emailError.setText("checking");
                    try {
                        //check email already exist or not.
                        mAuth2 = FirebaseAuth.getInstance();
                        mAuth2.fetchSignInMethodsForEmail(emailEdit.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                        try {
                                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                            if (isNewUser) {
                                                //true
                                                emailCheck.setImageResource(R.drawable.ic_correct);
                                                emailCheck.setVisibility(View.VISIBLE);
                                                //set error username should be blank
                                                emailError.setText("");
                                                emailPass = true;
                                            } else {
                                                //false
                                                emailCheck.setImageResource(R.drawable.ic_wrong);
                                                emailCheck.setVisibility(View.VISIBLE);
                                                //set error username should be blank
                                                emailError.setText("Email already exists");
                                                emailPass = false;
                                            }
                                        }

                                         catch (Exception ex){
                                            ((globalShared) getApplication()).gotException(ex,1);
                                        }

                                    }
                                });
                    } catch (Exception ex) {
                        ((globalShared) getApplication()).gotException(ex, 0);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /*
    This procedure will listen on the password edit done
     */
    private void passwordListener(){
        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus == true){
                    passwordError.setText("");
                    passwordCheck.setVisibility(View.GONE);
                }

                if (hasFocus == false && passwordEdit.getText().toString().length() < 6){
                    //wrong
                    passwordError.setText("Password should have at least 6 chars");
                    passwordCheck.setImageResource(R.drawable.ic_wrong);
                    passwordCheck.setVisibility(View.VISIBLE);
                }
                else if (hasFocus == false && passwordEdit.getText().toString().length() > 5){
                    //correct
                    passwordError.setText("");
                    passwordCheck.setImageResource(R.drawable.ic_correct);
                    passwordCheck.setVisibility(View.VISIBLE);
                    passwordPass = true ;
                }
            }
        });
    }

    /*
    This procedure will listen on the confirm password edit done
     */
    private void confirmPasswordListener(){
        confirmPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!passwordEdit.getText().toString().equals(confirmPasswordEdit.getText().toString())){
                    //false
                    confirmPasswordError.setText("Passwords don't match");
                    passwordConfirmCheck.setImageResource(R.drawable.ic_wrong);
                    passwordConfirmCheck.setVisibility(View.VISIBLE);
                    confirmPass = false ;
                }
                else if (passwordEdit.getText().toString().equals(confirmPasswordEdit.getText().toString()) && confirmPasswordEdit.getText().toString().length() > 5){
                    passwordConfirmCheck.setImageResource(R.drawable.ic_correct);
                    passwordConfirmCheck.setVisibility(View.VISIBLE);
                    confirmPasswordError.setText("");
                    confirmPass = true ;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*
    This procedure will set listener for finish
     */
    private void continueButtonListener() {
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEdit.clearFocus();
                confirmPasswordEdit.clearFocus();

                if (usernamePass == false){
                    usernameCheck.setImageResource(R.drawable.ic_wrong);
                    usernameCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    usernameError.setText("Invalid username");
                    usernameEdit.requestFocus();
                }
                if (emailPass == false){
                    emailCheck.setImageResource(R.drawable.ic_wrong);
                    emailCheck.setVisibility(View.VISIBLE);
                    //set error username should be blank
                    emailError.setText("Invalid email");
                    emailEdit.requestFocus();
                }
                if (passwordPass == false){
                    passwordError.setText("Invalid password");
                    passwordCheck.setImageResource(R.drawable.ic_wrong);
                    passwordCheck.setVisibility(View.VISIBLE);
                    passwordEdit.requestFocus();
                }
                if (confirmPass == false){
                    confirmPasswordError.setText("Passwords don't match");
                    passwordConfirmCheck.setImageResource(R.drawable.ic_wrong);
                    passwordConfirmCheck.setVisibility(View.VISIBLE);
                    confirmPasswordEdit.requestFocus();
                }

                if (confirmPass == true && passwordPass == true && emailPass == true && usernamePass == true) {
                    gotoNextSignUpActivity(usernameEdit.getText().toString(), emailEdit.getText().toString(), passwordEdit.getText().toString());
                }


            }
        });

    }

    /*
    This procedure will set up text if the user already has any account
     */
    private void setUpAlreadyHasAccount(){
         alreadyHasAccount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent signIn = new Intent(signupActivity00.this, signInActivity.class);
                 startActivity(signIn);
                 Animatoo.animateZoom(signupActivity00.this);
                 Intent mainActivity = new Intent(signupActivity00.this, com.wapazock.solveit.mainActivity.class);
                 startActivity(mainActivity);
                 Animatoo.animateZoom(signupActivity00.this);
             }
         });
    }

}
