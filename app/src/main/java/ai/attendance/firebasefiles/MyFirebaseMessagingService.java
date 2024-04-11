package ai.attendance.firebasefiles;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.activities.DetailsActivity;
import ai.attendance.activities.QRCodeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
            Log.e("TAG", "From: " + remoteMessage.getFrom());

            if (remoteMessage == null)
                return;

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.e("TAG", "Notification Body: " + remoteMessage.getNotification().getBody());
                Log.e("TAG", "Notification Title: " + remoteMessage.getNotification().getTitle());
                AttendanceAISharedPreference.saveUserEmpCodeToPreferences(this,remoteMessage.getNotification().getTitle());
                sendNotification(remoteMessage.getNotification().getBody());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.e("TAG", "Data Payload: " + remoteMessage.getData().toString());

                //sendNotification(remoteMessage.getData().toString());
                Log.e("AI","inside on msg resive >0");

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    //handleDataMessage(json);
                } catch (Exception e) {
                    Log.e("TAG", "Exception: " + e.getMessage());
                }
            }
        }


    public void sendNotification(String messageBody){
        Intent intent=null;
        intent=new Intent(this, DetailsActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        Uri defaultsound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.app_launcher_icon)
                .setContentTitle("Attendance AI")
                .setContentText(messageBody)
                .setSound(defaultsound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Random random=new Random();
        int systemnumber = random.nextInt(999999 - 100000) + 100000;
        notificationManager.notify(systemnumber,builder.build());
        Log.e("FINISH","FINISH");
    }


}