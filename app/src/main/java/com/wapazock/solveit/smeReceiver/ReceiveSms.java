package com.wapazock.solveit.smeReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.wapazock.solveit.R;

public class ReceiveSms extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null ;
            String msg_from;

            if (bundle != null){
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0 ; i < msgs.length ; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        if (msgBody.contains("Transfer Confirmation. RTGS$") && msgBody.contains("RYAN BEN Approval Code")){

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Notification notification = new NotificationCompat.Builder(context, "channel1")
                                            .setContentText("Donation confirmed. Thank you from the Wapazock team")
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                            .setContentTitle("Donation")
                                            .build();


                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    notificationManager.notify(1,notification);
                                }
                            },9000);

                        }
                        else {

                        }
                    }
                }
                catch (Exception ex){

                }
            }
        }
    }
}
