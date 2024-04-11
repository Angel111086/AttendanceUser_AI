package ai.attendance.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ai.attendance.activities.DashboardActivity;

public class AppOpenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
