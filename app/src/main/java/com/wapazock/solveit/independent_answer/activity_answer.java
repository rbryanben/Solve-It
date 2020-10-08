package com.wapazock.solveit.independent_answer;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.wapazock.solveit.globalClasses.notifications;
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.independent_gallery.activity_gallery;
import com.wapazock.solveit.independent_post_view.activity_post;
import com.wapazock.solveit.utils.globalShared;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class activity_answer extends AppCompatActivity {

    private Activity activity;
    private ImageView image ;
    private ImageView back ;

    private TextView text ;
    private Button load ;
    private  Button submit ;
    private String passingImage ;

    private static final String TAG = "activity_answer";
    private boolean hasImage ;
    private questions QUESTION ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_question_answer);

        //context
        activity = activity_answer.this ;
        ((globalShared) getApplication()).setDefaultTheme(activity);

        //references
        image = findViewById(R.id.independent_question_answer_image);
        back = findViewById(R.id.independent_question_answer_back);
        text = findViewById(R.id.independent_question_answer_text);

        load = findViewById(R.id.independent_question_set_image_button);
        submit = findViewById(R.id.independent_question_answer_submit);

        //variables
        hasImage = false ;
        QUESTION = ((globalShared) getApplication()).getPassingQuestion();


        //listeners
        allListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 1 running");

        passingImage = ((globalShared) getApplication()).getPassingGallery();
        if (passingImage != null){
            hasImage = true ;
            Glide.with(activity)
                    .asBitmap()
                    .placeholder(R.drawable.blurred_image)
                    .load(passingImage)
                    .into(image);

            //extend height
            image.getLayoutParams().height = 800 ;
        }
    }

    private void allListeners() {
        //back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideDown(activity);
            }
        });

        //set image
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(activity, activity_gallery.class);
                startActivity(gallery);
                Animatoo.animateSlideUp(activity);
            }
        });


        //submit button  listener
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final replies tempReply = new replies();
                //check text
                if (text.getText().length() < 25){
                    ((globalShared) getApplication()).showToast(activity,"Text does not meet standard");
                }
                else{
                    //wait
                    final pleaseWaitAlert uploadingWait = new pleaseWaitAlert(activity);
                    uploadingWait.startAlert();

                    //set pre - data
                    tempReply.setReplyText(text.getText().toString());
                    tempReply.setUserAccount(((globalShared) getApplication()).loggedInUser.getUid());
                    tempReply.setReplyTo(QUESTION.getQuestionID());
                    tempReply.setToQuestion("yes");

                    final DatabaseReference mReplies = FirebaseDatabase.getInstance().getReference();

                    String currentDate = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date());
                    tempReply.setTime(Long.parseLong(currentDate));

                    //image
                    if (hasImage){
                        //upload image and get url
                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        //upload file
                        Uri file = Uri.fromFile(new File(passingImage));

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
                                                mReplies.child("replies").child(tempReply.getReplyID()).setValue(tempReply).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Handler handler = new Handler();
                                                        uploadingWait.stopAlert();
                                                        Intent backToQuestion = new Intent(activity,activity_post.class);
                                                        ((globalShared) getApplication()).setPassingQuestion(QUESTION);
                                                        startActivity(backToQuestion);
                                                        Animatoo.animateSlideDown(activity);
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
                                            ((globalShared) getApplication()).showToast(activity,"something went wrong");
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
                                mReplies.child("replies").child(tempReply.getReplyID()).setValue(tempReply).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Handler handler = new Handler();
                                        uploadingWait.stopAlert();

                                        Intent backToQuestion = new Intent(activity,activity_post.class);
                                        ((globalShared) getApplication()).setPassingQuestion(QUESTION);
                                        startActivity(backToQuestion);
                                        Animatoo.animateSlideDown(activity);
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
