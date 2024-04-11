package ai.attendance.broadcast;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import ai.attendance.R;
import ai.attendance.activities.DashboardActivity;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;
    NotificationManager notificationManager;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        this.context = context;
        Log.e("Bool Enter","Test: "+entering);
        if (entering) {
            Log.e(getClass().getSimpleName(), "entering");
            Toast.makeText(context, "Enter", Toast.LENGTH_SHORT).show();
            enteredRegion();
        }else {
            Log.e(getClass().getSimpleName(), "exiting");
            Toast.makeText(context, "Exit", Toast.LENGTH_SHORT).show();
            exitedRegion();
        }
    }
    public void enteredRegion(){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //Notification notification = createNotification();

        //notification.setLatestEventInfo(context, "Proximity Alert!", "You are near your point of interest.", pendingIntent);
// The id of the channel.
        String id = "my_channel_01";

        // The user-visible name of the channel.
        String  name = "Attendance AI";

        // The user-visible description of the channel.
        String description = "Notifying about User's location";

        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(id, name,importance);

        // Configure the notification channel.
        mChannel.setDescription(description);

        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        notificationManager.createNotificationChannel(mChannel);
        Notification.Builder build = new Notification.Builder(context);
        build.setContentTitle("Attendance AI!!!");
        build.setContentText("Entered the Region..");
        build.setContentIntent(pendingIntent);
        build.setSmallIcon(R.drawable.app_launcher_icon);
        build.setChannelId(id);
        Notification notification = build.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void exitedRegion(){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //Notification notification = createNotification();

        //notification.setLatestEventInfo(context, "Proximity Alert!", "You are near your point of interest.", pendingIntent);
// The id of the channel.
        String id = "my_channel_01";

        // The user-visible name of the channel.
        String  name = "Attendance AI";

        // The user-visible description of the channel.
        String description = "Notifying about User's location";

        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(id, name,importance);

        // Configure the notification channel.
        mChannel.setDescription(description);

        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        notificationManager.createNotificationChannel(mChannel);
        Notification.Builder build = new Notification.Builder(context);
        build.setContentTitle("Attendance AI!!!");
        build.setContentText("Exited the Region..");
        build.setContentIntent(pendingIntent);
        build.setSmallIcon(R.drawable.app_launcher_icon);
        build.setChannelId(id);
        Notification notification = build.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
