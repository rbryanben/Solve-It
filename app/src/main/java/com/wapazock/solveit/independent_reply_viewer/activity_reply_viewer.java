package com.wapazock.solveit.independent_reply_viewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.confirmReplyDeletion;
import com.wapazock.solveit.alertDialouges.confirmReplyReplyDeletion;
import com.wapazock.solveit.alertDialouges.reportReply;
import com.wapazock.solveit.alertDialouges.reportReplyReply;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_imageView.activity_image_view;
import com.wapazock.solveit.independent_post_view.repliesGridClickInterface;
import com.wapazock.solveit.independent_post_view.repliesRecycleShimmerAdapter;
import com.wapazock.solveit.independent_post_view.repliesRecyclerAdapter;
import com.wapazock.solveit.independent_profile_view.activity_profile_view;
import com.wapazock.solveit.independent_reply_reply.activity_reply_reply;
import com.wapazock.solveit.utils.LinearSpacesItemDecoration;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_reply_viewer extends AppCompatActivity implements repliesGridClickInterface {

    //class variables

    private static final String TAG = "activity_reply_viewer";
    private replies REPLY ;
    private CircleImageView replierProfilePicture ;

    private ImageView replierImage ;
    private TextView replierText  ;
    private Context context;

    private TextView respondersText ;
    private DatabaseReference mDatabase ;
    private RecyclerView repliesGrid ;

    private ImageView backButton ;
    private Button replyButton ;
    private ArrayList<replies> repliesArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_reply_view);

        //set main data
        this.REPLY = ((globalShared) getApplication()).getPassingReply();
        context = activity_reply_viewer.this ;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set references
        replierProfilePicture = findViewById(R.id.independent_reply_profileImage);
        replierImage = findViewById(R.id.independent_reply_image);
        replierText = findViewById(R.id.independent_reply_replyText);

        respondersText = findViewById(R.id.independent_reply_respondersText);
        repliesGrid = findViewById(R.id.independent_reply_view_replies_grid);
        backButton = findViewById(R.id.independent_post_back);

        replyButton = findViewById(R.id.independent_reply_view_replyButton);

        //set data
        replierText.setText(REPLY.getReplyText());
        ((globalShared) getApplication()).loadProfilePictureIntoImageViewWithID(REPLY.getUserAccount(),replierProfilePicture,context);
        ((globalShared) getApplication()).loadProfilePictureIntoImageViewWithUrl(replierImage,activity_reply_viewer.this,REPLY.getReplyImage());

        getResponders();
        setListeners();
    }


    private void setListeners() {
        //back onclick
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSlideDown(context);
            }
        });

        //profile image click
        replierProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final DatabaseReference mUserAccount = FirebaseDatabase.getInstance().getReference();
                    mUserAccount.child("userAccounts").child(REPLY.getUserAccount()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ((globalShared) getApplication()).setPassingUserAccount(snapshot.getValue(userAccount.class));
                            Intent replyView = new Intent(activity_reply_viewer.this, activity_profile_view.class);
                            startActivity(replyView);
                            Animatoo.animateSlideUp(context);
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
        });

        //view image
        replierImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageView = new Intent(context, activity_image_view.class);
                imageView.putExtra("url",REPLY.getReplyImage());
                startActivity(imageView);
                Animatoo.animateZoom(context);
            }
        });

        //reply button click
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerReply = new Intent(context, activity_reply_reply.class);
                ((globalShared) getApplication()).setPassingReply(REPLY);
                startActivity(answerReply);
                Animatoo.animateSlideUp(context);
            }
        });
    }

    /*
    This procedure should set the number of people who responded to this reply
    this also get replies
     */
    private void getResponders() {
        DatabaseReference mReplyCount = mDatabase ;
        mReplyCount.child("repliesToReply").orderByChild("replyTo").equalTo(REPLY.getReplyID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long respondersCount = snapshot.getChildrenCount();

                if (respondersCount == 0 ){
                    respondersText.setText("NO REPLY");
                }
                else if (respondersCount == 1){
                    respondersText.setText("1 PERSON RESPONDED");
                }
                else {
                    respondersText.setText(Long.toString(respondersCount) + " PEOPLE RESPONDED");
                }

                //shimmer
                final ArrayList<String> shimmerArray = new ArrayList<>();
                for (int i = 0 ; i != snapshot.getChildrenCount(); i++){
                    shimmerArray.add("blank");
                }
                repliesRecycleShimmerAdapter adapter = new repliesRecycleShimmerAdapter(shimmerArray,activity_reply_viewer.this,activity_reply_viewer.this);
                repliesGrid.setAdapter(adapter);
                repliesGrid.addItemDecoration(new LinearSpacesItemDecoration(10));
                repliesGrid.setLayoutManager(new LinearLayoutManager(activity_reply_viewer.this,RecyclerView.VERTICAL,false));

                //add to replies
                for (DataSnapshot shot : snapshot.getChildren()){
                    replies tempReply = shot.getValue(replies.class);
                    repliesArray.add(tempReply);
                }
                setupGrid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupGrid() {
        repliesRecyclerAdapter adapter = new repliesRecyclerAdapter(repliesArray,this,activity_reply_viewer.this);
        repliesGrid.setAdapter(adapter);
        repliesGrid.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
     }

    @Override
    public void onReplyClicked(int position) {
        Intent replyView = new Intent(context,activity_reply_viewer.class);
        ((globalShared) getApplication()).setPassingReply(repliesArray.get(position));
        startActivity(replyView);
        Animatoo.animateSlideUp(context);
    }

    @Override
    public void onLongReplyClicked(int position) {
        if (repliesArray.get(position).getUserAccount().equals(((globalShared) getApplication()).loggedInUser.getUid())){
            confirmReplyReplyDeletion dialoug = new confirmReplyReplyDeletion(this,repliesArray.get(position).getReplyID(),this);
            dialoug.startAlert();
        }
        else {
            reportReplyReply dialoug = new reportReplyReply(this,repliesArray.get(position).getReplyID());
            dialoug.startAlert();
        }

    }

    @Override
    public void update() {
        getResponders();
    }
}
