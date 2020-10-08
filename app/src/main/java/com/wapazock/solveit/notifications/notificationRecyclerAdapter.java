package com.wapazock.solveit.notifications;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.resources.TextAppearance;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.notifications;
import com.wapazock.solveit.home.homeQuestionsInterface;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class notificationRecyclerAdapter extends RecyclerView.Adapter<notificationRecyclerAdapter.customHolder>{

    //global variables
    ArrayList<notifications> NOTIFICATIONS = new ArrayList<>();
    Long LAST_LOGIN  ;
    Activity activity ;
    homeQuestionsInterface interfaceRecycler ;

    public notificationRecyclerAdapter(ArrayList<notifications> NOTIFICATIONS, Activity activity, Long LAST_LOGIN, homeQuestionsInterface homeQuestionsInterface) {
        this.NOTIFICATIONS = NOTIFICATIONS ;
        this.activity = activity;
        this.LAST_LOGIN = LAST_LOGIN ;
        this.interfaceRecycler = homeQuestionsInterface ;
    }

    @NonNull
    @Override
    public customHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_holder,parent,false);
        return new  customHolder(view);
    }

    private static final String TAG = "notificationRecyclerAda";

    @Override
    public void onBindViewHolder(@NonNull customHolder holder, int position) {
        holder.header.setText(NOTIFICATIONS.get(position).getHeader());
        holder.textPreview.setText(NOTIFICATIONS.get(position).getTextPreview());

        ((globalShared) activity.getApplication()).loadProfilePictureIntoImageViewWithID(NOTIFICATIONS.get(position).getUserAccount(),holder.profileImage,activity);

        //set marker times
        notifications notification = NOTIFICATIONS.get(position);
        
        if (notification.getTime() >= LAST_LOGIN){
            //change notification back time
            holder.header.setTypeface(Typeface.DEFAULT_BOLD);
            holder.textPreview.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return NOTIFICATIONS.size();
    }

    //custom view holder
    class customHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage ;
        TextView header, textPreview ;
        RelativeLayout back ;

        public customHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            textPreview = itemView.findViewById(R.id.previewText);
            header = itemView.findViewById(R.id.headerText);

            back = itemView.findViewById(R.id.notification_back);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    interfaceRecycler.onQuestionClicked(getAdapterPosition());
                }
            });
        }
    }
}
