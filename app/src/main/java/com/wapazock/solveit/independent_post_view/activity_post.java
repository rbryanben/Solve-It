package com.wapazock.solveit.independent_post_view;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.confirmReplyDeletion;
import com.wapazock.solveit.alertDialouges.reportReply;
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_answer.activity_answer;
import com.wapazock.solveit.independent_imageView.activity_image_view;
import com.wapazock.solveit.independent_profile_view.activity_profile_view;
import com.wapazock.solveit.independent_reply_viewer.activity_reply_viewer;
import com.wapazock.solveit.utils.LinearSpacesItemDecoration;
import com.wapazock.solveit.utils.globalShared;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class activity_post extends AppCompatActivity  implements repliesGridClickInterface {

    /*
       This procedure will show a post and its replies
       it is run with a question.class instance used to fetch its data
     */

    private static final String TAG = "activity_post";
    private ImageView profileImage;
    private ImageView questionImage;
    private Button answerButton;

    private TextView questionText;
    private TextView repliesText;
    private Button solvedButton;

    private RecyclerView repliesGrid;
    private ImageView backButton;

    private Activity activity;
    private questions QUESTION;

    private ArrayList<replies> REPLIES = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_post_view);

        //get passing question
        activity = activity_post.this;
        QUESTION = ((globalShared) getApplication()).getPassingQuestion();

        //theme
        ((globalShared) getApplication()).setDefaultTheme(activity);

        //references
        profileImage = (ImageView) findViewById(R.id.independent_post_profileImage);
        questionImage = (ImageView) findViewById(R.id.independent_post_image);
        repliesGrid = (RecyclerView) findViewById(R.id.independent_reply_replies_grid);

        questionText = (TextView) findViewById(R.id.independent_post_questionText);
        repliesText = (TextView) findViewById(R.id.independent_post_respondersText);
        answerButton = (Button) findViewById(R.id.independent_reply_image_answerButton);
        backButton = (ImageView) findViewById(R.id.independent_post_back);

        solvedButton = (Button) findViewById(R.id.independent_reply_solvedButton);

        CountDownTimer timer = new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                //set default theme (hide navigation bar)
                ((globalShared) getApplication()).hideKeyboard(activity);
                ((globalShared) getApplication()).setDefaultTheme(activity_post.this);
                ((globalShared) getApplication()).themeKeyboardFixer(activity_post.this);
            }

            @Override
            public void onFinish() {

            }
        }.start();

        //back listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideDown(activity);
            }
        });

        //image view focus
        questionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageView = new Intent(activity, activity_image_view.class);
                imageView.putExtra("url", QUESTION.getQuestionImage());
                startActivity(imageView);
                Animatoo.animateZoom(activity);
            }
        });


        //get data
        try {
            REPLIES.clear();
            getData();
            setButtons();
        } catch (Exception ex) {
            ((globalShared) getApplication()).gotException(ex, 2);
        }
        if (QUESTION.getAnswered().equals("SOLVED")) {
            solvedButton.setText("SOLVED");
            solvedButton.setBackgroundResource(R.drawable.background_green_button_white);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setButtons() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final DatabaseReference mUserAccount = FirebaseDatabase.getInstance().getReference();
                    mUserAccount.child("userAccounts").child(QUESTION.getUserAccount()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ((globalShared) getApplication()).setPassingUserAccount(snapshot.getValue(userAccount.class));
                            Intent replyView = new Intent(activity, activity_profile_view.class);
                            startActivity(replyView);
                            Animatoo.animateSlideUp(activity);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } catch (Exception ex) {
                    ((globalShared) getApplication()).gotException(ex, 2);
                }
            }
        });

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyView = new Intent(activity, activity_answer.class);
                ((globalShared) getApplication()).setPassingQuestion(QUESTION);
                startActivity(replyView);
                Animatoo.animateSlideUp(activity);
            }
        });

    }


    private void getData() {
        //get profile picture
        ((globalShared) getApplication()).setDefaultTheme(this);
        try {
            ((globalShared) getApplication()).loadProfilePictureIntoImageViewWithID(QUESTION.getUserAccount(), profileImage, activity);
        } catch (Exception ex) {
            ((globalShared) getApplication()).gotException(ex, 2);
        }
        //get Question Image
        try {
            Glide.with(activity)
                    .asBitmap()
                    .load(QUESTION.getQuestionImage())
                    .into(questionImage);
        } catch (Exception ex) {
            ((globalShared) getApplication()).gotException(ex, 2);
        }

        //set question text
        questionText.setText(QUESTION.getQuestion());

        //get replies
        getQuestionsArrayUsingReplyOrQuestionID(QUESTION.getQuestionID());
    }

    public void getQuestionsArrayUsingReplyOrQuestionID(String id) {
        //get questions
        DatabaseReference mReplies = FirebaseDatabase.getInstance().getReference();
        mReplies.child("replies").orderByChild("replyTo").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    replies tempReply = shot.getValue(replies.class);
                    //add to array
                    REPLIES.add(tempReply);

                }

                //set replies count
                if (REPLIES.size() == 0) {
                    repliesText.setText("NO RESPONCE");
                } else if (REPLIES.size() == 1) {
                    repliesText.setText("1 PERSON RESPONDED");
                } else {
                    repliesText.setText(Integer.toString(REPLIES.size()) + " PEOPLE RESPONDED");
                }

                //set adapter
                repliesRecyclerAdapter adapter = new repliesRecyclerAdapter(REPLIES, activity_post.this, activity_post.this);
                repliesGrid.setAdapter(adapter);
                repliesGrid.addItemDecoration(new LinearSpacesItemDecoration(10));
                repliesGrid.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onReplyClicked(int position) {
        //passing data
        ((globalShared) getApplication()).setPassingReply(REPLIES.get(position));
        Intent replyView = new Intent(activity, activity_reply_viewer.class);
        startActivity(replyView);
        Animatoo.animateSlideUp(activity);
    }

    @Override
    public void onLongReplyClicked(int position) {
        if (REPLIES.get(position).getUserAccount().equals(((globalShared) getApplication()).loggedInUser.getUid())){
            confirmReplyDeletion dialoug = new confirmReplyDeletion(this,REPLIES.get(position).getReplyID(),this);
            dialoug.startAlert();
        }
        else {
            reportReply dialoug = new reportReply(this,REPLIES.get(position).getReplyID());
            dialoug.startAlert();
        }

    }

    @Override
    public void update() {
        REPLIES.clear();
        getQuestionsArrayUsingReplyOrQuestionID(QUESTION.getQuestionID());
    }

}

