package com.learning.manuelliriano.project_v1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button registerBtn, loginBtn;
    private EditText emailTV, passwordTV;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializarViews();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });


    }


    private void loginUserAccount(){
        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email =  emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please enter email...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please enter your password...", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Login succesful!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            //Intent
                            Intent intent = new Intent(MainActivity.this, Galeria.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Login failed, please verify your user password or try again later", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }
    private void initializarViews(){
        registerBtn = findViewById(R.id.register);
        loginBtn =findViewById(R.id.login);

        emailTV=findViewById(R.id.email);
        passwordTV=findViewById(R.id.password);

        progressBar= findViewById(R.id.progressBar);
    }
}