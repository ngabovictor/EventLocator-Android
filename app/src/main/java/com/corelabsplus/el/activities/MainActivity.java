package com.corelabsplus.el.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.corelabsplus.el.R;
import com.corelabsplus.el.adapters.EventsAdapter;
import com.corelabsplus.el.utils.DrawerUtil;
import com.corelabsplus.el.utils.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private List<Event> events = new ArrayList<>();

    @BindView(R.id.events_list) RecyclerView eventListView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.loading_results) LinearLayout loadingResults;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        context = this;

        eventListView.setHasFixedSize(true);
        eventListView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        else {
            DrawerUtil.getDrawer(this, toolbar, mAuth);
            loadEvents();
        }
    }

    private void loadEvents() {

        databaseReference.child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()){
                    if (eventSnapshot.hasChildren()){
                        Event event = new Event();

                        event.setKey(eventSnapshot.getKey());
                        event.setUser(eventSnapshot.child("userName").getValue().toString());
                        event.setTitle(eventSnapshot.child("title").getValue().toString());
                        event.setDescription(eventSnapshot.child("description").getValue().toString());
                        event.setTime(eventSnapshot.child("time").getValue().toString());
                        event.setLocation(eventSnapshot.child("location").getValue().toString());
                        event.setUserID(eventSnapshot.child("user").getValue().toString());

                        if (eventSnapshot.hasChild("coordinates")){
                            event.setLatitude(eventSnapshot.child("coordinates").child("latitude").getValue().toString());
                            event.setLongitude(eventSnapshot.child("coordinates").child("longitude").getValue().toString());
                        }

                        if (eventSnapshot.hasChild("reservations")){
                            if (eventSnapshot.child("reservations").hasChild(mAuth.getCurrentUser().getUid())){
                                event.setReserved(true);
                            }
                        }

                        if (eventSnapshot.hasChild("image")){
                            event.setImageUrl(eventSnapshot.child("image").getValue().toString());
                        }

                        events.add(event);
                    }
                }


                if (events.size() > 0){
                    Collections.reverse(events);
                    progressBar.setVisibility(View.INVISIBLE);
                    loadingResults.setVisibility(View.INVISIBLE);
                    EventsAdapter adapter = new EventsAdapter(events, context, mAuth, databaseReference);
                    eventListView.setAdapter(adapter);
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    loadingResults.setVisibility(View.VISIBLE);
                    EventsAdapter adapter = new EventsAdapter(events, context, mAuth, databaseReference);
                    eventListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
