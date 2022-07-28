package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView login_register,mForgotPassword;
    private Button signIn;
    private EditText login_email, login_password;


    private FirebaseAuth mAuth;
    private ProgressBar login_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mForgotPassword =(TextView) findViewById(R.id.forgotPassword);
        mForgotPassword.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);

        login_register = (TextView) findViewById(R.id.login_register);
        login_register.setOnClickListener(this);

        login_progressBar = (ProgressBar) findViewById(R.id.login_progressBar);

        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.signIn:
                UserSignIn();
                break;
            case R.id.forgotPassword:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Recover Password");
                LinearLayout linearLayout = new LinearLayout(this);

                EditText emailEt = new EditText(this);
                emailEt.setHint("email");
                emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailEt.setMinEms(16);

                linearLayout.addView(emailEt);
                linearLayout.setPadding(10,10,10,10);

                builder.setView(linearLayout);


              //button recover
                builder.setPositiveButton("recover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailEt.getText().toString().trim();
                        beingRecovery(email);

                    }
                });
        //button cancel
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
//show dialog
                builder.create().show();

        }

    }

    private void beingRecovery(String email) {
        login_progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this,"email Sent",Toast.LENGTH_LONG).show();
                    login_progressBar.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(Login.this,"failed ",Toast.LENGTH_LONG).show();
                    login_progressBar.setVisibility(View.GONE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }


    private void UserSignIn() {
        String email = login_email.getText().toString().trim();
        String password = login_password.getText().toString().trim();

        HashMap<Object, String> hashMap = new HashMap<>();




        if (email.isEmpty()) {
            login_email.setError("Email is required");
            login_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login_email.setError("Please provide valid email");
            login_email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            login_password.setError("Password  is required");
            login_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            login_password.setError("password length must be 6 characters");
            login_password.requestFocus();
            return;
        }
        login_progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //redirect to profile
                    hashMap.put("onlineStatus","online");
                        startActivity(new Intent(Login.this, Dashboard.class));

                } else {

                    Toast.makeText(Login.this, "Failed to login", Toast.LENGTH_LONG).show();
                    login_progressBar.setVisibility(View.GONE);
                }

            }
        });
    }
}