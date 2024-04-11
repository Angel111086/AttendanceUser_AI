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
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.model.QRModel;
import ai.attendance.multipart.AppHelper;
import ai.attendance.multipart.VolleyMultipartRequest;
import ai.attendance.utility.AppGlobalConstants;
import ai.attendance.utility.CommonUtilities;

public class MarkedAttendance extends AppCompatActivity {

    QRModel qrModel;
    String site_id,empdate,emplatitude,emplongitude,img_path;
    Bitmap bitmap;
    ImageView att_img;
    TextView txt_att;
    String empcode,empname;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_attendance);
        init();
    }

    public void init(){
        att_img = findViewById(R.id.att_img);
        txt_att = findViewById(R.id.txt_att);
        try {
            qrModel = (QRModel) getIntent().getSerializableExtra("QRModel");
            Log.e("Site", qrModel.getSite_id());
            site_id = qrModel.getSite_id();
            AttendanceAISharedPreference.saveSiteIdToPreferences(MarkedAttendance.this,site_id);
            empdate = qrModel.getEmpdate();
            emplatitude = qrModel.getEmplatitude();
            emplongitude = qrModel.getEmplongitude();
            img_path = QRModel.getImage_path();
            bitmap = QRModel.getBmp();
            empcode = AttendanceAISharedPreference.loadUserEmpCodeFromPreferences(MarkedAttendance.this);
            empname = AttendanceAISharedPreference.loadUserNameFromPreferences(MarkedAttendance.this);
            Log.e("CN",empcode + " : "+ empname);
            //markAttendanceAPI(site_id,empdate,emplatitude,emplongitude,img_path);
            saveImageToDB(site_id,empcode,empname,empdate,emplatitude,emplongitude,img_path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveImageToDB(final String id,final String code, final String name,final String date,final String latitude,final String longitude,final String img){
        if (!CommonUtilities.isOnline(MarkedAttendance.this)) {
            Toast.makeText(MarkedAttendance.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(MarkedAttendance.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://35.244.12.111:1234/userapi/markAttendance";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW + "markAttendance";
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
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("IMAGE_PATH", new DataPart(img, AppHelper.getBitmap(MarkedAttendance.this,bitmap),"image/jpeg"));


                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        multipartRequest.setShouldCache(false);
        VolleySingletonImage.getInstance(MarkedAttendance.this).addToRequestQueue(multipartRequest);
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
            Toast.makeText(MarkedAttendance.this, "Getting Null From Server.", Toast.LENGTH_LONG).show();
        }
    }



//    public void markAttendanceAPI(final String id,final String date,final String latitude,final String longitude,final String img){
//        if (!CommonUtilities.isOnline(MarkedAttendance.this)) {
//            Toast.makeText(MarkedAttendance.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final CustomProgressDialogue pd= new CustomProgressDialogue(MarkedAttendance.this);
//        pd.setCancelable(false);
//        pd.show();
//
//        HttpsTrustManager.allowAllSSL();
//        String  tag_string_req = "string_req";
//        //String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "Booking/DeAllocate";
//        String url = "http://192.168.1.24:1234/userapi/markAttendance";
//        //String url = "http://192.168.1.24:1234/userapi/testDemoNew";
//        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                pd.dismiss();
//                parseResponse(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                pd.dismiss();
//                NetworkResponse response = error.networkResponse;
//
//                Log.e("attendance_ai", "error response " + response);
//
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    //Log.e("Msg",error.getMessage());
//                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
//                } else if (error instanceof AuthFailureError) {                    //TODO
//                    Log.e("mls", "VolleyError AuthFailureError");
//                } else if (error instanceof ServerError) {
//                    Log.e("mls", "VolleyError ServerError");
//                } else if (error instanceof NetworkError) {
//                    Log.e("mls", "VolleyError NetworkError");
//                } else if (error instanceof ParseError) {
//                    Log.e("mls", "VolleyError TParseError");
//                }
//                if (error instanceof ServerError && response != null) {
//                    try {
//                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                        // Now you can use any deserializer to make sense of data
//                        Log.e("attendance_ai", "error response " + res);
//                        parseResponse(response.toString());
//
//                    } catch (Exception e1) {
//                        // Couldn't properly decode data to string
//                        e1.printStackTrace();
//                    }
//                }
//
//
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> map = new HashMap<>();
//                //map.put("Content-Type", "multipart/form-data");
//                map.put("Content-Type","application/x-www-form-urlencoded");
//                return map;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("SITE_ID",id);
//                map.put("EMPDATE",date);
//                map.put("EMPLATITUDE",latitude);
//                map.put("EMPLONGITUDE",longitude);
//                map.put("IMAGE_PATH",img);
//                return map;
//            }
//
//            @Override
//            public Priority getPriority() {
//                return Priority.IMMEDIATE;
//            }
//
//        };
//
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//        sr.setShouldCache(false);
//        //requestQueue.getCache().clear();
//        VolleySingleton.getInstance(MarkedAttendance.this).addToRequestQueue(sr);
//    }
//
//    public void parseResponse(String response) {
//        Log.e("Attendance Response", "Response " + response);
//        try {
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(MarkedAttendance.this, "Getting Null From Server.", Toast.LENGTH_LONG).show();
//        }
//    }
}

