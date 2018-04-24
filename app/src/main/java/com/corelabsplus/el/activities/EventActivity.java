package com.corelabsplus.el.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.corelabsplus.el.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventActivity extends AppCompatActivity {

    @BindView(R.id.event_image) ImageView eventImage;
    @BindView(R.id.event_title) TextView eventTitle;
    @BindView(R.id.event_description) TextView eventDesc;
    @BindView(R.id.event_time) TextView eventTime;
    @BindView(R.id.event_location) TextView eventLocation;
    @BindView(R.id.comments) ImageButton comments;
    @BindView(R.id.map) ImageButton map;
    @BindView(R.id.reserve) ImageButton reserve;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private String title, desc, time, location, latitude, longitude, user, event, imageUrl, userID;
    private boolean reserved;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title = getIntent().getStringExtra("eventTitle");
        desc = getIntent().getStringExtra("eventDesc");
        time = getIntent().getStringExtra("eventTime");
        location = getIntent().getStringExtra("eventLocation");
        event = getIntent().getStringExtra("event");
        imageUrl = getIntent().getStringExtra("eventImageUrl");
        user = getIntent().getStringExtra("eventUser");
        userID = getIntent().getStringExtra("eventUserID");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        reserved = getIntent().getBooleanExtra("reserved", false);

        getSupportActionBar().setTitle(title);
        eventTitle.setText(title);
        eventDesc.setText(desc);
        eventTime.setText(time);
        eventLocation.setText(location);


        Picasso.with(context).load(imageUrl).into(eventImage);

        // MAPS CONTROL

        if (latitude == null || longitude == null) {
            map.setEnabled(false);
            map.setVisibility(View.INVISIBLE);
        } else {

            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("event", title);
                    intent.putExtra("location", location);

                    startActivity(intent);
                }
            });
        }

        // RESEVE CONTROL

        if (reserved || mAuth.getCurrentUser().getUid().equals(userID)){
            reserve.setEnabled(false);
            reserve.setVisibility(View.INVISIBLE);
        }

        else{
            Map<String, String> reservation = new HashMap<>();
                reservation.put("userName", mAuth.getCurrentUser().getDisplayName());
                reservation.put("email", mAuth.getCurrentUser().getEmail());
                reservation.put("reference", String.valueOf(System.currentTimeMillis()));

            if (databaseReference.child("events")
                    .child(event)
                    .child("reservations")
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(reservation).isSuccessful()) {
                Toast.makeText(context, "Your reservation is saved. ", Toast.LENGTH_SHORT).show();
                reserve.setEnabled(false);
                reserve.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(context, MyReservationsActivity.class);
                startActivity(intent);
            }
        }


        // COMMENTS CONTROL

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
