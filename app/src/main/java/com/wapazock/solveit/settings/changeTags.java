package com.wapazock.solveit.settings;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.globalClasses.meta;
import com.wapazock.solveit.globalClasses.tagNameId;
import com.wapazock.solveit.globalClasses.tags;
import com.wapazock.solveit.independent_ask_question.recyclerViewAskAdapter;
import com.wapazock.solveit.signup.signupActivity03;
import com.wapazock.solveit.utils.RecyclerViewClickInterface;
import com.wapazock.solveit.utils.globalShared;
import com.wapazock.solveit.utils.tagsGridAdapter;

import java.util.ArrayList;

public class changeTags extends AppCompatActivity implements RecyclerViewClickInterface {


    String educationLevel;
    EditText addTagEdit ;
    ProgressBar progress ;

    ConstraintLayout items ;
    GridView tags ;
    RecyclerView selecedTags ;

    Button finish ;
    ArrayList<tagNameId> tagList = new ArrayList<>();
    ArrayList<com.wapazock.solveit.globalClasses.tags> selectedTags = new ArrayList<>();

    ArrayList<tags> TAGS_ARRAY = new ArrayList<>();
    private static final String TAG = "signupActivity03";
    private ArrayList<String> EDUCATION_LEVELS = new ArrayList<>();

    private Boolean activeFilter ;
    private  ArrayList<tagNameId> tempTagsArray = new ArrayList<>() ;
    private ArrayList<tags> TEMMP_TAGS_ARRAY = new ArrayList<>();

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
        EDUCATION_LEVELS.add("Primary-education");
        EDUCATION_LEVELS.add("O-level");
        EDUCATION_LEVELS.add("A-level");
        EDUCATION_LEVELS.add("Higher-education");

        //Theme
        ((globalShared) getApplication()).setDefaultTheme(this);

        //hide items
        items.setVisibility(View.GONE);

        //seaching
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

    private void getSelectedTags() {
        DatabaseReference mDatabse = FirebaseDatabase.getInstance().getReference();
        mDatabse.child("userTags").child(((globalShared) getApplication()).loggedInUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()){
                    int i = 0 ;
                    for (tags tag : TAGS_ARRAY){
                        if (tag.getId().equals(shot.getValue().toString())){
                            if (!selectedTags.contains(TAGS_ARRAY.get(i))){
                                selectedTags.add(TAGS_ARRAY.get(i));
                            }

                            break;
                        }
                        i++ ;
                    }
                }
                setupSelectedItemAdapter(selectedTags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                if (addTagEdit.getText().toString().isEmpty()){
                    setupTagsGridWith(tagList);
                }
                else {
                    for (int i = 0 ; i != tagList.size() ; i++){
                        if (tagList.get(i).getName().contains(addTagEdit.getText().toString())){
                            tempTagsArray.add(tagList.get(i));
                            TEMMP_TAGS_ARRAY.add(TAGS_ARRAY.get(i));
                            setupTagsGridWith(tempTagsArray);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ((globalShared) getApplication()).setDefaultTheme(changeTags.this);
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

        for (String level : EDUCATION_LEVELS){
            tagsReference.child("tags").orderByChild("educationLevel").equalTo(level)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                //add to grid
                                tagNameId tempTags = new tagNameId();
                                tempTags.setName(shot.child("name").getValue().toString());
                                tempTags.setId(shot.getKey());


                                //create tags array
                                if (!TAGS_ARRAY.contains(shot.getValue(tags.class))) {
                                    TAGS_ARRAY.add(shot.getValue(tags.class));
                                }

                                getSelectedTags();


                                //add to id
                                if (!tagList.contains(tempTags)) {
                                    tagList.add(tempTags);
                                }
                            }


                            setupTagsGridWith(tagList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (level.equals(educationLevel)){
                        break;
                    }
        }

    }


    /*
         This procedure will setup tags grid given an array list
     */
    private void setupTagsGridWith(ArrayList<tagNameId> tagsList){
        tagsGridAdapter tagsAdapter = new tagsGridAdapter(changeTags.this,tagsList);
        tags.setAdapter(tagsAdapter);
        progress.setVisibility(View.GONE);
        items.setVisibility(View.VISIBLE);

        //tags on touch
        tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectedTags.contains(TAGS_ARRAY.get(position))) {
                    selectedTags.add(TAGS_ARRAY.get(position));
                    setupSelectedItemAdapter(selectedTags);
                }

                try {

                    if (!selectedTags.contains(TEMMP_TAGS_ARRAY.get(position))) {
                        selectedTags.add(TEMMP_TAGS_ARRAY.get(position));
                        setupSelectedItemAdapter(selectedTags);
                    }
                }
                catch (Exception ex){

                }
            }
        });
    }


    private void setupSelectedItemAdapter(ArrayList<tags> tagList){
        recyclerViewAskAdapter adapter = new recyclerViewAskAdapter(tagList,changeTags.this);
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
                final pleaseWaitAlert alert = new pleaseWaitAlert(changeTags.this);


                 if (selectedTags.size() < 3){
                     ((globalShared) getApplication()).showToast(changeTags.this,"select a minimum of three tags");
                 }
                 else {
                     alert.startAlert();
                     final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                     databaseReference.child("userTags").child(((globalShared) getApplication()).loggedInUser.getUid()).removeValue();
                     final Handler handler = new Handler();
                     handler.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             for (tags tag : selectedTags) {
                                 databaseReference.child("userTags").child(((globalShared) getApplication()).loggedInUser.getUid())
                                         .push().setValue(tag.getId());

                             }
                         }
                     },400);

                     CountDownTimer timer = new CountDownTimer(1000,1000) {
                         @Override
                         public void onTick(long millisUntilFinished) {

                         }

                         @Override
                         public void onFinish() {
                             alert.stopAlert();
                              finish();
                         }
                     }.start();
                 }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        if (!selectedTags.contains(TAGS_ARRAY.get(position))) {
            selectedTags.add(TAGS_ARRAY.get(position));
            setupSelectedItemAdapter(selectedTags);
        }
    }

    @Override
    public void onItemLongClick(int position) {
        selectedTags.remove(position);
        setupSelectedItemAdapter(selectedTags);
    }
}
