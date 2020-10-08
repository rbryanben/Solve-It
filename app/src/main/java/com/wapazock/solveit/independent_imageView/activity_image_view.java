package com.wapazock.solveit.independent_imageView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

public class activity_image_view extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
    }


    private PhotoView mainImage ;
    private ImageView back ;

    @Override
    protected void onResume() {
        super.onResume();
        ((globalShared) getApplication()).setDefaultTheme(activity_image_view.this);
        mainImage = findViewById(R.id.activity_image_view_image);
        Glide.with(activity_image_view.this)
                .asBitmap()
                .placeholder(R.drawable.blurred_image)
                .load(getIntent().getStringExtra("url"))
                .into(mainImage);

        back = findViewById(R.id.activity_image_view_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateZoom(activity_image_view.this);
            }
        });
    }
}
