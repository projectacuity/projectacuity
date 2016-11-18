package com.example.mahdi.acuity.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.Post;
import com.example.mahdi.acuity.models.User;
import com.firebase.client.Firebase;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FloatingActionButton mSubmitButton;
    private static final int gal_intent = 2;
    private static final int camera_intent = 1;
    private ProgressDialog mProgdial;
    private ImageView mView;
    private TextInputEditText mTxt;
    private android.support.v7.widget.CardView cardView;
    private String imgUrl;

    private boolean image_uploaded = false;

    private Button btnupload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgdial = new ProgressDialog(this);
        mView = (ImageView) findViewById(R.id.postPhoto);
        mTxt = (TextInputEditText) findViewById(R.id.post_title);
        cardView = (CardView) findViewById(R.id.card_view);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_new_post);
        mAuth = FirebaseAuth.getInstance();
        cardView.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.GONE);
        takePhotoAction();
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

    }
    private void submitPost() {
        final String title = mTxt.getText().toString();

        // Disable button so there are no multi-posts
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = mAuth.getCurrentUser().getUid();
        // [START single_value_read]
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if (user == null) {
                    // User is null, error out
                    Toast.makeText(getApplicationContext(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Write new post
                    writeNewPost(userId, user.getUsername(), user.getPhotoUrl(), imgUrl, title);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void writeNewPost(String userId, String username, String userPhoto, String imgUrl, String title) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, userPhoto, imgUrl, title);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == camera_intent || requestCode == gal_intent) && resultCode == RESULT_OK){
            cardView.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.VISIBLE);

            Uri uri = data.getData();
            mProgdial.setMessage("Uploading photo...");
            mProgdial.show();
            StorageReference filepath = mStorage.child("photos").child(mAuth.getCurrentUser().getUid())
                    .child(UUID.randomUUID().toString());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgdial.dismiss();

                    image_uploaded = true;
                    imgUrl = taskSnapshot.getDownloadUrl().toString();

                    Glide.with(getApplicationContext()).load(imgUrl).centerCrop().into(mView);

                    Toast.makeText(UploadPostActivity.this,"Uploading finished !",Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    protected void takePhotoAction() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.add_a_photo_blue);
        builderSingle.setTitle("Add photo with");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_dialog);
        arrayAdapter.add("Camera");
        arrayAdapter.add("Gallery");
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (arrayAdapter.getItem(which)=="Camera") {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,camera_intent);
                        }
                        else if (arrayAdapter.getItem(which)=="Gallery") {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,gal_intent);
                        }
                        else {
                            finish();
                        }
                    }
                });
        builderSingle.show();
    }
}