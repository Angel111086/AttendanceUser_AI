package ai.attendance.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.appservice.LocationTrack;
import ai.attendance.cam.CameraActivity;
import ai.attendance.cam.CameraPreview;
import ai.attendance.cam.SiteCameraActivity;
import ai.attendance.model.LocationSingleton;
import ai.attendance.model.QRModel;
import ai.attendance.model.SignUpModel;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DashboardActivity extends AppCompatActivity {


    LinearLayout ln_attendance, ln_logout, layout_home, ln_scanAtt, ln_onSiteAtt;
    DrawerLayout drawer_layout;
    ImageView img_navigation, img_profile,testimg,img_profile_drawer;
    TextView username, usernumber, usercompany, userlocation;
    String COMPANY = "",LATITUDE="",LONGTITUDE="",EMPDATE;
    private ArrayList<String> permissionsToRequest = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    QRModel qrModel=new QRModel();
    //Test Camera
    private final static int CAMERA_PIC_REQUEST1 = 0;
    private final static int CAMERA_PIC_REQUEST2 = 1;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    int cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();
        listener();
        getLocation();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
//
//                getLocation();
//            }
//        }, 1000);



    }
    private void init() {
        ln_attendance = (LinearLayout) findViewById(R.id.ln_attendance);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        img_navigation = (ImageView) findViewById(R.id.img_navigation);
        ln_logout = (LinearLayout) findViewById(R.id.ln_logout);
        layout_home = (LinearLayout) findViewById(R.id.layout_home);
        img_profile = findViewById(R.id.img_profile);
        img_profile_drawer = findViewById(R.id.img_profile_drawer);
        username = findViewById(R.id.username);
        usernumber = findViewById(R.id.usernumber);
        usercompany = findViewById(R.id.usercompany);
        userlocation = findViewById(R.id.userlocation);
        ln_scanAtt = findViewById(R.id.ln_scanAtt);
        ln_onSiteAtt = findViewById(R.id.ln_onSiteAtt);
        //testimg = findViewById(R.id.testimg);
        username.setText(AttendanceAISharedPreference.loadUserNameFromPreferences(DashboardActivity.this));
        usernumber.setText(AttendanceAISharedPreference.loadMobileFromPreferences(DashboardActivity.this));
        usercompany.setText(AttendanceAISharedPreference.loadCompanyNameFromPreferences(DashboardActivity.this));
        userlocation.setText(AttendanceAISharedPreference.loadUserCompanyLocationFromPreferences(DashboardActivity.this));
        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        try {
            String pic = AttendanceAISharedPreference.loadPhotoFromPreferences(DashboardActivity.this);
            Glide.with(DashboardActivity.this).load(Uri.parse("http://35.244.12.111:1234/uploads/"+pic))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(img_profile_drawer);
            //Picasso.get().load("http://192.168.1.24:1234/uploads/"+pic).into(img_profile_drawer);

        }catch (Exception e){e.printStackTrace();
            Toast.makeText(DashboardActivity.this, "No Profile Picture Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void listener() {

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Profile", "Test");
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            }
        });

        img_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
            }
        });

        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.closeDrawers();
            }
        });

        ln_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, QRCodeActivity.class));
            }
        });

        ln_scanAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(DashboardActivity.this);
                //integrator.setCameraId(1);
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.initiateScan();

            }
        });
        ln_onSiteAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DashboardActivity.this, "Site Att", Toast.LENGTH_SHORT).show();

                Intent cameraIntent = new Intent();
                cameraIntent.setClass(DashboardActivity.this, SiteCameraActivity.class);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST2);

            }
        });
        ln_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finishAffinity();

                AttendanceAISharedPreference.saveUserNameToPreferences(DashboardActivity.this, "NA");
                AttendanceAISharedPreference.saveCompanyNameToPreferences(DashboardActivity.this, "NA");
                AttendanceAISharedPreference.saveMobileToPreferences(DashboardActivity.this, "NA");
                AttendanceAISharedPreference.saveUserLatituteToPreferences(DashboardActivity.this, "NA");
                AttendanceAISharedPreference.saveUserLongitudeToPreferences(DashboardActivity.this, "NA");
                AttendanceAISharedPreference.saveUserCompanyLocationToPreferences(DashboardActivity.this, "NA");
                AttendanceAISharedPreference.saveUserEmpCodeToPreferences(DashboardActivity.this,"NA");
                AttendanceAISharedPreference.saveOtpToPreferences(DashboardActivity.this,"NA");
            }
        });

    }

    private void getLocation() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        locationTrack = new LocationTrack(DashboardActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            Log.e("LatLong", "" + latitude + " : " + longitude);
            //getCompleteAddressString(latitude,longitude);
            AttendanceAISharedPreference.saveUserLatituteToPreferences(DashboardActivity.this, latitude + "");
            AttendanceAISharedPreference.saveUserLongitudeToPreferences(DashboardActivity.this, longitude + "");

            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }

    }

    private String getSiteLocation() {
        String off_site="";
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        locationTrack = new LocationTrack(DashboardActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            Log.e("LatLong", "" + latitude + " : " + longitude);
            off_site = getCompleteAddressString(latitude,longitude);

        } else {
            locationTrack.showSettingsAlert();
        }
        return off_site;
    }
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DashboardActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                if (addresses.size() > 0) {
                    Log.e("Locality",addresses.get(0).getLocality());
                    Log.e("Country",addresses.get(0).getCountryName());
                }

                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Toast.makeText(DashboardActivity.this, strAdd, Toast.LENGTH_SHORT).show();
                //userlocation.setText(AttendanceAISharedPreference.loadUserLocationFromPreferences(DashboardActivity.this));

                Log.e("My Current location address", strReturnedAddress.toString());
                Toast.makeText(DashboardActivity.this, "You are at: "+strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("My Current location address", "No Address returned!");
                Toast.makeText(DashboardActivity.this,"No Address returned!" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }

    public String formatFigureToTwoPlaces(double value) {
        DecimalFormat myFormatter = new DecimalFormat("00.00000");
        return myFormatter.format(value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.e("Scanned Data", result.getContents());
                try {
                    String test = result.getContents();
                    JSONObject obj = new JSONObject(test);
                    COMPANY = obj.getString("SITE_ID");
                    EMPDATE = obj.getString("EMPDATE");
                    LATITUDE = obj.getString("EMPLATITUDE");
                    LONGTITUDE = obj.getString("EMPLONGITUDE");
                    Log.e("Att Data",COMPANY + EMPDATE + LATITUDE + LONGTITUDE);

                    //if (COMPANY.equals(AttendanceAISharedPreference.)) {

                        //Intent in = new Intent(DashboardActivity.this,MarkedAttendance.class);
                        //startActivity(in);
                        Log.e("Scanned Data",result.getContents());

                        if (result.getContents()!=null){
//                            mCamera = Camera.open();
//                            mCamera.takePicture(null,null,mPicture);

                            if (getFrontCameraId() == -1) {
                                //if (findFrontFacingCamera() == -1) {
                                Toast.makeText(getApplicationContext(),
                                        "Front Camera Not Detected", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent cameraIntent = new Intent();
                                cameraIntent.setClass(this, CameraActivity.class);
                                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);

                            }

                        }
//                    }else{
//                        Toast.makeText(this, "Invalid Data" , Toast.LENGTH_LONG).show();
//                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

            if (requestCode == CAMERA_PIC_REQUEST1) {
                if (resultCode == RESULT_OK) {
                    try {

                        Toast.makeText(this, "Picture taken", Toast.LENGTH_SHORT)
                                .show();
                        //Log.e("CamPic",data.getData()+"");
                        Log.e("QRModelImage",QRModel.getImage_path());
                        qrModel = new QRModel(COMPANY,EMPDATE,LATITUDE,LONGTITUDE,QRModel.getImage_path());
                        LocationSingleton.setSharedLatitude(Double.parseDouble(LATITUDE));
                        LocationSingleton.setSharedLongitude(Double.parseDouble(LONGTITUDE));
                        Intent in = new Intent(DashboardActivity.this, MarkedAttendance.class);
                        in.putExtra("QRModel",qrModel);
                        startActivity(in);
                    } catch (Exception e) {
                    }
                }else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT)
                            .show();
                }
            }


            if (requestCode == CAMERA_PIC_REQUEST2) {
                if (resultCode == RESULT_OK) {
                    try {

                        Toast.makeText(this, "Picture taken", Toast.LENGTH_SHORT)
                                .show();
                        String get_add = getSiteLocation();
                        //LocationSingleton.setSharedLatitude(Double.parseDouble(LATITUDE));
                        //LocationSingleton.setSharedLongitude(Double.parseDouble(LONGTITUDE));
                        Intent in = new Intent(DashboardActivity.this, SiteMarkedAttendance.class);
                        in.putExtra("OffSite",get_add);
                        startActivity(in);
                    } catch (Exception e) {
                    }
                }else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT)
                            .show();
                }
            }
    }catch (Exception e){
        e.printStackTrace();
        }
    }
    int getFrontCameraId() {
        releaseCamera();
        try {
            CameraInfo ci = new CameraInfo();

            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, ci);
//                if(ci.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
//                    ci.facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
//                }
//                else {
//                    ci.facing= Camera.CameraInfo.CAMERA_FACING_BACK;
//                    //return i;
//                }
                if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
                    return i;
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(DashboardActivity.this, "No Front Facing Camera", Toast.LENGTH_SHORT).show();
        }
        return -1; // No front-facing camera found
    }

    private int findFrontFacingCamera() {
        //releaseCamera();
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            //mCamera.lock();
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }
    public void test(){
        LatLng startLatLng = new LatLng(22.7267501, 75.8810795);
        LatLng endLatLng = new LatLng(22.7238, 75.8846);
        double distance = SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
        Log.e("Test Method",distance+"");
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }
}
