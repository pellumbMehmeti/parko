package com.parkoKS.parko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotPass_email_field;
    private Button forgotPass_sendEmail_btn;
    private ProgressBar forgotPass_progress;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPass_email_field = (EditText) findViewById(R.id.forgotPwd_email);
        forgotPass_sendEmail_btn = (Button) findViewById(R.id.forgotPwd_send_Email_Btn);
        forgotPass_progress = (ProgressBar) findViewById(R.id.forgotPwd_progressBar);


        mAuth = FirebaseAuth.getInstance();


        forgotPass_sendEmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_forgot=forgotPass_email_field.getText().toString();

                if(!TextUtils.isEmpty(email_forgot))
                {
                    mAuth.sendPasswordResetEmail(forgotPass_email_field.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Password sent to your email!", Toast.LENGTH_LONG).show();
                                Intent loginIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            }
                            else {
                                Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please write your email!", Toast.LENGTH_LONG).show();
                }


            }
        });}




    }

