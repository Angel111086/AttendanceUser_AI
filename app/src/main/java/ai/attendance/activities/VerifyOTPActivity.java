package ai.attendance.activities;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.attendance.AppSharedPreference.AttendanceAISharedPreference;
import ai.attendance.R;
import ai.attendance.utility.AppConstant;
import ai.attendance.utility.AppGlobalConstants;

public class VerifyOTPActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    ImageView img_back;
    Button btn_submit;
    EditText pin_hidden_edittext,pin_first_edittext,pin_second_edittext,pin_third_edittext,pin_forth_edittext;
    int generateOtp;
    String mobile="",getHiddenData="",getOtp="";;
    SmsVerifyCatcher smsVerifyCatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        init();
        listener();
        setPINListeners();
        generateOtp = getIntent().getIntExtra("GETOTP",0);
        mobile = AttendanceAISharedPreference.loadMobileFromPreferences(VerifyOTPActivity.this);
        getOtp = String.valueOf(generateOtp);

        try{
            final String x = getOtp.substring(0,1);
            final String y = getOtp.substring(1,2);
            final String z = getOtp.substring(2,3);
            final String a = getOtp.substring(3,4);
            Log.e("SubString",x+":"+y+":"+z+":"+a);


        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                //String code = parseCode(message);//Parse verification code
                //etCode.setText(code);//set code in edit text
                //then you can send verification code to server
                pin_first_edittext.setText(x);
                pin_second_edittext.setText(y);
                pin_third_edittext.setText(z);
                pin_forth_edittext.setText(a);
                pin_hidden_edittext.setText(getOtp);
            }
        });
        }catch (Exception e){e.printStackTrace();}
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    private void init() {
        img_back=(ImageView)findViewById(R.id.img_back);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        pin_first_edittext = (EditText) findViewById(R.id.pin_first_edittext);
        pin_second_edittext = (EditText) findViewById(R.id.pin_second_edittext);
        pin_third_edittext = (EditText) findViewById(R.id.pin_third_edittext);
        pin_forth_edittext = (EditText) findViewById(R.id.pin_forth_edittext);
        pin_hidden_edittext = (EditText) findViewById(R.id.pin_hidden_edittext);

    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setPINListeners() {
        pin_hidden_edittext.addTextChangedListener(this);
        pin_first_edittext.setOnFocusChangeListener(this);
        pin_second_edittext.setOnFocusChangeListener(this);
        pin_third_edittext.setOnFocusChangeListener(this);
        pin_forth_edittext.setOnFocusChangeListener(this);

        pin_first_edittext.setOnKeyListener(this);
        pin_second_edittext.setOnKeyListener(this);
        pin_third_edittext.setOnKeyListener(this);
        pin_forth_edittext.setOnKeyListener(this);
        pin_hidden_edittext.setOnKeyListener(this);
    }

    private void listener() {



        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getHiddenData = pin_hidden_edittext.getText().toString();
                Log.e("Hidden::",getHiddenData);
                //To Check
                String fp = pin_first_edittext.getText().toString().trim();
                String sp = pin_second_edittext.getText().toString().trim();
                String tp = pin_third_edittext.getText().toString().trim();
                String fop = pin_forth_edittext.getText().toString().trim();
                if (fp.equalsIgnoreCase("") || sp.equalsIgnoreCase("") || tp.equalsIgnoreCase("") || fop.equalsIgnoreCase("")) {
                    //mPinFirstDigitEditText.setError("");
                    //mPinSecondDigitEditText.setError("");
                    //mPinThirdDigitEditText.setError("");
                    //mPinForthDigitEditText.setError("");
                    //return;
                    Toast.makeText(VerifyOTPActivity.this, "Pin Cannot be Blank.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("HD",getHiddenData);
                    if(getHiddenData.equals(getOtp)){
                        AttendanceAISharedPreference.saveOtpToPreferences(VerifyOTPActivity.this,getOtp);
                        Intent in = new Intent(VerifyOTPActivity.this, DashboardActivity.class);
                        startActivity(in);
                        finishAffinity();
                    }
                    else{
                        Toast.makeText(VerifyOTPActivity.this, "Pin Empty.", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("TestOTPinVerify",generateOtp+"");
                }



            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(pin_first_edittext);
        setDefaultPinBackground(pin_second_edittext);
        setDefaultPinBackground(pin_third_edittext);
        setDefaultPinBackground(pin_forth_edittext);

        if (s.length() == 0) {
            setFocusedPinBackground(pin_first_edittext);
            pin_first_edittext.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(pin_second_edittext);
            pin_first_edittext.setText(s.charAt(0) + "");
            pin_second_edittext.setText("");
            pin_third_edittext.setText("");
            pin_forth_edittext.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(pin_third_edittext);
            pin_second_edittext.setText(s.charAt(1) + "");
            pin_third_edittext.setText("");
            pin_forth_edittext.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(pin_forth_edittext);
            pin_third_edittext.setText(s.charAt(2) + "");
            pin_forth_edittext.setText("");
        }  else if (s.length() == 4) {
            setDefaultPinBackground(pin_forth_edittext);
            pin_forth_edittext.setText(s.charAt(3) + "");
            hideSoftKeyboard(pin_forth_edittext);
        }

    }

    private void setDefaultPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_default_holo_light));
    }

    private void setFocusedPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_focused_holo_light));
    }

    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;
            default:
                break;
        }

    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    private void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (pin_hidden_edittext.getText().length() == 4)
                            pin_forth_edittext.setText("");
                        else if (pin_hidden_edittext.getText().length() == 3)
                            pin_third_edittext.setText("");
                        else if (pin_hidden_edittext.getText().length() == 2)
                            pin_second_edittext.setText("");
                        else if (pin_hidden_edittext.getText().length() == 1)
                            pin_second_edittext.setText("");

                        if (pin_hidden_edittext.length() > 0)
                            pin_hidden_edittext.setText(pin_hidden_edittext.getText().subSequence(0, pin_hidden_edittext.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }
        return false;
    }



//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent in = new Intent(VerifyOTPActivity.this,LoginActivity.class);
//        startActivity(in);
//        finish();
//    }
}
