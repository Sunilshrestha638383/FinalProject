package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private TextView banner;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private Button registerUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();

        banner=(TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser=(Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName=(EditText) findViewById(R.id.name);
        editTextAge=(EditText) findViewById(R.id.phone);
        editTextEmail=(EditText) findViewById(R.id.email);
        editTextPassword=(EditText) findViewById(R.id.password);

        progressBar =(ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,Login.class));
                break;
            case R.id.registerUser:
                RegisterUser();
                break;
        }

    }

    private void RegisterUser() {
        String email= editTextEmail.getText().toString().trim();
        String phone= editTextAge.getText().toString().trim();
        String name= editTextFullName.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();

        if(name.isEmpty()){
            editTextFullName.setError("FUll Name is required");
            editTextFullName.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            editTextAge.setError("phone number  is required");
            editTextAge.requestFocus();
            return;
        }
        if(phone.length() != 10){
            editTextAge.setError("invalid");
            editTextAge.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password  is required");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError("password length must be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(name, phone, email);

                            FirebaseUser cuser =mAuth.getCurrentUser();

                            String email = cuser.getEmail();
                            String uid = cuser.getUid();

                            HashMap<Object, String> hashMap = new HashMap<>();

                            hashMap.put("email",email);
                            hashMap.put("uid", uid);
                            hashMap.put("phone",phone);
                            hashMap.put("onlineStatus","online");
                            hashMap.put("name",name);
                            hashMap.put("image","");
                            hashMap.put("cover","");

                            FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registered Sucessfully",
                                                Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(Register.this,Login.class));

                                        //redirect to signIn layout
                                    } else {
                                        Toast.makeText(Register.this, "Failed To register",
                                                Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else {
                                Toast.makeText(Register.this, "Failed To register",
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                    }
                });
    }
}