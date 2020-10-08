package com.wapazock.solveit.alertDialouges;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.wapazock.solveit.R;

public class appOutdatedAlert {
    Activity activity ;
    AlertDialog dialog ;

    public appOutdatedAlert(Activity activity) {
        this.activity = activity;
    }

    public void startAlert(){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //get layout
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        //build
        builder.setView(layoutInflater.inflate(R.layout.message_user_alert,null));

        //show
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
