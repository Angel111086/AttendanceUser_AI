package ai.attendance.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;

public class SplashActivity extends AppCompatActivity {

    public final int SPLASH_DISPLAY_LENGTH = 5000; //splash screen will be shown for 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                actionNext();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }



    private void actionNext(){

        String mob = AttendanceAISharedPreference.loadMobileFromPreferences(SplashActivity.this);
        String otp = AttendanceAISharedPreference.loadOtpFromPreferences(SplashActivity.this);
        Log.e("Shared Pref",mob);

        if(!mob.equalsIgnoreCase("NA") && (!otp.equalsIgnoreCase("NA"))){
            Intent intent = new Intent(SplashActivity.this,DashboardActivity.class);
            startActivity(intent);
            finishAffinity();
        }else{
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

}
