package ai.attendance.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.firebasefiles.MyFirebaseInstanseIdService;
import ai.attendance.model.SignUpModel;
import ai.attendance.utility.AppConstant;
import ai.attendance.utility.AppGlobalConstants;
import ai.attendance.utility.CommonUtilities;

public class ProfileActivity extends AppCompatActivity {

    ImageView img_back,profile_image;
    EditText user_mobile,user_bloodGroup,user_bankDetails,user_companyName,user_companyAddress,
            user_creationDate,user_designation,user_DOB,user_email,user_employeeCode,
            user_experience,user_fatherName,user_motherName,user_gender,user_joiningDate,
            user_localAddress,user_modificationDate,user_notice,user_passport,user_password,
            user_permanentAddress,user_salary,user_status,user_userName,user_guardianNumber,
            user_bankName,user_bankAccountNumber,user_branchName,user_ifscCode;
    TextView txt_userName;
    String mobile="";
    DatabaseReference database;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        listener();
        try{
            database = FirebaseDatabase.getInstance().getReference();
            pd = new ProgressDialog(ProfileActivity.this);
        }catch (Exception e){e.printStackTrace();}
        mobile = AttendanceAISharedPreference.loadMobileFromPreferences(ProfileActivity.this);
        getEmpData(mobile);
    }
    public void init(){
        img_back = findViewById(R.id.img_back);
        profile_image = findViewById(R.id.profile_image);
        user_mobile = findViewById(R.id.user_mobile);
        user_bloodGroup = findViewById(R.id.user_bloodGroup);
        user_bankName = findViewById(R.id.user_bankName);
        user_bankAccountNumber = findViewById(R.id.user_bankAccountNumber);
        user_branchName = findViewById(R.id.user_branchName);
        user_ifscCode = findViewById(R.id.user_ifscCode);
        user_companyName = findViewById(R.id.user_companyName);
        user_companyAddress = findViewById(R.id.user_companyAddress);
        //user_creationDate = findViewById(R.id.user_creationDate);
        user_designation = findViewById(R.id.user_designation);
        user_DOB = findViewById(R.id.user_DOB);
        user_email = findViewById(R.id.user_email);
        user_employeeCode = findViewById(R.id.user_employeeCode);
        user_experience = findViewById(R.id.user_experience);
        user_fatherName = findViewById(R.id.user_fatherName);
        user_motherName = findViewById(R.id.user_motherName);
        user_gender = findViewById(R.id.user_gender);
        user_joiningDate = findViewById(R.id.user_joiningDate);
        user_localAddress = findViewById(R.id.user_localAddress);
        //user_modificationDate = findViewById(R.id.user_modificationDate);
        user_notice = findViewById(R.id.user_notice);
        user_passport = findViewById(R.id.user_passport);
        user_password = findViewById(R.id.user_password);
        user_permanentAddress = findViewById(R.id.user_permanentAddress);
        user_salary = findViewById(R.id.user_salary);
        user_status = findViewById(R.id.user_status);
        user_userName = findViewById(R.id.user_userName);
        user_guardianNumber = findViewById(R.id.user_guardianNumber);
        txt_userName = findViewById(R.id.txt_userName);
    }

    public void listener(){
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void getEmpData(final String mob){
        if (!CommonUtilities.isOnline(ProfileActivity.this)) {
            Toast.makeText(ProfileActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(ProfileActivity.this);
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
                map.put("Mobile", mob);
                return map;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(ProfileActivity.this).addToRequestQueue(sr);
    }

    public void parseResponse(String response){
        Log.e("Attendance Response", "Response " + response);
        try {
            JSONObject obj = new JSONObject(response);
            String str = obj.getString("Msg");
            if(str.equals("Active User") || str.equals("New User")){
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

                String pic = AttendanceAISharedPreference.loadPhotoFromPreferences(ProfileActivity.this);
                Picasso.get().load("http://35.200.220.64:1234/uploads/"+pic).into(profile_image);

                user_mobile.setText(signUpModel.getMobile());
                user_bloodGroup.setText(signUpModel.getBloodGroup());
                user_bankName.setText(signUpModel.getBankName());
                user_bankAccountNumber.setText(signUpModel.getBankAccountNumber());
                user_branchName.setText(signUpModel.getBranchName());
                user_ifscCode.setText(signUpModel.getIFSCCode());
                user_companyName.setText(signUpModel.getCompanyName());
                user_companyAddress.setText(signUpModel.getCompanyAddress());
                //user_creationDate.setText(signUpModel.getCreationDate());
                user_designation.setText(signUpModel.getDesignation());
                user_DOB.setText(signUpModel.getDOB());
                user_email.setText(signUpModel.getEmail());
                user_employeeCode.setText(signUpModel.getEmpCode());
                user_experience.setText(signUpModel.getExperience());
                user_fatherName.setText(signUpModel.getFatherName());
                user_motherName.setText(signUpModel.getMothersName());
                user_gender.setText(signUpModel.getGender());
                user_joiningDate.setText(signUpModel.getJoiningDate());
                user_localAddress.setText(signUpModel.getLocalAddress());
                //user_modificationDate.setText(signUpModel.getModificationDate());
                user_notice.setText(signUpModel.getNotice_Period());
                user_passport.setText(signUpModel.getPassport());
                user_password.setText(signUpModel.getPassword());
                user_permanentAddress.setText(signUpModel.getPermanentAddress());
                user_salary.setText(signUpModel.getSalary());
                String status = signUpModel.getStatus();
                if(status.equals("0"))
                    user_status.setText("New User");
                else if(status.equals("1"))
                    user_status.setText("Active User");
                user_userName.setText(signUpModel.getUsername());
                user_guardianNumber.setText(signUpModel.getGurdianNumber());
                txt_userName.setText(signUpModel.getUsername());

            }else if(str.equals("Suspended User!")){
                Toast.makeText(this, "You are: Suspended User", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


//    public void getData(){
//        try{
//
//            if(AppConstant.checkConnection(ProfileActivity.this)){
//                pd.setCancelable(false);
//                pd.setTitle("Please Wait.");
//                pd.setMessage("Loading.");
//                pd.show();
//                Query loginQuery = database.child("Signup").orderByChild("Mobile").equalTo(mobile);
//                loginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            pd.dismiss();
//                            for(DataSnapshot user : dataSnapshot.getChildren()){
//                                SignUpModel signUpModel = user.getValue(SignUpModel.class);
//                                Log.e("LoginModel",signUpModel.getMobile());
//                                user_mobile.setText(signUpModel.getMobile());
//                                user_email.setText(signUpModel.getEmail());
//                                user_designation.setText(signUpModel.getDesignation());
//                            }
//                        }else{
//                            pd.dismiss();
//                            Toast.makeText(ProfileActivity.this,"Something Went Wrong..",Toast.LENGTH_LONG).show();
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
//    }
}
