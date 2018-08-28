package com.proclivis.smartknock.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;


import com.proclivis.smartknock.R;

import java.io.IOException;
import java.io.OutputStream;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    @SuppressWarnings("deprecation")
    Camera camera;
    SurfaceView camView;
    SurfaceHolder surfaceHolder;
    boolean camCondition = false;
    Button cap;

    Context context;
    Activity activity;

    int degree = 0;

    @SuppressLint("WrongConstant")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.camera_activity_potrait);

                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.camera_activity_landscape);
        }
        context = this;
        activity = this;

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        camView = findViewById(R.id.camerapreview);
        surfaceHolder = camView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        cap = findViewById(R.id.takepicture);
        cap.setVisibility(View.VISIBLE);
        cap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cap.setVisibility(View.GONE);
                camera.takePicture(null, null, null, mPictureCallback);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        degree = getPreviewDegree(activity);
    }

    @SuppressWarnings("deprecation")
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @SuppressLint("SdCardPath")
        public void onPictureTaken(byte[] data, Camera c) {

            /*FileOutputStream outStream = null;
            try {

                outStream = new FileOutputStream("/sdcard/AndroidCodec_" +
                        System.currentTimeMillis() + ".jpg");
                //outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            try {

                ContentValues image = new ContentValues();

                image.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis());
                image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                image.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis());
                image.put(MediaStore.Images.Media.DESCRIPTION, System.currentTimeMillis());
                image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                image.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                image.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
                image.put(MediaStore.Images.Media.ORIENTATION, degree);

                Uri uri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);

                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    Bitmap rotatedBitmap = null;

                    if (degree == 0){
                        Matrix matrix = new Matrix();
                        matrix.postRotate(270);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                        rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                    } else if (degree == 90){
                        rotatedBitmap = bitmap;
                    } else if (degree == 180){
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                        rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                    } else if (degree == 270){
                        Matrix matrix = new Matrix();
                        matrix.postRotate(180);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                        rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                    }
                    assert uri != null;
                    OutputStream out = getContentResolver().openOutputStream(uri);
                    assert rotatedBitmap != null;
                    boolean success = rotatedBitmap.compress(
                            Bitmap.CompressFormat.JPEG, 75, out);
                    assert out != null;
                    out.close();
                    if (!success) {
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                assert uri != null;
                intent.putExtra("image_uri", uri.toString());
                setResult(2, intent);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camCondition) {
            camera.stopPreview();
            camCondition = false;
        }

        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);
                setCameraDisplayOrientation(activity, cameraId, camera);
                camera.startPreview();
                camCondition = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        camCondition = false;
    }

    int cameraId;

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }

        camera = Camera.open(cameraId);
        setCameraDisplayOrientation(activity, cameraId, camera);
        camera.startPreview();

    }

    public void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, Camera camera) {

        Camera.CameraInfo info =
                new Camera.CameraInfo();

        Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degree) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degree + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public int getPreviewDegree(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
