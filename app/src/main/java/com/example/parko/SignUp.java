package com.example.parko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText signup_email_field;
    private EditText signup_password_field;
    private EditText signup_password_confirm_field;
    private Button signup_create_btn;
    private Button signup_login_btn;
    private ProgressBar signup_progessBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup_email_field = (EditText) findViewById(R.id.signup_email_field);
        signup_password_field = (EditText) findViewById(R.id.signup_pwd_field);
        signup_password_confirm_field = (EditText) findViewById(R.id.signup_confirm_pwd_field);
        signup_create_btn = (Button) findViewById(R.id.signup_create_btn);
        signup_login_btn = (Button) findViewById(R.id.signup_login_btn);
        signup_progessBar = (ProgressBar) findViewById(R.id.signup_progress);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signup_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              sendToLogin();
            }
        });

        signup_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = signup_email_field.getText().toString();
                String password = signup_password_field.getText().toString();
                String confirm_password = signup_password_confirm_field.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_password))
                {
                    if (password.equals(confirm_password))
                    {
                        signup_progessBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //sendToMain();//me ndrru me te qu ne login
                                                String user_id=mAuth.getCurrentUser().getUid();
                                                Map<String, Object> postMap = new HashMap<>();

                                                postMap.put("user_type", null);
                                                postMap.put("user_id", user_id);
                                                Toast.makeText(SignUp.this, "Registered Successfully. Please check your email for verification!", Toast.LENGTH_LONG).show();
                                               firebaseFirestore.collection("Users").document(user_id).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       final String TAG = "MapActivity";
                                                       if(task.isSuccessful()){
                                                           Log.d(TAG, "onComplete: Me sukses u be regjistrimi");
                                                       }
                                                       else
                                                       {
                                                           Log.d(TAG, "onComplete: Nuk u be me sukses regjistrimi");
                                                       }
                                                   }
                                               });
                                                signup_email_field.setText(null);
                                                signup_password_field.setText(null);
                                                signup_password_confirm_field.setText(null);

                                            }
                                            else {
                                                Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }



                                        }
                                    });

                                }
                                else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SignUp.this, "Error:"+errorMessage, Toast.LENGTH_LONG).show();

                                }

                                signup_progessBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }
                    else {
                        Toast.makeText(SignUp.this, "Passwords doesn't match. Please retype again!", Toast.LENGTH_LONG).show();

                    }

                }

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

 /*       FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified())
        {
            sendToMain();
        }*/
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(SignUp.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void sendToLogin() {
        Intent loginIntent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(loginIntent);
        //finish();//bug, t'len mu logirat,sduhet.
    }
}
