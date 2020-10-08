package com.wapazock.solveit.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.globalClasses.meta;
import com.wapazock.solveit.globalClasses.tagNameId;
import com.wapazock.solveit.globalClasses.tags;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_ask_question.recyclerViewAskAdapter;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.signin.signInActivity;
import com.wapazock.solveit.utils.RecyclerViewClickInterface;
import com.wapazock.solveit.utils.globalShared;
import com.wapazock.solveit.utils.selectedTagsGridAdapter;
import com.wapazock.solveit.utils.tagsGridAdapter;

import java.util.ArrayList;

public class signupActivity03 extends AppCompatActivity implements RecyclerViewClickInterface {

    /*
     notes :

     */
    String educationLevel;
    EditText addTagEdit ;
    ProgressBar progress ;
    ConstraintLayout items ;
    GridView tags ;
    RecyclerView selecedTags ;
    Button finish ;
    ArrayList<tagNameId> tagList = new ArrayList<>();
    ArrayList<tags> selectedTags = new ArrayList<>();
    ArrayList<tags> TAGS_ARRAY = new ArrayList<>();
    ArrayList<tags> TEMP_ARRAY = new ArrayList<>();
    private  boolean activeFilter;

    private static final String TAG = "signupActivity03";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page03);

        //references
        addTagEdit = (EditText)findViewById(R.id.activity_signup_page03_addTagEdit);
        progress = (ProgressBar)findViewById(R.id.activity_signup_page03_progress);
        tags = (GridView)findViewById(R.id.activity_signup_page03_tagsGrid);
        items = (ConstraintLayout) findViewById(R.id.activity_signup_page03_itemsLayout);
        selecedTags = (RecyclerView)findViewById(R.id.activity_signup_page03_selectedTagsGrid);
        finish = (Button) findViewById(R.id.activity_signup_page03_proceedText);
        //get extras
        educationLevel = getIntent().getStringExtra("educationLevel");

        //Theme
        ((globalShared) getApplication()).setDefaultTheme(this);

        //hide items
        items.setVisibility(View.GONE);

        //seaching
        activeFilter = false ;
        tagSearchOnTouch();
        onTagSearchTyping();

        //remove focus
        addTagEdit.setFocusable(false);

        ((globalShared) getApplication()).setDefaultTheme(this);

        //get education tags
        getTagsWithEducationLevel();

        //finish
        onFinish();
    }

    @Override
    protected void onResume() {
        ((globalShared) getApplication()).setDefaultTheme(signupActivity03.this);
        super.onResume();


    }



    /*
        nb : do not delete
        This procedure is used to proceed to the next signUp page using the dissolve animation but on
        doing this we need to pass in the written parameter { username | email | password } , which we obtained
        from the user
        Goes to "signupActivity03"
     */
     private void gotoNextHomeActivity(){
        //using zom animation should takes us to home
         Intent homePage = new Intent(signupActivity03.this,signupActivity04.class);
         startActivity(homePage);
         Animatoo.animateZoom(signupActivity03.this);
     }


     /*
        This procedure will filter tags on searching
      */
     private void onTagSearchTyping(){
         addTagEdit.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 ArrayList<tagNameId> tempTagsArray = new ArrayList<>();
                 TEMP_ARRAY.clear();



                 if (addTagEdit.getText().toString().isEmpty()){
                     activeFilter = false ;
                     setupTagsGridWith(tagList);
                  }
                 else {
                     activeFilter = true ;
                     for (int i = 0 ; i != tagList.size() ; i++){
                         if (tagList.get(i).getName().contains(addTagEdit.getText().toString())){
                             tempTagsArray.add(tagList.get(i));
                             TEMP_ARRAY.add(TAGS_ARRAY.get(i));
                             Log.d(TAG, "onTextChanged: added " + TAGS_ARRAY.get(i).getName());
                             setupTagsGridWith(tempTagsArray);

                         }
                     }
                 }
             }

             @Override
             public void afterTextChanged(Editable s) {
                 ((globalShared) getApplication()).setDefaultTheme(signupActivity03.this);
             }
         });
     }


     /*
         This procedure will set on touch event for searching
      */
     private void tagSearchOnTouch(){
         addTagEdit.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 addTagEdit.setFocusableInTouchMode(true);
                 addTagEdit.setFocusable(true);
                 addTagEdit.requestFocus();
                 return false;
             }
         });
     }

     /*
        This function will search database for tags using the education level
      */
     private void getTagsWithEducationLevel(){
         DatabaseReference tagsReference = FirebaseDatabase.getInstance().getReference();
         try {
             tagsReference.child("tags").orderByChild("educationLevel").equalTo(educationLevel)
                     .addListenerForSingleValueEvent(new ValueEventListener() {

                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             for (DataSnapshot shot : snapshot.getChildren()) {
                                 //add to grid
                                 tagNameId tempTags = new tagNameId();
                                 tempTags.setName(shot.child("name").getValue().toString());
                                 tempTags.setId(shot.getKey());


                                 //create tags array
                                 TAGS_ARRAY.add(shot.getValue(tags.class));

                                 //add to id
                                 tagList.add(tempTags);
                             }


                             setupTagsGridWith(tagList);
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });
         }
         catch (Exception ex){
             ((globalShared) getApplication()).gotException(ex,0);
         }
     }


     /*
          This procedure will setup tags grid given an array list
      */
     private void setupTagsGridWith(ArrayList<tagNameId> tagsList){
        tagsGridAdapter tagsAdapter = new tagsGridAdapter(signupActivity03.this,tagsList);
        tags.setAdapter(tagsAdapter);
        progress.setVisibility(View.GONE);
        items.setVisibility(View.VISIBLE);

        //tags on touch
        tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (activeFilter == true) {
                    Log.d(TAG, "onItemClick: filter active" );
                    if (!selectedTags.contains(TEMP_ARRAY.get(position))) {
                        selectedTags.add(TEMP_ARRAY.get(position));
                        setupSelectedItemAdapter(selectedTags);
                    }
                }
                else {
                    if (!selectedTags.contains(TAGS_ARRAY.get(position))) {
                        selectedTags.add(TAGS_ARRAY.get(position));
                        setupSelectedItemAdapter(selectedTags);
                    }
                }
            }
        });
     }


    private void setupSelectedItemAdapter(ArrayList<tags> tagList){
         recyclerViewAskAdapter adapter = new recyclerViewAskAdapter(tagList,signupActivity03.this);
         selecedTags.setAdapter(adapter);
         selecedTags.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
    }



    /*
        In this procedure we setup the account and continue
     */
    private void onFinish(){
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (selectedTags.size() < 3 ){
                     ((globalShared) getApplication()).showToast(signupActivity03.this,"please select "+ Integer.toString(3 - selectedTags.size())
                     + " more");
                 }
                 else {

                     try {
                         //setup authentication account
                        setupNewAuthenticationAccount(((globalShared) getApplication()).tempSignupAccountEmail,((globalShared) getApplication()).tempSignupAccountPassword);

                     } catch (Exception ex) {
                         ((globalShared) getApplication()).gotException(ex, 1);
                     }
                 }
            }
        });
    }

    private void setupNewAuthenticationAccount(String email , String password){
            try {
                //set alert
                final pleaseWaitAlert wait = new pleaseWaitAlert(signupActivity03.this);
                wait.startAlert();
                //setup account
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    ((globalShared) getApplication()).loggedInUser = mAuth.getCurrentUser();

                                    //setup in database
                                    DatabaseReference newUserRef = FirebaseDatabase.getInstance().getReference();

                                    //set id
                                    ((globalShared) getApplication()).tempSignupAccount.setId(((globalShared) getApplication()).loggedInUser.getUid());
                                    //userAccount
                                    DatabaseReference userAccountsRef = newUserRef.child("userAccounts").child(((globalShared) getApplication()).loggedInUser.getUid());
                                    userAccountsRef.setValue(((globalShared) getApplication()).tempSignupAccount);

                                    //Meta data
                                    meta tempMeta = new meta();
                                    tempMeta.setAnswers("0");
                                    tempMeta.setImpact("5");
                                    tempMeta.setPendingAmount("1.57");
                                    tempMeta.setPendingVotes("0");
                                    tempMeta.setVotes("0");

                                    DatabaseReference metaRef = newUserRef.child("meta").child(mAuth.getUid());
                                    metaRef.setValue(tempMeta);

                                    //tags
                                    DatabaseReference userTagsRef = newUserRef.child("userTags").child(mAuth.getUid());
                                    for (tags tag : selectedTags) {
                                        userTagsRef.push().setValue(tag.getId());
                                    }

                                    //goto next intent

                                    gotoNextHomeActivity();
                                    wait.stopAlert();
                                    gotoNextHomeActivity();

                                } else {
                                    ((globalShared) getApplication()).showToast(signupActivity03.this, "oops.. setting up failed");
                                    wait.stopAlert();
                                }
                            }
                        });
            } catch (Exception ex) {
                ((globalShared) getApplication()).gotException(ex, 1);
            }
    }


    @Override
    public void onItemClick(int position) {
        if (!selectedTags.contains(tagList.get(position))) {
                selectedTags.add(TAGS_ARRAY.get(position));
                setupSelectedItemAdapter(selectedTags);
                selecedTags.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }
    }

    @Override
    public void onItemLongClick(int position) {
        selectedTags.remove(position);
        setupSelectedItemAdapter(selectedTags);
    }
}
