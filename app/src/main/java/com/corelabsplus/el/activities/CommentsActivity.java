package com.corelabsplus.el.activities;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.corelabsplus.el.R;
import com.corelabsplus.el.adapters.CommentsAdapter;
import com.corelabsplus.el.utils.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsActivity extends AppCompatActivity {

    @BindView(R.id.comments_list) RecyclerView commentsList;
    @BindView(R.id.comment_input) EditText commentInput;
    @BindView(R.id.add_comment) FloatingActionButton addComment;
    private List<Comment> comments = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String EVENT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        context = this;
        EVENT = getIntent().getStringExtra("event");

        commentsList.setHasFixedSize(true);
        commentsList.setLayoutManager(new LinearLayoutManager(this));

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commentInput.getText().toString().trim().isEmpty() || commentInput.getText().toString().trim() != null){
                    addComment();
                    commentInput.setText(null);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child("events").child(EVENT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("comments")){
                    comments.clear();

                    for (DataSnapshot commentSnapshot : dataSnapshot.child("comments").getChildren()){
                        Comment comment = new Comment(
                                    commentSnapshot.child("userName").getValue().toString(),
                                    commentSnapshot.child("comment").getValue().toString()
                                );

                        comments.add(comment);
                    }
                }

                CommentsAdapter adapter = new CommentsAdapter(comments, context);
                commentsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addComment(){
        final String comment = commentInput.getText().toString().trim();

        String key = databaseReference.child("events").child(EVENT).child("comments").push().getKey();

        Map<String, String> commentMap = new HashMap<>();
            commentMap.put("userName", mAuth.getCurrentUser().getDisplayName());
            commentMap.put("comment", comment);

        databaseReference.child("events").child(EVENT).child("comments").child(key).setValue(commentMap);
    }
}
