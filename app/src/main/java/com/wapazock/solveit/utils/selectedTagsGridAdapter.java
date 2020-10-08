package com.wapazock.solveit.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.tagNameId;

import java.util.ArrayList;

public class selectedTagsGridAdapter extends ArrayAdapter<tagNameId> {

    public selectedTagsGridAdapter(@NonNull Context context, ArrayList<tagNameId> tags) {
        super(context, R.layout.item_selected_tag , tags);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater view = LayoutInflater.from(getContext());
        View customView = view.inflate(R.layout.item_selected_tag,parent,false);

        tagNameId tempTag = getItem(position);

        TextView tagName = (TextView)customView.findViewById(R.id.item_selected_tag_name);
        tagName.setText(tempTag.getName());

        return customView ;
    }
}
