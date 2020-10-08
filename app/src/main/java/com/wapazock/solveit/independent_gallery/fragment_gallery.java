package com.wapazock.solveit.independent_gallery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

import java.util.ArrayList;

public class fragment_gallery extends Fragment {

    //global
    private Spinner directoriesSpinner ;
    private GridView galleryGrid ;

    private Activity activity ;
    private ArrayList<String> directoriesArray = new ArrayList<>();
    private static final String TAG = "fragment_gallery";

    private ImageView backButton ;
    ArrayList<String> rootDirectoriesNameArray = new ArrayList<>();
    ArrayList<String> selectedDirectoryFiles = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View customView = inflater.inflate(R.layout.fragment_gallery,container,false);
        return customView ;
    }

    @Override
    public void onResume() {
        super.onResume();

        //references
        activity = getActivity();
        directoriesSpinner = activity.findViewById(R.id.fragment_gallery_directory_spinner);
        galleryGrid = activity.findViewById(R.id.fragment_gallery_gellery_grid);

        backButton = activity.findViewById(R.id.fragment_gallery_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Animatoo.animateSlideDown(activity);
            }
        });

        //setup spinner
        setupSpinner();

        setupImagesReturn();
    }

    private void setupImagesReturn() {
        galleryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((globalShared) activity.getApplication()).setPassingGallery(selectedDirectoryFiles.get(position));
                activity.finish();
                Animatoo.animateSlideDown(activity);
            }
        });
    }

    private void setupSpinner() {
        //items
        rootDirectoriesNameArray = ((globalShared) activity.getApplication()).getDirectories();

        ArrayAdapter<String> spinnerDirectories = new ArrayAdapter<String>(activity,R.layout.simple_spinner_dropdown_item,rootDirectoriesNameArray);
        directoriesSpinner.setAdapter(spinnerDirectories);

        try {
            int spinnerPosition = spinnerDirectories.getPosition("DCIM");
            directoriesSpinner.setSelection(spinnerPosition);
        }
        catch (Exception ex){

        }

        //on click
        directoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDirectoryFiles = ((globalShared) activity.getApplication()).getFilesFromDirectory(Environment.
                        getExternalStorageDirectory().getPath()+"/"+rootDirectoriesNameArray.get(position));

                //setup grid
                galleryAdapter adapter = new galleryAdapter(activity,selectedDirectoryFiles);
                galleryGrid.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
