package com.wapazock.solveit.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_answer.activity_answer;
import com.wapazock.solveit.independent_profile_view.activity_profile_view;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class homeQuestionsRecyclerViewAdapter extends RecyclerView.Adapter<homeQuestionsRecyclerViewAdapter.customHolder>  {


    ArrayList<questions> QUESTIONS_ARRAY = new ArrayList<>();
    private Activity activity;
    private homeQuestionsInterface interfaceHome ;

    public homeQuestionsRecyclerViewAdapter(ArrayList<questions> QUESTIONS_ARRAY, Activity activity, homeQuestionsInterface interfaceHome) {
        this.QUESTIONS_ARRAY = QUESTIONS_ARRAY;
        this.activity = activity;
        this.interfaceHome = interfaceHome ;
    }

    @NonNull
    @Override
    public customHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_holder,parent,false);
        return new customHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final customHolder holder, final int position) {
        //check if it my image
        if (QUESTIONS_ARRAY.get(position).getUserAccount().equals(((globalShared) activity.getApplication()).loggedInUser.getUid())){
              holder.postBack.setBackgroundResource(R.drawable.background_post_corner_gray_border_mine);
        }

        //set questions image
        Glide.with(activity)
                .asBitmap()
                .load(QUESTIONS_ARRAY.get(position).getQuestionImage())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.questionImage);

        //card
        holder.cardView.setElevation(0);

        //check for solved
        if (QUESTIONS_ARRAY.get(position).getAnswered().equals("SOLVED")) {
            holder.solvedText.setText("SOLVED");
            holder.solvedBackground.setBackgroundResource(R.drawable.background_green_button_white);
        }

        //set profile image
        ((globalShared) activity.getApplication()).loadProfilePictureIntoImageViewWithID(QUESTIONS_ARRAY.get(position).getUserAccount(),holder.profileImage,activity);

        //set question text
        holder.questionText.setText(QUESTIONS_ARRAY.get(position).getQuestion());

        //get replies count
        getReplyCount(holder.repliesText, QUESTIONS_ARRAY.get(position));

        //profile on click (Optimized)
        DatabaseReference mUserAccount = FirebaseDatabase.getInstance().getReference();
        mUserAccount.child("userAccounts").child(QUESTIONS_ARRAY.get(position).getUserAccount()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                holder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((globalShared) activity.getApplicationContext()).setPassingUserAccount(snapshot.getValue(userAccount.class));
                        Intent profileIntent = new Intent(activity, activity_profile_view.class);
                        activity.startActivity(profileIntent);
                        Animatoo.animateSlideUp(activity);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //votes (check and set listener)
        setupVoteOnClick(holder.upvoteButton,QUESTIONS_ARRAY.get(position),holder.upVoteText);


        //set answer button
        holder.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((globalShared) activity.getApplicationContext()).setPassingQuestion(QUESTIONS_ARRAY.get(position));
                Intent answerIntent = new Intent(activity, activity_answer.class);
                activity.startActivity(answerIntent);
                Animatoo.animateSlideUp(activity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return QUESTIONS_ARRAY.size();
    }

    //custom holder
    class customHolder extends RecyclerView.ViewHolder {
        private ImageView questionImage ;
        private ImageView profileImage;
        private TextView questionText ;

        private TextView repliesText ;
        private ImageView upvoteButton ;
        private TextView upVoteText ;

        private RelativeLayout solvedBackground ;
        private TextView solvedText;
        private RelativeLayout answerButton , postBack;

        private ProgressBar loading ;

        private CardView cardView;

        public customHolder(@NonNull final View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_post_holder_postImageCard);

            questionImage = itemView.findViewById(R.id.item_post_holder_postImage);
            profileImage = itemView.findViewById(R.id.item_post_holder_postProfileImage);
            questionText = itemView.findViewById(R.id.item_post_holder_postQuestionText);

            repliesText = itemView.findViewById(R.id.item_post_holder_repliesText);
            upvoteButton = itemView.findViewById(R.id.item_post_holder_upvoteButton);
            upVoteText = itemView.findViewById(R.id.item_post_holder_upvoteText);

            solvedBackground = (RelativeLayout) itemView.findViewById(R.id.item_post_holder_solvedBackground);
            solvedText = (TextView) itemView.findViewById(R.id.item_post_holder_solvedText);

            answerButton = (RelativeLayout) itemView.findViewById(R.id.item_post_holder_answerButton);
            postBack = itemView.findViewById(R.id.postBackground);
            loading = itemView.findViewById(R.id.postProgress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interfaceHome.onQuestionClicked(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    interfaceHome.onQuestionLongClicked(getAdapterPosition());
                    return true;
                }
            });

        }
    }

    private void getReplyCount(final TextView repliesText, questions tempData) {
        try {
            DatabaseReference mReplies = FirebaseDatabase.getInstance().getReference();
            mReplies.child("replies").orderByChild("replyTo").equalTo(tempData.getQuestionID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long replyCount = snapshot.getChildrenCount();
                    //compare
                    if (replyCount == 0) {
                        repliesText.setText("no reply");
                    } else if (replyCount == 1) {
                        repliesText.setText(replyCount.toString() + " reply");
                    } else {
                        repliesText.setText(replyCount + " replies");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
        }
    }

    private void setupVoteOnClick(final ImageView upvoteButton, final questions tempData, final TextView upVoteText) {
        //set votes
        if (Integer.parseInt(tempData.getVotes()) == 0 ){
            upVoteText.setText("vote");
        }
        else if (Integer.parseInt(tempData.getVotes()) == 1){
            upVoteText.setText(Integer.parseInt(tempData.getVotes()) + " vote");
        }
        else {
            upVoteText.setText(Integer.parseInt(tempData.getVotes()) + " votes");
        }


        try {

            //check vote
            final DatabaseReference questionVotesRef = FirebaseDatabase.getInstance().getReference();

            questionVotesRef.child("questionVotes").child(((globalShared) activity.getApplicationContext()).loggedInUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                if (shot.getValue().toString().equals(tempData.getQuestionID())) {
                                    upvoteButton.setImageResource(R.drawable.ic_upvote_voted);
                                    upvoteButton.setAlpha(0.99999f);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //vote listener
            upvoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (upvoteButton.getAlpha() == 0.99999f) {
                        //unvote
                        upvoteButton.setImageResource(R.drawable.ic_upvote);
                        upvoteButton.setAlpha(1f);

                        questionVotesRef.child("questionVotes").child(((globalShared) activity.getApplicationContext()).loggedInUser.getUid())
                                .orderByValue().equalTo(tempData.getQuestionID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //remove vote
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    shot.getRef().removeValue();
                                }
                                //decrement vote counts

                                questionVotesRef.child("questions").orderByChild("questionID").equalTo(tempData.getQuestionID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot shot : snapshot.getChildren()) {
                                            Integer currentVotes = Integer.parseInt(shot.child("votes").getValue().toString());
                                            currentVotes--;
                                            shot.getRef().child("votes").setValue(currentVotes.toString());

                                            //change votes text
                                            if (currentVotes == 0) {
                                                upVoteText.setText("upvote");
                                            } else if (currentVotes == 1) {
                                                upVoteText.setText(currentVotes + " vote");
                                            } else {
                                                upVoteText.setText(currentVotes + " votes");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        //vote
                        upvoteButton.setImageResource(R.drawable.ic_upvote_voted);
                        upvoteButton.setAlpha(0.99999f);

                        questionVotesRef.child("questionVotes").child(((globalShared) activity.getApplicationContext()).loggedInUser.getUid())
                                .push().setValue(tempData.getQuestionID());

                        //increment votes
                        questionVotesRef.child("questions").orderByChild("questionID").equalTo(tempData.getQuestionID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    Integer currentVotes = Integer.parseInt(shot.child("votes").getValue().toString());
                                    currentVotes++;
                                    shot.getRef().child("votes").setValue(currentVotes.toString());


                                    if (currentVotes == 0) {
                                        upVoteText.setText("upvote");
                                    } else if (currentVotes == 1) {
                                        upVoteText.setText(currentVotes + " vote");
                                    } else {
                                        upVoteText.setText(currentVotes + " votes");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });
        }catch (Exception ex){

        }
    }

}
