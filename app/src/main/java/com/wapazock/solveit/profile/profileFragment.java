package com.wapazock.solveit.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.requestPaymentAlert;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_ask_question.independent_ask_question;
import com.wapazock.solveit.payments.five_dollars;
import com.wapazock.solveit.payments.payment;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

public class profileFragment extends Fragment {

    //global variables
    private ImageView coverPhoto ;
    private userAccount USER_ACCOUNT ;
    private DatabaseReference mDatabase ;

    private Boolean dataLoaded ;
    private ImageView profilePhoto ;

    private TextView name ;
    private TextView surname ;
    private TextView age ;

    private TextView education ;
    private TextView tags ;
    private TextView impact ;

    private TextView votes ;
    private TextView answers ;
    private TextView username ;

    private ShimmerFrameLayout layoutTest ;
    private LinearLayout basicLayout;
    private LinearLayout metaLayout;

    private RelativeLayout creditLayout ;
    private RelativeLayout donateLayout;

    private Button pendinAmount ;
    private Button requestPayment ;
    private static final String TAG = "profileFragment";
    private ArrayList<String> TAGS = new ArrayList<>();


    private Button fiveDonateButton ;
    private TextView donateMore ;
    private Button askQuestionButton ;

    private Float Amount ;
    private ArrayList<String> TAGNAMES = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        dataLoaded = false ;
        return view ;
        }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");

        if (((globalShared) getActivity().getApplication()).profileUpdated){
            ((globalShared) getActivity().getApplication()).profileUpdated = false ;
            Log.d(TAG, "onResume: called");
            getData();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "hyut");
        layoutTest = getActivity().findViewById(R.id.fragment_profile_cover_image_shimmer);

        //shimmer
        layoutTest.startShimmer();

        //references
        coverPhoto = getActivity().findViewById(R.id.fragment_profile_cover_image);
        profilePhoto = getActivity().findViewById(R.id.fragment_profile_profile_image);
        basicLayout = getActivity().findViewById(R.id.independent_post_basic);

        metaLayout = getActivity().findViewById(R.id.fragment_profile_meta_layout);
        creditLayout = getActivity().findViewById(R.id.fragment_profile_credit_layout);
        donateLayout = getActivity().findViewById(R.id.fragment_profile_donate_layout);

        name = getActivity().findViewById(R.id.independent_profile_view_basic_name);
        surname = getActivity().findViewById(R.id.independent_profile_view_basic_surname);
        age = getActivity().findViewById(R.id.independent_profile_view_basic_age);

        education = getActivity().findViewById(R.id.independent_profile_view_basic_education);
        tags = getActivity().findViewById(R.id.independent_profile_view_basic_tags);
        impact = getActivity().findViewById(R.id.independent_profile_view_meta_impact);

        votes = getActivity().findViewById(R.id.independent_profile_view_meta_votes);
        answers = getActivity().findViewById(R.id.independent_profile_view_meta_answers);
        username = getActivity().findViewById(R.id.independent_profile_view_usernameText);

        pendinAmount = getActivity().findViewById(R.id.fragment_profile_pending_amount);
        requestPayment = getActivity().findViewById(R.id.fragment_profile_request_payment);
        fiveDonateButton = getActivity().findViewById(R.id.fragment_profile_donate_button);

        donateMore = getActivity().findViewById(R.id.fragment_profile_donate_more_button);
        askQuestionButton = getActivity().findViewById(R.id.fragment_profile_ask_question);

        //variables
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //hide layouts
        basicLayout.setVisibility(View.GONE);
        metaLayout.setVisibility(View.GONE);
        creditLayout.setVisibility(View.GONE);
        donateLayout.setVisibility(View.GONE);
        layoutTest.setVisibility(View.VISIBLE);

        //set items
        getData();
        setListeners();
    }

    public void update(){
        getData();
    }

    private void setListeners() {

        //ask question button
        askQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ask_question_intent = new Intent(getContext(), independent_ask_question.class);
                startActivity(ask_question_intent);
                Animatoo.animateSlideUp(getContext());
            }
        });

        //request payment
        requestPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Amount >= 10) {
                    requestPaymentAlert alert = new requestPaymentAlert(getActivity());
                    alert.startAlert();
                }
                else {
                    ((globalShared) getActivity().getApplication()).showToast(getActivity(),"credit not enough");
                }
            }
        });


        //donate five
        fiveDonateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), five_dollars.class));
                Animatoo.animateZoom(getContext());
            }
        });

        //donate more
        donateMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), payment.class));
                Animatoo.animateZoom(getContext());
            }
        });
    }

    private void getData() {
        //get logged user
            mDatabase.child("userAccounts").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    USER_ACCOUNT = snapshot.getValue(userAccount.class);

                    //data loaded
                    dataLoaded = true ;

                    //set data
                    setData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void setData() {
        //cover photo
        ((globalShared) getActivity().getApplication()).loadProfilePictureIntoImageViewWithUrl(coverPhoto, getActivity() ,USER_ACCOUNT.getCoverImage());

        //profile image
        ((globalShared) getActivity().getApplication()).loadProfilePictureIntoImageViewWithUrl(profilePhoto,getActivity(),USER_ACCOUNT.getProfileImage());

        //basic information
        String[] arr = USER_ACCOUNT.getFullname().split(" ");
        name.setText("Name                 "+arr[0]);
        surname.setText("Surname           "+arr[arr.length - 1]);
        age.setText("Age                     "+USER_ACCOUNT.getAge());
        education.setText("Edu                    "+USER_ACCOUNT.getEducationLevel());
        username.setText(USER_ACCOUNT.getUsername());

        //meta
        DatabaseReference mMeta = FirebaseDatabase.getInstance().getReference();
        try {
            mMeta.child("meta").child(USER_ACCOUNT.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    unHide();

                    answers.setText("Answers            "+ snapshot.child("answers").getValue().toString());
                    votes.setText("Votes                 "+ snapshot.child("votes").getValue().toString());
                    impact.setText("Impact              "+ snapshot.child("impact").getValue().toString());
                    pendinAmount.setText( "ZWL $"+snapshot.child("pendingAmount").getValue().toString());
                    Amount = Float.parseFloat(snapshot.child("pendingAmount").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            ((globalShared) getActivity().getApplication()).gotException(ex,2);
        }


        //tags
        final DatabaseReference mTags = FirebaseDatabase.getInstance().getReference();
        try {
            mTags.child("userTags").child(USER_ACCOUNT.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()){
                        TAGS.add(shot.getValue().toString());
                    }

                    //get tags with name
                    for (String tag : TAGS){
                        mTags.child("tags").child(tag).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //get actual names
                                TAGNAMES.add(snapshot.getValue().toString());
                                tags.setText("Tags                  " + snapshot.getValue().toString());
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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (String name : TAGNAMES){
                        try {
                            tags.setText("Tags                  " + name + " ");
                        }
                        catch (Exception ex){

                        }

                        }
                }
            },2000);
        }
        catch (Exception ex){
            ((globalShared) getActivity().getApplication()).gotException(ex,2);
        }


        //credit amount
    }

    private void unHide() {

        //hide layouts
        basicLayout.setVisibility(View.VISIBLE);
        metaLayout.setVisibility(View.VISIBLE);
        creditLayout.setVisibility(View.VISIBLE);
        donateLayout.setVisibility(View.VISIBLE);

        //shimmer disable
        layoutTest.stopShimmer();
        layoutTest.setVisibility(View.GONE);
    }


}
