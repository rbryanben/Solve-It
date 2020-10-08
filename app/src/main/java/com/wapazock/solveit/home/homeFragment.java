package com.wapazock.solveit.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.confirmDeletion;
import com.wapazock.solveit.alertDialouges.reportQuestion;
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.tags;
import com.wapazock.solveit.independent_post_view.activity_post;
import com.wapazock.solveit.utils.LinearSpacesItemDecoration;
import com.wapazock.solveit.utils.RecyclerViewClickInterface;
import com.wapazock.solveit.utils.globalShared;
import com.wapazock.solveit.utils.recyclerViewAdapter;
import com.wapazock.solveit.utils.recyclerViewAdapterShimmer;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

public class homeFragment extends Fragment implements RecyclerViewClickInterface, homeQuestionsInterface{

    private static final String TAG = "homeFragment";

    //references
    private RecyclerView tagsRecycler;
    private DatabaseReference mDatabase;
    private RecyclerView contentGrid;

    private Spinner sortSpinner;
    private RelativeLayout contentLayout;


    private EditText searchBoxEdit;
    private TextView searchResult;
    private homeQuestionsRecyclerViewAdapter adapter;

    //arrays
    private ArrayList<questions> questionsArray = new ArrayList<>();
    private ArrayList<questions> tempQuestionArray = new ArrayList<>();
    private ArrayList<String> loggedUserTagsList = new ArrayList<>();

    private Boolean tagslogaded;
    //values
    private boolean activeFilter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //theme
        //set default theme (hide navigation bar)
        ((globalShared) getActivity().getApplication()).setDefaultTheme(getActivity());
        ((globalShared) getActivity().getApplication()).themeKeyboardFixer(getActivity());

        //clear questions
        questionsArray.clear();
        ((globalShared) getActivity().getApplication()).loggedUserTagsIDS.clear();

        //references
        tagsRecycler = (RecyclerView) getActivity().findViewById(R.id.fragment_home_tagsRecycler);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        contentGrid = getActivity().findViewById(R.id.fragment_home_contentGrid);

        sortSpinner = (Spinner) getActivity().findViewById(R.id.fragment_home_sortSpinner);
        contentLayout = (RelativeLayout) getActivity().findViewById(R.id.fragment_home_contentLayout);

        searchBoxEdit = (EditText) getActivity().findViewById(R.id.fragment_home_searchBoxEdit);
        searchResult = (TextView) getActivity().findViewById(R.id.fragment_home_searchNoResults);

        //set content layout gone
        contentLayout.setVisibility(View.VISIBLE);
        searchBoxEdit.setText("");

        //active filter
        activeFilter = false;

        //clear tags
        loggedUserTagsList.clear();
        ((globalShared) getActivity().getApplication()).loggedUserTags.clear();
        ((globalShared) getActivity().getApplication()).loggedUserTagsIDS.clear();

        setShimmer();

        contentGrid.addItemDecoration(new LinearSpacesItemDecoration(10));


        //get data
        getUserTags();

        //sort listener
        sortListener();


        searchQuestionListener();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setShimmer() {
        //set shimmer
        ArrayList<String> dummyAdapter = new ArrayList<>();
        dummyAdapter.add("hello");
        dummyAdapter.add("hello");
        home_shimmer_recycler_adapter adapter = new home_shimmer_recycler_adapter(dummyAdapter);
        contentGrid.setAdapter(adapter);
        contentGrid.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //tags shimmer
        ArrayList<tags> array = new ArrayList<>();
        tags one = new tags();
        one.setName("         ");

        array.add(one);
        array.add(one);
        array.add(one);

        tagslogaded = false;
        sortSpinner.setVisibility(GONE);


        recyclerViewAdapterShimmer adapter1 = new recyclerViewAdapterShimmer(array, this);
        tagsRecycler.setAdapter(adapter1);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tagsRecycler.setLayoutManager(manager);
    }


    /*
        this procedure will get user tags
     */
    private void getUserTags() {
        try {
            final DatabaseReference userTagsRef = mDatabase;
            userTagsRef.child("userTags").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //clear tags to avid dublication
                            try {
                                ((globalShared) getActivity().getApplication()).loggedUserTagsIDS.clear();
                            } catch (Exception ex) {
                                ((globalShared) getActivity().getApplication()).gotException(ex, 2);
                            }

                            for (DataSnapshot shot : snapshot.getChildren()) {
                                loggedUserTagsList.add(shot.getValue().toString());
                                ((globalShared) getActivity().getApplication()).loggedUserTagsIDS.add(shot.getValue().toString());
                            }
                            getUserTagsAsTags();
                            //set questions
                            getQuestions();
                        }

                        private void getUserTagsAsTags() {
                            //clear tags to avoid duplication
                            ((globalShared) getActivity().getApplication()).loggedUserTags.clear();

                            for (final String tag : loggedUserTagsList) {
                                userTagsRef.child("tags").child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ((globalShared) getActivity().getApplication()).loggedUserTags.add(snapshot.getValue(tags.class));
                                        setupTagsGrid();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } catch (Exception ex) {
            ((globalShared) getActivity().getApplication()).gotException(ex, 2);
        }
    }

    /*
        This procedure will setup tags grid
     */
    private void setupTagsGrid() {
        //check for duplicates
        for (int i = 0; i < ((globalShared) getActivity().getApplication()).loggedUserTags.size(); i++) {
            for (int j = i + 1; j < ((globalShared) getActivity().getApplication()).loggedUserTags.size(); j++) {
                if (((globalShared) getActivity().getApplication()).loggedUserTags.get(i).getName().equals(((globalShared) getActivity().getApplication()).loggedUserTags.get(j).getName())) {
                    // got the duplicate element
                    ((globalShared) getActivity().getApplication()).loggedUserTags.remove(j);
                }
            }
        }


        //set adapter
        recyclerViewAdapter adapter = new recyclerViewAdapter(((globalShared) getActivity().getApplication()).loggedUserTags, this);
        tagsRecycler.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tagsRecycler.setLayoutManager(manager);
        sortSpinner.setVisibility(View.VISIBLE);
        tagslogaded = true;
    }

    /*
       This procedure will get the questions
     */
    private void getQuestions() {
        //hide no result
        searchResult.setVisibility(GONE);


        try {
            for (String tag : ((globalShared) getActivity().getApplication()).loggedUserTagsIDS) {
                mDatabase.child("questions").orderByChild("tag").equalTo(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //questionsArray.clear();
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: " + shot.getValue());
                            if (questionsArray.size() == 50) {
                                break;
                            }
                            questions tempQuestion = shot.getValue(questions.class);
                            if (tempQuestion.getAnswered().equals("UNSOLVED")) {
                                questionsArray.add(tempQuestion);
                            }
                        }


                        //sort questions array against time
                        for (int i = 0; i != questionsArray.size(); i++) {
                            for (int b = 0; b != questionsArray.size() - 1; b++) {
                                if (questionsArray.get(b).getTime() < questionsArray.get(b + 1).getTime()) {
                                    questions tempQuestion = questionsArray.get(b);
                                    questionsArray.set(b, questionsArray.get(b + 1));
                                    questionsArray.set(b + 1, tempQuestion);
                                }
                            }
                        }


                        //check duplicates
                        for (int i = 0; i < questionsArray.size(); i++) {
                            for (int j = i + 1; j < questionsArray.size(); j++) {
                                if (questionsArray.get(i).getQuestionID().equals(questionsArray.get(j).getQuestionID())) {
                                    // got the duplicate element
                                    questionsArray.remove(j);
                                }
                            }
                        }


                        //adapter
                         adapter = new homeQuestionsRecyclerViewAdapter(questionsArray, getActivity(),homeFragment.this);
                        contentGrid.setAdapter(adapter);
                        contentGrid.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));


                        //search for question
                        searchOnTouch();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } catch (Exception ex) {
            ((globalShared) getActivity().getApplication()).gotException(ex, 2);
        }
    }

    /*
        This procedure will sort 2 arrays according to time or date
        It checks on which array to set adapter with using the "activeFilter" value
     */
    private void sortListener() {
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // theme
                ((globalShared) getActivity().getApplication()).setDefaultTheme(getActivity());

                if (position == 0) {
                    //sort questions array against time

                    //default
                    for (int i = 0; i != questionsArray.size(); i++) {
                        for (int b = 0; b != questionsArray.size() - 1; b++) {
                            if (questionsArray.get(b).getTime() < questionsArray.get(b + 1).getTime()) {
                                questions tempQuestion = questionsArray.get(b);
                                questionsArray.set(b, questionsArray.get(b + 1));
                                questionsArray.set(b + 1, tempQuestion);
                            }
                        }
                    }

                    //filtered
                    for (int i = 0; i != tempQuestionArray.size(); i++) {
                        for (int b = 0; b != tempQuestionArray.size() - 1; b++) {
                            if (tempQuestionArray.get(b).getTime() < tempQuestionArray.get(b + 1).getTime()) {
                                questions tempQuestion = tempQuestionArray.get(b);
                                tempQuestionArray.set(b, tempQuestionArray.get(b + 1));
                                tempQuestionArray.set(b + 1, tempQuestion);
                            }
                        }
                    }
                } else {
                    //sort questions array against votes

                    //default
                    for (int i = 0; i != questionsArray.size(); i++) {
                        for (int b = 0; b != questionsArray.size() - 1; b++) {
                            if (Integer.parseInt(questionsArray.get(b).getVotes()) < Integer.parseInt(questionsArray.get(b + 1).getVotes())) {
                                questions tempQuestion = questionsArray.get(b);
                                questionsArray.set(b, questionsArray.get(b + 1));
                                questionsArray.set(b + 1, tempQuestion);
                            }
                        }
                    }

                    //default
                    for (int i = 0; i != tempQuestionArray.size(); i++) {
                        for (int b = 0; b != tempQuestionArray.size() - 1; b++) {
                            if (Integer.parseInt(tempQuestionArray.get(b).getVotes()) < Integer.parseInt(tempQuestionArray.get(b + 1).getVotes())) {
                                questions tempQuestion = tempQuestionArray.get(b);
                                tempQuestionArray.set(b, tempQuestionArray.get(b + 1));
                                tempQuestionArray.set(b + 1, tempQuestion);
                            }
                        }
                    }
                }

                if (activeFilter == false) {
                    //adapter
                   homeQuestionsRecyclerViewAdapter adapter = new homeQuestionsRecyclerViewAdapter(questionsArray, getActivity(), homeFragment.this);
                   contentGrid.setAdapter(adapter);
                   contentGrid.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                } else {
                    //adapter
                    homeQuestionsRecyclerViewAdapter adapter = new homeQuestionsRecyclerViewAdapter(tempQuestionArray, getActivity(), homeFragment.this);
                    contentGrid.setAdapter(adapter);
                    contentGrid.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /*
      This procedure will listen for a tag click
      Filters the tags and adds them to global temp array
      sets the temp array as an adapter
     */
    @Override
    public void onItemClick(int position) {
        if (tagslogaded) {
            //set that there is now an active filter
            activeFilter = true;
            //fetches selected tag
            ArrayList<tags> tempTags = new ArrayList<>();
            try {
                tempTags.add(((globalShared) getActivity().getApplication()).loggedUserTags.get(position));
            } catch (Exception ex) {
                Intent mainActivity = new Intent(getActivity(), com.wapazock.solveit.mainActivity.class);
                startActivity(mainActivity);
                getActivity().finish();
            }

            //set selected tag as the only adapter
            recyclerViewAdapter adapter = new recyclerViewAdapter(tempTags, this);
            tagsRecycler.setAdapter(adapter);
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            tagsRecycler.setLayoutManager(manager);


            //gets selected tag - tag id
            String selectedTAG = ((globalShared) getActivity().getApplication()).loggedUserTagsIDS.get(position);
            //clear temp array - to avoid duplication
            tempQuestionArray.clear();

            //import selected tag list to temporary array
            for (int i = 0; i != questionsArray.size(); i++) {
                if (questionsArray.get(i).getTag().equals(selectedTAG)) {
                    tempQuestionArray.add(questionsArray.get(i));
                }
            }


            //sets the adapter - but first checks if it is empty
            if (!tempQuestionArray.isEmpty()) {
                homeQuestionsRecyclerViewAdapter adapter2 = new homeQuestionsRecyclerViewAdapter(tempQuestionArray, getActivity(),homeFragment.this);
                contentGrid.setAdapter(adapter2);
                contentGrid.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            } else {
                contentGrid.setAdapter(null);
            }
        }
    }

    /*
       This procedure on long click will reset the arrays
     */
    @Override
    public void onItemLongClick(int position) {
        //remove active filter - for sorting to sort main array
        activeFilter = false;

        //clear main array o avoid duplication
        questionsArray.clear();

        //refresh questions
        getQuestions();
        getUserTags();
    }

    /*
       this procedure will set on touch of search
    */
    private void searchOnTouch() {
        if (tagslogaded) {
            searchBoxEdit.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    searchBoxEdit.setFocusable(true);
                    searchBoxEdit.setFocusableInTouchMode(true);
                    return false;
                }
            });
        }

    }

    /*
        this procedure will impement typing
     */
    private void searchQuestionListener() {
        searchBoxEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        searchBoxEdit.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchBoxEdit.getWindowToken(), 0);
        //...perform search
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchQuestion();
            }
        }, 200);

    }

    private ArrayList<questions> tempSearchQuestionArray = new ArrayList<>();
    private void searchQuestion() {
        if (!searchBoxEdit.getText().toString().isEmpty()) {
            //clear content
            contentGrid.setAdapter(null);


            try {

                mDatabase.child("questions").orderByChild("question").startAt(searchBoxEdit.getText().toString()).endAt(searchBoxEdit.getText().toString() + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot shot : snapshot.getChildren()) {
                            tempSearchQuestionArray.add(shot.getValue(questions.class));
                        }
                        //set adapter
                        if (!tempSearchQuestionArray.isEmpty()) {
                            homeQuestionsRecyclerViewAdapter adapter = new homeQuestionsRecyclerViewAdapter(tempSearchQuestionArray, getActivity(), homeFragment.this);
                            contentGrid.setAdapter(adapter);
                            contentGrid.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                            activeFilter = true;
                            //show no results
                            CountDownTimer timer = new CountDownTimer(100, 0) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    searchResult.setVisibility(GONE);
                                }
                            }.start();

                        } else {

                            //show no results
                            searchResult.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } catch (Exception ex) {
                ((globalShared) getActivity().getApplication()).gotException(ex, 2);
            }
        } else {
            getQuestions();
        }
    }


    @Override
    public void onQuestionClicked(int position) {
        if (activeFilter == true) {
            if (!tempQuestionArray.isEmpty()) {
                ((globalShared) getActivity().getApplication()).setPassingQuestion(tempQuestionArray.get(position));
                Intent activityPost = new Intent(getActivity(), activity_post.class);
                startActivity(activityPost);
                Animatoo.animateSlideUp(getContext());
            }
        } else {
            if (!questionsArray.isEmpty()) {
                ((globalShared) getActivity().getApplication()).setPassingQuestion(questionsArray.get(position));

                Intent activityPost = new Intent(getActivity(), activity_post.class);
                startActivity(activityPost);
                Animatoo.animateSlideUp(getContext());
            }
        }
    }

    @Override
    public void onQuestionLongClicked(int position) {
        if (activeFilter){
            if (tempSearchQuestionArray.get(position).getUserAccount().equals(((globalShared) getActivity().getApplication()).loggedInUser.getUid())){
                confirmDeletion dialoug = new confirmDeletion(getActivity(), tempSearchQuestionArray.get(position).getQuestionID(),homeFragment.this);
                dialoug.startAlert();
            }
            else {
                reportQuestion dialoug = new reportQuestion(getActivity(), tempSearchQuestionArray.get(position).getQuestionID(),homeFragment.this);
                dialoug.startAlert();
            }
        }
        else {
            if (questionsArray.get(position).getUserAccount().equals(((globalShared) getActivity().getApplication()).loggedInUser.getUid())) {
               Log.d(TAG, "onQuestionLongClicked: no filter " + questionsArray.get(position).getQuestionID());
               confirmDeletion dialoug = new confirmDeletion(getActivity(), questionsArray.get(position).getQuestionID(),homeFragment.this);
               dialoug.startAlert();
            }
            else {
                reportQuestion dialoug = new reportQuestion(getActivity(), questionsArray.get(position).getQuestionID(),homeFragment.this);
                dialoug.startAlert();
            }

        }
    }

    @Override
    public void update() {
        //get data
        questionsArray.clear();
        setShimmer();
        getUserTags();
    }

}