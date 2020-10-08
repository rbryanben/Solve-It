package com.wapazock.solveit.payments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

public class five_dollars extends AppCompatActivity {

    private Button donateFiveEco ;
    private Button donateFiveOne ;
    private ImageView donateFieZim ;

    private ImageView backButton ;
    private String[] PERMISSIONS = new String[] {Manifest.permission.CALL_PHONE , Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS} ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.componet_payment_five);

        //references
        backButton = findViewById(R.id.component_payment_five_back);
        donateFiveEco = findViewById(R.id.payment_ecocash_five);
        donateFiveOne = findViewById(R.id.payment_one_wallet_five);

        donateFieZim = findViewById(R.id.paynow_five);



    }

    @Override
    protected void onResume() {
        super.onResume();

        //check permissions
        if (allPermissionsGranted()){
            listeners();
        }
        else {
            ActivityCompat.requestPermissions(five_dollars.this,PERMISSIONS,124);
        }
    }

    private boolean allPermissionsGranted() {
       for (String permission : PERMISSIONS){
           if (ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
               return false ;
           }
       }

        return true ;
    }


    private void listeners() {
        //back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //eco-cash donation
        donateFiveEco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pay
                String ussdCode = "*" + "151*1*1*782533952*5" + Uri.encode("#");

                Intent dial = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode));
                startActivity(dial);
            }
        });


        //pay-now donation
        donateFieZim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(getString(R.string.paynow_five_url));
            }
        });

    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
