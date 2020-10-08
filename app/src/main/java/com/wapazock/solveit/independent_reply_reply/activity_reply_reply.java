package com.wapazock.solveit.independent_reply_reply;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.independent_gallery.activity_gallery;
import com.wapazock.solveit.independent_post_view.activity_post;
import com.wapazock.solveit.independent_reply_viewer.activity_reply_viewer;
import com.wapazock.solveit.utils.globalShared;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class activity_reply_reply extends AppCompatActivity {

    private replies REPLY ;
    private static final String TAG = "activity_reply_reply";
    private ImageView image ;
    
    private ImageView back ;
    private Button submit ;
    private Button setImage ;
    
    private EditText text ;
    private Context context ;
    private String IMAGE_URI ;

    private Boolean hasImage ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_reply_answer);

        //get pre required data
        REPLY = ((globalShared) getApplication()).getPassingReply();

        //theme
        ((globalShared) getApplication()).setDefaultTheme(activity_reply_reply.this);

        //references
        back = findViewById(R.id.independent_reply_answer_back);
        image = findViewById(R.id.independent_reply_answer_image);
        submit = findViewById(R.id.independent_reply_answer_submit);
        
        setImage = findViewById(R.id.independent_reply_set_image_button);
        text = findViewById(R.id.independent_reply_answer_text);
        context = this ;

        hasImage = false ;

        //set listeners 
        setListeners();
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check for image
        IMAGE_URI = ((globalShared) getApplication()).getPassingGallery();
        if (IMAGE_URI != null) {
            try {
                Glide.with(context)
                        .asBitmap()
                        .placeholder(R.drawable.blurred_image)
                        .load(IMAGE_URI)
                        .into(image);


                //extend image
                image.getLayoutParams().height = 800 ;

                //has image
                hasImage = true ;
            }
            catch (Exception ex){
                ((globalShared) getApplication()).gotException(ex,3);
            }
        }
    }

    private void setListeners() {

        //back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideDown(context);
            }
        });

        //set image
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(context,activity_gallery.class);
                startActivity(gallery);
                Animatoo.animateSlideUp(context);
            }
        });

        //submit button
        //submit button  listener
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final replies tempReply = new replies();
                //check text
                if (text.getText().length() < 50){
                    ((globalShared) getApplication()).showToast(activity_reply_reply.this,"Text does not meet standard");
                }
                else{
                    //wait
                    final pleaseWaitAlert uploadingWait = new pleaseWaitAlert(activity_reply_reply.this);
                    uploadingWait.startAlert();

                    //set pre - data
                    tempReply.setReplyText(text.getText().toString());
                    tempReply.setUserAccount(((globalShared) getApplication()).loggedInUser.getUid());
                    tempReply.setReplyTo(REPLY.getReplyID());


                    final DatabaseReference mReplies = FirebaseDatabase.getInstance().getReference();

                    String currentDate = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date());
                    tempReply.setTime(Long.parseLong(currentDate));

                    //image
                    if (hasImage){
                        //upload image and get url
                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        //upload file
                        Uri file = Uri.fromFile(new File(IMAGE_URI));

                        //mStorageRef = FirebaseStorage.getInstance().getReference().child("images");
                        StorageReference riversRef = mStorageRef.child("replies").child(tempReply.getUserAccount()).child(currentDate+".jpg");

                        try {
                            riversRef.putFile(file)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    //register with image
                                                    tempReply.setReplyImage(uri.toString());

                                                    tempReply.setReplyID(((globalShared) getApplication()).generateRandomChars(24));
                                                    //upload to database
                                                    mReplies.child("repliesToReply").child(tempReply.getReplyID()).setValue(tempReply).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            uploadingWait.stopAlert();

                                                            Intent backToQuestion = new Intent(activity_reply_reply.this, activity_reply_viewer.class);
                                                            ((globalShared) getApplication()).setPassingReply(REPLY);
                                                            startActivity(backToQuestion);
                                                            Animatoo.animateSlideDown(activity_reply_reply.this);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception exception) {
                                            uploadingWait.stopAlert();
                                            ((globalShared) getApplication()).showToast(activity_reply_reply.this,"something went wrong");
                                        }
                                    });
                        }
                        catch (Exception ex){
                            ((globalShared) getApplication()).gotException(ex,2);
                        }

                    }
                    else {
                        //register with no image
                        mReplies.child("userAccounts").child(((globalShared)getApplication()).loggedInUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tempReply.setReplyImage(snapshot.child("coverImage").getValue().toString());

                                tempReply.setReplyID(((globalShared) getApplication()).generateRandomChars(24));
                                //upload to database
                                mReplies.child("repliesToReply").child(tempReply.getReplyID()).setValue(tempReply).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        uploadingWait.stopAlert();
                                        Intent backToQuestion = new Intent(activity_reply_reply.this, activity_reply_viewer.class);
                                        ((globalShared) getApplication()).setPassingReply(REPLY);
                                        startActivity(backToQuestion);
                                        Animatoo.animateSlideDown(activity_reply_reply.this);
                                        finish();
                                    }

                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }
        });
    }
}
