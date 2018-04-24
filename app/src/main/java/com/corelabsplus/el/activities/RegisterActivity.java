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

import com.corelabsplus.el.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.nameView) EditText nameView;
    @BindView(R.id.emailView) EditText emailView;
    @BindView(R.id.passwordView) EditText passwordView;
    @BindView(R.id.repasswordView) EditText repasswordView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.signup) Button registerButton;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar.setVisibility(View.INVISIBLE);
        context = this;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameView.getText().toString().trim() != null && emailView.getText().toString().trim() != null && passwordView.getText().toString().trim() != null && repasswordView.getText().toString().trim() != null && passwordView.getText().toString().trim().equals(repasswordView.getText().toString().trim())){
                    disableViews();
                    signUp();
                }
            }
        });
    }

    private void signUp(){
        final String name, password, email;
        name = nameView.getText().toString().trim();
        password = passwordView.getText().toString().trim();
        email = emailView.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    task.getResult().getUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Map<String, String> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);

                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(context, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

                else {
                    enableViews();
                }
            }
        });
    }

    private void disableViews(){
        progressBar.setVisibility(View.VISIBLE);
        emailView.setEnabled(false);
        passwordView.setEnabled(false);
        registerButton.setEnabled(false);
        nameView.setEnabled(false);
        repasswordView.setEnabled(false);
    }
    private void enableViews(){
        progressBar.setVisibility(View.INVISIBLE);
        emailView.setEnabled(true);
        passwordView.setEnabled(true);
        registerButton.setEnabled(true);
        nameView.setEnabled(true);
        repasswordView.setEnabled(true);
    }

}
