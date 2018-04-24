package com.corelabsplus.el.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corelabsplus.el.R;
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

public class AddEventActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.event_image_view) ImageView eventImageView;
    @BindView(R.id.event_title_input) EditText eventTitleInput;
    @BindView(R.id.event_description_input) EditText eventDescInput;
    @BindView(R.id.event_location_input) EditText eventLocationInput;
    @BindView(R.id.event_time_input) EditText eventTimeInput;
    @BindView(R.id.event_location_latitude) EditText eventLocationLat;
    @BindView(R.id.event_location_longitude) EditText eventLocationLong;
    @BindView(R.id.add_event_button) FloatingActionButton addEventButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public static final int PICK_IMAGE = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    //private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Bitmap bitmap;
    private Context context;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        context = this;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar.setVisibility(View.INVISIBLE);

        eventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventTitleInput.getText().toString().trim() != null
                        && eventDescInput.getText().toString().trim() != null
                        && eventLocationInput.getText().toString().trim() != null
                        && eventTimeInput.getText().toString().trim() != null
                        ){

                    addEventButton.setEnabled(false);
                    addEventButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    createEvent();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
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

    public void createEvent(){
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date date = new Date();
        posted = dateFormat.format(date).toString();

        KEY = databaseReference.child("events").push().getKey();



        // Get the data from an ImageView as bytes
        eventImageView.setDrawingCacheEnabled(true);
        eventImageView.buildDrawingCache();
        Bitmap bitmap = eventImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.child(KEY).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddEventActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                addEventButton.setEnabled(true);
                addEventButton.setVisibility(View.VISIBLE);
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

                databaseReference.child("events").child(KEY).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (latitude.length() > 0 && longitude.length() > 0) {
                                databaseReference.child("events").child(KEY).child("coordinates").setValue(coordinates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(context, MyEventsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }

                            else {
                                Intent intent = new Intent(context, MyEventsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        });

    }
}
