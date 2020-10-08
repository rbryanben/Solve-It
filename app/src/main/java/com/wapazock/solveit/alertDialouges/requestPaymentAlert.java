package com.wapazock.solveit.alertDialouges;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

public class requestPaymentAlert {
    Activity activity ;
    AlertDialog dialog ;
    private String Amount ;

    public requestPaymentAlert(Activity activity) {
        this.activity = activity;
    }

    public void startAlert(){

        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //get layout
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        //build
        builder.setView(layoutInflater.inflate(R.layout.user_message_alert_requests_payment,null));

        //show
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        //database
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //set data
        final TextView cancel = dialog.findViewById(R.id.requestPaymentCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlert();
            }
        });


        final TextView amountText = dialog.findViewById(R.id.message_user_alert_messageText);

        mDatabase.child("meta").child(((globalShared) activity.getApplication()).loggedInUser.getUid()).child("pendingAmount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amountText.setText(amountText.getText() + "ZWL $"+snapshot.getValue().toString());
                Amount = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Button requestButton = (Button) dialog.findViewById(R.id.request_payment_proceed);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment tempPayment = new payment();
                tempPayment.setAmount(Amount);
                tempPayment.setPerson(((globalShared) activity.getApplication()).loggedInUser.getUid());
                mDatabase.child("requestPayment").push().setValue(tempPayment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child("meta").child(((globalShared) activity.getApplication()).loggedInUser.getUid()).child("pendingAmount").setValue("0.00").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ((globalShared) activity.getApplication()).showToast(activity,"requested");
                                    stopAlert();
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

    public class payment{
        String person ;
        String amount ;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
