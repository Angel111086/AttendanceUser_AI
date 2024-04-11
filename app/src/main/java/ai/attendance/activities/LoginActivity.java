package ai.attendance.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.firebasefiles.MyFirebaseInstanseIdService;
import ai.attendance.model.SignUpModel;
import ai.attendance.multipart.AppHelper;
import ai.attendance.multipart.VolleyMultipartRequest;
import ai.attendance.utility.AppConstant;
import ai.attendance.utility.AppGlobalConstants;
import ai.attendance.utility.CommonUtilities;

public class LoginActivity extends AppCompatActivity {

    Button btn_next;
    EditText et_mobile;
    DatabaseReference database;
    ProgressDialog pd;
    String empMobile;
    public static int OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try{
            database = FirebaseDatabase.getInstance().getReference();
        }catch (Exception e){e.printStackTrace();}
        init();
        listener();
    }

    private void init() {

        btn_next=(Button)findViewById(R.id.btn_next);
        et_mobile=(EditText) findViewById(R.id.et_mobile);
        pd = new ProgressDialog(LoginActivity.this);
    }

    private void listener() {

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                empMobile = et_mobile.getText().toString();

                if(empMobile.isEmpty()){
                    et_mobile.setError("Please enter mobile number");
                    et_mobile.requestFocus();
                }else if(empMobile.length()!=10){
                    et_mobile.setError("Please enter valid mobile number");
                    et_mobile.requestFocus();
                }else {
                    getSignUpData(empMobile);

                }
            }
        });
    }

    //public void getSignUpData(){
//        try{
//            if(AppConstant.checkConnection(LoginActivity.this)){
//                pd.setCancelable(false);
//                pd.setTitle("Please Wait.");
//                pd.setMessage("Loading.");
//                pd.show();
//                Query loginQuery = database.child("Signup").orderByChild("Mobile").equalTo(empMobile);
//                loginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            pd.dismiss();
//                            for(DataSnapshot user : dataSnapshot.getChildren()){
//                                SignUpModel signUpModel = user.getValue(SignUpModel.class);
//                                Log.e("LoginModel",signUpModel.getMobile());
//                                OTP = generateRandomNumber();
//                                String t = MyFirebaseInstanseIdService.generateToken();
//                                Log.e("TokenLogin",t);
//
//                                Intent in = new Intent(LoginActivity.this, VerifyOTPActivity.class);
//                                in.putExtra("GETOTP",OTP);
//                                startActivity(in);
//                                Log.e("TestOTP",OTP+"");
//                                AttendanceAISharedPreference.saveMobileToPreferences(LoginActivity.this,empMobile);
//                                AttendanceAISharedPreference.saveCompanyNameToPreferences(LoginActivity.this,signUpModel.getCompanyName());
//                                AttendanceAISharedPreference.saveUserNameToPreferences(LoginActivity.this,signUpModel.getUsername());
//                                AttendanceAISharedPreference.saveUserCompanyLocationToPreferences(LoginActivity.this,signUpModel.getCompanyAddress());
//                                AttendanceAISharedPreference.saveUserEmpCodeToPreferences(LoginActivity.this,signUpModel.getEmpCode());
//                            }
//                        }else{
//                            pd.dismiss();
//                            Toast.makeText(LoginActivity.this,"Something Went Wrong..",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//        }catch (Exception e){e.printStackTrace();}
//
//    }

    public int generateRandomNumber()
    {
        Random rnd = new Random();
        //int otp = rnd.nextInt(10000);
        int randomPIN = (int)(Math.random()*9000)+1000;
        return randomPIN;
    }

    public void getSignUpData(final String mobile){
        if (!CommonUtilities.isOnline(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(LoginActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.29.124:1234/userapi/checkLogin";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW + "checkLogin";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    pd.dismiss();
                    parseResponse(response);
                    Log.e("Response",response);
                }catch (Exception e){e.printStackTrace();}
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Mobile", mobile);
                return map;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(sr);
    }

    public void parseResponse(String response){
        Log.e("Attendance Response", "Response " + response);
        try {
            JSONObject obj = new JSONObject(response);
            String str = obj.getString("Msg");
            if(str.equals("Active User") || str.equals("New User"))
            {
                JSONObject user_data = obj.getJSONObject("data");
                String _id = user_data.getString("_id");
                String BankName = user_data.getString("BankName");
                String BankAccountNumber = user_data.getString("BankAccountNumber");
                String BranchName = user_data.getString("BranchName");
                String IFSCCode = user_data.getString("IFSCCode");
                String BloodGroup = user_data.getString("BloodGroup");
                String CompanyName = user_data.getString("CompanyName");
                String CompanyAddress = user_data.getString("CompanyAddress");
                String CreationDate = user_data.getString("CreationDate");
                String Designation = user_data.getString("Designation");
                String DOB = user_data.getString("DOB");
                String Email = user_data.getString("Email");
                String EmpCode = user_data.getString("EmpCode");
                String Experience = user_data.getString("Experience");
                String FatherName = user_data.getString("FatherName");
                String Gender = user_data.getString("Gender");
                String JoiningDate = user_data.getString("JoiningDate");
                String LocalAddress = user_data.getString("LocalAddress");
                String Mobile = user_data.getString("Mobile");
                String ModificationDate = user_data.getString("ModificationDate");
                String MothersName = user_data.getString("MothersName");
                String Notice_Period = user_data.getString("Notice_Period");
                String Passport = user_data.getString("Passport");
                String Password = user_data.getString("Password");
                String PermanentAddress = user_data.getString("PermanentAddress");
                String Photo = user_data.getString("Photo");
                String Salary = user_data.getString("Salary");
                String Status = user_data.getString("Status");
                String Username = user_data.getString("Username");
                String GurdianNumber = user_data.getString("GurdianNumber");

                SignUpModel signUpModel = new SignUpModel(BankName,BankAccountNumber,BranchName,IFSCCode,BloodGroup,CompanyName,CompanyAddress,
                CreationDate,Designation,DOB,Email,EmpCode,Experience,FatherName,Gender,JoiningDate,LocalAddress,
                Mobile,ModificationDate,MothersName,Notice_Period,Passport,Password,PermanentAddress,Photo,Salary,
                Status,Username,GurdianNumber);
                AttendanceAISharedPreference.savePhotoToPreferences(LoginActivity.this,Photo);

                OTP = generateRandomNumber();
                verifyAPI(OTP+"");
                Log.e("TestOTP",OTP+"");
                //String getOtp = String.valueOf(OTP);


                AttendanceAISharedPreference.saveMobileToPreferences(LoginActivity.this,empMobile);
                AttendanceAISharedPreference.saveCompanyNameToPreferences(LoginActivity.this,signUpModel.getCompanyName());
                AttendanceAISharedPreference.saveUserNameToPreferences(LoginActivity.this,signUpModel.getUsername());
                AttendanceAISharedPreference.saveUserCompanyLocationToPreferences(LoginActivity.this,signUpModel.getCompanyAddress());
                AttendanceAISharedPreference.saveUserEmpCodeToPreferences(LoginActivity.this,signUpModel.getEmpCode());

                Toast.makeText(this, "You are: Active User", Toast.LENGTH_SHORT).show();


            }
            else if(str.equals("Suspended User!")){
                Toast.makeText(this, "You are: Suspended User", Toast.LENGTH_SHORT).show();
            }
            else if(str.equals("wrong number , no record found!")){
                Toast.makeText(this, "No Record Found. Please Check the Number", Toast.LENGTH_SHORT).show();
            }
            else if(str.equals("Invalid Status")){
                Toast.makeText(this, "No Record Found. Please Check the Number", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void verifyAPI(final String otp){

        if (!AppConstant.checkConnection(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setTitle("Please Wait");
        pd.setCancelable(false);
        pd.show();

        String url = "https://control.msg91.com/api/sendhttp.php?authkey=6110Aw4HLHYba593675f9&mobiles="+empMobile+"&message="+OTP+"&sender=CKTPOS&route=4&country=91&response=json";
        //String url="";
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResponse(response,otp);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                NetworkResponse response = error.networkResponse;

                Log.e("com.curryout", "error response " + response);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
                } else if (error instanceof AuthFailureError) {                    //TODO
                    Log.e("mls", "VolleyError AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.e("mls", "VolleyError ServerError");
                } else if (error instanceof NetworkError) {
                    Log.e("mls", "VolleyError NetworkError");
                } else if (error instanceof ParseError) {
                    Log.e("mls", "VolleyError TParseError");
                }
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.e("com.curryout", "error response " + res);

                        parseResponse(response.toString(),otp);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }

        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(sr);
    }
    public void parseResponse(String response, String otp){
        Log.e("Verify OTP Response","response "+response);
        try {
            JSONObject object = new JSONObject(response);
            String type = object.getString("type");
            if(type.equalsIgnoreCase("success")) {
                    Intent in = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                    in.putExtra("GETOTP",OTP);
                    startActivity(in);
//                    Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
//                    startActivity(in);
//                    finishAffinity();

//                } else {
//                    Toast.makeText(this, "Pin Mismatch!!", Toast.LENGTH_SHORT).show();
//                }
            }
            else {
                Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
