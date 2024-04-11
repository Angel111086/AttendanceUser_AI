package ai.attendance.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.model.QRModel;
import ai.attendance.multipart.AppHelper;
import ai.attendance.multipart.VolleyMultipartRequest;
import ai.attendance.utility.AppGlobalConstants;
import ai.attendance.utility.CommonUtilities;

public class SiteMarkedAttendance extends AppCompatActivity {

    String site_id,empdate,emplatitude,emplongitude,img_path;
    Bitmap bitmap;
    ImageView att_img;
    TextView txt_att;
    String empcode,empname,off_Site;
    Date currentTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_marked_attendance);
        init();
    }

    public void init(){
        att_img = findViewById(R.id.att_img_site);
        txt_att = findViewById(R.id.txt_att_site);
        currentTime = Calendar.getInstance().getTime();
        off_Site = getIntent().getStringExtra("OffSite");
        try {
//            qrModel = (QRModel) getIntent().getSerializableExtra("QRModel");
//            Log.e("Site", qrModel.getSite_id());
              site_id = AttendanceAISharedPreference.loadSiteIdFromPreferences(SiteMarkedAttendance.this);
              empcode = AttendanceAISharedPreference.loadUserEmpCodeFromPreferences(SiteMarkedAttendance.this);
              empname = AttendanceAISharedPreference.loadUserNameFromPreferences(SiteMarkedAttendance.this);
              empdate = currentTime.toString();
              emplatitude = AttendanceAISharedPreference.loadUserLatitudeFromPreferences(SiteMarkedAttendance.this);
              emplongitude = AttendanceAISharedPreference.loadUserLongitudeFromPreferences(SiteMarkedAttendance.this);
              img_path = QRModel.getImage_path();
              bitmap = QRModel.getBmp();
            Log.e("CN",empcode + " : "+ empname);
            saveImageToDB(site_id,empcode,empname,empdate,emplatitude,emplongitude,off_Site,img_path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveImageToDB(final String id,final String code, final String name,final String date,final String latitude,final String longitude,final String off_add,final String img){
        if (!CommonUtilities.isOnline(SiteMarkedAttendance.this)) {
            Toast.makeText(SiteMarkedAttendance.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(SiteMarkedAttendance.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.1.24:1234/userapi/siteMarkAttendance";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW + "siteMarkAttendance";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    pd.dismiss();
                    String resultResponse = new String(response.data);
                    parseResponse(resultResponse);
                    Log.e("Response",resultResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    try {
                        String result = new String(networkResponse.data);
                        Log.e("Result",result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("SITE_ID",id);
                map.put("EMPCODE",code);
                map.put("EMPNAME",name);
                map.put("EMPDATE",date);
                map.put("EMPLATITUDE",latitude);
                map.put("EMPLONGITUDE",longitude);
                map.put("EMPOFFSITE",off_add);
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("IMAGE_PATH", new DataPart(img, AppHelper.getBitmap(SiteMarkedAttendance.this,bitmap),"image/jpeg"));


                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        multipartRequest.setShouldCache(false);
        VolleySingletonImage.getInstance(SiteMarkedAttendance.this).addToRequestQueue(multipartRequest);
    }

    public void parseResponse(String response){
        Log.e("Attendance Response", "Response " + response);
        try {
            JSONObject obj = new JSONObject(response);
            String str = obj.getString("data");
            if(str.equals("Attendance Marked Successfully!")){
                att_img.setImageResource(R.drawable.nice);
                txt_att.setText("Attendance Marked Successfully!");
            }else if(str.equals("Something Went Wrong!")){
                att_img.setImageResource(R.drawable.oops);
                txt_att.setText("Something Went Wrong!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SiteMarkedAttendance.this, "Getting Null From Server.", Toast.LENGTH_LONG).show();
        }
    }



}
