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
import com.wapazock.solveit.independent_post_view.repliesGridClickInterface;
import com.wapazock.solveit.utils.globalShared;

public class confirmReplyDeletion {
    Activity activity ;
    AlertDialog dialog ;
    String QUESTION_ID ;

    repliesGridClickInterface update ;

    private static final String TAG = "confirmDeletion";

    public confirmReplyDeletion(Activity activity, String QUESTION_ID, repliesGridClickInterface in) {
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
        builder.setView(layoutInflater.inflate(R.layout.dialoug_confirm_deletion,null));

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
                mDatabase.child("deleted_replies").push().setValue(QUESTION_ID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()){
                              //delete the question
                              mDatabase.child("replies").child(QUESTION_ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           stopAlert();
                                           update.update();
                                           ((globalShared) activity.getApplication()).showToast(activity,"We deleted your reply");
                                       }
                                       else {
                                           ((globalShared) activity.getApplication()).showToast(activity,"failed");
                                           stopAlert();
                                       }
                                  }
                              });
                         }
                         else {
                             ((globalShared) activity.getApplication()).showToast(activity,"failed");
                         }
                    }
                });

            }
        });

    }

    public void stopAlert(){
        dialog.dismiss();
    }
}
