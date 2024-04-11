package ai.attendance.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.firebasefiles.MyFirebaseInstanseIdService;
import ai.attendance.model.AttendanceQRModel;
import ai.attendance.model.SignUpModel;
import ai.attendance.utility.AppConstant;

public class DetailsActivity extends AppCompatActivity {

    Button btn_home;
    ImageView img_back;
    TextView company_name,company_address,user_dt;
    DatabaseReference database;
    ProgressDialog pd;
    String getEmpCode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();
        listener();
        getDataFromDB();
    }

    private void init() {
        btn_home=(Button)findViewById(R.id.btn_home);
        img_back=(ImageView)findViewById(R.id.img_back);
        company_name = findViewById(R.id.company_name);
        company_address = findViewById(R.id.company_address);
        user_dt = findViewById(R.id.user_dt);
        try {
            database = FirebaseDatabase.getInstance().getReference();
            pd = new ProgressDialog(DetailsActivity.this);
            getEmpCode = AttendanceAISharedPreference.loadUserEmpCodeFromPreferences(this);
            Log.e("EmpCode",getEmpCode);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void listener() {

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DetailsActivity.this, DashboardActivity.class));
                finishAffinity();
            }
        });

    }

    public void getDataFromDB(){
        try{
            if(AppConstant.checkConnection(DetailsActivity.this)){
                pd.setCancelable(false);
                pd.setTitle("Please Wait.");
                pd.setMessage("Loading.");
                pd.show();

                Query loginQuery = database.child("AttendanceQR").orderByChild("UserID").equalTo(getEmpCode);
                loginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            pd.dismiss();
                            for(DataSnapshot user : dataSnapshot.getChildren()) {
                                AttendanceQRModel attendanceQRModel = user.getValue(AttendanceQRModel.class);
                                Log.e("QRModel", attendanceQRModel.getCompanyName());
                                company_name.setText(attendanceQRModel.getCompanyName());
                                company_address.setText(attendanceQRModel.getCompanyAddress());
                                user_dt.setText(attendanceQRModel.getDateTime());
                            }
                        }else{
                            pd.dismiss();
                            Toast.makeText(DetailsActivity.this,"Something Went Wrong..",Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }catch (Exception e){e.printStackTrace();}
    }

}
