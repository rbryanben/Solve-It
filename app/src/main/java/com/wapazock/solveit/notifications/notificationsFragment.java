package com.wapazock.solveit.notifications;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.globalClasses.notifications;
import com.wapazock.solveit.globalClasses.questions;
import com.wapazock.solveit.globalClasses.replies;
import com.wapazock.solveit.home.homeQuestionsInterface;
import com.wapazock.solveit.independent_post_view.activity_post;
import com.wapazock.solveit.independent_reply_viewer.activity_reply_viewer;
import com.wapazock.solveit.utils.globalShared;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/*
  New notifications are from the current week
 */
public class notificationsFragment extends Fragment implements homeQuestionsInterface {

    //global variables
    RecyclerView newerNotifications;
    ArrayList<replies> REPLIES_ARRAY = new ArrayList<>();
    ArrayList<replies> REPLIES_TO_QUESTION_ARRAY = new ArrayList<>();

    ArrayList<notifications> NOTIFICATIONS = new ArrayList<>() ;
    DatabaseReference mDatabse ;

    Long LAST_LOGIN_TIME ;
    private static final String TAG = "notificationsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //references
        newerNotifications = view.findViewById(R.id.newer_notifications);

        //variables
        mDatabse = FirebaseDatabase.getInstance().getReference();


        //getNotifications
        getNotifications();
    }

    private void getNotifications() {
        //get last login
        mDatabse.child("login").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    LAST_LOGIN_TIME = Long.parseLong(snapshot.getValue().toString());
                    if (LAST_LOGIN_TIME != null){

                        getNotificationsNow();
                    }
                }
                catch (Exception ex){

                }
                //add last login
                String currentDate = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date());
                mDatabse.child("login").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).setValue(Long.parseLong(currentDate)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //do something
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       
    }


    private void getNotificationsNow() {
          final ArrayList<String> myQuestions = new ArrayList<>();
          //get all my question IDS
          final DatabaseReference ref = mDatabse ;
          ref.child("questions").orderByChild("userAccount").equalTo(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  for (DataSnapshot shot : snapshot.getChildren()) {
                          if (!myQuestions.contains(shot.child("questionID").getValue().toString())) {
                              myQuestions.add(shot.child("questionID").getValue().toString());
                              Log.d(TAG, "question :  " + shot.child("questionID").getValue().toString());
                          }



                          //get all my notifications for my questions
                          for (String question : myQuestions) {
                              ref.child("replies").orderByChild("replyTo").equalTo(question).addValueEventListener(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       for (DataSnapshot shot : snapshot.getChildren()){
                                           replies tempReply = shot.getValue(replies.class);

                                           notifications notification = new notifications();
                                           notification.parseReply(tempReply);


                                           //duplicate scan
                                           boolean found = false ;
                                           for (int i = 0 ; i != NOTIFICATIONS.size() ; i++){
                                               if (notification.getTextPreview().equals(NOTIFICATIONS.get(i).getTextPreview())){
                                                   found = true ;
                                               }
                                           }

                                           if (found == false){
                                               NOTIFICATIONS.add(notification);
                                           }
                                           else {
                                               Log.d(TAG, "onDataChange: duplicate found");
                                           }

                                       }

                                      //sort against time
                                      sortNotifications();

                                      removeMyNotifications();

                                      try {
                                          notificationRecyclerAdapter adapter = new notificationRecyclerAdapter(NOTIFICATIONS, getActivity(), LAST_LOGIN_TIME, notificationsFragment.this);
                                          newerNotifications.setAdapter(adapter);
                                          newerNotifications.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                                      } catch (Exception ex) {
                                          Log.d(TAG, "onDataChange: " + ex.toString());
                                      }


                                       //get replies to reply
                                      getRepliesToReply();


                                  }

                                  @Override
                                  public void onCancelled(@NonNull DatabaseError error) {

                                  }
                              });
                          }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
    }

    private void getRepliesToReply() {
        final ArrayList<String> myReplies = new ArrayList<>();

        if (((globalShared) getActivity().getApplication()).loggedInUser.getUid() != null) {
            mDatabse.child("replies").orderByChild("userAccount").equalTo(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        //check duplication
                        if (!myReplies.contains(shot.child("replyID").getValue().toString())) {
                            myReplies.add(shot.child("replyID").getValue().toString());
                            Log.d(TAG, "cfg: " + shot.child("replyID").getValue().toString());
                        }
                    }

                    //get replies to my reply
                    for (String replyID : myReplies) {
                        mDatabse.child("repliesToReply").orderByChild("replyTo").equalTo(replyID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    notifications notification = new notifications();
                                    notification.parseReplyToReply(shot.getValue(replies.class));

                                    Log.d(TAG, "mtk: " + notification.getTextPreview());
                                    //duplicate scan
                                    boolean found = false;
                                    for (int i = 0; i != NOTIFICATIONS.size(); i++) {
                                        if (notification.getTextPreview().equals(NOTIFICATIONS.get(i).getTextPreview())) {
                                            found = true;
                                        }
                                    }

                                    if (found == false) {
                                        NOTIFICATIONS.add(notification);
                                    } else {
                                        Log.d(TAG, "onDataChange: duplicate found");
                                    }
                                }


                                //sort against time
                                sortNotifications();

                                removeMyNotifications();

                                try {
                                    notificationRecyclerAdapter adapter = new notificationRecyclerAdapter(NOTIFICATIONS, getActivity(), LAST_LOGIN_TIME, notificationsFragment.this);
                                    newerNotifications.setAdapter(adapter);
                                    newerNotifications.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                                } catch (Exception ex) {
                                    Log.d(TAG, "onDataChange: " + ex.toString());
                                }

                                getRepliesToReplyToReply();
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
        }
    }

    private void getRepliesToReplyToReply() {
        final ArrayList<String> myRepliesToReplies = new ArrayList<>();

        //get all my replies to replies
            mDatabse.child("repliesToReply").orderByChild("userAccount").equalTo(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        if (!myRepliesToReplies.contains(shot.child("replyID"))) {
                            myRepliesToReplies.add(shot.child("replyID").getValue().toString());
                        }
                    }

                    //set data
                    //sort against time
                    sortNotifications();

                    //removeMyNotifications();


                    try {
                        notificationRecyclerAdapter adapter = new notificationRecyclerAdapter(NOTIFICATIONS, getActivity(), LAST_LOGIN_TIME, notificationsFragment.this);
                        newerNotifications.setAdapter(adapter);
                        newerNotifications.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    } catch (Exception ex) {
                        Log.d(TAG, "onDataChange: " + ex.toString());
                    }


                    //get replies to my reply
                    for (String replyID : myRepliesToReplies) {
                        mDatabse.child("repliesToReply").orderByChild("replyTo").equalTo(replyID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    notifications notification = new notifications();
                                    notification.parseReplyToReplyToReply(shot.getValue(replies.class));

                                    Log.d(TAG, "mtk: " + notification.getTextPreview());
                                    //duplicate scan
                                    boolean found = false;
                                    for (int i = 0; i != NOTIFICATIONS.size(); i++) {
                                        if (notification.getTextPreview().equals(NOTIFICATIONS.get(i).getTextPreview())) {
                                            found = true;
                                        }
                                    }

                                    if (found == false) {
                                        NOTIFICATIONS.add(notification);
                                    } else {
                                        Log.d(TAG, "onDataChange: duplicate found");
                                    }
                                }
                                //sort against time
                                sortNotifications();

                                removeMyNotifications();


                                try {
                                    notificationRecyclerAdapter adapter = new notificationRecyclerAdapter(NOTIFICATIONS, getActivity(), LAST_LOGIN_TIME, notificationsFragment.this);
                                    newerNotifications.setAdapter(adapter);
                                    newerNotifications.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                                } catch (Exception ex) {
                                    Log.d(TAG, "onDataChange: " + ex.toString());
                                }
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


    }

    private void removeMyNotifications() {
        for (int i = 0 ; i != NOTIFICATIONS.size() ; i++){
            if (NOTIFICATIONS.get(i).getUserAccount().equals(((globalShared) getActivity().getApplication()).loggedInUser.getUid())){
                 //NOTIFICATIONS.remove(i);
            }
        }
    }

    private void sortNotifications() {
        if (NOTIFICATIONS.size() > 0){
        for (int i =  0 ; i != NOTIFICATIONS.size()  - 1; i++){
            for (int b =  0 ; b != NOTIFICATIONS.size()  - 1; b++){
                if (NOTIFICATIONS.get(b).getTime() < NOTIFICATIONS.get(b + 1).getTime()){
                    notifications tempNotification = NOTIFICATIONS.get(b) ;
                    NOTIFICATIONS.set(b,NOTIFICATIONS.get(b + 1));
                    NOTIFICATIONS.set(b + 1 , tempNotification);
                }
            }
        }
      }
    }

    @Override
    public void onQuestionClicked(final int position) {
        final pleaseWaitAlert alert = new pleaseWaitAlert(getActivity());

        alert.startAlert();
        if (NOTIFICATIONS.get(position).isToQuestion()){

             //get question data
             mDatabse.child("questions").orderByChild("questionID").equalTo(NOTIFICATIONS.get(position).getQuestionID()).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     for (DataSnapshot shot : snapshot.getChildren()) {
                         questions QUESTION = shot.getValue(questions.class);
                         ((globalShared) getActivity().getApplication()).setPassingQuestion(QUESTION);
                         startActivity(new Intent(getActivity(), activity_post.class));
                         Animatoo.animateSlideUp(getContext());

                         Handler handler = new Handler();
                         handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 alert.stopAlert();
                             }
                         }, 1000);

                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });
         }
         else if (!NOTIFICATIONS.get(position).isToReply()){
             //get question data
             Log.d(TAG, "onDataChange: fetching for " + NOTIFICATIONS.get(position).getReplyID());
             mDatabse.child("repliesToReply").orderByChild("replyID").equalTo(NOTIFICATIONS.get(position).getReplyID()).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     Log.d(TAG, "onDataChange: sum " + snapshot.getChildrenCount());
                     for (DataSnapshot shot : snapshot.getChildren()) {
                         replies REPLY = shot.getValue(replies.class);
                         ((globalShared) getActivity().getApplication()).setPassingReply(REPLY);
                         startActivity(new Intent(getActivity(), activity_reply_viewer.class));
                         Animatoo.animateSlideUp(getContext());

                         Handler handler = new Handler();
                         handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 alert.stopAlert();
                             }
                         }, 1000);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });
         }
         else {
             //get question data
             Log.d(TAG, "onDataChange: fetching for " + NOTIFICATIONS.get(position).getReplyID());
             mDatabse.child("replies").orderByChild("replyID").equalTo(NOTIFICATIONS.get(position).getReplyID()).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     Log.d(TAG, "onDataChange: sum " + snapshot.getChildrenCount());
                     for (DataSnapshot shot : snapshot.getChildren()) {
                         replies REPLY = shot.getValue(replies.class);
                         ((globalShared) getActivity().getApplication()).setPassingReply(REPLY);
                         startActivity(new Intent(getActivity(), activity_reply_viewer.class));
                         Animatoo.animateSlideUp(getContext());

                         Handler handler = new Handler();
                         handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 alert.stopAlert();
                             }
                         }, 1000);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });
         }
    }

    @Override
    public void onQuestionLongClicked(int position) {

    }

    @Override
    public void update() {

    }


    class recyclerDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = 10 ;
        }
    }
}
