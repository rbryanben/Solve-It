package com.wapazock.solveit.independent_post_view;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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

public class repliesRecycleShimmerAdapter extends RecyclerView.Adapter<repliesRecycleShimmerAdapter.customHolder> {

    ArrayList<String> REPLIES_ARRAY = new ArrayList<>();

    public repliesRecycleShimmerAdapter(ArrayList<String> REPLIES_ARRAY, Activity activity, repliesGridClickInterface repliesGridClickInterface) {
        this.REPLIES_ARRAY = REPLIES_ARRAY;
    }

    @NonNull
    @Override
    public customHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View customView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shimmer_post_holder, parent,false);
        return new customHolder(customView);
    }

    @Override
    public void onBindViewHolder(@NonNull final customHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return REPLIES_ARRAY.size();
    }


    class customHolder extends RecyclerView.ViewHolder {
        public customHolder(@NonNull final View itemView) {
            super(itemView);
        }
    }
}
