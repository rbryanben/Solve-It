package com.wapazock.solveit.independent_gallery;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.wapazock.solveit.R;
import com.wapazock.solveit.utils.globalShared;

import java.io.File;

public class fragment_camera extends Fragment {

    private Activity activity ;
    private TextureView textureView ;
    private int REQUESTCODE = 121  ;

    private String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
    private ImageView flash ;
    private Preview preview ;

    private ImageView capture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View customView = inflater.inflate(R.layout.fragment_camera,container,false);
        return customView ;
    }

    @Override
    public void onResume() {
        super.onResume();

        //activity
        activity = getActivity();

        //references
        textureView = activity.findViewById(R.id.view_finder);
        flash = activity.findViewById(R.id.camera_flash);
        capture = activity.findViewById(R.id.fragment_camera_capture);

        //start
        checkPermissions();
    }

    private void checkPermissions(){
        if (allPermissionsGranted()){
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            startCamera();
        }
        else {
            ActivityCompat.requestPermissions(activity,REQUIRED_PERMISSIONS,REQUESTCODE);
            checkPermissions();
        }
    }

    private void startCamera() {
        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(),textureView.getHeight());
        Size screen = new Size(textureView.getWidth(),textureView.getHeight());

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                textureView.setSurfaceTexture(output.getSurfaceTexture());
            }
        });

        CameraX.bindToLifecycle(this,preview );

        //set listeners
        try {
            setOnClickListeners();
        }
        catch (Exception ex){

        }

    }

    private void setOnClickListeners() {
        //flask
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preview.isTorchOn()){
                     preview.enableTorch(false);
                     flash.setImageResource(R.drawable.ic_flash_off);
                }
                else {
                    //flash test
                    flash.setImageResource(R.drawable.ic_flash_on);
                    preview.enableTorch(true);
                }

            }
        });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(activity.getWindowManager().getDefaultDisplay().getRotation()).build();

        final  ImageCapture imgCap = new ImageCapture(imageCaptureConfig);
        //capture
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+"Camera_"+System.currentTimeMillis()+".jpg");
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        ((globalShared) activity.getApplication()).setPassingGallery(file.getAbsolutePath());
                        activity.finish();
                        Animatoo.animateSlideDown(activity);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                    }
                });
            }
        });

        CameraX.bindToLifecycle(this ,preview,imgCap);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS){
            if (ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){
                return false ;
            }
        }

        return  true ;
    }
}
