package com.corelabsplus.el.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.corelabsplus.el.R;
import com.corelabsplus.el.adapters.ReservationsAdapter;
import com.corelabsplus.el.utils.Reservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyReservationsActivity extends AppCompatActivity {

    @BindView(R.id.reservations_list) RecyclerView reservationsList;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.loading_results) RelativeLayout loadingResults;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private Context context;
    private List<Reservation> reservations = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        context = this;

        reservationsList.setHasFixedSize(true);
        reservationsList.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                reservations.clear();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()){
                    if (eventSnapshot.hasChild("reservations")){
                        if (eventSnapshot.child("reservations").hasChild(mAuth.getCurrentUser().getUid())){
                            String ref = eventSnapshot
                                    .child("reservations")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child("reference").getValue().toString();
                            String eventName = eventSnapshot.child("title").getValue().toString();
                            String eventLocation = eventSnapshot.child("location").getValue().toString();
                            String eventTime = eventSnapshot.child("time").getValue().toString();

                            String reservData= eventLocation + ", " + eventTime;
                            String reference = "Reference: " + ref;

                            Reservation reservation = new Reservation(eventName, reservData, reference);

                            reservations.add(reservation);
                        }
                    }
                }

                if (reservations.size() == 0){
                    loadingResults.setVisibility(View.VISIBLE);

                    ReservationsAdapter adapter = new ReservationsAdapter(reservations, context);
                    reservationsList.setAdapter(adapter);
                }

                else {
                    loadingResults.setVisibility(View.INVISIBLE);

                    Collections.reverse(reservations);

                    ReservationsAdapter adapter = new ReservationsAdapter(reservations, context);
                    reservationsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
