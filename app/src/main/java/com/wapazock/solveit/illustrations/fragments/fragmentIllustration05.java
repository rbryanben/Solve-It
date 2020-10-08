package com.wapazock.solveit.illustrations.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.wapazock.solveit.R;
import com.wapazock.solveit.signup.signupActivity00;
import com.wapazock.solveit.utils.ViewPagerAdapter;

public class fragmentIllustration05 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_illustration05,container,false);
        return view ;
    }

    @Override
    public void onResume() {
        super.onResume();
        //set finish button listener
        finishButtonListener();
    }

    /*
         This procedure will set listener for finishing the activity
     */
    private void finishButtonListener() {
        Button finishButton = (Button)getActivity().findViewById(R.id.fragment_illustration05_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), signupActivity00.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Animatoo.animateZoom(getContext());
                getActivity().finish();
            }
        });
    }

}
