package edu.ewubd.EventManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import edu.ewubd.calculatorassignment.R;

public class SignupActivity extends AppCompatActivity {


    private Button login, exitBtn, goBtn;
    private TableRow nameRow,emailRow,phoneRow,userIdRow, passwordRow,rePasswordRow;
    private TextView tvTitle,accountInfo;
    CheckBox rmbrUserID, rmbrLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        login = findViewById(R.id.signup_toggleBtn);
        nameRow =  findViewById(R.id.signup_nameRow);
        phoneRow  = findViewById(R.id.signup_phoneRow);
        emailRow = findViewById(R.id.signup_emailRow);
        tvTitle = findViewById(R.id.tvTitle);
        accountInfo = findViewById(R.id.signup_accountInfo);
        rmbrUserID = findViewById(R.id.signup_remembeerUserID);
        rmbrLogin = findViewById(R.id.signup_rememberLogin);
        rePasswordRow = findViewById(R.id.signup_RepasswordRow);

        EditText name = findViewById(R.id.signup_name);
        EditText userid = findViewById(R.id.signup_userID);
        EditText email = findViewById(R.id.signup_email);
        EditText pass = findViewById(R.id.signup_password);
        EditText phone = findViewById(R.id.signup_phone);

        Button toggleBtn  = findViewById(R.id.signup_toggleBtn);

        exitBtn = findViewById(R.id.signup_exit);
        goBtn = findViewById(R.id.signup_go);


        SharedPreferences shrd = getSharedPreferences("userDetails",MODE_PRIVATE);
        if(shrd.getBoolean("isUserIDCheckedKey",false)){
            String uid = shrd.getString("UserIDKey","not Found");
            String upass = shrd.getString("passwordKey","no pass found");
            boolean isUserIDChecked = shrd.getBoolean("isUserIDCheckedKey",false);
            if(shrd.getBoolean("isLoginCheckedKey",false)){
                nameRow.setVisibility(View.GONE);
                emailRow.setVisibility(View.GONE);
                phoneRow.setVisibility(View.GONE);
                rePasswordRow.setVisibility(View.GONE);

                boolean isLoginChecked =  shrd.getBoolean("isLoginCheckedKey",false);

                userid.setText(uid.toString());
                pass.setText(upass);
                rmbrUserID.setChecked(isUserIDChecked);
                rmbrLogin.setChecked(isLoginChecked);

            }
            else{
                nameRow.setVisibility(View.GONE);
                emailRow.setVisibility(View.GONE);
                phoneRow.setVisibility(View.GONE);
                rePasswordRow.setVisibility(View.GONE);

                userid.setText(uid.toString());
                rmbrUserID.setChecked(isUserIDChecked);

            }
        }
//        String n = shrd.getString("nameKey","lol");
//        name.setText(n);





//        Toast.makeText(getApplicationContext(),"name Saved",Toast.LENGTH_LONG );




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For toggling signup and login page
                String toggleValue = login.getText().toString();
                boolean isLogin = toggleValue.equalsIgnoreCase("Login");
                if(isLogin){
                    nameRow.setVisibility(View.GONE);
                    emailRow.setVisibility(View.GONE);
                    phoneRow.setVisibility(View.GONE);
                    rePasswordRow.setVisibility(View.GONE);

                    tvTitle.setText("Log In");
                    accountInfo.setText("Don't have an account?");
                    toggleBtn.setText("SignUp");
                }
                else{ // isLogin false that means Login page. Only userid and pass should be shown
                    tvTitle.setText("Sign Up");
                    accountInfo.setText("Already have an account?");

                    nameRow.setVisibility(View.VISIBLE);
                    emailRow.setVisibility(View.VISIBLE);
                    phoneRow.setVisibility(View.VISIBLE);
                    rePasswordRow.setVisibility(View.VISIBLE);


                    toggleBtn.setText("LogIn");



                }

            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toggleValue = ((Button)view).getText().toString();


                if(toggleValue.equalsIgnoreCase("Login")){

                    String userName = name.getText().toString();
                    String userEmail = email.getText().toString();
                    String userPhone = phone.getText().toString();
                    String userID = userid.getText().toString();
                    String password = pass.getText().toString();
                    boolean isUserIDChecked =  rmbrUserID.isChecked();
                    boolean isLoginChecked =  rmbrLogin.isChecked();

                    SharedPreferences sp = getSharedPreferences("userDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    if(sp.getBoolean("isLoginCheckedKey",false)){
                        Intent i = new Intent(SignupActivity.this,MainActivity.class);
                        startActivity(i);
                    }


                    editor.putString("nameKey",userName);
                    editor.putString("emailKey",userEmail);
                    editor.putString("phoneKey",userPhone);
                    editor.putString("UserIDKey",userID);
                    editor.putString("passwordKey",password);
                    editor.putBoolean("isUserIDCheckedKey",isUserIDChecked);
                    editor.putBoolean("isLoginCheckedKey",isLoginChecked);
                    editor.apply();

                    Toast.makeText(getApplicationContext(),"Details Saved",Toast.LENGTH_LONG ).show();




//                    name.setText(shrd.getString("nameKey",""));
//                    email.setText(shrd.getString("emailKey",""));
//                    pass.setText(shrd.getString("passwordKey",""));
//                    phone.setText(shrd.getString("phoneKey",""));
//                    userid.setText(shrd.getString("userIDKey",""));





                }
                else{
                    String userID = userid.getText().toString();
                    String password = pass.getText().toString();
                    boolean isUserIDChecked =  rmbrUserID.isChecked();
                    boolean isLoginChecked =  rmbrLogin.isChecked();


                    SharedPreferences sp = getSharedPreferences("userDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    if(sp.getBoolean("isLoginCheckedKey",false)){
                        Intent i = new Intent(SignupActivity.this,MainActivity.class);
                        startActivity(i);
                    }

                    editor.putString("UserIDKey",userID);
                    editor.putString("passwordKey",password);
                    editor.putBoolean("isUserIDCheckedKey",isUserIDChecked);
                    editor.putBoolean("isLoginCheckedKey",isLoginChecked);
                    editor.apply();

//                    Toast.makeText(getApplicationContext(),"Details Saved",Toast.LENGTH_LONG ).show();

//                    name.setText(shrd.getString("nameKey",""));
//                    pass.setText(shrd.getString("passwordKey",""));
//                    rmbrUserID.st
//                    sp.getBoolean("isLoginCheckedKey",false);

                }
            }
        });

    }
}