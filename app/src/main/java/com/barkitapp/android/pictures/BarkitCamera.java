package com.barkitapp.android.pictures;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.BitmapOperations;
import com.barkitapp.android._core.services.MediaFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@SuppressWarnings("deprecation")
public class BarkitCamera extends Activity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;

    private void setUpCamera() {
        mCamera = getCameraInstance();

        if(mCamera == null)
            finish();

        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        //STEP #1: Get rotation degrees
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break; //Upside down
            case Surface.ROTATION_270: degrees = 270; break; //Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the parameters
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for(int i=0;i<sizes.size();i++)
        {
            if(sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        params.setPictureSize(size.width, size.height);

        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setExposureCompensation(0);
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);

        mCamera.setParameters(params);

        RelativeLayout captureButton = (RelativeLayout) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //setUpCamera();
    }

    @Override
    public void onStart() {
        super.onStart();

        setUpCamera();
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Toast.makeText(this, R.string.no_camera_on_phone, Toast.LENGTH_LONG).show();
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = MediaFile.getOutputMediaFile(getApplicationContext());
            if (pictureFile == null) {
                return;
            }
            try {

                Bitmap original = BitmapFactory.decodeByteArray(data , 0, data.length);
                Bitmap resized = BitmapOperations.resize(original, 623, 831);
                resized = BitmapOperations.rotateImage(pictureFile.getAbsolutePath(), resized);

                if(resized == null)
                {
                    finish();
                    return;
                }

                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(blob.toByteArray());
                fos.close();

                original.recycle();
                resized.recycle();

                Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("path", pictureFile.getAbsolutePath());
                getApplicationContext().startActivity(intent);
                finish();

            } catch (Exception e) {
                Toast.makeText(getApplication(), R.string.picture_failed, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };
}

//        RelativeLayout useOtherCamera = (RelativeLayout) findViewById(R.id.useOtherCamera);
////if phone has only one camera, hide "switch camera" button
//        if(Camera.getNumberOfCameras() == 1) {
//            useOtherCamera.setVisibility(View.INVISIBLE);
//        }
//        else {
//            useOtherCamera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //swap the id of the camera to be used
//                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//                    }
//                    else {
//                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//                    }
//
//                    Intent intent = getIntent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    intent.putExtra(CAMERA_FACING, currentCameraId);
//                    finish();
//                    startActivity(intent);
//                }
//            });
//        }