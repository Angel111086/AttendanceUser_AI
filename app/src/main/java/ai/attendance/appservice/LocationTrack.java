package ai.attendance.appservice;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.activities.DashboardActivity;
import ai.attendance.broadcast.AppOpenReceiver;
import ai.attendance.broadcast.ProximityIntentReceiver;
import ai.attendance.model.LocationSingleton;
import ai.attendance.utility.AppGlobalConstants;

public class LocationTrack extends Service implements LocationListener {

    private final Context mContext;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    Location loc;
    double latitude, revisedLatitude;
    double longitude, revisedLongitude;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    //private static final long MIN_TIME_BW_UPDATES = 1000*10;
    protected LocationManager locationManager;

    //Proximity Alert
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 10; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire
    private static final String PROX_ALERT_INTENT = "com.androidmyway.demo.ProximityAlert";
    private static final String APP_OPEN_INTENT = "ai.attendance.broadcast.AppOpenReceiver";

    NotificationManager notificationManager;
    int notifyID = 1;
    boolean status = false;
    SimpleDateFormat sdf;
    String date;


    public LocationTrack(Context mContext) {
        this.mContext = mContext;
        getLocation();
        AppGlobalConstants.dat = "1/01/2020";
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        date = sdf.format(new Date());
        Log.e("Date",date);

    }

    private Location getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            20000,
                            1, this);
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }


                }
                if (checkNetwork) {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            20000,
                            1, this);

                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loc;
    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


    public void stopListener() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(LocationTrack.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("onLocationChanged", location.getLatitude() + " : " + location.getLongitude());
        revisedLatitude = location.getLatitude();
        revisedLongitude = location.getLongitude();
        //Toast.makeText(mContext, "on Changed Location" + location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        Log.e("Condition",(status==false && AppGlobalConstants.dat.compareTo(date)<0)+"");
        if(location !=null && status==false && AppGlobalConstants.dat.compareTo(date)<0) {
            sendNotification();
        }else{
            Log.e("No","Notification");
        }

        Log.e("Revised Lat", revisedLatitude + "");
        Log.e("Revised Lon", revisedLongitude + "");
        Log.e("Lat", LocationSingleton.getSharedLatitude() + "");
        Log.e("Lon", LocationSingleton.getSharedLongitude() + "");
        try {
//            IntentFilter filter = new IntentFilter(APP_OPEN_INTENT);
//            AppOpenReceiver receiver = new AppOpenReceiver();
//            registerReceiver(receiver, filter);

//            Intent intent = new Intent();
//            intent.setAction(APP_OPEN_INTENT);
//            //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//            sendBroadcast(intent);


        }catch (Exception e){e.printStackTrace();}
        //addProximityAlert();

        //UI Lat:- 22.7254,Lon:- 75.8780
        //CureWell Hos:- Lat:-22.7265,Lon:- 75.8821
        //Gravity Tower:- Lat:- 22.7257, Lon:- 75.8814
        //Indraprastha:- Lat:- 22.7223, Lon:- 75.8820

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }
    @Override
    public void onProviderDisabled(String s) {

    }
    private void addProximityAlert() {
        double latitude = Double.parseDouble("22.7257");
        double longitude = Double.parseDouble("75.8814");
        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no                           expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
        locationManager.addProximityAlert(22.7254,75.8780,10,10000,proximityIntent); //Universal
        locationManager.addProximityAlert(22.7265,75.8821,10,10000,proximityIntent); //Curewell
        locationManager.addProximityAlert(22.7261,75.8809,10,10000,proximityIntent); //Apollo Square
        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        mContext.registerReceiver(new ProximityIntentReceiver(), filter);
        //Toast.makeText(mContext,"Alert Added",Toast.LENGTH_SHORT).show();

    }





//
//    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
//        double earthRadius = 6371000; //meters
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        float dist = (float) (earthRadius * c);
//        return dist;
//    }
//
//
//    public static float getKmFromLatLong(float lat1, float lng1, float lat2, float lng2) {
//        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
//
//        loc1.setLatitude(lat1);
//        loc1.setLongitude(lng1);
//
//        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
//        loc2.setLatitude(lat2);
//        loc2.setLongitude(lng2);
//
//        float distanceInMeters = loc1.distanceTo(loc2); //in Meter
//        return distanceInMeters; //in Km
//        // Log.e("Dist in Me",distanceInMeters+"");
//    }
//
//    public double GetDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2)
//    {
//        final int R = 6371;
//        // Radius of the earth in km
//        double dLat = deg2rad(lat2 - lat1);
//        // deg2rad below
//        double dLon = deg2rad(lon2 - lon1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double d = R * c;
//        // Distance in km
//
//        //double m = d*1000; //Distance in meter
//        return d;
//
//
//    }
//    private double deg2rad(double deg)
//    {
//        return deg * (Math.PI / 180);
//    }
//
//    public void demo(){
//        float[] dist = new float[1];
//        float[] d = new float[3];
//        Location.distanceBetween(22.7257,75.8814,22.7223,75.8820,dist);
//
//        if(d[0]<=10){
//            //here your code or alert box for outside 1Km radius area
//            Log.e("If","Dist"+d.toString());
//        }
//    }

    public void sendNotification() {
        try {
            //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = "Channel";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            Notification.Builder builder = new Notification.Builder(mContext);
            builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
            Intent intent = new Intent(mContext, DashboardActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
            builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo));
            builder.setChannelId(CHANNEL_ID);
            builder.setContentTitle("Welcome");
            builder.setContentText("Welcome to Premises.");
            builder.setSubText("Tap to view.");
            // Will display the notification in the notification bar
            notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(notifyID, builder.build());
            status = true;
            AppGlobalConstants.dat = date;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

