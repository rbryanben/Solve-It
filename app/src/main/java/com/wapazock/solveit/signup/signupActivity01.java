package com.wapazock.solveit.signup;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.termsConditionsAlert;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.utils.globalShared;

public class signupActivity01 extends AppCompatActivity{

    /*
    xml file - activity_signup_page01

     notes :
          - developer should implement gender drop down
          - developer should implement dropdown education levels from database
          - developer should implement mobile number verification as for the safety of our app ,
            should popup custom alert dialouge asking input for verification
          - developer should implement checkbox and terms and conditions custom alert dialouge
     */

    //Global variables
    String username,email,password ;  //from previous intent
    Spinner genderSpinner, educationSpinner ;
    ImageView fullnameError, ageError, mobileError ;
    TextView fullnameErrorText, ageErrorText, mobileErrorText , termsAndConditions ;
    EditText fullnameEdit,ageEdit,mobileEdit ;
    CheckBox checkBox ;
    Button signupButton ;
    Boolean fullnameFlag = false;
    Boolean ageFlag = false ;
    Boolean mobileFlag = false ;
    private static final String TAG = "signupActivity01";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page01);

        //exception handlers
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });

        /*
        Do not delete
         */
        //get parsed parameters and set global
        getParsedParameters();

        //set references
        this.genderSpinner = (Spinner)findViewById(R.id.activity_signup_page01_genderEdit);
        this.educationSpinner = (Spinner)findViewById(R.id.activity_signup_page01_educationEdit);

        this.fullnameError = (ImageView)findViewById(R.id.activity_signup_page01_fullnameCheck);
        this.ageError = (ImageView)findViewById(R.id.activity_signup_page01_ageCheck);
        this.mobileError = (ImageView)findViewById(R.id.activity_signup_page01_mobileCheck);

        this.fullnameEdit = (EditText)findViewById(R.id.activity_signup_page01_fullnameEdit);
        this.ageEdit = (EditText)findViewById(R.id.activity_signup_page01_ageEdit);
        this.mobileEdit = (EditText)findViewById(R.id.activity_signup_page01_mobileEdit);

        this.fullnameErrorText = (TextView)findViewById(R.id.activity_signup_page01_fullnameErrorText);
        this.ageErrorText = (TextView)findViewById(R.id.activity_signup_page01_ageErrorText);
        this.mobileErrorText = (TextView)findViewById(R.id.activity_signup_page01_mobileErrorText);
        this.termsAndConditions = (TextView)findViewById(R.id.activity_signup_page01_termsAndConditionEdit);

        this.signupButton = (Button)findViewById(R.id.activity_signup_page01_signupButton);
        this.checkBox = (CheckBox)findViewById(R.id.activity_signup_page01_checkBox);

        //Spinner gender
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender, R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        //Spinner educations
        ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_education, R.layout.simple_spinner_item);
        educationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        educationSpinner.setAdapter(educationAdapter);

        //disable all errors
        disableErrors();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((globalShared) getApplication()).setDefaultTheme(this);

        //remove focus
        fullnameEdit.setFocusable(false);
        ageEdit.setFocusable(false);
        mobileEdit.setFocusable(false);

        //onClick listeners
        onClickListeners();

        //checks
        fullnameCheck();
        checkAge();
        checkMobile();

        //TCs
        termsConditions();

        //signup button
        finishButon();
    }

    /*
        nb : do not delete
        This procedure is used to proceed to the next signUp page using the dissolve animation but on
        doing this we need to pass in the written parameter { username | email | password } , which we obtained
        from the user
        Goes to "signupActivity01"
     */
     private void gotoNextSignUpActivity(){
         Intent signUpActivity02 = new Intent(this,signupActivity02.class);
         signUpActivity02.putExtra("mobile",mobileEdit.getText().toString());
         //start activity
         startActivity(signUpActivity02);
         Animatoo.animateSlideLeft(this);
     }

     /*
        This procedure will get { username | password | email } from previous
        activity and set them this activity global variables
      */
     private void getParsedParameters(){
         this.username = getIntent().getStringExtra("username");
         this.email = getIntent().getStringExtra("email");
         this.password = getIntent().getStringExtra("password");

     }

     /*
        This procedure will disable all errors
      */
     private void disableErrors(){
         fullnameError.setImageBitmap(null);
         ageError.setImageBitmap(null);
         mobileError.setImageBitmap(null);
     }

     /*
        This procedure will setup on touch events for edit boxes
      */
     private void onClickListeners(){
         fullnameEdit.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 fullnameEdit.setFocusableInTouchMode(true);
                 ageEdit.setFocusableInTouchMode(true);
                 mobileEdit.setFocusableInTouchMode(true);
                 return false;
             }
         });

         ageEdit.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 fullnameEdit.setFocusableInTouchMode(true);
                 ageEdit.setFocusableInTouchMode(true);
                 mobileEdit.setFocusableInTouchMode(true);
                 return false;
             }
         });

         mobileEdit.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 fullnameEdit.setFocusableInTouchMode(true);
                 ageEdit.setFocusableInTouchMode(true);
                 mobileEdit.setFocusableInTouchMode(true);
                 return false;
             }
         });
     }

     /*
        this procedure check fullname
      */
     private void fullnameCheck(){
         fullnameEdit.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                  if (fullnameEdit.getText().toString().length() < 7 || !fullnameEdit.getText().toString().contains(" ")){
                      fullnameError.setImageResource(R.drawable.ic_wrong);
                      fullnameErrorText.setText("Does not meet standard");
                      fullnameFlag = false ;
                  }
                  else {
                      fullnameError.setImageResource(R.drawable.ic_correct);
                      fullnameErrorText.setText("");
                      fullnameFlag = true ;
                  }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
     }

     /*
         Checks age
      */
     private  void checkAge(){
         ageEdit.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (ageEdit.getText().toString().isEmpty() || Integer.parseInt(ageEdit.getText().toString()) < 10
                         || Integer.parseInt(ageEdit.getText().toString()) > 65){

                     ageError.setImageResource(R.drawable.ic_wrong);
                     ageFlag = false ;
                     ageErrorText.setText("Does not meet standard");
                 }
                 else {
                     ageError.setImageResource(R.drawable.ic_correct);
                     ageErrorText.setText("");
                     ageFlag = true ;
                 }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
     }


     /*
         Terms and conditions
      */
     private void termsConditions(){
        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsConditionsAlert teezAndCeez = new termsConditionsAlert(signupActivity01.this);
                teezAndCeez.startAlert();
            }
        });
     }

     /*
         checks mobile number
      */
     private void checkMobile(){
         mobileEdit.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (((globalShared) getApplication()).isValid(mobileEdit.getText().toString())){
                        mobileErrorText.setText("");
                        mobileFlag = true ;
                        mobileError.setImageResource(R.drawable.ic_correct);
                    }
                    else {
                        mobileErrorText.setText("Does not meet standard 7********");
                        mobileFlag = false ;
                        mobileError.setImageResource(R.drawable.ic_wrong);
                    }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
     }


     /*
           Finish button
      */
     private void finishButon(){
          //check parametres
         signupButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (fullnameFlag == false){
                     fullnameError.setImageResource(R.drawable.ic_wrong);
                     fullnameErrorText.setText("Does not meet standard");
                     fullnameFlag = false ;
                     fullnameEdit.requestFocus();
                 }
                 else if (ageFlag == false){
                     ageError.setImageResource(R.drawable.ic_wrong);
                     ageFlag = false ;
                     ageErrorText.setText("Does not meet standard");
                     ageEdit.requestFocus();
                 }
                 else if (mobileFlag == false){
                     mobileErrorText.setText("");
                     mobileFlag = false ;
                     mobileError.setImageResource(R.drawable.ic_wrong);
                     mobileEdit.requestFocus();
                 }
                 else if (checkBox.isChecked() == false){
                     ((globalShared) getApplication()).showToast(signupActivity01.this,"Please agree with the terms and conditions");
                 }
                 else {
                     //next intent
                     setTempData();
                     gotoNextSignUpActivity();
                 }
             }
         });

     }

     private void setTempData(){
         //set class data
         userAccount tempAccount = new userAccount();
         tempAccount.setAge(ageEdit.getText().toString());
         tempAccount.setCoverImage("https://firebasestorage.googleapis.com/v0/b/solve-it-d0a3f.appspot.com/o/application%2Fdefault%20user%20profiles%2Fboard.jpg?alt=media&token=82f6ca94-0756-47c3-adae-ed67994c1680");
         tempAccount.setEducationLevel(educationSpinner.getSelectedItem().toString());
         tempAccount.setFullname(fullnameEdit.getText().toString());
         tempAccount.setGender(genderSpinner.getSelectedItem().toString());
         tempAccount.setMobile(mobileEdit.getText().toString());
         if (genderSpinner.getSelectedItem().toString().equals("female")){
             tempAccount.setProfileImage("https://firebasestorage.googleapis.com/v0/b/solve-it-d0a3f.appspot.com/o/application%2Fdefault%20user%20profiles%2Fpink.jpg?alt=media&token=ad2fcfc5-95fa-4655-a921-c4fef13432bb");
         }
         else if (genderSpinner.getSelectedItem().toString().equals("male")){
             tempAccount.setProfileImage("https://firebasestorage.googleapis.com/v0/b/solve-it-d0a3f.appspot.com/o/application%2Fdefault%20user%20profiles%2Fblue.jpg?alt=media&token=2a1e332e-d5dc-4e39-892e-9f43dcab49aa");
         }
         else {
             tempAccount.setProfileImage("https://firebasestorage.googleapis.com/v0/b/solve-it-d0a3f.appspot.com/o/application%2Fdefault%20user%20profiles%2Fblank_gender.jpg?alt=media&token=36b4e212-87d0-4e2e-a7cc-95eb956d5cd2");
         }
         tempAccount.setUsername(username);

         //global values
         ((globalShared) getApplication()).setTempSignupAccountEmail(email);
         ((globalShared) getApplication()).setTempSignupAccountPassword(password);
         ((globalShared) getApplication()).setTempSignupAccount(tempAccount);
     }
}
