package com.wapazock.solveit.settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.suke.widget.SwitchButton;
import com.wapazock.solveit.R;
import com.wapazock.solveit.alertDialouges.pleaseWaitAlert;
import com.wapazock.solveit.alertDialouges.termsConditionsAlert;
import com.wapazock.solveit.globalClasses.userAccount;
import com.wapazock.solveit.independent_gallery.activity_gallery;
import com.wapazock.solveit.mainActivity;
import com.wapazock.solveit.signup.signupActivity03;
import com.wapazock.solveit.utils.globalShared;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
public class settingsFragment extends Fragment {


    //global variables
    private Spinner educationSpinner;
    private Spinner genderSpinner ;
    private SwitchButton switchButton;

    private CircleImageView profileImage ;
    private ImageView coverImage ;
    private TextView termsAndConditions ;

    private userAccount USER_ACCOUNT ;
    private DatabaseReference mDatabse ;
    private TextView changeProfileImage ;

    private TextView changeCoverImage ;
    private EditText username ;
    private EditText fullname ;

    private EditText age ;
    private EditText mobileNumber ;
    private Button signOut , tags ;

    private Button updateButton ;
    private TextView changePassword;


    private StorageReference mStorageRef;

    private static final String TAG = "settingsFragment";
    private String PENDING_VALUE ;
    private String TEMP_URI ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        return view ;
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseAuth mAuthz = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuthz.getCurrentUser();
        ((globalShared) getActivity().getApplication()).loggedInUser = currentUser ;
        if (currentUser == null){
            ((globalShared) getActivity().getApplication()).changedPassword = false;
            mAuthz.signOut();
            startActivity(new Intent(getContext(), mainActivity.class));
            Animatoo.animateSlideDown(getContext());
            getActivity().finish();
        }

        //email
        EditText verification = getActivity().findViewById(R.id.emailEdit);
        if (((globalShared) getActivity().getApplication()).loggedInUser.isEmailVerified()){
            Log.d(TAG, "onResume: verified");
            verification.setBackgroundResource(R.drawable.background_green_button_white);
            verification.setTextColor(Color.WHITE);
            verification.setText("(Verified) " +((globalShared) getActivity().getApplication()).loggedInUser.getEmail());
        }
        else {
            verification.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ((globalShared) getActivity().getApplication()).loggedInUser.sendEmailVerification();
                    Uri webpage = Uri.parse("https://mail.google.com/mail");
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    return false;
                }
            });
        }
    



        //check changed password
        if (((globalShared) getActivity().getApplication()).changedPassword == true){
            ((globalShared) getActivity().getApplication()).changedPassword = false;
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            startActivity(new Intent(getContext(), mainActivity.class));
            Animatoo.animateSlideDown(getContext());
            getActivity().finish();
        }

        //get string from gallery
        TEMP_URI = ((globalShared) getActivity().getApplication()).getPassingGallery();

        try {
            //check pending
            if (TEMP_URI != null) {
                //profile
                if (PENDING_VALUE.equals("profile")) {
                    uploadImage();
                } else {
                    uploadCoverImage();
                }
            }
        }
        catch (Exception ex){

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //references
        educationSpinner = view.findViewById(R.id.highestEducation);
        genderSpinner= view.findViewById(R.id.activity_signup_page01_genderEdit);
        switchButton = view.findViewById(R.id.switch_button);

        username = view.findViewById(R.id.usernameEdit);
        fullname = view.findViewById(R.id.fullnameEdit);
        age = view.findViewById(R.id.ageEdit);

        mobileNumber = view.findViewById(R.id.mobileEdit);
        tags = view.findViewById(R.id.tags);
        signOut = view.findViewById(R.id.signOut);

        mDatabse = FirebaseDatabase.getInstance().getReference();
        profileImage= view.findViewById(R.id.profile_image);
        coverImage = view.findViewById(R.id.coverImage);

        updateButton = view.findViewById(R.id.update_button);
        changePassword = view.findViewById(R.id.change_password);

        changeCoverImage = view.findViewById(R.id.change_cover_picture);
        changeProfileImage = view.findViewById(R.id.change_profile_picture);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        termsAndConditions = view.findViewById(R.id.terms_and_conditions);

        //database
        mDatabse.child("userAccounts").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                USER_ACCOUNT = snapshot.getValue(userAccount.class);

                //set data
                setData();
                setListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //set spinners
        setSpinner();

        //notifications
        notificationsManager();
    }

    private void uploadCoverImage() {
        final pleaseWaitAlert alert = new pleaseWaitAlert(getActivity());
        alert.startAlert();

        String tempPath = Environment.getExternalStorageDirectory() + "/SolveIt" ;
        File tempFile = new File(tempPath);
        String filePath = SiliCompressor.with(getContext()).compress(TEMP_URI, tempFile);

        final File file = new File(filePath);

        StorageReference mUpload = mStorageRef.child("profiles").child("covers").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).child("cover");
        mUpload.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //upload to realtime database
                        mDatabse.child("userAccounts").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).child("coverImage").setValue(uri.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        alert.stopAlert();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alert.stopAlert();
                                file.delete();
                                ((globalShared) getActivity().getApplication()).showToast(getActivity(), "failed");
                            }
                        });


                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }

    private void uploadImage() {
        final pleaseWaitAlert alert = new pleaseWaitAlert(getActivity());
        alert.startAlert();

        String tempPath = Environment.getExternalStorageDirectory() + "/SolveIt" ;
        File tempFile = new File(tempPath);
        String filePath = SiliCompressor.with(getContext()).compress(TEMP_URI, tempFile);

        final File file = new File(filePath);

        StorageReference mUpload = mStorageRef.child("profiles").child("pictures").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).child("profile");
                mUpload.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               //upload to realtime database
                                mDatabse.child("userAccounts").child(((globalShared) getActivity().getApplication()).loggedInUser.getUid()).child("profileImage").setValue(uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                alert.stopAlert();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        alert.stopAlert();
                                        file.delete();

                                        //interface
                                        ((globalShared) getActivity().getApplication()).showToast(getActivity(), "failed");
                                    }
                                });


                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void setListeners() {

        //terms and conditions
        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsConditionsAlert alert = new termsConditionsAlert(getActivity());
                alert.startAlert();
            }
        });


        //update onclick
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if username exists
                mDatabse.child("userAccounts").orderByChild("username").equalTo(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if (snapshot.getChildrenCount() > 0){
                             for (DataSnapshot shot : snapshot.getChildren() ){
                                 if (!shot.child("id").getValue().toString().equals(((globalShared) getActivity().getApplication()).loggedInUser.getUid())){
                                     ((globalShared) getActivity().getApplication()).showToast(getActivity(),"Username already exists");
                                 }
                             }

                         }
                         else {
                             String UID = ((globalShared) getActivity().getApplication()).loggedInUser.getUid();
                             mDatabse.child("userAccounts").child(UID).child("fullname").setValue(fullname.getText().toString());
                             mDatabse.child("userAccounts").child(UID).child("mobile").setValue(mobileNumber.getText().toString());
                             mDatabse.child("userAccounts").child(UID).child("username").setValue(username.getText().toString());
                             mDatabse.child("userAccounts").child(UID).child("educationLevel").setValue(educationSpinner.getSelectedItem().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         final pleaseWaitAlert alert = new pleaseWaitAlert(getActivity());
                                         alert.startAlert();

                                         Handler handler = new Handler();
                                         handler.postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 alert.stopAlert();
                                             }
                                         }, 1000) ;
                                         ((globalShared) getActivity().getApplication()).profileUpdated = true ;
                                     }
                                     else {
                                         ((globalShared) getActivity().getApplication()).showToast(getActivity(),"failed");
                                     }
                                 }
                             });
                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        //change password onClick
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), com.wapazock.solveit.settings.changePassword.class));
                Animatoo.animateSlideUp(getContext());
            }
        });

        //change profile image
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PENDING_VALUE = "profile" ;

                //launch the gallery fragment
                startActivity(new Intent(getContext(), activity_gallery.class));
                Animatoo.animateSlideUp(getContext());
            }
        });

        //change cover image listeners
        changeCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PENDING_VALUE = "cover" ;

                //launch the gallery fragment
                startActivity(new Intent(getContext(), activity_gallery.class));
                Animatoo.animateSlideUp(getContext());
            }
        });

        //sign out
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                try {
                    mAuth.signOut();
                    startActivity(new Intent(getContext(), mainActivity.class));
                    Animatoo.animateSlideDown(getContext());
                    getActivity().finish();
                }
                catch (Exception ex){
                    ((globalShared) getActivity().getApplication()).gotException(ex,5);
                    ((globalShared) getActivity().getApplication()).showToast(getActivity(),"logout failed");
                }
            }
        });

        //tags button
        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tagsIntent = new Intent(getActivity(), changeTags.class);
                tagsIntent.putExtra("educationLevel",USER_ACCOUNT.getEducationLevel());
                startActivity(tagsIntent);
                Animatoo.animateSlideUp(getContext());
            }
        });

    }

    private void setData() {

        try {
            //set profile image and cover image
            ((globalShared) getActivity().getApplication()).loadProfilePictureIntoImageViewWithUrl(profileImage, getActivity(), USER_ACCOUNT.getProfileImage());
            ((globalShared) getActivity().getApplication()).loadProfilePictureIntoImageViewWithUrl(coverImage, getActivity(), USER_ACCOUNT.getCoverImage());
            mobileNumber.setText(USER_ACCOUNT.getMobile());
            username.setText(USER_ACCOUNT.getUsername());
            fullname.setText(USER_ACCOUNT.getFullname());
            age.setText(USER_ACCOUNT.getAge());
        }
        catch (Exception ex){
            setData();
        }

        //gender
        if (USER_ACCOUNT.getGender().equals("male")) {
            genderSpinner.setSelection(0);
        }
        else if (USER_ACCOUNT.getGender().equals("female")){
            genderSpinner.setSelection(1);
        }
        else {
            genderSpinner.setSelection(2);
        }

        //education level
        switch (USER_ACCOUNT.getEducationLevel()){
            case "Higher-education" :
                educationSpinner.setSelection(3);
                break;
            case "A-level" :
                educationSpinner.setSelection(2);
                break;
            case "O-level" :
                educationSpinner.setSelection(1);
                break;
            case "Primary-education":
                educationSpinner.setSelection(0);
                break;
        }

    }

    private void notificationsManager() {
        //check for notifications
        Cursor cursor = ((globalShared) getActivity().getApplication()).configurationDatabase.readSQL("SELECT * FROM configurations WHERE (NAME = 'notifications') ");
        cursor.moveToFirst();

        if (cursor.getString(2).equals("enabled")){
            switchButton.setChecked(true);
        }
        else {
            switchButton.setChecked(false);
        }

        //listener
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked == false){
                    ((globalShared) getActivity().getApplication()).configurationDatabase.writeSQL("UPDATE configurations SET varue = 'disabled' WHERE NAME = 'notifications' ");
                }
                else {
                    ((globalShared) getActivity().getApplication()).configurationDatabase.writeSQL("UPDATE configurations SET varue = 'enabled" +
                            "' WHERE NAME = 'notifications' ");
                }
            }
        });
    }

    private void setSpinner() {
        //Spinner educations
        ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_education, R.layout.simple_spinner_item);
           educationSpinner.setAdapter(educationAdapter );

        //Spinner gender
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_gender, R.layout.simple_spinner_item);

        genderSpinner.setAdapter(genderAdapter);

    }
}
