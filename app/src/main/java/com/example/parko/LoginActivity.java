package com.example.parko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREF ="Shared_pref";
    public static final String USER_TYPE= "user_type";
    private EditText loginEmailTxt;
    private EditText loginPwdTxt;
    private Button loginBtn;
    private Button registerBtn;
    private ProgressBar loginProgressbar;
    private Button forgotPasswordBtn;
private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseFirestore = FirebaseFirestore.getInstance();
        loginEmailTxt = (EditText)findViewById(R.id.login_email_field);
        loginPwdTxt = (EditText)findViewById(R.id.login_password_field);
        loginBtn = (Button)findViewById(R.id.login_btn);
        registerBtn = (Button) findViewById(R.id.login_register_btn);
        loginProgressbar = (ProgressBar) findViewById(R.id.login_progressBar);
        forgotPasswordBtn = (Button) findViewById(R.id.login_forgot_pwd);

        mAuth=FirebaseAuth.getInstance();

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPwdInent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPwdInent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(signupIntent);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String loginEmail=loginEmailTxt.getText().toString();
                String loginPwd=loginPwdTxt.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPwd)){
                    loginProgressbar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    sendToMain();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email to continue!", Toast.LENGTH_LONG).show();
                                    loginProgressbar.setVisibility(View.INVISIBLE);
                                }

                            }
                            else{
                                String errorMsg = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error:"+errorMsg, Toast.LENGTH_LONG).show();
                                loginProgressbar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
        {
           sendToMain();
    }
    }

    private void sendToMain() {




        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String TAG = "MapActivity";
                String userTYPE= documentSnapshot.getString("user_type");
                Log.d(TAG, "onSuccess: USER TYPE"+userTYPE);
/*
                SharedPreferences sp = getSharedPreferences(SHARED_PREF , Context.MODE_PRIVATE);
                sp.edit().putString(USER_TYPE,userTYPE).apply();*/
            }
        });
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
