package com.wapazock.solveit.independent_gallery;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.ViewPagerAdapter;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

public class activity_gallery extends AppCompatActivity {

    private static final String TAG = "activity_gallery";
    private Activity activity;
    private ViewPager pager ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.independent_gallery);

        //references
        activity = activity_gallery.this ;
        pager = findViewById(R.id.independent_gallery_pager);

        //theme
        ((globalShared) getApplication()).setDefaultTheme(activity);

        //set adapter
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new fragment_gallery());
        mFragments.add(new fragment_camera());

        //adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),mFragments);

        //set adapter
        pager.setAdapter(adapter);

    }
}
