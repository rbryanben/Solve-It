package com.wapazock.solveit.independent_profile_view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

public class activity_profile_view extends AppCompatActivity {

    /*
     This activity is run with passing USERACCOUNT
     */
    //globals
    Activity activity ;

    private TextView name ;
    private TextView surname ;
    private TextView age ;

    private TextView education ;
    private TextView tags ;
    private TextView impact ;

    private TextView votes ;
    private TextView answers ;
    private TextView username ;

    private ImageView profile ;
    private ImageView coverphoto ;
    private userAccount USERACCOUNT ;

    private static final String TAG = "activity_profile_view";
    private ArrayList<String> TAGS  = new ArrayList<>();
    private ArrayList<String> TAGSNAMES  = new ArrayList<>();

    private ImageView back ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_profile_view);

        //activity reference
        activity = activity_profile_view.this ;

        //theme
        ((globalShared) getApplication()).setDefaultTheme(activity);

        //set references
        name = findViewById(R.id.independent_profile_view_basic_name);
        surname = findViewById(R.id.independent_profile_view_basic_surname);
        age = findViewById(R.id.independent_profile_view_basic_age);

        education = findViewById(R.id.independent_profile_view_basic_education);
        tags = findViewById(R.id.independent_profile_view_basic_tags);
        impact = findViewById(R.id.independent_profile_view_meta_impact);

        votes = findViewById(R.id.independent_profile_view_meta_votes);
        answers = findViewById(R.id.independent_profile_view_meta_answers);
        profile = findViewById(R.id.independent_profile_view_profilePicture);
        username = findViewById(R.id.independent_profile_view_usernameText);

        coverphoto = findViewById(R.id.independent_profile_view_backgroundImage);

        //back
       back = findViewById(R.id.independent_profile_view_back);
       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
               Animatoo.animateSlideDown(activity);
           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set user account
        USERACCOUNT = ((globalShared) getApplication()).getPassingUserAccount();
        //load background image
        ((globalShared) getApplication()).loadProfilePictureIntoImageViewWithUrl(coverphoto,activity,USERACCOUNT.getCoverImage());
        //load profile image
        ((globalShared) getApplication()).loadProfilePictureIntoImageViewWithUrl(profile,activity,USERACCOUNT.getProfileImage());

        //basic information
        String[] arr = USERACCOUNT.getFullname().split(" ");
        name.setText("Name                 "+arr[0]);
        surname.setText("Surname           "+arr[arr.length - 1]);
        age.setText("Age                     "+USERACCOUNT.getAge());
        education.setText("Edu                    "+USERACCOUNT.getEducationLevel());
        username.setText(USERACCOUNT.getUsername());


        //get meta data
        DatabaseReference mMeta = FirebaseDatabase.getInstance().getReference();
        try {
            mMeta.child("meta").child(USERACCOUNT.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    answers.setText("Answers            "+ snapshot.child("answers").getValue().toString());
                    votes.setText("Votes                 "+ snapshot.child("votes").getValue().toString());
                    impact.setText("Impact              "+ snapshot.child("impact").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            ((globalShared) getApplication()).gotException(ex,2);
        }

        //get tags
        final DatabaseReference mTags = FirebaseDatabase.getInstance().getReference();
        try {
            mTags.child("userTags").child(USERACCOUNT.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()){
                        TAGS.add(shot.getValue().toString());
                    }

                    //get tags with name
                    for (String tag : TAGS){
                        mTags.child("tags").child(tag).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //get actual names
                                TAGSNAMES.add(snapshot.getValue().toString());
                                tags.setText(tags.getText()+snapshot.getValue().toString()+", ");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            ((globalShared) getApplication()).gotException(ex,2);
        }
    }
}
