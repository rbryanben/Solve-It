package com.wapazock.solveit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.tags;

import java.util.ArrayList;

public class recyclerViewAdapterShimmer extends RecyclerView.Adapter<recyclerViewAdapterShimmer.ViewHolder> {

    ArrayList<tags> mTags = new ArrayList<>();
    public   RecyclerViewClickInterface recyclerViewClickInterface ;

    public recyclerViewAdapterShimmer(ArrayList<tags> mTags, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.mTags = mTags;
        this.recyclerViewClickInterface = recyclerViewClickInterface ;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shimmer_tag,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    ///class holder
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tagText ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagText = itemView.findViewById(R.id.item_home_tag_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewClickInterface.onItemLongClick(getAdapterPosition());
                    return true;
                }
            });

        }

    }



}
