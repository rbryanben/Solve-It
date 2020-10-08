package com.wapazock.solveit.independent_gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.theophrast.ui.widget.SquareImageView;
import com.wapazock.solveit.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class galleryAdapter extends ArrayAdapter<String> {

    public galleryAdapter(@NonNull Context context,ArrayList<String> imagesArray) {
        super(context, R.layout.item_gallery_image,imagesArray);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater view = LayoutInflater.from(getContext());
        View customView = view.inflate(R.layout.item_gallery_image,parent,false);

        SquareImageView image = customView.findViewById(R.id.gallery_image);
        try {
            String path = getItem(position);
            Glide.with(getContext())
                    .asBitmap()
                    .placeholder(R.drawable.blurred_image)
                    .load(path)
                    .into(image);
        }
        catch (Exception ex){

        }

        return customView ;
    }
}
