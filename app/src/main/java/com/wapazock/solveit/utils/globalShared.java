package com.wapazock.solveit.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wapazock.solveit.R;
import com.wapazock.solveit.globalClasses.meta;
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.globalClasses.tagNameId;
import com.wapazock.solveit.globalClasses.tags;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_reply_reply.activity_reply_reply;
import com.wapazock.solveit.independent_reply_viewer.activity_reply_viewer;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.signin.signInActivity;
import com.wapazock.solveit.sql_helper.helperSQL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class globalShared extends Application {

    /*
    The following is a set of global variables
     */
    private static final String TAG = "globalShared";
    private static String DATA_FILE_NAME = "data.wfs" ;
    /*
        This variable store temp data for account creation
      */
    public userAccount tempSignupAccount ;
    public String tempSignupAccountEmail,tempSignupAccountPassword;
    public FirebaseUser loggedInUser ;

    public ArrayList<tags> loggedUserTags = new ArrayList<>();
    public ArrayList<String> loggedUserTagsIDS = new ArrayList<>();
    public Boolean changedPassword = false ;

    public String NOTIFICATION_CHANNEL = "channel1" ;
    public helperSQL configurationDatabase ;
    public Boolean profileUpdated = false ;
    public Boolean serviceRunning = false ;

    ///////////////////////////////////////////////functions and procedures


    @Override
    public void onCreate() {
        super.onCreate();
        //disable darkmode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //configuration file
        configurationDatabase = new helperSQL(getApplicationContext());

        //notification channels
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel1","channel1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //setup notification channels
        setupNotificationChannels();
    }

    private void setupNotificationChannels() {
        Log.d(TAG, "setupNotificationChannels: called");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("Channel 1","Channel 1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public void showNotification(String title, String text){
        Notification notification = new NotificationCompat.Builder(this,"Channel 1")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentTitle(title)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1,notification);
    }

    /*
        This procedure will hide the bottom navigation and set the Status bar to a light
        mode. Is called with a activity as a parameter
         */
    public void setDefaultTheme(Activity activity) {
        //hide navigation
        activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //set light action bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;   // add LIGHT_STATUS_BAR to flag
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE); // optional
        }
    }

    public void themeKeyboardFixer(final Activity activity){
        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect rect = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();

                int keyboardHeight = screenHeight - rect.bottom;

                if (keyboardHeight > screenHeight * 0.15) {
                    setDefaultTheme(activity);
                }
            }
        });
    }

    /*
      This procedure is used for exception handling
     */
    public void gotException(Exception ex,int errorNumber){
        //check internet
        if (!isNetworkAvailable()){
            Toast.makeText(this,"Please check your Connection",Toast.LENGTH_SHORT).show();
        }

        switch (errorNumber){
            case 0 :
                Log.d(TAG, "gotException: on connecting to database : "+ex.toString());
        }
    }

    /*
    This function will check internet connectivity
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    This function will check if a string is an email
     */
    public boolean isAnEmail(String email){
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        boolean matchFound = m.matches();
        if (matchFound) {
            //your work here
            return true ;
        }
        else
            return false;
    }

    /*
      This function checks if a string is a mobile number
     */
    public  boolean isValid(String s)
    {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/7/91)?[7-8-2][0-9]{8}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    /*
        This procedure will show a toast to a screen
     */
    public void showToast(Activity activity, String message){
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
    }

    /*
       This function will generate a random string
     */
    public String generateRandomChars(int length) {
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" ;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }

    ////////////////////////////////////////////////////getter and setters tempSignup
    public userAccount getTempSignupAccount() {
        return tempSignupAccount;
    }

    public void setTempSignupAccount(userAccount tempSignupAccount) {
        this.tempSignupAccount = tempSignupAccount;
    }

    public String getTempSignupAccountEmail() {
        return tempSignupAccountEmail;
    }

    public void setTempSignupAccountEmail(String tempSignupAccountEmail) {
        this.tempSignupAccountEmail = tempSignupAccountEmail;
    }

    public String getTempSignupAccountPassword() {
        return tempSignupAccountPassword;
    }

    public void setTempSignupAccountPassword(String tempSignupAccountPassword) {
        this.tempSignupAccountPassword = tempSignupAccountPassword;
    }

    public  void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getDataFile(){
        FileInputStream stream = null ;
        ArrayList<String> dataLines = new ArrayList<>();
        try {
            stream = openFileInput(DATA_FILE_NAME);
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line ;

            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }

            return  stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return  null ;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean writeNewDataFile(String lines){
        FileOutputStream stream = null ;

        try {
            stream = openFileOutput(DATA_FILE_NAME,MODE_PRIVATE);
            stream.write(lines.getBytes());
            return true ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeDataFile(String lines){
        FileOutputStream stream = null ;

        try {
            stream = openFileOutput(DATA_FILE_NAME,MODE_APPEND);
            stream.write(lines.getBytes());
            return true ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    //passing data Question
    private questions passingQuestion ;

    public void setPassingQuestion(questions question){
        this.passingQuestion = question ;
    }

    public questions getPassingQuestion(){
        questions TempQuestion = this.passingQuestion ;
        return TempQuestion ;
    }

    //passing data userAccount
    private userAccount passingUserAccount ;

    public void setPassingUserAccount(userAccount UserAccount){
        this.passingUserAccount = UserAccount ;
    }

    public userAccount getPassingUserAccount(){
        userAccount tempUserAccount  = this.passingUserAccount ;
        return  tempUserAccount;
    }

    //passing gallery
    private String passingGallery ;

    public void setPassingGallery(String path){
        this.passingGallery = path ;
    }

    public String getPassingGallery(){
        String tempString = this.passingGallery ;
        return  tempString ;
    }

    //passing reply
    private replies passingReply ;

    public void setPassingReply(replies reply){
        this.passingReply = reply ;
    }

    public replies getPassingReply(){
        replies tempReply = this.passingReply ;
        return tempReply ;
    }

    /////////////////////////////////////////////////database APIs
    public void loadProfilePictureIntoImageViewWithID(String id, final ImageView holder, final Context context){
        try {
           DatabaseReference mUserAccounts = FirebaseDatabase.getInstance().getReference();
           mUserAccounts.child("userAccounts").child(id).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   try {
                       Glide.with(context)
                               .asBitmap()
                               .placeholder(R.drawable.blurred_image)
                               .load(snapshot.getValue().toString())
                               .diskCacheStrategy(DiskCacheStrategy.ALL)
                               .into(holder);
                   }
                   catch (Exception ex){
                       gotException(ex,2);
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
        }
        catch (Exception ex){
            Log.d(TAG, "getPosterProfileImage: got exception on setting post profile image from the home page");
        }
    }

    public void getReplyCountToTextView(String id, final TextView holder, final Context context ){
        try {
            DatabaseReference mReplies = FirebaseDatabase.getInstance().getReference();
            mReplies.child("repliesToReply").orderByChild("replyTo").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long count = snapshot.getChildrenCount();
                    if (count == 0) {
                        holder.setText("NO REPLY");
                    } else if (count == 1) {
                        holder.setText("1 REPLY");
                    } else {
                        holder.setText(Long.toString(count) + " REPLIES");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            gotException(ex,2);
        }
    }

    public void getVotesCountToTextView(String id, final TextView holder, final Context context ){
        try {
            DatabaseReference mReplies = FirebaseDatabase.getInstance().getReference();
            mReplies.child("votes").orderByChild("voteReply").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.setText("VOTE");
                    try {
                        Long count = snapshot.getChildrenCount();
                        Log.d(TAG, "votes : "+Long.toString(snapshot.getChildrenCount()));
                        if (count == 0) {
                            holder.setText("VOTE");
                        } else if (count == 1) {
                            holder.setText("1 VOTE");
                        } else {
                            holder.setText(Long.toString(count) + " VOTES");
                        }
                    }
                    catch (Exception ex){
                        gotException(ex,3);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            gotException(ex,2);
        }
    }

    public void loadProfilePictureIntoImageViewWithUrl(ImageView holder, Activity activity , String url){
        try {
            Glide.with(activity)
                    .asBitmap()
                    .placeholder(R.drawable.blurred_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(url)
                    .into(holder);
        }
        catch (Exception ex){
            gotException(ex,2);
        }
    }



    /////////////////////////////////////////////////phone APIS
    public ArrayList<String> getDirectories(){
        //directories array
        ArrayList<String> directories = new ArrayList<>();

        try {
            //get directoriees
            File rootDirectory = new File(Environment.getExternalStorageDirectory().getPath());
            File[] rootDirectoryList = rootDirectory.listFiles();

            for (File file : rootDirectoryList){
                if (file.isDirectory()) {
                    directories.add(file.getName());
                }
            }
        }
        catch (Exception ex){
            gotException(ex,3);
        }

        //result
        return directories ;
    }

    public ArrayList<String> getFilesFromDirectory(String filepath){
        //directories array
        ArrayList<String> directories = new ArrayList<>();


        // add root directory
        directories.add(filepath);

        //get too files
        File rootFolder = new File(filepath);
        File[] rootFileList = rootFolder.listFiles();

        for (File file : rootFileList){
            if (file.isDirectory()){
                directories.add(file.getAbsolutePath());
            }
        }

        //running loop
        for (int i = 0 ; i != 12 ; i++){
            //get subfolder
            try {
                if (directories != null) {
                    for (String path : directories) {
                        File file = new File(path);
                        File[] fileList = file.listFiles();

                        if (fileList != null) {
                            for (File subFile : fileList) {
                                if (subFile.isDirectory()) {
                                    if (!directories.contains(subFile.getAbsolutePath())) {
                                        directories.add(subFile.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception Ex){

            }
        }

        //image list
        ArrayList<String> images = new ArrayList<>();
        //check
        for (String paths : directories){
            File filelist = new File(paths);
            File[] fileListArray = filelist.listFiles();

            for (File file : fileListArray){
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".bmp")){
                    images.add(file.getAbsolutePath());
                }
            }
        }


        //result
        return images ;
    }





}
