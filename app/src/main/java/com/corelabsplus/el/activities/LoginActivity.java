package com.corelabsplus.el.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.corelabsplus.el.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.emailView) EditText emailView;
    @BindView(R.id.passwordView) EditText passwordView;
    @BindView(R.id.login) Button loginButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.register) Button registerButton;
    private Context context;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        context = this;
        progressBar.setVisibility(View.INVISIBLE);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailView.getText().toString().trim() != null && passwordView.getText().toString().trim() != null){
                    disableViews();
                    login();
                }
            }
        });
    }

    private void login(){
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    enableViews();
                    Toast.makeText(context, "Login failed, try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void disableViews(){
        progressBar.setVisibility(View.VISIBLE);
        emailView.setEnabled(false);
        passwordView.setEnabled(false);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
    }
    private void enableViews(){
        progressBar.setVisibility(View.INVISIBLE);
        emailView.setEnabled(true);
        passwordView.setEnabled(true);
        loginButton.setEnabled(true);
        registerButton.setEnabled(true);
    }
}
