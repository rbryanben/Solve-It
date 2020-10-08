package com.wapazock.solveit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.alertDialouges.appOutdatedAlert;
import com.wapazock.solveit.alertDialouges.signingInAlert;
import com.wapazock.solveit.home.homeFragment;
import com.wapazock.solveit.illustrations.illustrationsActivity;
import com.wapazock.solveit.independent_gallery.activity_gallery;
import com.wapazock.solveit.notifications.notificationsFragment;
import com.wapazock.solveit.profile.profileFragment;
import com.wapazock.solveit.settings.settingsFragment;
import com.wapazock.solveit.signin.signInActivity;
import com.wapazock.solveit.signup.signupActivity00;
import com.wapazock.solveit.signup.signupActivity01;
import com.wapazock.solveit.signup.signupActivity02;
import com.wapazock.solveit.signup.signupActivity03;
import com.wapazock.solveit.signup.signupActivity04;
import com.wapazock.solveit.utils.ViewPagerAdapter;
import com.wapazock.solveit.utils.globalShared;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class mainActivity extends AppCompatActivity {

    private static final String MY_PREFERENCES = "my_preferences";
    private Fragment home,notifications, profile, settings ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
            String dataFileLines = ((globalShared) getApplication()).getDataFile();

            if (dataFileLines == null) {
                //start
                Intent intent = new Intent(mainActivity.this, illustrationsActivity.class);
                startActivity(intent);
                finish();
            } else {

                //check authentication
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                ((globalShared) getApplication()).loggedInUser = mAuth.getCurrentUser();
                if (((globalShared) getApplication()).loggedInUser == null) {
                    Intent signIn = new Intent(mainActivity.this, signInActivity.class);
                    startActivity(signIn);
                    Animatoo.animateZoom(mainActivity.this);
                    finish();
                } else {
                    //setup pages
                    setUpViewPagerPages();

                    //setup bottom navigation
                    setupBottomNavigation();

                    //check application version
                    checkApplicationVersion();

                    //start service
                    if (!((globalShared) getApplication()).serviceRunning) {
                        //startService(new Intent(this, background.class));
                    }

                    //set default theme (hide navigation bar)
                    ((globalShared) getApplication()).setDefaultTheme(this);
                    ((globalShared) getApplication()).themeKeyboardFixer(this);
                }
            }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
       This function is used to check if a service is running
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*
      this procedure will check if the version app version matches the current app version
      if the versions dont match it will show a message to update the application.
    */
    private void checkApplicationVersion(){
        DatabaseReference checkApplicationVersion = FirebaseDatabase.getInstance().getReference();
        checkApplicationVersion.child("application").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if the version matches
                for (DataSnapshot shot : snapshot.getChildren()){
                    //compare versions
                    if (!shot.getValue().toString().equals(getResources().getString(R.string.version))){
                        //show message
                        appOutdatedAlert appOutdatedAlert = new appOutdatedAlert(mainActivity.this);
                        appOutdatedAlert.startAlert();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /*
    This procedure will add fragments { home | notifications | profile | settings } to the main
    activity view pager
    It uses the custom FragmentPagerAdapter class ViewPagerAdapter in "java > com.wapazock.solveit > utils"
    It also sets up the navigation sync with the bottom navigation
     */
    private void setUpViewPagerPages(){
        //creating an array list which is a parameter of the view pager
        ArrayList<Fragment> mFragents = new ArrayList<>();

        home = new homeFragment();
        mFragents.add(home);

        notifications = new notificationsFragment();
        mFragents.add(notifications);

        profile = new profileFragment();
        mFragents.add(profile);

        settings = new settingsFragment();
        mFragents.add(settings);

        //creating adapter
        ViewPagerAdapter viewPagerAdapterMain = new ViewPagerAdapter(getSupportFragmentManager(),mFragents);

        //setting adapter to ViewPager
        ViewPager mainPager = (ViewPager)findViewById(R.id.activity_main_viewPager);
        mainPager.setAdapter(viewPagerAdapterMain);

        mainPager.setOffscreenPageLimit(3);
        //get bottom navigation reference
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.activity_main_bottomNavigation);

        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                 switch (position){
                     case 0 :
                         bottomNavigationView.getMenu().getItem(0).setChecked(true);
                         break;
                     case 1 :
                         bottomNavigationView.getMenu().getItem(1).setChecked(true);
                         break;
                     case 2 :
                         bottomNavigationView.getMenu().getItem(2).setChecked(true);
                         break;
                     case 3 :
                         bottomNavigationView.getMenu().getItem(3).setChecked(true);
                         break;
                 }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
    This procedure will setup bottom navigation with menu "bottom_navigation_menu"
    and will also set the on click listeners for each item
     */
    private void setupBottomNavigation(){

        try {

            //Viewpager for sync
            final ViewPager mainPager = (ViewPager) findViewById(R.id.activity_main_viewPager);

            final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottomNavigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.notifications:
                            bottomNavigationView.getMenu().getItem(1).setChecked(true);
                            mainPager.setCurrentItem(1);
                            break;
                        case R.id.home:
                            bottomNavigationView.getMenu().getItem(0).setChecked(true);
                            mainPager.setCurrentItem(0);
                            break;
                        case R.id.profile:
                            bottomNavigationView.getMenu().getItem(2).setChecked(true);
                            mainPager.setCurrentItem(2);
                            break;
                        case R.id.settings:
                            bottomNavigationView.getMenu().getItem(3).setChecked(true);
                            mainPager.setCurrentItem(3);
                            break;
                    }

                    return false;
                }

                ;
            });
        }
        catch (Exception ex){
            ((globalShared) getApplication()).gotException(ex,2);
        }
    }

    /*
    This procedure will disable shifting for the bottom navigation
     */
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShifting(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    private void SearchString(DatabaseReference mDatabase, final String criteria) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //loop the children of the the current key
                for (DataSnapshot shot : snapshot.getChildren()){
                     if (shot.getValue().toString().equals(criteria)){
                         //found the string
                         // do something
                         //
                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}