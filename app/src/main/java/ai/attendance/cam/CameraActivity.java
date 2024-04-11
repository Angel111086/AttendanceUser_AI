package ai.attendance.cam;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import ai.attendance.R;
import ai.attendance.activities.MarkedAttendance;
import ai.attendance.model.QRModel;

public class CameraActivity  extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    Button captureButton;
    public static final int MEDIA_TYPE_IMAGE = 1;
    static Context con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //captureButton = (Button) findViewById(R.id.button_capture);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        // Create an instance of Camera
        con = getApplicationContext();
        try {
            //mCamera = getCameraInstance();
            mCamera = getFrontFacingCamera();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Create our Preview view and set it as the content of our activity.
        //mPreview = new CameraPreview(CameraActivity.this, mCamera);

        if(mCamera == null){
            //mCamera = getCameraInstance();
            mCamera = getFrontFacingCamera();
            //Log.e("MCamActivity",mCamera.toString());
            mPreview = new CameraPreview(con, mCamera);
        }else {
            mPreview = new CameraPreview(con, mCamera);
        }
        preview.addView(mPreview);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mCamera == null){
                    Toast.makeText(CameraActivity.this, "Camera Null", Toast.LENGTH_SHORT).show();
                }
                mCamera.takePicture(null, null, mPicture);
                {}}},4000);
    }

    Bitmap bitmap;
    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.gc();
            bitmap = null;
            BitmapWorkerTask task = new BitmapWorkerTask(data);
            task.execute(0);

        }
    };

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<byte[]> dataf;
        private int data = 0;

        public BitmapWorkerTask(byte[] imgdata) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            dataf = new WeakReference<byte[]>(imgdata);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            ResultActivity(dataf.get());
            return mainbitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mainbitmap != null) {


                Intent i = new Intent();
                i.putExtra("BitmapImage", mainbitmap);
                setResult(-1, i);
                getOutputMediaFile(mainbitmap);
                QRModel.setBmp(mainbitmap);
                // Here I am Setting the Requestcode 1, you can put according to
                // your requirement
                finish();
            }
        }
    }

    Bitmap mainbitmap;

    public void ResultActivity(byte[] data) {
        mainbitmap = null;
        mainbitmap = decodeSampledBitmapFromResource(data, 200, 200);
        mainbitmap=RotateBitmap(mainbitmap,270);
        mainbitmap=flip(mainbitmap);
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /** A safe way to get an instance of the Camera object. */
//    public static Camera getCameraInstance() {
//        Camera c = null;
//        try {
//            Log.d("No of cameras", Camera.getNumberOfCameras() + "");
//            for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
//                CameraInfo camInfo = new CameraInfo();
//                Camera.getCameraInfo(camNo, camInfo);
//
//                if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
//                    c = Camera.open(camNo);
//                    c.setDisplayOrientation(90);
//                }
//            }
//            return c; // returns null if camera is unavailable
//        }catch (Exception e){
//            Log.d("No of cameras", Camera.getNumberOfCameras() + "");
//            for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
//                CameraInfo camInfo = new CameraInfo();
//                Camera.getCameraInfo(camNo, camInfo);
//
//                if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
//                    c = Camera.open(camNo);
//                    c.setDisplayOrientation(90);
//                }
//            }
//            e.printStackTrace();
//            return c; // returns null if camera is unavailable
//
//
//           }
//
//    }

      static void setFrontCamera(Camera camera) {
        final Camera.Parameters parameters = camera.getParameters();
        parameters.set("camera-id", 1);
        try {
            camera.setParameters(parameters);
        } catch (final Exception e) {
            // If we can't set front camera it means that device hasn't got "camera-id". Maybe it's not Galaxy S.
            e.printStackTrace();
        }
    }
    public static Camera getFrontFacingCamera() throws NoSuchElementException {

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < Camera.getNumberOfCameras(); cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, cameraInfo);
            Log.e("Camera Facing Front",""+Camera.CameraInfo.CAMERA_FACING_FRONT);
            Log.e("Camera Facing Back",""+ CameraInfo.CAMERA_FACING_BACK);
//
//            releaseCamera();
//                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
//                    cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
//                }
//                else {
//                    cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_BACK;
//                }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    Camera c = Camera.open(cameraIndex);
                    setFrontCamera(c);
                    c.setDisplayOrientation(90);
                    return c;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        throw new NoSuchElementException("Can't find front camera.");
    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isStoragePermissionGranted();
        if (mCamera == null) {
            setContentView(R.layout.activity_camera);
            //captureButton = (Button) findViewById(R.id.button_capture);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

            // Create an instance of Camera
            con = getApplicationContext();
            try {
                //mCamera = getCameraInstance();
                mCamera = getFrontFacingCamera();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Create our Preview view and set it as the content of our
            // activity.
            //mPreview = new CameraPreview(CameraActivity.this, mCamera);
            mPreview = new CameraPreview(con, mCamera);
            preview.addView(mPreview);

        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    // rotate the bitmap to portrait
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    //the front camera displays the mirror image, we should flip it to its original
    Bitmap flip(Bitmap d)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d;
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    //make picture and save to a folder
    private File getOutputMediaFile(Bitmap m) {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/JCG Camera");
        //File mediaStorageDir = new File("/sdcard/", "JCG Camera");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        Log.e("File",mediaFile.getName());
        QRModel.setImage_path(mediaFile.getName());
        try{
            FileOutputStream fos = new FileOutputStream(mediaFile);
            m.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
        }catch (Exception e){e.printStackTrace();}
        return mediaFile;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.e("TAG","Permission is granted");
                return true;
            }
            else if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                Log.e("Read","Permission");
                return true;
            }
            else {
                Log.e("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.e("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.e("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}



