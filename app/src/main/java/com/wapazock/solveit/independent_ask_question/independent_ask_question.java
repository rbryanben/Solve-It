package com.wapazock.solveit.independent_ask_question;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.tags;
import com.wapazock.solveit.home.homeFragment;
import com.wapazock.solveit.independent_gallery.activity_gallery;
import com.wapazock.solveit.independent_post_view.activity_post;
import com.wapazock.solveit.independent_reply_reply.activity_reply_reply;
import com.wapazock.solveit.independent_reply_viewer.activity_reply_viewer;
import com.wapazock.solveit.utils.RecyclerViewClickInterface;
import com.wapazock.solveit.utils.globalShared;
import com.wapazock.solveit.utils.tagsGridAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class independent_ask_question extends AppCompatActivity implements RecyclerViewClickInterface {


    //global variables
    private static final String TAG = "independent_ask_questio";
    private Activity activity ;
    private ImageView questionImage ;

    private ImageView backButton ;
    private EditText questionText;
    private EditText tagText;

    private RecyclerView tagRecycler ;
    private Button setImageButton ;
    private Button submitButton;

    private String IMAGE_URI ;
    private DatabaseReference mDatabase ;
    private ArrayList<tags> TAGS_ARRAY = new ArrayList<>();

    private ArrayList<tags> TEMP_ARRAY ;
    private tags SELECTED_TAG ;
    private Boolean activeFilter ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_ask_question);

        //references
        setImageButton = findViewById(R.id.independent_reply_set_image_button);
        questionImage = findViewById(R.id.independent_reply_answer_image);
        backButton = findViewById(R.id.independent_reply_answer_back);

        questionText = findViewById(R.id.independent_reply_answer_text);
        tagText = findViewById(R.id.independent_ask_question_tag_input_text);
        tagRecycler = findViewById(R.id.independent_ask_question_tag);

        submitButton = findViewById(R.id.independent_reply_answer_submit);

        //variables
        activity = independent_ask_question.this ;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        activeFilter = false ;

        //get tags
        getTags();

        //set listeners
        setListeners();
    }

    private void getTags() {
       mDatabase.child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot shot : snapshot.getChildren()){
                   TAGS_ARRAY.add(shot.getValue(tags.class));
               }

               recyclerViewAskAdapter adapter = new recyclerViewAskAdapter(TAGS_ARRAY, independent_ask_question.this );
               tagRecycler.setAdapter(adapter);
               tagRecycler.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false));
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check if image has been selected
        IMAGE_URI = ((globalShared) getApplication()).getPassingGallery();

        if (IMAGE_URI != null){
            ((globalShared) getApplication()).loadProfilePictureIntoImageViewWithUrl(questionImage,activity,IMAGE_URI);
        }
    }

    private void setListeners() {

        //set image
        setImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(independent_ask_question.this, activity_gallery.class));
                Animatoo.animateSlideUp(activity);
            }
        });

        //back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideDown(activity);
            }
        });

        //tag select
        tagText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tagText.setTypeface(Typeface.DEFAULT);
                tagText.getText().clear();
                tagText.setFocusableInTouchMode(true);
                tagText.setFocusable(true);
                SELECTED_TAG = null ;
                return false;
            }
        });

        //tag on typing
        tagText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tagText.getText().toString().isEmpty()){
                    recyclerViewAskAdapter adapter = new recyclerViewAskAdapter(TAGS_ARRAY, independent_ask_question.this );
                    tagRecycler.setAdapter(adapter);
                    activeFilter = false ;
                    tagRecycler.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false));
                }
                else {
                    activeFilter = true ;
                    TEMP_ARRAY = new ArrayList<>();
                    for (tags tag : TAGS_ARRAY){
                        if (tag.getName().contains(tagText.getText().toString())){
                            TEMP_ARRAY.add(tag);
                        }
                    }

                    recyclerViewAskAdapter adapter = new recyclerViewAskAdapter(TEMP_ARRAY, independent_ask_question.this );
                    tagRecycler.setAdapter(adapter);
                    tagRecycler.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check char count
                if (!(questionText.getText().toString().length() < 40)){
                    if (SELECTED_TAG != null){
                         //make question
                        makeQuestion();
                    }
                    else {
                        ((globalShared) getApplication()).showToast(activity,"Please select a tag");
                    }
                }
                else {
                    ((globalShared) getApplication()).showToast(activity,"Question have more than 40 Chars");
                }

            }
        });

    }

    private void makeQuestion() {
        final pleaseWaitAlert alert = new pleaseWaitAlert(activity);
        alert.startAlert();

        final questions tempQuestion = new questions();
        tempQuestion.setQuestionID(((globalShared) getApplication()).generateRandomChars(24));
        tempQuestion.setQuestion(questionText.getText().toString());
        tempQuestion.setVotes("0");
        tempQuestion.setUserAccount(((globalShared) getApplication()).loggedInUser.getUid());
        tempQuestion.setTag(SELECTED_TAG.getId());
        tempQuestion.setAnswered("UNSOLVED");
        tempQuestion.setTime(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date())));
        if (IMAGE_URI == null){
            //register with no image
            mDatabase.child("userAccounts").child(((globalShared)getApplication()).loggedInUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tempQuestion.setQuestionImage(snapshot.child("coverImage").getValue().toString());
                    //upload to database
                    mDatabase.child("questions").child(tempQuestion.getQuestionID()).setValue(tempQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alert.stopAlert();
                            Intent backToQuestion = new Intent(activity, activity_post.class);
                            ((globalShared) getApplication()).setPassingQuestion(tempQuestion);
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
        else {
            //upload file
            Uri file = Uri.fromFile(new File(IMAGE_URI));

            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = mStorageRef.child("posts").child(tempQuestion.getUserAccount()).child(System.currentTimeMillis()+".jpg");

            try {
                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //register with image
                                        tempQuestion.setQuestionImage(uri.toString());

                                        //upload to database
                                        mDatabase.child("questions").child(tempQuestion.getQuestionID()).setValue(tempQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                alert.stopAlert();
                                                Intent backToQuestion = new Intent(activity, activity_post.class);
                                                ((globalShared) getApplication()).setPassingQuestion(tempQuestion);
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
                                alert.stopAlert();
                                ((globalShared) getApplication()).showToast(activity,"something went wrong");
                            }
                        });
            }
            catch (Exception ex){
                ((globalShared) getApplication()).gotException(ex,2);
            }
        }

    }

    @Override
    public void onItemClick(int position) {
        ((globalShared) getApplication()).hideKeyboard(activity);
        tagText.setFocusableInTouchMode(false);
        tagText.setFocusable(false);
        if (activeFilter) {
            SELECTED_TAG = TEMP_ARRAY.get(position);
            tagText.setText(SELECTED_TAG.getName());
            tagText.setTypeface(tagText.getTypeface(), Typeface.BOLD);
        } else {
            SELECTED_TAG = TAGS_ARRAY.get(position);
            tagText.setText(SELECTED_TAG.getName());
            tagText.setTypeface(tagText.getTypeface(), Typeface.BOLD);
        }
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
