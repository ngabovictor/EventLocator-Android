package com.corelabsplus.el.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.corelabsplus.el.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.update_profile) Button updateProfile;
    @BindView(R.id.name_label) TextView nameLabel;
    @BindView(R.id.nameView) EditText nameView;
    @BindView(R.id.emailView) EditText emailView;
    @BindView(R.id.phoneView) EditText phoneView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneView.getText().toString().trim() != null && phoneView.getText().toString().trim().length() >=13){
                    updateProfile.setEnabled(false);
                    updateProfile();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameLabel.setText(mAuth.getCurrentUser().getDisplayName());
        nameView.setText(mAuth.getCurrentUser().getDisplayName());
        emailView.setText(mAuth.getCurrentUser().getEmail());

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("phone").getValue() != null){
                    phoneView.setText(dataSnapshot.child("phone").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile(){
        final String phone = phoneView.getText().toString().trim();
        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("phone").setValue(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateProfile.setEnabled(true);
                Toast.makeText(ProfileActivity.this, "Profile saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
