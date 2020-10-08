package com.wapazock.solveit.illustrations;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.wapazock.solveit.R;
import com.wapazock.solveit.illustrations.fragments.fragmentIllustration00;
import com.wapazock.solveit.illustrations.fragments.fragmentIllustration01;
import com.wapazock.solveit.illustrations.fragments.fragmentIllustration02;
import com.wapazock.solveit.illustrations.fragments.fragmentIllustration03;
import com.wapazock.solveit.illustrations.fragments.fragmentIllustration04;
import com.wapazock.solveit.illustrations.fragments.fragmentIllustration05;
import com.wapazock.solveit.utils.ViewPagerAdapter;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

public class illustrationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illustration);

        //setup pages
        setUpViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set theme
        ((globalShared) getApplication()).setDefaultTheme(this);
    }

    /*
        This procedure will set the pages for the viewPager
        Also sets up listener for swiping through illustrations setting focused item
         */
    private void setUpViewPager(){
        //view pager
        ViewPager pager = (ViewPager)findViewById(R.id.activity_illustration_viewPager);

        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new fragmentIllustration00());
        mFragments.add(new fragmentIllustration01());
        mFragments.add(new fragmentIllustration02());
        mFragments.add(new fragmentIllustration03());
        mFragments.add(new fragmentIllustration04());
        mFragments.add(new fragmentIllustration05());
        //set adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mFragments);
        pager.setAdapter(viewPagerAdapter);

        //setup listener

        //references
        final RelativeLayout position00 = (RelativeLayout)findViewById(R.id.activity_illustration_item0);
        final RelativeLayout position01 = (RelativeLayout)findViewById(R.id.activity_illustration_item1);
        final RelativeLayout position02 = (RelativeLayout)findViewById(R.id.activity_illustration_item2);
        final RelativeLayout position03 = (RelativeLayout)findViewById(R.id.activity_illustration_item3);
        final RelativeLayout position04 = (RelativeLayout)findViewById(R.id.activity_illustration_item4);
        final RelativeLayout switcher = (RelativeLayout)findViewById(R.id.activity_illustration_switcher);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //clear positions
                clearPositions();
                switch (position){
                    case 0 :
                        position00.setBackgroundResource(R.drawable.shape_circle_checked);
                        break;
                    case 1 :
                        position01.setBackgroundResource(R.drawable.shape_circle_checked);
                        break;
                    case 2 :
                        position02.setBackgroundResource(R.drawable.shape_circle_checked);
                        break;
                    case 3 :
                        position03.setBackgroundResource(R.drawable.shape_circle_checked);
                        break;
                    case 4 :
                        switcher.setVisibility(View.VISIBLE);
                        position04.setBackgroundResource(R.drawable.shape_circle_checked);
                        break;
                    case 5 :
                        switcher.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
        this function will clear selected items
     */
    private void clearPositions(){
        //references
        RelativeLayout position00 = (RelativeLayout)findViewById(R.id.activity_illustration_item0);
        RelativeLayout position01 = (RelativeLayout)findViewById(R.id.activity_illustration_item1);
        RelativeLayout position02 = (RelativeLayout)findViewById(R.id.activity_illustration_item2);
        RelativeLayout position03 = (RelativeLayout)findViewById(R.id.activity_illustration_item3);
        RelativeLayout position04 = (RelativeLayout)findViewById(R.id.activity_illustration_item4);

        //clearing
        position00.setBackgroundResource(R.drawable.shape_circle_unchecked);
        position01.setBackgroundResource(R.drawable.shape_circle_unchecked);
        position02.setBackgroundResource(R.drawable.shape_circle_unchecked);
        position02.setBackgroundResource(R.drawable.shape_circle_unchecked);
        position03.setBackgroundResource(R.drawable.shape_circle_unchecked);
        position04.setBackgroundResource(R.drawable.shape_circle_unchecked);
    }
}
