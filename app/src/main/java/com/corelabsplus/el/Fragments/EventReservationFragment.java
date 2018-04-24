package com.corelabsplus.el.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventReservationFragment extends Fragment {

    @BindView(R.id.reservations_list) RecyclerView reservationsList;
    private String event;
    private List<Reservation> reservations = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;


    public EventReservationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_event_reservation, container, false);
        ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        event = getActivity().getIntent().getStringExtra("event");

        reservationsList.setHasFixedSize(true);
        reservationsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        loadReservations();

        return v;
    }

    public void loadReservations(){
        databaseReference.child("events").child(event).child("reservations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservations.clear();

                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()){
                    String name, email, ref;

                    name = reservationSnapshot.child("userName").getValue().toString();
                    email = reservationSnapshot.child("email").getValue().toString();
                    ref = reservationSnapshot.child("reference").getValue().toString();

                    Reservation reservation = new Reservation(name, email, ref);

                    reservations.add(reservation);
                }

                ReservationsAdapter adapter = new ReservationsAdapter(reservations, getContext());
                reservationsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
