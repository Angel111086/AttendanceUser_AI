package ai.attendance.firebasefiles;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;

public class MyFirebaseInstanseIdService extends FirebaseInstanceIdService {

    FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
    Context context;

    public static String generateToken()
    {
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        //sendRegistrationToServer(refreshToken);
        //Log.e("Token",refreshToken);
        return refreshToken;

    }

    @Override
    public void onTokenRefresh() {
        String reftoken= FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(reftoken);
        //1AttendanceAISharedPreference.saveTokenToPreferences(context,reftoken);
        Log.e("TOKEN",reftoken);
    }

    public void sendRegistrationToServer(String token){

    }
}
