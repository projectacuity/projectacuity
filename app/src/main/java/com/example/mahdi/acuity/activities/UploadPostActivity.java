package com.example.mahdi.acuity.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.Post;
import com.example.mahdi.acuity.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ValueEventListener userListner;
    private FloatingActionButton mSubmitButton;
    private static final int gal_intent = 2;
    private static final int camera_intent = 1;
    private ProgressDialog mProgdial;
    private ImageView mView;
    private TextInputEditText mTxt;
    private android.support.v7.widget.CardView cardView;
    private String imgUrl;
    private String imgRef;

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
        mProgdial.setCancelable(false);
        mProgdial.setIndeterminate(true);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userListner!=null) {
            mDatabase.removeEventListener(userListner);
        }
    }

    private void submitPost() {
        final String title = mTxt.getText().toString();

        final String userId = mAuth.getCurrentUser().getUid();
        userListner = mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if (user == null) {
                    Toast.makeText(getApplicationContext(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    writeNewPost(userId, user.getUsername(), user.getPhotoUrl(), imgUrl, title);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Toast.makeText(getApplicationContext(),"Uploading post finished!",Toast.LENGTH_LONG).show();
        finish();

    }
    private void writeNewPost(final String userId, String username, String userPhoto, String imgUrl, String title) {

        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, userPhoto, imgUrl, imgRef, title);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == camera_intent || requestCode == gal_intent) && resultCode == RESULT_OK){

            Uri uri = data.getData();
            mProgdial.setMessage("Uploading photo...");
            mProgdial.show();

            StorageReference filepath = mStorage.child("photos").child(mAuth.getCurrentUser().getUid())
                    .child(UUID.randomUUID().toString());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgdial.dismiss();
                    imgRef = taskSnapshot.getStorage().toString();
                    imgUrl = taskSnapshot.getDownloadUrl().toString();
                    cardView.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext()).load(imgUrl).centerCrop().into(mView);
                    Toast.makeText(getApplicationContext(),"Uploading photo finished!",Toast.LENGTH_LONG).show();
                    mTxt.requestFocus();
                    mSubmitButton.setVisibility(View.VISIBLE);
                }
            });

        }
    }
    protected void takePhotoAction() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.add_a_photo_black);
        builderSingle.setCancelable(false);
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
                        if ((arrayAdapter.getItem(which)).equals("Camera")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,camera_intent);
                        }
                        else {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,gal_intent);
                        }
                    }
                });
        builderSingle.show();
    }
}