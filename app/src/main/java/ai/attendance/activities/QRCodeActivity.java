package ai.attendance.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.model.SignUpModel;
import ai.attendance.utility.AppConstant;
import ai.attendance.utility.AppGlobalConstants;
import ai.attendance.utility.CommonUtilities;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QRCodeActivity extends AppCompatActivity {

    ImageView img_back,qrscan_img;
    Button btn_ok;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    DatabaseReference database;
    ProgressDialog pd;
    String empMobile,empLocation,empCode;
    Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        try{
            currentTime = Calendar.getInstance().getTime();
            Log.e("CD",currentTime+"");
            empMobile = AttendanceAISharedPreference.loadMobileFromPreferences(QRCodeActivity.this);
            getEmpData();
        }catch (Exception e){e.printStackTrace();}
        init();
        listener();
        getEmpData();
    }


    private void init() {

        img_back=(ImageView)findViewById(R.id.img_back);
        //btn_ok=(Button)findViewById(R.id.btn_ok);
        qrscan_img = (ImageView) findViewById(R.id.qrscan_img);
    }

    private void listener() {

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(QRCodeActivity.this, DetailsActivity.class));
//            }
//        });

    }

    public void getEmpData(){
        if (!CommonUtilities.isOnline(QRCodeActivity.this)) {
            Toast.makeText(QRCodeActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(QRCodeActivity.this);
        pd.setCancelable(false);
        pd.show();

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
                map.put("Mobile",empMobile);
                return map;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(QRCodeActivity.this).addToRequestQueue(sr);
    }

    public void parseResponse(String response) {
        Log.e("QR Response", "Response " + response);
        try {
            JSONObject obj = new JSONObject(response);
            String str = obj.getString("Msg");
            if (str.equals("Active User") || str.equals("New User")) {
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
                try {
                    JSONObject object = new JSONObject();
                    object.accumulate("EMPCODE",EmpCode);
                    object.accumulate("EMPDATE",currentTime);
                    object.accumulate("EMPLATITUDE",AttendanceAISharedPreference.loadUserLatitudeFromPreferences(QRCodeActivity.this));
                    object.accumulate("EMPLONGITUDE",AttendanceAISharedPreference.loadUserLongitudeFromPreferences(QRCodeActivity.this));
                    object.accumulate("EMPNAME",Username);
                    object.accumulate("EMPMOBILE",AttendanceAISharedPreference.loadMobileFromPreferences(QRCodeActivity.this));
                    bitmap = TextToImageEncode(object.toString());
                    qrscan_img.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (str.equals("Suspended User!")) {
                Toast.makeText(this, "You are: Suspended User", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
//    public void getData(){
//        try{
//            if(AppConstant.checkConnection(QRCodeActivity.this)){
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
//                                empCode = signUpModel.getEmpCode();
//                                Log.e("LoginModel",empCode);
//
//                            }
//                        }else{
//                            pd.dismiss();
//                            Toast.makeText(QRCodeActivity.this,"Something Went Wrong..",Toast.LENGTH_LONG).show();
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


    public Bitmap TextToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            Log.e("Value",value);
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


}
