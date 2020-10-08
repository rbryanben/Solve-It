package com.wapazock.solveit.payments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

public class payment extends AppCompatActivity {

    
    private ImageView backButton ;
    private EditText amountText ;
    private Button ecoCashButton;
    
    private Button netOne ;
    private ImageView paynow ;
    private String[] PERMISSIONS = new String[] {Manifest.permission.CALL_PHONE , Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS} ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_donate_more);


        //references
        backButton = findViewById(R.id.imageView4);
        amountText = findViewById(R.id.editText);
        ecoCashButton = findViewById(R.id.payment_ecocash_five);

        netOne = findViewById(R.id.payment_one_wallet_five);
        paynow = findViewById(R.id.paynow_five);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListeners();
    }

    private void setListeners() {
        //check permissions
        if (allPermissionsGranted()){
            //back button
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            //ecoCashDonate
            ecoCashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!amountText.getText().toString().isEmpty()) {
                        try {
                            Integer amount =  Integer.parseInt(amountText.getText().toString());
                            //pay
                            String ussdCode = "*" + "151*1*1*782533952*" + amount + Uri.encode("#");

                            Intent dial = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode));
                            startActivity(dial);
                        }
                        catch (Exception ex){
                            ((globalShared) getApplication()).showToast(payment.this,"check your entry");
                        }

                    }
                    else {
                        ((globalShared) getApplication()).showToast(payment.this,"check your entry");
                    }
                }
            });

        }
        else {
            ActivityCompat.requestPermissions(payment.this,PERMISSIONS,127);
            setListeners();
        }


        //paynow
        paynow.setOnClickListener(new View.OnClickListener() {
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

    private boolean allPermissionsGranted() {
        for (String permission : PERMISSIONS){
            if (ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                return false ;
            }
        }
        return true ;
    }

}
