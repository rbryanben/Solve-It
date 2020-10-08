package com.wapazock.solveit.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wapazock.solveit.R;

import java.util.ArrayList;

public class home_shimmer_recycler_adapter extends RecyclerView.Adapter<home_shimmer_recycler_adapter.customHolder>{

    private ArrayList<String> SHIMMER_ITEMS = new ArrayList<>();

    public home_shimmer_recycler_adapter(ArrayList<String> SHIMMER_ITEMS) {
        this.SHIMMER_ITEMS = SHIMMER_ITEMS;
    }

    @NonNull
    @Override
    public customHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View customView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shimmer_post_holder,parent,false);
        return new customHolder(customView);
    }

    @Override
    public void onBindViewHolder(@NonNull customHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return SHIMMER_ITEMS.size();
    }

    class customHolder extends RecyclerView.ViewHolder {

        public customHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
