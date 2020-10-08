package com.wapazock.solveit.alertDialouges;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wapazock.solveit.R;
import com.wapazock.solveit.home.homeQuestionsInterface;
import com.wapazock.solveit.utils.globalShared;

public class reportQuestion {
    Activity activity ;
    AlertDialog dialog ;
    String QUESTION_ID ;

    homeQuestionsInterface update ;

    private static final String TAG = "confirmDeletion";

    public reportQuestion(Activity activity, String QUESTION_ID, homeQuestionsInterface in) {
        this.activity = activity;
        this.QUESTION_ID = QUESTION_ID ;
        this.update = in ;
    }

    public void startAlert(){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //get layout
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        //build
        builder.setView(layoutInflater.inflate(R.layout.dialoug_report_question,null));

        //show
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        //cancel button
        Button cancel = dialog.findViewById(R.id.cancel_button_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlert();
            }
        });

        //confirm
        Button confirm = dialog.findViewById(R.id.yes_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onComplete: " + QUESTION_ID);
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                //register deletion
                mDatabase.child("report_question").push().setValue(QUESTION_ID);
                ((globalShared) activity.getApplication()).showToast(activity,"reported");
                stopAlert();
            }
        });

    }

    public void stopAlert(){
        dialog.dismiss();
    }
}
