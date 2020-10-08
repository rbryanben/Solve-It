package com.wapazock.solveit.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.utils.globalShared;

public class changePassword extends AppCompatActivity {

    private EditText oldPassword , newPassword ;
    private Button cancelButton , changeButton ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_change_password);

        //references
        cancelButton = findViewById(R.id.cancel_button);
        changeButton  = findViewById(R.id.activity_signin_recover_sendEmail);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);

        //variables
        mAuth = FirebaseAuth.getInstance();

        //set listeners
        setListeners();
    }

    private void setListeners() {

        //cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideDown(changePassword.this);
            }
        });

        //chane password
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((globalShared) getApplication()).hideKeyboard(changePassword.this);
                final pleaseWaitAlert  alert = new pleaseWaitAlert(changePassword.this);
                alert.startAlert();
                //get user
                final FirebaseUser user = mAuth.getCurrentUser();

                try {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword.getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                           alert.stopAlert();
                                           ((globalShared) getApplication()).changedPassword = true ;
                                           finish();
                                     }
                                     else {
                                         alert.stopAlert();
                                         ((globalShared) getApplication()).showToast(changePassword.this,"failed");
                                     }
                                }
                            });
                        }
                        else {
                            ((globalShared) getApplication()).showToast(changePassword.this,"old-password is incorrect");
                            alert.stopAlert();
                        }
                    }
                });
                }
                catch (Exception ex){
                    ((globalShared) getApplication()).showToast(changePassword.this,"failed");
                }
            }
        });

    }
}
