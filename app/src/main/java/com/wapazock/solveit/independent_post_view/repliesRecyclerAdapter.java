package com.wapazock.solveit.independent_post_view;

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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.globalClasses.votes;
import com.wapazock.solveit.independent_profile_view.activity_profile_view;
import com.wapazock.solveit.independent_reply_reply.activity_reply_reply;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class repliesRecyclerAdapter extends RecyclerView.Adapter<repliesRecyclerAdapter.customHolder> {

    ArrayList<replies> REPLIES_ARRAY = new ArrayList<>();
    Activity activity ;
    repliesGridClickInterface repliesGridClickInterface ;

    public repliesRecyclerAdapter(ArrayList<replies> REPLIES_ARRAY, Activity activity, repliesGridClickInterface repliesGridClickInterface) {
        this.REPLIES_ARRAY = REPLIES_ARRAY;
        this.activity = activity ;
        this.repliesGridClickInterface = repliesGridClickInterface ;
    }

    @NonNull
    @Override
    public customHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View customView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_holder, parent,false);
        return new customHolder(customView);
    }

    @Override
    public void onBindViewHolder(@NonNull final customHolder holder, int position) {
        final replies tempReply = REPLIES_ARRAY.get(position);

        if (REPLIES_ARRAY.get(position).getUserAccount().equals(((globalShared) activity.getApplication()).loggedInUser.getUid())){
              holder.replyBack.setBackgroundResource(R.drawable.background_post_corner_gray_border_mine);
        }

        //card
        holder.cardView.setElevation(0);
        if (holder.profileImage.getAlpha() != 0.9999f) {
            holder.profileImage.setAlpha(0.9999f);
            //set reply profile photo
            ((globalShared) activity.getApplication()).loadProfilePictureIntoImageViewWithID(tempReply.getUserAccount(), holder.profileImage, activity);
        }

        //set text
        holder.questionText.setText(tempReply.getReplyText());

        //get reply reply count
        ((globalShared) activity.getApplicationContext()).getReplyCountToTextView(tempReply.getReplyID(),holder.repliesText,activity);

        //get vote count
        ((globalShared) activity.getApplicationContext()).getVotesCountToTextView(tempReply.getReplyID(),holder.upvoteText,activity);

        //set photo image
        Glide.with(activity)
                .asBitmap()
                .load(REPLIES_ARRAY.get(position).getReplyImage())
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
                .into(holder.replyImage);

        //setup voting
        setupVoting(holder.voteButton,tempReply);

        //setup profile picture on click
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseReference mUserAccount = FirebaseDatabase.getInstance().getReference();
                    mUserAccount.child("userAccounts").child(tempReply.getUserAccount()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //we should pass data
                            ((globalShared)activity.getApplicationContext()).setPassingUserAccount(snapshot.getValue(userAccount.class));
                            Intent profileViewer = new Intent(activity, activity_profile_view.class);
                            activity.startActivity(profileViewer);
                            Animatoo.animateSlideUp(activity);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                catch (Exception ex){
                    ((globalShared) activity.getApplicationContext()).gotException(ex,2);
                }


            }
        });

        //reply button
        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we should pass data
                Log.d(TAG, "onResume: reply has been clicked ");
                ((globalShared) activity.getApplicationContext()).setPassingReply(tempReply);
                Intent profileViewer = new Intent(activity, activity_reply_reply.class);
                activity.startActivity(profileViewer);
                Animatoo.animateSlideUp(activity);
            }
        });


    }

    @Override
    public int getItemCount() {
        return REPLIES_ARRAY.size();
    }

    public void setupVoting(final ImageView voteButton, final replies tempRely) {


        DatabaseReference mVotes = FirebaseDatabase.getInstance().getReference();


        //check vote
        mVotes = FirebaseDatabase.getInstance().getReference();
        mVotes.child("votes").orderByChild("userAccount").equalTo(((globalShared) activity.getApplicationContext()).loggedInUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()){
                    if (tempRely.getReplyID().equals(shot.child("voteReply").getValue().toString())){
                        voteButton.setAlpha(0.99999f);
                        voteButton.setImageResource(R.drawable.ic_upvote_voted);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mVotesRef = FirebaseDatabase.getInstance().getReference();

                if (voteButton.getAlpha() == 1){
                    //vote
                    voteButton.setAlpha(0.99999f);
                    voteButton.setImageResource(R.drawable.ic_upvote_voted);
                    //write to database
                    votes tempVote = new votes();
                    tempVote.setUserAccount(((globalShared) activity.getApplicationContext()).loggedInUser.getUid());
                    tempVote.setVoteID("not set");
                    tempVote.setVoteReply(tempRely.getReplyID());

                    mVotesRef.child("votes").push().setValue(tempVote);
                }
                else {
                    //unvote
                    voteButton.setAlpha(1f);
                    voteButton.setImageResource(R.drawable.ic_upvote);

                    mVotesRef.child("votes").orderByChild("voteReply").equalTo(tempRely.getReplyID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot shot : snapshot.getChildren()){
                                if (shot.child("userAccount").getValue().toString().equals(((globalShared) activity.getApplicationContext()).loggedInUser.getUid())){
                                    shot.getRef().removeValue();
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
    }

    class customHolder extends RecyclerView.ViewHolder {

        //set items
        ImageView replyImage ;
        CircleImageView profileImage ;
        TextView questionText ;
        TextView repliesText;
        TextView upvoteText;
        ImageView voteButton;
        RelativeLayout replyButton, replyBack;
        ProgressBar loading ;

        CardView cardView ;

        public customHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repliesGridClickInterface.onReplyClicked(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    repliesGridClickInterface.onLongReplyClicked(getAdapterPosition());
                    return true;
                }
            });

            //references
            View customView = itemView ;

            replyBack = itemView.findViewById(R.id.replyBackground);

            cardView = itemView.findViewById(R.id.item_post_holder_postImageCard);
            replyImage = (ImageView)customView.findViewById(R.id.item_reply_holder_postImage);
            profileImage =  (CircleImageView)customView.findViewById(R.id.item_reply_holder_postProfileImage);

            loading = itemView.findViewById(R.id.postProgress);
            questionText = (TextView) customView.findViewById(R.id.item_reply_holder_postQuestionText) ;
            repliesText = (TextView)customView.findViewById(R.id.item_reply_holder_repliesText);
            upvoteText = (TextView)customView.findViewById(R.id.item_reply_holder_upvoteText);

            voteButton = (ImageView)customView.findViewById(R.id.item_reply_holder_upvoteButton);
            replyButton = (RelativeLayout) customView.findViewById(R.id.item_reply_holder_answerButton);

        }



    }
}
