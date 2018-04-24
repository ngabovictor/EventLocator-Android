package com.corelabsplus.el.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corelabsplus.el.R;
import com.corelabsplus.el.activities.EventActivity;
import com.corelabsplus.el.activities.MainActivity;
import com.corelabsplus.el.activities.MyEventActivity;
import com.corelabsplus.el.activities.MyEventsActivity;
import com.corelabsplus.el.utils.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public EventsAdapter(List<Event> events, Context context, FirebaseAuth mAuth, DatabaseReference databaseReference) {
        this.events = events;
        this.context = context;
        this.mAuth = mAuth;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.event, parent, false);
        return new EventsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsAdapter.ViewHolder holder, final int position) {
        final Event event = events.get(position);

        holder.eventUser.setText(event.getUser());
        holder.eventTitle.setText(event.getTitle());
        holder.eventDescription.setText(event.getDescription());
        holder.eventLocation.setText(event.getLocation());
        holder.eventTime.setText(event.getTime());

        Picasso.with(context).load(event.getImageUrl()).placeholder(R.drawable.ic_image).into(holder.eventImage);

        // CLICK CONTROL

        holder.event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Activity) context instanceof MainActivity) {

                    Intent intent = new Intent(context, EventActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event", event.getKey());
                    intent.putExtra("eventTitle", event.getTitle());
                    intent.putExtra("eventTime", event.getTime());
                    intent.putExtra("eventDesc", event.getDescription());
                    intent.putExtra("eventLocation", event.getLocation());
                    intent.putExtra("latitude", event.getLatitude());
                    intent.putExtra("longitude", event.getLongitude());
                    intent.putExtra("eventImageUrl", event.getImageUrl());
                    intent.putExtra("reserved", event.isReserved());
                    intent.putExtra("eventUser", event.getUser());
                    intent.putExtra("eventUserID", event.getUserID());

                    context.startActivity(intent);
                }

                else if ((Activity) context instanceof MyEventsActivity){
                    Intent intent = new Intent(context, MyEventActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event", event.getKey());
                    intent.putExtra("eventTitle", event.getTitle());
                    intent.putExtra("eventTime", event.getTime());
                    intent.putExtra("eventDesc", event.getDescription());
                    intent.putExtra("eventLocation", event.getLocation());
                    intent.putExtra("latitude", event.getLatitude());
                    intent.putExtra("longitude", event.getLongitude());
                    intent.putExtra("eventImageUrl", event.getImageUrl());
                    intent.putExtra("reserved", event.isReserved());
                    intent.putExtra("eventUser", event.getUser());

                    context.startActivity(intent);
                }
            }
        });


        // VIEWS CONTROL


        if (event.getUserID().equals(mAuth.getCurrentUser().getUid()) || event.isReserved()){
            holder.eventMenu.setVisibility(View.INVISIBLE);
        }
        else{
            holder.eventMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, holder.eventMenu);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.reserve_popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            int id = item.getItemId();

                            switch (id){
                                case R.id.reserve:
                                    Map<String, String> reservation = new HashMap<>();
                                        reservation.put("userName", mAuth.getCurrentUser().getDisplayName());
                                        reservation.put("email", mAuth.getCurrentUser().getEmail());
                                        reservation.put("reference", String.valueOf(System.currentTimeMillis()));

                                    if (databaseReference.child("events")
                                            .child(event.getKey())
                                            .child("reservations")
                                            .child(mAuth.getCurrentUser().getUid())
                                            .setValue(reservation).isSuccessful()) {
                                        Toast.makeText(context, "Your reservation is saved. ", Toast.LENGTH_SHORT).show();
                                    }
                            }

                            return true;
                        }
                    });

                    popup.show(); //showing popup menu
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout event;
        private TextView eventUser, eventTitle, eventDescription, eventTime, eventLocation;
        private ImageView eventImage;
        private ImageButton eventMenu;

        public ViewHolder(View itemView) {
            super(itemView);

            event = (RelativeLayout) itemView.findViewById(R.id.event_container);
            eventUser = (TextView) itemView.findViewById(R.id.event_user);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventDescription = (TextView) itemView.findViewById(R.id.event_content);
            eventTime = (TextView) itemView.findViewById(R.id.event_time);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location);
            eventImage = (ImageView) itemView.findViewById(R.id.event_image);
            eventMenu = (ImageButton) itemView.findViewById(R.id.event_menu);
        }
    }
}
