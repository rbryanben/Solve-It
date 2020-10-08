package com.wapazock.solveit.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.wapazock.solveit.R;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.utils.globalShared;

public class signupActivity04 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page04);


        //
        ((globalShared) getApplication()).setDefaultTheme(signupActivity04.this);
        //go to home
        TextView home = (TextView)findViewById(R.id.activity_signup_page04_finish);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show notification
                ((globalShared) getApplication()).showNotification("Greetings","We are starting of with notifications enabled, you can turn them off in settings");

                Intent i = new Intent(signupActivity04.this, mainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                Animatoo.animateZoom(signupActivity04.this);
            }
        });
    }

}
