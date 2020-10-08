package com.wapazock.solveit.alertDialouges;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.wapazock.solveit.R;

public class termsConditionsAlert {
    Activity activity ;
    AlertDialog dialog ;

    public termsConditionsAlert(Activity activity) {
        this.activity = activity;
    }

    public void startAlert(){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //get layout
        LayoutInflater layoutInflater = activity.getLayoutInflater();


        //build
        builder.setView(layoutInflater.inflate(R.layout.message_user_alert_terms_conditions,null));

        //show
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        //close button
        Button close = dialog.findViewById(R.id.closeTC);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlert();
            }
        });
    }

    public void stopAlert(){
        dialog.dismiss();
    }
}
