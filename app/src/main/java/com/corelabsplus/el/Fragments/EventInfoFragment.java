package com.corelabsplus.el.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.corelabsplus.el.R;
import com.corelabsplus.el.activities.AddEventActivity;
import com.corelabsplus.el.activities.MyEventsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class EventInfoFragment extends Fragment {

    private String title, desc, time, location, latitude, longitude, user, EVENT, imageUrl;
    private boolean reserved;
    private Context context;
    private final int PICK_IMAGE = 5;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @BindView(R.id.event_image_view) ImageView eventImageView;
    @BindView(R.id.event_title_input) EditText eventTitleInput;
    @BindView(R.id.event_description_input) EditText eventDescInput;
    @BindView(R.id.event_location_input) EditText eventLocationInput;
    @BindView(R.id.event_time_input) EditText eventTimeInput;
    @BindView(R.id.event_location_latitude) EditText eventLocationLat;
    @BindView(R.id.event_location_longitude) EditText eventLocationLong;
    @BindView(R.id.update_event) Button updateEvent;

    public EventInfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_info, container, false);

        ButterKnife.bind(this, v);
        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("images");

        title = getActivity().getIntent().getStringExtra("eventTitle");
        desc = getActivity().getIntent().getStringExtra("eventDesc");
        time = getActivity().getIntent().getStringExtra("eventTime");
        location = getActivity().getIntent().getStringExtra("eventLocation");
        EVENT = getActivity().getIntent().getStringExtra("event");
        imageUrl = getActivity().getIntent().getStringExtra("eventImageUrl");
        user = getActivity().getIntent().getStringExtra("eventUser");
        latitude = getActivity().getIntent().getStringExtra("latitude");
        longitude = getActivity().getIntent().getStringExtra("longitude");
        reserved = getActivity().getIntent().getBooleanExtra("reserved", false);

        eventTitleInput.setText(title);
        eventDescInput.setText(desc);
        eventTimeInput.setText(time);
        eventLocationInput.setText(location);
        eventLocationLat.setText(latitude);
        eventLocationLong.setText(longitude);

        Picasso.with(context).load(imageUrl).into(eventImageView);

        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent.setEnabled(false);
                updateEvent();
            }
        });

        eventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        return v;
    }


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Event Image"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getApplicationContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                eventImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    public void updateEvent(){
        final String title;
        final String desc;
        final String time;
        final String location;
        final String latitude;
        final String longitude;
        final String KEY;
        final String imageUrl;
        final String posted;
        final String[] image = new String[1];

        title = eventTitleInput.getText().toString().trim();
        desc = eventDescInput.getText().toString().trim();
        time = eventTimeInput.getText().toString().trim();
        location = eventLocationInput.getText().toString().trim();
        latitude = eventLocationLat.getText().toString().trim();
        longitude = eventLocationLong.getText().toString().trim();

        if (title.length() == 0){
            eventTitleInput.setError("Title is required");
        }

        else if (desc.length() == 0){
            eventDescInput.setError("Description is required.");
        }

        else if (location.length() == 0){
            eventLocationInput.setError("Location is required");
        }

        else if (time.length() == 0){
            eventTimeInput.setError("Time is required");
        }

        else {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

            Date date = new Date();
            posted = dateFormat.format(date).toString();


            // Get the data from an ImageView as bytes
            eventImageView.setDrawingCacheEnabled(true);
            eventImageView.buildDrawingCache();
            Bitmap bitmap = eventImageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.child(EVENT).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    updateEvent.setEnabled(true);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    image[0] = downloadUrl.toString();

                    Map<String, String> event = new HashMap<>();
                    event.put("title", title);
                    event.put("description", desc);
                    event.put("time", time);
                    event.put("location", location);
                    event.put("posted", posted);
                    event.put("image", image[0]);
                    event.put("user", mAuth.getCurrentUser().getUid());
                    event.put("userName", mAuth.getCurrentUser().getDisplayName());

                    final Map<String, String> coordinates = new HashMap<>();
                    coordinates.put("latitude", latitude);
                    coordinates.put("longitude", longitude);

                    databaseReference.child("events").child(EVENT).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (latitude.length() > 0 && longitude.length() > 0) {
                                    databaseReference.child("events").child(EVENT).child("coordinates").setValue(coordinates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(getActivity().getApplicationContext(), MyEventsActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        }
                                    });
                                } else {
                                    Intent intent = new Intent(getActivity().getApplicationContext(), MyEventsActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        }
                    });
                }
            });
        }

    }



}
